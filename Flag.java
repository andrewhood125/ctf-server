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
}
