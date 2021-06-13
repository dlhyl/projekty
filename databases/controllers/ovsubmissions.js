const { pool } = require("../db/pool");
const { postSubmissionsSchema, getSubmissionsSchema, idSubmissionSchema } = require("./validations");

// ************ GET METHOD ************
const get = async function (req, res) {
  // Validation of query parameters
  const { value, error } = getSubmissionsSchema.validate(req.query);
  if (error) {
    error.details.forEach((i) => {
      delete value[i.context.label];
    });
  }

  let SQLwhereClause = "";
  // Setting params
  if ("query" in value) {
    SQLwhereClause += "WHERE ((corporate_body_name ILIKE $query OR city ILIKE $query)";
    if (typeof value.query == "number") {
      value.cin = value.query;
      SQLwhereClause += " OR (cin = $cin)";
    }
    SQLwhereClause += ")";
    value.query = "%" + value.query + "%";
  }
  if ("registration_date_lte" in value) {
    value.registration_date_lte = new Date(value.registration_date_lte).toISOString().split("T")[0];
    SQLwhereClause += SQLwhereClause ? " AND " : "WHERE ";
    SQLwhereClause += "(registration_date <= $registration_date_lte)";
  }
  if ("registration_date_gte" in value) {
    value.registration_date_gte = new Date(value.registration_date_gte).toISOString().split("T")[0];
    SQLwhereClause += SQLwhereClause ? " AND " : "WHERE ";
    SQLwhereClause += "(registration_date >= $registration_date_gte)";
  }

  value.offset = (value.page - 1) * value.per_page;

  const selectQueryString = `
  SELECT id, br_court_name, kind_name, cin, registration_date, corporate_body_name, br_section, br_insertion, text, street, postal_code, city 
  FROM ov.or_podanie_issues ${SQLwhereClause} ORDER BY ${value.order_by} ${value.order_type} NULLS LAST LIMIT $per_page OFFSET $offset`;

  const numOfResultsQuery = `SELECT count(*) FROM ov.or_podanie_issues ${SQLwhereClause}`;

  // get results
  selectQueryRes = await pool.query(selectQueryString, value);
  totalCountRes = await pool.query(numOfResultsQuery, value);
  const totalRows = parseInt(totalCountRes.rows[0].count);
  const metadata = {
    page: value.page,
    per_page: value.per_page,
    pages: value.per_page ? Math.ceil(totalRows / value.per_page) : 0,
    total: totalRows,
  };

  // send results in json
  res.status(200).json({ items: selectQueryRes.rows, metadata });
};

// ************ POST METHOD ************
const post = async function (req, res) {
  // Validation of body parameters
  const { value, error } = postSubmissionsSchema.validate(req.body);
  if (error) {
    const { details } = error;
    const message = details.map((i) => {
      return { field: i.context.label, reasons: [i.message] };
    });
    return res.status(422).json({ errors: message });
  }

  // Bulletin issues SQL Query
  const bulletinQuery = `INSERT INTO ov.bulletin_issues(year, number, published_at, created_at, updated_at)
    VALUES(DATE_PART('year',CURRENT_DATE), (SELECT COALESCE((SELECT number+1 as nmb FROM ov.bulletin_issues WHERE year = date_part('year', CURRENT_DATE) ORDER BY number DESC LIMIT 1),1)), CURRENT_DATE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
    RETURNING *;`;
  const bulletin_res = await pool.query(bulletinQuery);
  value.bulletin_id = bulletin_res.rows[0].id;

  // Raw issues SQL Query
  const rawIssuesQuery = `INSERT INTO ov.raw_issues(bulletin_issue_id, file_name, content, created_at, updated_at)
    VALUES(${value.bulletin_id}, '-', '-', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP) RETURNING *;`;
  const rawIssues_res = await pool.query(rawIssuesQuery);
  value.raw_id = rawIssues_res.rows[0].id;

  // Create Address Line
  value.address_line = `${value.street}, ${value.postal_code} ${value.city}`;

  // Or Podanie Issues SQL Query
  const insertQuery = `INSERT INTO ov.or_podanie_issues(bulletin_issue_id, raw_issue_id, br_mark, br_court_code, br_court_name, kind_code, kind_name, cin, registration_date, corporate_body_name, br_section, br_insertion, text, created_at, updated_at, address_line, street, postal_code, city)
  VALUES ($bulletin_id, $raw_id, '-', '-', $br_court_name, '-', $kind_name, $cin, $registration_date, $corporate_body_name, $br_section, $br_insertion, $text, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, $address_line, $street, $postal_code, $city)
  RETURNING id, br_court_name, kind_name, cin, registration_date, corporate_body_name, br_section, br_insertion, text, street, postal_code, city`;
  const insertRes = await pool.query(insertQuery, value);

  // Send result
  return res.status(201).json({ response: insertRes.rows[0] });
};

// ************ DELETE METHOD ************
const del = async function (req, res) {
  // Validation of id
  const { value, error } = idSubmissionSchema.validate(req.params);
  if (error) {
    return res.status(404).json({
      error: {
        message: "Záznam neexistuje",
      },
    });
  }

  // Check existence of entry with id
  const { rows } = await pool.query("SELECT id from ov.or_podanie_issues WHERE id = $id::bigint;", { id: value.id });
  if (!rows.length) {
    return res.status(404).json({
      error: {
        message: "Záznam neexistuje",
      },
    });
  }

  await pool.query("DELETE from ov.or_podanie_issues WHERE id = $id;", { id: value.id });
  return res.status(204).json();
};

module.exports = { get, post, del };
