/**
 * Author: Andrew Hood <andrewhood125@gmail.com>
 * Description: Store everything that a single player will need.
 * Copyright (c) 2014 Andrew Hood. All rights reserved.
 */
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;


class Player extends Locate implements Runnable
{
  int id;
  PrintWriter out;
  BufferedReader in;
  String btMAC;
  String username;
  Socket socket;
  boolean greeted, inLobby;

  Player(Socket socket)
  {
    this.socket = socket;
    try
    {
      out = new PrintWriter(socket.getOutputStream(), true);
      in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      System.out.println("New player connected from IP: " + socket.getInetAddress());
      out.println("New player connected from IP: " + socket.getInetAddress());
    } catch(IOException ex) {
      System.err.println(ex.getMessage());
      System.exit(4);
    }
  }

  public void run()
  {
    System.out.println(this.toString() + "'s thread was started.");
    out.println(this.toString() + "'s thread was started.");

    try
    {
      String incomingCommunication;
      while(!(incomingCommunication = in.readLine()).equals("QUIT"))
        processCommand(incomingCommunication);
    } catch(IOException ex) {
      System.err.println(ex.getMessage());
      System.exit(5);
    } catch(Exception ex) {
      System.err.println(ex.getMessage());
      System.exit(6);
    }

    try
    {
      out.close();
      in.close();
      socket.close();
    } catch(IOException ex) {
      System.err.println(ex.getMessage());
      System.exit(6);
    }
  }

  private void readBluetoothMAC()
  {
    try 
    {
      String tempBtMac = in.readLine();
      System.out.println(this.toString() + " BT MAC: " + tempBtMac);
      btMAC = tempBtMac;
    } catch(Exception ex) {
      System.err.println(ex.getMessage());
      System.exit(10);
    }
  }

  private void readUsername()
  {
    try 
    {
      String tempUsername = in.readLine();
      System.out.println(this.toString() + " username: " + tempUsername);
      username = tempUsername;
    } catch(Exception ex) {
      System.err.println(ex.getMessage());
      System.exit(11);
    }
  } 

  private void processCommand(String com)
  {
    switch(com)
    {
      case "HELLO":
        if(!greeted)
        {
          greeted = true;
          out.println("Proceed with blutooth MAC.");
          readBluetoothMAC();
          out.println("Proceed with username");
          readUsername();
        } else {
          out.println("..hi.");
        }
        break;
      case "CREATE": 
        if(greeted && !inLobby)
        {
          CTFServer.createLobby(this);
          inLobby = true;
          out.println("Establishing a lobby.");
        } else {
          out.println("You've not introduced yourself or are already in a lobby.");
        }
        break;
      case "JOIN":
       if(greeted && !inLobby)
       {
         try
         {
           inLobby = true;
           String lobbyID = in.readLine();
           out.println("Joining lobby " + lobbyID + "...");
         } catch(IOException ex) {
           System.err.println(ex.getMessage());
           System.exit(7);
         }
       } else {
         out.println("You've not introduced yourself or are already in a lobby.");
       } 
       break;
      default: out.println("Command not understood.");
    }
  }
}
