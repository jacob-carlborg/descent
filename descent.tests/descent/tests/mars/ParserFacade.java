package descent.tests.mars;

import descent.core.dom.AST;
import descent.core.dom.ASTNode;
import descent.core.dom.ASTParser;
import descent.core.dom.Block;
import descent.core.dom.CompilationUnit;
import descent.core.dom.Expression;
import descent.core.dom.FunctionDeclaration;
import descent.core.dom.GenericVisitor;
import descent.core.dom.Initializer;
import descent.core.dom.Statement;

public class ParserFacade {
	
	public CompilationUnit parseCompilationUnit(String source, int apiLevel) {
		ASTParser parser = ASTParser.newParser(apiLevel);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(source.toCharArray());
		return (CompilationUnit) parser.createAST(null);
	}
	
	public Expression parseExpression(String source, int apiLevel) {
		ASTParser parser = ASTParser.newParser(apiLevel);
		parser.setKind(ASTParser.K_EXPRESSION);
		parser.setSource(source.toCharArray());
		return (Expression) parser.createAST(null);
	}
	
	public Statement parseStatement(String source) {
		return parseStatement(source, AST.LATEST);
	}
	
	public Statement parseStatement(String source, int apiLevel) {
		source = "void x_x() { " + source + " }";
		
		CompilationUnit unit = parseCompilationUnit(source, apiLevel);
		unit.accept(new GenericVisitor() {
			@Override
			protected boolean visitNode(ASTNode node) {
				node.setSourceRange(node.getStartPosition() - 13, node.getLength());
				return true;
			}
		});
		FunctionDeclaration func = (FunctionDeclaration) unit.declarations().get(0);
		Block block = (Block) func.getBody();
		return block.statements().get(0);
		
		/*
		ASTParser parser = ASTParser.newParser(apiLevel);
		parser.setKind(ASTParser.K_STATEMENT);
		parser.setSource(source.toCharArray());
		return (Statement) parser.createAST(null);
		*/
	}
	
	public Initializer parseInitializer(String source) {
		ASTParser parser = ASTParser.newParser(AST.LATEST);
		parser.setKind(ASTParser.K_INITIALIZER);
		parser.setSource(source.toCharArray());
		return (Initializer) parser.createAST(null);
	}

}
