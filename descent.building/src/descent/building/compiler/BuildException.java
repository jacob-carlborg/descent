package descent.building.compiler;

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
