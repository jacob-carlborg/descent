package mmrnmhrm.ui.wizards;

import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.model.DeeModelRoot;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

public class DeeProjectWizard extends NewElementWizard {

    private DeeProjectWizardFirstPage fFirstPage;
    private DeeProjectWizardSecondPage fSecondPage;
	
	public DeeProjectWizard() {
		setWindowTitle(DeeNewWizardMessages.LangNewProject_wizardTitle);
	}
	
    public void addPages() {
        super.addPages();
        fFirstPage= new DeeProjectWizardFirstPage();
        fSecondPage= new DeeProjectWizardSecondPage("Foo");
        addPage(fFirstPage);
        addPage(fSecondPage);
    }

	/** {@inheritDoc} */
	@Override
	protected void finishPage(IProgressMonitor monitor) throws CoreException {
		//fFirstPage.performFinish(monitor);
		createDeeProject(monitor);
	}

	private void createDeeProject(final IProgressMonitor monitor) throws CoreException {

		IWorkspaceRoot workspaceRoot = DeeCore.getWorkspaceRoot();
		IProject project = workspaceRoot.getProject(fFirstPage.getProjectName());
		project.create(monitor);
		project.open(monitor);

		DeeModelRoot.getInstance().createDeeProject(project);

	}

}
