package mmrnmhrm.core.model;

import java.util.ArrayList;

import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.IElementChangedListener;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

/**
 * The Dee Model. It's elements are not handle-based, nor cached like JDT.
 */
public class DeeModelManager {
	
	private static DeeModelManager deemodel = new DeeModelManager();
	
	/** @return the shared instance */
	public static DeeModelManager getInstance() {
		return deemodel;
	}
	
	public ArrayList<IElementChangedListener> elementChangedListeners 
		= new ArrayList<IElementChangedListener>(5);
	
	/** Registers an element change listener. */
	public synchronized void addElementChangedListener(IElementChangedListener listener, int eventMask) {
		elementChangedListeners.add(listener);
	}
	
	/** Unregisters an element change listener. */
	public synchronized void removeElementChangedListener(IElementChangedListener listener) {
		elementChangedListeners.remove(listener);
	}
	
	/** Notifies element change listener of model changes. */
	public void fireModelChanged() {
		// Watch out for listener add/remove while change notification is in progress.
		IElementChangedListener[] listeners;
		synchronized(this) {
			listeners = new IElementChangedListener[elementChangedListeners.size()];
			listeners = elementChangedListeners.toArray(listeners);
		}
		
		for(IElementChangedListener listener : listeners) {
			listener.elementChanged(null);
		}
	}

	
	/** Gets the D Model Root */
	public static DeeModelRoot getRoot() {
		return DeeModelRoot.getInstance();
	}

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
		DeeModelRoot.getInstance().addDeeProject(deeproj);
		return deeproj;
	}


	/** Returns the D project with the given name, null if none. */
	public static DeeProject getLangProject(String name) {
		return (DeeProject) getRoot().getLangProject(name);
	}
	
	/** Returns the D project for given project */
	public static DeeProject getLangProject(IProject project) {
		return (DeeProject) getRoot().getLangProject(project);
	}

	/** Creates a D project in the given existing workspace project. */
	public static DeeProject createDeeProject(IProject project) throws CoreException {
		return getRoot().createDeeProject(project);
	}

	/** Returns the Compilation for the given file. If the file is not part
	 * of a source root, returns an out of model Compilation Unit.*/
	public static CompilationUnit getCompilationUnit(IFile file) {
		CompilationUnit cunit = findMember(file);
		return cunit;
	}

	private static CompilationUnit findMember(IFile file) {
		IPath filepath = file.getProjectRelativePath();
		DeeProject deeproj = getLangProject(file.getProject());
		if(deeproj == null) return null;

		IDeeSourceRoot srcRoot = null;
		for (IDeeSourceRoot element : deeproj.getSourceRoots()) {
			IPath path = element.getUnderlyingResource().getProjectRelativePath();
			if(path.isPrefixOf(filepath)) {
				srcRoot = element;
				break;
			}
		}
		if(srcRoot == null) return null;

		PackageFragment pkgFrag = null;
		for (PackageFragment element : srcRoot.getPackageFragments()) {
			IPath path = element.getUnderlyingResource().getProjectRelativePath();
			if(path.equals(filepath.removeLastSegments(1))) {
				pkgFrag = element;
				break;
			}
		}
		if(pkgFrag == null) return null;

		CompilationUnit cunit = null;
		for (CompilationUnit element : pkgFrag.getCompilationUnits()) {
			IPath path = element.getUnderlyingResource().getProjectRelativePath();
			if(path.equals(filepath)) {
				cunit = element;
				break;
			}
		}
		return cunit;
	}

}
