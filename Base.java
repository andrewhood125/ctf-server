/**
 * The location of a teams base. 
 * 
 * @author Andrew Hood <andrewhood125@gmail.com>
 * @version 0.1
 * 
 * Copyright (c) 2014 Andrew Hood. All rights reserved.
 */

public class Base extends Area
{
    /**
     * Instance variables
     */
    private int team;
    
    /**
     * Constructors
     */
    Base(int team, double latitude, double longitude, double radius)
    {
        super(latitude, longitude, radius);
        this.team = team;
    }     
}
