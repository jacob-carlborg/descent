package descent.launching.compiler;

import org.eclipse.core.runtime.IProgressMonitor;

import descent.core.IJavaProject;

/**
 * Interface for providing information about a requested compile. The way the
 * Descent builder works is that it doesn't actually do any building of binary
 * files during the standard Eclipse build cycle. Instead, it waits for a run
 * to be invoked that depends on an executable, then builds that executable then
 * (if it's not built already). To invoke a build, use the
 * {@link IJavaProject#getExecutableTarget(IExecutableTarget, IProgressMonitor)}
 * method.
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
	
}
