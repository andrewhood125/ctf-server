/**
 * Listen for requests for new lobbies and keep track of current ones.
 * @author Andrew Hood <andrewhood125@gmail.com>
 * @version 0.1
 * 
 * Copyright (c) 2014 Andrew Hood. All rights reserved.
 */

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
        
        try
        {
            loadGitInfo();
        } catch(IOException ex) {
            System.err.println("Error getting git info." + ex.getMessage());
        } catch(InterruptedException ex) {
            System.err.println("Error getting git info." + ex.getMessage());
        }
        
        System.out.println(CTFServer.commit + "\n" + CTFServer.commitMsg);

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
