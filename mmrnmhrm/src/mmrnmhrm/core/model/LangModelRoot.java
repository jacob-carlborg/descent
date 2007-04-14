package mmrnmhrm.core.model;

import org.eclipse.core.resources.IProject;


public abstract class LangModelRoot extends LangElement {
	

	/** Returns all Lang projects. */
	private ILangProject[] getLangProjects() {
		return (ILangProject[]) getChildren();
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

	
	public String getElementName() {
		return "";
	}
	
	public int getElementType() {
		return ELangElementTypes.MODELROOT;
	}
	
}