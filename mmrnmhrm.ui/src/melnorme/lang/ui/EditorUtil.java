package melnorme.lang.ui;



import static melnorme.miscutil.Assert.assertNotNull;
import mmrnmhrm.core.dltk.DeeModuleDeclaration;
import mmrnmhrm.core.dltk.ParsingUtil;
import mmrnmhrm.ui.ActualPlugin;

import org.eclipse.core.resources.IProject;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.internal.ui.editor.EditorUtility;
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

import descent.internal.compiler.parser.ast.IASTNode;
import dtool.dom.definitions.Module;

public class EditorUtil {
	
	// ------------ used to editor ------------ 
	public static TextSelection getSelection(ITextEditor editor) {
		return (TextSelection) editor.getSelectionProvider().getSelection();
	}
	
	public static void setSelection(ITextEditor textEditor, IASTNode node) {
		textEditor.getSelectionProvider().setSelection(
				new TextSelection(node.getStartPos(), node.getLength())); 
	}
	
	public static void setSelection(ITextEditor textEditor, int offset, int length) {
		textEditor.getSelectionProvider().setSelection(
				new TextSelection(offset, length)); 
	}

	
	public static IEditorPart getActiveEditor() {
		IWorkbenchWindow window = ActualPlugin.getInstance().getWorkbench().getActiveWorkbenchWindow();
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
			IASTNode element = (IASTNode) sel.getFirstElement();
			
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
	
	public static ISourceModule getModuleUnit(ITextEditor textEditor) {
		IModelElement element = EditorUtility.getEditorInputModelElement(textEditor, false);
		if(!(element instanceof ISourceModule))
			return null;
		return (ISourceModule) element;
	}

	/** Gets a Module from this editor's input, and setups the Module'
	 * modUnit as the editor's input. */
	public static Module getNeoModuleFromEditor(ITextEditor textEditor) {
		IModelElement element = EditorUtility.getEditorInputModelElement(textEditor, false);
		if(!(element instanceof ISourceModule))
			return null;
		ISourceModule modUnit = (ISourceModule) element;
		DeeModuleDeclaration modDec = ParsingUtil.parseModule(modUnit);
		Module neoModule = ParsingUtil.getNeoASTModule(modDec);
		assertNotNull(neoModule.getModuleUnit());
		return neoModule;
	}

}
