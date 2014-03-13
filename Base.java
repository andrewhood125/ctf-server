/**
 * The location of a teams base. 
 * 
 * @author Andrew Hood <andrewhood125@gmail.com>
 * @version 0.1
 * 
 * Copyright (c) 2014 Andrew Hood. All rights reserved.
 */

// Shold implement locatable
public class Base extends Locate
{
    /**
     * Instanve variables
     */
    double west, east, north, south;

    /**
     * Constructors
     */
    Base(double latitude, double longitude, double accuracy)
    {
        this.latitude = latitude;
        this.longitude = longitude;
        west = latitude - accuracy;
        east = latitude + accuracy;
        north = longitude + accuracy;
        south = longitude - accuracy;
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
