package melnorme.lang.ui;



import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.ITextEditor;

import descent.internal.compiler.parser.ast.ASTNode;

public class EditorUtil {
	
	// ------------ used to editor ------------ 
	public static TextSelection getSelection(ITextEditor editor) {
		return (TextSelection) editor.getSelectionProvider().getSelection();
	}
	
	public static void setSelection(ITextEditor textEditor, ASTNode node) {
		textEditor.getSelectionProvider().setSelection(
				new TextSelection(node.getStartPos(), node.getLength())); 
	}
	
	public static void setSelection(ITextEditor textEditor, int offset, int length) {
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

	public static void selectNodeInEditor(AbstractTextEditor editor, SelectionChangedEvent event) {
		ISelection selection = event.getSelection();
		if (selection.isEmpty())
			editor.resetHighlightRange();
		else {
			IStructuredSelection sel = (IStructuredSelection) selection;
			ASTNode element = (ASTNode) sel.getFirstElement();
			
			if(element.hasNoSourceRangeInfo())
				return;
			
			int start = element.getOffset();
			int end = element.getLength();
			try {
				editor.setHighlightRange(start, end, true);
				setSelection(editor, start, end);
			} catch (IllegalArgumentException x) {
				editor.resetHighlightRange();
			}
		}
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
