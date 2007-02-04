package descent.tests.rewrite;

import descent.core.dom.ArrayAccess;
import descent.core.dom.Assignment;
import descent.core.dom.Block;
import descent.core.dom.ExpressionStatement;
import descent.core.dom.FunctionDeclaration;

public class RewriteArrayAccessTest extends RewriteTest {
	
	public void testChangeArray() throws Exception {
		begin("void bla() { a = b[c, d]; }");
		
		FunctionDeclaration func = (FunctionDeclaration) unit.declarations().get(0);
		ExpressionStatement exp = (ExpressionStatement) ((Block) func.getBody()).statements().get(0);
		Assignment assignment = (Assignment) exp.getExpression();
		ArrayAccess access = (ArrayAccess) assignment.getRightHandSide();
		access.setArray(ast.newSimpleName("changed"));
		
		assertEqualsTokenByToken("void bla() { a = changed[c, d]; }", end());
	}
	
	public void testAddIndex() throws Exception {
		begin("void bla() { a = b[c]; }");
		
		FunctionDeclaration func = (FunctionDeclaration) unit.declarations().get(0);
		ExpressionStatement exp = (ExpressionStatement) ((Block) func.getBody()).statements().get(0);
		Assignment assignment = (Assignment) exp.getExpression();
		ArrayAccess access = (ArrayAccess) assignment.getRightHandSide();
		access.indexes().add(ast.newSimpleName("d"));
		
		assertEqualsTokenByToken("void bla() { a = b[c, d]; }", end());
	}
	
	public void testChangeIndex() throws Exception {
		begin("void bla() { a = b[c]; }");
		
		FunctionDeclaration func = (FunctionDeclaration) unit.declarations().get(0);
		ExpressionStatement exp = (ExpressionStatement) ((Block) func.getBody()).statements().get(0);
		Assignment assignment = (Assignment) exp.getExpression();
		ArrayAccess access = (ArrayAccess) assignment.getRightHandSide();
		access.indexes().set(0, ast.newSimpleName("d"));
		
		assertEqualsTokenByToken("void bla() { a = b[d]; }", end());
	}
	
	public void testRemoveIndex() throws Exception {
		begin("void bla() { a = b[c, d]; }");
		
		FunctionDeclaration func = (FunctionDeclaration) unit.declarations().get(0);
		ExpressionStatement exp = (ExpressionStatement) ((Block) func.getBody()).statements().get(0);
		Assignment assignment = (Assignment) exp.getExpression();
		ArrayAccess access = (ArrayAccess) assignment.getRightHandSide();
		access.indexes().get(1).delete();
		
		assertEqualsTokenByToken("void bla() { a = b[c]; }", end());
	}

}
