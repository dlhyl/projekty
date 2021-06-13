require("dotenv").config();
const Sequelize = require("sequelize");
const seq_models = require("./models/init-models");

const db = new Sequelize(process.env.DB_NAME, process.env.DB_USER, process.env.DB_PASS, {
  host: process.env.DB_HOST,
  dialect: "postgres",
  logging: false,
});

const models = seq_models(db);

module.exports = { db, models };
