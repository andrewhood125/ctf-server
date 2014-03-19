
/**
 * ComLink listens, interprets, parses and sends input
 *  and output to and from a player.
 * 
 * @author Andrew Hood 
 * @version 0.1
 */

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ComLink
{
    /**
     * Instance variables
     */
    private BufferedReader in;
    private Gson gson;
    private PrintWriter out;
    private Socket socket;
    private Player player;
    
    public ComLink(Socket socket, Player player)
    {
        this.socket = socket;
        this.player = player;
        try
        {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("New player connected from IP: " + socket.getInetAddress());
        } catch(IOException ex) {
            System.err.println(ex.getMessage());
            JsonObject jo = new JsonObject();
            jo.addProperty("ACTION", "LOG");
            jo.addProperty("LEVEL", "ERROR");
            jo.addProperty("PAYLOAD", "I caught an IOException in the ComLink constructor, this is what we know: " + ex.getMessage());
            this.send(jo);
        }
        gson = new Gson();
    }
    
    public void close() throws IOException
    {
        out.close();
        in.close();
        socket.close();
    } 
    
    public void parseCommunication(JsonObject jo)
    {
        JsonElement action = jo.get("ACTION");
        switch(action.getAsString())
        {
            case "HELLO":
            if(!player.isInitialized())
            {
                JsonElement bluetooth = jo.get("BLUETOOTH");
                player.setMyBluetoothMac(bluetooth.getAsString());
                JsonElement username = jo.get("USERNAME");
                player.setUsername(username.getAsString());
            } else {
                out.println("..hi.");
            }
            break;

            case "CREATE": 
            if(!player.isInitialized())
            {
                out.println("ERROR: Need to greet first.");
            } else if(!player.isInLobby()) {
                out.println("Proceed with location.");
                readLocation();
                double newLobbySize = 0;
                try 
                {
                    out.println("Proceed with arena size.");
                    newLobbySize  = Double.parseDouble(in.readLine());
                    player.setLobby(new Lobby(player, newLobbySize));
                } catch(NumberFormatException ex) {
                    System.err.println(ex.getMessage());
                    //parseCommunication("CREATE");
                } catch(IOException ex) {
                    System.err.println("IOException while trying to create a new lobby: " + ex.getMessage());
                    //System.exit(25);
                }
                JsonObject job = new JsonObject();
                job.addProperty("ACTION", "LOG");
                job.addProperty("LEVEL", "INFO");
                job.addProperty("PAYLOAD", "You're now in lobby " + player.getLobby().getLobbyID());
                send(job);
            } else if(player.isInLobby()) {
                out.println("You are already in a lobby.");
            } else {
                out.println("ERROR: Something went wrong but I don't know what.");
                System.exit(17);
            }
            break;

            case "START":
            if(!player.isInLobby())
            {
                out.println("ERROR: Need to greet first.");
            } else if(!player.isInLobby()) {
                out.println("ERROR: Need to be in lobby.");
            } else if(!player.getLobby().isLobbyLeader(player)) {
                out.println("ERROR: Only the lobby leader can start the game.");
            } else {
                player.getLobby().startGame();
            }
            break;
            case "GPS":
            // GPS is used to accept location updates from clients
            if(!player.isInitialized())
            {
                out.println("ERROR: Need to greet first.");
            } else if(!player.isInLobby()) {
                out.println("ERROR: Need to be in lobby.");
            } else if(player.getLobby().getGameState()!= Lobby.IN_PROGRESS) {
                out.println("ERROR: The game must be in progress.");
            } else {
                readLocation();
                player.getLobby().playerUpdate(player);
            }
            break;
            case "JOIN":
            if(!player.isInitialized())
            {
                out.println("ERROR: Need to greet first.");
            } else if(!player.isInLobby()) {
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
                            player.setLobby(Lobby.addPlayerToLobby(player, lobbyID));
                            out.println("Joining lobby " + lobbyID + "...");
                        } else {
                            out.println("ERROR: Lobby not found.");
                        }
                    } catch(IOException ex) {
                        System.err.println(ex.getMessage());
                        System.exit(7);
                    }
                }
            } else if (player.isInLobby()){
                out.println("ERROR: You are already in a lobby.");
            } else {
                out.println("ERROR: Something went wrong but I don't know what.");
                System.exit(18);
            }
            break;

            case "LOBBY": 
            if(!player.isInitialized())
            {
                out.println("ERROR: Need to greet first.");
            } else if(!player.isInLobby()) {
                // List all lobbies
                out.println(Lobby.listLobbies());
            } else if(player.isInLobby()) {
                out.println(player.getLobby().toString());
            }
            break;

            case "LEAVE":
            if(!player.isInitialized())
            {
                out.println("ERROR: Need to greet first.");
            } else if (!player.isInLobby()) {
                out.println("ERROR: You're not in a lobby.");
            } else if(player.isInLobby()) {
                Lobby.removePlayerFromLobby(player, player.getLobby());
                out.println("You've left the lobby.");
            } else {
                out.println("ERROR: Something went wrong but I don't know what.");
            }
            break;
            
            case "DROP":
            if(!player.isInitialized())
            {
                out.println("ERROR: Need to greet first.");
            } else if (!player.isInLobby()) {
                out.println("ERROR: You're not in a lobby.");
            } else if(player.isInLobby()) {
                try
                {
                    player.setObservedBluetoothMac(in.readLine());
                } catch(IOException ex) {
                    System.err.println(ex.getMessage());
                }
                player.getLobby().playerUpdate(player);
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
            if(tempBtMac.matches(Player.MACPAT))
            {
                System.out.println(this.toString() + " BT MAC: " + tempBtMac);
                player.setMyBluetoothMac(tempBtMac);
            }else
            {
                readBluetoothMAC();
            }

        } catch(Exception ex) {
            System.err.println(ex.getMessage());
            System.exit(10);
        }
    }
    
    public JsonObject readLine() throws IOException
    {
        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(in.readLine());
        JsonObject jo = je.getAsJsonObject();
        //JsonElement action = jo.get("ACTION");
        //System.out.println(action.getAsString());
        return jo;
    }

    private void readUsername()
    {
        try 
        {
            String tempUsername = in.readLine();
            System.out.println(this.toString() + " username: " + tempUsername);
            player.setUsername(tempUsername);
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
            player.setPoint(location);
        } catch(IOException ex) {
            System.err.println(ex.getMessage());
            readLocation();
        } catch(PointException ex) {
            System.err.println(ex.getMessage());
            readLocation();
        }
    }
    
    public void send(JsonObject obj)
    {
        System.out.println(gson.toJson(obj));
        out.println(gson.toJson(obj));
    }
}
