/**
 * Author: Andrew Hood <andrewhood125@gmail.com>
 * Description: Store everything that a single lobby will need.
 * Copyright (c) 2014 Andrew Hood. All rights reserved.
 */

import java.util.ArrayList;

class Lobby
{
  // All the players in the lobby
  ArrayList<Player> players = new ArrayList<Player>();
  // The N E S W boundaries in lat and long. 
  Arena arena  = new Arena();
  // The N E S W boundaries in lat and long.of the jail for each team
  Jail red = new Jail();
  Jail blue = new Jail();
  // The flags associated with this lobby
  Flag[] flags = new Flag[2];
  // Store the current game state of the lobby {lobby, in progress, destroy}
  int gameState;
  // a unique 4 digit id amoing all the lobbies
  int lobbyID;
  
  Lobby(Player host)
  {
    players.add(host);
  }

}
