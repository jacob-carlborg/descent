/**
 * 
 */
package mmrnmhrm.ui.wizards;

import java.util.Observable;

import mmrnmhrm.core.model.DeeNature;
import mmrnmhrm.ui.preferences.pages.DeeCompilersPreferencePage;

import org.eclipse.dltk.launching.IInterpreterInstall;
import org.eclipse.dltk.ui.wizards.ProjectWizardFirstPage;
import org.eclipse.swt.widgets.Composite;

public class DeeProjectWizardPage1 extends ProjectWizardFirstPage {

	DeeInterpreterGroup fInterpreterGroup;
	
	final class DeeInterpreterGroup extends AbstractInterpreterGroup {
		
		public DeeInterpreterGroup(Composite composite) {
			super (composite);
		}

		@Override
		protected String getCurrentLanguageNature() {
			return DeeNature.NATURE_ID;
		}

		@Override
		protected String getIntereprtersPreferencePageId() {
			return DeeCompilersPreferencePage.PAGE_ID;
		}
    	
    };

    @Override
	protected void createInterpreterGroup(Composite parent) {
		fInterpreterGroup = new DeeInterpreterGroup(parent);
	}

    @Override
	protected Observable getInterpreterGroupObservable() {
		return fInterpreterGroup;
	}
	
	@Override
	protected boolean supportInterpreter() {
		return true;
	}

	@Override
	protected IInterpreterInstall getInterpreter() {
		return fInterpreterGroup.getSelectedInterpreter();
	}

	@Override
	protected void handlePossibleInterpreterChange() {
		fInterpreterGroup.handlePossibleInterpreterChange();
	}

	@Override
	protected boolean interpeterRequired() {
		return false;
	}
	
	@Override
	public boolean isSrc() {
		return true; // Create src+bin buildpath
	}
}