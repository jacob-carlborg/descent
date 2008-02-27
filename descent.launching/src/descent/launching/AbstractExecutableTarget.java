package descent.launching;

import descent.core.IJavaProject;

/**
 * Base class for an executable target. Although classes requesting a build need
 * only do so using an {@link IExecutableTarget} this class is a basic
 * implementation of an executable target designed to be subclassed/overriden
 * for specific purposes by plugins requesting a build.
 *
 * @author Robert Fraser
 */
public abstract class AbstractExecutableTarget implements IExecutableTarget
{
	private IJavaProject project;
	
	protected AbstractExecutableTarget()
	{
		
	}
	
	public IJavaProject getProject()
	{
		return project;
	}
	
	public void setProject(IJavaProject project)
	{
		this.project = project;
	}
}
