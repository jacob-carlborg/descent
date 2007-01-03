package dtool.project;

import java.io.File;

import util.FileUtil;
import descent.core.compiler.IProblem;
import descent.core.domX.AbstractElement;
import descent.internal.core.dom.ParserFacade;
import dtool.descentadapter.DescentASTConverter;
import dtool.dom.ast.tree.TreeParentizer;
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

	public CompilationUnit(String source) {
		this.source = source;
		parse();
	}

	public CompilationUnit(File file) throws Exception {
		this.file = file;
		this.source = FileUtil.readStringFromFile(file);
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
	
	public void update(String source) {
		this.source = source;
		parse();
	}

	public void preParseCompilationUnit() {
		ParserFacade parser = new descent.internal.core.dom.ParserFacade();
		descent.internal.core.dom.Module descentmodule;
		descentmodule = parser.parseCompilationUnit(source);

		this.problems = descentmodule.getProblems();
		this.cumodule = descentmodule;
	}
	
	
	public void adaptDOM() {
		DescentASTConverter domadapter = new DescentASTConverter();
		this.cumodule = domadapter.convert((AbstractElement) cumodule); 
		TreeParentizer.parentize(this.cumodule);
	}
	
	public void parse(){
		preParseCompilationUnit();
		if(hasErrors())
			return;
		adaptDOM();
	}

}
