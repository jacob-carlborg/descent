package descent.internal.launching.debuild;

import descent.launching.compiler.ICompilerInterface;
import descent.launching.target.IExecutableTarget;

/**
 * Wrapper for information about a build request. Exactly one object
 * of this type will exist per DebuildBuilder, and this object should
 * generally simply serve as a wrapper for abstracting getting information
 * that the build needs from the {@link IExecutableTarget}.
 *
 * @author Robert Fraser
 */
public class BuildRequest
{	
	
	/**
	 * Information about the executable target to be built (is it debug?
	 * should we optimize? Add unit tests? etc., etc.)
	 */
	private final IExecutableTarget target; 
	
	public BuildRequest(IExecutableTarget target)
	{
		this.target = target;
	}
	
	public ICompilerInterface getCompilerInterface()
	{
		// TODO
		return null;
	}
}
