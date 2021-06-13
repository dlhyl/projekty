const { getCompaniesSchema, getSubmissionsSchema, idSubmissionSchema, putSubmissionsSchema, postSubmissionsSchema } = require("./validations");
const { Op, fn, col, literal } = require("sequelize");
const { models } = require("../db/orm");

/********************** SUBMISSIONS ENDPOINTS **********************/
const submissions = {
  /*************** GET Endpoint ***************/
  get: async (req, res) => {
    // Validation of query parameters
    const { value, error } = getSubmissionsSchema.validate(req.query);
    if (error) {
      error.details.forEach((i) => {
        delete value[i.context.label];
      });
    }

    let filter = [];
    // Building ORM filter from parameters
    if ("query" in value) {
      filter.push({
        [Op.or]: [{ corporate_body_name: { [Op.iLike]: "%" + value.query + "%" } }, { city: { [Op.iLike]: "%" + value.query + "%" } }],
      });
      if (typeof value.query == "number") {
        filter[0][Op.or].push({ cin: { [Op.eq]: value.query } });
      }
    }
    if ("registration_date_lte" in value) {
      filter.push({ registration_date: { [Op.lte]: new Date(value.registration_date_lte).toISOString().split("T")[0] } });
    }
    if ("registration_date_gte" in value) {
      filter.push({ registration_date: { [Op.gte]: new Date(value.registration_date_gte).toISOString().split("T")[0] } });
    }
    filter = filter.length ? { [Op.and]: [filter] } : filter;
    value.offset = (value.page - 1) * value.per_page;

    const results = await models.or_podanie_issues.findAll({
      attributes: [
        "id",
        "br_court_name",
        "kind_name",
        "cin",
        "registration_date",
        "corporate_body_name",
        "br_section",
        "br_insertion",
        "text",
        "street",
        "postal_code",
        "city",
      ],
      where: filter,
      order: [[col(value.order_by), value.order_type + " NULLS LAST"]],
      limit: value.per_page,
      offset: value.offset,
    });

    const row_count = await models.or_podanie_issues.count({ where: filter });

    const metadata = {
      page: value.page,
      per_page: value.per_page,
      pages: value.per_page ? Math.ceil(row_count / value.per_page) : 0,
      total: row_count,
    };

    // send results in json
    res.status(200).json({ items: results, metadata });
  },

  /*************** GET BY ID Endpoint ***************/
  getByID: async (req, res) => {
    // Validation of query parameters
    const { value, error } = idSubmissionSchema.validate(req.params);
    if (error) {
      return res.status(404).json({
        error: {
          message: "Záznam neexistuje",
        },
      });
    }

    const results = await models.or_podanie_issues.findOne({
      attributes: [
        "id",
        "br_court_name",
        "kind_name",
        "cin",
        "registration_date",
        "corporate_body_name",
        "br_section",
        "br_insertion",
        "text",
        "street",
        "postal_code",
        "city",
      ],
      where: { id: { [Op.eq]: value.id } },
    });

    if (results == null) {
      return res.status(404).json({
        error: {
          message: "Záznam neexistuje",
        },
      });
    }

    // send results in json
    res.status(200).json(results);
  },

  /*************** PUT Endpoint ***************/
  put: async (req, res) => {
    const idValidation = idSubmissionSchema.validate(req.params);
    // No id passed or id not number
    if (idValidation.error) {
      return res.status(404).json({
        error: {
          message: "Záznam neexistuje",
        },
      });
    }

    // Check existence of entry with id
    const record = await models.or_podanie_issues.findByPk(idValidation.value.id);

    if (record == null) {
      return res.status(404).json({
        error: {
          message: "Záznam neexistuje",
        },
      });
    }

    // Validate all fields in request body
    const { value, error } = putSubmissionsSchema.validate(req.body);
    if (error) {
      const { details } = error;
      const message = details.map((i) => {
        return { field: i.context.label, reasons: [i.message] };
      });
      return res.status(422).json({ errors: message });
    }

    // if no fields are passed in the request body
    if (Object.keys(value).length == 0) return res.status(422).json();

    // Update fields in found record
    record.update(value);

    // Send result
    return res.status(201).json({
      response: {
        id: record.id,
        br_court_name: record.br_court_name,
        kind_name: record.kind_name,
        cin: record.cin,
        registration_date: record.registration_date,
        corporate_body_name: record.corporate_body_name,
        br_section: record.br_section,
        br_insertion: record.br_insertion,
        text: record.text,
        street: record.street,
        postal_code: record.postal_code,
        city: record.city,
      },
    });
  },

  /*************** POST Endpoint ***************/
  post: async (req, res) => {
    // Validation of body parameters
    const { value, error } = postSubmissionsSchema.validate(req.body);
    if (error) {
      const { details } = error;
      const message = details.map((i) => {
        return { field: i.context.label, reasons: [i.message] };
      });
      return res.status(422).json({ errors: message });
    }

    // Bulletin issues INSERT
    //get current number from current year
    const curr_num = await models.bulletin_issues.findOne({
      where: {
        year: {
          [Op.eq]: new Date().getFullYear(),
        },
      },
      order: [[col("number"), "DESC"]],
    });

    const bulletinRecord = await models.bulletin_issues.create({
      year: new Date().getFullYear(),
      number: curr_num == null ? 1 : curr_num.number + 1,
      published_at: fn("now"),
      created_at: fn("now"),
      updated_at: fn("now"),
    });

    // Raw issues INSERT
    const rawIssuesRecord = await models.raw_issues.create({
      bulletin_issue_id: bulletinRecord.id,
      file_name: "-",
      content: "-",
      created_at: fn("now"),
      updated_at: fn("now"),
    });

    // Create Address Line
    value.address_line = `${value.street}, ${value.postal_code} ${value.city}`;

    // Podanie Issues INSERT
    const podanieRecord = await models.or_podanie_issues.create({
      bulletin_issue_id: bulletinRecord.id,
      raw_issue_id: rawIssuesRecord.id,
      br_mark: "-",
      br_court_code: "-",
      br_court_name: value.br_court_name,
      kind_code: "-",
      kind_name: value.kind_name,
      cin: value.cin,
      registration_date: value.registration_date,
      corporate_body_name: value.corporate_body_name,
      br_section: value.br_section,
      br_insertion: value.br_insertion,
      text: value.text,
      created_at: fn("now"),
      updated_at: fn("now"),
      address_line: value.address_line,
      street: value.street,
      postal_code: value.postal_code,
      city: value.city,
    });

    // Send result
    return res.status(201).json({
      response: {
        id: podanieRecord.id,
        br_court_name: podanieRecord.br_court_name,
        kind_name: podanieRecord.kind_name,
        cin: podanieRecord.cin,
        registration_date: podanieRecord.registration_date,
        corporate_body_name: podanieRecord.corporate_body_name,
        br_section: podanieRecord.br_section,
        br_insertion: podanieRecord.br_insertion,
        text: podanieRecord.text,
        street: podanieRecord.street,
        postal_code: podanieRecord.postal_code,
        city: podanieRecord.city,
      },
    });
  },

  /*************** DELETE Endpoint ***************/
  del: async (req, res) => {
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
    const result = await models.or_podanie_issues.findOne({
      where: { id: { [Op.eq]: value.id } },
    });

    if (result == null) {
      return res.status(404).json({
        error: {
          message: "Záznam neexistuje",
        },
      });
    }

    await models.or_podanie_issues.destroy({
      where: {
        id: value.id,
      },
      force: true,
    });

    return res.status(204).json();
  },
};

/********************** COMPANIES ENDPOINT **********************/
const companies = {
  get: async (req, res) => {
    // Validation of query parameters
    const { value, error } = getCompaniesSchema.validate(req.query);
    if (error) {
      error.details.forEach((i) => {
        delete value[i.context.label];
      });
    }

    let filter = [];
    // Building ORM filter from parameters
    if ("query" in value) {
      filter.push({
        [Op.or]: [{ name: { [Op.iLike]: "%" + value.query + "%" } }, { address_line: { [Op.iLike]: "%" + value.query + "%" } }],
      });
    }
    if ("last_update_lte" in value) {
      filter.push({ last_update: { [Op.lte]: new Date(value.last_update_lte) } });
    }
    if ("last_update_gte" in value) {
      filter.push({ last_update: { [Op.gte]: new Date(value.last_update_gte) } });
    }
    filter = filter.length ? { [Op.and]: [filter] } : filter;
    value.offset = (value.page - 1) * value.per_page;

    // Result
    const results = await models.companies.findAll({
      attributes: {
        include: [
          [literal('(SELECT COUNT(*) FROM ov.or_podanie_issues as X WHERE X.company_id = "companies".cin)'), "or_podanie_issues_count"],
          [literal('(SELECT COUNT(*) FROM ov.znizenie_imania_issues as X WHERE X.company_id = "companies".cin)'), "znizenie_imania_issues_count"],
          [literal('(SELECT COUNT(*) FROM ov.likvidator_issues as X WHERE X.company_id = "companies".cin)'), "likvidator_issues_count"],
          [literal('(SELECT COUNT(*) FROM ov.konkurz_vyrovnanie_issues as X WHERE X.company_id = "companies".cin)'), "konkurz_vyrovnanie_issues_count"],
          [
            literal('(SELECT COUNT(*) FROM ov.konkurz_restrukturalizacia_actors as X WHERE X.company_id = "companies".cin)'),
            "konkurz_restrukturalizacia_actors_count",
          ],
        ],
        exclude: ["created_at", "updated_at"],
      },
      where: filter,
      order: [[col(value.order_by), value.order_type + " NULLS LAST"]],
      limit: value.per_page,
      offset: value.offset,
    });
    const row_count = await models.companies.count({ where: filter });

    const metadata = {
      page: value.page,
      per_page: value.per_page,
      pages: value.per_page ? Math.ceil(row_count / value.per_page) : 0,
      total: row_count,
    };

    // send results in json
    res.status(200).json({ items: results, metadata });
  },
};

module.exports = { submissions, companies };
