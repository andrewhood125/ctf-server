/**
 * Store everything that a single player will need.
 * @author Andrew Hood <andrewhood125@gmail.com>
 * @version 0.1
 * 
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

public class Player extends Point implements Runnable
{
    /**
     * Constants
     */
    public static final String MACPAT = "^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$";
    public static final int DEAD = 0;
    public static final int ALIVE = 1;

    /** 
     * Instance variables
     */
    private PrintWriter out;
    private BufferedReader in;
    private Flag myFlag;
    private String btMAC, otherMac;
    private String username;
    private Socket socket;
    private boolean greeted;
    private Lobby myLobby;
    private int team;
    private int lifeState;

    /**
     * Constructors
     */
    Player(Socket socket)
    {
        super(35.1174,-89.9711);   // Initialize location to Memphis, TN.
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
        this.setOtherMac("");
    }
    
    public void checkIfPickedUpFlag()
    {
        if(this.getTeam() == Lobby.RED_TEAM && myLobby.isFlagDropped(Lobby.BLUE_TEAM))
        {
            if(this.isWithinArea(myLobby.getFlag(Lobby.BLUE_TEAM)))
            {
                // player picks up blue flag
                this.setFlag(myLobby.getFlag(Lobby.BLUE_TEAM));
            }
        } else if(this.getTeam() == Lobby.BLUE_TEAM && myLobby.isFlagDropped(Lobby.RED_TEAM)) {
            if(this.isWithinArea(myLobby.getFlag(Lobby.RED_TEAM)))
            {
                this.setFlag(myLobby.getFlag(Lobby.RED_TEAM));
            }
        }
    }

    public void checkIfReturnedToBase()
    {
        if(this.getTeam() == Lobby.RED_TEAM)
        {
            if(this.isWithinArea(myLobby.getBase(Lobby.RED_TEAM)))
            {
                this.spawn();
            }
        } else if(this.getTeam() == Lobby.BLUE_TEAM) {
            if(this.isWithinArea(myLobby.getBase(Lobby.BLUE_TEAM)))
            {
                this.spawn();
            }
        }
        
        //I returned my flag to base
        if(this.isHoldingFlag(this.getTeam()) && this.isWithinArea(myLobby.getBase(this.getTeam())))
        {
            this.dropFlag();
            myLobby.broadcast(this + " saved the flag");
        }
    }

    public void checkIfScored()
    {
        if(this.getTeam() == Lobby.RED_TEAM)
        {
            // Check if at blue base
            if(this.myFlag.getTeam() == Lobby.BLUE_TEAM && this.isWithinArea(myLobby.getBase(Lobby.RED_TEAM)))
            {
                // player has scored increment player teams score
                // return flag back to base
                // send all players new flag coordinates
                myLobby.scored(this);
            }
        } else if(this.getTeam() == Lobby.BLUE_TEAM) {
            if(this.myFlag.getTeam() == Lobby.RED_TEAM && this.isWithinArea(myLobby.getBase(Lobby.BLUE_TEAM)))
            {
                myLobby.scored(this);
            }
        }
    }
    
    public void dropFlag()
    {
        if(myFlag.getTeam() == this.getTeam())
        {
            this.send("You dropped your flag.");
        } else {
            this.send("You dropped your opponents flag.");
        }
        this.myFlag = null;
    }
    
    public Flag getFlag()
    {
        return myFlag;
    }
    
    public String getMac()
    {
        return this.btMAC;
    }
    
    public String getOtherMac()
    {
        return this.otherMac;
    }

    public int getTeam(){
        return team;
    }
    
    public String getTeamString()
    {
        if(team == Lobby.BLUE_TEAM)
        {
            return "Blue";
        } else {
            return "Red";
        } 
    }

    public String getUsername()
    {
        return username;
    }

    public boolean isDead()
    {
        return lifeState == Player.DEAD;
    }

    public boolean isHoldingFlag(int team)
    {
        if(this.isHoldingFlag() && this.myFlag.getTeam() == team)
        {
            return true;
        } else {
            return false;
        }
    }
    
    public boolean isHoldingFlag()
    {
        if(myFlag == null)
        {
            return false;
        } else {
            return true;
        }
    }
    
    public boolean isInLobby()
    {
        if(myLobby == null)
        {
            return false;
        } else {
            return true;
        }
    }
    
    private void notify(String message)
    {
        System.out.println(message);
        this.out.println(message);
    }
    
    private void notifyError(String message)
    {
        System.err.println(message);
        this.out.println(message);
    }

    public void kill()
    {
        this.setLifeState(Player.DEAD);
        System.out.println(this + " killed.");
        this.send("You have been killed.");
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
                out.println("Welcome " + username + ".");
            } else {
                out.println("..hi.");
            }
            break;

            case "CREATE": 
            if(!greeted)
            {
                out.println("ERROR: Need to greet first.");
            } else if(!this.isInLobby()) {
            	out.println("Proceed with location.");
                readLocation();
                double newLobbySize = 0;
                try 
                {
                    out.println("Proceed with arena size.");
                    newLobbySize  = Double.parseDouble(in.readLine());
                    myLobby = new Lobby(this, newLobbySize);
                } catch(NumberFormatException ex) {
                    System.err.println(ex.getMessage());
                    processCommand("CREATE");
                } catch(IOException ex) {
                    System.err.println("IOException while trying to create a new lobby: " + ex.getMessage());
                    System.exit(25);
                }
                out.println("You're now in lobby " + myLobby.getLobbyID());
            } else if(this.isInLobby()) {
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
            } else if(!isInLobby()) {
                out.println("ERROR: Need to be in lobby.");
            } else if(!myLobby.isLobbyLeader(this)) {
                out.println("ERROR: Only the lobby leader can start the game.");
            } else {
                myLobby.startGame();
            }
            break;
            case "GPS":
            // GPS is used to accept location updates from clients
            if(!greeted)
            {
                out.println("ERROR: Need to greet first.");
            } else if(!isInLobby()) {
                out.println("ERROR: Need to be in lobby.");
            } else if(myLobby.getGameState()!= Lobby.IN_PROGRESS) {
                out.println("ERROR: The game must be in progress.");
            } else {
                readLocation();
                myLobby.playerUpdate(this);
            }
            break;
            case "JOIN":
            if(!greeted)
            {
                out.println("ERROR: Need to greet first.");
            } else if(!isInLobby()) {
            	out.println("Proceed with location.");
                readLocation();
                if(Lobby.lobbies.size() == 0)
                {
                    out.println("There are currently no lobbies.");
                } else { 
                    try
                    {
                        String lobbyID;
                        out.println("Proceed with lobby ID.");
                        if(Lobby.isJoinable(lobbyID = in.readLine()))
                        {
                            myLobby =  Lobby.addPlayerToLobby(this, lobbyID);
                            out.println("Joining lobby " + lobbyID + "...");
                        } else {
                            out.println("ERROR: Lobby not found.");
                        }
                    } catch(IOException ex) {
                        System.err.println(ex.getMessage());
                        System.exit(7);
                    }
                }
            } else if (this.isInLobby()){
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
            } else if(!this.isInLobby()) {
                // List all lobbies
                out.println(Lobby.listLobbies());
            } else if(this.isInLobby()) {
                out.println(myLobby.toString());
            }
            break;

            case "LEAVE":
            if(!greeted)
            {
                out.println("ERROR: Need to greet first.");
            } else if (!this.isInLobby()) {
                out.println("ERROR: You're not in a lobby.");
            } else if(this.isInLobby()) {
                Lobby.removePlayerFromLobby(this, myLobby);
                out.println("You've left the lobby.");
            } else {
                out.println("ERROR: Something went wrong but I don't know what.");
            }
            break;
            
            case "DROP":
            if(!greeted)
            {
                out.println("ERROR: Need to greet first.");
            } else if (!this.isInLobby()) {
                out.println("ERROR: You're not in a lobby.");
            } else if(this.isInLobby()) {
                try
                {
                    this.otherMac = in.readLine();
                } catch(IOException ex) {
                    System.err.println(ex.getMessage());
                }
                myLobby.playerUpdate(this);
            } else {
                out.println("ERROR: Something went wrong but I don't know what.");
            } 

            default: out.println("Command not understood.");
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
    
    private void readLocation()
    {
        String location = "";
        try 
        {
            location = in.readLine();
            this.setPoint(location);
        } catch(IOException ex) {
            this.notifyError(ex.getMessage());
            readLocation();
        } catch(PointException ex) {
            this.notifyError(ex.getMessage());
            readLocation();
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
            this.notifyError(ex.getMessage());
        } /*catch(NullPointerException ex) {
            this.notifyError(this + " socket shutdown? NullPointerException.");
        } */

        this.notifyError(this + " shutting down.");
        
        try
        {
            out.close();
            in.close();
            socket.close();
            if(this.isInLobby())
            {
                Lobby.removePlayerFromLobby(this, myLobby);
            }
        } catch(IOException ex) {
            System.err.println(ex.getMessage());
            System.exit(6);
        }
    }

    public void send(String message)
    {
        this.out.println(message);
    }
    
    public void setFlag(Flag newFlag)
    {
        if(newFlag.getTeam() == this.getTeam())
        {
            this.send("You retrieved your flag.");
        } else {
            this.send("You captured your opponents flag.");
        }
        this.myFlag = newFlag;
    }

    public void setLifeState(int lifeState)
    {
        if(lifeState >= 0  && lifeState <= 1)
        {
            this.lifeState = lifeState;
        } else {
            System.err.println(this + " attempted to set its lifestate to a invalid number");
        }
    }

    public void setOtherMac(String mac)
    {
        this.otherMac = mac;
    }
    
    public void setTeam(int team)
    {
        if(team >= 0 && team <= 2)
        {
            this.team = team;
        } else {
            System.err.println(this + " attempted to set its team to a invalid number");
        }
    }

    public void spawn()
    {
        if(this.isDead())
        {
        	this.setLifeState(Player.ALIVE);
            System.out.println(this + " has spawned.");
            this.send("You have now spawned.");
        }
    	
    }
    
    public String toString()
    {
        if(this.username == null)
        {
            return this.getClass().getName() + "@" + Integer.toHexString(this.hashCode());
        } else {
            return this.username;
        }
        
    }
}
