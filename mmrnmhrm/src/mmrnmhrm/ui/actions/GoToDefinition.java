package mmrnmhrm.ui.actions;

import mmrnmhrm.ui.editors.DeeEditor;
import mmrnmhrm.ui.editors.EditorUtil;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import util.Logger;
import dtool.dom.ast.ASTPrinter;
import dtool.dom.base.ASTNode;
import dtool.dom.base.DefUnit;
import dtool.dom.base.Entity;
import dtool.project.CompilationUnit;

/**
 * Opens a DeeEditor in a given target
 */
public class GoToDefinition implements IWorkbenchWindowActionDelegate {
	private IWorkbenchWindow window;

	public GoToDefinition() {
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
	
	/**
	 * The action has been activated. The argument of the
	 * method represents the 'real' action sitting
	 * in the workbench UI.
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	public void run(IAction action) {
		IEditorPart editor  = window.getActivePage().getActiveEditor();
		if(!(editor instanceof DeeEditor)) {
			MessageDialog.openWarning(window.getShell(),
					"Go to Definition",
					"Not a Mmrnmhrm Dee Editor.");
			return;
		}
			
		DeeEditor deeEditor = (DeeEditor) editor;
		
		TextSelection sel = deeEditor.getSelection();
		int offset = sel.getOffset();
		Logger.println("[" + sel.getOffset() +","+ sel.getLength() + "] =>" + offset);
		Logger.println(sel.getText());
		
		CompilationUnit cunit = deeEditor.getDocument().getCompilationUnit();

		ASTNode elem = cunit.findEntity(offset);
		if(elem == null) {
			dialogWarning("No element found at pos: " + offset);
			return;
		}
		System.out.println("FOUND: " + ASTPrinter.toStringElement(elem));
		if(elem instanceof Entity) {
			DefUnit defunit = ((Entity)elem).getTargetDefUnit();
			if(defunit == null) {
				dialogWarning("Definition not found for entity: " + elem);
				return;
			}
			EditorUtil.setSelection(deeEditor, defunit);
		} else if(elem instanceof DefUnit.Symbol) {
			dialogInfo("Already at definition of element: " + elem);
		} else {
			dialogInfo("Element is not an entity reference. (" + elem +")");
		} 

	}

	private void dialogWarning(String string) {
		MessageDialog.openWarning(window.getShell(),
				"Go to Definition",	string);
	}

	private void dialogInfo(String string) {
		MessageDialog.openInformation(window.getShell(),
				"Go to Definition",	string);
	}

}