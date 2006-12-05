package dtool;

import java.io.File;
import java.io.FileReader;

import util.StringUtil;
import descent.core.dom.IProblem;
import descent.core.domX.ASTNode;
import descent.core.domX.AbstractElement;
import descent.internal.core.dom.ParserFacade;
import dtool.descentadapter.DescentASTConverter;
/**
 * Similar to Module 
 */
public class CompilationUnit {
	public FileReader fr;
	public char[] source;
	public File file;

	public CompilationUnit(String filename) throws Exception {
		this(new File(filename));
	}

	public CompilationUnit(File file) throws Exception {
		this.file = file;
		this.fr = new java.io.FileReader(file);
		this.source = StringUtil.readBytesFromFile(file);
	}
	
	public ASTNode cumodule;
	public IProblem[] problems;

	
	public void preParseCompilationUnit() {
		ParserFacade parser = new descent.internal.core.dom.ParserFacade();
		String str = new String(source);
		descent.internal.core.dom.Module descentmodule;
		descentmodule = parser.parseCompilationUnit(str);

		this.problems = descentmodule.getProblems();
		this.cumodule = descentmodule;
	}
	
	
	public void adaptDOM() {
		DescentASTConverter domadapter = new DescentASTConverter();
		this.cumodule = domadapter.convert((AbstractElement) cumodule); 
	}
}
