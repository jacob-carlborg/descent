package mmrnmhrm.ui;

import mmrnmhrm.core.DeePluginActivator;

import org.eclipse.jdt.internal.ui.viewsupport.ImageDescriptorRegistry;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;

public class DeeUI extends DeePluginActivator {


	private static ImageDescriptorRegistry imageDescriptorRegistry;

	public static synchronized ImageDescriptorRegistry getImageDescriptorRegistry() {
		if (imageDescriptorRegistry == null)
			imageDescriptorRegistry= new ImageDescriptorRegistry();
		return imageDescriptorRegistry;
	}
	
	public static IWorkbenchWindow getActiveWorkbenchWindow() {
		return getDefault().getWorkbench().getActiveWorkbenchWindow();
	}
	
	public static Shell getActiveWorkbenchShell() {
		 IWorkbenchWindow window= getActiveWorkbenchWindow();
		 if (window != null) {
		 	return window.getShell();
		 }
		 return null;
	}

}
