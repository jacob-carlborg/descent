package mmrnmhrm.ui.wizards;


import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.ui.wizards.GenericDLTKProjectWizard;
import org.eclipse.dltk.ui.wizards.NewElementWizard;
import org.eclipse.dltk.ui.wizards.ProjectWizardFirstPage;
import org.eclipse.dltk.ui.wizards.ProjectWizardSecondPage;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;

/**
 * D New Project Wizard.
 * See also {@link GenericDLTKProjectWizard}
 */
public class DeeNewProjectWizard extends NewElementWizard implements IExecutableExtension {
	
	public static final String WIZARD_ID = "mmrnmhrm.ui.wizards.deeProjectWizard";
	
	protected ProjectWizardFirstPage fFirstPage;
    protected ProjectWizardSecondPage fSecondPage;
    protected DeeProjectWizardPage3 fThirdPage;
    
	private IConfigurationElement fConfigElement;
    
	public DeeNewProjectWizard() {
		super();
		//setDefaultPageImageDescriptor(RubyImages.DESC_WIZBAN_PROJECT_CREATION);
		setDialogSettings(DLTKUIPlugin.getDefault().getDialogSettings());
		setWindowTitle(DeeNewWizardMessages.LangNewProject_wizardTitle);
	}
	
	@Override
	public void setInitializationData(IConfigurationElement cfig, String propertyName, Object data) {
		/* Stores the configuration element for the wizard. The config element will
		 * be used in <code>performFinish</code> to set the result perspective. */
		fConfigElement = cfig;
	}
	
	@Override
	public void addPages() {
        fFirstPage = new DeeProjectWizardPage1();
        fSecondPage = new DeeProjectWizardPage2(fFirstPage);
        fThirdPage = new DeeProjectWizardPage3(fSecondPage);
        addPage(fFirstPage);
        addPage(fSecondPage);
        //addPage(fThirdPage); // XXX: Page3 disabled because secondPage breaks if it is not last page
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
	
	@Override
	public IModelElement getCreatedElement() {
		return DLTKCore.create(fFirstPage.getProjectHandle());
	}
	
}
