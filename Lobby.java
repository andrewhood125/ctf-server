/**
 * Store everything that a single lobby will need.
 * @author Andrew Hood <andrewhood125@gmail.com>
 * @version 0.1
 * 
 * Copyright (c) 2014 Andrew Hood. All rights reserved.
 */

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Lobby
{
    /**
     * Constants
     */
    public static final int AT_LOBBY = 0;
    public static final int IN_PROGRESS = 1;
    public static final int WAITING = 2;
    public static final int SPECTATOR = 0;
    public static final int RED_TEAM = 1;
    public static final int BLUE_TEAM = 2;
    public static final int MAX_SCORE = 5;

    /**
     * Static variables
     */
    public static ArrayList<Lobby> lobbies = new ArrayList<Lobby>();

    /**
     * Instance variables
     */
    private Arena arena;
    private ArrayList<Player> players;
    private Base blueBase, redBase;
    private Flag blueFlag, redFlag;
    private int blueScore, duration, gameState, redScore;
    private String lobbyID;
    private long endTime;
    private Point epicenter;
    /**
     * Constructors
     */
    Lobby(Player host, double arenaSize)
    {
        this(host, arenaSize, 0.005);
    }
    
    Lobby(Player host, double arenaSize, double arenaAccuracy)
    {
        this.players = new ArrayList<Player>();
        this.addNewPlayer(host);
        this.lobbyID = generateLobbyID();
        // Create arena based on arenaSize and Players gps coordinates.
        this.arena = new Arena(host.getLatitude(), host.getLongitude(), arenaSize);
        this.duration = 30;
        this.epicenter = host.getPoint();
        this.redFlag = new Flag(Lobby.RED_TEAM, 0, 0, arenaAccuracy, this.arena);
        this.blueFlag = new Flag(Lobby.BLUE_TEAM, 0, 0, arenaAccuracy, this.arena);
        this.redBase = new Base(Lobby.RED_TEAM, 0, 0, arenaAccuracy, this.arena);
        this.blueBase = new Base(Lobby.BLUE_TEAM, 0, 0, arenaAccuracy, this.arena);
        this.setGameState(Lobby.AT_LOBBY);
        //this.size = arenaSize;
        // Add this new Lobby to the lobbies list
        Lobby.lobbies.add(this);
    }

    public static Lobby addPlayerToLobby(Player newPlayer, String lobbyID)
    {
        for(int i = 0; i < lobbies.size(); i++)
        {
            if(lobbies.get(i).getLobbyID().equals(lobbyID))
            {
                CTFServer.log("INFO", newPlayer.getUsername() + " added to lobby " + lobbies.get(i).getLobbyID());
                lobbies.get(i).addNewPlayer(newPlayer);
                return lobbies.get(i);
            }
        }
        return null;
    } 

    public void addNewPlayer(Player newPlayer)
    {
        int bluePlayers = this.getNumberOfPlayers(Lobby.BLUE_TEAM);
        int redPlayers = this.getNumberOfPlayers(Lobby.RED_TEAM);
        if(bluePlayers < redPlayers) 
        {
            newPlayer.setTeam(Lobby.BLUE_TEAM);  
        } else {
            newPlayer.setTeam(Lobby.RED_TEAM);
        }
        
        JsonObject jo = new JsonObject();
        jo.addProperty("ACTION", "JOINED");
        jo.addProperty("LOCATION", newPlayer.getLocation());
        jo.addProperty("PLAYER", newPlayer.getUsername());
        jo.addProperty("TEAM", newPlayer.getTeam());
        jo.addProperty("BLUETOOTH", newPlayer.getMyBluetoothMac());
        broadcast(jo);
        CTFServer.log("INFO", newPlayer.getUsername() + " added to lobby " + this.getLobbyID());
        players.add(newPlayer);
    }

    public void broadcast(JsonObject obj)
    {
        for(int i = 0; i < players.size(); i++)
        {
            players.get(i).send(obj);
        }
    }
    
    public static void dumpLobbies()
    {
        PrintWriter lobbiesFile = null;
        JsonArray ja = new JsonArray();
        try
        {
            lobbiesFile = new PrintWriter("lobbies.json");
        } catch(FileNotFoundException ex) {
            CTFServer.log("ERROR", ex.getMessage());
        }
        
        // dump the lobbies to a file
        for(int i = 0; i < lobbies.size(); i++)
        {
            lobbies.get(i).dumpLobby();
            ja.add(lobbies.get(i).toJson());
        }
        lobbiesFile.println(ja.toString());
        lobbiesFile.close();
        CTFServer.log("INFO", "Regenerating lobbies.json");
    }
    
    public void dumpLobby()
    {
        PrintWriter lobby = null;
        JsonArray ja = new JsonArray();
        try
        {
            lobby = new PrintWriter("lobbies/" + this.getLobbyID() + ".json");
        } catch(FileNotFoundException ex) {
            CTFServer.log("ERROR", ex.getMessage());
        }   
        
        ja.add(this.toJson());
        
        JsonArray flags = new JsonArray();
        flags.add(redFlag.toJson());
        flags.add(blueFlag.toJson());
        JsonObject flag = new JsonObject();
        flag.addProperty("ACTION","FLAG");
        flag.add("FLAGS",flags);     
        ja.add(flag);
        
        
        JsonArray bases = new JsonArray();
        bases.add(redBase.toJson());
        bases.add(blueBase.toJson());
        JsonObject base = new JsonObject();
        base.addProperty("ACTION","BASE");
        base.add("BASES",bases);     
        ja.add(base);
        
        lobby.println(ja.toString());
        
        lobby.close();
    }
    
    public void endGame(String message)
    {
        // Wrap up the game.
        // Kill all the players
        this.killAllPlayers();
        // Set the lobby status to at lobby
        this.setGameState(Lobby.AT_LOBBY);
        // Broadcast what ended the game
        JsonObject jo = new JsonObject();
        jo.addProperty("ACTION", "STOP");
        jo.addProperty("INFO", message);
        this.broadcast(jo);
    }
    
    public boolean isFlagDropped(int team)
    {
        for(int i = 0; i < players.size(); i++)
        {
            Player tempPlayer = players.get(i);
            Flag tempFlag = tempPlayer.getFlag();
            if(tempFlag != null && tempFlag.getTeam() == team)
            {
                return true;
            }
        }
        
        return true;
    }
    
    public Player findPlayerByMAC(String mac)
    {
        for(int i = 0; i < players.size(); i++)
        {
            if(players.get(i).getMyBluetoothMac().equals(mac))
            {
                return players.get(i);
            }
        }
        
        return null;
    }

    public static String generateLobbyID()
    {
        String lobbyID = "";
        for(int i = 0; i < 5; i++)
        {
            int randomNumber =  ((int) (Math.random()*16))+48;
            if(randomNumber >= 58)
            {
                randomNumber += 7;
            }
            char nextChar = (char) randomNumber;
            lobbyID += nextChar;
        }  
        return lobbyID;
    }

    public Arena getArena()
    {
        return arena;
    }
    
    public Base getBase(int team)
    {
        if(team == Lobby.BLUE_TEAM)
        {
            return blueBase;
        } else {
            return redBase;
        }
    }
    
    public Flag getFlag(int team)
    {
        if(team == Lobby.BLUE_TEAM)
        {
            return blueFlag;
        } else {
            return redFlag;
        }
    }
    
    public String getFlagHolder(int team)
    {
        for(int i = 0; i < players.size(); i++)
        {
            Player tempPlayer = players.get(i);
            Flag tempFlag = tempPlayer.getFlag();
            if(tempFlag != null && tempFlag.getTeam() == team)
            {
                return tempPlayer.toString();
            }
        }
        
        return "null";
    }

    public int getGameState()
    {
        return gameState;
    }

    public String getGameStateString()
    {
        if (gameState == 0)
        {
            return "AT_LOBBY";
        }else if (gameState == 1)
        {
            return "IN_PROGRESS";
        }else
        {
            return null;
        }
    }

    public String getLobbyID()
    {
        return lobbyID;
    }

    public int getNumberOfPlayers()
    {
        return players.size();
    }
    
    public int getNumberOfPlayers(int team)
    {
        int count = 0;
        for(int i = 0; i < players.size(); i++)
        {
            if(players.get(i).getTeam() == team)
            {
                count++;
            }
        }
        return count;
    }
    
    public String getTeamPlayers(int team)
    {
        String returnString = "";
        for(int i = 0; i < this.players.size(); i++)
        {
            if(this.players.get(i).getTeam() == team)
            {
                returnString += players.get(i).toString() + ",";
            }
        }
        if(returnString.length() > 1)
        {
            return returnString.substring(0,returnString.length()-1);
        } else {
            return returnString;
        }
    }
    
    public void incrementScore(int team)
    {
        if(team == Lobby.BLUE_TEAM)
        {
            blueScore++;
        } else if(team == Lobby.RED_TEAM) {
            redScore++;
        } else {
            CTFServer.log("ERROR", this.getLobbyID() + ": attempted to score on a invalid team: " + team);
        }
    }

    public static boolean isJoinable(String lobbyID)
    {
        for(int  i = 0; i < lobbies.size(); i++)
        {
            if(lobbies.get(i).getLobbyID().equals(lobbyID))
            {
                if(lobbies.get(i).getGameState() == Lobby.AT_LOBBY)
                {
                    return true;
                } 
            }
        }
        return false;
    }

    public boolean isLobbyLeader(Player player)
    {
        if(player.equals(players.get(0)))
        {
            return true;
        } else {
            return false;
        }
    }

    public void killAllPlayers()
    {
        for(int i = 0; i < players.size(); i++)
        {
            players.get(i).kill();
        }
    }
    

    public static JsonObject listLobbies()
    {
        JsonObject jo = new JsonObject();
        
        JsonArray ja = new JsonArray();
        for(int i = 0; i < lobbies.size(); i++)
        {
            JsonObject temp = new JsonObject();
            temp.addProperty("LOBBY",lobbies.get(i).getLobbyID());
            temp.addProperty("BLUE", lobbies.get(i).getNumberOfPlayers(Lobby.BLUE_TEAM));
            temp.addProperty("RED", lobbies.get(i).getNumberOfPlayers(Lobby.RED_TEAM));
            temp.addProperty("STATE", lobbies.get(i).getGameState());
            ja.add(temp);
        }
        
        jo.addProperty("ACTION","LOBBIES");
        jo.add("LOBBIES", ja);
        return jo;
    }
    
    /**
     * Process updates from players and perform game logic. 
     * Finally broadcast this players location to all other players
     */
    public void playerUpdate(Player player)
    {
        if(player.isHoldingFlag())
        {
            // Update the flag to the players location. 
            player.getFlag().setPoint(player.getPoint());
            //Check if scored
            player.checkIfScored();
        } else if(player.isAlive()) { 
            // Check if the player picked up a flag 
            player.checkIfPickedUpFlag();
        }

        // Check if player is dead and came back to base to be spawned again
        // Or the player is holding their flag and returning it to their base.
        if(player.isDead() || player.isHoldingFlag(player.getTeam()))
        {
            player.checkIfReturnedToBase();
        }

        for(int i = 0; i < players.size(); i++)
        {
            if(!players.get(i).equals(player))
            {
                JsonObject jo = new JsonObject();
                jo.addProperty("ACTION", "GPS");
                jo.addProperty("LOCATION", player.getLocation());
                jo.addProperty("PLAYER", player.getUsername());
                jo.addProperty("TEAM", player.getTeam());
                players.get(i).send(jo);
            }
        }
        
        // Check if player has come within range of the opposite team flag holder
        if(player.isAlive() && player.getObservedBluetoothMac() != "")
        {
            Player otherPlayer = this.findPlayerByMAC(player.getObservedBluetoothMac());
            // We are on opposite teams and other player is holding my flag. 
            // Force him to drop the flag
            if(player.getTeam() != otherPlayer.getTeam() && otherPlayer.isHoldingFlag(player.getTeam()))
            {
                Flag tempFlag = otherPlayer.getFlag();
                otherPlayer.dropFlag();
                otherPlayer.kill();
                otherPlayer.setObservedBluetoothMac("");
                player.setFlag(tempFlag);
            }
        }
        
        // Check if the time limit has been reached. 
        if(System.currentTimeMillis() > endTime)
        {
            this.endGame("Time limit hit");
        }
        
        // Check if the score limit has been reached.
        if(redScore >= Lobby.MAX_SCORE || blueScore >= Lobby.MAX_SCORE)
        {
            this.endGame("Max score limit hit");
        }
        
        // Check if the user has gone out of bounds. 
        if(player.isOutOfBounds())
        {
            player.kill();
        }
        
        // If less than two players return to lobby.
        if(players.size() < 2)
        {
            this.endGame("Too few players to continue.");
        }
        player.getLobby().dumpLobby();
    }
    
    public boolean removePlayer(Player player)
    {
        for(int i = 0; i < players.size(); i++)
        {
            if(players.get(i).equals(player))
            {
                players.get(i).setLobby(null);
                players.remove(i);
                return true;
            }
        }
        return false;
    }
    
    public static void removePlayerFromLobby(Player player, Lobby lobby)
    {
        try
        {
            lobby.removePlayer(player);
            JsonObject jo = new JsonObject();
            jo.addProperty("ACTION","LEFT");
            jo.addProperty("PLAYER", player.getUsername());
            lobby.broadcast(jo);
            if(lobby.getNumberOfPlayers() == 0)
            {
                lobbies.remove(lobby);
                Lobby.dumpLobbies();
            }
        } catch(Exception ex) {
            CTFServer.log("ERROR", "Error removing " + player + " from " + lobby);
        }
    }
    
    public void scored(Player player)
    {
        this.incrementScore(player.getTeam());
        CTFServer.log("INFO", player.getUsername() + " has scored.");
        arena.setRandomLocation(player.getFlag());
        player.dropFlag();
        JsonObject jo = new JsonObject();
        jo.addProperty("ACTION", "SCORE");
        jo.addProperty("TEAM", player.getTeamString());
        broadcast(jo);
    }

    public void setGameState(int gameState)
    {
        if(gameState >= 0 && gameState <= 2)
        {
            this.gameState = gameState;
            JsonObject temp = new JsonObject();
            temp.addProperty("ACTION","STATE");
            temp.addProperty("STATE", this.gameState);
            broadcast(temp);
        } 
    }

    public void startGame()
    {
        if(this.getNumberOfPlayers()>=2)
        {
            setGameState(Lobby.IN_PROGRESS);
            JsonObject jo = new JsonObject();
            jo.addProperty("ACTION", "START");
            
            broadcast(jo);
            CTFServer.log("INFO", this.getLobbyID() + " has been started.");
            
            this.killAllPlayers();
            // Set the end time to duration minutes after the start time. 
            long timeToAdd = duration*60*1000;
            this.endTime = System.currentTimeMillis() + timeToAdd;
        }else{
            JsonObject jo = new JsonObject();
            jo.addProperty("ACTION", "LOG");
            jo.addProperty("LEVEL", "ERROR");
            jo.addProperty("PAYLOAD", "Not enough players to start game.");
            broadcast(jo);
        }
        
    }
    
    public JsonObject toJson()
    {
        JsonObject jo = new JsonObject();
        jo.addProperty("ACTION","LOBBY");
        jo.addProperty("ID", this.getLobbyID());
        jo.addProperty("ACCURACY", redFlag.getRadius());
        jo.addProperty("EPICENTER", epicenter.getLocation());
        jo.addProperty("STATUS", this.getGameState());
        jo.addProperty("NORTH", arena.getNorth());
        jo.addProperty("SOUTH", arena.getSouth());
        jo.addProperty("EAST", arena.getEast());
        jo.addProperty("WEST", arena.getWest());
        jo.addProperty("RED_SCORE", redScore);
        jo.addProperty("BLUE_SCORE", blueScore);
        jo.addProperty("END_TIME", endTime);
        JsonArray ja = new JsonArray();
        for(int i = 0; i < players.size(); i++)
        {
            ja.add(players.get(i).toJson());
        }
        jo.add("PLAYERS", ja);
        return jo;
    }
}

