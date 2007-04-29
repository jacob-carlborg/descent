package mmrnmhrm.core;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;

import util.Assert;

/**
 * Lang Core
 */
public abstract class LangCore /*extends Plugin */{


	protected static LangCore pluginInstance;
	
	/** Returns the shared instance. */
	public static LangCore getInstance() {
		return pluginInstance;
	}

	/** {@inheritDoc} */
	/*public void start(BundleContext context) throws Exception {
		super.start(context);
		initPlugin();
	}*/

	/** {@inheritDoc} */
	/*public void stop(BundleContext context) throws Exception {
		super.stop(context);
		pluginInstance = null;
	}*/


	/* *********************************************** */

	abstract void initPlugin() throws CoreException;
	
	/** Convenience method to get the WorkspaceRoot. */
	public static IWorkspaceRoot getWorkspaceRoot() {
		return ResourcesPlugin.getWorkspace().getRoot();
	}
	
	/** Convenience method to get the Workspace. */
	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}

	/**
	 * Runs the given action as an atomic D model operation.
	 * <p>
	 * If this method is called in the dynamic scope of another such call, this
	 * method simply runs the action.
	 * </p> <p>
	 * The supplied scheduling rule is used to determine whether this operation
	 * can be run simultaneously with workspace changes in other threads. 
	 * </p>
	 * @param action the action to perform
	 * @param rule the scheduling rule to use when running this operation, or
	 *            <code>null</code> if there are no scheduling restrictions
	 *            for this operation.
	 */
	public static void run(IWorkspaceRunnable action, ISchedulingRule rule, IProgressMonitor monitor) throws CoreException {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		if (workspace.isTreeLocked()) {
			Assert.fail("DUNNO: if workspace is locked, then don't run Lang ops?");
			//new BatchOperation(action).run(monitor);
		} else {
			// use IWorkspace.run(...) to ensure that a build will be done in autobuild mode
			workspace.run(new BatchOperation(action), rule, IWorkspace.AVOID_UPDATE, monitor);
		}
	}

	/** Runs {@link #run(IWorkspaceRunnable, ISchedulingRule, IProgressMonitor) }
	 * with workspace root as the rule. */
	public static void run(IWorkspaceRunnable action, IProgressMonitor monitor) throws CoreException {
		run(action, ResourcesPlugin.getWorkspace().getRoot(), monitor);
	}
	
}
