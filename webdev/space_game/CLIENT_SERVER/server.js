// Lubomir Dlhy
import fs from "fs";
import path from "path";
import express from "express";
import session from "express-session";
import { WebSocketServer } from "ws";
import crypto from "crypto";
import { GAME_STATUS, Game, Profile } from "./game.js";
import db from "./db.js";

const __dirname = path.resolve();

const port_http = 8080;
const port_ws = 8082;

const app = express();
const ws_app = new WebSocketServer({
  port: port_ws,
});

const sessionParser = session({
  secret: "vavjs-zadanie-2",
  saveUninitialized: true,
  resave: false,
});
const validSites = ["index", "register", "login", "game", "join"];
const clients = new Map();

app.use(sessionParser);
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// Load all public resources
app.use("/", express.static(__dirname + "/public"));

app.use((req, res, next) => {
  const bads = ["/game/start", "/game/reset", "/game/join", "/game/getplocha"];
  if (bads.some((bad) => req.path.startsWith(bad))) return next();
  removeGameNSpec(req.sessionID);
  next();
});

// Send website html content encoded in json
app.get("/site/:name", (req, res) => {
  var sitename = req.params.name;
  if (validSites.indexOf(sitename) >= 0) {
    if (sitename === "index" && req.session.userid) {
      sitename = req.session.userid === "admin" ? "index-admin" : "index-logged";
    }

    res.header("Content-Type", "application/json");
    res.status(200).sendFile(__dirname + `/data/${sitename}.json`);
  } else {
    res.sendStatus(404);
  }
});

app.post("/register", (req, res) => {
  const email = req.body.email;
  const login = req.body.login;
  const pw1 = req.body.password1;
  const pw2 = req.body.password2;
  const name = req.body.name;

  const login_duplicate = new Set(db.data.users.map((user) => user.login)).has(login);
  const login_format = /^[a-zA-Z]+$/.test(login);

  const password_equality = pw1 === pw2;
  const pw_hash = crypto.createHash("md5").update(pw1, "utf8").digest("hex");

  const email_duplicate = new Set(db.data.users.map((user) => user.email)).has(email);
  // Email validation regex from HTML5 SPEC https://html.spec.whatwg.org/multipage/input.html#valid-e-mail-address
  const email_format =
    /^[a-zA-Z0-9.!#$%&'*+\/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$/.test(email);

  const name_format = /^[A-Z][a-z]+\s[A-Z][a-z]+$/.test(name);

  if (!login_duplicate && login_format && password_equality && !email_duplicate && email_format && name_format) {
    db.data.users.push(new Profile(null, email, login, pw_hash, name));
    db.write();
    res.sendStatus(200);
  } else {
    const problems = [];
    if (login_duplicate) problems.push("Login is not unique!");
    if (!login_format) problems.push("Login is in wrong format!");
    if (!password_equality) problems.push("Passwords do not match!");
    if (email_duplicate) problems.push("Email is not unique!");
    if (!email_format) problems.push("Email is in wrong format!");
    if (!name_format) problems.push("Name is in wrong format!");
    res.status(400).json(problems);
  }
});

app.post("/login", (req, res) => {
  const login = req.body.login;
  const pw = req.body.password;

  const login_format = /^[a-zA-Z]+$/.test(login);
  if (!login_format) {
    return res.status(400).json(["Wrong login format!"]);
  }

  const pw_hash = crypto.createHash("md5").update(pw, "utf8").digest("hex");
  const user = db.data.users.filter((user) => user.login === login && user.pw_hash === pw_hash)[0];

  if (nullCheck(user)) {
    res.status(400).json(["Wrong login or password!"]);
  } else {
    req.session.userid = login;
    user.sessionID = req.sessionID;
    db.write();
    const client_obj = clients.get(req.sessionID);
    client_obj.user.profile = user;
    res.sendStatus(200);
  }
});

app.get("/logout", (req, res) => {
  req.session.destroy();
  return res.redirect("/site/index");
});

app.get("/admin/list", (req, res) => {
  var games = getGames();
  var users = db.data.users;
  if (!req.session.userid || (req.session.userid && req.session.userid !== "admin")) return res.sendStatus(403);
  res.json([
    {
      element: "h2",
      content: `${games.length} Active Games, ${clients.size} Users Online, ${users.length} Total Users`,
    },
    {
      element: "ol",
      children: games.flatMap((game) => {
        return game.players.map((player) => {
          return {
            element: "li",
            content: `Name: ${player.profile.name} | Session: ${player.profile.sessionID} | PIN: ${game.pin}`,
          };
        });
      }),
    },
  ]);
});

app.get("/admin/csv", (req, res) => {
  var users = db.data.users;
  if (!req.session.userid || (req.session.userid && req.session.userid !== "admin")) return res.sendStatus(403);
  res.json([
    {
      element: "h3",
      content: "Export",
    },
    { element: "div", content: "Name, Email, Password Hash, Max score, Max level" },
    {
      element: "textarea",
      id: "export-text",
      rows: 20,
      cols: 100,
      innerHTML: users.map((user) => [user.name, user.email, user.pw_hash, user.max_score, user.max_level].join(",")).join("\r\n"),
    },
    {
      element: "h3",
      content: "Import",
    },
    {
      element: "textarea",
      id: "import-text",
      rows: 20,
      cols: 100,
    },
    {
      element: "br",
    },
    {
      element: "button",
      id: "import-btn",
      content: "Import!",
      onclick: "sendCSV();",
    },
  ]);
});

app.post("/admin/csv/import", (req, res) => {
  var users = db.data.users;
  if (!req.session.userid || (req.session.userid && req.session.userid !== "admin")) return res.sendStatus(403);
  const text = req.body;
});

app.get("/game/list", (req, res) => {
  var games = getGames();
  res.json([
    {
      element: "h2",
      content: `${games.length} Games`,
    },
    {
      element: "ol",
      children: games.map((game) => {
        return {
          element: "li",
          content: `Score: ${game.score}, Level: ${game.level}, Status: ${Object.keys(GAME_STATUS)[game.status]}`,
          children: [{ element: "button", content: "Spectate", onclick: `spectateGame("${game.id}")` }],
        };
      }),
    },
  ]);
});

app.get("/game/create", (req, res) => {
  var client_obj = clients.get(req.sessionID);
  const newGame = new Game(req.sessionID);
  newGame.addPlayer(client_obj.user);
  client_obj.game = newGame;

  var gameweb_json = JSON.parse(fs.readFileSync(__dirname + "/data/game.json"));
  var pinElement = {
    element: "h3",
    content: `PIN: ${newGame.pin}`,
  };
  gameweb_json.splice(1, 0, pinElement);
  res.status(200).json({ gameid: newGame.id, web: gameweb_json });
});

app.get("/game/start", (req, res) => {
  var game = getGameBySessionID(req.sessionID);
  if (!nullCheck(game) && game.status === GAME_STATUS.CREATED) {
    game.gameLoop();
    res.sendStatus(201);
  } else {
    if (!nullCheck(game) && game.status === GAME_STATUS.LOSS) game.reset();
    res.sendStatus(202);
  }
});

app.get("/game/reset", (req, res) => {
  var game = getGameBySessionID(req.sessionID);
  if (!nullCheck(game)) {
    game.reset();
    res.sendStatus(200);
  }
});

app.get("/game/getplocha/:id", (req, res) => {
  const id = req.params.id;
  const game = getGameByGameID(id);
  if (typeof game === "undefined" || game == null) return res.sendStatus(404);
  else {
    const user = getUserBySessionID(req.sessionID);
    if (game.spectators.includes(user) || game.players.includes(user)) {
      user.ws.send(JSON.stringify(game.getPlayerPlocha(user)));
    }
    res.sendStatus(200);
  }
});

app.get("/game/spectate/:id", (req, res) => {
  const id = req.params.id;
  const game = getGameByGameID(id);
  if (nullCheck(game)) return res.sendStatus(404);
  else {
    var client_obj = clients.get(req.sessionID);
    client_obj.spectate = game;
    game.addSpectator(client_obj.user);
    res.header("Content-Type", "application/json");
    res.status(200).sendFile(__dirname + `/data/spectate.json`);
  }
});

app.get("/game/join", (req, res) => {
  const PIN = req.query.pin;
  if (nullCheck(PIN) || !/^[0-9]{4}$/.test(PIN)) res.sendStatus(400);
  else {
    var game = getGameByPIN(PIN);
    if (nullCheck(game)) res.sendStatus(404);
    else {
      var client_obj = clients.get(req.sessionID);
      client_obj.game = game;
      game.addPlayer(client_obj.user);
      res.header("Content-Type", "application/json");
      return res.status(200).sendFile(__dirname + `/data/game.json`);
    }
  }
});

ws_app.on("connection", (ws, req) => {
  sessionParser(req, {}, () => {
    ws.id = req.sessionID;
    var user = db.data.users.filter((user) => user.sessionID === ws.id)[0];
    if (nullCheck(user)) {
      user = new Profile(ws.id);
      db.data.users.push(user);
      db.write();
    }
    clients.set(ws.id, { user: { ws: ws, profile: user }, game: null, spectate: null });
  });

  ws.on("message", (msg) => {
    const game = getGameBySessionID(ws.id);
    if (!nullCheck(game) && game.status === GAME_STATUS.RUNNING) game.checkKey(msg.toString());
  });

  ws.on("close", () => {
    var game = getGameBySessionID(ws.id);
    if (!nullCheck(game)) game.delete();
    clients.delete(ws.id);
  });
});

const getGameByPIN = (gamePIN) => {
  return getAttr(Array.from(clients.values()).filter((val) => !nullCheck(val.game) && val.game.pin === gamePIN)[0], "game");
};

const getGameByGameID = (gameID) => {
  return getAttr(Array.from(clients.values()).filter((val) => !nullCheck(val.game) && val.game.id === gameID)[0], "game");
};

const getGames = () => {
  return Array.from(
    new Set(
      Array.from(clients.values())
        .filter((val) => !nullCheck(val.game))
        .map((val) => val.game)
    )
  ).sort((a, b) => b.score - a.score);
};

const getGameBySessionID = (sessionID) => {
  return getAttr(clients.get(sessionID), "game");
};

const getWsBySessionID = (sessionID) => {
  return getAttr(getAttr(clients.get(sessionID), "user"), "ws");
};

const getUserBySessionID = (sessionID) => {
  return getAttr(clients.get(sessionID), "user");
};

const nullCheck = (variable) => {
  if (typeof variable !== "undefined" && variable != null) return false;
  return true;
};

const getAttr = (object, attribute) => {
  if (typeof object !== "undefined" && object != null) return object[attribute];
  return null;
};

const removeGameNSpec = (sessionID) => {
  var client_obj = clients.get(sessionID);
  if (nullCheck(client_obj)) return;
  if (!nullCheck(client_obj.game)) {
    client_obj.game.removePlayer(client_obj.user);
    client_obj.game = null;
  }
  if (!nullCheck(client_obj.spectate)) {
    client_obj.spectate.removeSpectator(client_obj.user);
    client_obj.spectate = null;
  }
};

app.listen(port_http, () => {
  console.log(`App listening on http://localhost:${port_http}`);
});
