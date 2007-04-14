package mmrnmhrm.core.model;

import org.eclipse.core.resources.IProject;

public abstract class LangProject extends LangElement implements ILangProject {

	protected IProject project;
	
	public LangProject(IProject project) {
		this.project = project;
	}
	
	
	public IProject getProject() {
		return project;
	}
	
	public String getElementName() {
		return project.getName();
	}

	public int getElementType() {
		return ELangElementTypes.PROJECT;
	}

}
