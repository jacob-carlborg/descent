/**
 * 
 */
package mmrnmhrm.ui.wizards;

import java.util.Observable;

import mmrnmhrm.core.model.DeeNature;
import mmrnmhrm.ui.preferences.DeeCompilersPreferencePage;

import org.eclipse.dltk.debug.ui.DLTKDebugUIPlugin;
import org.eclipse.dltk.launching.IInterpreterInstall;
import org.eclipse.dltk.ui.wizards.ProjectWizardFirstPage;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.swt.widgets.Composite;

public class DeeProjectWizardFirstPage extends ProjectWizardFirstPage {

	RubyInterpreterGroup fInterpreterGroup;
	
	final class RubyInterpreterGroup extends AbstractInterpreterGroup {
		
		public RubyInterpreterGroup(Composite composite) {
			super (composite);
		}

		protected String getCurrentLanguageNature() {
			return DeeNature.NATURE_ID;
		}

		protected void showInterpreterPreferencePage() {
			IPreferencePage page = new DeeCompilersPreferencePage(); 
			DLTKDebugUIPlugin.showPreferencePage(DeeCompilersPreferencePage.PAGE_ID, page); 					
		}
    	
    };

    @Override
	protected void createInterpreterGroup(Composite parent) {
		fInterpreterGroup = new RubyInterpreterGroup(parent);
	}

    @Override
	protected Observable getInterpreterGroupObservable() {
		return fInterpreterGroup;
	}
	
	@Override
	protected boolean supportInterpreter() {
		return true;
	}

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
}