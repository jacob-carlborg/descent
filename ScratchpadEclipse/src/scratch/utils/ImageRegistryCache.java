package scratch.utils;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * A simple class for caching ImageDescriptors
 * It is not synchronized internally.
 */
public class ImageRegistryCache {
	
	private Map<String, ImageDescriptor> myImageDescriptors = new LinkedHashMap<String, ImageDescriptor>();

	public ImageDescriptor getImageDescriptor(String pluginId, String imagePath) {
		final String imageKey = pluginId + "##" + imagePath;
		ImageDescriptor imageDescriptor = myImageDescriptors.get(imageKey);
		if (imageDescriptor == null) {
			imageDescriptor = AbstractUIPlugin.imageDescriptorFromPlugin(pluginId, imagePath);
			if (imageDescriptor != null) {
				myImageDescriptors.put(imageKey, imageDescriptor);
			} else {
				throw new RuntimeException("Unable to find image descriptor: " + imagePath);
			}
		}
		return imageDescriptor;
	}

}