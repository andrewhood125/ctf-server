/**
 * Store the boundaries for arena 
 * @author Andrew Hood <andrewhood125@gmail.com>
 * @version 0.1
 * 
 * Copyright (c) 2014 Andrew Hood. All rights reserved.
 */
public class Arena extends Area
{
    Arena(double latitude, double longitude, double radius)
    {
        super(latitude, longitude, radius);
        this.setWest(radius*2);
        this.setEast(radius*2);
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
        this.setEast(this.getRadius()*2);
        this.setSouth(this.getRadius());
        this.setWest(this.getRadius()*2);
    }
}
