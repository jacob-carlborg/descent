package descent.core.builder;

import org.eclipse.core.runtime.IProgressMonitor;

import descent.core.IJavaProject;

/**
 * Interface for providing information about a requested compile. The way
 * the Descent builder works is that object files are compiled continuously
 * by the build process (generally in the background if the user has Build
 * Automatically enabled). However, files are only linked into an executable
 * when the project is run. Different run configurations may need differently
 * linked targets. This interface exists to specify a target to link (i.e.
 * what module to use as main, what other modules are necessary, etc.).
 * 
 *  The purpose of doing the builder linke this is to faciuiltate integration
 *  into the Java-centric model of JDT. Since Java projects do not need to be
 *  linked at compile-time, JDT does not do any linking. Since D projects do,
 *  this iterface allows this to be done at runtime.
 *  
 *  To perform a link (which should generally be done when the project is
 *  launched), use the 
 *  {@link IJavaProject#getExecutableTarget(IExecutableTarget, IProgressMonitor)}
 *  method on the project, which will perform the link and return the path of
 *  the executable file. Keep in mind that creating an executable target may
 *  compile object files as well (for exampl, if the executable target requests
 *  additional modules from outside the project that have not been compiled).
 *
 * @author Robert Fraser
 */
public interface IExecutableTarget
{
	
}
