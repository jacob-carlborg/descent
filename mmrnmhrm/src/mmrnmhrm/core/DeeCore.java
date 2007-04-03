package mmrnmhrm.core;

import mmrnmhrm.core.model.DeeModel;
import mmrnmhrm.ui.text.DeeCodeScanner;
import mmrnmhrm.ui.text.DeeDocumentProvider;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.ISchedulingRule;

import util.Assert;
import util.log.Logg;

public class DeeCore extends DeePluginActivator {

	private DeeDocumentProvider deeDocumentProvider;
	private DeeCodeScanner defaultDeeCodeScanner;
	
	public final static String BUILDER_ID = PLUGIN_ID + ".deebuilder";
	
	
	public static DeeCodeScanner getDefaultDeeCodeScanner() {
		return getInstance().defaultDeeCodeScanner;
	}
	
	protected void initPlugin() throws CoreException {
		Logg.println(" =============  Mmrnmhrm INITIALIZING  ============= " );
		Logg.println("Location: " + Platform.getLocation());
		Logg.println("Instance Location: " + Platform.getInstanceLocation().getURL());

		deeDocumentProvider = new DeeDocumentProvider();
		defaultDeeCodeScanner = new DeeCodeScanner();
		
		DeeModel.initDeeModel();
	}
	
	public static DeeDocumentProvider getDeeDocumentProvider() {
		return getInstance().deeDocumentProvider;
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
