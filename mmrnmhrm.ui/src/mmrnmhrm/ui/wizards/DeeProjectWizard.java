package mmrnmhrm.ui.wizards;

import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.DeeCoreException;
import mmrnmhrm.core.model.DeeModel;
import mmrnmhrm.core.model.DeeNature;
import mmrnmhrm.core.model.DeeProject;
import mmrnmhrm.core.model.lang.LangElement;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
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
        fSecondPage = new DeeProjectWizardSecondPage();
        addPage(fFirstPage);
        addPage(fSecondPage);
    }
    

	public LangElement getDeeProject() {
		return deeProject;
	}

	/** {@inheritDoc} */ @Override
	public boolean performCancel() {
		if(deeProject != null)
			performPage2GoBack();
		return true;
	}

	
	boolean performPage2Entry() {
		boolean result = performWizardOperation(new WizardOperation() {
			protected void execute(IProgressMonitor monitor) throws CoreException {
				createDeeProject(monitor);
			}
		});
		if(deeProject != null)
			fSecondPage.init(deeProject);
		return result;
	}

	public void createDeeProject(final IProgressMonitor monitor) throws CoreException {
		IWorkspaceRoot workspaceRoot = DeeCore.getWorkspaceRoot();
		IProject project;
		project = workspaceRoot.getProject(fFirstPage.getProjectName());
		if(!fFirstPage.fLocationGroup.isInWorkspace()) {
			//TODO: allow creating in external location
			throw new DeeCoreException("External location creation not supported yet", null);
		}
		project.create(monitor);
		project.open(monitor);
		DeeNature.addNature(project, DeeNature.NATURE_ID);
		
		DeeProject deeproj = new DeeProject(project);
		deeproj.setDefaultBuildPath();
		deeproj.saveProjectConfigFile();
		DeeModel.getRoot().addDeeProject(deeproj);
		deeProject = deeproj;
	}
	
	
	boolean performPage2GoBack() {
		return performWizardOperation(new WizardOperation() {
			protected void execute(IProgressMonitor monitor) throws CoreException {
				if(deeProject != null)
					deleteDeeProject(monitor);
			}
		});
	}
	
	public void deleteDeeProject(final IProgressMonitor monitor) throws CoreException {
		try {
			DeeModel.getRoot().removeDeeProject(deeProject);
			deeProject.getProject().delete(false, monitor);
		} finally {
			deeProject = null;
		}
	}
	
	
	/** {@inheritDoc} */ @Override
	protected void doFinish(IProgressMonitor monitor) throws CoreException {
		if(deeProject == null) {
			createDeeProject(monitor);
		} else {
			fSecondPage.projectConfigBlock.applyConfig();
		}
	}
}
