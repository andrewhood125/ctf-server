/**
 * Author: Andrew Hood <andrewhood125@gmail.com>
 * Description: Store everything that a single lobby will need.
 * Copyright (c) 2014 Andrew Hood. All rights reserved.
 */

import java.util.ArrayList;

class Lobby
{
  public static final int AT_LOBBY = 0;
  public static final int IN_PROGRESS = 1;
  public static final int SPECTATOR = 0;
  public static final int RED_TEAM = 1;
  public static final int BLUE_TEAM = 2;

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
  double size;
  
  Lobby(Player host, String lobbyID, double arenaSize)
  {
    this.lobbyID = lobbyID;
    // Create arena based on arenaSize and Players gps coordinates.
    arena = new Arena(host.getLatitude(), host.getLongitude(), arenaSize);
    host.setTeam(RED_TEAM);
    players.add(host);
    double flagLatitude = arena.getNorth() + arena.getSouth() / 2;
    redFlag = new Flag(flagLatitude, arena.getWest() + arenaSize*.15);
    blueFlag = new Flag(flagLatitude, arena.getEast() - arenaSize*.15);
    gameState = Lobby.AT_LOBBY;
    size = arenaSize;
  }

  public String getLobbyID()
  {
    return lobbyID;
  }
  
  public ArrayList<Player> getPlayers()
  {
	  return players;
  }
  
  public double getSize()
  {
	  return size;
  }
  
  public Arena getArena()
  {
	  return arena;
  }

  public int getNumberOfPlayers()
  {
    return players.size();
  }

  public int getGameState()
  {
    return gameState;
  }
  public String getGameStateString()
  {
	  if (gameState == 0)
	  {
		  return "AT_LOBBY";
	  }else if (gameState == 1)
	  {
		  return "IN_PROGRESS";
	  }else
	  {
		  return null;
	  }
  }

  public void setGameState(int gameState)
  {
    if(gameState >= 0 && gameState <= 1)
    {
      this.gameState = gameState;
    } 
  }
  public boolean isLobbyLeader(Player player)
  {
    if(player.equals(players.get(0)))
    {
      return true;
    } else {
      return false;
    }
  }
  
  public void start()
  {
    setGameState(Lobby.IN_PROGRESS);
    broadcast("The game has been started.");
    System.out.println(this + " has been started.");
    // Kill all players 
    for(int i = 0; i < players.size(); i++)
    {
      kill(players.get(i));
    }
  }

  public void kill(Player player)
  {
    player.setLifeState(Player.DEAD);
  }

  public void addNewPlayer(Player newPlayer)
  {
    if(players.size() % 2 != 0 )
    {
      newPlayer.setTeam(Lobby.BLUE_TEAM);
      broadcast(newPlayer.getUsername() + " joined blue team.");
    } else {
      newPlayer.setTeam(Lobby.RED_TEAM);
      broadcast(newPlayer.getUsername() + " joined red team.");
    }
    players.add(newPlayer);
  }
  public void removePlayer(Player player)
  {
    for(int i = 0; i < players.size(); i++)
    {
      if(players.get(i).equals(player))
      {
        players.remove(i);
        break;
      }
    }
  }
  public void broadcast(String broadcastMessage)
  {
    for(int i = 0; i < players.size(); i++)
    {
      players.get(i).send(broadcastMessage);
    }
  }
  public void broadcastLocation(Player player)
  {
    for(int i = 0; i < players.size(); i++)
    {
      if(!players.get(i).equals(player))
      {
        players.get(i).send("GPS: " + player.getUsername() + " " + player.getLatitude() + "," + player.getLongitude());
      }
    }
  }
  public void broadcastPlayers()
  {
	  broadcast("RED TEAM");
      broadcast("=========");
      for(int i = 0; i < players.size(); i++)
      {
   	   if(players.get(i).getTeam() == 1)
   	   {
   		   broadcast("Player: " + players.get(i).getUsername());
   	   }            	   
      }
      broadcast("BLUE TEAM");
      broadcast("=========");
      for(int i = 0; i < players.size(); i++)
      {
   	   if(players.get(i).getTeam() == 2)
   	   {
   		broadcast("Player: " + players.get(i).getUsername());
   	   }            	   
      }  
  }
}

