package mmrnmhrm.core.dltk;

import melnorme.miscutil.StringUtil;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.IScriptFolder;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;

import dtool.dom.definitions.Module;
import dtool.refmodel.pluginadapters.IModuleResolver;

public class DLTKModelResolver implements IModuleResolver {
	
	public static final DLTKModelResolver instance = new DLTKModelResolver();
	
	public static final String[] VALID_EXTENSIONS = new String[] {
		".d", ".di", ".dh"
	};

	/*@Override
	public Module findModule(Module refSourceModule, String[] packages,
			String module) throws Exception {
		ISourceModule sourceModule = (ISourceModule) refSourceModule.getModuleUnit();
		IScriptProject scriptProject = sourceModule.getScriptProject();
		String packageName = StringUtil.collToString(packages, ".");
	 	IType type = scriptProject.findType(packageName, module);
	 	if(type == null)
	 		return null;
	 	ModuleDeclaration modDecl = ModelUtil.parseModule(type.getSourceModule());
	 	return ModelUtil.getNeoASTModule(modDecl);
	} */
	
	/** Finds the module with the given package and module name.
	 * refModule is used to determine which project/build-path to search. */
	@Override
	public Module findModule(Module sourceRefModule, String[] packages,
			String modName) throws CoreException {

		//ScriptModelUtil.findType(module, qualifiedName, delimeter)
		
		ISourceModule sourceModule = (ISourceModule) sourceRefModule.getModuleUnit();

		IScriptProject deeproj = sourceModule.getScriptProject();
		if(deeproj == null)
			return null;
		
		String fullPackageName = StringUtil.collToString(packages, "/");  
		
		for (IProjectFragment srcFolder : deeproj.getProjectFragments()) {
			
			IScriptFolder pkgFrag = srcFolder.getScriptFolder(fullPackageName);
			if(pkgFrag != null && pkgFrag.exists()) {
				for (int i = 0; i < VALID_EXTENSIONS.length; i++) {
					String fileext = VALID_EXTENSIONS[i];
					ISourceModule modUnit = pkgFrag.getSourceModule(modName+fileext);
				 	if(modUnit != null && modUnit.exists()) { 
					 	ModuleDeclaration modDecl = ModelUtil.parseModule(modUnit);
					 	return ModelUtil.getNeoASTModule(modDecl);
				 	}
				}
			
			}	

			/*for (IScriptFolder srcFolder : pkgFrag.getScriptFolder()) {
				if(srcFolder.getElementName().equals(fullPackageName)) {
			
					for (IModelElement elem : pkgFrag.getChildren()) {
						ISourceModule modUnit = (ISourceModule) elem;
						String str = modUnit.getElementName();
						str = str.substring(0, str.lastIndexOf('.'));
						if(modName.equals(str)) {
						 	ModuleDeclaration modDecl = ModelUtil.parseModule(modUnit);
						 	return ModelUtil.getNeoASTModule(modDecl);
						}
	
					}
				
				}
			}*/
		}
		return null;
	}

}
