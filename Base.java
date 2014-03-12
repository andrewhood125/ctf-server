/**
 * Author: Andrew Hood <andrewhood125@gmail.com>
 * Description: The location of a teams base. 
 * Copyright (c) 2014 Andrew Hood. All rights reserved.
 */

class Base extends Locate
{

  // Base range
  double west, east, north, south;
  Base(double latitude, double longitude, double accuracy)
  {
    this.latitude = latitude;
    this.longitude = longitude;
    west = latitude - accuracy;
    east = latitude + accuracy;
    north = longitude + accuracy;
    south = longitude - accuracy;
  } 

  public double getWest()
  {
    return west;
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
}
