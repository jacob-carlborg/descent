package descent.internal.core.builder.debuild;

import descent.core.IJavaProject;
import descent.core.builder.ICompilerInterface;

/**
 * Represents a request for a debuild build. This class serves two purposes.
 * First, it serves as an aggregation of all the different peices of data
 * debuild needs to know before it can perform a build. Second, it serves
 * as a convience wrapper around project/environment information. Most of the
 * fields are public because this calss assumes, since this is an internal class,
 * that you know what you're doing when you create it. It also doesn't validate
 * a correct request. So be careful!
 * 
 * @author Robert Fraser
 */
public class BuildRequest
{
	/**
	 * The project being built.
	 */
	public IJavaProject project;
	
	/**
	 * Should error markers be added to the project if errors are found?
	 * (default true)
	 */
	public boolean reportErrors;
	
	public BuildRequest()
	{
		setDefaults();
	}
	
	public void setDefaults()
	{
		project = null;
		reportErrors = true;
	}
	
	public ICompilerInterface getCompilerInterface()
	{
		return DmdCompilerInterface.getInstance();
	}
}
