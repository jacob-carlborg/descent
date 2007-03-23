package mmrnmhrm.ui.wizards;

import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.model.DeeModel;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import util.ExceptionAdapter;

public class DeeProjectWizard extends NewElementWizard {

    private JavaProjectWizardFirstPage fFirstPage;
    private DeeProjectWizardFirstPage fSecondPage;
	
	public DeeProjectWizard() {
	}
	
    public void addPages() {
        super.addPages();
        fFirstPage= new JavaProjectWizardFirstPage();
        fSecondPage= new DeeProjectWizardFirstPage("Foo");
        addPage(fSecondPage);
        addPage(fFirstPage);
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

		DeeModel.getInstance().createDeeProject(project);

	}

}
