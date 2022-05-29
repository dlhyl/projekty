// Lubomir Dlhy
import db from "./db.js";

class Game {
  static pins = new Set();
  aliens = [1, 3, 5, 7, 9, 23, 25, 27, 29, 31];
  ship = [104, 114, 115, 116];
  direction = 1;
  missiles = [];
  level = 1;
  speed = 512;
  score = 0;
  status = GAME_STATUS.CREATED;
  loopGame = null;
  loopPlocha = null;
  id = null;
  pin = null;
  players = [];
  spectators = [];

  constructor() {
    this.generatePIN();
    this.generateID();
  }

  delete() {
    if (this.players.length === 0) {
      Game.pins.delete(this.pin);
      clearInterval(this.loopGame);
      clearInterval(this.loopPlocha);
    }
  }

  reset() {
    clearInterval(this.loopGame);
    clearInterval(this.loopPlocha);
    this.aliens = [1, 3, 5, 7, 9, 23, 25, 27, 29, 31];
    this.ship = [104, 114, 115, 116];
    this.direction = 1;
    this.missiles = [];
    this.level = 1;
    this.speed = 512;
    this.score = 0;
    this.status = GAME_STATUS.CREATED;
    this.gameLoop();
  }

  generateID() {
    this.id = Date.now().toString(36) + Math.random().toString(36).substr(2);
  }

  generatePIN() {
    var pinnumber = Math.floor(Math.random() * 10000);
    var gen_pin = pinnumber.toString().padStart(4, "0");
    while (Game.pins.has(gen_pin)) {
      pinnumber = Math.floor(Math.random() * 10000);
      gen_pin = pinnumber.toString().padStart(4, "0");
    }
    Game.pins.add(gen_pin);
    this.pin = gen_pin;
  }

  moveAliens() {
    var i = 0;
    for (i = 0; i < this.aliens.length; i++) {
      this.aliens[i] = this.aliens[i] + this.direction;
    }
    this.direction *= -1;
  }

  lowerAliens() {
    var i = 0;
    for (i = 0; i < this.aliens.length; i++) {
      this.aliens[i] += 11;
    }
  }

  moveMissiles() {
    var i = 0;
    for (i = 0; i < this.missiles.length; i++) {
      this.missiles[i] -= 11;
      if (this.missiles[i] < 0) this.missiles.splice(i, 1);
    }
  }

  checkKey(keyCode) {
    if (keyCode == "37") {
      if (this.ship[0] > 100) {
        var i = 0;
        for (i = 0; i < this.ship.length; i++) {
          this.ship[i]--;
        }
      }
    } else if (keyCode == "39" && this.ship[0] < 108) {
      var i = 0;
      for (i = 0; i < this.ship.length; i++) {
        this.ship[i]++;
      }
    } else if (keyCode == "32") {
      this.missiles.push(this.ship[0] - 11);
    }
  }

  checkCollisionsMA() {
    var change = false;
    for (var i = 0; i < this.missiles.length; i++) {
      if (this.aliens.includes(this.missiles[i])) {
        var alienIndex = this.aliens.indexOf(this.missiles[i]);
        this.aliens.splice(alienIndex, 1);
        this.missiles.splice(i, 1);
        this.score += 10;
        this.players.forEach((player) => {
          if (player.profile.max_score < this.score) {
            player.profile.max_score = this.score;
            change = true;
          }
        });
      }
    }
    if (change) db.write();
  }

  RaketaKolidujeSVotrelcom() {
    for (var i = 0; i < this.aliens.length; i++) {
      if (this.aliens[i] > 98) {
        return true;
      }
    }
    return false;
  }

  nextLevel() {
    var change = false;
    this.level++;
    this.players.forEach((player) => {
      if (player.profile.max_level < this.level) {
        player.profile.max_level = this.level;
        change = true;
      }
    });
    if (change) db.write();
    if (this.level == 1) this.aliens = [1, 3, 5, 7, 9, 23, 25, 27, 29, 31];
    if (this.level == 2) this.aliens = [1, 3, 5, 7, 9, 13, 15, 17, 19, 23, 25, 27, 29, 31];
    if (this.level == 3) this.aliens = [1, 5, 9, 23, 27, 31];
    if (this.level == 4) this.aliens = [45, 53];
    if (this.level > 4) {
      this.aliens = [1, 3, 5, 7, 9, 23, 25, 27, 29, 31];
      this.speed = this.speed / 2;
    }
    this.gameLoop();
  }

  win() {
    this.status = GAME_STATUS.WIN;
    this.sendPlochaToPlayer();
    this.sendPlochaToSpectators();
  }

  loose() {
    this.status = GAME_STATUS.LOSS;
    this.sendPlochaToPlayer();
    this.sendPlochaToSpectators();
  }

  gameLoop() {
    var a = 0;
    const that = this;
    this.status = GAME_STATUS.RUNNING;
    this.loopGame = setInterval(function () {
      that.moveAliens();
      that.moveMissiles();
      that.checkCollisionsMA();
      if (a % 4 == 3) that.lowerAliens();
      if (that.RaketaKolidujeSVotrelcom()) {
        clearInterval(that.loopPlocha);
        clearInterval(that.loopGame);
        that.missiles = [];
        that.loose();
      }
      a++;
    }, that.speed);

    this.loopPlocha = setInterval(function () {
      that.sendPlochaToPlayer();
      that.sendPlochaToSpectators();
      if (that.aliens.length === 0) {
        clearInterval(that.loopPlocha);
        clearInterval(that.loopGame);
        that.missiles = [];
        that.win();
        setTimeout(function () {
          that.nextLevel();
        }, 1000);
      }
    }, that.speed / 2);
  }

  getPlocha() {
    return {
      a: this.aliens,
      m: this.missiles,
      sh: this.ship,
      l: this.level,
      sc: this.score,
      st: this.status,
    };
  }

  getPlayerPlocha(player) {
    return {
      ...this.getPlocha(),
      ms: player.profile.max_score,
      ml: player.profile.max_level,
    };
  }

  sendPlochaToPlayer() {
    this.players.forEach((player) => player.ws.send(JSON.stringify(this.getPlayerPlocha(player))));
  }

  sendPlochaToSpectators() {
    const plocha = JSON.stringify(this.getPlocha());
    this.spectators.forEach((spec) => {
      spec.ws.send(plocha);
    });
  }

  addPlayer(user) {
    this.players.push(user);
  }

  removePlayer(user) {
    var index = this.players.indexOf(user);
    if (index >= 0) this.players.splice(index, 1);
    this.delete();
  }

  addSpectator(user) {
    this.spectators.push(user);
  }

  removeSpectator(user) {
    var index = this.spectators.indexOf(user);
    if (index >= 0) this.spectators.splice(index, 1);
  }
}

const GAME_STATUS = {
  CREATED: 0,
  RUNNING: 1,
  LOSS: 2,
  WIN: 3,
};

class Profile {
  constructor(sessionID, email = null, login = null, pw_hash = null, name = "[N/A]", max_level = 1, max_score = 0) {
    this.sessionID = sessionID;
    this.email = email;
    this.login = login;
    this.pw_hash = pw_hash;
    this.name = name;
    this.max_level = max_level;
    this.max_score = max_score;
  }
}

export { GAME_STATUS, Game, Profile };
