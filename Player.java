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
    private boolean isWebPlayer;
    private String webID;
    private String callback;
    
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
    
    Player(Socket socket, boolean isWebPLayer)
    {
        super(35.1174,-89.9711);   // Initialize location to Memphis, TN.
        comLink = new ComLink(socket, this);
        this.setObservedBluetoothMac("");
        this.setMyBluetoothMac("");
        this.isWebPlayer = isWebPlayer;
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
        if(myFlag != null)
        {
            JsonObject jo = new JsonObject();
            jo.addProperty("ACTION", "DROP");
            jo.addProperty("FLAG", myFlag.getTeam());
            jo.addProperty("PLAYER", this.toString());
            comLink.send(jo);
            this.myFlag = null;
            CTFServer.log("INFO", this.toString() + " dropped the flag.");
        }
        
    }
    
    public String getCallback()
    {
        return callback;
    }
    
    public ComLink getComLink()
    {
        return comLink;
    }
    
    public int getLifeState()
    {
        return this.lifeState;
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
    
    public String getWebID()
    {
        return webID;
    }
    
    public boolean isAlive()
    {
        return lifeState == Player.ALIVE;
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
    
    public boolean isOutOfBounds()
    {
        Arena arena = myLobby.getArena();
        if(this.getLatitude() > arena.getNorth() || 
           this.getLatitude() < arena.getSouth() || 
           this.getLongitude() > arena.getEast() || 
           this.getLongitude() < arena.getWest())
        {
            return true;
        } else {
            return false;
        }
    }
    
    public boolean isWebPlayer()
    {
        return isWebPlayer;
    }

    public void kill()
    {
        if(this.isHoldingFlag())
        {
            myFlag.setRandomLocation(myFlag);
        }
        this.dropFlag();
        this.setLifeState(Player.DEAD);
        JsonObject jo = new JsonObject();
        CTFServer.log("INFO", this.toString() + " has been killed.");
        jo.addProperty("ACTION", "KILL");
        jo.addProperty("PLAYER", this.toString());
        comLink.send(jo);
        myLobby.broadcast(jo);
    }

    

    public void run()
    {
        JsonObject incomingCommunication = new JsonObject();
        try
        {
            while(!(incomingCommunication = comLink.readLine()).equals("QUIT"))
                comLink.parseCommunication(incomingCommunication);
        } catch(IOException ex) {
            CTFServer.log("THREAD CRITICAL", "IOException was thrown for: " + incomingCommunication.toString());
        } catch(IllegalStateException ex) {
            CTFServer.log("THREAD CRITICAL", "IllegalStateException was thrown for: " + incomingCommunication.toString());
        } catch(NullPointerException ex) {
            CTFServer.log("THREAD CRITICAL", this + " socket shutdown? NullPointerException.");
        } catch(Exception ex) {
            CTFServer.log("THREAD CRITICAL", "Exception caught from player: " + ex.getMessage());
        } 

        comLink.close();
        if(this.isInLobby())
        {
            Lobby.removePlayerFromLobby(this, myLobby);
        }
    }
    
    public void send(JsonObject obj)
    {
        comLink.send(obj);
    }
    
    public void setCallback(String callback)
    {
        this.callback = callback;
    }
    
    public void setMyBluetoothMac(String mac)
    {
        this.myBluetoothMac = mac;
    }
    
    public void setComLink(ComLink comLink)
    {
        this.comLink = comLink;
    }

    public void setFlag(Flag newFlag)
    {
        this.myFlag = newFlag;
        JsonObject jo = new JsonObject();
        jo.addProperty("ACTION", "CAPTURE"); 
        jo.addProperty("FLAG", myFlag.getTeam());
        jo.addProperty("PLAYER", this.toString());
        comLink.send(jo);
        this.getLobby().broadcast(jo);
        CTFServer.log("INFO", this.toString() + " picked up " + newFlag.getTeam() + " flag.");
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
    
    public void setWebID(String webID)
    {
        this.webID = webID;
        this.isWebPlayer = true;
    }

    public void spawn()
    {   
        this.setLifeState(Player.ALIVE);
        JsonObject jo = new JsonObject();
        jo.addProperty("ACTION", "SPAWN");
        jo.addProperty("PLAYER", this.toString());
        comLink.send(jo);
        myLobby.broadcast(jo);
        CTFServer.log("INFO", this.toString() + " spawned.");
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
        jo.addProperty("STATUS", this.getLifeState());
        return jo;
    }
}
