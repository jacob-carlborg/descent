package descent.launching;

import java.util.HashSet;
import java.util.Set;

import descent.core.ICompilationUnit;
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
	private final Set<ICompilationUnit> compilationUnits = new HashSet<ICompilationUnit>();
	
	protected AbstractExecutableTarget()
	{
		ICompilationUnit[] defaultCompilationUnits = getDefaultCompilationUnits();
		if(null != defaultCompilationUnits)
			for(ICompilationUnit cu : defaultCompilationUnits)
				compilationUnits.add(cu);
	}
	
	public IJavaProject getProject()
	{
		return project;
	}
	
	public void setProject(IJavaProject project)
	{
		this.project = project;
	}
	
	public void addCompilationUnit(ICompilationUnit cu)
	{
		compilationUnits.add(cu);
	}
	
	public void removeCompilationUnit(ICompilationUnit cu)
	{
		compilationUnits.remove(cu);
	}
	
	public ICompilationUnit[] getCompilationUnits()
	{
		return compilationUnits.toArray(new ICompilationUnit[compilationUnits.size()]);
	}
	
	protected ICompilationUnit[] getDefaultCompilationUnits()
	{
		return null;
	}
}
