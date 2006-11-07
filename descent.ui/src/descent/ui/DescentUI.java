package descent.ui;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class DescentUI extends AbstractUIPlugin {

	/**
	 * The plugin ID.
	 */
	public static final String PLUGIN_ID = "descent.ui";
	
	/**
	 * The nature ID
	 */
	public static final String NATURE_ID = PLUGIN_ID + "." + "dnature";
	
	/**
	 * The builder ID
	 */
	public static final String BUILDER_ID = PLUGIN_ID + "." + "dbuilder";
	
	/**
	 * The preference keyword for the D root path.
	 */
	public static final String PREFERENCE_D_ROOT = "d.root";

	// The shared instance
	private static DescentUI plugin;
	
	
	
	/**
	 * The constructor
	 */
	public DescentUI() {
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
	public static DescentUI getDefault() {
		return plugin;
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
	
	/**
	 * Returns the path to the DMD compiler.
	 */
	public String getDMDCompilerPath() {
		return getPreferenceStore().getString(PREFERENCE_D_ROOT);
	}
	
	/**
	 * Log a status
	 * @param aStatus the status to log
	 */
	public static void log(IStatus aStatus) {
		getDefault().getLog().log(aStatus);
	}
	
	/**
	 * log an exception
	 * @param aThrowable the exception to log
	 */
	public static void log(Throwable aThrowable) {
		log(new Status(IStatus.ERROR, PLUGIN_ID, Status.OK,
						"Internal Error",
						aThrowable));
	}
	
	/**
	 * log an error message
	 * @param aMessage the message to log
	 */
	public static void logErrorMessage(String aMessage) {
		log(new Status(IStatus.ERROR, PLUGIN_ID, Status.OK, aMessage, null));
	}

	
	/**
	 * log an error status
	 * @param aMessage the message
	 * @param aStatus the status
	 */
	public static void logErrorStatus(String aMessage, IStatus aStatus) {
		if (aStatus == null) {
			logErrorMessage(aMessage);
		} else {
			MultiStatus multi = new MultiStatus(PLUGIN_ID, Status.OK,
											    aMessage, null);
			multi.add(aStatus);
			log(multi);
		}
	}
	
}
