const db = require("./db/orm").db;
const express = require("express");
const bodyParser = require("body-parser");
const router = require("./routes/index");
const appPort = 3066;

const app = express();
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

db.sync({ force: false }).then(() => console.log("All models were synchronized."));

app.use("/", router);

app.listen(appPort, () => {
  console.log(`App running on port ${appPort}.`);
});
