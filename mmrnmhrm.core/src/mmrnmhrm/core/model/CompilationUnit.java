package mmrnmhrm.core.model;

import melnorme.miscutil.ExceptionAdapter;
import melnorme.miscutil.log.Logg;
import mmrnmhrm.core.dltk.ModelUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IBuffer;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;

import descent.internal.compiler.parser.ast.IASTNode;
import dtool.dom.definitions.Module;
import dtool.refmodel.pluginadapters.IGenericCompilationUnit;

/**
 * Compilation Unit. Bridges the Lang resource elements with ASTNode elements.
 * The content/structure of this element is allways created when the object
 * is instantiated.
 */
public class CompilationUnit implements IGenericCompilationUnit {
	
	public interface EModelStatus {
		int OK = 0;
		int PARSER_INTERNAL_ERROR = 1;
		int PARSER_SYNTAX_ERRORS = 2;
	}
	
	protected final IFile file;
	public ISourceModule modUnit;
	

	public CompilationUnit(IFile file) {
		this.file = file;
		this.modUnit = DLTKCore.createSourceModuleFrom(file);
	}
	
	public CompilationUnit(ISourceModule srcModule, IFile file) {
		this(file);
		this.modUnit = srcModule;
	}

	@Override
	public ISourceModule getModuleUnit() {
		return modUnit;
	}
	
	public IFile getFile() {
		return file;
	}
	
	public IBuffer getBuffer() throws ModelException {
		return modUnit.getBuffer();
	}

	public String getSource() throws ModelException {
		return getBuffer().getContents();
	}

	public int getParseStatus() {
		return ModelUtil.getParseStatus(modUnit);
	}
	
	public descent.internal.compiler.parser.Module getDmdModule() {
		return ModelUtil.getDmdASTModule(modUnit);
	}
	
	public Module getNeoModule() {
		return ModelUtil.getNeoASTModule(modUnit);
	}
	
	public IASTNode getModule() {
		Module module = ModelUtil.getNeoASTModule(modUnit);
		if(module == null 
				|| getParseStatus() == EModelStatus.PARSER_SYNTAX_ERRORS)
			return getDmdModule();
		return module;
	}
	

	public void parseModuleUnit() throws ModelException {
		Logg.main.println("CUNIT: reconcile");
		if(!modUnit.isOpen())
			modUnit.open(null);
		modUnit.becomeWorkingCopy(null, null);
		modUnit.reconcile(false, null, null);
	}

	public String toStringParseStatus() {
		if(getParseStatus() == EModelStatus.PARSER_INTERNAL_ERROR) {
			return "Parser Internal Error";
		} else if(getParseStatus() == EModelStatus.PARSER_SYNTAX_ERRORS) {
			return "Document Syntax Error";
		} else
			return "Status OK";
	}



	public void createElementInfo() {
		try {
			Logg.main.println("CUNIT: createElementInfo");
			parseModuleUnit();
		} catch (ModelException e) {
			throw ExceptionAdapter.unchecked(e);
		}
	}




}
