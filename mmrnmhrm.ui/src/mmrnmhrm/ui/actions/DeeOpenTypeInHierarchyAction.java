package mmrnmhrm.ui.actions;

import mmrnmhrm.ui.DeeUILanguageToolkit;

import org.eclipse.dltk.internal.ui.actions.OpenTypeInHierarchyAction;
import org.eclipse.dltk.ui.IDLTKUILanguageToolkit;

@SuppressWarnings("restriction")
public class DeeOpenTypeInHierarchyAction extends OpenTypeInHierarchyAction {

	@Override
	protected IDLTKUILanguageToolkit getLanguageToolkit() {
		return DeeUILanguageToolkit.getDefault();
	}
}
