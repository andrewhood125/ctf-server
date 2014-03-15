
/**
 * Thrown when a impossible latitude is given.
 * 
 * @author Andrew Hood
 * @version 0.1
 */
public class LatitudeException extends PointException
{
    public LatitudeException(double latitude)
    {
        super("Latitude: " + latitude + " is outside acceptable range.");
    }
}
