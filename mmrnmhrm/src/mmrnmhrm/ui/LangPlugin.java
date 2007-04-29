package mmrnmhrm.ui;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;


public abstract class LangPlugin extends AbstractUIPlugin {

	protected static LangPlugin pluginInstance;
	
	
	/** @return the shared instance */
	public static LangPlugin getInstance() {
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


	
	/* *********************************************** */

	abstract void initPlugin() throws CoreException;

	//abstract void getPluginId();

	public static String getPluginId() {
		return ActualPlugin.PLUGIN_ID;
	}
	
	/** Logs the given status. */
	public static void log(IStatus status) {
		getInstance().getLog().log(status);
	}
	
	/** Logs the given Throwable, wrapping it in a Status. */
	public static void log(Throwable e) {
		log(new Status(IStatus.ERROR, getPluginId(),
				ILangStatusConstants.INTERNAL_ERROR,
				LangUIMessages.JavaPlugin_internal_error, e)); 
	}
	

	/** Gets the active workbench window. */
	public static IWorkbenchWindow getActiveWorkbenchWindow() {
		return DeePlugin.getInstance().getWorkbench().getActiveWorkbenchWindow();
	}

	/** Gets the active workbench shell. */
	public static Shell getActiveWorkbenchShell() {
		 IWorkbenchWindow window= getActiveWorkbenchWindow();
		 if (window != null) {
		 	return window.getShell();
		 }
		 return null;
	}

	/** Gets the plugins preference store. */
	public static IPreferenceStore getPrefStore() {
		return getInstance().getPreferenceStore();
	}
}