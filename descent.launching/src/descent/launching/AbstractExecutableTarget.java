package descent.launching;

import java.util.HashSet;
import java.util.Set;

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
	private final Set<String> modules = new HashSet<String>();
	
	protected AbstractExecutableTarget()
	{
		String[] defaultModules = getDefaultModules();
		if(null != defaultModules)
			for(String moduleName : defaultModules)
				modules.add(moduleName);
	}
	
	public IJavaProject getProject()
	{
		return project;
	}
	
	public void setProject(IJavaProject project)
	{
		this.project = project;
	}
	
	public void addModule(String moduleName)
	{
		modules.add(moduleName);
	}
	
	public void removeModule(String moduleName)
	{
		modules.remove(moduleName);
	}
	
	public String[] getModules()
	{
		return modules.toArray(new String[modules.size()]);
	}
	
	protected String[] getDefaultModules()
	{
		return null;
	}
}
