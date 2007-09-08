package mmrnmhrm.ui.wizards;


import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.ui.wizards.NewElementWizard;
import org.eclipse.dltk.ui.wizards.ProjectWizardFirstPage;
import org.eclipse.dltk.ui.wizards.ProjectWizardSecondPage;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;

public class DeeProjectCreationWizard extends NewElementWizard {

	protected ProjectWizardFirstPage fFirstPage;
    protected ProjectWizardSecondPage fSecondPage;
    protected DeeProjectWizardPage3 fThirdPage;
    
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
	
	protected void finishPage(IProgressMonitor monitor) throws InterruptedException, CoreException {
		fSecondPage.performFinish(monitor); // use the full progress monitor
		fThirdPage.performOk();
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
		fThirdPage.performCancel();
		return super.performCancel();
	}

}
