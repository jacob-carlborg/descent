package melnorme.util.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;

public abstract class EditorActionDelegate implements IEditorActionDelegate {

	protected IEditorPart editor;
	protected IWorkbenchWindow window;

	
	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		editor = targetEditor;
		if(editor == null)
			return;
		window = editor.getSite().getWorkbenchWindow(); 
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

}