package mmrnmhrm.core.model;

import mmrnmhrm.core.DeeCore;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

/**
 * The Dee Model. It's elements are not handle-based, nor cached like JDT.
 */
public class DeeModelManager {

	/** Inits the D model. */
	public static void initDeeModel() throws CoreException {
		// Init the model with existing D projects.
		for(IProject proj : DeeCore.getWorkspaceRoot().getProjects()) {
			if(proj.hasNature(DeeNature.NATURE_ID))
			loadDeeProject(proj);
		}
	}
	
	/** Adds a D project from a resource project to Dee Model. */
	public static DeeProject loadDeeProject(IProject project) throws CoreException {
		DeeProject deeproj = new DeeProject(project);
		// Add the project to the model before loading
		deeproj.loadProjectConfigFile();
		DeeModelRoot.getInstance().addChild(deeproj);
		return deeproj;
	}


	/** Returns the D project for given project */
	public static DeeProject getLangProject(String name) {
		return (DeeProject) DeeModelRoot.getInstance().getLangProject(name);
	}
	
	/** Returns the D project for given project */
	public static DeeProject getLangProject(IProject project) {
		return (DeeProject) DeeModelRoot.getInstance().getLangProject(project);
	}
	

}
