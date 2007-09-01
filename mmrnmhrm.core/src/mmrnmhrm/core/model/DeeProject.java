package mmrnmhrm.core.model;

import org.eclipse.core.resources.IProject;
import org.eclipse.dltk.core.IScriptProject;


//@Deprecated
public class DeeProject {

	public IScriptProject dltkProj;

	
	public DeeProject(IScriptProject dltkProj) {
		this.dltkProj = dltkProj;
	}

	public IProject getProject() {
		return dltkProj.getProject();
	}

}
