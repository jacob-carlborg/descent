package descent.tests.rewrite;

import descent.core.dom.ArrayAccess;
import descent.core.dom.ArrayLiteral;
import descent.core.dom.AssertExpression;
import descent.core.dom.CallExpression;
import descent.core.dom.CastExpression;
import descent.core.dom.DeleteExpression;
import descent.core.dom.PrimitiveType;

public class RewriteExpressionTest extends AbstractRewriteTest {
	
	public void testArrayAccessChangeArray() throws Exception {
		ArrayAccess access = (ArrayAccess) beginExpression("b[c, d]");
		access.setArray(ast.newSimpleName("changed"));
		assertExpressionEqualsTokenByToken("changed[c, d]", end());
	}
	
	public void testArrayAccessAddIndex() throws Exception {
		ArrayAccess access = (ArrayAccess) beginExpression("b[c]");
		access.indexes().add(ast.newSimpleName("theNew"));
		assertExpressionEqualsTokenByToken("b[c, theNew]", end());
	}
	
	public void testArrayAccessChangeIndex() throws Exception {
		ArrayAccess access = (ArrayAccess) beginExpression("b[c]");
		access.indexes().set(0, ast.newSimpleName("theNew"));
		assertExpressionEqualsTokenByToken("b[theNew]", end());
	}
	
	public void testArrayAccessRemoveIndex() throws Exception {
		ArrayAccess access = (ArrayAccess) beginExpression("b[c, d]");
		access.indexes().get(1).delete();
		assertExpressionEqualsTokenByToken("b[c]", end());
	}
	
	public void testArrayLiteralAddArgument() throws Exception {
		ArrayLiteral literal = (ArrayLiteral) beginExpression("[b]");
		literal.arguments().add(ast.newSimpleName("theNew"));		
		assertExpressionEqualsTokenByToken("[b, theNew]", end());
	}
	
	public void testArrayLiteralRemoveArgument() throws Exception {
		ArrayLiteral literal = (ArrayLiteral) beginExpression("[b]");
		literal.arguments().clear();		
		assertExpressionEqualsTokenByToken("[]", end());
	}
	
	public void testAssertExpressionChangeExpression() throws Exception {
		AssertExpression exp = (AssertExpression) beginExpression("assert(x)");
		exp.setExpression(ast.newSimpleName("theNew"));
		assertExpressionEqualsTokenByToken("assert(theNew)", end());
	}
	
	public void testAssertExpressionAddMessage() throws Exception {
		AssertExpression exp = (AssertExpression) beginExpression("assert(x)");
		exp.setMessage(ast.newSimpleName("theNew"));
		assertExpressionEqualsTokenByToken("assert(x, theNew)", end());
	}
	
	public void testAssertExpressionRemoveMessage() throws Exception {
		AssertExpression exp = (AssertExpression) beginExpression("assert(x, y)");
		exp.getMessage().delete();
		assertExpressionEqualsTokenByToken("assert(x)", end());
	}
	
	//
	public void testCallExpressionChangeExpression() throws Exception {
		CallExpression exp = (CallExpression) beginExpression("b(c, d)");
		exp.setExpression(ast.newSimpleName("changed"));
		assertExpressionEqualsTokenByToken("changed(c, d)", end());
	}
	
	public void testCallExpressionAddArgument() throws Exception {
		CallExpression exp = (CallExpression) beginExpression("b(c)");
		exp.arguments().add(ast.newSimpleName("theNew"));
		assertExpressionEqualsTokenByToken("b(c, theNew)", end());
	}
	
	public void testCallExpressionChangeArgument() throws Exception {
		CallExpression exp = (CallExpression) beginExpression("b(c)");
		exp.arguments().set(0, ast.newSimpleName("theNew"));
		assertExpressionEqualsTokenByToken("b(theNew)", end());
	}
	
	public void testCallExpressionRemoveArgument() throws Exception {
		CallExpression exp = (CallExpression) beginExpression("b(c, d)");
		exp.arguments().get(1).delete();
		assertExpressionEqualsTokenByToken("b(c)", end());
	}
	
	public void testCaseExpressionChangeType() throws Exception {
		CastExpression cast = (CastExpression) beginExpression("cast(int) 2");
		cast.setType(ast.newPrimitiveType(PrimitiveType.Code.LONG));
		assertExpressionEqualsTokenByToken("cast(long) 2", end());
	}
	
	public void testCaseExpressionChangeExpression() throws Exception {
		CastExpression cast = (CastExpression) beginExpression("cast(int) 2");
		cast.setExpression(ast.newSimpleName("theNew"));
		assertExpressionEqualsTokenByToken("cast(int) theNew", end());
	}
	
	public void testDeleteExpressionChangeExpression() throws Exception {
		DeleteExpression del = (DeleteExpression) beginExpression("delete theOld");
		del.setExpression(ast.newSimpleName("theNew"));
		assertExpressionEqualsTokenByToken("delete theNew", end());
	}

}
