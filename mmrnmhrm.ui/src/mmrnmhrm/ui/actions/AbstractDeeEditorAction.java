package mmrnmhrm.ui.actions;

import mmrnmhrm.ui.editor.DeeEditor;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;

import util.Assert;

public abstract class AbstractDeeEditorAction extends Action implements IEditorActionDelegate {

	public DeeEditor deeEditor;
	
	public AbstractDeeEditorAction(String text) {
		super(text);
	}

	
	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		if(targetEditor != null)
			Assert.isTrue(targetEditor instanceof DeeEditor, "Not a Mmrnmhrm DeeEditor.");
		deeEditor = (DeeEditor) targetEditor;
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}
	
	public void run(IAction action) {
		run();
	}

}
