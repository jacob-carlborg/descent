package mmrnmhrm.ui.actions;

import melnorme.util.ui.actions.EditorActionDelegate;
import mmrnmhrm.core.model.CompilationUnit;
import mmrnmhrm.ui.editor.DeeEditor;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;

import dtool.dom.ast.ASTPrinter;

/**
 * DEBUG UTIL: Prints the AST in a message box.
 */
public class PrintAST extends EditorActionDelegate {

	public void run(IAction action) {
		IWorkbenchWindow window = editor.getSite().getWorkbenchWindow();

		if(editor instanceof DeeEditor) {
			DeeEditor deeEditor = (DeeEditor) editor;
			
			CompilationUnit cunit = deeEditor.getDocument().getCompilationUnit();
			MessageDialog.openInformation( window.getShell(),
					"Partitions",
					ASTPrinter.toStringAST(cunit.getModule()));
		}
	}

}