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
	
	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path, optionally using the missing image descriptor.
	 */
	public static ImageDescriptor getImageDescriptor(String path, boolean useMissingDesc) {
		ImageDescriptor imgDesc = getImageDescriptor(path);
		if(imgDesc == null)
			return ImageDescriptor.getMissingImageDescriptor();
		return imgDesc;
	}

}
