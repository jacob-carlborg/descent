package mmrnmhrm.ui.actions;

import melnorme.util.ui.actions.EditorActionDelegate;
import mmrnmhrm.core.model.CompilationUnit;
import mmrnmhrm.ui.editor.DeeEditor;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.ui.IEditorPart;

import util.Assert;
import util.log.Logg;
import dtool.dom.ast.ASTNode;
import dtool.dom.ast.ASTPrinter;

/**
 * Opens sets the editor cursor to the definition of the selected entity.
 */
public class GoToDefinitionActionDg extends EditorActionDelegate {

	public void setActiveEditor(IAction action, IEditorPart newEditor) {
		super.setActiveEditor(action, newEditor);
		if(editor != null)
		Assert.isTrue(newEditor instanceof DeeEditor, "Not a Mmrnmhrm DeeEditor.");
	}

	/** {@inheritDoc} */
	public void run(IAction action) {
			
		DeeEditor deeEditor = (DeeEditor) editor;
		
		TextSelection sel = deeEditor.getSelection();
		int offset = sel.getOffset();
		Logg.main.println("[" + sel.getOffset() +","+ sel.getLength() + "] =>" + offset);
		Logg.main.println(sel.getText());
		
		CompilationUnit cunit = deeEditor.getDocument().getCompilationUnit();

		ASTNode elem = cunit.findEntity(offset);
		if(elem == null) {
			dialogWarning("No element found at pos: " + offset);
			return;
		}
		System.out.println("FOUND: " + ASTPrinter.toStringNodeExtra(elem));
		GoToDefinitionOperation.execute(window, elem);
	
	}

	private void dialogWarning(String string) {
		MessageDialog.openWarning(window.getShell(),
				"Go to Definition",	string);
	}
	
}