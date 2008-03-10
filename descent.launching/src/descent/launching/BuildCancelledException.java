package descent.launching;

@SuppressWarnings("serial")
public class BuildCancelledException extends RuntimeException
{
    public BuildCancelledException()
    {
        super();
    }
    
    public BuildCancelledException(String message)
    {
        super(message);
    }
}
