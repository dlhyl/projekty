const { Pool } = require("pg");

const pool = new Pool({
  connectionString: "postgres://postgres:postgres@pg:5432/vavjs",
});

module.exports = { pool };
