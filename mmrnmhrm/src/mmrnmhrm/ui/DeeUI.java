package mmrnmhrm.ui;

import org.eclipse.jface.resource.ImageDescriptor;

/**
 * UI specific plugin methods.
 */
public class DeeUI  extends DeePlugin {


	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return DeePlugin.imageDescriptorFromPlugin(DeePlugin.PLUGIN_ID, path);
	}

}
