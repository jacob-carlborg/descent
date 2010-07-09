package mmrnmhrm.core.dltk;

import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.ast.parser.IModuleDeclaration;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.SourceParserUtil;

import dtool.ast.definitions.Module;

public abstract class DeeParserUtil {

	/*
	private static ISourceModuleInfo getSourceModuleInfo(ISourceModule module) {
		ISourceModuleInfoCache sourceModuleInfoCache;
		sourceModuleInfoCache = org.eclipse.dltk.internal.core.ModelManager.
			getModelManager().getSourceModuleInfoCache();
		return sourceModuleInfoCache.get(module);
	}*/

	
	/** Gets a DeeModuleDeclaration from a sourceModule, parsing it if necessary.
	 * XXX: what are the conditions to ensure that a ISourceModule is parsed by Dee ?? */
	public static DeeModuleDeclaration parseModule(ISourceModule sourceModule) {
		ModuleDeclaration moduleDeclaration = SourceParserUtil.getModuleDeclaration(sourceModule, null);
		return getFixedDeeModuleDeclaration(moduleDeclaration, sourceModule);
		//ISourceModuleInfo info = getSourceModuleInfo(sourceModule);
		//return DeeSourceElementParser.parseModule(info, sourceModule, null, null, null);
	}

	/** If a moduleDeclaration is a DeeModuleDeclaration, fixes it (set sourceModule) and returns 
	 * it, otherwise return null. */
	public static DeeModuleDeclaration getFixedDeeModuleDeclaration(
			IModuleDeclaration moduleDeclaration, ISourceModule sourceModule) {
		if (moduleDeclaration instanceof DeeModuleDeclaration) {
			DeeModuleDeclaration deeModuleDecl = (DeeModuleDeclaration) moduleDeclaration;
			if (deeModuleDecl.neoModule != null)
				deeModuleDecl.neoModule.setModuleUnit(sourceModule);
			return deeModuleDecl;
		}
		return null;
	}

	public static Module getNeoASTModule(DeeModuleDeclaration moduleDec) {
		return moduleDec.neoModule;
	}

	public static Module getNeoASTModule(ISourceModule modUnit) {
		return getNeoASTModule(parseModule(modUnit));
	}

}
