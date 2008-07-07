/**
 * 
 */
package mmrnmhrm.ui.wizards;

import mmrnmhrm.core.model.DeeNature;
import mmrnmhrm.ui.DeePlugin;
import mmrnmhrm.ui.preferences.DeeBuildpathsBlock;

import org.eclipse.dltk.ui.util.BusyIndicatorRunnableContext;
import org.eclipse.dltk.ui.util.IStatusChangeListener;
import org.eclipse.dltk.ui.wizards.BuildpathsBlock;
import org.eclipse.dltk.ui.wizards.ProjectWizardFirstPage;
import org.eclipse.dltk.ui.wizards.ProjectWizardSecondPage;
import org.eclipse.jface.preference.IPreferenceStore;

public class DeeProjectWizardPage2 extends ProjectWizardSecondPage {

	public DeeProjectWizardPage2(ProjectWizardFirstPage mainPage) {
		super(mainPage);
	}

	@Override
	protected IPreferenceStore getPreferenceStore() {
		return DeePlugin.getInstance().getPreferenceStore();
	}

	@Override
	protected BuildpathsBlock createBuildpathBlock(IStatusChangeListener listener) {
		return new DeeBuildpathsBlock(new BusyIndicatorRunnableContext(),
				listener, 0, useNewSourcePage(), null);
	}

	@Override
	protected String getScriptNature() {
		return DeeNature.NATURE_ID;
	}
	
	
}