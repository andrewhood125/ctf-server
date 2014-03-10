/**
 * Author: Andrew Hood <andrewhood125@gmail.com>
 * Description: Listen for requests for new lobbies and keep track of current ones.
 * Copyright (c) 2014 Andrew Hood. All rights reserved.
 */

import java.io.IOException;
import java.util.ArrayList;
import java.net.ServerSocket;
import java.net.Socket;

class CTFServer
{
  // Hold the running lobbies
  public static ArrayList<Lobby> lobbies = new ArrayList<Lobby>();

  public static Lobby createLobby(Player host, double arenaSize)
  {
    String lobbyID = "";
    for(int i = 0; i < 5; i++)
    {
      int randomNumber =  ((int) (Math.random()*16))+48;
      if(randomNumber >= 58)
      {
        randomNumber += 7;
      }
      char nextChar = (char) randomNumber;
      lobbyID += nextChar;
    }    
    System.out.println("Generated lobbyID:" + lobbyID);
    Lobby tempLobby = new Lobby(host, lobbyID, arenaSize);
    lobbies.add(tempLobby);
    return tempLobby;
  }

  public static boolean lobbyJoinable(String lobbyID)
  {
    for(int i = 0; i < lobbies.size(); i++)
    {
      if(lobbies.get(i).getLobbyID().equals(lobbyID))
      {
        if(lobbies.get(i).getGameState() == Lobby.atLobby)
        {
          return true;
        } else {
          return false;
        }
      }
    }
    return false;
  }

  public static Lobby joinLobby(Player newPlayer, String lobbyID)
  {
    for(int i = 0; i < lobbies.size(); i++)
    {
      if(lobbies.get(i).getLobbyID().equals(lobbyID))
      {
        lobbies.get(i).addNewPlayer(newPlayer);
        return lobbies.get(i);
      }
    }
    return null;
  }

  public static void leaveLobby(Player player, Lobby lobby)
  {
    try
    {
      lobby.removePlayer(player);
      if(lobby.getNumberOfPlayers() == 0)
      {
        for(int  i = 0; 0 < lobbies.size(); i++)
        {
          if(lobbies.get(i).equals(lobby))
          {
            lobbies.remove(i);
          }
        }
      }
    } catch(Exception ex) {
      System.err.println("ERROR: Error removing " + player + " from " + lobby);
    }
  }

  public static String listLobbies()
  {
    String returnString = "";
    if(lobbies.size() == 0)
    {
      return "There are currently no lobbies.";
    }

    for(int i = 0; i < lobbies.size(); i++)
    {
      returnString += lobbies.get(i).getLobbyID() + "," 
        + lobbies.get(i).getNumberOfPlayers() + ","
        + lobbies.get(i).getGameState() + "\n";
    }
    return returnString;
  }

  public static void main(String[] args)
  {
    if(args.length != 1)
    {
      System.err.println("Usage: java CTFServer [portnumber]");
      System.exit(1);
    }

    int portNumber = 4444;
    try
    {
      portNumber = Integer.parseInt(args[0]);
    } catch(NumberFormatException ex) {
      System.err.println(ex.getMessage());
      System.exit(2);
    }


    ServerSocket serverSocket = null;

      try
      {
        // Setup a socket locally to listen and accept connections
        serverSocket = new ServerSocket(portNumber);
        System.out.println("CTFServer listening for connections.");
        System.out.println(serverSocket);
      } catch(IOException ex) {
        System.err.println(ex.getMessage());
        System.exit(3);
      }


    // Loop listening for new connections
    while(true)
    {
      Player newPlayer;
      try
      {
        // Listen for a new player to connect
        newPlayer = new Player(serverSocket.accept());
        Thread newPlayerThread = new Thread(newPlayer);
        newPlayerThread.start();
      } catch(IOException ex) {
        System.err.println(ex.getMessage());
        System.exit(3);
      }
    }
  }
}
