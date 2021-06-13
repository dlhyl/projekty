const Joi = require("joi");

const getSubmissionsSchema = Joi.object({
  page: Joi.alternatives().try(Joi.number().min(1), Joi.any().empty(Joi.any())).default(1),
  per_page: Joi.alternatives().try(Joi.number().min(1), Joi.any().empty(Joi.any())).default(10),
  query: Joi.alternatives().try(Joi.number(), Joi.string()),
  order_by: Joi.alternatives()
    .try(
      Joi.string().valid(
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
        "city"
      ),
      Joi.any().empty(Joi.any())
    )
    .default("id"),
  order_type: Joi.alternatives().try(Joi.string().lowercase().valid("asc", "desc"), Joi.any().empty(Joi.any())).default("desc"),
  registration_date_lte: Joi.date().iso(),
  registration_date_gte: Joi.date().iso(),
}).options({
  allowUnknown: true,
  stripUnknown: true,
  abortEarly: false,
  presence: "optional",
});

const postSubmissionsSchema = Joi.object({
  br_court_name: Joi.string().allow(""),
  kind_name: Joi.string().allow(""),
  cin: Joi.number().integer(),
  registration_date: Joi.date()
    .iso()
    .custom((v, h) => {
      if (new Date(v).getFullYear() !== new Date(Date.now()).getFullYear()) return h.message("invalid_range");
      return v;
    })
    .required(),
  corporate_body_name: Joi.string().allow(""),
  br_section: Joi.string().allow(""),
  br_insertion: Joi.string().allow(""),
  text: Joi.string().allow(""),
  street: Joi.string().allow(""),
  postal_code: Joi.string().allow(""),
  city: Joi.string().allow(""),
}).options({
  allowUnknown: true,
  stripUnknown: true,
  abortEarly: false,
  presence: "required",
  messages: {
    "any.required": "required",
    "number.base": "not_number",
    "string.base": "not_string",
    "date.base": "invalid_range",
    "date.format": "invalid_range",
  },
});

const putSubmissionsSchema = Joi.object({
  br_court_name: Joi.string().allow(""),
  kind_name: Joi.string().allow(""),
  cin: Joi.number().integer(),
  registration_date: Joi.date()
    .iso()
    .custom((v, h) => {
      if (new Date(v).getFullYear() !== new Date(Date.now()).getFullYear()) return h.message("invalid_range");
      return v;
    }),
  corporate_body_name: Joi.string().allow(""),
  br_section: Joi.string().allow(""),
  br_insertion: Joi.string().allow(""),
  text: Joi.string().allow(""),
  street: Joi.string().allow(""),
  postal_code: Joi.string().allow(""),
  city: Joi.string().allow(""),
}).options({
  allowUnknown: true,
  stripUnknown: true,
  abortEarly: false,
  presence: "optional",
  messages: {
    "number.base": "not_number",
    "string.base": "not_string",
    "date.base": "invalid_range",
    "date.format": "invalid_range",
  },
});

const idSubmissionSchema = Joi.object({ id: Joi.number().integer() }).options({
  allowUnknown: true,
  stripUnknown: true,
  abortEarly: false,
  presence: "required",
});

const getCompaniesSchema = Joi.object({
  page: Joi.alternatives().try(Joi.number().min(1), Joi.any().empty(Joi.any())).default(1),
  per_page: Joi.alternatives().try(Joi.number().min(1), Joi.any().empty(Joi.any())).default(10),
  query: Joi.alternatives().try(Joi.number(), Joi.string()),
  order_by: Joi.alternatives()
    .try(
      Joi.string().valid(
        "cin",
        "name",
        "br_section",
        "address_line",
        "last_update",
        "or_podanie_issues_count",
        "znizenie_imania_issues_count",
        "likvidator_issues_count",
        "konkurz_vyrovnanie_issues_count",
        "konkurz_restrukturalizacia_actors_count"
      ),
      Joi.any().empty(Joi.any())
    )
    .default("cin"),
  order_type: Joi.alternatives().try(Joi.string().lowercase().valid("asc", "desc"), Joi.any().empty(Joi.any())).default("desc"),
  last_update_lte: Joi.date().iso(),
  last_update_gte: Joi.date().iso(),
}).options({
  allowUnknown: true,
  stripUnknown: true,
  abortEarly: false,
  presence: "optional",
});

module.exports = {
  postSubmissionsSchema,
  getSubmissionsSchema,
  idSubmissionSchema,
  getCompaniesSchema,
  putSubmissionsSchema,
};
