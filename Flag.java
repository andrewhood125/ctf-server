/**
 * Store everything that a single lobby will need.
 * @author Andrew Hood <andrewhood125@gmail.com>
 * @version 0.1
 * 
 * Copyright (c) 2014 Andrew Hood. All rights reserved.
 */
import com.google.gson.JsonObject;

public class Flag extends Area
{
    /**
     * Instance variables
     */
    private int team;
    
    /**
     * Constructors
     */
    Flag(int team, double latitude, double longitude, double radius, Arena arena)
    {
        super(latitude, longitude, radius, arena);
        this.team = team;
        this.setRandomLocation(this);
    }
    
    public int getTeam()
    {
        return this.team;
    }
    
    public void updateLocation(double latitude, double longitude)
    {
        try
        {
            this.setLatitude(latitude);
            this.setLongitude(longitude);
        } catch(LatitudeException ex) {
            System.err.println(ex.getMessage());
        } catch(LongitudeException ex) {
            System.err.println(ex.getMessage());
        }
        
        this.setNorth(this.getRadius());
        this.setEast(this.getRadius());
        this.setSouth(this.getRadius());
        this.setWest(this.getRadius());
    }
    
    public JsonObject toJson()
    {
        JsonObject jo = new JsonObject();
        jo.addProperty("TEAM", this.team);
        jo.addProperty("LOCATION", this.getLocation());
        return jo;
    }
}
