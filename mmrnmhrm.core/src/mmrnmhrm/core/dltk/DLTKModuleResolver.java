package mmrnmhrm.core.dltk;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import melnorme.miscutil.StringUtil;
import mmrnmhrm.core.model.DeeNameRules;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.IExternalSourceModule;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.IScriptFolder;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.environment.EnvironmentPathUtils;

import dtool.ast.definitions.Module;
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
		
		if(deeproj == null || deeproj.exists() == false)
			return null;
		
		String fullPackageName = StringUtil.collToString(packages, "/");  
		
		for (IProjectFragment srcFolder : deeproj.getProjectFragments()) {
			
			IScriptFolder pkgFrag = srcFolder.getScriptFolder(fullPackageName);
			if(pkgFrag != null && pkgFrag.exists()) {
				for (int i = 0; i < DeeNameRules.VALID_EXTENSIONS.length; i++) {
					String fileext = DeeNameRules.VALID_EXTENSIONS[i];
					ISourceModule modUnit = pkgFrag.getSourceModule(modName+fileext);
					if(exists(modUnit)) { 
						DeeModuleDeclaration modDecl = DeeParserUtil.parseModule(modUnit);
						return DeeParserUtil.getNeoASTModule(modDecl);
					}
				}
			}	
		}
		return null;
	}
	
	private boolean exists(ISourceModule modUnit) {
		return modUnit != null && modUnit.exists()
		// XXX: DLTK bug workaround: 
		// modUnit.exists() true on ANY external source modules of libraries
		// we should make a test case for this
			&& externalReallyExists(modUnit)
		;
	}
	
	private boolean externalReallyExists(ISourceModule modUnit) {
		if(!(modUnit instanceof IExternalSourceModule))
			return true;
		//modUnit.getUnderlyingResource() of externals is allways null
		IPath localPath = EnvironmentPathUtils.getLocalPath(modUnit.getPath());
		return new File(localPath.toOSString()).exists();
	}
	
	@Override
	public String[] findModules(Module refSourceModule, String fqNamePrefix) throws ModelException {
		ISourceModule sourceModule = (ISourceModule) refSourceModule.getModuleUnit();
		
		IScriptProject scriptProject = sourceModule.getScriptProject();
		
		List<String> strings = new ArrayList<String>();
		
		for (IProjectFragment srcFolder : scriptProject.getProjectFragments()) {
			
			for (IModelElement pkgFragElem : srcFolder.getChildren()) {
				IScriptFolder pkgFrag = (IScriptFolder) pkgFragElem;
				
				String pkgName = pkgFrag.getElementName();
				if(!DeeNameRules.isValidPackagePathName(pkgName))
					continue;
				
				pkgName = DeeNameRules.convertPackagePathName(pkgName);
				
				for (IModelElement srcUnitElem : pkgFrag.getChildren()) {
					ISourceModule srcUnit = (ISourceModule) srcUnitElem;
					String modName = srcUnit.getElementName();
					// remove extension
					modName = modName.substring(0, modName.indexOf('.'));
					String fqName;
					if(pkgName.equals(""))
						fqName = modName;
					else 
						fqName = pkgName + "." + modName;
					
					if(fqName.startsWith(fqNamePrefix))
						strings.add(fqName);
				}
			}
		}		
		
		return strings.toArray(new String[strings.size()]);
	}
	
}
