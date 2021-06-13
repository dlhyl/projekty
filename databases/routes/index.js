const express = require("express");
const router = express.Router();

const healthController = require("../controllers/health");
const ovSubmissionsController = require("../controllers/ovsubmissions");
const ovCompanies = require("../controllers/ovcompanies");
const ORMcontroller = require("../controllers/ORMcontroller.js");

// First assignment
router.get("/v1/health", healthController);

// Second assignment
router.get("/v1/ov/submissions", ovSubmissionsController.get);
router.post("/v1/ov/submissions", ovSubmissionsController.post);
router.delete("/v1/ov/submissions/:id", ovSubmissionsController.del);

// Third assignment
router.get("/v1/companies", ovCompanies);

// Fifth assignment
router.get("/v2/ov/submissions", ORMcontroller.submissions.get);
router.get("/v2/ov/submissions/:id", ORMcontroller.submissions.getByID);
router.post("/v2/ov/submissions", ORMcontroller.submissions.post);
router.delete("/v2/ov/submissions/:id", ORMcontroller.submissions.del);
router.put("/v2/ov/submissions/:id", ORMcontroller.submissions.put);
router.get("/v2/companies", ORMcontroller.companies.get);

module.exports = router;
