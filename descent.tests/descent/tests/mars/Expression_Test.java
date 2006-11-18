package descent.tests.mars;

import descent.core.dom.IArrayExpression;
import descent.core.dom.IArrayLiteralExpression;
import descent.core.dom.IAssertExpression;
import descent.core.dom.IBinaryExpression;
import descent.core.dom.ICallExpression;
import descent.core.dom.ICastExpression;
import descent.core.dom.IConditionExpression;
import descent.core.dom.IDeleteExpression;
import descent.core.dom.IDotIdExpression;
import descent.core.dom.IDElement;
import descent.core.dom.IExpression;
import descent.core.dom.IIntegerExpression;
import descent.core.dom.INewExpression;
import descent.core.dom.IParenthesizedExpression;
import descent.core.dom.IScopeExpression;
import descent.core.dom.ISliceExpression;
import descent.core.dom.IStringExpression;
import descent.core.dom.IType;
import descent.core.dom.ITypeDotIdentifierExpression;
import descent.core.dom.ITypeExpression;
import descent.core.dom.IUnaryExpression;
import descent.internal.core.dom.ParserFacade;

public class Expression_Test extends Parser_Test {
	
	public void testThis() {
		String s = " this";
		IExpression expr = new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.EXPRESSION_THIS, expr.getExpressionType());
		assertEquals("this", expr.toString());
		assertPosition(expr, 1, s.length() - 1);
		
		assertVisitor(expr, 1);
	}
	
	public void testSuper() {
		String s = " super";
		IExpression expr = new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.EXPRESSION_SUPER, expr.getExpressionType());
		assertEquals("super", expr.toString());
		assertPosition(expr, 1, s.length() - 1);
		
		assertVisitor(expr, 1);
	}
	
	public void testNull() {
		String s = " null";
		IExpression expr = new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.EXPRESSION_NULL, expr.getExpressionType());
		assertEquals("null", expr.toString());
		assertPosition(expr, 1, s.length() - 1);
		
		assertVisitor(expr, 1);
	}
	
	public void testTrue() {
		String s = " true";
		IExpression expr = new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.EXPRESSION_TRUE, expr.getExpressionType());
		assertEquals("true", expr.toString());
		assertPosition(expr, 1, s.length() - 1);
		
		assertVisitor(expr, 1);
	}
	
	public void testFalse() {
		String s = " false";
		IExpression expr = new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.EXPRESSION_FALSE, expr.getExpressionType());
		assertEquals("false", expr.toString());
		assertPosition(expr, 1, s.length() - 1);
		
		assertVisitor(expr, 1);
	}
	
	public void testString() {
		String s = " \"hola\"";
		IStringExpression expr = (IStringExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.EXPRESSION_STRING, expr.getExpressionType());
		assertEquals("hola", expr.getString());
		assertEquals(0, expr.getPostfix());
		assertPosition(expr, 1, s.length() - 1);
		
		assertVisitor(expr, 1);
	}
	
	public void testStringMany() {
		String s = " \"hola\" \"chau\"";
		IStringExpression expr = (IStringExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.EXPRESSION_STRING, expr.getExpressionType());
		assertEquals("holachau", expr.getString());
		assertEquals(0, expr.getPostfix());
		assertPosition(expr, 1, s.length() - 1);
	}
	
	public void testStringPostfix() {
		String s = " \"hola\"c";
		IStringExpression expr = (IStringExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.EXPRESSION_STRING, expr.getExpressionType());
		assertEquals("hola", expr.getString());
		assertEquals('c', expr.getPostfix());
		assertPosition(expr, 1, s.length() - 1);
	}
	
	public void testStringHex() {
		String s = " x\"1234\"";
		IStringExpression expr = (IStringExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.EXPRESSION_STRING, expr.getExpressionType());
		assertPosition(expr, 1, s.length() - 1);
	}
	
	public void testInt32() {
		String s = " 1234";
		IIntegerExpression expr = (IIntegerExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.EXPRESSION_INTEGER, expr.getExpressionType());
		assertEquals(1234, expr.getValue());
		assertPosition(expr, 1, s.length() - 1);
		
		assertVisitor(expr, 1);
	}
	
	public void testUInt32() {
		String s = " 1234u";
		IIntegerExpression expr = (IIntegerExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.EXPRESSION_INTEGER, expr.getExpressionType());
		assertEquals(1234, expr.getValue());
		assertPosition(expr, 1, s.length() - 1);
	}
	
	public void testInt64() {
		String s = " 1234L";
		IIntegerExpression expr = (IIntegerExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.EXPRESSION_INTEGER, expr.getExpressionType());
		assertEquals(1234, expr.getValue());
		assertPosition(expr, 1, s.length() - 1);
	}
	
	public void testUInt64() {
		String s = " 1234Lu";
		IIntegerExpression expr = (IIntegerExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.EXPRESSION_INTEGER, expr.getExpressionType());
		assertEquals(1234, expr.getValue());
		assertPosition(expr, 1, s.length() - 1);
	}
	
	public void testFloat32() {
		String s = " 1.2f";
		IExpression expr = (IExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.EXPRESSION_REAL, expr.getExpressionType());
		assertPosition(expr, 1, s.length() - 1);
	}
	
	public void testFloat64() {
		String s = " 1.2";
		IExpression expr = (IExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.EXPRESSION_REAL, expr.getExpressionType());
		assertPosition(expr, 1, s.length() - 1);
	}
	
	public void testFloat80() {
		String s = " 1.2L";
		IExpression expr = (IExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.EXPRESSION_REAL, expr.getExpressionType());
		assertPosition(expr, 1, s.length() - 1);
	}
	
	public void testReal() {
		String s = " 1.2";
		IExpression expr = (IExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.EXPRESSION_REAL, expr.getExpressionType());
		assertPosition(expr, 1, s.length() - 1);
		
		assertVisitor(expr, 1);
	}
	
	public void testImaginary32() {
		String s = " 1.2fi";
		IExpression expr = (IExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.EXPRESSION_REAL, expr.getExpressionType());
		assertPosition(expr, 1, s.length() - 1);
	}
	
	public void testImaginary64() {
		String s = " 1.2i";
		IExpression expr = (IExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.EXPRESSION_REAL, expr.getExpressionType());
		assertPosition(expr, 1, s.length() - 1);
	}
	
	public void testImaginary80() {
		String s = " 1.2Li";
		IExpression expr = (IExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.EXPRESSION_REAL, expr.getExpressionType());
		assertPosition(expr, 1, s.length() - 1);
	}
	
	public void testChar() {
		String s = " 'c'";
		IExpression expr = (IExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.EXPRESSION_INTEGER, expr.getExpressionType());
		assertPosition(expr, 1, s.length() - 1);
	}
	
	public void testAssert() {
		String s = " assert(false)";
		IAssertExpression expr = (IAssertExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.EXPRESSION_ASSERT, expr.getExpressionType());
		assertEquals(IExpression.EXPRESSION_FALSE, expr.getExpression().getExpressionType());
		assertNull(expr.getMessage());
		assertPosition(expr, 1, s.length() - 1);
		
		assertVisitor(expr, 2);
	}
	
	public void testAssertWithMessage() {
		String s = " assert(false, true)";
		IAssertExpression expr = (IAssertExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.EXPRESSION_ASSERT, expr.getExpressionType());
		assertEquals(IExpression.EXPRESSION_FALSE, expr.getExpression().getExpressionType());
		assertEquals(IExpression.EXPRESSION_TRUE, expr.getMessage().getExpressionType());
		assertPosition(expr, 1, s.length() - 1);
		
		assertVisitor(expr, 3);
	}
	
	public void testParenthesized() {
		String s = " ( false )";
		IParenthesizedExpression expr = (IParenthesizedExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.EXPRESSION_PARENTHESIZED, expr.getExpressionType());
		assertEquals(IExpression.EXPRESSION_FALSE, expr.getExpression().getExpressionType());
		assertPosition(expr, 1, s.length() - 1);
		
		assertVisitor(expr, 2);
	}
	
	public void testBinary() {
		Object[][] objs = { 
				{ "*", IBinaryExpression.MUL },
				{ "/", IBinaryExpression.DIV },
				{ "%", IBinaryExpression.MOD },
				{ "+", IBinaryExpression.ADD },
				{ "-", IBinaryExpression.MIN },
				{ "~", IBinaryExpression.CAT },
				{ ">>", IBinaryExpression.SHIFT_RIGHT },
				{ "<<", IBinaryExpression.SHIFT_LEFT },
				{ ">>>", IBinaryExpression.UNSIGNED_SHIFT_RIGHT },
				{ "<", IBinaryExpression.CMP },
				{ "in", IBinaryExpression.IN },
				{ "==", IBinaryExpression.EQUAL },
				{ "===", IBinaryExpression.IDENTITY },
				{ "&", IBinaryExpression.AND },
				{ "^", IBinaryExpression.XOR },
				{ "|", IBinaryExpression.OR },
				{ "&&", IBinaryExpression.AND_AND },
				{ "||", IBinaryExpression.OR_OR },
				{ "=", IBinaryExpression.ASSIGN },
				{ "+=", IBinaryExpression.ADD_ASSIGN },
				{ "-=", IBinaryExpression.MIN_ASSIGN },
				{ "*=", IBinaryExpression.MUL_ASSIGN },
				{ "/=", IBinaryExpression.DIV_ASSIGN },
				{ "%=", IBinaryExpression.MOD_ASSIGN },
				{ "&=", IBinaryExpression.AND_ASSIGN },
				{ "|=", IBinaryExpression.OR_ASSIGN },
				{ "^=", IBinaryExpression.XOR_ASSIGN },
				{ "<<=", IBinaryExpression.SHIFT_LEFT_ASSIGN },
				{ ">>=", IBinaryExpression.SHIFT_RIGHT_ASSIGN },
				{ ">>>=", IBinaryExpression.UNSIGNED_SHIFT_RIGHT_ASSIGN },
				{ "~=", IBinaryExpression.CAT_ASSIGN },
				{ ",", IBinaryExpression.COMMA },
			};
		
		for(Object[] pair : objs) {
			String s = " 1 " + pair[0] + " 1.0";
			IBinaryExpression expr = (IBinaryExpression) new ParserFacade().parseExpression(s);
			
			assertEquals(IExpression.EXPRESSION_BINARY, expr.getExpressionType());
			assertEquals(pair[1], expr.getBinaryExpressionType());
			assertEquals(IExpression.EXPRESSION_INTEGER, expr.getLeftExpression().getExpressionType());
			assertEquals(IExpression.EXPRESSION_REAL, expr.getRightExpression().getExpressionType());
			assertPosition(expr, 1, 6 + ((String) pair[0]).length());
			
			assertVisitor(expr, 3);
		}
	}
	
	public void testUnary() {
		Object[][] objs = { 
				{ "&", IUnaryExpression.ADDRESS },
				{ "++", IUnaryExpression.PRE_INCREMENT },
				{ "--", IUnaryExpression.PRE_DECREMENT },
				{ "*", IUnaryExpression.POINTER },
				{ "-", IUnaryExpression.NEGATIVE },
				{ "+", IUnaryExpression.POSITIVE },
				{ "!", IUnaryExpression.NOT },
				{ "~", IUnaryExpression.INVERT },
			};
		
		for(Object[] pair : objs) {
			String s = " " + pair[0] + "1";
			IUnaryExpression expr = (IUnaryExpression) new ParserFacade().parseExpression(s);
			
			assertEquals(IExpression.EXPRESSION_UNARY, expr.getExpressionType());
			assertEquals(pair[1], expr.getUnaryExpressionType());
			assertEquals(IExpression.EXPRESSION_INTEGER, expr.getInnerExpression().getExpressionType());
			assertPosition(expr, 1, 1 + ((String) pair[0]).length());
			
			assertVisitor(expr, 2);
		}
	}
	
	public void testUnary2() {
		Object[][] objs = { 
				{ "++", IUnaryExpression.POST_INCREMENT },
				{ "--", IUnaryExpression.POST_DECREMENT },
			};
		
		for(Object[] pair : objs) {
			String s = " 1" + pair[0];
			IUnaryExpression expr = (IUnaryExpression) new ParserFacade().parseExpression(s);
			
			assertEquals(IExpression.EXPRESSION_UNARY, expr.getExpressionType());
			assertEquals(pair[1], expr.getUnaryExpressionType());
			assertEquals(IExpression.EXPRESSION_INTEGER, expr.getInnerExpression().getExpressionType());
			assertPosition(expr, 1, 1 + ((String) pair[0]).length());
			
			assertVisitor(expr, 2);
		}
	}
	
	public void testCondition() {
		String s = " 1 ? true : false";
		IConditionExpression expr = (IConditionExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.EXPRESSION_CONDITION, expr.getExpressionType());
		assertEquals(IExpression.EXPRESSION_INTEGER, expr.getCondition().getExpressionType());
		assertEquals(IExpression.EXPRESSION_TRUE, expr.getTrue().getExpressionType());
		assertEquals(IExpression.EXPRESSION_FALSE, expr.getFalse().getExpressionType());
		assertPosition(expr, 1, 16);
		
		assertVisitor(expr, 4);
	}
	
	public void testTypeDotId() {
		String s = " int.length";
		ITypeDotIdentifierExpression expr = (ITypeDotIdentifierExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.EXPRESSION_TYPE_DOT_IDENTIFIER, expr.getExpressionType());
		assertPosition(expr, 1, 10);
		
		assertEquals(IType.TYPE_BASIC, expr.getType().getTypeType());
		assertEquals("length", expr.getProperty().toString());
		
		assertVisitor(expr, 3);
	}
	
	public void testDelete() {
		String s = " delete some";
		IDeleteExpression expr = (IDeleteExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.EXPRESSION_DELETE, expr.getExpressionType());
		assertPosition(expr, 1, 11);
		
		assertEquals("some", expr.getExpression().toString());
		
		assertVisitor(expr, 2);
	}
	
	public void testCast() {
		String s = " cast(float) 1";
		ICastExpression expr = (ICastExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.EXPRESSION_CAST, expr.getExpressionType());
		assertPosition(expr, 1, 13);
		
		assertEquals("float", expr.getType().toString());
		assertEquals("1", expr.getExpression().toString());
		
		assertVisitor(expr, 3);
	}
	
	public void testTemplateInstance() {
		String s = " some!(int, float)";
		IScopeExpression expr = (IScopeExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.EXPRESSION_SCOPE, expr.getExpressionType());
		assertPosition(expr, 1, 17);
		
		assertEquals("some", expr.getName().toString());
		assertPosition(expr.getName(), 1, 4);
		
		IDElement[] arguments = expr.getArguments();
		assertEquals(2, arguments.length);
		
		assertEquals("int", arguments[0].toString());
		assertEquals("float", arguments[1].toString());
		
		assertVisitor(expr, 4);
	}
	
	public void testNew() {
		String s = " new Bla(1, 2)";
		INewExpression expr = (INewExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.EXPRESSION_NEW, expr.getExpressionType());
		assertPosition(expr, 1, 13);
		
		IExpression[] args = expr.getArguments();
		assertEquals(2, args.length);
		
		assertEquals("1", args[0].toString());
		assertEquals("2", args[1].toString());
	}
	
	public void testArray() {
		String s = " bla[1, 2, 3]";
		IArrayExpression expr = (IArrayExpression) new ParserFacade().parseExpression(s);
		assertEquals(IExpression.EXPRESSION_ARRAY, expr.getExpressionType());
		
		assertEquals("bla", expr.getExpression().toString());
		
		IExpression[] args = expr.getArguments();
		assertEquals(3, args.length);
		
		assertEquals("1", args[0].toString());
		assertEquals("2", args[1].toString());
		assertEquals("3", args[2].toString());
		
		assertPosition(expr, 1, s.length() - 1);
	}
	
	public void testArrayLiteral() {
		String s = " [1, 2, 3]";
		IArrayLiteralExpression expr = (IArrayLiteralExpression) new ParserFacade().parseExpression(s);
		assertEquals(IExpression.EXPRESSION_ARRAY_LITERAL, expr.getExpressionType());
		
		IExpression[] args = expr.getArguments();
		assertEquals(3, args.length);
		
		assertEquals("1", args[0].toString());
		assertEquals("2", args[1].toString());
		assertEquals("3", args[2].toString());
		
		assertPosition(expr, 1, s.length() - 1);
	}
	
	public void testSlice() {
		String s = " bla[1 .. 3]";
		ISliceExpression expr = (ISliceExpression) new ParserFacade().parseExpression(s);
		assertEquals(IExpression.EXPRESSION_SLICE, expr.getExpressionType());
		
		assertEquals("bla", expr.getExpression().toString());
		
		assertEquals("1", expr.getFrom().toString());
		assertEquals("3", expr.getTo().toString());
		
		assertPosition(expr, 1, s.length() - 1);
	}
	
	public void testDolar() {
		String s = " $";
		IExpression expr = (IExpression) new ParserFacade().parseExpression(s);
		assertEquals(IExpression.EXPRESSION_DOLAR, expr.getExpressionType());
		
		assertPosition(expr, 1, 1);
	}
	
	public void testCall() {
		String s = " bla(1, 2, 3)";
		ICallExpression expr = (ICallExpression) new ParserFacade().parseExpression(s);
		assertEquals(IExpression.EXPRESSION_CALL, expr.getExpressionType());
		
		assertEquals("bla", expr.getExpression().toString());
		
		IExpression[] args = expr.getArguments();
		assertEquals(3, args.length);
		
		assertEquals("1", args[0].toString());
		assertEquals("2", args[1].toString());
		assertEquals("3", args[2].toString());
		
		assertPosition(expr, 1, s.length() - 1);
	}
	
	public void testType() {
		String s = " typeof(int)";
		ITypeExpression expr = (ITypeExpression) new ParserFacade().parseExpression(s);
		assertEquals(IExpression.EXPRESSION_TYPE, expr.getExpressionType());
		
		assertEquals(IType.TYPE_TYPEOF, expr.getType().getTypeType());
		assertPosition(expr, 1, s.length() - 1);
	}
	
	public void testDotId() {
		String s = " .bla";
		IDotIdExpression expr = (IDotIdExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.EXPRESSION_DOT_ID, expr.getExpressionType());
		assertPosition(expr, 1, s.length() - 1);
		
		assertNull(expr.getExpression());
		
		assertEquals("bla", expr.getName().toString());
		assertPosition(expr.getName(), 2, 3);
	}
	
	public void testExprDotId() {
		String s = " ble.bla";
		IDotIdExpression expr = (IDotIdExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.EXPRESSION_DOT_ID, expr.getExpressionType());
		assertPosition(expr, 1, s.length() - 1);
		
		assertEquals("ble", expr.getExpression().toString());
		assertPosition(expr.getExpression(), 1, 3);
		
		assertEquals("bla", expr.getName().toString());
		assertPosition(expr.getName(), 5, 3);
	}

}
