package mmrnmhrm.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public abstract class DeePluginActivator extends AbstractUIPlugin {

	/// The plug-in ID 
	// XXX: Watch for changes
	public static final String PLUGIN_ID = "mmrnmhrm";

	private static DeeCore pluginInstance;
	
	/** The constructor */
	protected DeePluginActivator() {
		pluginInstance = (DeeCore) this;
	}
	

	/** @return the shared instance */
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


	
	/* *********************************************** */

	void initPlugin() throws CoreException {
	}
	
	
}
