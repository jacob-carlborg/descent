package mmrnmhrm.core.model;

import mmrnmhrm.core.model.lang.ELangElementTypes;
import mmrnmhrm.core.model.lang.ILangElement;
import mmrnmhrm.core.model.lang.LangElement;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import util.ExceptionAdapter;
import descent.core.compiler.IProblem;
import descent.internal.core.dom.ParserFacade;
import dtool.descentadapter.DescentASTConverter;
import dtool.dom.ast.ASTNode;
import dtool.dom.definitions.Module;
import dtool.modelinterface.IDTool_DeeCompilationUnit;

/**
 * Module Wrapper 
 */
public class CompilationUnit extends LangElement implements IDTool_DeeCompilationUnit, IDeeElement {

	public IFile file;
	public String source; // Document??
	
	private descent.internal.core.dom.Module oldModule;
	private Module module;
	private boolean astUpdated;

	public IProblem[] problems;
	public int parseStatus;
	
	
	public CompilationUnit(PackageFragment parent, IFile file) {
		super(parent);
		this.file = file;
		//TODO: update source here?
	}
	
	public CompilationUnit(IFile file) {
		this(null, file);
	}
	

	public String getElementName() {
		return file.getName();
	}

	public int getElementType() {
		return ELangElementTypes.COMPILATION_UNIT;
	}
	
	/** Returns the D project of this compilation unit, null if none. */
	public DeeProject getProject() {
		return DeeModelManager.getLangProject(file.getProject().getName());
	}
	
	public void refreshElementChildren() throws CoreException {
		parseAST();
	}

	public ILangElement[] getChildren() {
		return new ILangElement[0];
	}
	
	
	public void setSource(String source) {
		this.source = source;
		astUpdated = false;
	}

	
	public descent.internal.core.dom.Module getOldModule() {
		return oldModule;
	}
	
	public Module getNeoModule() {
		return module;
	}
	
	public ASTNode getModule() {
		if(module == null || parseStatus == EModelStatus.PARSER_SYNTAX_ERRORS)
			return oldModule;
		return module;
	}

	public boolean hasErrors() {
		return problems.length > 0;
	}
	
	public void parseAST() {
		if(astUpdated)
			return;
		astUpdated = true;
		module = null;
		
		clearErrorMarkers();
		preParseCompilationUnit();

		if (hasErrors()) {
			createErrorMarkers();
			parseStatus = EModelStatus.PARSER_SYNTAX_ERRORS;
			return;
		}

		try {
			adaptAST();
			parseStatus = EModelStatus.OK;
		} catch (UnsupportedOperationException uoe) {
			parseStatus = EModelStatus.PARSER_AST_UNSUPPORTED_NODE;
		} catch (RuntimeException re) {
			parseStatus = EModelStatus.PARSER_INTERNAL_ERROR;
			throw re;
		}
	}

	private void clearErrorMarkers() {
		try {
			file.deleteMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
		} catch (CoreException e) {
			throw ExceptionAdapter.unchecked(e);
		}
	}
	
	private void createErrorMarkers() {
		for (IProblem problem : getOldModule().getProblems()) {
			try {
				IMarker marker = file.createMarker(IMarker.PROBLEM);
				marker.setAttribute(IMarker.MESSAGE, problem.getMessage());
				marker.setAttribute(IMarker.SEVERITY,
						IMarker.SEVERITY_ERROR);
			} catch (CoreException e) {
				throw ExceptionAdapter.unchecked(e);
			}
		}
	}

	private void preParseCompilationUnit() {
		this.module = null;
		this.problems = null;
		ParserFacade parser = new descent.internal.core.dom.ParserFacade();
		this.oldModule = parser.parseCompilationUnit(source).mod;
		this.problems = getOldModule().getProblems();
	}
	
	
	private void adaptAST() {
		DescentASTConverter domadapter = new DescentASTConverter();
		Module neoModule = domadapter.convertModule(oldModule);
		neoModule.cunit = this;
		module = neoModule;
	}
	


	public String toStringParseStatus() {
		if(parseStatus == EModelStatus.PARSER_INTERNAL_ERROR) {
			return "Parser Internal Error";
		} else if(parseStatus == EModelStatus.PARSER_SYNTAX_ERRORS) {
			return "Document Syntax Error";
		} else if(parseStatus == EModelStatus.PARSER_AST_UNSUPPORTED_NODE) {
			return "Unsupported AST Node configuration.";
		} else
			return "Status OK";
	}




}
