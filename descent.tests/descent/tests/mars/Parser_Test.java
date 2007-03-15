package descent.tests.mars;

import java.util.List;

import junit.framework.TestCase;
import descent.core.compiler.IProblem;
import descent.core.dom.AST;
import descent.core.dom.ASTNode;
import descent.core.dom.CompilationUnit;
import descent.core.dom.CompilationUnitResolver;
import descent.core.dom.Declaration;
import descent.core.dom.Expression;
import descent.core.dom.Initializer;
import descent.core.dom.ModuleDeclaration;
import descent.core.dom.Statement;
import descent.internal.compiler.parser.Module;
import descent.internal.compiler.parser.Parser;

public abstract class Parser_Test extends TestCase {
	
	protected final static int SEVERITY_ERROR = 1;
	protected final static int SEVERITY_WARNING = 2;
	
	protected List<Declaration> getDeclarationsWithProblems(String source, int numberOfProblems) {
		return getDeclarationsWithProblems(source, numberOfProblems, AST.LATEST);
	}
	
	protected List<Declaration> getDeclarationsWithProblems(String source, int numberOfProblems, int apiLevel) {
		CompilationUnit unit = new ParserFacade().parseCompilationUnit(source, apiLevel);
		assertEquals(numberOfProblems, unit.getProblems().length);
		return unit.declarations();
	}
	
	protected List<Declaration> getDeclarationsNoProblems(String source) {
		return getDeclarationsWithProblems(source, 0);
	}
	
	protected Declaration getSingleDeclarationNoProblems(String source) {
		List<Declaration> declDefs = getDeclarationsNoProblems(source);
		assertEquals(1, declDefs.size());		
		return declDefs.get(0);
	}
	
	protected Declaration getSingleDeclarationWithProblems(String source, int numberOfProblems) {
		List<Declaration> declDefs = getDeclarationsWithProblems(source, numberOfProblems);
		assertEquals(1, declDefs.size());		
		return declDefs.get(0);
	}
	
	protected Declaration getSingleDeclarationWithProblems(String source, int numberOfProblems, int apiLevel) {
		List<Declaration> declDefs = getDeclarationsWithProblems(source, numberOfProblems, apiLevel);
		assertEquals(1, declDefs.size());		
		return declDefs.get(0);
	}
	
	protected Expression parseExpression(String source) {
		Expression exp = new ParserFacade().parseExpression(source, AST.LATEST);
		return exp;
	}
	
	protected Expression parseExpression(String source, int apiLevel) {
		Expression exp = new ParserFacade().parseExpression(source, apiLevel);
		return exp;
	}
	
	protected Initializer parseInitializer(String source) {
		Initializer init = new ParserFacade().parseInitializer(source);
		return init;
	}
	
	protected Statement parseStatement(String source) {
		return parseStatement(source, AST.LATEST);
	}
	
	protected Statement parseStatement(String source, int apiLevel) {
		Statement stm = new ParserFacade().parseStatement(source, apiLevel);
		return stm;
	}
	
	protected ModuleDeclaration getModuleDeclaration(String source) {
		ModuleDeclaration md = new ParserFacade().parseCompilationUnit(source, AST.LATEST).getModuleDeclaration();
		return md;
	}
	
	protected CompilationUnit getCompilationUnit(String source) {
		return getCompilationUnit(source, AST.LATEST);
	}
	
	protected CompilationUnit getCompilationUnit(String source, int apiLevel) {
		CompilationUnit unit = new ParserFacade().parseCompilationUnit(source, apiLevel);
		return unit;
	}
	
	protected Module getModule(String source) {
		Parser parser = new Parser(AST.newAST(AST.D2), source);
		return parser.parseModuleObj();
	}
	
	protected Module getModuleSemantic(String source) {
		Module module = getModule(source);
		return CompilationUnitResolver.semantic1(module);
	}
	
	protected IProblem[] getModuleProblems(String source) {
		Module module = getModuleSemantic(source);
		return module.problems.toArray(new IProblem[module.problems.size()]);
	}
	
	protected void assertOriginal(ASTNode elem) {
		assertTrue((elem.getFlags() & ASTNode.ORIGINAL) > 0);
	}
	
	protected void assertMalformed(ASTNode elem) {
		assertTrue((elem.getFlags() & ASTNode.MALFORMED) > 0);
	}
	
	protected void assertRecovered(ASTNode elem) {
		assertTrue((elem.getFlags() & ASTNode.RECOVERED) > 0);
	}
	
	protected void assertPosition(ASTNode elem, int start, int length) {
		assertEquals(start, elem.getStartPosition());
		assertEquals(length, elem.getLength());
	}
	
	protected void assertExtendedPosition(ASTNode elem, int start, int length, CompilationUnit unit) {
		assertEquals(start, unit.getExtendedStartPosition(elem));
		assertEquals(length, unit.getExtendedLength(elem));
	}
	
	protected void assertError(IProblem p, int errorCode, int start, int length) {
		assertEquals(errorCode, p.getID());
		assertTrue(p.isError());
		assertEquals(start, p.getSourceStart());
		assertEquals(length, p.getSourceEnd() - p.getSourceStart() + 1);
	}
	
	protected void assertWarning(IProblem p, int errorCode, int start, int length) {
		assertEquals(errorCode, p.getID());
		assertTrue(p.isError());
		assertEquals(start, p.getSourceStart());
		assertEquals(length, p.getSourceEnd() - p.getSourceStart() + 1);
	}

}
