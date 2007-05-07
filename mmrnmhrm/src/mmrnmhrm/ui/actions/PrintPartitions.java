package mmrnmhrm.ui.actions;

import mmrnmhrm.ui.editor.DeeEditor;
import mmrnmhrm.ui.text.DebugPartitioner;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;

/**
 */
public class PrintPartitions implements IEditorActionDelegate {
	private IEditorPart editor;

	public PrintPartitions() {
	}
	

	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		editor = targetEditor;
	}

	/** {@inheritDoc} */
	public void run(IAction action) {
		IWorkbenchWindow window = editor.getSite().getWorkbenchWindow();
		

		if(editor instanceof DeeEditor) {
			DeeEditor deeEditor = (DeeEditor) editor;
			
			MessageDialog.openInformation( window.getShell(),
					"Partitions",
					DebugPartitioner.toStringPartitions(deeEditor.getDocument()));
		}
	}


	public void dispose() {
	}


	public void selectionChanged(IAction action, ISelection selection) {
	}
}