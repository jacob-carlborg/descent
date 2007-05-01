package mmrnmhrm.ui.wizards;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

public class DeeProjectWizard extends NewElementWizard {

    public DeeProjectWizardFirstPage fFirstPage;
    protected DeeProjectWizardSecondPage fSecondPage;
	
	public DeeProjectWizard() {
		setWindowTitle(DeeNewWizardMessages.LangNewProject_wizardTitle);
	}
	
    public void addPages() {
        super.addPages();
        fFirstPage = new DeeProjectWizardFirstPage();
        fSecondPage = new DeeProjectWizardSecondPage();
        addPage(fFirstPage);
        //addPage(fSecondPage);
    }

	/** {@inheritDoc} */
	@Override
	protected void finishPage(IProgressMonitor monitor) throws CoreException {
		fFirstPage.createDeeProject(monitor);
	}


}
