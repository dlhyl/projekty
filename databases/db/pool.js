require("dotenv").config();
const { Pool } = require("pg");
const types = require("pg").types;
const namedParams = require("node-postgres-named");

// parse bigint from db
types.setTypeParser(20, function (val) {
  return parseInt(val, 10);
});

// parse date to yyyy-mm-dd format
types.setTypeParser(1082, (val) => {
  return val;
});

// parse datetime without timezone to correct format
types.setTypeParser(1114, (val) => {
  return new Date(val.endsWith("Z") ? val : val + "Z").toISOString();
});

const pool = new Pool({
  user: process.env.DB_USER,
  host: process.env.DB_HOST,
  database: process.env.DB_NAME,
  password: process.env.DB_PASS,
  port: 5432,
});
namedParams.patch(pool);

module.exports = { pool };
