var missiles = [];
var ship = [104, 114, 115, 116];
var aliens = [1, 3, 5, 7, 9, 23, 25, 27, 29, 31];

var rocketImages = Array(4);
var backgroundImg;
var alienImg;
var fireImg;
var imgsToLoad = 7;

var score = 0;
var level = 1;
var speed = 512;

var debug = false;

// Audio source - Lofi Night Dreaming - (IG version Loop 03) by Lesfm - https://pixabay.com/music/beats-lofi-night-dreaming-ig-version-loop-03-7319/
// License: https://pixabay.com/service/license/
var audio = new Audio("assets/audio.mp3");
audio.loop = true;
audio.volume = 0.3;
var playingAudio = false;

// function to remove table with the original game
function removeTable() {
  var space = document.getElementById("space");
  var spaceTable = space.querySelector("table");
  space.removeChild(spaceTable);
}

// function to create and put canvas element on the site
function createCanvas() {
  var space = document.getElementById("space");
  var canvas = document.createElement("canvas");
  canvas.id = "space-canvas";
  canvas.width = 528;
  canvas.height = 576;
  space.append(canvas);
}

function createResetButton() {
  var resetButton = document.createElement("button");
  resetButton.id = "reset";
  resetButton.innerText = "Reset";
  document.body.appendChild(resetButton);
  resetButton.addEventListener("click", () => {
    if (debug) console.log("Game reset!");
    ship = [104, 114, 115, 116];
    aliens = [1, 3, 5, 7, 9, 23, 25, 27, 29, 31];
    missiles = [];
    level = 1;
    speed = 512;
    score = 0;
    if (!running) {
      gameLoop();
      running = true;
    }
  });
  resetButton.addEventListener("keydown", function (e) {
    e.preventDefault();
    e.stopPropagation();
  });
}

function createMusicButton() {
  var musicButton = document.createElement("button");
  musicButton.id = "music";
  musicButton.innerText = "♪ Play Music ♪";
  musicButton.addEventListener("click", () => {
    if (!playingAudio) {
      if (debug) console.log("Music started playing.");
      audio.play();
      musicButton.innerText = "♪ Stop Music ♪";
      playingAudio = true;
    } else {
      if (debug) console.log("Music stopped playing.");
      audio.pause();
      musicButton.innerText = "♪ Play Music ♪";
      playingAudio = false;
    }
  });
  document.body.appendChild(musicButton);
}

// function to load all images
// All images were created by Lubomir Dlhy.
// All images can be used for commercial and non-commercial purposes.
function loadImages() {
  backgroundImg = new Image(528, 576);
  backgroundImg.src = "assets/background.png";

  for (let xd = 0; xd < 4; xd++) {
    rocketImages[xd] = new Image(48, 48);
    rocketImages[xd].src = "assets/rocket_0" + (xd + 1).toString() + ".png";
  }

  alienImg = new Image(48, 48);
  alienImg.src = "assets/alien.png";

  fireImg = new Image(48, 48);
  fireImg.src = "assets/fire.png";
}

// Helper function to convert position to coordinates
function getCoordsFromPosition(pos) {
  return [(pos % 11) * 48, Math.floor(pos / 11) * 48 + 48];
}

// draw score and level on canvas
function drawScore() {
  var canvas = document.getElementById("space-canvas");
  var ctx = canvas.getContext("2d");
  ctx.fillStyle = "red";
  ctx.textAlign = "left";
  ctx.font = "bold 25px Courier";
  var [posX, posY] = [10, 25];
  ctx.fillText(`Level: ${level}`, posX, posY);

  ctx.textAlign = "right";
  var [posX, posY] = [canvas.width - 10, 25];
  ctx.fillText(`Score: ${score}`, posX, posY);
}

loadImages();
removeTable();
createCanvas();
createResetButton();
createMusicButton();

// |                                         |
// v  Changed original functions are below!  v

// drawShip changed to draw the spaceship on the canvas
function drawShip() {
  var ctx = document.getElementById("space-canvas").getContext("2d");
  positions = ship.map((pos) => getCoordsFromPosition(pos));
  rocketImages.forEach((img, index) => {
    ctx.drawImage(img, positions[index][0], positions[index][1]);
  });
}

// drawSpace changed to draw the background and score on the canvas
function drawSpace() {
  var ctx = document.getElementById("space-canvas").getContext("2d");
  ctx.drawImage(backgroundImg, 0, 0);
  drawScore();
}

// drawAliens changed to draw all aliens on the canvas
function drawAliens() {
  var ctx = document.getElementById("space-canvas").getContext("2d");
  aliens.forEach((position) => {
    var pos = getCoordsFromPosition(position);
    ctx.drawImage(alienImg, pos[0], pos[1]);
  });
}

// drawMissiles changed to draw all missilies on the canvas
function drawMissiles() {
  var ctx = document.getElementById("space-canvas").getContext("2d");
  missiles.forEach((position) => {
    var pos = getCoordsFromPosition(position);
    ctx.drawImage(fireImg, pos[0], pos[1]);
  });
}

// loose changed to show loss message and score
function loose() {
  if (debug) console.log(`Game lost. Score=${score}. Level=${level}`);
  drawSpace();
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
  running = false;
}

// win changed to show win message
function win() {
  if (debug) console.log(`Game won. Score=${score}. Level=${level}`);
  drawSpace();
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

// checkCollisionsMA changed to add points to the score when enemy destroyed
function checkCollisionsMA() {
  for (var i = 0; i < missiles.length; i++) {
    if (aliens.includes(missiles[i])) {
      var alienIndex = aliens.indexOf(missiles[i]);
      aliens.splice(alienIndex, 1);
      missiles.splice(i, 1);
      score += 10;
      if (debug) console.log(`Enemy killed (+10).`);
    }
  }
}

// checkKey changed to allow for keys 'J' (74) and 'L' (76)
function checkKey(e) {
  e = e || window.event;
  if (e.keyCode == "37" || e.keyCode == "74") {
    if (ship[0] > 100) {
      var i = 0;
      for (i = 0; i < ship.length; i++) {
        ship[i]--;
      }
    }
  } else if ((e.keyCode == "39" || e.keyCode == "76") && ship[0] < 108) {
    var i = 0;
    for (i = 0; i < ship.length; i++) {
      ship[i]++;
    }
  } else if (e.keyCode == "32") {
    missiles.push(ship[0] - 11);
  }
}
