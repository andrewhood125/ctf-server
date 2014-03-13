/**
 * Store the boundaries for arena 
 * @author Andrew Hood <andrewhood125@gmail.com>
 * @version 0.1
 * 
 * Copyright (c) 2014 Andrew Hood. All rights reserved.
 */
// It should implement locatable
class Arena extends Locate
{
    /**
     * Instance variables
     */
    double north, south, east, west,size;

    Arena(double latitude, double longitude, double arenaSize)
    {
        west = latitude - arenaSize;
        east = latitude + arenaSize;
        north = longitude + arenaSize;
        south = longitude - arenaSize; 
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

    public double getWest()
    {
        return west;
    }
}
