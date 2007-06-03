package mmrnmhrm.ui.actions;

import melnorme.util.ui.actions.EditorActionDelegate;
import mmrnmhrm.ui.editor.DeeEditor;
import mmrnmhrm.ui.text.DebugPartitioner;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;

/**
 * DEBUG UTIL
 */
public class PrintPartitions extends EditorActionDelegate {

	/** {@inheritDoc} */
	public void run(IAction action) {

		if(editor instanceof DeeEditor) {
			DeeEditor deeEditor = (DeeEditor) editor;
			
			MessageDialog.openInformation(window.getShell(),
					"Partitions",
					DebugPartitioner.toStringPartitions(deeEditor.getDocument()));
		}
	}

}