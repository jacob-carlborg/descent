package melnorme.lang.ui;

import mmrnmhrm.ui.LangPlugin;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.texteditor.AbstractTextEditor;

import dtool.dom.ast.ASTNode;

public class EditorUtil {
	
	// ------------ used to editor ------------ 
	
	public static void setSelection(AbstractTextEditor textEditor, ASTNode node) {
		textEditor.getSelectionProvider().setSelection(
				new TextSelection(node.getStartPos(), node.getLength())); 
	}
	
	public static void setSelection(AbstractTextEditor textEditor, int offset, int length) {
		textEditor.getSelectionProvider().setSelection(
				new TextSelection(offset, length)); 
	}

	
	public static IEditorPart getActiveEditor() {
		IWorkbenchWindow window = LangPlugin.getInstance().getWorkbench().getActiveWorkbenchWindow();
		if (window != null) {
			IWorkbenchPage page = window.getActivePage();
			if (page != null) {
				return page.getActiveEditor();
			}
		}
		return null;
	}
	
	// ------------  Used by editor ------------ 
	
	public static IProject getProject(IEditorInput input) {
		IProject project = null;
		if (input instanceof IFileEditorInput) {
			project = ((IFileEditorInput)input).getFile().getProject();
		} 
		return project;
	}
}
