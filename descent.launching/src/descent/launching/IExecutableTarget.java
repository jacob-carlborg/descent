package descent.launching;

import java.util.Set;

import descent.core.ICompilationUnit;
import descent.core.IJavaProject;

/**
 * Interface for providing information about a requested compile. The way the
 * Descent builder works is that it doesn't actually do any building of binary
 * files during the standard Eclipse build cycle. Instead, it waits for a run
 * to be invoked that depends on an executable, then builds that executable then
 * (if it's not built already).
 * 
 * This class provides a means for specifying what sort of build you want to
 * invoke. Generally, each launch configuration for a D project will need a
 * different <code>IExecutableTarget</code> implementation that specifies
 * information specific to that launch type.
 *
 * @author Robert Fraser
 */
public interface IExecutableTarget
{
	/**
	 * Gets the project currently being built.
	 */
	public IJavaProject getProject();
	
	/**
	 * Gets the list of compilation units that must be built into this project. The set should
	 * not be modified after a call to this method has been made
	 */
	public Set<ICompilationUnit> getCompilationUnits();
}
