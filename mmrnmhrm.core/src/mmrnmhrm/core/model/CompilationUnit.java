package mmrnmhrm.core.model;

import java.util.Collection;

import melnorme.miscutil.ExceptionAdapter;
import mmrnmhrm.core.CorePreferenceInitializer;
import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.model.lang.ILangElement;
import mmrnmhrm.core.model.lang.LangModuleUnit;
import mmrnmhrm.core.model.lang.LangPackageFragment;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;

import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.ASTNode;
import dtool.descentadapter.DescentASTConverter;
import dtool.dom.definitions.Module;
import dtool.refmodel.ParserAdapter;
import dtool.refmodel.pluginadapters.IGenericCompilationUnit;

/**
 * Compilation Unit. Bridges the Lang resource elements with ASTNode elements.
 * The content/structure of this element is allways created when the object
 * is instantiated.
 */
public class CompilationUnit extends LangModuleUnit implements IGenericCompilationUnit, IDeeElement {

	
	private descent.internal.compiler.parser.Module oldModule;
	private Module module;
	//private boolean astUpdated;

	public Collection<IProblem> problems;
	public int parseStatus;
	

	public CompilationUnit(LangPackageFragment parent, IFile file) {
		super(parent, file);
		//createElementInfo();
	}
	
	public CompilationUnit(IFile file) {
		this(null, file);
	}
	
	@Override
	public ILangElement[] getLangChildren() {
		return ILangElement.NO_LANGELEMENTS;
	}
	
	public boolean hasChildren() {
		return true;
	}

	public ASTNode[] getChildren() {
		getElementInfo();
		return getModule().getChildren();
	}
	
	public void disposeElementInfo() throws CoreException {
		super.disposeElementInfo();
		module = null;
		oldModule = null;
		problems = null;
		parseStatus = 0;
	}
	
	public descent.internal.compiler.parser.Module getOldModule() {
		getElementInfo();
		return oldModule;
	}
	
	public Module getNeoModule() {
		getElementInfo();
		return module;
	}
	
	public ASTNode getModule() {
		getElementInfo();
		if(module == null || parseStatus == EModelStatus.PARSER_SYNTAX_ERRORS)
			return oldModule;
		return module;
	}
	

	/** Updates this CompilationUnit's AST according to the underlying text. */
	public void reconcile() {
		openBuffer();
		parseUnit();
	}
	
	/** Parses this unit's document to produce and AST. Buffer must be open. */
	protected void parseUnit() {
		module = null;
		
		clearErrorMarkers();
		parseCompilationUnit();

		if (hasErrors()) {
			createErrorMarkers(getDocument());
			parseStatus = EModelStatus.PARSER_SYNTAX_ERRORS;
			return;
		}

		try {
			convertAST();
			parseStatus = EModelStatus.OK;
		} catch (UnsupportedOperationException uoe) {
			parseStatus = EModelStatus.PARSER_AST_UNSUPPORTED_NODE;
		} catch (RuntimeException re) {
			parseStatus = EModelStatus.PARSER_INTERNAL_ERROR;
			throw re;
		}
	}
	
	private boolean hasErrors() {
		return problems.size() > 0;
	}

	private void clearErrorMarkers() {
		try {
			file.deleteMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
		} catch (CoreException ce) {
			throw ExceptionAdapter.unchecked(ce);
		}
	}
	
	private void createErrorMarkers(IDocument doc) {
		boolean reportSyntaxErrors;
		reportSyntaxErrors = DeeCore.getInstance().getPluginPreferences()
				.getBoolean(CorePreferenceInitializer.REPORT_SYNTAX_ERRORS);

		for (IProblem problem : oldModule.problems) {
			try {
				createMarker(doc, problem, reportSyntaxErrors);
			} catch (CoreException e) {
				throw ExceptionAdapter.unchecked(e);
			}
		}
	}
	
	private void createMarker(IDocument doc, IProblem problem,
			boolean reportSyntaxErrors) throws CoreException {
		IMarker marker = file.createMarker(IMarker.PROBLEM);
		
		int lineNum = 0;
		try {
			lineNum = doc.getLineOfOffset(problem.getSourceStart());
			marker.setAttribute(IMarker.LINE_NUMBER, lineNum);
		} catch (BadLocationException e) {
			DeeCore.log(e);
		}
		
		if(reportSyntaxErrors) {
			marker.setAttribute(IMarker.LOCATION, "Line "+ lineNum);
			marker.setAttribute(IMarker.CHAR_START, problem.getSourceStart());
			marker.setAttribute(IMarker.CHAR_END, problem.getSourceEnd());
		}
		marker.setAttribute(IMarker.MESSAGE, problem.getMessage());
		marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
	}

	private void parseCompilationUnit() {
		this.module = null;
		this.problems = null;
		this.oldModule = ParserAdapter.parseSource(getSource()).mod;
		//Logg.model.println(ASTPrinter.toStringAST(this.oldModule, true));
		this.problems = oldModule.problems;
	}
	
	
	private void convertAST() {
		Module neoModule = DescentASTConverter.convertModule(oldModule);
		neoModule.setCUnit(this);
		module = neoModule;
		//oldModule = null;
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
