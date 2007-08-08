package mmrnmhrm.core.model;

import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.model.lang.ILangProject;
import mmrnmhrm.core.model.lang.LangElement;
import mmrnmhrm.core.model.lang.LangModelRoot;
import mmrnmhrm.core.model.lang.LangPackageFragment;
import mmrnmhrm.core.model.lang.LangProject;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

import dtool.dom.definitions.Module;
import dtool.refmodel.pluginadapters.IModuleResolver;


/**
 * Represents the root of the D Model.
 */
public class DeeModelRoot extends LangModelRoot implements IDeeElement, IModuleResolver {

	private static DeeModelRoot instance = new DeeModelRoot();
	
	static {
		instance = new DeeModelRoot();
	}
	
	/** @return the shared instance */
	public static DeeModelRoot getInstance() {
		return instance;
	}
	
	@Override
	public ILangProject[] newChildrenArray(int size) {
		return new DeeProject[size];
	}
	
	/** {@inheritDoc} */
	public void createElementInfo() throws CoreException {
		// Init the model with existing D projects.
		clearChildren();
		for(IProject proj : DeeCore.getWorkspaceRoot().getProjects()) {
			if(proj.isOpen() && proj.hasNature(DeeNature.NATURE_ID))
			loadDeeProject(proj);
		}
	}

	/** Adds a D project from a resource project to Dee Model. */
	public LangElement loadDeeProject(IProject project) throws CoreException {
		DeeProject deeproj = new DeeProject(project);
		deeproj.createElementInfo();
		addDeeProject(deeproj);
		return deeproj;
	}
	
	/** Adds an existing D project to the model. Refreshes the project.  */
	public void addDeeProject(LangProject deeproj)  {
		addChild(deeproj);
	}

	/** Creates a D project in the given existing workspace project. 
	 * May or may not have a Dee nature, if not, one will be added.
	 * Sets a default build path (src:src, out:bin) on the project. */
	public DeeProject createDeeProject(IProject project) throws CoreException {
		DeeNature.addNature(project, DeeNature.NATURE_ID);
	
		DeeProject deeproj = new DeeProject(project);
		deeproj.setupNewProjectConfig();
		addDeeProject(deeproj);
		return deeproj;
	}

	/** Adds an existing D project to the model. Refreshes the project.  */
/*	public void addDeeProject(DeeProject deeproj) throws CoreException {
		addChild(deeproj);
		//deeproj.updateElementRecursive();
	}
*/	
	/** Finds the module with the given package and module name.
	 * refModule is used to determine which project/build-path to search. */
	public Module findModule(Module refModule, String packageName, String moduleName) throws CoreException {
		CompilationUnit refcunit = (CompilationUnit) refModule.getCUnit();
		
		DeeProject deeproj = refcunit.getProject();
		if(deeproj == null)
			return null;
		
		for (IDeeSourceRoot srcRoot : deeproj.getSourceRoots()) {
			
			for (LangPackageFragment pkgFrag : srcRoot.getPackageFragments()) {
				if(pkgFrag.getElementName().equals(packageName)) {
				
					for (CompilationUnit cunit : pkgFrag.getCompilationUnits()) {
						String str = cunit.getElementName();
						str = str.substring(0, str.lastIndexOf('.'));
						if(moduleName.equals(str))
							return cunit.getNeoModule();
	
					}
				
				}
			}
		}
		return null;
	}

	public void disposeElementInfo() {
		// Do nothing
	}

}
