package descent.core.builder;

/**
 * Response to a build command (compile or link).
 *
 * @author Robert Fraser
 */
public interface IBuildResponse
{
	/**
	 * Checks whether the command succeeded
	 * 
	 * @return true if and only if the command was succesful
	 */
	public boolean wasSuccessful();
}
