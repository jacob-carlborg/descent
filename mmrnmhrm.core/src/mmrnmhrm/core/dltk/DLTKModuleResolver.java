package mmrnmhrm.core.dltk;

import java.io.File;

import melnorme.miscutil.StringUtil;
import mmrnmhrm.core.model.DeeNameRules;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.IExternalSourceModule;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.IScriptFolder;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;

import dtool.dom.definitions.Module;
import dtool.refmodel.pluginadapters.IModuleResolver;

public class DLTKModuleResolver implements IModuleResolver {
	
	public static final DLTKModuleResolver instance = new DLTKModuleResolver();
	
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
				for (int i = 0; i < DeeNameRules.VALID_EXTENSIONS.length; i++) {
					String fileext = DeeNameRules.VALID_EXTENSIONS[i];
					ISourceModule modUnit = pkgFrag.getSourceModule(modName+fileext);
				 	if(exists(modUnit)) { 
					 	DeeModuleDeclaration modDecl = ParsingUtil.parseModule(modUnit);
					 	return ParsingUtil.getNeoASTModule(modDecl);
				 	}
				}
			
			}	
		}
		return null;
	}

	private boolean exists(ISourceModule modUnit) {
		return modUnit != null && modUnit.exists()
		// XXX: Fix for DLTK bug: 
		// modUnit.exists() true on external source modules of libraries
		// we should make a test case for this
			&& externalReallyExists(modUnit)
		;
	}

	private boolean externalReallyExists(ISourceModule modUnit) {
		if(!(modUnit instanceof IExternalSourceModule))
			return true;
		return new File(modUnit.getPath().toOSString()).exists();
	}

}
