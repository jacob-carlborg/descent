package descent.building.compiler;

import java.util.ArrayList;
import java.util.List;

public final class BuildResponse
{
	public final List<BuildError> errors = new ArrayList<BuildError>();
	
	/* (non-Javadoc)
	 * @see descent.launching.compiler.IBuildResponse#wasSuccessful()
	 */
	public boolean wasSuccessful()
	{
		return errors.size() == 0;
	}
	
	/* (non-Javadoc)
	 * @see descent.launching.compiler.IBuildResponse#getBuildErrors()
	 */
	public List<BuildError> getBuildErrors()
	{
		return errors;
	}
	
	public void addError(BuildError error)
	{
		errors.add(error);
	}
}
