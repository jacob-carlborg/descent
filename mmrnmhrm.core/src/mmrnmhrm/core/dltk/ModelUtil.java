package mmrnmhrm.core.dltk;

import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ISourceModuleInfoCache;
import org.eclipse.dltk.core.ISourceModuleInfoCache.ISourceModuleInfo;

import dtool.dom.definitions.Module;

public abstract class ModelUtil {

	@SuppressWarnings("restriction")
	public static ISourceModuleInfo getSourceModuleInfo(ISourceModule module) {
		ISourceModuleInfoCache sourceModuleInfoCache;
		sourceModuleInfoCache = org.eclipse.dltk.internal.core.ModelManager.
			getModelManager().getSourceModuleInfoCache();
		return sourceModuleInfoCache.get(module);
	}

	public static ModuleDeclaration parseModule(ISourceModule sourceModule) {
		ISourceModuleInfo info = getSourceModuleInfo(sourceModule);

		return DeeSourceElementParser.parseModule(info, sourceModule, null, null, null);
	}

	public static Module getNeoASTModule(ModuleDeclaration moduleDec) {
		return (Module) moduleDec.getStatements().iterator().next();
	}

	public static Module getNeoASTModule(ISourceModule modUnit) {
		return getNeoASTModule(parseModule(modUnit));
	}

	public static descent.internal.compiler.parser.Module getDmdASTModule(
			ISourceModule modUnit) {
		// TODO Auto-generated method stub
		return null;
	}

	public static int getParseStatus(ISourceModule modUnit) {
		// TODO Auto-generated method stub
		return 0;
	}

}
