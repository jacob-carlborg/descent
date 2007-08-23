package mmrnmhrm.ui.wizards;

import java.util.Observable;

import mmrnmhrm.core.model.DeeNature;
import mmrnmhrm.core.model.DeeProject;
import mmrnmhrm.ui.DeePlugin;
import mmrnmhrm.ui.preferences.DeeCompilersPreferencePage;
import mmrnmhrm.ui.properties.DeeBuildpathsBlock;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.debug.ui.DLTKDebugUIPlugin;
import org.eclipse.dltk.launching.IInterpreterInstall;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.ui.util.BusyIndicatorRunnableContext;
import org.eclipse.dltk.ui.util.IStatusChangeListener;
import org.eclipse.dltk.ui.wizards.BuildpathsBlock;
import org.eclipse.dltk.ui.wizards.NewElementWizard;
import org.eclipse.dltk.ui.wizards.ProjectWizardFirstPage;
import org.eclipse.dltk.ui.wizards.ProjectWizardSecondPage;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;

public class DeeProjectCreationWizard extends NewElementWizard {

	
	public static class DeeDtlkProjectWizardFirstPage extends ProjectWizardFirstPage {

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
	
	public static class DeeDtlkProjectWizardSecondPage extends ProjectWizardSecondPage {

		public DeeDtlkProjectWizardSecondPage(ProjectWizardFirstPage mainPage) {
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
	
	protected ProjectWizardFirstPage fFirstPage;
    protected ProjectWizardSecondPage fSecondPage;
    protected DeeProject deeProject;
    
	private IConfigurationElement fConfigElement;
    
	public DeeProjectCreationWizard() {
		//setDefaultPageImageDescriptor(RubyImages.DESC_WIZBAN_PROJECT_CREATION);
		setDialogSettings(DLTKUIPlugin.getDefault().getDialogSettings());
		setWindowTitle(DeeNewWizardMessages.LangNewProject_wizardTitle);
	}
	
	public void setInitializationData(IConfigurationElement cfig, String propertyName, Object data) {
		fConfigElement = cfig;
	}

	public void addPages() {
        fFirstPage = new DeeDtlkProjectWizardFirstPage();
        fSecondPage = new DeeDtlkProjectWizardSecondPage(fFirstPage);
        addPage(fFirstPage);
        addPage(fSecondPage);
	}

	@Override
	public IModelElement getCreatedElement() {
		return DLTKCore.create(fFirstPage.getProjectHandle());
	}
	
	protected void finishPage(IProgressMonitor monitor) throws InterruptedException, CoreException {
		fSecondPage.performFinish(monitor); // use the full progress monitor
	}

	
	public boolean performFinish() {
		boolean res = super.performFinish();
		if (res) {
			BasicNewProjectResourceWizard.updatePerspective(fConfigElement);
			selectAndReveal(fSecondPage.getScriptProject().getProject());
		}
		return res;
	}
	
	public boolean performCancel() {
		fSecondPage.performCancel();
		return super.performCancel();
	}

}
