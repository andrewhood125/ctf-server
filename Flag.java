/**
 * Store everything that a single lobby will need.
 * @author Andrew Hood <andrewhood125@gmail.com>
 * @version 0.1
 * 
 * Copyright (c) 2014 Andrew Hood. All rights reserved.
 */

public class Flag implements Locatable
{

    /**
     * Instanve variables
     */
    private int team;
    private boolean isDropped;
    private double west, east, north, south, latitude, longitude;

    /**
     * Constructors
     */
    Flag(int team, double latitude, double longitude, double accuracy)
    {
        this.team = team;
        this.latitude = latitude;
        this.longitude = longitude;
        west = latitude - accuracy;
        east = latitude + accuracy;
        north = longitude + accuracy;
        south = longitude - accuracy;
        isDropped = true;
    } 

    public boolean isDropped()
    {
        return isDropped;
    }
    
    public double getLatitude()
    {
        return latitude;
    }
    
    public double getLongitude()
    {
        return longitude;
    }

    public double getEast()
    {
        return east;
    }

    public double getNorth()
    {
        return north;
    }

    public double getSouth()
    {
        return south;
    }
    
    public int getTeam()
    {
        return this.team;
    }

    public double getWest()
    {
        return west;
    }

    public void setDropped(boolean bool)
    {
        isDropped = bool;
    }

    public void updateLocation(Base base)
    {
        latitude = base.getLatitude();
        longitude = base.getLongitude();
    }
}
