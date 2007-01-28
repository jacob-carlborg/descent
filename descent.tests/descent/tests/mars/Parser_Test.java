package descent.tests.mars;

import java.util.List;

import junit.framework.TestCase;
import descent.core.compiler.IProblem;
import descent.core.dom.ASTNode;
import descent.core.dom.CompilationUnit;
import descent.core.dom.Declaration;
import descent.core.dom.Expression;
import descent.core.dom.Initializer;
import descent.core.dom.ModuleDeclaration;
import descent.core.dom.Statement;

public abstract class Parser_Test extends TestCase {
	
	protected final static int SEVERITY_ERROR = 1;
	protected final static int SEVERITY_WARNING = 2;
	
	protected List<Declaration> getDeclarationsWithProblems(String source, int numberOfProblems) {
		CompilationUnit unit = new ParserFacade().parseCompilationUnit(source);
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
	
	protected Expression parseExpression(String source) {
		Expression exp = new ParserFacade().parseExpression(source);
		return exp;
	}
	
	protected Initializer parseInitializer(String source) {
		Initializer init = new ParserFacade().parseInitializer(source);
		return init;
	}
	
	protected Statement parseStatement(String source) {
		Statement stm = new ParserFacade().parseStatement(source);
		return stm;
	}
	
	protected ModuleDeclaration getModuleDeclaration(String source) {
		ModuleDeclaration md = new ParserFacade().parseCompilationUnit(source).getModuleDeclaration();
		return md;
	}
	
	protected CompilationUnit getCompilationUnit(String source) {
		CompilationUnit unit = new ParserFacade().parseCompilationUnit(source);
		return unit;
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
		assertEquals(length, p.getSourceEnd() - p.getSourceStart());
	}
	
	protected void assertWarning(IProblem p, int errorCode, int start, int length) {
		assertEquals(errorCode, p.getID());
		assertTrue(p.isError());
		assertEquals(start, p.getSourceStart());
		assertEquals(length, p.getSourceEnd() - p.getSourceStart());
	}

}
