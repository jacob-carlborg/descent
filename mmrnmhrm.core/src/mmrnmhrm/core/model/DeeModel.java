package mmrnmhrm.core.model;

import java.util.ArrayList;

import mmrnmhrm.core.IElementChangedListener;
import mmrnmhrm.core.model.lang.LangElement;
import mmrnmhrm.core.model.lang.LangPackageFragment;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

/**
 * The Dee Model. It's elements are not handle-based, nor cached like JDT.
 */
public class DeeModel {
	
	private static DeeModel deemodel = new DeeModel();
	
	/** @return the shared instance */
	public static DeeModel getInstance() {
		return deemodel;
	}
	
	public static ArrayList<IElementChangedListener> elementChangedListeners 
		= new ArrayList<IElementChangedListener>(5);
	

	
	/** Registers an element change listener. */
	public static synchronized void addElementChangedListener(IElementChangedListener listener) {
		elementChangedListeners.add(listener);
	}
	
	/** Unregisters an element change listener. */
	public static synchronized void removeElementChangedListener(IElementChangedListener listener) {
		elementChangedListeners.remove(listener);
	}
	
	/** Notifies element change listener of model changes. */
	public static void fireModelChanged() {
		// Watch out for listener add/remove while change notification is in progress.
		IElementChangedListener[] listeners;
		synchronized(elementChangedListeners) {
			listeners = new IElementChangedListener[elementChangedListeners.size()];
			listeners = elementChangedListeners.toArray(listeners);
		}
		
		for(IElementChangedListener listener : listeners) {
			listener.elementChanged(null);
		}
	}

	/** Initializes the D model. */
	public static void initDeeModel() throws CoreException {
		getRoot().createElementInfo();
	}
	
	/** Gets the D Model Root */
	public static DeeModelRoot getRoot() {
		return DeeModelRoot.getInstance();
	}
	
	/** Adds a D project from a resource project to Dee Model. */
	public static LangElement loadDeeProject(IProject project) throws CoreException {
		return DeeModelRoot.getInstance().loadDeeProject(project);
	}

	/** Returns the D project with the given name, null if none. */
	public static DeeProject getLangProject(String name) {
		return (DeeProject) getRoot().getLangProject(name);
	}
	
	/** Returns the D project for given project */
	public static DeeProject getLangProject(IProject project) {
		return (DeeProject) getRoot().getLangProject(project);
	}

	/** Returns the Compilation for the given file. If the file is not part
	 * of the model, return null. */
	public static CompilationUnit findCompilationUnit(IFile file) throws CoreException {
		CompilationUnit cunit = findMember(file);
		return cunit;
	}

	private static CompilationUnit findMember(IFile file) throws CoreException {
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

		LangPackageFragment pkgFrag = null;
		for (LangPackageFragment element : srcRoot.getPackageFragments()) {
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
