package descent.internal.launching.debuild;

import descent.core.IJavaProject;
import descent.launching.compiler.DmdCompilerInterface;
import descent.launching.compiler.ICompilerInterface;
import descent.launching.compiler.IExecutableTarget;

/**
 * Wrapper for information about a build request. Exactly one object
 * of this type will exist per DebuildBuilder, and this object should
 * generally simply serve as a wrapper for abstracting getting information
 * that the build needs. 
 *
 * @author Robert Fraser
 */
public class BuildRequest
{	
	/**
	 * The project being built.
	 */
	private final IJavaProject project;
	
	/**
	 * Information about the executable target to be built (is it debug?
	 * should we optimize? Add unit tests? etc., etc.)
	 */
	private final IExecutableTarget target; 
	
	public BuildRequest(IJavaProject project, IExecutableTarget target)
	{
		this.project = project;
		this.target = target;
	}
	
	public ICompilerInterface getCompilerInterface()
	{
		return DmdCompilerInterface.getInstance();
	}
}
