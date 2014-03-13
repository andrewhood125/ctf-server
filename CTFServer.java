/**
 * Listen for requests for new lobbies and keep track of current ones.
 * @author Andrew Hood <andrewhood125@gmail.com>
 * @version 0.1
 * 
 * Copyright (c) 2014 Andrew Hood. All rights reserved.
 */

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class CTFServer
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
