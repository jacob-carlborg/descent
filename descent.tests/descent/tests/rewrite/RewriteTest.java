package descent.tests.rewrite;

import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.TextEdit;

import descent.core.compiler.IScanner;
import descent.core.compiler.ITerminalSymbols;
import descent.core.dom.AST;
import descent.core.dom.Block;
import descent.core.dom.BooleanLiteral;
import descent.core.dom.CompilationUnit;
import descent.core.dom.DoStatement;
import descent.core.dom.ExpressionStatement;
import descent.core.dom.FunctionDeclaration;
import descent.core.dom.IfStatement;
import descent.core.dom.Import;
import descent.core.dom.ImportDeclaration;
import descent.core.dom.InfixExpression;
import descent.core.dom.ModuleDeclaration;
import descent.core.dom.PostfixExpression;
import descent.core.dom.ToolFactory;
import descent.core.dom.rewrite.ASTRewrite;
import descent.core.dom.rewrite.ListRewrite;
import descent.tests.mars.Parser_Test;

public class RewriteTest extends Parser_Test {
	
	private CompilationUnit unit;
	private Document document;
	private ASTRewrite rewriter;
	private AST ast;
	
	private void begin(String source) {
		document = new Document(source);
		unit = getCompilationUnit(document.get());
		ast = unit.getAST();
		rewriter = ASTRewrite.create(ast);
	}
	
	private String end() throws Exception {
		TextEdit edit = rewriter.rewriteAST(document, null);
		edit.apply(document);
		
		System.out.println(document.get());
		System.out.println("-------------------------------------------");
		
		return document.get().trim();
	}
	
	public void testInsertModuleDeclaration() throws Exception {
		begin("");
		
		ModuleDeclaration module = ast.newModuleDeclaration();
		module.setName(ast.newSimpleName("hola"));
		
		rewriter.set(unit, CompilationUnit.MODULE_DECLARATION_PROPERTY, module, null);
		
		assertEqualsTokenByToken("module hola;", end());
	}
	
	public void testInsertModuleDeclarationWithComments() throws Exception {
		begin("");
		
		ModuleDeclaration module = ast.newModuleDeclaration();
		module.setName(ast.newSimpleName("hola"));
		module.preDDocs().add(ast.newDDocComment("/** hello! */"));
		
		rewriter.set(unit, CompilationUnit.MODULE_DECLARATION_PROPERTY, module, null);
		
		assertEqualsTokenByToken("/** hello! */ module hola;", end());
	}
	
	public void testInsertImportWithModule() throws Exception {
		begin("module hola;");
		
		ImportDeclaration importDeclaration = ast.newImportDeclaration();
		Import imp = ast.newImport();
		imp.setName(ast.newName(new String[] { "std",  "stdio" }));
		importDeclaration.imports().add(imp);
		
		ListRewrite lrw = rewriter.getListRewrite(unit, CompilationUnit.DECLARATIONS_PROPERTY);
		lrw.insertFirst(importDeclaration, null);
		
		assertEqualsTokenByToken("module hola; import std.stdio;", end());
	}
	
	public void testInsertTwoImportsWithModule() throws Exception {
		begin("module hola;");
		
		ImportDeclaration importDeclaration = ast.newImportDeclaration();
		Import imp = ast.newImport();
		imp.setName(ast.newName(new String[] { "std",  "stdio" }));
		importDeclaration.imports().add(imp);
		
		ImportDeclaration importDeclaration2 = ast.newImportDeclaration();
		Import imp2 = ast.newImport();
		imp2.setName(ast.newName(new String[] { "std",  "something" }));
		importDeclaration2.imports().add(imp2);
		
		ListRewrite lrw = rewriter.getListRewrite(unit, CompilationUnit.DECLARATIONS_PROPERTY);
		lrw.insertFirst(importDeclaration, null);
		lrw.insertAfter(importDeclaration2, importDeclaration, null);
		
		assertEqualsTokenByToken("module hola; import std.stdio; import std.something;", end());
	}
	
	public void testDeleteStatementFromFunction() throws Exception {
		begin("int bla() { if (true) { return 1; } else { return 2; } }");
		
		FunctionDeclaration function = (FunctionDeclaration) unit.declarations().get(0);
		Block block = (Block) function.getBody();
		
		ListRewrite lrw = rewriter.getListRewrite(block, Block.STATEMENTS_PROPERTY);
		lrw.remove(block.statements().get(0), null);
		
		assertEqualsTokenByToken("int bla() { }", end());
	}
	
	public void testReplaceExpressionValueInIf() throws Exception {
		begin("int bla() { if (true) { return 1; } else { return 2; } }");
		
		FunctionDeclaration function = (FunctionDeclaration) unit.declarations().get(0);
		Block block = (Block) function.getBody();
		
		IfStatement ifStatement = (IfStatement) block.statements().get(0);
		BooleanLiteral condition = (BooleanLiteral) ifStatement.getExpression();
		
		rewriter.set(condition, BooleanLiteral.BOOLEAN_VALUE_PROPERTY, false, null);
		
		assertEqualsTokenByToken("int bla() { if (false) { return 1; } else { return 2; } }", end());
	}
	
	public void testReplaceExpressionInIf() throws Exception {
		begin("int bla() { if (true) { return 1; } else { return 2; } }");
		
		FunctionDeclaration function = (FunctionDeclaration) unit.declarations().get(0);
		Block block = (Block) function.getBody();
		
		IfStatement ifStatement = (IfStatement) block.statements().get(0);
		
		InfixExpression infix = ast.newInfixExpression();
		infix.setLeftOperand(ast.newSimpleName("a"));
		infix.setOperator(InfixExpression.Operator.GREATER);
		infix.setRightOperand(ast.newSimpleName("b"));
		
		rewriter.set(ifStatement, IfStatement.EXPRESSION_PROPERTY, infix, null);
		
		assertEqualsTokenByToken("int bla() { if (a > b) { return 1; } else { return 2; } }", end());
	}
	
	public void testReplaceDoBody() throws Exception {
		begin("int bla() { do { i++; } while(i < 3); }");
		
		FunctionDeclaration function = (FunctionDeclaration) unit.declarations().get(0);
		Block functionBlock = (Block) function.getBody();
		
		DoStatement doStatement = (DoStatement) functionBlock.statements().get(0);
		
		Block newBlock = ast.newBlock();
		PostfixExpression decrement = ast.newPostfixExpression();
		decrement.setOperand(ast.newSimpleName("j"));
		decrement.setOperator(PostfixExpression.Operator.DECREMENT);
		
		ExpressionStatement statement = ast.newExpressionStatement();
		statement.setExpression(decrement);
		newBlock.statements().add(statement);
		
		rewriter.replace(doStatement.getBody(), newBlock, null);
		
		assertEqualsTokenByToken("int bla() { do { j--; } while(i < 3); }", end());
	}
	
	private void assertEqualsTokenByToken(String document1, String document2) throws Exception {
		IScanner scanner1 = ToolFactory.createScanner(true, true, false, false, AST.D1);
		IScanner scanner2 = ToolFactory.createScanner(true, true, false, false, AST.D1);
		
		scanner1.setSource(document1.toCharArray());
		scanner2.setSource(document2.toCharArray());
		
		int token1 = scanner1.getNextToken();
		int token2 = scanner2.getNextToken();
		
		while(token1 == token2) {
			if (token1 == ITerminalSymbols.TokenNameEOF) {
				return;
			}
			assertEquals(scanner1.getRawTokenSource(), scanner2.getRawTokenSource());
			
			token1 = scanner1.getNextToken();
			token2 = scanner2.getNextToken();
		}
		
		fail("'" + document1 + "' is not equal to '" + document2 + "'");
	}
	
	private void assertEquals(char[] s1, char[] s2) {
		assertEquals(s1.length, s2.length);
		for(int i = 0; i < s1.length; i++) {
			assertEquals(s1[i], s2[i]);
		}
	}

}
