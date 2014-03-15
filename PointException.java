
/**
 * Thrown when a improperly formmated Point is given.
 * 
 * @author Andrew Hood
 * @version 0.1
 */
public class PointException extends Exception
{
    public PointException(String point)
    {
        super("Point: " + point + " is improperly formatted.");
    }
}
