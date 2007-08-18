package descent.tests.mars;

import java.util.List;

import junit.framework.TestCase;
import descent.core.compiler.IProblem;
import descent.core.dom.AST;
import descent.core.dom.ASTNode;
import descent.core.dom.Block;
import descent.core.dom.CompilationUnit;
import descent.core.dom.CompilationUnitResolver;
import descent.core.dom.Declaration;
import descent.core.dom.Expression;
import descent.core.dom.FunctionDeclaration;
import descent.core.dom.GenericVisitor;
import descent.core.dom.Initializer;
import descent.core.dom.ModuleDeclaration;
import descent.core.dom.Statement;
import descent.core.dom.CompilationUnitResolver.ParseResult;
import descent.internal.compiler.parser.Module;

public abstract class Parser_Test extends TestCase {
	
	protected final static int SEVERITY_ERROR = 1;
	protected final static int SEVERITY_WARNING = 2;
	
	protected List<Declaration> getDeclarationsWithProblems(String source, int numberOfProblems) {
		return getDeclarationsWithProblems(source, numberOfProblems, AST.D1);
	}
	
	protected List<Declaration> getDeclarationsWithProblems(String source, int numberOfProblems, int apiLevel) {
		CompilationUnit unit = getCompilationUnit(source, apiLevel);
		assertEquals(numberOfProblems, unit.getProblems().length);
		return unit.declarations();
	}
	
	protected List<Declaration> getDeclarationsNoProblems(String source) {
		return getDeclarationsWithProblems(source, 0);
	}
	
	protected List<Declaration> getDeclarationsNoProblems(String source, int apiLevel) {
		return getDeclarationsWithProblems(source, 0, apiLevel);
	}
	
	protected Declaration getSingleDeclarationNoProblems(String source) {
		List<Declaration> declDefs = getDeclarationsNoProblems(source);
		assertEquals(1, declDefs.size());		
		return declDefs.get(0);
	}
	
	protected Declaration getSingleDeclarationNoProblems(String source, int apiLevel) {
		List<Declaration> declDefs = getDeclarationsNoProblems(source, apiLevel);
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
		Expression exp = new ParserFacade().parseExpression(source, AST.D1);
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
		return parseStatement(source, AST.D1);
	}
	
	protected Statement parseStatement(String source, int apiLevel) {
		source = "void x_x() { " + source + " }";
		
		CompilationUnit unit = getCompilationUnit(source, apiLevel);
		FunctionDeclaration func = (FunctionDeclaration) unit.declarations().get(0);
		Block block = (Block) func.getBody();
		Statement stm = block.statements().get(0);
		stm.accept(new GenericVisitor() {
			@Override
			protected boolean visitNode(ASTNode node) {
				node.setSourceRange(node.getStartPosition() - 13, node.getLength());
				return true;
			}
		});
		return stm;
	}
	
	protected ModuleDeclaration getModuleDeclaration(String source) {
		ModuleDeclaration md = new ParserFacade().parseCompilationUnit(source, AST.D1).getModuleDeclaration();
		return md;
	}
	
	protected CompilationUnit getCompilationUnit(String source) {
		return getCompilationUnit(source, AST.D1);
	}
	
	protected CompilationUnit getCompilationUnit(String source, int apiLevel) {
		ParseResult result = getParseResult(source, apiLevel);
		// For syntaxis analysis do semantic to see if this breaks
		// something in ASTConverter while keeping only syntaxis problems
		/*
		module.semantic(new SemanticContext(new IProblemRequestor() {
			public void acceptProblem(IProblem problem) {
			}
			public void beginReporting() {
			}
			public void endReporting() {
			}
			public boolean isActive() {
				return false;
			}
		}, module.ast));
		*/
		return CompilationUnitResolver.convert(AST.newAST(apiLevel), result, null);
	}
	
	protected ParseResult getParseResult(String source, int apiLevel) {
		return CompilationUnitResolver.parse(apiLevel, source.toCharArray(), null, true);
	}
	
	protected Module getModuleSemantic(String source, int apiLevel) {
		ParseResult result = getParseResult(source, apiLevel);
		return CompilationUnitResolver.resolve(result.module);
	}
	
	protected IProblem[] getModuleProblems(String source) {
		Module module = getModuleSemantic(source, AST.D1);
		return module.problems.toArray(new IProblem[module.problems.size()]);
	}
	
	protected void assertNoSemanticErrors(String source) {
		assertEquals(0, getModuleProblems(source).length);
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
