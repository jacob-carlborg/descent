package mmrnmhrm.core;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;

public class DeeUI extends DeePluginActivator {

	
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
