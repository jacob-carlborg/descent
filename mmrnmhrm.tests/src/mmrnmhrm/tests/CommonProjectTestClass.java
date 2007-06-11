package mmrnmhrm.tests;


import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.model.DeeModelManager;
import mmrnmhrm.core.model.DeeModelRoot;
import mmrnmhrm.core.model.DeeProject;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.junit.AfterClass;
import org.junit.BeforeClass;

public abstract class CommonProjectTestClass extends BaseUITestClass {

	protected static final String EXISTINGPROJNAME = "ExistingProj";
	protected static DeeProject sampleDeeProj = null;

	@BeforeClass
	public static void commonSetUp() throws Exception {
		IWorkspaceRoot workspaceRoot = DeeCore.getWorkspaceRoot();
		IProject project = workspaceRoot.getProject(EXISTINGPROJNAME);
		project.create(null);
		project.open(null);
		DeeModelRoot.getInstance().createDeeProject(project);
		sampleDeeProj = DeeModelManager.getLangProject(EXISTINGPROJNAME); 
	}

	@AfterClass
	public static void commonTearDown() throws Exception {
		IWorkspaceRoot workspaceRoot = DeeCore.getWorkspaceRoot();
		IProject project = workspaceRoot.getProject(EXISTINGPROJNAME);
		project.delete(true, null);
	}
	
	protected IWorkspaceRoot getWorkspaceRoot() {
		return ResourcesPlugin.getWorkspace().getRoot();
	}
	
	public void createRecursive(IFolder container, boolean force) throws CoreException {
		if(!container.getParent().exists()) {
			if(container.getParent().getType() == IResource.FOLDER)
				createRecursive((IFolder)container.getParent(), force);
		}
		container.create(force, true, null);
	}


}