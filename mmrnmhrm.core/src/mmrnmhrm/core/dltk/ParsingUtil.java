package mmrnmhrm.core.dltk;

import static melnorme.miscutil.Assert.assertNotNull;

import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ISourceModuleInfoCache;
import org.eclipse.dltk.core.ISourceModuleInfoCache.ISourceModuleInfo;

import dtool.dom.definitions.Module;

public abstract class ParsingUtil {

	@SuppressWarnings("restriction")
	public static ISourceModuleInfo getSourceModuleInfo(ISourceModule module) {
		ISourceModuleInfoCache sourceModuleInfoCache;
		sourceModuleInfoCache = org.eclipse.dltk.internal.core.ModelManager.
			getModelManager().getSourceModuleInfoCache();
		return sourceModuleInfoCache.get(module);
	}

	public static DeeModuleDeclaration parseModule(ISourceModule sourceModule) {
		ISourceModuleInfo info = getSourceModuleInfo(sourceModule);
		return DeeSourceElementParser.parseModule(info, sourceModule, null, null, null);
	}

	public static Module getNeoASTModule(DeeModuleDeclaration moduleDec) {
		return moduleDec.neoModule;
	}

	public static Module getNeoASTModule(ISourceModule modUnit) {
		return getNeoASTModule(parseModule(modUnit));
	}

}
