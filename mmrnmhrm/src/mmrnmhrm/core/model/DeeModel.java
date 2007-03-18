package mmrnmhrm.core.model;

import java.util.HashMap;
import java.util.Map;

import mmrnmhrm.core.DeeCore;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;



/**
 * Represents the root of the D Model.
 * TODO: must use Resource listener 
 */
public class DeeModel extends AbstractLanguageModel {
	private static DeeModel deemodel = new DeeModel();
	
	public Map<String, DeeProject> deeProjects;
	
	private DeeModel() {
		deeProjects = new HashMap<String, DeeProject>();
	}

	/** @return the shared instance */
	public static DeeModel getInstance() {
		return deemodel;
	}

	/** Inits the D model. */
	public static void initDeeModel() throws CoreException {
		// Init the model with existing D projects.
		for(IProject proj : DeeCore.getWorkspaceRoot().getProjects()) {
			if(proj.hasNature(DeeNature.NATURE_FQID))
			getInstance().loadDeeProject(proj);
		}
	}
	
	/** Creates a D project in the given workspace project. */
	public void createDeeProject(IProject project) throws CoreException {
		addNature(project, DeeNature.NATURE_FQID);
	
		DeeProject deeproj = new DeeProject();
		deeproj.setProject(project);
		deeproj.setDefaultBuildPath();
		deeproj.saveProjectConfigFile();
		deeProjects.put(project.getName(), deeproj);
	}

	/** Adds a D project from a resource project to Dee Model. */
	private DeeProject loadDeeProject(IProject project) throws CoreException {
		DeeProject deeproj = new DeeProject();
		deeproj.setProject(project);
		deeproj.loadProjectConfigFile();
		deeProjects.put(project.getName(), deeproj);
		return deeproj;
	}
	

	/** Returns the D project for given IProject, or null of none. */
	public static DeeProject getDeeProject(IProject project) {
		return getInstance().deeProjects.get(project.getName());
	}

	/** folder must exist. */
	public void addSourceFolderToBuildPath(IFolder folder) throws CoreException {
		IProject project = folder.getProject();
		getDeeProject(project).addSourceFolder(folder);
		getDeeProject(project).saveProjectConfigFile();
	}
}
