package mmrnmhrm.core.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

public class DeeProject0 {


	public IProject project;
	public List<DeeSourceFolder> sourceFolders;
	public IFolder outputDir;

	
	public DeeProject0() {
		sourceFolders = new ArrayList<DeeSourceFolder>();
	}
	
	/** {@inheritDoc} */
	public IProject getProject() {
		return project;
	}

	/** {@inheritDoc} */
	public void setProject(IProject project) {
		this.project = project;
		loadBuildPathFromFile();
	}
	

	public String getOutputDirLocationString() {
		return outputDir.getLocation().toString();
	}
	
	@Override
	public String toString() {
		return project.getName();
	}

	
	
	public void addSourceFolder(IFolder folder) throws CoreException {
		sourceFolders.add(new DeeSourceFolder(folder));
	}
	
	public void removeSourceFolder(IFolder folder) throws CoreException {
		sourceFolders.remove(new DeeSourceFolder(folder));
	}

	private void loadBuildPathFromFile() {
		// TODO read from actual file
		DeeSourceFolder deesf = new DeeSourceFolder(project.getFolder("src"));
		sourceFolders.add(deesf);
		outputDir = project.getFolder("bin");
	}

	
}
