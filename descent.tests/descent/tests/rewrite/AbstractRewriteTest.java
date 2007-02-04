package descent.tests.rewrite;

import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.TextEdit;

import descent.core.compiler.IScanner;
import descent.core.compiler.ITerminalSymbols;
import descent.core.dom.AST;
import descent.core.dom.AggregateDeclaration;
import descent.core.dom.Assignment;
import descent.core.dom.Block;
import descent.core.dom.CompilationUnit;
import descent.core.dom.Expression;
import descent.core.dom.ExpressionStatement;
import descent.core.dom.FunctionDeclaration;
import descent.core.dom.Initializer;
import descent.core.dom.Statement;
import descent.core.dom.TemplateParameter;
import descent.core.dom.ToolFactory;
import descent.core.dom.Type;
import descent.core.dom.VariableDeclaration;
import descent.tests.mars.Parser_Test;

public abstract class AbstractRewriteTest extends Parser_Test {
	
	protected CompilationUnit unit;
	protected Document document;
	protected AST ast;
	
	protected void begin(String source) {
		document = new Document(source);
		unit = getCompilationUnit(document.get());
		unit.recordModifications();
		ast = unit.getAST();
	}
	
	protected String end() throws Exception {
		TextEdit edit = unit.rewrite(document, null);
		edit.apply(document);		
		return document.get().trim();
	}
	
	protected Expression beginExpression(String s) {
		begin("void bla() { a = " + s + "; }");
		
		FunctionDeclaration func = (FunctionDeclaration) unit.declarations().get(0);
		ExpressionStatement exp = (ExpressionStatement) ((Block) func.getBody()).statements().get(0);
		Assignment assignment = (Assignment) exp.getExpression();
		return assignment.getRightHandSide();
	}
	
	protected void assertExpressionEqualsTokenByToken(String s1, String s2) throws Exception {
		assertEqualsTokenByToken("void bla() { a = " + s1 + "; }", s2);
	}
	
	protected Initializer beginInitializer(String s) {
		begin("int[] x = " + s + ";");
		VariableDeclaration var = (VariableDeclaration) unit.declarations().get(0);
		return var.fragments().get(0).getInitializer();
	}
	
	protected void assertInitializerEqualsTokenByToken(String s1, String s2) throws Exception {
		assertEqualsTokenByToken("int[] x = " + s1 + ";", s2);
	}
	
	protected TemplateParameter beginTemplateParameter(String s) {
		begin("class C (" + s + ") { }");
		
		AggregateDeclaration agg = (AggregateDeclaration) unit.declarations().get(0);
		return agg.templateParameters().get(0);
	}
	
	protected void assertTemplateParameterEqualsTokenByToken(String s1, String s2) throws Exception {
		assertEqualsTokenByToken("class C (" + s1 + ") { }", s2);
	}
	
	protected Statement beginStatement(String s) {
		begin("void bla() { " + s + " }");
		
		FunctionDeclaration func = (FunctionDeclaration) unit.declarations().get(0);
		return ((Block) func.getBody()).statements().get(0);
	}
	
	protected void assertStatementEqualsTokenByToken(String s1, String s2) throws Exception {
		assertEqualsTokenByToken("void bla() { " + s1 + " }", s2);
	}
	
	protected Type beginType(String s) {
		begin(s + " x;");
		
		VariableDeclaration var = (VariableDeclaration) unit.declarations().get(0);
		return var.getType();
	}
	
	protected void assertTypeEqualsTokenByToken(String s1, String s2) throws Exception {
		assertEqualsTokenByToken(s1 + " x;", s2);
	}
	
	protected void assertEqualsTokenByToken(String document1, String document2) throws Exception {
		IScanner scanner1 = ToolFactory.createScanner(true, true, false, false, AST.D1);
		IScanner scanner2 = ToolFactory.createScanner(true, true, false, false, AST.D1);
		
		scanner1.setSource(document1.toCharArray());
		scanner2.setSource(document2.toCharArray());
		
		int token1 = scanner1.getNextToken();
		int token2 = scanner2.getNextToken();
		
		try {
			while(token1 == token2) {
				if (token1 == ITerminalSymbols.TokenNameEOF) {
					return;
				}
				assertEquals(scanner1.getRawTokenSource(), scanner2.getRawTokenSource());
				
				token1 = scanner1.getNextToken();
				token2 = scanner2.getNextToken();
			}
		} catch (Throwable t) {
		}
		fail("'" + document1 + "' is not equal to '" + document2 + "'");
	}
	
	protected void assertEquals(char[] s1, char[] s2) {
		assertEquals(s1.length, s2.length);
		for(int i = 0; i < s1.length; i++) {
			assertEquals(s1[i], s2[i]);
		}
	}

}
