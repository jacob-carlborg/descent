package descent.tests.rewrite;

import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.TextEdit;

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
import descent.core.dom.Type;
import descent.core.dom.VariableDeclaration;
import descent.tests.mars.Parser_Test;
import descent.tests.utils.Util;

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
		assertTrue(Util.equalsTokenByToken(document1, document2));
	}

}
