/**
 * Listen for requests for new lobbies and keep track of current ones.
 * @author Andrew Hood <andrewhood125@gmail.com>
 * @version 0.1
 * 
 * Copyright (c) 2014 Andrew Hood. All rights reserved.
 */

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class CTFServer
{ 
    public static String commit = "Not Set";
    public static String commitMsg = "Not Set";
    public static ArrayList<Player> webPlayers = new ArrayList<Player>();
    
    public static void handleWebPlayer(Player player, JsonObject jo)
    {
        CTFServer.log("INFO", "Handling: " + jo.toString());
        JsonElement action = jo.get("ACTION");
        // If greeting then create a new player and add them to the webPlayerList
        if(action.getAsString().equals("HELLO"))
        {
            player.getComLink().parseCommunication(jo);
            JsonElement webID = jo.get("WEB_ID");
            player.setWebID(webID.getAsString());
            webPlayers.add(player);
        } else {
            for(int i = 0; i < webPlayers.size(); i++)
            {
                JsonElement webID = jo.get("WEB_ID");
                if(webPlayers.get(i).getWebID().equals(webID.getAsString()))
                {
                    webPlayers.get(i).setComLink(player.getComLink());
                    webPlayers.get(i).getComLink().setPlayer(webPlayers.get(i));
                    webPlayers.get(i).getComLink().parseCommunication(jo);
                }
            }
            // Otherwise find the player in the list and process their command.
        }
        
    }
    
    public static void loadGitInfo() throws IOException, InterruptedException
    {
        Runtime r = Runtime.getRuntime();
        Process p = r.exec("git rev-parse HEAD");
        p.waitFor();
        BufferedReader b = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String temp = "";
        String line = "";
        while ((line = b.readLine()) != null) {
          temp += line;
        }
        
        CTFServer.commit = temp;
        p = r.exec("git --no-pager log --format=%B -n 1");
        b = new BufferedReader(new InputStreamReader(p.getInputStream()));
        temp = "";
        line = "";
        while ((line = b.readLine()) != null) {
          temp += line;
        }
        CTFServer.commitMsg = temp;
    }
    
    public static void log(String tag, String message)
    {
        System.out.println("[" + tag + "]: " + message + "<br>");
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
            CTFServer.log("CRITICAL", ex.getMessage());
            System.exit(2);
        }
        
        try
        {
            loadGitInfo();
        } catch(IOException ex) {
            CTFServer.log("ERROR", "Error getting git info." + ex.getMessage());
        } catch(InterruptedException ex) {
            CTFServer.log("ERROR", "Error getting git info." + ex.getMessage());
        }
        
        ServerSocket serverSocket = null;
        try
        {
            // Setup a socket locally to listen and accept connections
            serverSocket = new ServerSocket(portNumber);
            CTFServer.log("INFO", "CTFServer listening for connections.");
            CTFServer.log("INFO", serverSocket.toString());
        } catch(IOException ex) {
            CTFServer.log("CRITICAL", ex.getMessage());
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
                CTFServer.log("CRITICAL", ex.getMessage());
                System.exit(3);
            }
        }
    }
}
