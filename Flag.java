/**
 * Store everything that a single lobby will need.
 * @author Andrew Hood <andrewhood125@gmail.com>
 * @version 0.1
 * 
 * Copyright (c) 2014 Andrew Hood. All rights reserved.
 */

public class Flag extends Area
{
    /**
     * Instance variables
     */
    private int team;
    private boolean isDropped;

    /**
     * Constructors
     */
    Flag(int team, double latitude, double longitude, double radius)
    {
        super(latitude, longitude, radius);
        this.team = team;
        this.setDropped(true);
    } 

    public boolean isDropped()
    {
        return isDropped;
    }
    
    public int getTeam()
    {
        return this.team;
    }

    public void setDropped(boolean bool)
    {
        isDropped = bool;
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
}
