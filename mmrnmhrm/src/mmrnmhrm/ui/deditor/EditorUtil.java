package mmrnmhrm.ui.deditor;

import org.eclipse.jface.text.TextSelection;

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

}
