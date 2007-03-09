package dtool.project;

import java.io.File;

import util.AssertIn;
import descent.core.compiler.IProblem;
import descent.core.domX.AbstractElement;
import descent.internal.core.dom.ParserFacade;
import dtool.descentadapter.DescentASTConverter;
import dtool.dom.ast.ASTElementFinder;
import dtool.dom.ast.TreeParentizer_ASTNode;
import dtool.dom.base.ASTNode;
import dtool.dom.base.Module;
/**
 * Similar to Module 
 */
public class CompilationUnit {
	public String source;
	public File file;
	
	private ASTNode cumodule;
	public IProblem[] problems;
	private boolean astUpdated;

	public CompilationUnit(String source) {
		update(source);

	}
	
	public void update(String source) {
		this.source = source;
		astUpdated = false;
		parse();	
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
	
	
	public void adaptDOM() {
		DescentASTConverter domadapter = new DescentASTConverter();
		this.cumodule = domadapter.convert((AbstractElement) cumodule); 
		TreeParentizer_ASTNode.parentize(this.cumodule);
	}
	
	public void parse(){
		if(astUpdated)
			return;
		astUpdated = true;
		preParseCompilationUnit();
		if(hasErrors())
			return;
		adaptDOM();
	}

	/* === bindings === */
	public ASTNode findEntity(int offset) {
		AssertIn.isTrue(offset < source.length());
		return ASTElementFinder.findElement(getModule(), offset);
	}

}
