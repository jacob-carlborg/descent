package mmrnmhrm.core.dltk;

import melnorme.miscutil.Assert;
import melnorme.miscutil.log.Logg;
import mmrnmhrm.core.DeeCore;

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

	@Override
	public void setReporter(IProblemReporter reporter) {
		this.fReporter = reporter;
	}
	
	public void setRequestor(ISourceElementRequestor requestor ) {
		this.fRequestor = requestor;
	}

	@SuppressWarnings("restriction")
	@Override
	public ModuleDeclaration parseSourceModule(
			char[] contents, ISourceModuleInfo astCache, char[] filename) {

		//XXX: Use DTLKCore ? 
		IPath path = new Path(new String(filename));
		IFile file = DeeCore.getWorkspaceRoot().getFile(path);
		ISourceModule module = null;
		module = DLTKCore.createSourceModuleFrom(file);

		/*
		Model model = ModelManager.getModelManager().getModel();
		IScriptProject project = model.getScriptProject(path.segment(0));
		try {
						
			//IPath projRelPath = path.removeFirstSegments(1);
			IProjectFragment[] fragments = project.getProjectFragments();
			for (int i = 0; i < fragments.length; i++) {
				IProjectFragment srcFolder = fragments[i];
				int commonSegs = srcFolder.getPath().matchingFirstSegments(path);
				IPath srcRelPath = path.removeFirstSegments(commonSegs);
				IScriptFolder scriptFolder = srcFolder.getScriptFolder(srcRelPath.removeLastSegments(1));
				if(scriptFolder == null)
					continue;
				String modName = srcRelPath.removeFileExtension().lastSegment();
				module = scriptFolder.getSourceModule(modName);
				if(module != null) {
					break;
				}
			}
		} catch (RuntimeException e) {
			throw ExceptionAdapter.unchecked(e);
		}*/

		Assert.isNotNull(module);
		// create a parser, that gives us an AST
		ModuleDeclaration moduleDeclaration = parseModule(astCache, module, contents, fReporter, filename);
		
		// traverse fetched AST with a visitor, that reports model element 
		// to given ISourceElementRequestor
		DeeSourceElementRequestor requestor; 
		requestor = new DeeSourceElementRequestor(this.fRequestor);
		
		try {
			moduleDeclaration.traverse(requestor);
		} catch (Exception e) {
			if (DLTKCore.DEBUG) {
				e.printStackTrace();
			}
		}

		return moduleDeclaration;
	}
	

	/** Obtains an AST from the given module, possibly using a cached value. 
	 * Must supply either sourceModule or source */
	public static ModuleDeclaration parseModule(ISourceModuleInfo astCache,
			ISourceModule sourceModule, char[] source, IProblemReporter reporter, char[] filename) {
		ModuleDeclaration moduleDeclaration = getModuleAST(astCache);
		if(moduleDeclaration != null) {
			String str = (sourceModule == null) ? "<null>" : sourceModule.getElementName();
			Logg.model.println("ParseModule (got AST cache): " + str);
			return moduleDeclaration;
		}
		
		if(source == null)
		try {
			source = sourceModule.getSourceAsCharArray();
		} catch (ModelException e) {
			DeeCore.log(e);
			if( DLTKCore.DEBUG ) {
				e.printStackTrace();
			}
			//ExceptionAdapter.unchecked(e);
			return null;
		}
		
		moduleDeclaration = DeeSourceParser.parseModule(source, reporter, filename);
		String str = (filename == null) ? "<null>" : new String(filename);
		Logg.model.println("ParseModule parsed: ", str);
		Module neoModule = ModelUtil.getNeoASTModule(moduleDeclaration);
		Assert.isNotNull(sourceModule);
		neoModule.setModuleUnit(sourceModule);

		if(astCache != null) {
			astCache.put(AST_CACHE_KEY, moduleDeclaration);
		}
		return moduleDeclaration;
	}

	public static ModuleDeclaration getModuleAST(ISourceModuleInfo astCache) {
		if(astCache != null) {
			ModuleDeclaration moduleDeclaration;
			moduleDeclaration = (ModuleDeclaration) astCache.get(AST_CACHE_KEY);
			if(moduleDeclaration != null)
				return moduleDeclaration;
		}
		return null;
	}

}
