package mmrnmhrm.core;

import mmrnmhrm.core.model.DeeModel;
import mmrnmhrm.text.DeeCodeScanner_Native;
import mmrnmhrm.text.DeeDocumentProvider;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.jface.text.rules.ITokenScanner;

import util.Assert;

public class DeeCore extends DeePluginActivator {

	private DeeDocumentProvider deeDocumentProvider;
	private ITokenScanner defaultDeeCodeScanner;
	
	public final static String BUILDER_ID = PLUGIN_ID + ".deebuilder";
	
	
	public static ITokenScanner getDefaultDeeCodeScanner() {
		return getDefault().defaultDeeCodeScanner;
	}
	protected void initPlugin() throws CoreException {
		deeDocumentProvider = new DeeDocumentProvider();
		defaultDeeCodeScanner = new DeeCodeScanner_Native();
		
		DeeModel.initDeeModel();
	}
	
	public static DeeDocumentProvider getDeeDocumentProvider() {
		return getDefault().deeDocumentProvider;
	}


	/** Convenience method to get WorkspaceRoot. */
	public static IWorkspaceRoot getWorkspaceRoot() {
		return ResourcesPlugin.getWorkspace().getRoot();
	}
	
	/** Convenience method to get Workspace. */
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
