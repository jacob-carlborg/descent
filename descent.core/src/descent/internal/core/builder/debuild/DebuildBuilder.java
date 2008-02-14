package descent.internal.core.builder.debuild;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

import descent.core.IJavaProject;
import descent.core.builder.IExecutableTarget;

/**
 * The main engine of the descent remote builder. Given an executable target
 * (info on what type of executable is needed) and a project, performs the
 * build. The publuc interface of this class can be accessed via the
 * {@link #build(IJavaProject, IExecutableTarget, IProgressMonitor)} method,
 * which will initiat a build.
 * 
 * @author Robert Fraser
 */
public class DebuildBuilder
{
	/**
	 * Public interface to the debuild builder, which initiates a new build
	 * based on the given build project an executable target. The target
	 * should contain information on what is to be built. Returns the path to
	 * the executable file if one is built (or already exists in the project),
	 * or null if there were compile/link errors or the project otherwise
	 * could not be built.
	 * 
	 * @param proj   the project to be built
	 * @param target information about the target executable to be built
	 * @param pm     a monitor to track the progress of the build
	 * @return       the path to the executable file, or null if the build
	 *               failed
	 */
	public static String build(IJavaProject proj,
			IExecutableTarget target,
			IProgressMonitor pm)
	{
		DebuildBuilder builder = new DebuildBuilder(
				new BuildRequest(proj, target));
		return builder.build(pm);
	}
	
	/* package */ static final boolean DEBUG = true;
	
	private final BuildRequest req;
	
	private DebuildBuilder(BuildRequest req)
	{
		this.req = req;
	}
	
	private String build(IProgressMonitor pm)
	{
		if(null == pm)
			pm = new NullProgressMonitor();
		
		return null;
	}
}
