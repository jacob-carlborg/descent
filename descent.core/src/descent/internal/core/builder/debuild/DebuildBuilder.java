package descent.internal.core.builder.debuild;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

/**
 * The main engine of the descent remote builder. Takes a {@link BuildRequest},
 * manages the state of the build, and performs the build itself. Use the
 * {@link #build(BuildRequest, IProgressMonitor)} method to initiate a build.
 * 
 * @author Robert Fraser
 */
public class DebuildBuilder
{
	/**
	 * Public interface to the debuild builder, which initiates a new build
	 * based on the given build request. The request should hold all the
	 * relevant information rather than passing a million things to this
	 * method.
	 * 
	 * @param req the build request to initiate building on
	 * @param pm  a monitor to track the progress of the build
	 */
	public static void build(BuildRequest req, IProgressMonitor pm)
	{
		DebuildBuilder builder = new DebuildBuilder(req);
		builder.build(pm);
	}
	
	private final BuildRequest req;
	
	private DebuildBuilder(BuildRequest req)
	{
		this.req = req;
	}
	
	private void build(IProgressMonitor pm)
	{
		if(null == pm)
			pm = new NullProgressMonitor();
	}
}
