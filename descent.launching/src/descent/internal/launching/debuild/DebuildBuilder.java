package descent.internal.launching.debuild;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

import descent.core.ICompilationUnit;
import descent.launching.IExecutableTarget;
import descent.launching.BuildProcessor.BuildCancelledException;
import descent.launching.BuildProcessor.BuildFailedException;

/**
 * The main engine of the descent remote builder. Given an executable target
 * (info on what type of executable is needed) and a project, performs the
 * build. The publuc interface of this class can be accessed via the
 * {@link #build(IExecutableTarget, IProgressMonitor)} method,
 * which will initiat a build.
 * 
 * @author Robert Fraser
 */
public class DebuildBuilder
{
	/**
	 * Public interface to the debuild builder, which initiates a new build
	 * based on the given executable target. The target
	 * should contain information on what is to be built. Returns the path to
	 * the executable file if one is built (or already exists in the project).
	 * Will always return non-null, and will throw either a BuildFailedException
	 * or a BuildCancelledException if the buikd does not succeed.
	 * 
	 * @param target information about the target executable to be built
	 * @param pm     a monitor to track the progress of the build
	 * @return       the path to the executable file,
	 */
	public static String build(IExecutableTarget target, IProgressMonitor pm)
	{
		DebuildBuilder builder = new DebuildBuilder(new BuildRequest(target));
		return builder.build(pm);
	}
	
	/* package */ static final boolean DEBUG = true;
	
	private final BuildRequest req;
	private final ErrorReporter err;
	
	private DebuildBuilder(BuildRequest req)
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
		
		try
		{
			pm.beginTask("Building D application", 1000);
			
			System.out.println("Okay, we buildin'!");
			
			for(ICompilationUnit cu : req.getCompilationUnits())
				System.out.println(cu.getFullyQualifiedName());
			
			// TODO
			throw new BuildFailedException();
		}
		finally
		{
			pm.done();
		}
	}
}
