package mmrnmhrm.core.model;

import melnorme.miscutil.ArrayUtil;
import mmrnmhrm.core.DeeCore;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;

public class ModelUtil {

	public static DeeProject getDeeProject(String name) {
		return new DeeProject(ModelUtil.getModel().getScriptProject(name));
	}

	/** Get's a sourceModule's file, apparently getUnderlyingResource()
	 * doesn't work all the time. */
	public static IFile getSourceModuleFile(ISourceModule srcModule) {
		//return (IFile) srcModule.getUnderlyingResource();
		return DeeCore.getWorkspaceRoot().getFile(srcModule.getPath());
	}
	
	/** Creates a SourceFolder for the given folder, and adds it the project. */
	public static IProjectFragment createAddSourceFolder(IScriptProject dltkProj, IFolder folder) throws CoreException {
		IProjectFragment fragment = dltkProj.getProjectFragment(folder);
		if(fragment == null || !fragment.exists()) {
			IBuildpathEntry[] bpentries = dltkProj.getRawBuildpath();
			IBuildpathEntry entry = DLTKCore.newSourceEntry(fragment.getPath());
			dltkProj.setRawBuildpath(ArrayUtil.concat(bpentries, entry), null);
		}
		return fragment;
	}

	public static void createDeeProject(IProject project) throws CoreException {
		ModelUtil.addNature(project, DeeNature.NATURE_ID);
	
		IScriptProject dltkProj = DLTKCore.create(project);
		dltkProj.setRawBuildpath(new IBuildpathEntry[0], null);
	}

	/** Adds a nature to the given project if it doesn't exist already.*/
	public static void addNature(IProject project, String natureID) throws CoreException {
		IProjectDescription description = project.getDescription();
		String[] natures = description.getNatureIds();
		if(ArrayUtil.contains(natures, natureID))
			return;
	
		String[] newNatures = ArrayUtil.append(natures, natureID);
		description.setNatureIds(newNatures);
		project.setDescription(description, null); 
	}

	@SuppressWarnings("restriction")
	public static org.eclipse.dltk.internal.core.Model getModel() {
		return org.eclipse.dltk.internal.core.ModelManager.
		getModelManager().getModel();
	}

}
