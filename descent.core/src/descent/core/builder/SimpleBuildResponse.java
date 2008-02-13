package descent.core.builder;

public abstract class SimpleBuildResponse implements IBuildResponse
{
	public boolean succesful;
	
	public boolean wasSuccessful()
	{
		return succesful;
	}
}
