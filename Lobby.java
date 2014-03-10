/**
 * Author: Andrew Hood <andrewhood125@gmail.com>
 * Description: Store everything that a single lobby will need.
 * Copyright (c) 2014 Andrew Hood. All rights reserved.
 */

import java.util.ArrayList;

class Lobby
{
  public static final int atLobby = 0;
  public static final int inProgress = 1;
  // All the players in the lobby
  ArrayList<Player> players = new ArrayList<Player>();
  // The N E S W boundaries in lat and long. 
  Arena arena;
  // The flags associated with this lobby
  Flag redFlag;
  Flag blueFlag;
  // Store the current game state of the lobby {lobby, in progress, destroy}
  int gameState;
  // a unique 4 digit id amoing all the lobbies
  String lobbyID;
  
  Lobby(Player host, String lobbyID, double arenaSize)
  {
    this.lobbyID = lobbyID;
    // Create arena based on arenaSize and Players gps coordinates.
    arena = new Arena(host.getLatitude(), host.getLongitude(), arenaSize);
    players.add(host);
    double flagLatitude = arena.getNorth() + arena.getSouth() / 2;
    redFlag = new Flag(flagLatitude, arena.getWest() + arenaSize*.15);
    blueFlag = new Flag(flagLatitude, arena.getEast() - arenaSize*.15);
    gameState = Lobby.atLobby;
  }

  public String getLobbyID()
  {
    return lobbyID;
  }

  public void addNewPlayer(Player newPlayer)
  {
    players.add(newPlayer);
  }
}

