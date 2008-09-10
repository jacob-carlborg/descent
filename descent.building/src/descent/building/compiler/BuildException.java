package descent.building.compiler;

// DOCS moar
public class BuildException extends RuntimeException
{
    public BuildException(String msg)
    {
        super(msg);
    }
    
    public BuildException(Exception e)
    {
        super(e);
    }
}
