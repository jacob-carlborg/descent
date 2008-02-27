package descent.internal.launching.rebuild;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

import descent.launching.IExecutableTarget;
import descent.launching.BuildProcessor.BuildCancelledException;
import descent.launching.BuildProcessor.BuildFailedException;

/**
 * Main builder class for building an executable target. Uses Gregor Richards'
 * Rebuild to perform the actual build (since otherwise Descent would have to
 * load and pase library files -- using Rebuild helps separate library
 * requirements from project requirements).
 * 
 * This class should only be accessed by 
 * {@link descent.launching.BuildProcessor} via the static
 * {@link #build(IExecutableTarget, IProgressMonitor)} method.
 *
 * @author Robert Fraser
 */
public class RebuildBuilder
{
	/**
	 * Public interface to the debuild builder, which initiates a new build
	 * based on the given executable target. The target
	 * should contain information on what is to be built. Returns the path to
	 * the executable file if one is built (or already exists in the project).
	 * Throws an exception of either {@link BuildFailedException} or
	 * {@link BuildCancelledException} if the build was unable to complete.
	 * Will never return null.
	 * 
	 * @param target information about the target executable to be built
	 * @param pm     a monitor to track the progress of the build
	 * @return       the path to the executable file
	 */
	public static String build(IExecutableTarget target, IProgressMonitor pm)
	{
		RebuildBuilder builder = new RebuildBuilder(new BuildRequest(target));
		return builder.build(pm);
	}
	
	/* package */ static final boolean DEBUG = true;
	
	private final BuildRequest req;
	private final ErrorReporter err;
	
	private RebuildBuilder(BuildRequest req)
	{
		this.req = req;
		this.err = new ErrorReporter(req.getProject());
	}
	
	private String build(IProgressMonitor pm)
	{
		if(null == pm)
			pm = new NullProgressMonitor();
		
		if(pm.isCanceled())
			throw new BuildCancelledException();
		
		throw new BuildFailedException();
	}
}
