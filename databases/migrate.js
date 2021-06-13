const Postgrator = require("postgrator");
require("dotenv").config();

const postgrator = new Postgrator({
  migrationDirectory: __dirname + "/migrations",
  driver: "pg",
  port: 5432,
  host: process.env.DB_HOST,
  database: process.env.DB_NAME,
  username: process.env.DB_USER,
  password: process.env.DB_PASS,
});

const args = process.argv.slice(2);
var version = "";
switch (args[0]) {
  case "rollback":
    version = "0";
    break;
  case "migrate":
    break;
  default:
    console.log("Usage: node migrate.js (migrate|rollback)");
    return;
}

// Migrate to max version
postgrator.migrate(version).catch((error) => console.log(error));

postgrator.on("migration-started", (migration) => console.log("Migration started for migration " + migration.name + "\n", migration));
postgrator.on("migration-finished", (migration) => console.log("Migration finished for migration " + migration.name + "\n", migration));
