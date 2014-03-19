TODO
====

- [X] After "HELLO" gather all required stats for a player. 
- [x] Add a "LOBBY" command which returns all lobbies
- [x] MAC format verification.
- [x] Location verification (lattitude and longitude)
- [x] Provide a startup message server side with some diagnostic info.
- [x] Figure out why program crashes with null when client <C^c>
- [x] Handle teams in lobby.
- [x] Check that Lobby is atLobby before joining. 
- [x] Generate alpha numberic lobby id's by random. 
- [x] A leave command to leave the current lobby. If you're last to leave a lobby the lobby should be destoryed. 
- [X] Dump information to player when they join a lobby: Arena boundaries, player information, score....
- [X] All commands entered into CMD should be .toUpper
- [x] Change communications to be in json or some other strict format   and reduce number of messages. In essence stream line the protocol. This should probably be done last since we are far down this road. 
- [x] START command 
- [ ] When a player joins the player should get info on the arena, flag   locations, all the other players coordinates and usernames, and the scores. Existing players should get the new players username and location. 
- [x] GPS command to accept a gps update from client.
- [x] Support a command for when client has forced a flag holder to drop the flag. It will need to send the bt mac of the player that dropped so they can be killed. It will also set the flag holder as the user. 
- [\] Upon receiving an update from players location check if they have scored, gone out of bounds, picked up the flag. 
- [x] Lobby command while in lobby should send information about the lobby. 
- [ ] When creating a lobby a accuracy range should be included for how close someone needs to be to pick up a flag or to score. 
- [x] Come up with a hierarchy that Arena, Base, Flag and Player can inherit from to get everything having to do with location methods. There needs to be a intermediary between the top level and Base of Flag that includes N S E W and the associatied methods. 
- [x] Check if player is holding the opposite teams flag before scoring
- [x] Go through code and bring up to standards and organize. 
- [x] game should end after x minutes or x points.
- [x] Scoring points when going to oppoonents base. Something is
  switched around. 
- [x] Latitude, and longitude is flip flopped on flags and bases. 
- [X] Remove gps from Hello command and move it to when you join a
  lobby.
- [ ] Parent class for player, flag and base that has team and arena
  methods.
- [x] Blue Flags and Bases are being generated outside the bound of the arena or too close to the edge.
- [X] Don't spawn when you go to your base unless you're dead.
- [X] Not accepting 10:40:f3:97:28:9e as a valid mac.
- [X] Start needs min 2 players.
- [ ] Leave is not leaving the lobby. 
- [ ] Flags and Bases latitudes being generated outside the arena.
  initial location 12,23 arena size 69.
- [ ] Kill players outside the arena.
- [ ] Waiting for players lobby status.
- [ ] Dead players can kill live playesr and steal flags. 
- [ ] Bad GPS gives no error
