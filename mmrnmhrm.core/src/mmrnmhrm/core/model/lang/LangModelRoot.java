package mmrnmhrm.core.model.lang;


import mmrnmhrm.core.model.DeeProject;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;


public abstract class LangModelRoot extends LangContainerElement {
	
	public LangModelRoot() {
		super(null);
	}
	
	
	public String getElementName() {
		return "/";
	}
	
	public int getElementType() {
		return ELangElementTypes.MODELROOT;
	}
	
	/** {@inheritDoc} */ @Override
	public void updateElementLazily() throws CoreException {
		createElementInfo();
	}

	/** Returns all Lang projects. */
	public DeeProject[] getLangProjects() {
		return (DeeProject[]) getChildren();
	}

	/** Returns the Lang project with the given name, or null if not found. */
	public ILangProject getLangProject(String name) {
		for (ILangProject element : getLangProjects()) {
			if(element.getElementName().equals(name))
				return element;
		}
		return null;
	}
	
	/** Returns the D project for given IProject, or null if not found. */
	public ILangProject getLangProject(IProject project) {
		return getLangProject(project.getName());
	}


	/** Removes a D project from the model. Does not delete workspace project. */
	public void removeDeeProject(LangElement deeproject) throws CoreException {
		removeChild(deeproject);
	}

	/** {@inheritDoc} */
	public IResource getUnderlyingResource() {
		return ResourcesPlugin.getWorkspace().getRoot();
	}
	
}