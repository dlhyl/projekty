const { pool } = require("../db/pool");
const { getCompaniesSchema } = require("./validations");

const getCompanies = async (req, res) => {
  // Validation of query parameters
  const { value, error } = getCompaniesSchema.validate(req.query);
  if (error) {
    error.details.forEach((i) => {
      delete value[i.context.label];
    });
  }

  let SQLwhereClause = "";
  // Setting params
  if ("query" in value) {
    value.query = "%" + value.query + "%";
    SQLwhereClause = "WHERE (t1.name ILIKE $query OR t1.address_line ILIKE $query)";
  }
  if ("last_update_lte" in value) {
    value.last_update_lte = new Date(value.last_update_lte).toISOString().split("T")[0];
    SQLwhereClause += SQLwhereClause ? " AND " : "WHERE ";
    SQLwhereClause += "(t1.last_update <= $last_update_lte)";
  }
  if ("last_update_gte" in value) {
    value.last_update_gte = new Date(value.last_update_gte).toISOString().split("T")[0];
    SQLwhereClause += SQLwhereClause ? " AND " : "WHERE ";
    SQLwhereClause += "(t1.last_update >= $last_update_gte)";
  }

  value.page_offset = (value.page - 1) * value.per_page;

  const SQLSelectString = `
  SELECT t1.cin, t1.name, t1.br_section, t1.address_line, t1.last_update, 
  (SELECT count(*) FROM ov.or_podanie_issues WHERE company_id = t1.cin) as or_podanie_issues_count,
  (SELECT count(*) FROM ov.znizenie_imania_issues WHERE company_id = t1.cin) as znizenie_imania_issues_count,
  (SELECT count(*) FROM ov.likvidator_issues WHERE company_id = t1.cin) as likvidator_issues_count,
  (SELECT count(*) FROM ov.konkurz_vyrovnanie_issues WHERE company_id = t1.cin) as konkurz_vyrovnanie_issues_count,
  (SELECT count(*) FROM ov.konkurz_restrukturalizacia_actors WHERE company_id = t1.cin) as konkurz_restrukturalizacia_actors_count
  FROM ov.companies as t1 ${SQLwhereClause} ORDER BY ${value.order_by} ${value.order_type} NULLS LAST LIMIT $per_page OFFSET $page_offset`;

  const numOfResultsQuery = `SELECT count(*) FROM ov.companies as t1 ${SQLwhereClause}`;

  // get results
  selectQueryRes = await pool.query(SQLSelectString, value);
  totalCountRes = await pool.query(numOfResultsQuery, value);
  const totalRows = parseInt(totalCountRes.rows[0].count);
  const metadata = {
    page: value.page,
    per_page: value.per_page,
    pages: value.per_page ? Math.ceil(totalRows / value.per_page) : 0,
    total: totalRows,
  };

  // send results in json
  res.status(200).json({ items: selectQueryRes.rows, metadata: metadata });
};

module.exports = getCompanies;
