package mmrnmhrm.core.model;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;

import util.ArrayUtil;



public class DeeModel {
	private static DeeModel deemodel = new DeeModel();
	
	private DeeModel() {}
	
	/** @return the shared instance */
	public static DeeModel getDefault() {
		return deemodel;
	}

	/** Creates a D project in the given workspace project. */
	public void createDeeProject(IProject project) throws CoreException {
		IProjectDescription description = project.getDescription();
		String[] natures = description.getNatureIds();
		String[] newNatures = ArrayUtil.append(natures, DeeProject.NATURE_FQID);
		description.setNatureIds(newNatures);
		project.setDescription(description, null);
	}
}
