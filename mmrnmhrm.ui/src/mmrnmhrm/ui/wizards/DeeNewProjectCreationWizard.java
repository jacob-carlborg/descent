package mmrnmhrm.ui.wizards;


import mmrnmhrm.org.eclipse.dltk.ui.wizards.ProjectWizardFirstPage_;
import mmrnmhrm.org.eclipse.dltk.ui.wizards.ProjectWizardSecondPage_;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.ui.wizards.NewElementWizard;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;

public class DeeNewProjectCreationWizard extends NewElementWizard {

	protected ProjectWizardFirstPage_ fFirstPage;
    protected ProjectWizardSecondPage_ fSecondPage;
    protected DeeProjectWizardPage3 fThirdPage;
    
	private IConfigurationElement fConfigElement;
    
	public DeeNewProjectCreationWizard() {
		//setDefaultPageImageDescriptor(RubyImages.DESC_WIZBAN_PROJECT_CREATION);
		setDialogSettings(DLTKUIPlugin.getDefault().getDialogSettings());
		setWindowTitle(DeeNewWizardMessages.LangNewProject_wizardTitle);
	}
	
	
	/*
	 * Stores the configuration element for the wizard. The config element will
	 * be used in <code>performFinish</code> to set the result perspective.
	 */
	/*public void setInitializationData(IConfigurationElement cfig, String propertyName, Object data) {
		fConfigElement = cfig;
	}*/

	@Override
	public void addPages() {
        fFirstPage = new DeeProjectWizardPage1();
        fSecondPage = new DeeProjectWizardPage2(fFirstPage);
        fThirdPage = new DeeProjectWizardPage3(fSecondPage);
        addPage(fFirstPage);
        addPage(fSecondPage);
        //addPage(fThirdPage); // because secondPage breaks if is not last page
	}

	@Override
	public IModelElement getCreatedElement() {
		return DLTKCore.create(fFirstPage.getProjectHandle());
	}
	
	@Override
	protected void finishPage(IProgressMonitor monitor) throws InterruptedException, CoreException {
		fSecondPage.performFinish(monitor); // use the full progress monitor
		//fThirdPage.performOk();
	}

	@Override
	public boolean performFinish() {
		boolean res = super.performFinish();
		if (res) {
			BasicNewProjectResourceWizard.updatePerspective(fConfigElement);
			selectAndReveal(fSecondPage.getScriptProject().getProject());
		}
		return res;
	}
	
	@Override
	public boolean performCancel() {
		fSecondPage.performCancel();
		//fThirdPage.performCancel();
		return super.performCancel();
	}

}