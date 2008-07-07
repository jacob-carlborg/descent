package mmrnmhrm.core.model;

import mmrnmhrm.core.dltk.DeeModuleDeclaration;
import mmrnmhrm.core.dltk.DeeParserUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.ISourceModule;

import descent.internal.compiler.parser.ast.IASTNode;

/**
 * Compilation Unit. Bridges the Lang resource elements with ASTNode elements.
 * The content/structure of this element is allways created when the object
 * is instantiated.
 */
public class CompilationUnit {
	
	public ISourceModule modUnit;

	public CompilationUnit(IFile file) {
		this.modUnit = DLTKCore.createSourceModuleFrom(file);
	}
	
	private CompilationUnit(ISourceModule srcModule) {
		this.modUnit = srcModule;
	}
	
	public static CompilationUnit create(ISourceModule modUnit) {
		if(modUnit != null)
			return new CompilationUnit(modUnit);
		return null;
	}
	
	public IASTNode getModule() {
		DeeModuleDeclaration deeModule = DeeParserUtil.parseModule(modUnit);
		if(deeModule.neoModule == null)
			return deeModule.dmdModule;
		return deeModule.neoModule;
	}

	public DeeModuleDeclaration getDeeModuleDeclaration() {
		return DeeParserUtil.parseModule(modUnit);
	}

}
