package mmrnmhrm.ui.preferences;

import org.eclipse.dltk.internal.debug.ui.interpreters.InterpreterPreferencePage;
import org.eclipse.dltk.internal.debug.ui.interpreters.InterpretersBlock;

public class DeeCompilersPreferencePage extends InterpreterPreferencePage {
	
	public final static String PAGE_ID = "mmrnmhrm.ui.preferences.DeeCompilers";

	public InterpretersBlock createInterpretersBlock() {
		return new DeeCompilersBlock();
	}
}
