package descent.launching.ui;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import descent.internal.launching.ui.Images;

/**
 * The activator class controls the plug-in life cycle
 */
public class DescentLaunchingUI extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "descent.launching.ui";

	// The shared instance
	private static DescentLaunchingUI plugin;
	
	/**
	 * The constructor
	 */
	public DescentLaunchingUI() {
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		
		getImageRegistry().put(Images.MAIN_TAB, createImageDescriptor(new Path(Images.MAIN_TAB)));
		getImageRegistry().put(Images.VARIABLE_TAB, createImageDescriptor(new Path(Images.VARIABLE_TAB)));
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
	public static DescentLaunchingUI getDefault() {
		return plugin;
	}
	
	/*
	 * Since 3.1.1. Load from icon paths with $NL$
	 */
	public ImageDescriptor createImageDescriptor(IPath path) {
		URL url= FileLocator.find(getBundle(), path, null);
		if (url != null) {
			return ImageDescriptor.createFromURL(url);
		}
		return null;
	}

}
