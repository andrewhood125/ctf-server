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

  public static Lobby createLobby(Player host, String lobbyID, double arenaSize)
  {
    Lobby tempLobby = new Lobby(host, lobbyID, arenaSize);
    lobbies.add(tempLobby);
    return tempLobby;
  }

  public static boolean lobbyExists(String lobbyID)
  {
    for(int i = 0; i < lobbies.size(); i++)
    {
      if(lobbies.get(i).getLobbyID().equals(lobbyID))
      {
        return true;
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
