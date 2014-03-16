
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
    private double east, north, south, west,radius;
    
    /**
     * Constructor
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
    
    public void generateBlueBasePoint(Base base)
    {
        // Get width of the arena
        double width = this.getEast() - this.getWest();
        // Get width of a single Q
        width /= 6;
        //Subtract the diameter of the flag
        width -= base.getRadius()*2;
        
        // Width is now the range to generate the longitude;
        double newLongitude = Math.random()*width;
        
        // Now add west and flag radius
        newLongitude +=  this.getEast() - base.getRadius();
        
        // Get height of the arena
        double height = this.getNorth() - this.getSouth();
        // Subtract the diameter of the flag
        height -= base.getRadius()*2;
        
        // Height is now the range to generate the latitude
        double newLatitude = Math.random()*height;
        
        // Now add south and flag radius
        newLatitude += this.getSouth() + base.getRadius();
        
        base.updateLocation(newLatitude, newLongitude);
    }
    
    public void generateBlueFlagPoint(Flag flag)
    {
        // Get width of the arena
        double width = this.getEast() - this.getWest();
        // Get width of a single Q
        width /= 6;
        // Double the Q
        width *= 2;
        //Subtract the diameter of the flag
        width -= flag.getRadius()*2;
        
        // Width is now the range to generate the longitude;
        double newLongitude = Math.random()*width;
        
        // Now add west and flag radius
        newLongitude +=  this.getEast() - flag.getRadius();
        
        // Get height of the arena
        double height = this.getNorth() - this.getSouth();
        // Subtract the diameter of the flag
        height -= flag.getRadius()*2;
        
        // Height is now the range to generate the latitude
        double newLatitude = Math.random()*height;
        
        // Now add south and flag radius
        newLatitude += this.getSouth() + flag.getRadius();
       
        flag.updateLocation(newLatitude, newLongitude);
    }
    
    public void generateRedBasePoint(Base base)
    {
        // Get width of the arena
        double width = this.getEast() - this.getWest();
        // Get width of a single Q
        width /= 6;
        //Subtract the diameter of the flag
        width -= base.getRadius()*2;
        
        // Width is now the range to generate the longitude;
        double newLongitude = Math.random()*width;
        
        // Now add west and flag radius
        newLongitude += this.getWest() + base.getRadius();
        
        // Get height of the arena
        double height = this.getNorth() - this.getSouth();
        // Subtract the diameter of the flag
        height -= base.getRadius()*2;
        
        // Height is now the range to generate the latitude
        double newLatitude = Math.random()*height;
        
        // Now add south and flag radius
        newLatitude += this.getSouth() + base.getRadius();
       
        base.updateLocation(newLatitude, newLongitude);
    }
    
    public void generateRedFlagPoint(Flag flag)
    {
        // Get width of the arena
        double width = this.getEast() - this.getWest();
        // Get width of a single Q
        width /= 6;
        // Double the Q
        width *= 2;
        //Subtract the diameter of the flag
        width -= flag.getRadius()*2;
        
        // Width is now the range to generate the longitude;
        double newLongitude = Math.random()*width;
        
        // Now add west and flag radius
        newLongitude += this.getWest() + flag.getRadius();
        
        // Get height of the arena
        double height = this.getNorth() - this.getSouth();
        // Subtract the diameter of the flag
        height -= flag.getRadius()*2;
        
        // Height is now the range to generate the latitude
        double newLatitude = Math.random()*height;
        
        // Now add south and flag radius
        newLatitude += this.getSouth() + flag.getRadius();
       
        flag.updateLocation(newLatitude, newLongitude);
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
