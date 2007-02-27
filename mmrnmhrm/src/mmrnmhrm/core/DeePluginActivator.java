package mmrnmhrm.core;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public abstract class DeePluginActivator extends AbstractUIPlugin {

	// The plug-in ID XXX: Watch for changes
	public static final String PLUGIN_ID = "Mmrnmhrm";

	// The shared instance
	private static DeeCore plugin;
	
	/** The constructor */
	public DeePluginActivator() {
		plugin = (DeeCore) this;
	}
	

	/** @return the shared instance */
	public static DeeCore getDefault() {
		return plugin;
	}

	/** {@inheritDoc} */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		initPlugin();
	}

	/** {@inheritDoc} */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}


	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
	
	/**************************************************/

	void initPlugin() {
	}
	
}
