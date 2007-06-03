package dtool.model;

import java.io.File;

import util.AssertIn;
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
public class DToolCompilationUnit implements IDeeCompilationUnit {
	public String source;
	public File file;
	
	private ASTNode cumodule;
	private boolean astUpdated;

	public IProblem[] problems;
	public int status;
	
	public DToolCompilationUnit(String source) {
		setSource(source);
	}
	
	public void setSource(String source) {
		this.source = source;
		astUpdated = false;
	}

	
	public descent.internal.core.dom.Module getOldModule() {
		return (descent.internal.core.dom.Module) cumodule;
	}
	
	public Module getNeoModule() {
		return (Module) cumodule;
	}
	
	public ASTNode getModule() {
		return cumodule;
	}

	public boolean hasErrors() {
		return problems.length > 0;
	}
	
	public void preParseCompilationUnit() {
		ParserFacade parser = new descent.internal.core.dom.ParserFacade();
		descent.internal.core.dom.Module descentmodule;
		descentmodule = parser.parseCompilationUnit(source).mod;

		this.problems = descentmodule.getProblems();
		this.cumodule = descentmodule;
	}
	
	
	public void adaptAST() {
		DescentASTConverter domadapter = new DescentASTConverter();
		Module module = domadapter.convertModule(cumodule);
		module.cunit = this;
		cumodule = module;
	}
	
	public void parseAST(){
		if(astUpdated)
			return;
		astUpdated = true;
		preParseCompilationUnit();
		if(hasErrors())
			return;
		adaptAST();
	}

	/* === bindings === */
	public ASTNode findEntity(int offset) {
		AssertIn.isTrue(offset < source.length());
		return ASTElementFinder.findElement(getModule(), offset);
	}
}
