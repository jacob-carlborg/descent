package descent.internal.launching.debuild;

import descent.core.ICompilationUnit;
import descent.core.IJavaProject;
import descent.launching.IExecutableTarget;
import descent.launching.compiler.ICompilerInterface;

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
	
	/**
	 * Gets the compiler interface for the compiler for this project
	 */
	public ICompilerInterface getCompilerInterface()
	{
		// TODO
		return null;
	}
	
	/**
	 * Gets the Java project being built
	 */
	public IJavaProject getProject()
	{
		return target.getProject();
	}
	
	/**
	 * Gets all the compilation units that must be built for this target
	 */
	public ICompilationUnit[] getCompilationUnits()
	{
		// TODO
		return null;
	}
}
