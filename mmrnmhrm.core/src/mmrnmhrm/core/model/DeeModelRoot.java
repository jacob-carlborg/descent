package mmrnmhrm.core.model;

import mmrnmhrm.core.model.lang.LangModelRoot;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;


/**
 * Represents the root of the D Model.
 * TODO: use Resource listener 
 */
public class DeeModelRoot extends LangModelRoot {

	private static DeeModelRoot deemodel = new DeeModelRoot();
	
	/** @return the shared instance */
	public static DeeModelRoot getInstance() {
		return deemodel;
	}
	
	@Override
	public DeeProject[] newChildrenArray(int size) {
		return new DeeProject[size];
	}

	/** Creates a D project in the given existing workspace project. */
	public DeeProject createDeeProject(IProject project) throws CoreException {
		DeeNature.addNature(project, DeeNature.NATURE_ID);
	
		DeeProject deeproj = new DeeProject(project);
		deeproj.setDefaultBuildPath();
		deeproj.saveProjectConfigFile();
		addDeeProject(deeproj);
		return deeproj;
	}

	/** Adds an existing D project to the model. Refreshes the project.  */
	public void addDeeProject(DeeProject deeproj) throws CoreException {
		addChild(deeproj);
		deeproj.refreshElementChildren();
	}
	
	/** Returns all D projects in the D model. */
	public DeeProject[] getDeeProjects() {
		return (DeeProject[]) getChildren();
	}

	/** Removes a D project from the model. Does not delete workspace project. */
	public void removeDeeProject(DeeProject deeproject) throws CoreException {
		removeChild(deeproject);
	}
	
	/** Delete D project. Removes workspace project. */
	public void deleteDeeProject(DeeProject deeproject) throws CoreException {
		removeDeeProject(deeproject);
		deeproject.getProject().delete(false, null);
	}


}
