package mmrnmhrm.ui.search;

import mmrnmhrm.core.dltk.DeeLanguageToolkit;

import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.internal.ui.search.ScriptSearchPage;

@SuppressWarnings("restriction")
public class DeeSearchPage extends ScriptSearchPage {
	@Override
	protected IDLTKLanguageToolkit getLanguageToolkit() {
		return DeeLanguageToolkit.getDefault();
	}
}
