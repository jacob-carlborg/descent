package mmrnmhrm.ui;

import static melnorme.miscutil.Assert.assertNotNull;
import static melnorme.miscutil.Assert.assertTrue;

import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.internal.ui.editor.EditorUtility;
import org.eclipse.ui.texteditor.ITextEditor;

import dtool.ast.definitions.Module;

public class ModelOperationUtil {

	// XXX
	public static void prepareModuleForOperation(Module module, ITextEditor editor) {
		ISourceModule modUnit = (ISourceModule) EditorUtility.
		getEditorInputModelElement(editor, false);
		assertNotNull(modUnit);
		
		ISourceModule currentModUnit = module.getModuleUnit();
		if(currentModUnit == null)
			module.setModuleUnit(modUnit);
		else {
			assertTrue(currentModUnit.equals(modUnit));
		}
	}

}
