/**
 * Author: Andrew Hood <andrewhood125@gmail.com>
 * Description: Store everything that a single lobby will need.
 * Copyright (c) 2014 Andrew Hood. All rights reserved.
 */

interface Locatable
{
  double lat, long;

  public getLat()
  {
    return lat;
  }
}
