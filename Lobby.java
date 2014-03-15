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
        this.players = new ArrayList<Player>();
        this.addNewPlayer(host);
        this.lobbyID = generateLobbyID();
        // Create arena based on arenaSize and Players gps coordinates.
        this.arena = new Arena(host.getLatitude(), host.getLongitude(), arenaSize);
        double flagLatitude = (arena.getNorth() + arena.getSouth()) / 2;
        // accuracy should be provided during lobby creation in the future defaults to 1 right now. 
        int accuracy = 1;

        this.redFlag = new Flag(Lobby.RED_TEAM, flagLatitude, this.arena.getWest() + arenaSize*.15, accuracy);
        this.blueFlag = new Flag(Lobby.BLUE_TEAM, flagLatitude, this.arena.getEast() - arenaSize*.15, accuracy);
        this.redBase = new Base(Lobby.RED_TEAM, flagLatitude, this.arena.getWest() + arenaSize*.15, accuracy);
        this.blueBase = new Base(Lobby.BLUE_TEAM, flagLatitude, this.arena.getEast() - arenaSize*.15, accuracy);
        this.setGameState(Lobby.AT_LOBBY);
        this.size = arenaSize;
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
    
    public void blueScored()
    {
        blueScore++;
        broadcast("Blue team has scored.");
        redFlag.setPoint(redBase);
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
            player.checkIfScored();
        } else { 
            // Check if the player picked up a flag 
            player.checkIfPickedUpFlag();
        }

        // Check if player is dead and came back to base to be spawned again
        if(player.isDead())
        {
            player.checkIfReturnedToBase();
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
    
    public Base getBlueBase()
    {
        return blueBase;
    }
    
    public Flag getBlueFlag()
    {
        return blueFlag;
    }
    
    public Base getRedBase()
    {
        return redBase;
    }
    
    public Flag getRedFlag()
    {
        return redFlag;
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

    public static boolean isJoinable(String lobbyID)
    {
        for(int  i = 0; i < lobbies.size(); i++)
        {
            if(lobbies.get(i).getLobbyID().equals(lobbyID))
            {
                return true;
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
    
    public void redScored()
    {
        redScore++;
        broadcast("Red team has scored.");
        blueFlag.setPoint(blueFlag);
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
        broadcast("Red Flag {" + redFlag.getLatitude() + "," + redFlag.getLongitude() + "}");
        broadcast("Blue Flag {" + blueFlag.getLatitude() + "," + blueFlag.getLongitude() + "}");
        broadcast("Red Base {" + redBase.getLatitude() + "," + redBase.getLongitude() + "}");
        broadcast("Blue Base {" + blueBase.getLatitude() + "," + blueBase.getLongitude() + "}");
        // Kill all players 
        for(int i = 0; i < players.size(); i++)
        {
            players.get(i).kill();
        }
    }
    
    public String toString()
    {
        return "LOBBY===========" + this.lobbyID + "\n"
                + "Red Team " + this.redScore + " {" + this.getTeamPlayers(Lobby.RED_TEAM) + "}\n"
                + "Blue Team " + this.blueScore + " {" + this.getTeamPlayers(Lobby.BLUE_TEAM) + "}\n"
                + "Red Flag {" + redFlag.getLocation() + "}\n"
                + "Blue Flag {" + blueFlag.getLocation() + "}\n"
                + "Red Base {" + redBase.getLocation() + "}\n"
                + "Blue Base {" + blueBase.getLocation() + "}\n"
                + "----------------" + this.arena.getNorth() + "----------------\n"
                + "|                                     |\n"
                + "|                                     |\n"
                + "   " + this.arena.getSouth()
                + "|                                              " + this.arena.getEast() + "\n"
                + "|                                     |\n"
                + "|                                     |\n"
                + "----------------" + this.arena.getSouth() + "----------------\n";
    }
}

