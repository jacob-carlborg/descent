package mmrnmhrm.core.model;

import mmrnmhrm.core.build.DeeCompilerOptions;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.dltk.core.IScriptProject;


// TODO deprecate
//@Deprecated
public class DeeProject {

	public IScriptProject dltkProj;

	public DeeCompilerOptions compilerOptions;
	protected IContainer outputDir;
	
	public DeeProject(IScriptProject dltkProj) {
		this.compilerOptions = new DeeCompilerOptions();
		this.dltkProj = dltkProj;
	}

	public IProject getProject() {
		return dltkProj.getProject();
	}

	/** Gets the output dir path for this project. 
	 * The resource is allowed not to exist. */
	public IContainer getOutputDir() {
		return outputDir;
	}

	/** Sets the output dir path for this project. 
	 * The resource is allowed not to exist. */
	public void setOutputDir(IFolder outputDir) {
		this.outputDir = outputDir;
	}

}
