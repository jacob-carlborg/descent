package mmrnmhrm.core.model;

import java.util.ArrayList;

import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.IElementChangedListener;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

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
		DeeModelRoot.getInstance().addChild(deeproj);
		return deeproj;
	}


	/** Returns the D project for given project */
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
	

}
