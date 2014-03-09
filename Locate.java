/**
 * Author: Andrew Hood <andrewhood125@gmail.com>
 * Description: Store common elements that have a location.
 * Copyright (c) 2014 Andrew Hood. All rights reserved.
 */

abstract class Locate
{
  double latitude;
  double longitude;

  public double getLatitude()
  {
    return latitude;
  }

  public void readLocation()
  {
    try
    {
      String location in.readLine();
      System.out.println(this.toString() + " location: " + location);
      String[] coordinates = location.split(",");
      latitude = coordinates[0];
      longitude = coordinates[1];
    } catch(Exception ex) {
      System.err.println(ex.getMessage());
      System.exit(13);
    }
  }
}
