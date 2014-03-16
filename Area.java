
/**
 * Parent of Base, Flag and Arena any class
 * that has occupies a space.
 * 
 * @author Andrew Hood 
 * @version 0.1
 */
public abstract class Area extends Point
{
    /**
     * Instance variable
     */
    private Area area;
    private double east, north, south, west, radius;
    
    // Constructors
    
    /**
     * Constructor for Flag and Base which provide the Arena
     */
    public Area(double latitude, double longitude, double radius, Area area)
    {
        super(latitude, longitude);
        this.setEast(radius);
        this.setNorth(radius);
        this.setSouth(radius);
        this.setWest(radius);
        this.setRadius(radius);
        this.area = area;
    }
    
    /**
     * Constructor for Arena which excludes itself from this constructor
     */
    public Area(double latitude, double longitude, double radius)
    {
        super(latitude, longitude);
        this.setEast(radius);
        this.setNorth(radius);
        this.setSouth(radius);
        this.setWest(radius);
        this.setRadius(radius);
    }
    
    public Area getArea()
    {
        return area;
    }
    
    public double getEast()
    {
        return east;
    }

    public double getNorth()
    {
        return north;
    }
    
    public double getRadius()
    {
        return radius;
    }

    public double getSouth()
    {
        return south;
    }

    public double getWest()
    {
        return west;
    }
    
    public void setEast(double radius)
    {
        this.east = this.getLongitude() + radius;
    }
    
    public void setNorth(double radius)
    {
        this.north = this.getLatitude() + radius;
    }
    
    public void setRadius(double radius)
    {
        this.radius = radius;
    }
    
    public void setRandomLocation(Base base)
    {
        Area arena = base.getArea();
        double width = ((arena.getEast() - arena.getWest()) / 6) - base.getRadius()*2;
        double newLongitude = Math.random()*width;
        switch(base.getTeam())
        {
            case Lobby.RED_TEAM: newLongitude += arena.getWest() + base.getRadius(); break;
            case Lobby.BLUE_TEAM: newLongitude *= -1;
                                  newLongitude += arena.getEast() - base.getRadius(); break;
        }
        double height = (arena.getNorth() - arena.getSouth()) - base.getRadius()*2;
        double newLatitude = Math.random()*height;
        newLatitude += arena.getSouth() + base.getRadius();
        base.updateLocation(newLatitude, newLongitude);
    }
    
    public void setRandomLocation(Flag flag)
    {
        Area arena = flag.getArea();
        // Get width of the arena
        // Get width of a single Q
        // Double the Q
        //Subtract the diameter of the flag
        double width = ((arena.getEast() - arena.getWest())/6*2) - flag.getRadius()*2;
        double newLongitude = Math.random()*width;
        switch(flag.getTeam())
        {
            case Lobby.RED_TEAM: newLongitude += arena.getWest() + flag.getRadius(); break;
            case Lobby.BLUE_TEAM: newLongitude *= -1;
                                  newLongitude += arena.getEast() - flag.getRadius(); break;
        }
        
        // Get height of the arena
        // Subtract the diameter of the flag
        double height = (arena.getNorth() - arena.getSouth()) - flag.getRadius()*2;
        
        // Height is now the range to generate the latitude
        // Now add south and flag radius
        double newLatitude = Math.random()*height + arena.getSouth() + flag.getRadius();
        flag.updateLocation(newLatitude, newLongitude);
    }
    
    public void setSouth(double radius)
    {
        this.south = this.getLatitude() - radius;
    }
    
    public void setWest(double radius)
    {
        this.west = this.getLongitude() - radius;
    }
    
    public abstract void updateLocation(double latitude, double longitude);

}
