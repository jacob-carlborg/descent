package mmrnmhrm.ui;

import mmrnmhrm.core.DeePluginActivator;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;

public class DeeUI extends DeePluginActivator {

	
	public static IWorkbenchWindow getActiveWorkbenchWindow() {
		return getInstance().getWorkbench().getActiveWorkbenchWindow();
	}
	
	public static Shell getActiveWorkbenchShell() {
		 IWorkbenchWindow window= getActiveWorkbenchWindow();
		 if (window != null) {
		 	return window.getShell();
		 }
		 return null;
	}
	

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

}
