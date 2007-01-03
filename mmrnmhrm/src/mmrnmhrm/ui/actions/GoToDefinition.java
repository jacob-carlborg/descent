package mmrnmhrm.ui.actions;

import mmrnmhrm.text.DebugPartitioner;
import mmrnmhrm.ui.editors.DeeEditor;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * Opens a DeeEditor in a given target
 */
public class GoToDefinition implements IWorkbenchWindowActionDelegate {
	private IWorkbenchWindow window;

	public GoToDefinition() {
	}

	/**
	 * The action has been activated. The argument of the
	 * method represents the 'real' action sitting
	 * in the workbench UI.
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	public void run(IAction action) {
		IEditorPart editor  = window.getActivePage().getActiveEditor();
		if(editor instanceof DeeEditor) {
			DeeEditor deeEditor = (DeeEditor) editor;
			//deeEditor.sourceViewerConfiguration.get
			
			MessageDialog.openInformation( window.getShell(),
					"Partitions",
					DebugPartitioner.toStringPartitions(deeEditor.getDocument()));
		}
	}

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

	/**
	 * We will cache window object in order to
	 * be able to provide parent shell for the message dialog.
	 * @see IWorkbenchWindowActionDelegate#init
	 */
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}
}