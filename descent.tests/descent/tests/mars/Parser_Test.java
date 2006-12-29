package descent.tests.mars;

import java.util.List;

import junit.framework.TestCase;
import descent.core.dom.ASTNode;
import descent.core.dom.CompilationUnit;
import descent.core.dom.Declaration;
import descent.core.dom.Expression;
import descent.core.dom.Initializer;
import descent.core.dom.ModuleDeclaration;
import descent.core.dom.Statement;

public abstract class Parser_Test extends TestCase {
	
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
		return new ParserFacade().parseExpression(source);
	}
	
	protected Initializer parseInitializer(String source) {
		return new ParserFacade().parseInitializer(source);
	}
	
	protected Statement parseStatement(String source) {
		return new ParserFacade().parseStatement(source);
	}
	
	protected ModuleDeclaration getModuleDeclaration(String source) {
		return new ParserFacade().parseCompilationUnit(source).getModuleDeclaration();
	}
	
	protected CompilationUnit getCompilationUnit(String source) {
		return new ParserFacade().parseCompilationUnit(source);
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

}
