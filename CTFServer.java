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

    // Hold the running lobbies
    ArrayList<Lobby> lobbies = new ArrayList<Lobby>();

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
