package mmrnmhrm.core;

import mmrnmhrm.core.dltk.DLTKModelResolver;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.dltk.core.DLTKCore;
import org.osgi.framework.BundleContext;

import dtool.refmodel.EntityResolver;


public class DeeCore extends Plugin {

	/** The plug-in ID */
	public static final String PLUGIN_ID = "mmrnmhrm.core";
	/** The builder ID */
	public final static String BUILDER_ID = PLUGIN_ID + ".deebuilder";
	
	protected static DeeCore pluginInstance;
	
	public DeeCore() {
		pluginInstance = this;
	}
	
	/** Returns the shared instance. */
	public static DeeCore getInstance() {
		return pluginInstance;
	}

	/** {@inheritDoc} */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		initPlugin();
	}

	/** {@inheritDoc} */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		pluginInstance = null;
	}

	
	public void initPlugin() throws CoreException {
		//EntityResolver.initializeEntityResolver(DeeModel.getRoot());
		EntityResolver.initializeEntityResolver(DLTKModelResolver.instance);
		//TypeHierarchy.DEBUG = true;
	}


	/* *********************************************** */
	
	/** Convenience method to get the WorkspaceRoot. */
	public static IWorkspaceRoot getWorkspaceRoot() {
		return ResourcesPlugin.getWorkspace().getRoot();
	}
	
	/** Convenience method to get the Workspace. */
	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}

	/**
	 * See {@link DLTKCore#run(IWorkspaceRunnable, ISchedulingRule, IProgressMonitor)}
	 */
	public static void run(IWorkspaceRunnable action, ISchedulingRule rule, IProgressMonitor monitor) throws CoreException {
		DLTKCore.run(action, rule, monitor);
	}

	/** Runs {@link #run(IWorkspaceRunnable, ISchedulingRule, IProgressMonitor) }
	 * with workspace root as the rule. */
	public static void run(IWorkspaceRunnable action, IProgressMonitor monitor) throws CoreException {
		run(action, ResourcesPlugin.getWorkspace().getRoot(), monitor);
	}
	

	/** Logs the given exception, creating a new status for this plugin. */
	public static void log(Exception e) {
		getInstance().getLog().log(new Status(IStatus.ERROR, DeeCore.PLUGIN_ID,
				ILangModelConstants.INTERNAL_ERROR,
				LangCoreMessages.LangCore_internal_error, e));
	}

}
