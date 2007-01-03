package mmrnmhrm;

import mmrnmhrm.text.DeeDocumentProvider;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;

public class DeeCore extends DeeCoreActivator {


	private DeeDocumentProvider deeDocumentProvider;

		
	protected void initCore() {
		deeDocumentProvider = new DeeDocumentProvider();
	}
	
	public static DeeDocumentProvider getDeeDocumentProvider() {
		return getDefault().deeDocumentProvider;
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
