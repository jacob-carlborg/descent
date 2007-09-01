package mmrnmhrm.ui.launch;


import mmrnmhrm.core.model.DeeNature;
import mmrnmhrm.ui.preferences.pages.DeeCompilersPreferencePage;

import org.eclipse.dltk.internal.debug.ui.interpreters.AbstractInterpreterComboBlock;
import org.eclipse.jface.preference.IPreferencePage;

public class RubyInterpreterComboBlock extends AbstractInterpreterComboBlock {
	
	protected void showInterpreterPreferencePage()  { 
		IPreferencePage page = new DeeCompilersPreferencePage(); 
		showPrefPage(DeeCompilersPreferencePage.PAGE_ID, page); 
	}

	protected String getCurrentLanguageNature () {
		return DeeNature.NATURE_ID;
	}
	
}
