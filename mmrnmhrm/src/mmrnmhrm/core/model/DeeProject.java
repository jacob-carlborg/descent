package mmrnmhrm.core.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

/**
 * Class for a D project. 
 */
public class DeeProject {

	private IProject project;
	private List<DeeSourceFolder> sourceFolders;
	private IFolder outputDir;

	
	public DeeProject() {
		sourceFolders = new ArrayList<DeeSourceFolder>();
	}
	
	public IProject getProject() {
		return project;
	}
	
	public IFolder getOutputDir() {
		return outputDir;
	}


	public List<DeeSourceFolder> getSourceFolders() {
		return sourceFolders;
	}

	
	public String toString() {
		return project.getName();
	}

	public String getOutputDirLocationString() {
		return outputDir.getLocation().toString();
	}

	/*public void setOutputDir(IFolder outputDir) {
		this.outputDir = outputDir;
	}*/

	public void loadDeeProject(IProject project) {
		this.project = project;
		loadProjectFromFile();
	}	
	
	public void addSourceFolder(IFolder folder) throws CoreException {
		sourceFolders.add(new DeeSourceFolder(folder));
	}
	
	public void removeSourceFolder(IFolder folder) throws CoreException {
		sourceFolders.remove(new DeeSourceFolder(folder));
	}

	private void loadProjectFromFile() {
		//Ini ini = new Ini(new FileReader("projc"));

		// TODO read from actual file
		DeeSourceFolder deesf = new DeeSourceFolder(project.getFolder("src"));
		sourceFolders.add(deesf);
		outputDir = project.getFolder("bin");
	}

	public boolean containsElement(Object element) {
		for(DeeSourceFolder sourceFolder : sourceFolders) {
			if(sourceFolder.folder.equals(element))
				return true;
		}
		return false;
	}

}
