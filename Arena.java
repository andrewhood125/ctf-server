/**
 * Author: Andrew Hood <andrewhood125@gmail.com>
 * Description: Store the boundaries for arena 
 * Copyright (c) 2014 Andrew Hood. All rights reserved.
 */
class Arena extends Locate
{
  double north, south, east, west;

  Arena(double latitude, double longitude, double arenaSize)
  {
    west = latitude - arenaSize;
    east = latitude + arenaSize;
    north = longitude + arenaSize;
    south = longitude - arenaSize;  
  }

  public double getNorth()
  {
    return north;
  }
  public double getSouth()
  {
    return south;
  }
  public double getEast()
  {
    return east;
  }
  public double getWest()
  {
    return west;
  }
}
