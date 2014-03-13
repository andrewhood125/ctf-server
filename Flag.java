/**
 * Store everything that a single lobby will need.
 * @author Andrew Hood <andrewhood125@gmail.com>
 * @version 0.1
 * 
 * Copyright (c) 2014 Andrew Hood. All rights reserved.
 */

// Should implement locatable
class Flag extends Locate
{

    /**
     * Instanve variables
     */
    boolean isDropped;
    double west, east, north, south;

    /**
     * Constructors
     */
    Flag(double latitude, double longitude, double accuracy)
    {
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
