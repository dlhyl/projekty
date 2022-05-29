# Space Game

Space Game is simple game made in JavaScript and uses HTML Canvas to visualize the content.

Controls:

- J / Left arrow - left movement
- L / Right arrow - right movement
- Space - shoot

## Client only version

Single player version, multiple levels, enemies speed up as leveling up.

### Use Cases:

- User can start the game
- User can restart the game
- User can play/stop audio

### Tech Stack:

- JavaScript
- HTML
- CSS

## Client-Server version

Multi-player version, multiple levels, REST API & WebSocket Communication

### Use Cases:

- User can register
- User can login
- User can logout
- User can start the game
- User can restart the game
- User can join other player's game using PIN
- User can spectate other players
- Admin can see list of games & users
- Admin can export users' data & score
- Data is encoded in JSON

### Tech Stack:

- JavaScript
- HTML
- CSS
- Express (REST API) & Express Session
- WebSockets
- lowdb (JSON DB)
