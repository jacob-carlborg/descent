package mmrnmhrm.core.model;

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
	public void createDeeProject(IProject project) throws CoreException {
		DeeNature.addNature(project, DeeNature.NATURE_ID);
	
		DeeProject deeproj = new DeeProject(project);
		deeproj.setDefaultBuildPath();
		deeproj.saveProjectConfigFile();
		addChild(deeproj);
	}
	
	/** Returns all D projects in the D model. */
	public DeeProject[] getDeeProjects() {
		return (DeeProject[]) getChildren();
	}

}
