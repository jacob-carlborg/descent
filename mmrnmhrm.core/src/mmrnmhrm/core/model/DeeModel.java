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

/**
 * The Dee Model. It's elements are not handle-based, nor cached like JDT.
 */
public class DeeModel {
	
	@SuppressWarnings("restriction")
	private static org.eclipse.dltk.internal.core.Model getModel() {
		return org.eclipse.dltk.internal.core.ModelManager.
		getModelManager().getModel();
	}
	
	
	/** Returns the D project with the given name, null if none. */
	@SuppressWarnings("restriction")
	public static DeeProject getLangProject(String name) {
		return new DeeProject(getModel().getScriptProject(name));
	}
	
	/** Returns the D project for given project */
	public static DeeProject getLangProject(IProject project) {
		return new DeeProject(DLTKCore.create(project));
	}
	
	public static void createDeeProject(IProject project) throws CoreException {
		DeeModel.addNature(project, DeeNature.NATURE_ID);

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

	/** Returns the Compilation for the given file. If the file is not part
	 * of the model, return null. */
	public static CompilationUnit findCompilationUnit(IFile file) throws CoreException {
		CompilationUnit cunit = findMember(file);
		return cunit;
	}

	private static CompilationUnit findMember(IFile file) throws CoreException {
		DeeProject deeproj = getLangProject(file.getProject());
		if(deeproj == null) return null;
		
		ISourceModule srcModule = DLTKCore.createSourceModuleFrom(file);
		return new CompilationUnit(srcModule, getSourceModuleFile(srcModule));
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




}
