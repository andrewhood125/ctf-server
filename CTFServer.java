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
    /**
     * Instance variables
     */
    // This should be in Lobby
    public static ArrayList<Lobby> lobbies = new ArrayList<Lobby>();

    // The lobby constructor should be used instead of this and whatever that 
    // returns should be aded to the lobbies list right their or in a method addLobby
    public static Lobby createLobby(Player host, double arenaSize)
    {
        String lobbyID = Lobby.generateLobbyID();
        System.out.println("Generated lobbyID:" + lobbyID);
        Lobby tempLobby = new Lobby(host, lobbyID, arenaSize);
        lobbies.add(tempLobby);
        return tempLobby;
    }

    // Should be in Lobby
    public static Lobby joinLobby(Player newPlayer, String lobbyID)
    {
        for(int i = 0; i < lobbies.size(); i++)
        {
            if(lobbies.get(i).getLobbyID().equals(lobbyID))
            {
                lobbies.get(i).addNewPlayer(newPlayer);
                lobbies.get(i).broadcastPlayers();
                return lobbies.get(i);
            }
        }
        return null;
    } 

    // In lobby
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

    // in lobby
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

    // in lobby
    public static boolean lobbyJoinable(String lobbyID)
    {
        for(int i = 0; i < lobbies.size(); i++)
        {
            if(lobbies.get(i).getLobbyID().equals(lobbyID))
            {
                if(lobbies.get(i).getGameState() == Lobby.AT_LOBBY)
                {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
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
