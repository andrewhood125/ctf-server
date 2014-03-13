/**
 * Store everything that a single lobby will need.
 * @author Andrew Hood <andrewhood125@gmail.com>
 * @version 0.1
 * 
 * Copyright (c) 2014 Andrew Hood. All rights reserved.
 */

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
    private double size;
    private Flag blueFlag, redFlag;
    private int blueScore, gameState, redScore;
    private String lobbyID;
    
    /**
     * Constructors
     */
    Lobby(Player host, double arenaSize)
    {
        players = new ArrayList<Player>();
        // accuracy should be provided during lobby creation in the future defaults to 1 right now. 
        int accuracy = 1;
        this.lobbyID = generateLobbyID();
        // Create arena based on arenaSize and Players gps coordinates.
        arena = new Arena(host.getLatitude(), host.getLongitude(), arenaSize);
        host.setTeam(RED_TEAM);
        players.add(host);
        double flagLatitude = (arena.getNorth() + arena.getSouth()) / 2;
        redFlag = new Flag(flagLatitude, arena.getWest() + arenaSize*.15, accuracy);
        blueFlag = new Flag(flagLatitude, arena.getEast() - arenaSize*.15, accuracy);
        redBase = new Base(flagLatitude, arena.getWest() + arenaSize*.15, accuracy);
        blueBase = new Base(flagLatitude, arena.getEast() - arenaSize*.15, accuracy);
        gameState = Lobby.AT_LOBBY;
        size = arenaSize;
        // Add this new Lobby to the lobbies list
        lobbies.add(this);
    }
    
    public static Lobby addPlayerToLobby(Player newPlayer, String lobbyID)
    {
        for(int i = 0; i < lobbies.size(); i++)
        {
            if(lobbies.get(i).getLobbyID().equals(lobbyID))
            {
                lobbies.get(i).addNewPlayer(newPlayer);
                lobbies.get(i).broadcastPlayers();
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
            broadcast(newPlayer.getUsername() + " joined blue team.");
        } else {
            newPlayer.setTeam(Lobby.RED_TEAM);
            broadcast(newPlayer.getUsername() + " joined red team.");
        }
        players.add(newPlayer);
    }

    public void broadcast(String broadcastMessage)
    {
        for(int i = 0; i < players.size(); i++)
        {
            players.get(i).send(broadcastMessage);
        }
    }

    public void broadcastLocation(Player player)
    {
        // Check if player has scored if the player is holding the flag
        if(player.isHoldingFlag())
        {
            //Check if scored
            checkIfScored(player);
        } else { 
            // Check if the player picked up a flag 
            checkIfPickedUpFlag(player);
        }

        // Check if player is dead and came back to base to be spawned again
        if(player.isDead())
        {
            checkIfReturnedToBase(player);
        }

        for(int i = 0; i < players.size(); i++)
        {
            if(!players.get(i).equals(player))
            {
                players.get(i).send("GPS: " + player.getUsername() + " " + player.getLatitude() + "," + player.getLongitude());
            }
        }
    }

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

    // This method should be in Player you check if a Player picks up a flag not a lobby. 
    public void checkIfPickedUpFlag(Player player)
    {
        if(player.getTeam() == Player.RED_TEAM && blueFlag.isDropped())
        {
            if(withinRange(player, blueFlag))
            {
                // player picks up blue flag
                blueFlag.setDropped(false);
                player.setHoldingFlag(true);
            }
        } else if(player.getTeam() == Player.BLUE_TEAM && redFlag.isDropped()) {
            if(withinRange(player, redFlag))
            {
                redFlag.setDropped(false);
                player.setHoldingFlag(true);
            }
        }
    }

    // This method should be in Player you check if a Player picks up a flag not a lobby.
    public void checkIfReturnedToBase(Player player)
    {
        if(player.getTeam() == Player.RED_TEAM)
        {
            if(withinRange(player, redBase))
            {
                player.spawn();
            }
        } else if(player.getTeam() == Player.BLUE_TEAM) {
            if(withinRange(player, blueBase))
            {
                player.spawn();
            }
        }
    }

    // // This method should be in Player. Players score not lobbies
    public void checkIfScored(Player player)
    {
        // TODO!!! Check if player is holding the opposite teams flag beore scoring. 
        if(player.getTeam() == Player.RED_TEAM)
        {
            // Check if at blue base
            if(withinRange(player, blueBase))
            {
                // player has scored increment player teams score
                // return flag back to base
                // send all players new flag coordinates
                redScore++;
                broadcast("Blue team has scored.");
                blueFlag.updateLocation(blueBase);
            }
        } else if(player.getTeam() == Player.BLUE_TEAM) {
            if(withinRange(player, redBase))
            {
                blueScore++;
                broadcast("Red team has scored.");
                redFlag.updateLocation(redBase);
            }
        }
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

    public ArrayList<Player> getPlayers()
    {
        return players;
    }

    public double getSize()
    {
        return size;
    }
    
    public static boolean isJoinable(String lobbyID)
    {
        for(int i = 0; i < lobbies.size(); i++)
        {
            if(lobbies.get(i).getLobbyID().equals(lobbyID))
            {
                if(lobbies.get(i).getGameState() == Lobby.AT_LOBBY)
                {
                    return true;
                } else {
                    return false;
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

    // This is killing a player not a lobby. It should be player.kill() in Player.java
    public void kill(Player player)
    {
        player.setLifeState(Player.DEAD);
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

    public void setGameState(int gameState)
    {
        if(gameState >= 0 && gameState <= 1)
        {
            this.gameState = gameState;
        } 
    }

    public void start()
    {
        setGameState(Lobby.IN_PROGRESS);
        broadcast("The game has been started.");
        System.out.println(this + " has been started.");
        broadcast(redFlag + " {" + redFlag.getLatitude() + "," + redFlag.getLongitude() + "}");
        broadcast(blueFlag + " {" + blueFlag.getLatitude() + "," + blueFlag.getLongitude() + "}");
        broadcast(redBase + " {" + redBase.getLatitude() + "," + redBase.getLongitude() + "}");
        broadcast(blueBase + " {" + blueBase.getLatitude() + "," + blueBase.getLongitude() + "}");
        // Kill all players 
        for(int i = 0; i < players.size(); i++)
        {
            kill(players.get(i));
        }
    }

    // Belongs in player
    public boolean withinRange(Player player, Base base)
    {
        // if player is at base + or - scoring range
        if(player.getLatitude() > base.getWest() && player.getLatitude() < base.getEast() &&
        player.getLongitude() > base.getSouth() && player.getLongitude() < base.getNorth())
        {
            return true;
        }
        return false;
    }

    // Belongs in player
    public boolean withinRange(Player player, Flag flag)
    {
        // if player is at base + or - scoring range
        if(player.getLatitude() > flag.getWest() && player.getLatitude() < flag.getEast() &&
        player.getLongitude() > flag.getSouth() && player.getLongitude() < flag.getNorth())
        {
            return true;
        }
        return false;
    }   

    
}

