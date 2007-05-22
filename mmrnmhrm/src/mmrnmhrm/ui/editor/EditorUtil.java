package mmrnmhrm.ui.editor;

import mmrnmhrm.ui.LangPlugin;

import org.eclipse.jface.text.TextSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;

import dtool.dom.declarations.DefUnit;

public class EditorUtil {

	public static void setSelection(DeeEditor deeEditor, DefUnit defunit) {
		deeEditor.getSelectionProvider().setSelection(
				new TextSelection(defunit.getStartPos(), defunit.getLength())); 
	}
	
	public static void setSelection(DeeEditor deeEditor, int offset, int length) {
		deeEditor.getSelectionProvider().setSelection(
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
}
