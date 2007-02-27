package mmrnmhrm.ui.actions;

import mmrnmhrm.ui.deditor.DeeEditor;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;

import dtool.dom.ast.ASTPrinter;
import dtool.project.CompilationUnit;

/**
 * DEBUG UTIL: Prints the AST in a message bug.
 */
public class PrintAST implements IEditorActionDelegate {
	private IEditorPart editor;

	public PrintAST() {
	}
	
	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		editor = targetEditor;
	}

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

	public void dispose() {
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

}