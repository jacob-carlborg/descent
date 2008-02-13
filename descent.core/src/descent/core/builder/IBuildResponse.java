package descent.core.builder;

import java.util.List;

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
	
	/**
	 * Gets the list of errors encountered while building.
	 * 
	 * @return the list of errors
	 */
	public List<IBuildError> getBuildErrors();
}
