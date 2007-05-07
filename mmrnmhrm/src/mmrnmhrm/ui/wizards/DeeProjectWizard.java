package mmrnmhrm.ui.wizards;

import mmrnmhrm.core.model.DeeProject;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

public class DeeProjectWizard extends NewElementWizard {

    public DeeProjectWizardFirstPage fFirstPage;
    protected DeeProjectWizardSecondPage fSecondPage;
    protected DeeProject deeProject;
	
	public DeeProjectWizard() {
		setWindowTitle(DeeNewWizardMessages.LangNewProject_wizardTitle);
	}
	
    public void addPages() {
        super.addPages();
        fFirstPage = new DeeProjectWizardFirstPage();
        //fSecondPage = new DeeProjectWizardSecondPage();
        addPage(fFirstPage);
        //addPage(fSecondPage);
    }

	/** {@inheritDoc} */
	@Override
	protected void finishPage(IProgressMonitor monitor) throws CoreException {
		if(deeProject == null) {
			fFirstPage.createDeeProject(monitor);
		}
		
		//TODO: select and reveal?
	}

	public DeeProject getDeeProject() {
		return deeProject;
	}

}
