package descent.tests.rewrite;

import descent.core.dom.ArrayLiteral;
import descent.core.dom.Assignment;
import descent.core.dom.Block;
import descent.core.dom.ExpressionStatement;
import descent.core.dom.FunctionDeclaration;

public class RewriteArrayLiteralTest extends RewriteTest {
	
	public void testAddArgument() throws Exception {
		begin("void bla() { a = [b]; }");
		
		FunctionDeclaration func = (FunctionDeclaration) unit.declarations().get(0);
		ExpressionStatement exp = (ExpressionStatement) ((Block) func.getBody()).statements().get(0);
		Assignment assignment = (Assignment) exp.getExpression();
		ArrayLiteral literal = (ArrayLiteral) assignment.getRightHandSide();
		literal.arguments().add(ast.newSimpleName("c"));
		
		assertEqualsTokenByToken("void bla() { a = [b, c]; }", end());
	}
	
	public void testRemoveArgument() throws Exception {
		begin("void bla() { a = [b]; }");
		
		FunctionDeclaration func = (FunctionDeclaration) unit.declarations().get(0);
		ExpressionStatement exp = (ExpressionStatement) ((Block) func.getBody()).statements().get(0);
		Assignment assignment = (Assignment) exp.getExpression();
		ArrayLiteral literal = (ArrayLiteral) assignment.getRightHandSide();
		literal.arguments().clear();
		
		assertEqualsTokenByToken("void bla() { a = []; }", end());
	}

}
