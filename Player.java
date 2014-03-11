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
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


class Player extends Locate implements Runnable
{
  public static final String MACPAT = "^([0-9A-F]{2}[:-]){5}([0-9A-F]{2})$";
  public static final int DEAD = 0;
  public static final int ALIVE = 1;

 

  int id;
  PrintWriter out;
  BufferedReader in;
  String btMAC;
  String username;
  Socket socket;
  boolean greeted, inLobby;
  Lobby myLobby;
  int team;
  int lifeState;

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

  public double getLatitude()
  {
    return latitude;
  }

  public double getLongitude()
  {
    return longitude;
  }

  public String getUsername()
  {
    return username;
  }

  public int getTeam(){
	  return team;
  }
  
  public void setTeam(int team)
  {
    if(team >= 0 && team <= 2)
    {
      this.team = team;
    }
  }
  
  public void setLifeState(int lifeState)
  {
    if(lifeState >= 0  && lifeState <= 1)
    {
      this.lifeState = lifeState;
    } else {
      this.lifeState = Player.ALIVE;
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
    	  processCommand(incomingCommunication.toUpperCase());
    } catch(IOException ex) {
      System.err.println(ex.getMessage());
      System.exit(5);
    } catch (Exception ex) {
      System.err.println("Shutting down " + this);
    }

    try
    {
      out.close();
      in.close();
      socket.close();
      if(inLobby)
      {
        CTFServer.leaveLobby(this, myLobby);
      }
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
      tempBtMac.toUpperCase();
      if(tempBtMac.matches(MACPAT))
      {
    	  System.out.println(this.toString() + " BT MAC: " + tempBtMac);
          btMAC = tempBtMac;
      }else
      {
    	  readBluetoothMAC();
      }
      
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

  public void readLocation()
  {
    try
    {
      String location = in.readLine();
      System.out.println(this.toString() + " location: " + location);
      String[] coordinates = location.split(",");
      if(coordinates.length != 2)
      {
        out.println("ERROR: GPS improperly formatted.");
        readLocation();
      } else {
        try 
        {
          latitude = Double.parseDouble(coordinates[0]);
          longitude = Double.parseDouble(coordinates[1]);
          if(latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180)
          {
            out.println("ERROR: GPS improperly formatted.");
            readLocation();
          }
        } catch(NumberFormatException ex) {
          System.err.println(ex.getMessage());
          System.exit(20);
        }
      }
    } catch(Exception ex) {
      System.err.println(ex.getMessage());
      out.println("ERROR: GPS improperly formatted.");
    }
  }
  
  public void send(String message)
  {
    out.println(message);
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
          out.println("Proceed with username.");
          readUsername();
          out.println("Proceed with location.");
          readLocation();
          out.println("Welcome " + username + ".");
        } else {
          out.println("..hi.");
        }
        break;

      case "CREATE": 
        if(!greeted)
        {
          out.println("ERROR: Need to greet first.");
        } else if(!inLobby) {
          double newLobbySize = 0;
          try 
          {
            out.println("Proceed with arena size.");
            newLobbySize  = Double.parseDouble(in.readLine());
          } catch(NumberFormatException ex) {
            System.err.println(ex.getMessage());
            processCommand("CREATE");
          } catch(IOException ex) {
            System.err.println(ex.getMessage());
            System.exit(25);
          }
          // Create a lobby with this player as the host
          myLobby = CTFServer.createLobby(this, newLobbySize);
          inLobby = true;
          out.println("You're now in lobby " + myLobby.getLobbyID());
        } else if(inLobby) {
          out.println("You are already in a lobby.");
        } else {
          out.println("ERROR: Something went wrong but I don't know what.");
          System.exit(17);
        }
        break;

      case "START":
        if(!greeted)
        {
          out.println("ERROR: Need to greet first.");
        } else if(!inLobby) {
          out.println("ERROR: Need to be in lobby.");
        } else if(!myLobby.isLobbyLeader(this)) {
          out.println("ERROR: Only the lobby leader can start the game.");
        } else {
          myLobby.start();
        }
        break;
      case "JOIN":
        if(!greeted)
        {
          out.println("ERROR: Need to greet first.");
        } else if(!inLobby) {
          if(CTFServer.lobbies.size() == 0)
          {
            out.println("There are currently no lobbies.");
          } else { 
           try
           {
             String lobbyID;
             out.println("Proceed with lobby ID.");
             if(CTFServer.lobbyJoinable(lobbyID = in.readLine()))
             {
               myLobby =  CTFServer.joinLobby(this, lobbyID);
               inLobby = true;
               out.println("Joining lobby " + lobbyID + "...");
               out.println("Arena Boundaries: " + myLobby.getSize());
             } else {
               out.println("ERROR: Lobby not found.");
             }
           } catch(IOException ex) {
             System.err.println(ex.getMessage());
             System.exit(7);
           }
         }
       } else if (inLobby){
         out.println("ERROR: You are already in a lobby.");
       } else {
         out.println("ERROR: Something went wrong but I don't know what.");
         System.exit(18);
       }
       break;

      case "LOBBY": 
        if(!greeted)
        {
          out.println("ERROR: Need to greet first.");
        } else if(!inLobby) {
          // List all lobbies
          out.println(CTFServer.listLobbies());
        } else if(inLobby) {
          out.println(myLobby.toString());
        }
        break;

      case "LEAVE":
        if(!greeted)
        {
          out.println("ERROR: Need to greet first.");
        } else if (!inLobby) {
          out.println("ERROR: You're not in a lobby.");
        } else if(inLobby) {
          CTFServer.leaveLobby(this, myLobby);
          inLobby = false;
          out.println("You've left the lobby.");
        } else {
          out.println("ERROR: Something went wrong but I don't know what.");
        }
        break;

      default: out.println("Command not understood.");
    }
  }
}
