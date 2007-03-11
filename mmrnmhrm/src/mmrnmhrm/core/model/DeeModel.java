package mmrnmhrm.core.model;

import java.util.HashMap;
import java.util.Map;

import mmrnmhrm.core.DeeCore;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;

import util.ArrayUtil;
import util.Assert;


/**
 * Represents the root of the D Model.
 * TODO: must use Resource listener 
 */
public class DeeModel {
	private static DeeModel deemodel = new DeeModel();
	
	public Map<String, DeeProject> deeProjects;
	
	private DeeModel() {
		deeProjects = new HashMap<String, DeeProject>();
	}

	/** Inits the D model. */
	public static void initDeeModel() throws CoreException {
		// Init the model with existing D projects.
		for(IProject proj : DeeCore.getWorkspaceRoot().getProjects()) {
			if(proj.hasNature(DeeNature.NATURE_FQID))
			getInstance().addDeeProjectToDeeModel(proj);
		}
	}
	
	/** @return the shared instance */
	public static DeeModel getInstance() {
		return deemodel;
	}

	/** Creates a D project in the given workspace project. */
	public void createDeeProject(IProject project) throws CoreException {
		addNature(project, DeeNature.NATURE_FQID);
		addDeeProjectToDeeModel(project);
		setDefaultBuildPath(project);
	}

	private void addNature(IProject project, String natureID) throws CoreException {
		IProjectDescription description = project.getDescription();
		String[] natures = description.getNatureIds();
		Assert.isTrue(ArrayUtil.contains(natures, natureID) == false);
		String[] newNatures = ArrayUtil.append(natures, natureID);
		description.setNatureIds(newNatures);
		project.setDescription(description, null); 
	}

	/** Adds D project to Dee Model. */
	private DeeProject addDeeProjectToDeeModel(IProject project) {
		DeeProject deeproj = new DeeProject();
		deeproj.loadDeeProject(project);
		deeProjects.put(project.getName(), deeproj);
		return deeproj;
	}
	

	private void setDefaultBuildPath(IProject project) throws CoreException {
		IFolder srcFolder = project.getFolder("src");
		srcFolder.create(false, true, null);
		IFolder binFolder = project.getFolder("bin");
		binFolder.create(false, true, null);
	}

	/** Returns the D project for given IProject, or null of none. */
	public static DeeProject getDeeProject(IProject project) {
		return getInstance().deeProjects.get(project.getName());
	}

	/**
	 * folder must exist.
	 */
	public void addToBuildPath(IFolder folder) throws CoreException {
		IProject project = folder.getProject();
		getDeeProject(project).addSourceFolder(folder);
	}
}
