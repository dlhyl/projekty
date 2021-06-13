var DataTypes = require("sequelize").DataTypes;
var _bulletin_issues = require("./bulletin_issues");
var _companies = require("./companies");
var _konkurz_restrukturalizacia_actors = require("./konkurz_restrukturalizacia_actors");
var _konkurz_restrukturalizacia_issues = require("./konkurz_restrukturalizacia_issues");
var _konkurz_restrukturalizacia_proposings = require("./konkurz_restrukturalizacia_proposings");
var _konkurz_vyrovnanie_issues = require("./konkurz_vyrovnanie_issues");
var _likvidator_issues = require("./likvidator_issues");
var _or_podanie_issue_documents = require("./or_podanie_issue_documents");
var _or_podanie_issues = require("./or_podanie_issues");
var _raw_issues = require("./raw_issues");
var _znizenie_imania_ceos = require("./znizenie_imania_ceos");
var _znizenie_imania_issues = require("./znizenie_imania_issues");

function initModels(sequelize) {
  var bulletin_issues = _bulletin_issues(sequelize, DataTypes);
  var companies = _companies(sequelize, DataTypes);
  var konkurz_restrukturalizacia_actors = _konkurz_restrukturalizacia_actors(sequelize, DataTypes);
  var konkurz_restrukturalizacia_issues = _konkurz_restrukturalizacia_issues(sequelize, DataTypes);
  var konkurz_restrukturalizacia_proposings = _konkurz_restrukturalizacia_proposings(sequelize, DataTypes);
  var konkurz_vyrovnanie_issues = _konkurz_vyrovnanie_issues(sequelize, DataTypes);
  var likvidator_issues = _likvidator_issues(sequelize, DataTypes);
  var or_podanie_issue_documents = _or_podanie_issue_documents(sequelize, DataTypes);
  var or_podanie_issues = _or_podanie_issues(sequelize, DataTypes);
  var raw_issues = _raw_issues(sequelize, DataTypes);
  var znizenie_imania_ceos = _znizenie_imania_ceos(sequelize, DataTypes);
  var znizenie_imania_issues = _znizenie_imania_issues(sequelize, DataTypes);

  konkurz_restrukturalizacia_issues.belongsTo(bulletin_issues, { as: "bulletin_issue", foreignKey: "bulletin_issue_id" });
  bulletin_issues.hasMany(konkurz_restrukturalizacia_issues, { as: "konkurz_restrukturalizacia_issues", foreignKey: "bulletin_issue_id" });
  konkurz_vyrovnanie_issues.belongsTo(bulletin_issues, { as: "bulletin_issue", foreignKey: "bulletin_issue_id" });
  bulletin_issues.hasMany(konkurz_vyrovnanie_issues, { as: "konkurz_vyrovnanie_issues", foreignKey: "bulletin_issue_id" });
  likvidator_issues.belongsTo(bulletin_issues, { as: "bulletin_issue", foreignKey: "bulletin_issue_id" });
  bulletin_issues.hasMany(likvidator_issues, { as: "likvidator_issues", foreignKey: "bulletin_issue_id" });
  or_podanie_issues.belongsTo(bulletin_issues, { as: "bulletin_issue", foreignKey: "bulletin_issue_id" });
  bulletin_issues.hasMany(or_podanie_issues, { as: "or_podanie_issues", foreignKey: "bulletin_issue_id" });
  raw_issues.belongsTo(bulletin_issues, { as: "bulletin_issue", foreignKey: "bulletin_issue_id" });
  bulletin_issues.hasMany(raw_issues, { as: "raw_issues", foreignKey: "bulletin_issue_id" });
  znizenie_imania_issues.belongsTo(bulletin_issues, { as: "bulletin_issue", foreignKey: "bulletin_issue_id" });
  bulletin_issues.hasMany(znizenie_imania_issues, { as: "znizenie_imania_issues", foreignKey: "bulletin_issue_id" });
  konkurz_restrukturalizacia_actors.belongsTo(companies, { as: "company", foreignKey: "company_id" });
  companies.hasMany(konkurz_restrukturalizacia_actors, { as: "konkurz_restrukturalizacia_actors", foreignKey: "company_id" });
  konkurz_vyrovnanie_issues.belongsTo(companies, { as: "company", foreignKey: "company_id" });
  companies.hasMany(konkurz_vyrovnanie_issues, { as: "konkurz_vyrovnanie_issues", foreignKey: "company_id" });
  likvidator_issues.belongsTo(companies, { as: "company", foreignKey: "company_id" });
  companies.hasMany(likvidator_issues, { as: "likvidator_issues", foreignKey: "company_id" });
  or_podanie_issues.belongsTo(companies, { as: "company", foreignKey: "company_id" });
  companies.hasMany(or_podanie_issues, { as: "or_podanie_issues", foreignKey: "company_id" });
  znizenie_imania_issues.belongsTo(companies, { as: "company", foreignKey: "company_id" });
  companies.hasMany(znizenie_imania_issues, { as: "znizenie_imania_issues", foreignKey: "company_id" });
  konkurz_restrukturalizacia_issues.belongsTo(konkurz_restrukturalizacia_actors, { as: "debtor", foreignKey: "debtor_id" });
  konkurz_restrukturalizacia_actors.hasMany(konkurz_restrukturalizacia_issues, { as: "konkurz_restrukturalizacia_issues", foreignKey: "debtor_id" });
  konkurz_restrukturalizacia_proposings.belongsTo(konkurz_restrukturalizacia_actors, { as: "actor", foreignKey: "actor_id" });
  konkurz_restrukturalizacia_actors.hasMany(konkurz_restrukturalizacia_proposings, { as: "konkurz_restrukturalizacia_proposings", foreignKey: "actor_id" });
  konkurz_restrukturalizacia_proposings.belongsTo(konkurz_restrukturalizacia_issues, { as: "issue", foreignKey: "issue_id" });
  konkurz_restrukturalizacia_issues.hasMany(konkurz_restrukturalizacia_proposings, { as: "konkurz_restrukturalizacia_proposings", foreignKey: "issue_id" });
  or_podanie_issue_documents.belongsTo(or_podanie_issues, { as: "or_podanie_issue", foreignKey: "or_podanie_issue_id" });
  or_podanie_issues.hasMany(or_podanie_issue_documents, { as: "or_podanie_issue_documents", foreignKey: "or_podanie_issue_id" });
  konkurz_restrukturalizacia_issues.belongsTo(raw_issues, { as: "raw_issue", foreignKey: "raw_issue_id" });
  raw_issues.hasMany(konkurz_restrukturalizacia_issues, { as: "konkurz_restrukturalizacia_issues", foreignKey: "raw_issue_id" });
  konkurz_vyrovnanie_issues.belongsTo(raw_issues, { as: "raw_issue", foreignKey: "raw_issue_id" });
  raw_issues.hasMany(konkurz_vyrovnanie_issues, { as: "konkurz_vyrovnanie_issues", foreignKey: "raw_issue_id" });
  likvidator_issues.belongsTo(raw_issues, { as: "raw_issue", foreignKey: "raw_issue_id" });
  raw_issues.hasMany(likvidator_issues, { as: "likvidator_issues", foreignKey: "raw_issue_id" });
  or_podanie_issues.belongsTo(raw_issues, { as: "raw_issue", foreignKey: "raw_issue_id" });
  raw_issues.hasMany(or_podanie_issues, { as: "or_podanie_issues", foreignKey: "raw_issue_id" });
  znizenie_imania_issues.belongsTo(raw_issues, { as: "raw_issue", foreignKey: "raw_issue_id" });
  raw_issues.hasMany(znizenie_imania_issues, { as: "znizenie_imania_issues", foreignKey: "raw_issue_id" });
  znizenie_imania_ceos.belongsTo(znizenie_imania_issues, { as: "znizenie_imania_issue", foreignKey: "znizenie_imania_issue_id" });
  znizenie_imania_issues.hasMany(znizenie_imania_ceos, { as: "znizenie_imania_ceos", foreignKey: "znizenie_imania_issue_id" });

  return {
    bulletin_issues,
    companies,
    konkurz_restrukturalizacia_actors,
    konkurz_restrukturalizacia_issues,
    konkurz_restrukturalizacia_proposings,
    konkurz_vyrovnanie_issues,
    likvidator_issues,
    or_podanie_issue_documents,
    or_podanie_issues,
    raw_issues,
    znizenie_imania_ceos,
    znizenie_imania_issues,
  };
}
module.exports = initModels;
module.exports.initModels = initModels;
module.exports.default = initModels;
