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
- [ ] Change communications to be in json or some other strict format
  and reduce number of messages. In essence stream line the protocol.
This should probably be done last since we are far down this road. 
- [x] START command 
- [ ] When a player joins the player should get info on the arena, flag
  locations, all the other players coordinates and usernames, and the
scores. Existing players should get the new players username and
location. 
- [x] GPS command to accept a gps update from client.
- [ ] Support a command for when client has forced a flag holder to drop
  the flag. It will need to send the bt mac of the player that dropped
so they can be killed. It will also set the flag holder as the user. 
- [ ] Upon receiving an update from players location check if they have
  scored, gone out of bounds, picked up the flag. 
- [ ] Lobby command while in lobby should send information about the
  lobby. 
- [ ] When creating a lobby a accuracy range should be included for how
  close someone needs to be to pick up a flag or to score. 
- [ ] Write a standard for the ctf protocol
- [ ] Come up with a hierarchy that Arena, Base, Flag and Player can
  inherit from to get everything having to do with location methods.
There needs to be a intermediary between the top level and Base of Flag
that includes N S E W and the associatied methods. 
- [ ] Check if player is holding the opposite teams flag before scoring
- [ ] Go through code and bring up to standards and organize. 
