package mmrnmhrm.ui.preferences;


import mmrnmhrm.core.model.DeeNature;
import mmrnmhrm.ui.preferences.pages.DeeCompilersPreferencePage;

import org.eclipse.dltk.debug.ui.launchConfigurations.IMainLaunchConfigurationTabListenerManager;
import org.eclipse.dltk.internal.debug.ui.interpreters.AbstractInterpreterComboBlock;

public class DeeCompilersComboBlock extends AbstractInterpreterComboBlock {
	
	public DeeCompilersComboBlock(IMainLaunchConfigurationTabListenerManager listenerManager) {
		super(listenerManager);
	}

	@Override
	protected void showInterpreterPreferencePage()  {
		showPrefPage(DeeCompilersPreferencePage.PAGE_ID);
	}

	@Override
	protected String getCurrentLanguageNature () {
		return DeeNature.NATURE_ID;
	}
	
}
