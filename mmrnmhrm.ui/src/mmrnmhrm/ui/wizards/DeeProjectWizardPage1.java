/**
 * 
 */
package mmrnmhrm.ui.wizards;

import java.util.Observable;

import mmrnmhrm.core.model.DeeNature;
import mmrnmhrm.ui.preferences.pages.DeeCompilersPreferencePage;
import mmrnmrhm.org.eclipse.dltk.ui.wizards.ProjectWizardFirstPage_;

import org.eclipse.dltk.debug.ui.DLTKDebugUIPlugin;
import org.eclipse.dltk.launching.IInterpreterInstall;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.swt.widgets.Composite;

public class DeeProjectWizardPage1 extends ProjectWizardFirstPage_ {

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
		protected void showInterpreterPreferencePage() {
			IPreferencePage page = new DeeCompilersPreferencePage(); 
			DLTKDebugUIPlugin.showPreferencePage(DeeCompilersPreferencePage.PAGE_ID, page); 					
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