package mmrnmhrm.core;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;

import util.Assert;

public class DeeCore extends LangCore {

	/** The plug-in ID. XXX: Watch for changes */
	public static final String PLUGIN_ID = "mmrnmhrm";
	/** Builder ID */
	public final static String BUILDER_ID = PLUGIN_ID + ".deebuilder";

	public DeeCore() {
		pluginInstance = this;
	}
	

	/** Convenience method to get the WorkspaceRoot. */
	public static IWorkspaceRoot getWorkspaceRoot() {
		return ResourcesPlugin.getWorkspace().getRoot();
	}
	
	/** Convenience method to get the Workspace. */
	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}
	
	/**
	 * Runs the given action as an atomic D model operation. (TODO)
	 */
	public static void run(IWorkspaceRunnable action, ISchedulingRule rule, IProgressMonitor monitor) throws CoreException {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		if (workspace.isTreeLocked()) {
			Assert.fail("I DUNNO :(");
		} else {
			workspace.run(action, rule, IWorkspace.AVOID_UPDATE, monitor);
		}
	}

}
