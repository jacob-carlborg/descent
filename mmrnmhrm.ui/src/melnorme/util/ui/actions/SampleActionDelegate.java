package melnorme.util.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.jface.dialogs.MessageDialog;

public class SampleActionDelegate implements IWorkbenchWindowActionDelegate,
		IObjectActionDelegate, IEditorActionDelegate, IViewActionDelegate {
	private IWorkbenchWindow window;
	
	
	/**
	 * Selection in the workbench has been changed. We 
	 * can change the state of the 'real' action here
	 * if we want, but this can only happen after 
	 * the delegate has been created.
	 * @see IWorkbenchWindowActionDelegate#selectionChanged
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

	/**
	 * We can use this method to dispose of any system
	 * resources we previously allocated.
	 * @see IWorkbenchWindowActionDelegate#dispose
	 */
	public void dispose() {
	}

		public void init(IWorkbenchWindow window) {
		this.window = window;
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		if(targetPart != null)
		this.window = targetPart.getSite().getWorkbenchWindow();
	}

	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		if(targetEditor != null)
		this.window = targetEditor.getSite().getWorkbenchWindow();
	}

	public void init(IViewPart view) {
		if(view != null)
		this.window = view.getSite().getWorkbenchWindow();
	}

	/**
	 * The action has been activated. The argument of the
	 * method represents the 'real' action sitting
	 * in the workbench UI.
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	public void run(IAction action) {
		MessageDialog.openInformation(
			window.getShell(),
			"Wasdasd Plug-in",
			"Hello, Eclipse world");
	}


}