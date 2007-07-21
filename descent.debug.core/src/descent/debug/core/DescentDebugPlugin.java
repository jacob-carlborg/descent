package descent.debug.core;

import java.text.MessageFormat;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import descent.core.IJavaModel;
import descent.core.IJavaProject;
import descent.core.JavaCore;
import descent.internal.debug.core.DebuggerRegistry;

/**
 * The activator class controls the plug-in life cycle
 */
public class DescentDebugPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "descent.debug.core";

	// The shared instance
	private static DescentDebugPlugin plugin;
	private static IDebuggerRegistry debuggerRegistry;
	
	/**
	 * The constructor
	 */
	public DescentDebugPlugin() {
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static DescentDebugPlugin getDefault() {
		return plugin;
	}
	
	/**
	 * Convenience method to get the java model.
	 */
	private static IJavaModel getJavaModel() {
		return JavaCore.create(ResourcesPlugin.getWorkspace().getRoot());
	}
	
	/**
	 * Return the <code>IJavaProject</code> referenced in the specified configuration or
	 * <code>null</code> if none.
	 *
	 * @exception CoreException if the referenced Java project does not exist
	 * @since 2.0
	 */
	public static IJavaProject getJavaProject(ILaunchConfiguration configuration) throws CoreException {
		String projectName = configuration.getAttribute(IDescentLaunchConfigurationConstants.ATTR_PROJECT_NAME, (String)null);
		if ((projectName == null) || (projectName.trim().length() < 1)) {
			return null;
		}			
		IJavaProject javaProject = getJavaModel().getJavaProject(projectName);
		if (javaProject != null && javaProject.getProject().exists() && !javaProject.getProject().isOpen()) {
			abort(MessageFormat.format("Launch configuration {0} references closed project {1}", new String[] {configuration.getName(), projectName}), IDescentLaunchConfigurationConstants.ERR_PROJECT_CLOSED, null); 
		}
		if ((javaProject == null) || !javaProject.exists()) {
			abort(MessageFormat.format("Launch configuration {0} references non-existing project {1}.", new String[] {configuration.getName(), projectName}), IDescentLaunchConfigurationConstants.ERR_NOT_A_JAVA_PROJECT, null); 
		}
		return javaProject;
	}
	
	/**
	 * Returns the debugger registry.
	 * @return the debugger registry
	 */
	public static IDebuggerRegistry getDebuggerRegistry() {
		if (debuggerRegistry == null) {
			debuggerRegistry = new DebuggerRegistry();
		}
		return debuggerRegistry;
	}
	
	/**
	 * Returns the current configured debugger.
	 */
	public static IDebuggerDescriptor getCurrentDebugger() {
		String debuggerId = getDefault().getPluginPreferences().getString(IDescentLaunchingPreferenceConstants.DEBUGGER_ID);
		return getDebuggerRegistry().findDebugger(debuggerId);
	}
	
	/**
	 * Throws a core exception with an internal error status.
	 * 
	 * @param message the status message
	 * @param exception lower level exception associated with the
	 *  error, or <code>null</code> if none
	 */
	private static void abort(String message, Throwable exception) throws CoreException {
		abort(message, IDescentLaunchConfigurationConstants.ERR_INTERNAL_ERROR, exception);
	}	
		
		
	/**
	 * Throws a core exception with an internal error status.
	 * 
	 * @param message the status message
	 * @param code status code
	 * @param exception lower level exception associated with the
	 * 
	 *  error, or <code>null</code> if none
	 */
	private static void abort(String message, int code, Throwable exception) throws CoreException {
		throw new CoreException(new Status(IStatus.ERROR, "PLUGIN_ID", code, message, exception));
	}	

}
