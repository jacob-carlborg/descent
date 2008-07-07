package descent.internal.building.debuild;

/**
 * Simple exception wrapper for anything that goes wrong during a debuild
 * execution.
 * 
 * @author Robert Fraser
 */
@SuppressWarnings("serial")
/* package */ class DebuildException extends RuntimeException
{    
    public DebuildException(String msg)
    {
        super(msg);
    }
    
    public DebuildException(Exception e)
    {
        super(e);
    }
}
