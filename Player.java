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

public class Player implements Runnable, Locatable
{
    /**
     * Constants
     */
    public static final String MACPAT = "^([0-9A-F]{2}[:-]){5}([0-9A-F]{2})$";
    public static final int DEAD = 0;
    public static final int ALIVE = 1;

    /** 
     * Instance variables
     */
    private int id;
    private PrintWriter out;
    private BufferedReader in;
    private String btMAC;
    private String username;
    private Socket socket;
    private boolean greeted, inLobby;
    private Lobby myLobby;
    private int team;
    private int lifeState;
    private boolean isHoldingFlag;
    private double latitude, longitude;

    /**
     * Constructors
     */
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
    
    public void checkIfPickedUpFlag()
    {
        if(this.getTeam() == Lobby.RED_TEAM && myLobby.getBlueFlag().isDropped())
        {
            if(this.withinRange(myLobby.getBlueFlag()))
            {
                // player picks up blue flag
                myLobby.getBlueFlag().setDropped(false);
                this.setHoldingFlag(true);
            }
        } else if(this.getTeam() == Lobby.BLUE_TEAM && myLobby.getRedFlag().isDropped()) {
            if(this.withinRange(myLobby.getRedFlag()))
            {
                myLobby.getRedFlag().setDropped(false);
                this.setHoldingFlag(true);
            }
        }
    }

    public void checkIfReturnedToBase()
    {
        if(this.getTeam() == Lobby.RED_TEAM)
        {
            if(this.withinRange(myLobby.getRedBase()))
            {
                this.spawn();
            }
        } else if(this.getTeam() == Lobby.BLUE_TEAM) {
            if(this.withinRange(myLobby.getBlueBase()))
            {
                this.spawn();
            }
        }
    }

    public void checkIfScored()
    {
        // TODO!!! Check if player is holding the opposite teams flag beore scoring. 
        if(this.getTeam() == Lobby.RED_TEAM)
        {
            // Check if at blue base
            if(this.withinRange(myLobby.getBlueBase()))
            {
                // player has scored increment player teams score
                // return flag back to base
                // send all players new flag coordinates
                myLobby.redScored();
            }
        } else if(this.getTeam() == Lobby.BLUE_TEAM) {
            if(this.withinRange(myLobby.getRedBase()))
            {
                myLobby.blueScored();
            }
        }
    }

    // Go in a parent class for all methods needing location methods
    public double getLatitude()
    {
        return latitude;
    }

    // Go in a parent class for all methods needing location methods
    public double getLongitude()
    {
        return longitude;
    }

    public int getTeam(){
        return team;
    }

    public String getUsername()
    {
        return username;
    }

    public boolean isDead()
    {
        return lifeState == Player.DEAD;
    }

    public boolean isHoldingFlag()
    {
        return isHoldingFlag;
    }

    // Go in a parent class for all methods needing location methods
    private boolean isValidLatitude(double latitude)
    {
        if(latitude < -90 || latitude > 90)
        {
            return false;
        }
        return true;
    }

    // Go in a parent class for all methods needing location methods
    public boolean isValidLongitude(double longitude)
    {
        if(longitude < -180 || longitude > 180)
        {
            return false;
        }
        return true;
    }

    public void kill()
    {
        this.setLifeState(Player.DEAD);
    }
    
    // Go in a parent class for all methods needing location methods
    public double[] parseCoordinates(String location) throws Exception
    {
        if(!location.contains(","))
        {
            throw new Exception();
        }

        String[] coord = location.split(",");

        if(coord.length != 2)
        {
            throw new Exception();
        }

        double[] latlong = new double[2];

        latlong[0] = Double.parseDouble(coord[0]);
        latlong[1] = Double.parseDouble(coord[1]);

        if(!isValidLatitude(latitude) || !isValidLongitude(longitude))
        {
            throw new Exception();
        }
        return latlong;
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
                    myLobby = new Lobby(this, newLobbySize);
                } catch(NumberFormatException ex) {
                    System.err.println(ex.getMessage());
                    processCommand("CREATE");
                } catch(IOException ex) {
                    System.err.println("IOException while trying to create a new lobby: " + ex.getMessage());
                    System.exit(25);
                }
                
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
            case "GPS":
            // GPS is used to accept location updates from clients
            if(!greeted)
            {
                out.println("ERROR: Need to greet first.");
            } else if(!inLobby) {
                out.println("ERROR: Need to be in lobby.");
            } else if(myLobby.getGameState()!= Lobby.IN_PROGRESS) {
                out.println("ERROR: The game must be in progress.");
            } else {
                updateLocation();
            }
            break;
            case "JOIN":
            if(!greeted)
            {
                out.println("ERROR: Need to greet first.");
            } else if(!inLobby) {
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
                out.println(Lobby.listLobbies());
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
                Lobby.removePlayerFromLobby(this, myLobby);
                inLobby = false;
                out.println("You've left the lobby.");
            } else {
                out.println("ERROR: Something went wrong but I don't know what.");
            }
            break;

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

    public void readLocation()
    {
        try
        {
            String location = in.readLine();
            System.out.println(this.toString() + " location: " + location);
            double[] coordinates = parseCoordinates(location);
            latitude = coordinates[0];
            longitude = coordinates[1];
        } catch(Exception ex) {
            System.err.println(ex.getMessage());
            out.println("ERROR: GPS improperly formatted.");
            readLocation();
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

        this.send("Shutting down per your request.\n Good bye.");
        try
        {
            out.close();
            in.close();
            socket.close();
            if(inLobby)
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
        out.println(message);
    }

    public void setHoldingFlag(boolean bool)
    {
        isHoldingFlag = bool;
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

    public void setTeam(int team)
    {
        if(team >= 0 && team <= 2)
        {
            this.team = team;
        }
    }

    public void spawn()
    {
        setLifeState(Player.ALIVE);
    }
    
    public void updateLocation()
    {
        try
        {
            String coordinates = in.readLine();
            double[] latlong = parseCoordinates(coordinates);
            latitude = latlong[0];
            longitude = latlong[1];
        } catch(Exception ex) {
            System.out.println("Received bad location from " + this.toString());
            send("ERROR: Improperly formatted location.");
        }
        myLobby.broadcastLocation(this);
        System.out.println(this + " sent updated location: {" + latitude + "," + longitude + "}");
    }
    
    public boolean withinRange(Base base)
    {
        // if player is at base + or - scoring range
        if(this.getLatitude() > base.getWest() && this.getLatitude() < base.getEast() &&
        this.getLongitude() > base.getSouth() && this.getLongitude() < base.getNorth())
        {
            return true;
        }
        return false;
    }

    public boolean withinRange(Flag flag)
    {
        // if player is at base + or - scoring range
        if(this.getLatitude() > flag.getWest() && this.getLatitude() < flag.getEast() &&
        this.getLongitude() > flag.getSouth() && this.getLongitude() < flag.getNorth())
        {
            return true;
        }
        return false;
    }   
}
