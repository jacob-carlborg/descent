package mmrnmhrm.ui.actions;

import mmrnmhrm.ui.editors.DeeEditor;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import dtool.dom.ast.ASTPrinter;
import dtool.project.CompilationUnit;

/**
 * DEBUG UTIL: Prints the AST in a message bug.
 */
public class PrintAST implements IWorkbenchWindowActionDelegate {
	private IWorkbenchWindow window;

	public PrintAST() {
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
			
			CompilationUnit cunit = deeEditor.getDocument().getCompilationUnit();
			MessageDialog.openInformation( window.getShell(),
					"Partitions",
					ASTPrinter.toStringAST(cunit.getModule()));
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