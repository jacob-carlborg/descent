package descent.tests.mars;

import java.math.BigInteger;
import java.util.List;

import descent.core.dom.IArrayExpression;
import descent.core.dom.IArrayLiteralExpression;
import descent.core.dom.IArrayType;
import descent.core.dom.IAssertExpression;
import descent.core.dom.IBooleanLiteral;
import descent.core.dom.ICallExpression;
import descent.core.dom.ICastExpression;
import descent.core.dom.IConditionExpression;
import descent.core.dom.IDeleteExpression;
import descent.core.dom.IDotIdentifierExpression;
import descent.core.dom.IDynamicArrayType;
import descent.core.dom.IElement;
import descent.core.dom.IExpression;
import descent.core.dom.IFunctionExpression;
import descent.core.dom.IInfixExpression;
import descent.core.dom.IIntegerExpression;
import descent.core.dom.IIsExpression;
import descent.core.dom.INewAnonymousClassExpression;
import descent.core.dom.INewExpression;
import descent.core.dom.IParenthesizedExpression;
import descent.core.dom.IScopeExpression;
import descent.core.dom.ISliceExpression;
import descent.core.dom.IStringExpression;
import descent.core.dom.IType;
import descent.core.dom.ITypeDotIdentifierExpression;
import descent.core.dom.ITypeExpression;
import descent.core.dom.ITypeSpecialization;
import descent.core.dom.ITypeidExpression;
import descent.core.dom.ITypeofType;
import descent.core.dom.IUnaryExpression;
import descent.internal.core.dom.Expression;
import descent.internal.core.dom.InfixExpression;
import descent.internal.core.dom.ParserFacade;
import descent.internal.core.dom.SimpleName;

public class Expression_Test extends Parser_Test {
	
	public void testThis() {
		String s = " this";
		IExpression expr = new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.THIS_LITERAL, expr.getNodeType0());
		assertEquals("this", expr.toString());
		assertPosition(expr, 1, s.length() - 1);
		
		assertVisitor(expr, 1);
	}
	
	public void testSuper() {
		String s = " super";
		IExpression expr = new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.SUPER_LITERAL, expr.getNodeType0());
		assertEquals("super", expr.toString());
		assertPosition(expr, 1, s.length() - 1);
		
		assertVisitor(expr, 1);
	}
	
	public void testNull() {
		String s = " null";
		IExpression expr = new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.NULL_LITERAL, expr.getNodeType0());
		assertEquals("null", expr.toString());
		assertPosition(expr, 1, s.length() - 1);
		
		assertVisitor(expr, 1);
	}
	
	public void testTrue() {
		String s = " true";
		IBooleanLiteral expr = (IBooleanLiteral) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.BOOLEAN_LITERAL, expr.getNodeType0());
		assertTrue(expr.booleanValue());
		assertPosition(expr, 1, s.length() - 1);
		
		assertVisitor(expr, 1);
	}
	
	public void testFalse() {
		String s = " false";
		IBooleanLiteral expr = (IBooleanLiteral) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.BOOLEAN_LITERAL, expr.getNodeType0());
		assertFalse(expr.booleanValue());
		assertPosition(expr, 1, s.length() - 1);
		
		assertVisitor(expr, 1);
	}
	
	public void testString() {
		String s = " \"hola\"";
		IStringExpression expr = (IStringExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.STRING_EXPRESSION, expr.getNodeType0());
		assertEquals("hola", expr.getString());
		assertEquals(0, expr.getPostfix());
		assertPosition(expr, 1, s.length() - 1);
		
		assertVisitor(expr, 1);
	}
	
	public void testStringMany() {
		String s = " \"hola\" \"chau\"";
		IStringExpression expr = (IStringExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.STRING_EXPRESSION, expr.getNodeType0());
		assertEquals("holachau", expr.getString());
		assertEquals(0, expr.getPostfix());
		assertPosition(expr, 1, s.length() - 1);
	}
	
	public void testStringPostfix() {
		String s = " \"hola\"c";
		IStringExpression expr = (IStringExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.STRING_EXPRESSION, expr.getNodeType0());
		assertEquals("hola", expr.getString());
		assertEquals('c', expr.getPostfix());
		assertPosition(expr, 1, s.length() - 1);
	}
	
	public void testStringHex() {
		String s = " x\"1234\"";
		IStringExpression expr = (IStringExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.STRING_EXPRESSION, expr.getNodeType0());
		assertPosition(expr, 1, s.length() - 1);
	}
	
	public void testInt32() {
		String s = " 1234";
		IIntegerExpression expr = (IIntegerExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.INTEGER_EXPRESSION, expr.getNodeType0());
		assertEquals(new BigInteger("1234"), expr.getValue());
		assertPosition(expr, 1, s.length() - 1);
		
		assertVisitor(expr, 1);
	}
	
	public void testUInt32() {
		String s = " 1234u";
		IIntegerExpression expr = (IIntegerExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.INTEGER_EXPRESSION, expr.getNodeType0());
		assertEquals(new BigInteger("1234"), expr.getValue());
		assertPosition(expr, 1, s.length() - 1);
	}
	
	public void testInt64() {
		String s = " 1234L";
		IIntegerExpression expr = (IIntegerExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.INTEGER_EXPRESSION, expr.getNodeType0());
		assertEquals(new BigInteger("1234"), expr.getValue());
		assertPosition(expr, 1, s.length() - 1);
	}
	
	public void testUInt64() {
		String s = " 1234Lu";
		IIntegerExpression expr = (IIntegerExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.INTEGER_EXPRESSION, expr.getNodeType0());
		assertEquals(new BigInteger("1234"), expr.getValue());
		assertPosition(expr, 1, s.length() - 1);
	}
	
	public void testFloat32() {
		String s = " 1.2f";
		IExpression expr = (IExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.REAL_EXPRESSION, expr.getNodeType0());
		assertPosition(expr, 1, s.length() - 1);
	}
	
	public void testFloat64() {
		String s = " 1.2";
		IExpression expr = (IExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.REAL_EXPRESSION, expr.getNodeType0());
		assertPosition(expr, 1, s.length() - 1);
	}
	
	public void testFloat80() {
		String s = " 1.2L";
		IExpression expr = (IExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.REAL_EXPRESSION, expr.getNodeType0());
		assertPosition(expr, 1, s.length() - 1);
	}
	
	public void testReal() {
		String s = " 1.2";
		IExpression expr = (IExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.REAL_EXPRESSION, expr.getNodeType0());
		assertPosition(expr, 1, s.length() - 1);
		
		assertVisitor(expr, 1);
	}
	
	public void testImaginary32() {
		String s = " 1.2fi";
		IExpression expr = (IExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.REAL_EXPRESSION, expr.getNodeType0());
		assertPosition(expr, 1, s.length() - 1);
	}
	
	public void testImaginary64() {
		String s = " 1.2i";
		IExpression expr = (IExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.REAL_EXPRESSION, expr.getNodeType0());
		assertPosition(expr, 1, s.length() - 1);
	}
	
	public void testImaginary80() {
		String s = " 1.2Li";
		IExpression expr = (IExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.REAL_EXPRESSION, expr.getNodeType0());
		assertPosition(expr, 1, s.length() - 1);
	}
	
	public void testChar() {
		String s = " 'c'";
		IExpression expr = (IExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.INTEGER_EXPRESSION, expr.getNodeType0());
		assertPosition(expr, 1, s.length() - 1);
	}
	
	public void testAssert() {
		String s = " assert(false)";
		IAssertExpression expr = (IAssertExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.ASSERT_EXPRESSION, expr.getNodeType0());
		assertEquals(IExpression.BOOLEAN_LITERAL, expr.getExpression().getNodeType0());
		assertNull(expr.getMessage());
		assertPosition(expr, 1, s.length() - 1);
	}
	
	public void testAssertWithMessage() {
		String s = " assert(false, true)";
		IAssertExpression expr = (IAssertExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.ASSERT_EXPRESSION, expr.getNodeType0());
		assertEquals(IExpression.BOOLEAN_LITERAL, expr.getExpression().getNodeType0());
		assertEquals(IExpression.BOOLEAN_LITERAL, expr.getMessage().getNodeType0());
		assertPosition(expr, 1, s.length() - 1);
		
		assertVisitor(expr, 3);
	}
	
	public void testParenthesized() {
		String s = " ( false )";
		IParenthesizedExpression expr = (IParenthesizedExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.PARENTHESIZED_EXPRESSION, expr.getNodeType0());
		assertEquals(IExpression.BOOLEAN_LITERAL, expr.getExpression().getNodeType0());
		assertPosition(expr, 1, s.length() - 1);
		
		assertVisitor(expr, 2);
	}
	
	public void testBinary() {
		Object[][] objs = {
				{ "*", InfixExpression.Operator.TIMES },
				{ "/", InfixExpression.Operator.DIVIDE },
				{ "%", InfixExpression.Operator.REMAINDER },
				{ "+", InfixExpression.Operator.PLUS },
				{ "-", InfixExpression.Operator.MINUS },
				{ "~", InfixExpression.Operator.CONCATENATE },
				{ ">>", InfixExpression.Operator.RIGHT_SHIFT_SIGNED },
				{ "<<", InfixExpression.Operator.LEFT_SHIFT },
				{ ">>>", InfixExpression.Operator.RIGHT_SHIFT_UNSIGNED },
				{ "in", InfixExpression.Operator.IN },
				{ "is", InfixExpression.Operator.IS },
				{ "!is", InfixExpression.Operator.NOT_IS },
				{ "==", InfixExpression.Operator.EQUALS },
				{ "!=", InfixExpression.Operator.NOT_EQUALS },
				{ "&", InfixExpression.Operator.AND },
				{ "^", InfixExpression.Operator.XOR },
				{ "|", InfixExpression.Operator.OR },
				{ "&&", InfixExpression.Operator.AND_AND },
				{ "||", InfixExpression.Operator.OR_OR },
				{ "=", InfixExpression.Operator.ASSIGN },
				{ "+=", InfixExpression.Operator.PLUS_ASSIGN },
				{ "-=", InfixExpression.Operator.MINUS_ASSIGN },
				{ "*=", InfixExpression.Operator.TIMES_ASSIGN },
				{ "/=", InfixExpression.Operator.DIVIDE_ASSIGN },
				{ "%=", InfixExpression.Operator.REMAINDER_ASSIGN },
				{ "&=", InfixExpression.Operator.AND_ASSIGN },
				{ "|=", InfixExpression.Operator.OR_ASSIGN },
				{ "^=", InfixExpression.Operator.XOR_ASSIGN },
				{ "<<=", InfixExpression.Operator.LEFT_SHIFT_ASSIGN },
				{ ">>=", InfixExpression.Operator.RIGHT_SHIFT_SIGNED_ASSIGN },
				{ ">>>=", InfixExpression.Operator.RIGHT_SHIFT_UNSIGNED_ASSIGN },
				{ "~=", InfixExpression.Operator.CONCATENATE_ASSIGN },
				{ ",", InfixExpression.Operator.COMMA },
				{ "<", InfixExpression.Operator.LESS },
				{ "<=", InfixExpression.Operator.LESS_EQUALS },
				{ ">", InfixExpression.Operator.GREATER },
				{ ">=", InfixExpression.Operator.GREATER_EQUALS },
				{ "!<>=", InfixExpression.Operator.NOT_LESS_GREATER_EQUALS },
				{ "<>", InfixExpression.Operator.LESS_GREATER },
				{ "<>=", InfixExpression.Operator.LESS_GREATER_EQUALS },
				{ "!<=", InfixExpression.Operator.NOT_LESS_EQUALS },
				{ "!<", InfixExpression.Operator.NOT_LESS },
				{ "!>=", InfixExpression.Operator.NOT_GREATER_EQUALS },
				{ "!>", InfixExpression.Operator.NOT_GREATER },
				{ "<>=", InfixExpression.Operator.LESS_GREATER_EQUALS },
				{ "!<>", InfixExpression.Operator.NOT_LESS_GREATER },
			};
		
		for(Object[] pair : objs) {
			String s = " 1 " + pair[0] + " 1.0";
			IInfixExpression expr = (IInfixExpression) new ParserFacade().parseExpression(s);
			
			assertEquals(IExpression.INFIX_EXPRESSION, expr.getNodeType0());
			assertEquals(pair[1], expr.getOperator());
			assertEquals(pair[0], pair[1].toString());
			assertEquals(IExpression.INTEGER_EXPRESSION, expr.getLeftOperand().getNodeType0());
			assertEquals(IExpression.REAL_EXPRESSION, expr.getRightOperand().getNodeType0());
			assertPosition(expr, 1, 6 + ((String) pair[0]).length());
			
			assertVisitor(expr, 3);
		}
	}
	
	public void testBinary2() {
		Object[][] objs = { 
				{ "===", InfixExpression.Operator.IS, "is" },
				{ "!==", InfixExpression.Operator.NOT_IS, "!is" },
			};
		
		for(Object[] pair : objs) {
			String s = " 1 " + pair[0] + " 1.0";
			IInfixExpression expr = (IInfixExpression) new ParserFacade().parseExpression(s);
			
			assertEquals(IExpression.INFIX_EXPRESSION, expr.getNodeType0());
			assertEquals(pair[1], expr.getOperator());
			assertEquals(pair[2], pair[1].toString());
			assertEquals(IExpression.INTEGER_EXPRESSION, expr.getLeftOperand().getNodeType0());
			assertEquals(IExpression.REAL_EXPRESSION, expr.getRightOperand().getNodeType0());
			assertPosition(expr, 1, 6 + ((String) pair[0]).length());
			
			assertVisitor(expr, 3);
		}
	}
	
	public void testUnary() {
		Object[][] objs = { 
				{ "&", IUnaryExpression.Operator.ADDRESS },
				{ "++", IUnaryExpression.Operator.PRE_INCREMENT },
				{ "--", IUnaryExpression.Operator.PRE_DECREMENT },
				{ "*", IUnaryExpression.Operator.POINTER },
				{ "-", IUnaryExpression.Operator.NEGATIVE },
				{ "+", IUnaryExpression.Operator.POSITIVE },
				{ "!", IUnaryExpression.Operator.NOT },
				{ "~", IUnaryExpression.Operator.INVERT },
			};
		
		for(Object[] pair : objs) {
			String s = " " + pair[0] + "1";
			IUnaryExpression expr = (IUnaryExpression) new ParserFacade().parseExpression(s);
			
			assertEquals(IExpression.UNARY_EXPRESSION, expr.getNodeType0());
			assertEquals(pair[1], expr.getOperator());
			assertEquals(IExpression.INTEGER_EXPRESSION, expr.getInnerExpression().getNodeType0());
			assertPosition(expr, 1, 1 + ((String) pair[0]).length());
			
			assertVisitor(expr, 2);
		}
	}
	
	public void testUnary2() {
		Object[][] objs = { 
				{ "++", IUnaryExpression.Operator.POST_INCREMENT },
				{ "--", IUnaryExpression.Operator.POST_DECREMENT },
			};
		
		for(Object[] pair : objs) {
			String s = " 1" + pair[0];
			IUnaryExpression expr = (IUnaryExpression) new ParserFacade().parseExpression(s);
			
			assertEquals(IExpression.UNARY_EXPRESSION, expr.getNodeType0());
			assertEquals(pair[1], expr.getOperator());
			assertEquals(IExpression.INTEGER_EXPRESSION, expr.getInnerExpression().getNodeType0());
			assertPosition(expr, 1, 1 + ((String) pair[0]).length());
			
			assertVisitor(expr, 2);
		}
	}
	
	public void testCondition() {
		String s = " 1 ? true : false";
		IConditionExpression expr = (IConditionExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.CONDITIONAL_EXPRESSION, expr.getNodeType0());
		assertEquals(IExpression.INTEGER_EXPRESSION, expr.getExpression().getNodeType0());
		assertEquals(IExpression.BOOLEAN_LITERAL, expr.getThenExpression().getNodeType0());
		assertEquals(IExpression.BOOLEAN_LITERAL, expr.getElseExpression().getNodeType0());
		assertPosition(expr, 1, 16);
		
		assertVisitor(expr, 4);
	}
	
	public void testTypeDotId() {
		String s = " int.length";
		ITypeDotIdentifierExpression expr = (ITypeDotIdentifierExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.TYPE_DOT_IDENTIFIER_EXPRESSION, expr.getNodeType0());
		assertPosition(expr, 1, 10);
		
		assertEquals(IType.PRIMITIVE_TYPE, expr.getType().getNodeType0());
		assertEquals("length", expr.getProperty().toString());
		
		assertVisitor(expr, 3);
	}
	
	public void testDelete() {
		String s = " delete some";
		IDeleteExpression expr = (IDeleteExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.DELETE_EXPRESSION, expr.getNodeType0());
		assertPosition(expr, 1, 11);
		
		assertEquals("some", ((SimpleName) expr.getExpression()).getIdentifier().toString());
	}
	
	public void testCast() {
		String s = " cast(float) 1";
		ICastExpression expr = (ICastExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.CAST_EXPRESSION, expr.getNodeType0());
		assertPosition(expr, 1, 13);
		
		assertEquals("float", expr.getType().toString());
		assertEquals("1", expr.getExpression().toString());
		
		assertVisitor(expr, 3);
	}
	
	public void testTemplateInstance() {
		String s = " some!(int, float)";
		IScopeExpression expr = (IScopeExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.SCOPE_EXPRESSION, expr.getNodeType0());
		assertPosition(expr, 1, 17);
		
		assertEquals("some", expr.getName().toString());
		assertPosition(expr.getName(), 1, 4);
		
		IElement[] arguments = expr.getArguments();
		assertEquals(2, arguments.length);
		
		assertEquals("int", arguments[0].toString());
		assertEquals("float", arguments[1].toString());
		
		assertVisitor(expr, 4);
	}
	
	public void testNew() {
		String s = " new Bla(1, 2)";
		INewExpression expr = (INewExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.NEW_EXPRESSION, expr.getNodeType0());
		assertPosition(expr, 1, 13);
		
		IExpression[] args = expr.getArguments();
		assertEquals(2, args.length);
		
		assertEquals("1", args[0].toString());
		assertEquals("2", args[1].toString());
	}
	
	public void testArray() {
		String s = " bla[1, 2, 3]";
		IArrayExpression expr = (IArrayExpression) new ParserFacade().parseExpression(s);
		assertEquals(IExpression.ARRAY_ACCESS, expr.getNodeType0());
		
		assertEquals("bla", ((SimpleName) expr.getArray()).getIdentifier().toString());
		
		List<Expression> args = expr.indexes();
		assertEquals(3, args.size());
		
		assertEquals("1", args.get(0).toString());
		assertEquals("2", args.get(1).toString());
		assertEquals("3", args.get(2).toString());
		
		assertPosition(expr, 1, s.length() - 1);
	}
	
	public void testArrayLiteral() {
		String s = " [1, 2, 3]";
		IArrayLiteralExpression expr = (IArrayLiteralExpression) new ParserFacade().parseExpression(s);
		assertEquals(IExpression.ARRAY_LITERAL, expr.getNodeType0());
		
		IExpression[] args = expr.arguments().toArray(new IExpression[expr.arguments().size()]);
		assertEquals(3, args.length);
		
		assertEquals("1", args[0].toString());
		assertEquals("2", args[1].toString());
		assertEquals("3", args[2].toString());
		
		assertPosition(expr, 1, s.length() - 1);
	}
	
	public void testSlice() {
		String s = " bla[1 .. 3]";
		ISliceExpression expr = (ISliceExpression) new ParserFacade().parseExpression(s);
		assertEquals(IExpression.SLICE_EXPRESSION, expr.getNodeType0());
		
		assertEquals("bla", ((SimpleName) expr.getExpression()).getIdentifier());
		
		assertEquals("1", expr.getFromExpression().toString());
		assertEquals("3", expr.getToExpression().toString());
		
		assertPosition(expr, 1, s.length() - 1);
	}
	
	public void testSliceEmpty() {
		String s = " bla[]";
		ISliceExpression expr = (ISliceExpression) new ParserFacade().parseExpression(s);
		assertEquals(IExpression.SLICE_EXPRESSION, expr.getNodeType0());
		
		assertEquals("bla", ((SimpleName) expr.getExpression()).getIdentifier());
		
		assertNull(expr.getFromExpression());
		assertNull(expr.getToExpression());
		
		assertPosition(expr, 1, s.length() - 1);
	}
	
	public void testDolar() {
		String s = " $";
		IExpression expr = (IExpression) new ParserFacade().parseExpression(s);
		assertEquals(IExpression.DOLLAR_LITERAL, expr.getNodeType0());
		
		assertPosition(expr, 1, 1);
	}
	
	public void testCall() {
		String s = " bla(1, 2, 3)";
		ICallExpression expr = (ICallExpression) new ParserFacade().parseExpression(s);
		assertEquals(IExpression.CALL_EXPRESSION, expr.getNodeType0());
		
		assertEquals("bla", ((SimpleName) expr.getExpression()).getIdentifier());
		
		IExpression[] args = expr.arguments().toArray(new IExpression[expr.arguments().size()]);
		assertEquals(3, args.length);
		
		assertEquals("1", args[0].toString());
		assertEquals("2", args[1].toString());
		assertEquals("3", args[2].toString());
		
		assertPosition(expr, 1, s.length() - 1);
	}
	
	public void testTypeof() {
		String s = " typeof(3)";
		ITypeExpression expr = (ITypeExpression) new ParserFacade().parseExpression(s);
		assertEquals(IExpression.TYPE_EXPRESSION, expr.getNodeType0());
		
		assertEquals(IType.TYPEOF_TYPE, expr.getType().getNodeType0());
		assertPosition(expr, 1, s.length() - 1);
		
		ITypeofType typeof = (ITypeofType) expr.getType();
		assertEquals("3", typeof.getExpression().toString());
	}
	
	public void testTypeofDotId() {
		String s = " typeof(3).length";
		ITypeDotIdentifierExpression expr = (ITypeDotIdentifierExpression) new ParserFacade().parseExpression(s);
		assertEquals(IExpression.TYPE_DOT_IDENTIFIER_EXPRESSION, expr.getNodeType0());
		
		assertEquals(IType.TYPEOF_TYPE, expr.getType().getNodeType0());
		assertPosition(expr, 1, s.length() - 1);
		
		ITypeofType typeof = (ITypeofType) expr.getType();
		assertEquals("3", typeof.getExpression().toString());
		
		assertEquals("length", expr.getProperty().toString());
		assertPosition(expr.getProperty(), 11, 6);
	}
	
	public void testDotId() {
		String s = " .bla";
		IDotIdentifierExpression expr = (IDotIdentifierExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.DOT_IDENTIFIER_EXPRESSION, expr.getNodeType0());
		assertPosition(expr, 1, s.length() - 1);
		
		assertNull(expr.getExpression());
		
		assertEquals("bla", ((SimpleName) expr.getName()).getIdentifier());
		assertPosition(expr.getName(), 2, 3);
	}
	
	public void testExprDotId() {
		String s = " ble.bla";
		IDotIdentifierExpression expr = (IDotIdentifierExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.DOT_IDENTIFIER_EXPRESSION, expr.getNodeType0());
		assertPosition(expr, 1, s.length() - 1);
		
		assertEquals("ble", ((SimpleName) expr.getExpression()).getIdentifier());
		assertPosition(expr.getExpression(), 1, 3);
		
		assertEquals("bla", expr.getName().getIdentifier());
		assertPosition(expr.getName(), 5, 3);
	}
	
	public void testTypeid() {
		String s = " typeid(int)";
		ITypeidExpression expr = (ITypeidExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.TYPEID_EXPRESSION, expr.getNodeType0());
		assertPosition(expr, 1, s.length() - 1);
		
		assertEquals("int", expr.getType().toString());
	}
	
	public void testIftype() {
		String s = " is(x : float)";
		IIsExpression expr = (IIsExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.IS_EXPRESSION, expr.getNodeType0());
		assertPosition(expr, 1, s.length() - 1);
		
		assertEquals("x", expr.getType().toString());
		assertEquals("float", expr.getSpecialization().toString());
		assertNull(expr.getIdentifier());
	}
	
	public void testIftypeWithId() {
		String s = " is(int x : float)";
		IIsExpression expr = (IIsExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.IS_EXPRESSION, expr.getNodeType0());
		assertPosition(expr, 1, s.length() - 1);
		
		assertEquals("int", expr.getType().toString());
		assertEquals("float", expr.getSpecialization().toString());
		
		assertEquals("x", expr.getIdentifier().toString());
		assertPosition(expr.getIdentifier(), 8, 1);
	}
	
	public void testIftypeWithType() {
		Object[][] objs = {
				{ "typedef", ITypeSpecialization.TYPEDEF },
				{ "struct", ITypeSpecialization.STRUCT },
				{ "union", ITypeSpecialization.UNION },
				{ "class", ITypeSpecialization.CLASS },
				{ "super", ITypeSpecialization.SUPER },
				{ "enum", ITypeSpecialization.ENUM },
				{ "interface", ITypeSpecialization.INTERFACE },
				{ "function", ITypeSpecialization.FUNCTION },
				{ "delegate", ITypeSpecialization.DELEGATE },
				{ "return", ITypeSpecialization.RETURN },
		};
		
		for(Object[] pair : objs) {
			String s = " is(x == " + pair[0] + ")";
			IIsExpression expr = (IIsExpression) new ParserFacade().parseExpression(s);
			
			assertEquals(IExpression.IS_EXPRESSION, expr.getNodeType0());
			assertPosition(expr, 1, s.length() - 1);
			
			assertEquals("x", expr.getType().toString());
			assertNull(expr.getSpecialization());
			
			assertEquals(pair[1], expr.getTypeSpecialization().getKeyword());
			assertPosition(expr.getTypeSpecialization(), 9, ((String) pair[0]).length());
		}
	}
	
	public void testFunctionLiteralEmpty() {
		String s = " () { }";
		IFunctionExpression expr = (IFunctionExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.FUNCTION_EXPRESSION, expr.getNodeType0());
		assertPosition(expr, 1, s.length() - 1);
		
		assertEquals(0, expr.getArguments().length);
		assertPosition(expr.getBody(), 4, 3);
	}
	
	public void testFunctionLiteralWithParams() {
		String s = " (int x) { }";
		IFunctionExpression expr = (IFunctionExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(1, expr.getArguments().length);
		assertEquals("int", expr.getArguments()[0].getType().toString());
	}
	
	public void testFunctionLiteralWithoutParameters() {
		String s = " { }";
		IFunctionExpression expr = (IFunctionExpression) new ParserFacade().parseExpression(s);
		
		assertPosition(expr, 1, s.length() - 1);
		
		assertEquals(0, expr.getArguments().length);
	}
	
	public void testFunctionLiteralDelegate() {
		String s = " delegate { }";
		IFunctionExpression expr = (IFunctionExpression) new ParserFacade().parseExpression(s);
		
		assertPosition(expr, 1, s.length() - 1);
		
		assertEquals(0, expr.getArguments().length);
	}
	
	public void testAnnonymousClass() {
		String s = " new class { }";
		INewAnonymousClassExpression expr = (INewAnonymousClassExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.NEW_ANONYMOUS_CLASS_EXPRESSION, expr.getNodeType0());
		assertPosition(expr, 1, s.length() - 1);
		
		assertEquals(0, expr.getCallArguments().length);
		assertEquals(0, expr.getConstructorArguments().length);
		assertEquals(0, expr.getBaseClasses().length);
	}
	
	public void testAnnonymousClass2() {
		String s = " new (1, 2) class (int a, int b) { }";
		INewAnonymousClassExpression expr = (INewAnonymousClassExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.NEW_ANONYMOUS_CLASS_EXPRESSION, expr.getNodeType0());
		assertPosition(expr, 1, s.length() - 1);
		
		assertEquals(2, expr.getCallArguments().length);
		assertEquals(2, expr.getConstructorArguments().length);
		assertEquals(0, expr.getBaseClasses().length);
	}
	
	public void testAnnonymousClass3() {
		String s = " new class A, B { }";
		INewAnonymousClassExpression expr = (INewAnonymousClassExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.NEW_ANONYMOUS_CLASS_EXPRESSION, expr.getNodeType0());
		assertPosition(expr, 1, s.length() - 1);
		
		assertEquals(0, expr.getCallArguments().length);
		assertEquals(0, expr.getConstructorArguments().length);
		assertEquals(2, expr.getBaseClasses().length);
	}
	
	public void testNewDynamicArray1() {
		String s = " new int[size]";
		INewExpression expr = (INewExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.NEW_EXPRESSION, expr.getNodeType0());
		assertPosition(expr, 1, s.length() - 1);
		
		IDynamicArrayType array = (IDynamicArrayType) expr.getType();
		assertEquals(IArrayType.DYNAMIC_ARRAY_TYPE, array.getNodeType0());
		assertEquals(1, expr.getArguments().length);
		assertEquals("size", ((SimpleName) expr.getArguments()[0]).getIdentifier());
		assertPosition(expr.getArguments()[0], 9, 4);
	}
	
	public void testNewDynamicArray2() {
		String s = " new int[3]";
		INewExpression expr = (INewExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.NEW_EXPRESSION, expr.getNodeType0());
		assertPosition(expr, 1, s.length() - 1);
		
		IDynamicArrayType array = (IDynamicArrayType) expr.getType();
		assertEquals(IArrayType.DYNAMIC_ARRAY_TYPE, array.getNodeType0());
		assertEquals(1, expr.getArguments().length);
		assertEquals("3", expr.getArguments()[0].toString());
		assertPosition(expr.getArguments()[0], 9, 1);
	}

}
