const { pool } = require("../db/pool");

const getUptime = (req, res) => {
  let uptimeQuery = "SELECT date_trunc('second', current_timestamp - pg_postmaster_start_time()) as uptime;";
  pool
    .query(uptimeQuery)
    .then((dbres) => {
      let uptimeJSON = dbres.rows[0].uptime;
      let days = 0,
        hrs = 0,
        mins = 0,
        secs = 0;
      if (uptimeJSON.hasOwnProperty("days")) days = uptimeJSON.days;
      if (uptimeJSON.hasOwnProperty("hours")) hrs = uptimeJSON.hours;
      if (uptimeJSON.hasOwnProperty("minutes")) mins = uptimeJSON.minutes;
      if (uptimeJSON.hasOwnProperty("seconds")) secs = uptimeJSON.seconds;
      let result = { pgsql: { uptime: `${days ? days + " days" : ""} ${hrs}:${mins}:${secs}` } };
      res.status(200).json(result);
    })
    .catch((err) => console.error(err));
};

module.exports = getUptime;
