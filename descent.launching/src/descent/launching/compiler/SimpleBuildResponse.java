package descent.launching.compiler;

import java.util.ArrayList;
import java.util.List;

public class SimpleBuildResponse implements IBuildResponse
{
	public boolean succesful;
	public final List<IBuildError> errors = new ArrayList<IBuildError>();
	
	/* (non-Javadoc)
	 * @see descent.launching.compiler.IBuildResponse#wasSuccessful()
	 */
	public boolean wasSuccessful()
	{
		return succesful;
	}
	
	/* (non-Javadoc)
	 * @see descent.launching.compiler.IBuildResponse#getBuildErrors()
	 */
	public List<IBuildError> getBuildErrors()
	{
		return errors;
	}
	
	public void addError(IBuildError error)
	{
		errors.add(error);
	}
}
