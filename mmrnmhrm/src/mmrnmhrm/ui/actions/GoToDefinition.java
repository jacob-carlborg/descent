package mmrnmhrm.ui.actions;

import mmrnmhrm.ui.deditor.DeeEditor;
import mmrnmhrm.ui.deditor.EditorUtil;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;

import util.Assert;
import util.Logger;
import dtool.dom.ast.ASTPrinter;
import dtool.dom.base.ASTNode;
import dtool.dom.base.DefUnit;
import dtool.dom.base.Entity;
import dtool.project.CompilationUnit;

/**
 * Opens sets the editor cursor to the definition of the selected entity.
 */
public class GoToDefinition implements IEditorActionDelegate {
	private IEditorPart editor;
	private IWorkbenchWindow window;


	public GoToDefinition() {
	}

	public void setActiveEditor(IAction action, IEditorPart newEditor) {
		Assert.isTrue(newEditor instanceof DeeEditor, "Not a Mmrnmhrm DeeEditor.");
		editor = newEditor;
		window = editor.getSite().getWorkbenchWindow(); 
	}

	/** {@inheritDoc} */
	public void run(IAction action) {
			
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
	

	public void selectionChanged(IAction action, ISelection selection) {
	}

	public void dispose() {
	}

}