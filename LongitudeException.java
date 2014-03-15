
/**
 * Thrown when a impossible longitude is given.
 * 
 * @author Andrew Hood
 * @version 0.1
 */
public class LongitudeException extends PointException
{
    public LongitudeException(double longitude)
    {
        super("Longitude: " + longitude + " is outside acceptable range.");
    }
}
