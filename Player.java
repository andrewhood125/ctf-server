/**
 * Store everything that a single player will need.
 * @author Andrew Hood <andrewhood125@gmail.com>
 * @version 0.2.0
 * 
 * Copyright (c) 2014 Andrew Hood. All rights reserved.
 */


import java.io.IOException;
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
    private ComLink comLink;
    private Flag myFlag;
    private String myBluetoothMac, observedBluetoothMac;
    private String username;
    private Lobby myLobby;
    private int team;
    private int lifeState;

    /**
     * Constructors
     */
    Player(Socket socket)
    {
        super(35.1174,-89.9711);   // Initialize location to Memphis, TN.
        comLink = new ComLink(socket, this);
        this.setObservedBluetoothMac("");
        this.setMyBluetoothMac("");
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
            myLobby.broadcast("SAVED", new String[] {"flag", myFlag.toString(),
                                                     "player", this.toString()});
            this.dropFlag();
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
        comLink.send("DROP", new String[] {"flag", myFlag.toString(),
                                           "player", this.toString()});
        this.myFlag = null;
    }
    
    public Flag getFlag()
    {
        return myFlag;
    }
    
    public Lobby getLobby()
    {
        return myLobby;
    }
    
    public String getMyBluetoothMac()
    {
        return this.myBluetoothMac;
    }
    
    public String getObservedBluetoothMac()
    {
        return this.observedBluetoothMac;
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
    
    public boolean isInitialized()
    {
        if(this.myBluetoothMac.equals(""))
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

    public void kill()
    {
        this.setLifeState(Player.DEAD);
        comLink.send("KILL", new String[] {"player", this.toString()});
    }

    

    public void run()
    {
        comLink.send("LOG", new String[] {"info", this.toString() + "'s thread was started."});

        try
        {
            String incomingCommunication;
            while(!(incomingCommunication = comLink.readLine()).equals("QUIT"))
                comLink.processCommand(incomingCommunication.toUpperCase());
        } catch(IOException ex) {
            comLink.send("LOG", new String[] {"ERROR", "IOException caught in Player.run(). This is what we know: " + ex.getMessage()});
        } /*catch(NullPointerException ex) {
            this.notifyError(this + " socket shutdown? NullPointerException.");
        } */

        comLink.send("LOG", new String[] {"INFO", this + " shutting down"});
        
        try
        {
            comLink.close();
            if(this.isInLobby())
            {
                Lobby.removePlayerFromLobby(this, myLobby);
            }
        } catch(IOException ex) {
            System.err.println(ex.getMessage());
        }
    }
    
    public void send(String object, String[] payload)
    {
        comLink.send(object, payload);
    }
    
    public void setMyBluetoothMac(String mac)
    {
        this.myBluetoothMac = mac;
    }

    public void setFlag(Flag newFlag)
    {
        this.myFlag = newFlag;
        comLink.send("CAPTURE", new String[] {"flag", myFlag.toString(),
                                              "player", this.toString()});    
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
    
    public void setLobby(Lobby lobby)
    {
        this.myLobby = lobby;
    }

    public void setObservedBluetoothMac(String mac)
    {
        this.observedBluetoothMac = mac;
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
    
    public void setUsername(String username)
    {
        this.username = username;
    }

    public void spawn()
    {   
        this.setLifeState(Player.ALIVE);
        comLink.send("SPAWN", new String[] {"player", this.toString()});
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
