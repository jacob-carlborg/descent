package mmrnmhrm.ui.editors;

import org.eclipse.jface.text.TextSelection;

import dtool.dom.base.DefUnit;

public class EditorUtil {

	public static void setSelection(DeeEditor deeEditor, DefUnit defunit) {
		deeEditor.getSelectionProvider().setSelection(
				new TextSelection(defunit.getStartPos(), defunit.getLength())); 
	}

}
