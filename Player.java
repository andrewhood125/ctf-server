/**
 * Store everything that a single player will need.
 * @author Andrew Hood <andrewhood125@gmail.com>
 * @version 0.2.0
 * 
 * Copyright (c) 2014 Andrew Hood. All rights reserved.
 */

import com.google.gson.JsonObject;
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
            JsonObject jo = new JsonObject();
            jo.addProperty("ACTION", "SAVED");
            jo.addProperty("FLAG", myFlag.toString());
            jo.addProperty("PLAYER", this.toString());
            myLobby.broadcast(jo);
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
        JsonObject jo = new JsonObject();
        jo.addProperty("ACTION", "DROP");
        jo.addProperty("FLAG", myFlag.toString());
        jo.addProperty("PLAYER", this.toString());
        comLink.send(jo);
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
        JsonObject jo = new JsonObject();
        jo.addProperty("ACTION", "KILL");
        jo.addProperty("PLAYER", this.toString());
        comLink.send(jo);
    }

    

    public void run()
    {
        try
        {
            JsonObject incomingCommunication;
            while(!(incomingCommunication = comLink.readLine()).equals("QUIT"))
                comLink.parseCommunication(incomingCommunication);
        } catch(IOException ex) {
            JsonObject job = new JsonObject();
            job.addProperty("ACTION", "LOG");
            job.addProperty("LEVEL", "ERROR");
            job.addProperty("PAYLOAD", "IOException caught in Player.run(). This is what we know: " + ex.getMessage());
            comLink.send(job);
        } catch(IllegalStateException ex) {
            System.err.println("IllegalState " + ex.getMessage());
        }/*catch(NullPointerException ex) {
            this.notifyError(this + " socket shutdown? NullPointerException.");
        } */

        JsonObject jobj = new JsonObject();
        jobj.addProperty("ACTION", "LOG");
        jobj.addProperty("LEVEL", "INFO");
        jobj.addProperty("PAYLOAD", this.toString() + " shutting down");
        comLink.send(jobj);
        
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
    
    public void send(JsonObject obj)
    {
        comLink.send(obj);
    }
    
    public void setMyBluetoothMac(String mac)
    {
        this.myBluetoothMac = mac;
    }

    public void setFlag(Flag newFlag)
    {
        this.myFlag = newFlag;
        JsonObject jo = new JsonObject();
        jo.addProperty("ACTION", "CAPTURE");
        jo.addProperty("FLAG", myFlag.toString());
        jo.addProperty("PLAYER", this.toString());
        comLink.send(jo);    
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
        JsonObject jo = new JsonObject();
        jo.addProperty("ACTION", "SPAWN");
        jo.addProperty("PLAYER", this.toString());
        comLink.send(jo);
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
    
    public JsonObject toJson()
    {
        JsonObject jo = new JsonObject();
        jo.addProperty("USERNAME", this.getUsername());
        jo.addProperty("BLUETOOTH", this.getMyBluetoothMac());
        jo.addProperty("LOCATION", this.getLocation());
        jo.addProperty("TEAM", this.getTeam());
        return jo;
    }
}
