
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
        double Q = (arena.getEast() - arena.getWest()) / 6.0;
        double offset = Math.random()*Q;
        double newLongitude = 0;
        switch(base.getTeam())
        {
            // Offset the red base by Q/2 and the western longitude. 
            case Lobby.RED_TEAM: newLongitude = arena.getWest() + Q/2 + offset; break;
            // offset the blue base by Q/2 and the eastern longitude. 
            case Lobby.BLUE_TEAM: newLongitude = arena.getEast() - Q/2 - offset; break;
        }
        double height = arena.getNorth() - arena.getSouth() - Q;
        double newLatitude = Math.random()*height;
        newLatitude += arena.getSouth() + Q/2;
        base.updateLocation(newLatitude, newLongitude);
        CTFServer.log("INFO", "Base " + base.getTeam() + " new location: " + base.getLocation());
    }
    
    public void setRandomLocation(Flag flag)
    {
        Area arena = flag.getArea();
        double Q = (arena.getEast() - arena.getWest()) / 6.0;
        double offset = Math.random()*Q;
        double newLongitude = 0;
        switch(flag.getTeam())
        {
            // Offset the red base by Q/2 and the western longitude. 
            case Lobby.RED_TEAM: newLongitude = arena.getWest() + Q/2 + offset; break;
            // offset the blue base by Q/2 and the eastern longitude. 
            case Lobby.BLUE_TEAM: newLongitude = arena.getEast() - Q/2 - offset; break;
        }
        double height = arena.getNorth() - arena.getSouth() - Q;
        double newLatitude = Math.random()*height;
        newLatitude += arena.getSouth() + Q/2;
        flag.updateLocation(newLatitude, newLongitude);
        CTFServer.log("INFO", "Flag " + flag.getTeam() + " new location: " + flag.getLocation());
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
