package mmrnmhrm.core.dltk;

import melnorme.miscutil.ExceptionAdapter;
import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.LangCore;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.compiler.ISourceElementRequestor;
import org.eclipse.dltk.compiler.problem.IProblemReporter;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.ISourceElementParser;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.ISourceModuleInfoCache.ISourceModuleInfo;

import dtool.Logg;
import dtool.dom.definitions.Module;

public class DeeSourceElementParser implements ISourceElementParser {
 
	private static final String AST_CACHE_KEY = "AST";
	
	private ISourceElementRequestor fRequestor = null;
	private IProblemReporter fReporter = null;

	public DeeSourceElementParser(
/*			ISourceElementRequestor requestor,
			IProblemReporter problemReporter*/) {
		//this.fRequestor = requestor;
		//this.fReporter = problemReporter;
	}

	//@Override
	public void setReporter(IProblemReporter reporter) {
		this.fReporter = reporter;
	}
	
	public void setRequestor(ISourceElementRequestor requestor ) {
		this.fRequestor = requestor;
	}

	@SuppressWarnings("restriction")
	//@Override
	public ModuleDeclaration parseSourceModule(
			char[] contents, ISourceModuleInfo astCache, char[] filename) {

		IPath path = new Path(new String(filename));
		ISourceModule module = null;
		if(!path.isAbsolute()) {
			IFile file = DeeCore.getWorkspaceRoot().getFile(path);
			module = DLTKCore.createSourceModuleFrom(file);
		}
		
		//assertNotNull(module);
		//assertTrue(module.exists());
		// create a parser, that gives us an AST
		DeeModuleDeclaration moduleDeclaration = parseModule(astCache, module, contents, fReporter, filename);
		
		DeeSourceElementProvider provider = new DeeSourceElementProvider(fRequestor);
		provider.provide(moduleDeclaration);
		return null;
		//return moduleDeclaration;
	}
	

	/** Obtains an AST from the given module, possibly using a cached value. 
	 * Must supply either sourceModule or source */
	public static DeeModuleDeclaration parseModule(ISourceModuleInfo astCache,
			ISourceModule modUnit, char[] source, IProblemReporter reporter, char[] filename) {
		DeeModuleDeclaration moduleDecl = getCachedModule(astCache);
		if(moduleDecl != null) {
			String str = (modUnit == null) ? "<null>" : modUnit.getElementName();
			Logg.model.println("ParseModule (got AST cache): " + str);
			// setup the moduleUnit nonetheless, cause it can be null
			if(modUnit != null) {
				moduleDecl.neoModule.setModuleUnit(modUnit);
			}
			return moduleDecl;
		}
		
		if(source == null)
		try {
			source = modUnit.getSourceAsCharArray();
		} catch (ModelException e) {
			LangCore.log(e);
			throw ExceptionAdapter.unchecked(e);
		}

		
		moduleDecl = DeeSourceParser.parseModule(source, reporter, filename);
		String str = (filename == null) ? "<null>" : new String(filename);
		Logg.model.println("ParseModule parsed: ", str);
		Module neoModule = ParsingUtil.getNeoASTModule(moduleDecl);
		//Assert.isNotNull(modUnit);
		if(neoModule != null && modUnit != null)
			neoModule.setModuleUnit(modUnit);

		if(astCache != null) {
			astCache.put(AST_CACHE_KEY, moduleDecl);
		}
		return moduleDecl;
	}

	public static DeeModuleDeclaration getCachedModule(ISourceModuleInfo astCache) {
		if(astCache != null) {
			DeeModuleDeclaration moduleDeclaration;
			moduleDeclaration = (DeeModuleDeclaration) astCache.get(AST_CACHE_KEY);
			if(moduleDeclaration != null)
				return moduleDeclaration;
		}
		return null;
	}

}
