package mmrnmhrm.core.model;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import util.AssertIn;
import util.ExceptionAdapter;
import descent.core.compiler.IProblem;
import descent.internal.core.dom.ParserFacade;
import dtool.descentadapter.DescentASTConverter;
import dtool.dom.ast.ASTElementFinder;
import dtool.dom.ast.ASTNode;
import dtool.dom.definitions.Module;
import dtool.model.IDeeCompilationUnit;

/**
 * Module Wrapper 
 */
public class CompilationUnit implements IDeeCompilationUnit {
	public String source;
	public IFile file;
	
	private ASTNode module;
	private boolean astUpdated;

	public IProblem[] problems;
	public int parseStatus;
	
	
	public CompilationUnit(IFile file) {
		this.file = file;
		//TODO: update source here?
	}
	
	public void setSource(String source) {
		this.source = source;
		astUpdated = false;
	}

	
	public descent.internal.core.dom.Module getOldModule() {
		return (descent.internal.core.dom.Module) module;
	}
	
	public Module getNeoModule() {
		return (Module) module;
	}
	
	public ASTNode getModule() {
		return module;
	}

	public boolean hasErrors() {
		return problems.length > 0;
	}
	
	public void parseAST(){
		if(astUpdated)
			return;
		astUpdated = true;
		
		clearErrorMarkers();
		preParseCompilationUnit();

		if (hasErrors()) {
			createErrorMarkers();
			parseStatus = EModelStatus.PARSER_SYNTAX_ERRORS;
			return;
		}

		try {
			adaptAST();
		} catch (UnsupportedOperationException uoe) {
			parseStatus = EModelStatus.PARSER_AST_UNSUPPORTED_NODE;
		} catch (RuntimeException re) {
			parseStatus = EModelStatus.PARSER_INTERNAL_ERROR;
		}
		parseStatus = EModelStatus.OK;
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
		this.module = parser.parseCompilationUnit(source).mod;
		this.problems = getOldModule().getProblems();
	}
	
	
	private void adaptAST() {
		DescentASTConverter domadapter = new DescentASTConverter();
		Module neoModule = domadapter.convertModule(module);
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

	/* === bindings === */
	public ASTNode findEntity(int offset) {
		AssertIn.isTrue(offset < source.length());
		return ASTElementFinder.findElement(getModule(), offset);
	}
	
}
