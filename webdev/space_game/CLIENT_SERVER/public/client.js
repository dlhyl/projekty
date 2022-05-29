// Lubomir Dlhy
const json2elements = (json) => {
  var elements = [];
  for (var item of json) {
    var element = document.createElement(item["element"]);
    Object.entries(item)
      .filter(([key, value]) => !["children", "element", "content", "innerHTML"].includes(key))
      .forEach(([key, value]) => {
        element.setAttribute(key, value);
      });
    if ("innerHTML" in item) element.innerHTML = item["innerHTML"];
    if ("content" in item) element.innerText = item["content"];
    elements.push(element);
    if (Object.keys(item).includes("children"))
      element.innerHTML += json2elements(item["children"])
        .map((el) => el.outerHTML)
        .join("");
  }
  return elements;
};

const sendKeyCode = (event) => {
  ws.send(event.keyCode.toString());
};

const getCoordsFromPosition = (pos) => {
  return [(pos % 11) * 48, Math.floor(pos / 11) * 48 + 48];
};

const drawImage = (canvasID, imageID, positionX, positionY) => {
  const ctx = document.getElementById(canvasID).getContext("2d");
  const img = document.getElementById(imageID);
  ctx.drawImage(img, positionX, positionY);
};

const drawStats = (canvasID, score, level, max_score, max_level) => {
  var canvas = document.getElementById(canvasID);
  var ctx = canvas.getContext("2d");
  ctx.fillStyle = "red";
  ctx.textAlign = "left";
  ctx.font = "bold 15px Courier";
  var [posX, posY] = [10, 15];
  if (typeof max_level != "undefined") {
    ctx.fillText(`Max level: ${max_level}`, posX, posY);
  }
  ctx.fillText(`Level: ${level}`, posX, posY + 15);

  ctx.textAlign = "right";
  var [posX, posY] = [canvas.width - 10, 15];
  if (typeof max_score != "undefined") {
    ctx.fillText(`Max score: ${max_score}`, posX, posY);
  }
  ctx.fillText(`Score: ${score}`, posX, posY + 15);
};

const drawSpace = () => {
  drawImage("space-canvas", "img-background", 0, 0);
};

function drawLoose(score) {
  var canvas = document.getElementById("space-canvas");
  var ctx = canvas.getContext("2d");
  ctx.fillStyle = "red";
  ctx.textAlign = "center";
  ctx.font = "bold 50px Courier";
  var [posX, posY] = [canvas.width / 2, canvas.height / 2];
  ctx.fillText("You lost!", posX, posY);

  ctx.font = "bold 30px Courier";
  var [posX, posY] = [canvas.width / 2, canvas.height / 2 + 40];
  ctx.fillText(`Score: ${score}`, posX, posY);

  ctx.font = "25px Courier";
  var [posX, posY] = [canvas.width / 2, canvas.height / 2 + 70];
  ctx.fillText(`Press Reset to play again.`, posX, posY);
}

function drawWin(score) {
  var canvas = document.getElementById("space-canvas");
  var ctx = canvas.getContext("2d");
  ctx.fillStyle = "green";
  ctx.textAlign = "center";
  ctx.font = "bold 50px Courier";
  var [posX, posY] = [canvas.width / 2, canvas.height / 2];
  ctx.fillText("You won!", posX, posY);

  ctx.font = "bold 30px Courier";
  var [posX, posY] = [canvas.width / 2, canvas.height / 2 + 40];
  ctx.fillText(`Score: ${score}`, posX, posY);
}

const drawShip = (ship) => {
  pos = ship.map((pos) => getCoordsFromPosition(pos));
  drawImage("space-canvas", "img-rocket_01", pos[0][0], pos[0][1]);
  drawImage("space-canvas", "img-rocket_02", pos[1][0], pos[1][1]);
  drawImage("space-canvas", "img-rocket_03", pos[2][0], pos[2][1]);
  drawImage("space-canvas", "img-rocket_04", pos[3][0], pos[3][1]);
};

const drawAliens = (aliens) => {
  aliens.forEach((position) => {
    var pos = getCoordsFromPosition(position);
    drawImage("space-canvas", "img-alien", pos[0], pos[1]);
  });
};

const drawMissiles = (missiles) => {
  missiles.forEach((position) => {
    var pos = getCoordsFromPosition(position);
    drawImage("space-canvas", "img-fire", pos[0], pos[1]);
  });
};

const drawPlocha = (plocha) => {
  const aliens = plocha.a;
  const missiles = plocha.m;
  const ship = plocha.sh;
  const score = plocha.sc;
  const level = plocha.l;
  const status = plocha.st;
  const max_score = plocha.ms;
  const max_level = plocha.ml;

  drawSpace();
  drawStats("space-canvas", score, level, max_score, max_level);
  switch (status) {
    case 1:
      drawAliens(aliens);
      drawMissiles(missiles);
      drawShip(ship);
      break;
    case 2:
      drawLoose(score);
      break;
    case 3:
      drawWin(score);
      break;
  }
};

const setChildren = (parent, children) => {
  parent.innerHTML = children.map((el) => el.outerHTML).join("");
};

const getPage = (name) => {
  document.removeEventListener("keydown", sendKeyCode);
  fetch(`http://localhost:8080/site/${name}`)
    .then((res) => res.json())
    .then((data) => {
      setChildren(name === "index" ? document.body : document.getElementById("main"), json2elements(data));
    });
};

getPage("index");

const getGameList = () => {
  document.removeEventListener("keydown", sendKeyCode);
  fetch("http://localhost:8080/game/list")
    .then((res) => res.json())
    .then((data) => setChildren(document.getElementById("main"), json2elements(data)));
};

const getUserGameList = () => {
  document.removeEventListener("keydown", sendKeyCode);
  fetch("http://localhost:8080/admin/list")
    .then((res) => res.json())
    .then((data) => setChildren(document.getElementById("main"), json2elements(data)));
};

const getImportExport = () => {
  document.removeEventListener("keydown", sendKeyCode);
  fetch("http://localhost:8080/admin/csv")
    .then((res) => res.json())
    .then((data) => setChildren(document.getElementById("main"), json2elements(data)));
};

const spectateGame = (gameID) => {
  fetch(`http://localhost:8080/game/spectate/${gameID}`)
    .then((res) => res.json())
    .then((data) => setChildren(document.getElementById("main"), json2elements(data)))
    .then(() => {
      fetch(`http://localhost:8080/game/getplocha/${gameID}`);
    });
};

const register = () => {
  $.ajax({
    url: "http://localhost:8080/register",
    type: "post",
    data: $("#form-register").serialize(),
    success: function () {
      getPage("login");
      alert("You have been successfuly registered. Please Log In.");
    },
    error: function (request, status, error) {
      const obj = JSON.parse(request.responseText);
      alert(obj.join("\n"));
    },
  });
  return false;
};

const signin = () => {
  $.ajax({
    url: "http://localhost:8080/login",
    type: "post",
    data: $("#form-login").serialize(),
    success: function () {
      getPage("index");
      alert("You have successfuly logged in.");
    },
    error: function (request, status, error) {
      const obj = JSON.parse(request.responseText);
      alert(obj.join("\n"));
    },
  });
  return false;
};

const sendCSV = () => {
  $.ajax({
    url: "http://localhost:8080/admin/csv/import",
    type: "post",
    data: $("#import"),
    success: function () {
      getPage("index");
      alert("You have successfuly logged in.");
    },
    error: function (request, status, error) {
      const obj = JSON.parse(request.responseText);
      alert(obj.join("\n"));
    },
  });
};

const logOut = () => {
  fetch("http://localhost:8080/logout").then(() => {
    window.location.href = "/";
  });
};

const createNewGame = () => {
  document.removeEventListener("keydown", sendKeyCode);
  fetch(`http://localhost:8080/game/create`)
    .then((res) => res.json())
    .then((data) => {
      setChildren(document.getElementById("main"), json2elements(data.web));
      fetch(`http://localhost:8080/game/getplocha/${data.gameid}`);
    });
};

const startGame = () => {
  fetch(`http://localhost:8080/game/start`).then((res) => {
    if (res.status === 201) {
      document.addEventListener("keydown", sendKeyCode);
    }
  });
};

const resetGame = () => {
  fetch(`http://localhost:8080/game/reset`);
};

const joinGame = () => {
  document.removeEventListener("keydown", sendKeyCode);
  const pin = document.getElementById("pin-input").value;
  fetch(`http://localhost:8080/game/join?pin=${pin}`)
    .then((res) => res.json().then((data) => ({ status: res.status, data: data })))
    .then((res) => {
      if (res.status === 200) {
        setChildren(document.getElementById("main"), json2elements(res.data));
      } else {
        alert("You put a wrong PIN!");
      }
    });
};

var ws = new WebSocket("ws://localhost:8082");

ws.onmessage = function (msg) {
  const plocha = JSON.parse(msg.data);
  if (document.getElementById("space-canvas")) drawPlocha(plocha);
};
