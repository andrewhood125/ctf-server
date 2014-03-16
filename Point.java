
/**
 * Parent for all classes that have a latitude and longitude. 
 * 
 * @author Andrew Hood 
 * @version 0.1
 */
public class Point implements Locatable
{
    /**
     * Instance variables
     */
    private double latitude;
    private double longitude;
    
    /**
     * Constructors
     */
    public Point(double latitude, double longitude)
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
    }
    
    public double getLatitude()
    {
        return this.latitude;
    }
    
    public String getLocation()
    {
        return this.latitude + "," + this.longitude;
    }
    
    public double getLongitude()
    {
        return this.longitude;
    }
    
    public void setPoint(String location) throws PointException
    {
        if(location.contains(","))
        {
            String[] coord = location.split(",");
            if(coord.length == 2)
            {
                double[] latlong = new double[2];
                try 
                {
                    this.setLatitude(Double.parseDouble(coord[0]));
                    this.setLongitude(Double.parseDouble(coord[1]));
                    System.out.println(this + " set point to: " + this.getLocation());
                } catch(NullPointerException ex) {
                    throw new PointException(location);
                } catch(NumberFormatException ex) {
                    throw new PointException(location);
                } catch(LatitudeException ex) {
                    System.err.println(ex.getMessage());
                } catch(LongitudeException ex) {
                    System.err.println(ex.getMessage());
                }
            } else {
                throw new PointException(location);
            }
        } else {
            throw new PointException(location);
        }
    }
    
    public void setPoint(Point point)
    {
        try
        {
            this.setLatitude(point.getLatitude());
            this.setLongitude(point.getLongitude());
        } catch(LatitudeException ex) {
            System.err.println(ex.getMessage());
        } catch(LongitudeException ex) {
            System.err.println(ex.getMessage());
        }
    }
    
    public void setLatitude(double latitude) throws LatitudeException
    {
        if(latitude < -90 || latitude > 90)
        {
            throw new LatitudeException(latitude);
        } else {
            this.latitude = latitude;
        }
    }
    
    public void setLongitude(double longitude) throws LongitudeException
    {
        if(longitude < -180 || longitude > 180)
        {
            throw new LongitudeException(longitude);
        } else {
            this.longitude = longitude;
        }
    }
    
    public boolean isWithinArea(Area area)
    {
        if(this.getLongitude() >= area.getWest() && this.getLongitude() <= area.getEast() &&
        this.getLatitude() >= area.getSouth() && this.getLatitude() <= area.getNorth())
        {
            return true;
        } else {
            return false;
        }
    }
}
