/**
 * Store everything that a single lobby will need.
 * @author Andrew Hood <andrewhood125@gmail.com>
 * @version 0.1
 * 
 * Copyright (c) 2014 Andrew Hood. All rights reserved.
 */

import com.google.gson.JsonObject;
import java.util.ArrayList;

public class Lobby
{
    /**
     * Constants
     */
    public static final int AT_LOBBY = 0;
    public static final int IN_PROGRESS = 1;
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

    /**
     * Constructors
     */
    Lobby(Player host, double arenaSize)
    {
        this.players = new ArrayList<Player>();
        this.addNewPlayer(host);
        this.lobbyID = generateLobbyID();
        // Create arena based on arenaSize and Players gps coordinates.
        this.arena = new Arena(host.getLatitude(), host.getLongitude(), arenaSize);
        // accuracy should be provided during lobby creation in the future defaults to 1 right now. 
        int radius = 1;
        this.duration = 5;

        this.redFlag = new Flag(Lobby.RED_TEAM, 0, 0, radius, this.arena);
        this.blueFlag = new Flag(Lobby.BLUE_TEAM, 0, 0, radius, this.arena);
        this.redBase = new Base(Lobby.RED_TEAM, 0, 0, radius, this.arena);
        this.blueBase = new Base(Lobby.BLUE_TEAM, 0, 0, radius, this.arena);
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
                lobbies.get(i).addNewPlayer(newPlayer);
                return lobbies.get(i);
            }
        }
        return null;
    } 

    public void addNewPlayer(Player newPlayer)
    {
        if(players.size() % 2 != 0 )
        {
            newPlayer.setTeam(Lobby.BLUE_TEAM);
            
        } else {
            newPlayer.setTeam(Lobby.RED_TEAM);
        }
        
        JsonObject jo = new JsonObject();
        jo.addProperty("ACTION", "JOINED");
        jo.addProperty("LOCATION", newPlayer.getLocation());
        jo.addProperty("PLAYER", newPlayer.getTeamString());
        jo.addProperty("TEAM", newPlayer.getTeamString());
        jo.addProperty("BLUETOOTH", newPlayer.getMyBluetoothMac());
        broadcast(jo);
        players.add(newPlayer);
    }

    public void broadcast(JsonObject obj)
    {
        for(int i = 0; i < players.size(); i++)
        {
            players.get(i).send(obj);
        }
    }

    /*
    public void broadcastPlayers()
    {
        broadcast("RED TEAM");
        broadcast("=========");
        for(int i = 0; i < players.size(); i++)
        {
            if(players.get(i).getTeam() == 1)
            {
                broadcast("Player: " + players.get(i).getUsername());
            }                  
        }
        broadcast("BLUE TEAM");
        broadcast("=========");
        for(int i = 0; i < players.size(); i++)
        {
            if(players.get(i).getTeam() == 2)
            {
                broadcast("Player: " + players.get(i).getUsername());
            }                  
        }  
    }
    */
    
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
        jo.addProperty("WINNER", "BLUE, this is static");
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
                return players.get(i) + " @ " + players.get(i).getLocation();
            }
        }
        
        return "dropped @ " + this.getFlag(team).getLocation();
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
            System.err.println("Attempted to score on a invalid team: " + team);
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
    
    /**
     * Process updates from players and perform game logic. 
     * Finally broadcast this players location to all other players
     */
    public void playerUpdate(Player player)
    {
        // Check if player has scored if the player is holding the flag
        if(player.isHoldingFlag())
        {
            //Check if scored
            player.checkIfScored();
        } else { 
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
                players.get(i).send(jo);
            }
        }
        
        // Check if player has come within range of the opposite team flag holder
        if(player.getObservedBluetoothMac() != "")
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
    }
    
    public void removePlayer(Player player)
    {
        for(int i = 0; i < players.size(); i++)
        {
            if(players.get(i).equals(player))
            {
                players.remove(i);
                break;
            }
        }
    }
    
    public static void removePlayerFromLobby(Player player, Lobby lobby)
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
    
    public void scored(Player player)
    {
        this.incrementScore(player.getTeam());
        arena.setRandomLocation(player.getFlag());
        player.dropFlag();
        JsonObject jo = new JsonObject();
        jo.addProperty("ACTION", "SCORE");
        jo.addProperty("TEAM", player.getTeamString());
        broadcast(jo);
    }

    public void setGameState(int gameState)
    {
        if(gameState >= 0 && gameState <= 1)
        {
            this.gameState = gameState;
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
            System.out.println(this + " has been started.");
            
            this.killAllPlayers();
            // Set the end time to duration minutes after the start time. 
            long timeToAdd = duration*60*1000;
            System.out.println("Current time " + System.currentTimeMillis());
            System.out.println("Time to add to current time: " + timeToAdd);
            this.endTime = System.currentTimeMillis() + timeToAdd;
        }else{
            JsonObject jo = new JsonObject();
            jo.addProperty("ACTION", "LOG");
            jo.addProperty("LEVEL", "ERROR");
            jo.addProperty("PAYLOAD", "Not enough players to start game.");
            broadcast(jo);
        }
        
    }
    
    public String toString()
    {
        return "LOBBY=====" + this.getGameStateString() + "======" + this.lobbyID + "\n"
                + "End Time " + endTime + "\n"
                + "Current Time " + System.currentTimeMillis() + "\n"
                + "Time left " + (endTime - System.currentTimeMillis()) + "\n"
                + "Red Team " + this.redScore + " {" + this.getTeamPlayers(Lobby.RED_TEAM) + "}\n"
                + "Red Base {" + redBase.getLocation() + "}\n"
                + "Red Flag held by " + this.getFlagHolder(Lobby.RED_TEAM) + "\n"
                + "Blue Team " + this.blueScore + " {" + this.getTeamPlayers(Lobby.BLUE_TEAM) + "}\n"
                + "Blue Base {" + blueBase.getLocation() + "}\n"
                + "Blue Flag held by " + this.getFlagHolder(Lobby.BLUE_TEAM) + "\n"
                + "----------------" + this.arena.getNorth() + "----------------\n"
                + "|                                  |\n"
                + "|                                  |\n"
                + this.arena.getWest() + "\n"
                + "|                                    " + this.arena.getEast() + "\n"
                + "|                                  |\n"
                + "|                                  |\n"
                + "----------------" + this.arena.getSouth() + "----------------\n";
    }
}

