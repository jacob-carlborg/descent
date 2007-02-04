package descent.tests.rewrite;

import descent.core.dom.ArrayAccess;
import descent.core.dom.ArrayLiteral;
import descent.core.dom.AssertExpression;

public class RewriteExpressionTest extends AbstractRewriteTest {
	
	public void testArrayAccessChangeArray() throws Exception {
		ArrayAccess access = (ArrayAccess) beginExpression("b[c, d]");
		access.setArray(ast.newSimpleName("changed"));
		assertExpressionEqualsTokenByToken("changed[c, d]", end());
	}
	
	public void testArrayAccessAddIndex() throws Exception {
		ArrayAccess access = (ArrayAccess) beginExpression("b[c]");
		access.indexes().add(ast.newSimpleName("d"));
		assertExpressionEqualsTokenByToken("b[c, d]", end());
	}
	
	public void testArrayAccessChangeIndex() throws Exception {
		ArrayAccess access = (ArrayAccess) beginExpression("b[c]");
		access.indexes().set(0, ast.newSimpleName("d"));
		assertExpressionEqualsTokenByToken("b[d]", end());
	}
	
	public void testArrayAccessRemoveIndex() throws Exception {
		ArrayAccess access = (ArrayAccess) beginExpression("b[c, d]");
		access.indexes().get(1).delete();
		assertExpressionEqualsTokenByToken("b[c]", end());
	}
	
	public void testArrayLiteralAddArgument() throws Exception {
		ArrayLiteral literal = (ArrayLiteral) beginExpression("[b]");
		literal.arguments().add(ast.newSimpleName("c"));		
		assertExpressionEqualsTokenByToken("[b, c]", end());
	}
	
	public void testArrayLiteralRemoveArgument() throws Exception {
		ArrayLiteral literal = (ArrayLiteral) beginExpression("[b]");
		literal.arguments().clear();		
		assertExpressionEqualsTokenByToken("[]", end());
	}
	
	public void testAssertExpressionChangeExpression() throws Exception {
		AssertExpression exp = (AssertExpression) beginExpression("assert(x)");
		exp.setExpression(ast.newSimpleName("y"));
		assertExpressionEqualsTokenByToken("assert(y)", end());
	}
	
	public void testAssertExpressionAddMessage() throws Exception {
		AssertExpression exp = (AssertExpression) beginExpression("assert(x)");
		exp.setMessage(ast.newSimpleName("y"));
		assertExpressionEqualsTokenByToken("assert(x, y)", end());
	}
	
	public void testAssertExpressionRemoveMessage() throws Exception {
		AssertExpression exp = (AssertExpression) beginExpression("assert(x, y)");
		exp.getMessage().delete();
		assertExpressionEqualsTokenByToken("assert(x)", end());
	}

}
