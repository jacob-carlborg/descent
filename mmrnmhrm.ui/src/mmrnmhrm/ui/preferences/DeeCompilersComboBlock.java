package mmrnmhrm.ui.preferences;


import mmrnmhrm.core.model.DeeNature;
import mmrnmhrm.ui.preferences.pages.DeeCompilersPreferencePage;

import org.eclipse.dltk.internal.debug.ui.interpreters.AbstractInterpreterComboBlock;
import org.eclipse.jface.preference.IPreferencePage;

public class DeeCompilersComboBlock extends AbstractInterpreterComboBlock {
	
	@Override
	protected void showInterpreterPreferencePage()  { 
		IPreferencePage page = new DeeCompilersPreferencePage(); 
		showPrefPage(DeeCompilersPreferencePage.PAGE_ID, page); 
	}

	@Override
	protected String getCurrentLanguageNature () {
		return DeeNature.NATURE_ID;
	}
	
}
