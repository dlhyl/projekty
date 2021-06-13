const Discord = require("discord.js");
const fs = require("fs");
const fetch = require("node-fetch");
const ytdl = require("ytdl-core");
const ytpl = require("ytpl");

const token = "";
const client = new Discord.Client();

var IDs = {};
var queue = null;

const config = {
  prefix: "!",
  commands: {
    hi: { name: "hi", desc: "Cutieee ta pozdravi <3" },
    luv: { name: "luv", param: "[@user]", desc: "Shows luv to someone <3" },
    gn: { name: "gn", desc: "Good nite bois" },
    domino: {
      name: "domino",
      desc: "Ukaze pravu tvar Domina :bear:",
    },
    meme: { name: "meme", desc: "Posle random meme z redditu" },
    play: {
      name: "play",
      param: "[youtube url]",
      desc: "Pusti song z url do voice channelu",
    },
    playpl: {
      name: "play",
      param: "[youtube playlist url] [pocet - optional]",
      desc: "Pusti cely playlist do voice",
    },
    skip: { name: "skip", desc: "Skipne song" },
    stop: { name: "stop", desc: "Stopne celu queue" },
  },
  commandsShitPost: {
    wipecutie: {
      name: "wipecutie",
      param: "[minutes]",
      desc: "Vymaze spravy cutie v shitposte za poslednych x minut",
    },
    wipemyass: {
      name: "wipemyass",
      param: "[minutes]",
      desc: "Vymaze tvoje spravy v shitposte za poslednych x minut",
    },
    ig: {
      name: "ig",
      param: "[add,remove,list] [nickname]",
      desc: "Prida/Vymaze IG ucet z watch listu.",
    },
  },
};

function isEmpty(obj) {
  return Object.keys(obj).length === 0 && obj.constructor === Object;
}

function loadID() {
  if (fs.existsSync("ID.json")) {
    let existingdata = fs.readFileSync("ID.json");
    IDs = JSON.parse(existingdata);
  }
}

function addToWatch(name, id) {
  if (isEmpty(IDs)) loadID();
  IDs[name] = id;
  fs.writeFileSync("ID.json", JSON.stringify(IDs));
}

var getFeed = ({
  id,
  sessionid,
  userid,
  min_timestamp,
  headers = {
    "x-ig-capabilities": "3brTvw==",
    "user-agent": "Instagram 10.3.2 (iPhone7,2; iPhone OS 9_3_3; en_US; en-US; scale=2.00; 750x1334) AppleWebKit/420+",
    host: "i.instagram.com",
  },
}) =>
  fetch(`https://i.instagram.com/api/v1/feed/user/${id}?min_timestamp=${min_timestamp}`, {
    headers: Object.assign(headers, {
      cookie: `sessionid=${sessionid}; ds_user_id=${userid}`,
    }),
  }).then((res) => res.json());

function getFeedAll() {
  if (isEmpty(IDs)) loadID();

  Object.keys(IDs).forEach(function (key) {
    getFeed({
      id: IDs[key],
      sessionid: "4105239046%3AeIc8NaYY2Qq82a%3A24",
      userid: "4105239046",
      min_timestamp: ((Date.now() - 301 * 1000) / 1000).toFixed(0),
    }).then((res) => {
      if (res.status === "ok" && res.num_results) {
        for (x in res.items) {
          client.channels
            .fetch("689877703070974030")
            .then((channel) => channel.send(`${res.items[x].user.username} just made a post!\nhttps://www.instagram.com/p/${res.items[x].code}`));
        }
      }
    });
  });
}

function playSong(song) {
  if (!song) {
    queue.voiceChannel.leave();
    queue = null;
    return;
  }

  const dispatcher = queue.connection
    .play(ytdl(song.url))
    .on("finish", () => {
      queue.songs.shift();
      playSong(queue.songs[0]);
    })
    .on("error", (error) => console.error(error));
  dispatcher.setVolumeLogarithmic(queue.volume / 5);
  queue.textChannel.send(`Start playing: **${song.title}** (1/${queue.songs.length})`);
}

function skipSong(msg) {
  if (!msg.member.voice.channel) {
    msg.channel.send(msg.author + "You have to be in a voice channel to stop the music!");
    return;
  }
  if (!queue) {
    msg.channel.send("There is no song that I could skip!");
    return;
  }
  queue.connection.dispatcher.end();
}

function stopSong(message) {
  if (!message.member.voice.channel) {
    message.channel.send(message.author + "You have to be in a voice channel to stop the music!");
    return;
  }

  queue.songs = [];
  queue.connection.dispatcher.end();
}

client.on("ready", () => {
  console.log(`Logged in as ${client.user.tag}!`);
});

client.on("message", (msg) => {
  if (msg.content.indexOf(config.prefix) !== 0) return;

  const args = msg.content.slice(config.prefix.length).trim().split(/ +/g);
  const command = args.shift().toLowerCase();

  if (command === "hi") {
    // const ayy = client.emojis.find(emoji => emoji.name === "kekw");
    msg.channel.send(`Cmuq <:kekw:687383633970987028> ${msg.author}`);
  }

  if (command === "ig") {
    if (msg.channel.id !== "689877703070974030") return;
    var cmnd = args[0];
    var igAcc = args[1];
    if (typeof cmnd === "undefined") {
      msg.channel.send(`${msg.author}, na niečo si zabudol <:kekw:687383633970987028>`);
      return;
    }

    if (cmnd === "add") {
      if (typeof igAcc === "undefined") {
        msg.channel.send(`${msg.author}, na niečo si zabudol <:kekw:687383633970987028>`);
        return;
      }
      fetch(`https://www.instagram.com/${igAcc}/?__a=1`)
        .then((res) => res.json())
        .then((data) => {
          if (data.graphql.user.is_private) {
            msg.channel.send(`${msg.author}, bruh the profile is private.`);
          } else {
            addToWatch(igAcc, data.graphql.user.id);
            msg.channel.send(`${igAcc} added to the watch list.`);
          }
        })
        .catch((err) => msg.channel.send(`${msg.author}, couldnt find profile`));
    } else if (cmnd === "remove") {
      if (typeof igAcc === "undefined") {
        msg.channel.send(`${msg.author}, na niečo si zabudol <:kekw:687383633970987028>`);
        return;
      }
      if (isEmpty(IDs)) loadID();
      delete IDs[igAcc];
      fs.writeFileSync("ID.json", JSON.stringify(IDs));
      msg.channel.send(`${igAcc} removed from the watch list.`);
    } else if (cmnd === "list") {
      if (isEmpty(IDs)) loadID();
      msg.channel.send(`Instagram watch list: ${Object.keys(IDs)}`);
    }
  }

  if (command === "commands") {
    var text = "";
    for (const key in config.commands) {
      if (config.commands.hasOwnProperty(key)) {
        const element = config.commands[key];
        text += "**" + config.prefix + element.name + (element.hasOwnProperty("param") ? " " + element.param : "") + "** -> " + element.desc + "\n";
      }
    }
    if (msg.channel.id === "689877703070974030")
      for (const key in config.commandsShitPost) {
        if (config.commandsShitPost.hasOwnProperty(key)) {
          const element = config.commandsShitPost[key];
          text += "**" + config.prefix + element.name + (element.hasOwnProperty("param") ? " " + element.param : "") + "** -> " + element.desc + "\n";
        }
      }
    msg.channel.send("Command list: \n" + text);
  }

  if (command === "luv") {
    if (msg.mentions.users.size) {
      msg.channel.send(`${msg.mentions.users.first()}`, {
        files: ["https://cdn.ebaumsworld.com/mediaFiles/picture/2502212/86225451.jpg"],
      });
    }
  }

  if (command === "gn") {
    msg.channel.send({
      files: ["https://i.pinimg.com/originals/b7/a1/33/b7a133b4249c8e5d92db17e8c725e97b.jpg"],
    });
  }

  if (command === "domino") {
    msg.channel.send({
      files: ["https://media.giphy.com/media/3o72F7Yl8y35V5gCZO/giphy.gif"],
    });
  }

  if (command === "wipecutie") {
    if (msg.channel.id !== "689877703070974030") return;
    let numOfMinutes = args[0];
    let timeStamp = Date.now() - numOfMinutes * 60 * 1000;
    if (typeof numOfMinutes === "undefined") {
      msg.channel.send(`Bruv ${msg.author}, u need to specify the amount.`);
    } else {
      msg.delete().then(() => {
        client.channels
          .fetch("689877703070974030")
          .then((channel) => {
            //console.log(channel);
            channel.messages
              .fetch({ limit: 100 })
              .then((message) => {
                message.map((e) => {
                  console.log("looking @ messages..");
                  if (e.createdTimestamp < timeStamp) {
                    console.log("timestamp older than " + numOfMinutes + " minutes");
                    return;
                  }
                  if (e.author.id === client.user.id && !e.pinned) {
                    console.log("removing message..");
                    e.delete()
                      .then((msg) => console.log(`Deleted message from ${msg.author.username}`))
                      .catch(console.error);
                  }
                });
                //console.log(message);
                //console.log("msg-" + msg.author);
              })
              .catch(console.error);
          })
          .catch(console.error);
      });
    }
  }

  if (command === "meme") {
    fetch("https://meme-api.herokuapp.com/gimme/1")
      .then((response) => {
        return response.json();
      })
      .then((data) => {
        //console.log(data);
        msg.channel.send("Sending random meme:\n", {
          files: [data.memes[0].url],
        });
      });
  }

  if (command === "domino2") {
    msg.channel.send({
      files: ["https://lubomirdlhy.sk/domino2.gif"],
    });
  }

  if (command === "domino3") {
    msg.channel.send({
      files: ["https://lubomirdlhy.sk/domino3.gif"],
    });
  }

  if (command === "wipemyass") {
    if (msg.channel.id !== "689877703070974030") return;
    let numOfMinutes = args[0];
    let timeStamp = Date.now() - numOfMinutes * 60 * 1000;
    if (typeof numOfMinutes === "undefined") {
      msg.channel.send(`Bruv ${msg.author}, u need to specify the amount.`);
    } else {
      client.channels
        .fetch("689877703070974030")
        .then((channel) => {
          //console.log(channel);
          channel.messages
            .fetch({ limit: 100 })
            .then((message) => {
              message.map((e) => {
                console.log("looking @ messages..");
                if (e.createdTimestamp < timeStamp) {
                  console.log("timestamp older than " + numOfMinutes + " minutes");
                  return;
                }
                if (e.author.id === msg.author.id && !e.pinned) {
                  console.log("removing message..");
                  e.delete()
                    .then((msg) => console.log(`Deleted message from ${msg.author.username}`))
                    .catch(console.error);
                }
              });
              //console.log(message);
              //console.log("msg-" + msg.author);
            })
            .catch(console.error);
        })
        .catch(console.error);
    }
  }

  if (command === "play") {
    const voiceChannel = msg.member.voice.channel;
    if (!voiceChannel) {
      msg.channel.send(msg.author + ", you need to be in a voice channel to play music!");
      return;
    }

    const permissions = voiceChannel.permissionsFor(msg.client.user);
    if (!permissions.has("CONNECT") || !permissions.has("SPEAK")) {
      msg.channel.send("I need the permissions to join and speak in your voice channel!");
      return;
    }

    var song = null;

    if (args[0].includes("list")) {
      console.log("LIST");
      var items = [];
      if (typeof args[1] != "undefined") {
        ytpl(args[0], { limit: parseInt(args[1]) }, function (err, playlist) {
          if (err) throw err;
          console.log("W LIMIT" + playlist.items.length);
          //console.log("FOUND PLAYLIST1 - " + playlist.items.length);
          items = playlist.items;

          if (!queue) {
            queue = {
              textChannel: msg.channel,
              voiceChannel: voiceChannel,
              connection: null,
              songs: [],
              volume: 2,
              playing: true,
            };

            for (var item of items) {
              song = {
                title: item.title,
                url: item.url_simple,
              };
              queue.songs.push(song);
            }

            try {
              voiceChannel.join().then((conn) => {
                queue.connection = conn;
                playSong(queue.songs[0]);
              });
            } catch (err) {
              console.log(err);
              queue = null;
              return;
            }
          } else {
            for (var item of items) {
              song = {
                title: item.title,
                url: item.url_simple,
              };
              queue.songs.push(song);
            }
            msg.channel.send("Playlist has been added to the queue. (" + queue.songs.length + "/" + queue.songs.length + ")");
          }
        });
      } else {
        ytpl(args[0], function (err, playlist) {
          if (err) throw err;
          console.log("WO LIMIT" + playlist.items.length);
          //console.log("FOUND PLAYLIST1 - " + playlist.items.length);
          items = playlist.items;

          if (!queue) {
            queue = {
              textChannel: msg.channel,
              voiceChannel: voiceChannel,
              connection: null,
              songs: [],
              volume: 2,
              playing: true,
            };

            for (var item of items) {
              song = {
                title: item.title,
                url: item.url_simple,
              };
              queue.songs.push(song);
            }

            msg.channel.send("Playlist has been added to the queue. (" + queue.songs.length + "/" + queue.songs.length + ")");

            try {
              voiceChannel.join().then((conn) => {
                queue.connection = conn;
                playSong(queue.songs[0]);
              });
            } catch (err) {
              console.log(err);
              queue = null;
              return;
            }
          } else {
            for (var item of items) {
              song = {
                title: item.title,
                url: item.url_simple,
              };
              queue.songs.push(song);
            }
            msg.channel.send("Playlist has been added to the queue. (" + queue.songs.length + "/" + queue.songs.length + ")");
          }
        });
      }
    } else {
      ytdl
        .getInfo(args[0])
        .then((data) => {
          //msg.channel.send(data.title);
          //msg.channel.send(data.video_url);
          song = {
            title: data.title,
            url: data.video_url,
          };

          if (!queue) {
            queue = {
              textChannel: msg.channel,
              voiceChannel: voiceChannel,
              connection: null,
              songs: [],
              volume: 2,
              playing: true,
            };
            queue.songs.push(song);

            try {
              voiceChannel.join().then((conn) => {
                queue.connection = conn;
                playSong(queue.songs[0]);
              });
            } catch (err) {
              console.log(err);
              queue = null;
              return;
            }
          } else {
            queue.songs.push(song);
            msg.channel.send("Song " + song.title + " has been added to the queue. (" + queue.songs.length + "/" + queue.songs.length + ")");
          }
        })
        .catch((error) => console.log(error));
    }

    msg.delete();
  }

  if (command === "skip") {
    skipSong(msg);
  }

  if (command === "stop") {
    stopSong(msg);
  }
});

client.login(token).then(() => {
  client.setInterval(() => {
    // client.channels
    //   .fetch("689877703070974030")
    //   .then(channel => channel.send("getAll"));
    getFeedAll();
  }, 5 * 60 * 1000);
});
