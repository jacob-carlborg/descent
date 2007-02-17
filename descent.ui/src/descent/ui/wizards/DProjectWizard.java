package descent.ui.wizards;

import java.net.URI;

import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import descent.core.JavaCore;
import descent.internal.ui.JavaPlugin;

public class DProjectWizard extends Wizard implements INewWizard {
	
	private DProjectWizardPage page;
	
	public DProjectWizard() {
		setWindowTitle("New D Project");
	}
	
	@Override
	public void addPages() {
		page = new DProjectWizardPage();
		addPage(page);
	}
	
	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}

	@Override
	public boolean performFinish() {
		String name = page.getName();
		IPath location = page.getLocation();
		URI locationURI = URIUtil.toURI(location);
		
		if (locationURI != null && ResourcesPlugin.getWorkspace().getRoot().getLocationURI().equals(locationURI)) {
			locationURI = null;
		}
		
		try {
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IWorkspaceRoot root = workspace.getRoot();
			
			IProject project = root.getProject(name);
			
			IProjectDescription description = workspace.newProjectDescription(name);
			if (!page.isUseDefaultLocation()) { 
				description.setLocationURI(locationURI);
			}
			
			project.create(description, null);
			
			if (!project.isOpen()) {
				project.open(null);
			}
			
			// Add nature
			description = project.getDescription();
			
			String[] natures = description.getNatureIds();
			String[] newNatures = new String[natures.length + 1];
			System.arraycopy(natures, 0, newNatures, 0, natures.length);
			newNatures[natures.length] = JavaCore.NATURE_ID;
			
			IStatus status = workspace.validateNatureSet(natures);
			// check the status and decide what to do
			if (status.getCode() != IStatus.OK) {
			  	throw new CoreException(new Status(IStatus.ERROR, JavaPlugin.getPluginId(), 1, "Error", null));
			}
			
			description.setNatureIds(newNatures);			
			project.setDescription(description, null);
			
			/* TODO 
			IFolder srcFolder = project.getFolder("src");
			if (!srcFolder.exists()) {
				srcFolder.create(true, false, null);
			}		
			*/	
		} catch (CoreException e) {
			JavaPlugin.log(e);
		}
		
		return true;
	}

}
