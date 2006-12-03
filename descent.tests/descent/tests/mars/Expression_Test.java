package descent.tests.mars;

import java.math.BigInteger;

import descent.core.dom.IArrayExpression;
import descent.core.dom.IArrayLiteralExpression;
import descent.core.dom.IArrayType;
import descent.core.dom.IAssertExpression;
import descent.core.dom.IInfixExpression;
import descent.core.dom.ICallExpression;
import descent.core.dom.ICastExpression;
import descent.core.dom.IConditionExpression;
import descent.core.dom.IDeleteExpression;
import descent.core.dom.IDotIdentifierExpression;
import descent.core.dom.IDynamicArrayType;
import descent.core.dom.IElement;
import descent.core.dom.IExpression;
import descent.core.dom.IFunctionExpression;
import descent.core.dom.IIdentifierExpression;
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
import descent.internal.core.dom.ParserFacade;

public class Expression_Test extends Parser_Test {
	
	public void testThis() {
		String s = " this";
		IExpression expr = new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.THIS_EXPRESSION, expr.getElementType());
		assertEquals("this", expr.toString());
		assertPosition(expr, 1, s.length() - 1);
		
		assertVisitor(expr, 1);
	}
	
	public void testSuper() {
		String s = " super";
		IExpression expr = new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.SUPER_EXPRESSION, expr.getElementType());
		assertEquals("super", expr.toString());
		assertPosition(expr, 1, s.length() - 1);
		
		assertVisitor(expr, 1);
	}
	
	public void testNull() {
		String s = " null";
		IExpression expr = new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.NULL_EXPRESSION, expr.getElementType());
		assertEquals("null", expr.toString());
		assertPosition(expr, 1, s.length() - 1);
		
		assertVisitor(expr, 1);
	}
	
	public void testTrue() {
		String s = " true";
		IExpression expr = new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.TRUE_EXPRESSION, expr.getElementType());
		assertEquals("true", expr.toString());
		assertPosition(expr, 1, s.length() - 1);
		
		assertVisitor(expr, 1);
	}
	
	public void testFalse() {
		String s = " false";
		IExpression expr = new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.FALSE_EXPRESSION, expr.getElementType());
		assertEquals("false", expr.toString());
		assertPosition(expr, 1, s.length() - 1);
		
		assertVisitor(expr, 1);
	}
	
	public void testString() {
		String s = " \"hola\"";
		IStringExpression expr = (IStringExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.STRING_EXPRESSION, expr.getElementType());
		assertEquals("hola", expr.getString());
		assertEquals(0, expr.getPostfix());
		assertPosition(expr, 1, s.length() - 1);
		
		assertVisitor(expr, 1);
	}
	
	public void testStringMany() {
		String s = " \"hola\" \"chau\"";
		IStringExpression expr = (IStringExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.STRING_EXPRESSION, expr.getElementType());
		assertEquals("holachau", expr.getString());
		assertEquals(0, expr.getPostfix());
		assertPosition(expr, 1, s.length() - 1);
	}
	
	public void testStringPostfix() {
		String s = " \"hola\"c";
		IStringExpression expr = (IStringExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.STRING_EXPRESSION, expr.getElementType());
		assertEquals("hola", expr.getString());
		assertEquals('c', expr.getPostfix());
		assertPosition(expr, 1, s.length() - 1);
	}
	
	public void testStringHex() {
		String s = " x\"1234\"";
		IStringExpression expr = (IStringExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.STRING_EXPRESSION, expr.getElementType());
		assertPosition(expr, 1, s.length() - 1);
	}
	
	public void testInt32() {
		String s = " 1234";
		IIntegerExpression expr = (IIntegerExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.INTEGER_EXPRESSION, expr.getElementType());
		assertEquals(new BigInteger("1234"), expr.getValue());
		assertPosition(expr, 1, s.length() - 1);
		
		assertVisitor(expr, 1);
	}
	
	public void testUInt32() {
		String s = " 1234u";
		IIntegerExpression expr = (IIntegerExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.INTEGER_EXPRESSION, expr.getElementType());
		assertEquals(new BigInteger("1234"), expr.getValue());
		assertPosition(expr, 1, s.length() - 1);
	}
	
	public void testInt64() {
		String s = " 1234L";
		IIntegerExpression expr = (IIntegerExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.INTEGER_EXPRESSION, expr.getElementType());
		assertEquals(new BigInteger("1234"), expr.getValue());
		assertPosition(expr, 1, s.length() - 1);
	}
	
	public void testUInt64() {
		String s = " 1234Lu";
		IIntegerExpression expr = (IIntegerExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.INTEGER_EXPRESSION, expr.getElementType());
		assertEquals(new BigInteger("1234"), expr.getValue());
		assertPosition(expr, 1, s.length() - 1);
	}
	
	public void testFloat32() {
		String s = " 1.2f";
		IExpression expr = (IExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.REAL_EXPRESSION, expr.getElementType());
		assertPosition(expr, 1, s.length() - 1);
	}
	
	public void testFloat64() {
		String s = " 1.2";
		IExpression expr = (IExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.REAL_EXPRESSION, expr.getElementType());
		assertPosition(expr, 1, s.length() - 1);
	}
	
	public void testFloat80() {
		String s = " 1.2L";
		IExpression expr = (IExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.REAL_EXPRESSION, expr.getElementType());
		assertPosition(expr, 1, s.length() - 1);
	}
	
	public void testReal() {
		String s = " 1.2";
		IExpression expr = (IExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.REAL_EXPRESSION, expr.getElementType());
		assertPosition(expr, 1, s.length() - 1);
		
		assertVisitor(expr, 1);
	}
	
	public void testImaginary32() {
		String s = " 1.2fi";
		IExpression expr = (IExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.REAL_EXPRESSION, expr.getElementType());
		assertPosition(expr, 1, s.length() - 1);
	}
	
	public void testImaginary64() {
		String s = " 1.2i";
		IExpression expr = (IExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.REAL_EXPRESSION, expr.getElementType());
		assertPosition(expr, 1, s.length() - 1);
	}
	
	public void testImaginary80() {
		String s = " 1.2Li";
		IExpression expr = (IExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.REAL_EXPRESSION, expr.getElementType());
		assertPosition(expr, 1, s.length() - 1);
	}
	
	public void testChar() {
		String s = " 'c'";
		IExpression expr = (IExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.INTEGER_EXPRESSION, expr.getElementType());
		assertPosition(expr, 1, s.length() - 1);
	}
	
	public void testAssert() {
		String s = " assert(false)";
		IAssertExpression expr = (IAssertExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.ASSERT_EXPRESSION, expr.getElementType());
		assertEquals(IExpression.FALSE_EXPRESSION, expr.getExpression().getElementType());
		assertNull(expr.getMessage());
		assertPosition(expr, 1, s.length() - 1);
		
		assertVisitor(expr, 2);
	}
	
	public void testAssertWithMessage() {
		String s = " assert(false, true)";
		IAssertExpression expr = (IAssertExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.ASSERT_EXPRESSION, expr.getElementType());
		assertEquals(IExpression.FALSE_EXPRESSION, expr.getExpression().getElementType());
		assertEquals(IExpression.TRUE_EXPRESSION, expr.getMessage().getElementType());
		assertPosition(expr, 1, s.length() - 1);
		
		assertVisitor(expr, 3);
	}
	
	public void testParenthesized() {
		String s = " ( false )";
		IParenthesizedExpression expr = (IParenthesizedExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.PARENTHESIZED_EXPRESSION, expr.getElementType());
		assertEquals(IExpression.FALSE_EXPRESSION, expr.getExpression().getElementType());
		assertPosition(expr, 1, s.length() - 1);
		
		assertVisitor(expr, 2);
	}
	
	public void testBinary() {
		Object[][] objs = {
				{ "*", IInfixExpression.Operator.TIMES },
				{ "/", IInfixExpression.Operator.DIVIDE },
				{ "%", IInfixExpression.Operator.REMAINDER },
				{ "+", IInfixExpression.Operator.PLUS },
				{ "-", IInfixExpression.Operator.MINUS },
				{ "~", IInfixExpression.Operator.CONCATENATE },
				{ ">>", IInfixExpression.Operator.RIGHT_SHIFT_SIGNED },
				{ "<<", IInfixExpression.Operator.LEFT_SHIFT },
				{ ">>>", IInfixExpression.Operator.RIGHT_SHIFT_UNSIGNED },
				{ "in", IInfixExpression.Operator.IN },
				{ "is", IInfixExpression.Operator.IS },
				{ "!is", IInfixExpression.Operator.NOT_IS },
				{ "==", IInfixExpression.Operator.EQUALS },
				{ "!=", IInfixExpression.Operator.NOT_EQUALS },
				{ "&", IInfixExpression.Operator.AND },
				{ "^", IInfixExpression.Operator.XOR },
				{ "|", IInfixExpression.Operator.OR },
				{ "&&", IInfixExpression.Operator.AND_AND },
				{ "||", IInfixExpression.Operator.OR_OR },
				{ "=", IInfixExpression.Operator.ASSIGN },
				{ "+=", IInfixExpression.Operator.PLUS_ASSIGN },
				{ "-=", IInfixExpression.Operator.MINUS_ASSIGN },
				{ "*=", IInfixExpression.Operator.TIMES_ASSIGN },
				{ "/=", IInfixExpression.Operator.DIVIDE_ASSIGN },
				{ "%=", IInfixExpression.Operator.REMAINDER_ASSIGN },
				{ "&=", IInfixExpression.Operator.AND_ASSIGN },
				{ "|=", IInfixExpression.Operator.OR_ASSIGN },
				{ "^=", IInfixExpression.Operator.XOR_ASSIGN },
				{ "<<=", IInfixExpression.Operator.LEFT_SHIFT_ASSIGN },
				{ ">>=", IInfixExpression.Operator.RIGHT_SHIFT_SIGNED_ASSIGN },
				{ ">>>=", IInfixExpression.Operator.RIGHT_SHIFT_UNSIGNED_ASSIGN },
				{ "~=", IInfixExpression.Operator.CONCATENATE_ASSIGN },
				{ ",", IInfixExpression.Operator.COMMA },
				{ "<", IInfixExpression.Operator.LESS },
				{ "<=", IInfixExpression.Operator.LESS_EQUALS },
				{ ">", IInfixExpression.Operator.GREATER },
				{ ">=", IInfixExpression.Operator.GREATER_EQUALS },
				{ "!<>=", IInfixExpression.Operator.NOT_LESS_GREATER_EQUALS },
				{ "<>", IInfixExpression.Operator.LESS_GREATER },
				{ "<>=", IInfixExpression.Operator.LESS_GREATER_EQUALS },
				{ "!<=", IInfixExpression.Operator.NOT_LESS_EQUALS },
				{ "!<", IInfixExpression.Operator.NOT_LESS },
				{ "!>=", IInfixExpression.Operator.NOT_GREATER_EQUALS },
				{ "!>", IInfixExpression.Operator.NOT_GREATER },
				{ "<>=", IInfixExpression.Operator.LESS_GREATER_EQUALS },
				{ "!<>", IInfixExpression.Operator.NOT_LESS_GREATER },
			};
		
		for(Object[] pair : objs) {
			String s = " 1 " + pair[0] + " 1.0";
			IInfixExpression expr = (IInfixExpression) new ParserFacade().parseExpression(s);
			
			assertEquals(IExpression.INFIX_EXPRESSION, expr.getElementType());
			assertEquals(pair[1], expr.getOperator());
			assertEquals(pair[0], pair[1].toString());
			assertEquals(IExpression.INTEGER_EXPRESSION, expr.getLeftOperand().getElementType());
			assertEquals(IExpression.REAL_EXPRESSION, expr.getRightOperand().getElementType());
			assertPosition(expr, 1, 6 + ((String) pair[0]).length());
			
			assertVisitor(expr, 3);
		}
	}
	
	public void testBinary2() {
		Object[][] objs = { 
				{ "===", IInfixExpression.Operator.IS, "is" },
				{ "!==", IInfixExpression.Operator.NOT_IS, "!is" },
			};
		
		for(Object[] pair : objs) {
			String s = " 1 " + pair[0] + " 1.0";
			IInfixExpression expr = (IInfixExpression) new ParserFacade().parseExpression(s);
			
			assertEquals(IExpression.INFIX_EXPRESSION, expr.getElementType());
			assertEquals(pair[1], expr.getOperator());
			assertEquals(pair[2], pair[1].toString());
			assertEquals(IExpression.INTEGER_EXPRESSION, expr.getLeftOperand().getElementType());
			assertEquals(IExpression.REAL_EXPRESSION, expr.getRightOperand().getElementType());
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
			
			assertEquals(IExpression.UNARY_EXPRESSION, expr.getElementType());
			assertEquals(pair[1], expr.getOperator());
			assertEquals(IExpression.INTEGER_EXPRESSION, expr.getInnerExpression().getElementType());
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
			
			assertEquals(IExpression.UNARY_EXPRESSION, expr.getElementType());
			assertEquals(pair[1], expr.getOperator());
			assertEquals(IExpression.INTEGER_EXPRESSION, expr.getInnerExpression().getElementType());
			assertPosition(expr, 1, 1 + ((String) pair[0]).length());
			
			assertVisitor(expr, 2);
		}
	}
	
	public void testCondition() {
		String s = " 1 ? true : false";
		IConditionExpression expr = (IConditionExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.CONDITION_EXPRESSION, expr.getElementType());
		assertEquals(IExpression.INTEGER_EXPRESSION, expr.getCondition().getElementType());
		assertEquals(IExpression.TRUE_EXPRESSION, expr.getTrue().getElementType());
		assertEquals(IExpression.FALSE_EXPRESSION, expr.getFalse().getElementType());
		assertPosition(expr, 1, 16);
		
		assertVisitor(expr, 4);
	}
	
	public void testTypeDotId() {
		String s = " int.length";
		ITypeDotIdentifierExpression expr = (ITypeDotIdentifierExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.TYPE_DOT_IDENTIFIER_EXPRESSION, expr.getElementType());
		assertPosition(expr, 1, 10);
		
		assertEquals(IType.BASIC_TYPE, expr.getType().getElementType());
		assertEquals("length", expr.getProperty().toString());
		
		assertVisitor(expr, 3);
	}
	
	public void testDelete() {
		String s = " delete some";
		IDeleteExpression expr = (IDeleteExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.DELETE_EXPRESSION, expr.getElementType());
		assertPosition(expr, 1, 11);
		
		assertEquals("some", ((IIdentifierExpression) expr.getExpression()).getIdentifier().toString());
		
		assertVisitor(expr, 3);
	}
	
	public void testCast() {
		String s = " cast(float) 1";
		ICastExpression expr = (ICastExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.CAST_EXPRESSION, expr.getElementType());
		assertPosition(expr, 1, 13);
		
		assertEquals("float", expr.getType().toString());
		assertEquals("1", expr.getExpression().toString());
		
		assertVisitor(expr, 3);
	}
	
	public void testTemplateInstance() {
		String s = " some!(int, float)";
		IScopeExpression expr = (IScopeExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.SCOPE_EXPRESSION, expr.getElementType());
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
		
		assertEquals(IExpression.NEW_EXPRESSION, expr.getElementType());
		assertPosition(expr, 1, 13);
		
		IExpression[] args = expr.getArguments();
		assertEquals(2, args.length);
		
		assertEquals("1", args[0].toString());
		assertEquals("2", args[1].toString());
	}
	
	public void testArray() {
		String s = " bla[1, 2, 3]";
		IArrayExpression expr = (IArrayExpression) new ParserFacade().parseExpression(s);
		assertEquals(IExpression.ARRAY_EXPRESSION, expr.getElementType());
		
		assertEquals("bla", ((IIdentifierExpression) expr.getExpression()).getIdentifier().toString());
		
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
		assertEquals(IExpression.ARRAY_LITERAL_EXPRESSION, expr.getElementType());
		
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
		assertEquals(IExpression.SLICE_EXPRESSION, expr.getElementType());
		
		assertEquals("bla", expr.getExpression().toString());
		
		assertEquals("1", expr.getFrom().toString());
		assertEquals("3", expr.getTo().toString());
		
		assertPosition(expr, 1, s.length() - 1);
	}
	
	public void testSliceEmpty() {
		String s = " bla[]";
		ISliceExpression expr = (ISliceExpression) new ParserFacade().parseExpression(s);
		assertEquals(IExpression.SLICE_EXPRESSION, expr.getElementType());
		
		assertEquals("bla", expr.getExpression().toString());
		
		assertNull(expr.getFrom());
		assertNull(expr.getTo());
		
		assertPosition(expr, 1, s.length() - 1);
	}
	
	public void testDolar() {
		String s = " $";
		IExpression expr = (IExpression) new ParserFacade().parseExpression(s);
		assertEquals(IExpression.DOLAR_EXPRESSION, expr.getElementType());
		
		assertPosition(expr, 1, 1);
	}
	
	public void testCall() {
		String s = " bla(1, 2, 3)";
		ICallExpression expr = (ICallExpression) new ParserFacade().parseExpression(s);
		assertEquals(IExpression.CALL_EXPRESSION, expr.getElementType());
		
		assertEquals("bla", expr.getExpression().toString());
		
		IExpression[] args = expr.getArguments();
		assertEquals(3, args.length);
		
		assertEquals("1", args[0].toString());
		assertEquals("2", args[1].toString());
		assertEquals("3", args[2].toString());
		
		assertPosition(expr, 1, s.length() - 1);
	}
	
	public void testTypeof() {
		String s = " typeof(3)";
		ITypeExpression expr = (ITypeExpression) new ParserFacade().parseExpression(s);
		assertEquals(IExpression.TYPE_EXPRESSION, expr.getElementType());
		
		assertEquals(IType.TYPEOF_TYPE, expr.getType().getElementType());
		assertPosition(expr, 1, s.length() - 1);
		
		ITypeofType typeof = (ITypeofType) expr.getType();
		assertEquals("3", typeof.getExpression().toString());
	}
	
	public void testTypeofDotId() {
		String s = " typeof(3).length";
		ITypeDotIdentifierExpression expr = (ITypeDotIdentifierExpression) new ParserFacade().parseExpression(s);
		assertEquals(IExpression.TYPE_DOT_IDENTIFIER_EXPRESSION, expr.getElementType());
		
		assertEquals(IType.TYPEOF_TYPE, expr.getType().getElementType());
		assertPosition(expr, 1, s.length() - 1);
		
		ITypeofType typeof = (ITypeofType) expr.getType();
		assertEquals("3", typeof.getExpression().toString());
		
		assertEquals("length", expr.getProperty().toString());
		assertPosition(expr.getProperty(), 11, 6);
	}
	
	public void testDotId() {
		String s = " .bla";
		IDotIdentifierExpression expr = (IDotIdentifierExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.DOT_IDENTIFIER_EXPRESSION, expr.getElementType());
		assertPosition(expr, 1, s.length() - 1);
		
		assertNull(expr.getExpression());
		
		assertEquals("bla", expr.getName().toString());
		assertPosition(expr.getName(), 2, 3);
	}
	
	public void testExprDotId() {
		String s = " ble.bla";
		IDotIdentifierExpression expr = (IDotIdentifierExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.DOT_IDENTIFIER_EXPRESSION, expr.getElementType());
		assertPosition(expr, 1, s.length() - 1);
		
		assertEquals("ble", expr.getExpression().toString());
		assertPosition(expr.getExpression(), 1, 3);
		
		assertEquals("bla", expr.getName().toString());
		assertPosition(expr.getName(), 5, 3);
	}
	
	public void testTypeid() {
		String s = " typeid(int)";
		ITypeidExpression expr = (ITypeidExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.TYPEID_EXPRESSION, expr.getElementType());
		assertPosition(expr, 1, s.length() - 1);
		
		assertEquals("int", expr.getType().toString());
	}
	
	public void testIftype() {
		String s = " is(x : float)";
		IIsExpression expr = (IIsExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.IS_EXPRESSION, expr.getElementType());
		assertPosition(expr, 1, s.length() - 1);
		
		assertEquals("x", expr.getType().toString());
		assertEquals("float", expr.getSpecialization().toString());
		assertNull(expr.getIdentifier());
	}
	
	public void testIftypeWithId() {
		String s = " is(int x : float)";
		IIsExpression expr = (IIsExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.IS_EXPRESSION, expr.getElementType());
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
			
			assertEquals(IExpression.IS_EXPRESSION, expr.getElementType());
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
		
		assertEquals(IExpression.FUNCTION_EXPRESSION, expr.getElementType());
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
		
		assertEquals(IExpression.NEW_ANONYMOUS_CLASS_EXPRESSION, expr.getElementType());
		assertPosition(expr, 1, s.length() - 1);
		
		assertEquals(0, expr.getCallArguments().length);
		assertEquals(0, expr.getConstructorArguments().length);
		assertEquals(0, expr.getBaseClasses().length);
	}
	
	public void testAnnonymousClass2() {
		String s = " new (1, 2) class (int a, int b) { }";
		INewAnonymousClassExpression expr = (INewAnonymousClassExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.NEW_ANONYMOUS_CLASS_EXPRESSION, expr.getElementType());
		assertPosition(expr, 1, s.length() - 1);
		
		assertEquals(2, expr.getCallArguments().length);
		assertEquals(2, expr.getConstructorArguments().length);
		assertEquals(0, expr.getBaseClasses().length);
	}
	
	public void testAnnonymousClass3() {
		String s = " new class A, B { }";
		INewAnonymousClassExpression expr = (INewAnonymousClassExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.NEW_ANONYMOUS_CLASS_EXPRESSION, expr.getElementType());
		assertPosition(expr, 1, s.length() - 1);
		
		assertEquals(0, expr.getCallArguments().length);
		assertEquals(0, expr.getConstructorArguments().length);
		assertEquals(2, expr.getBaseClasses().length);
	}
	
	public void testNewDynamicArray1() {
		String s = " new int[size]";
		INewExpression expr = (INewExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.NEW_EXPRESSION, expr.getElementType());
		assertPosition(expr, 1, s.length() - 1);
		
		IDynamicArrayType array = (IDynamicArrayType) expr.getType();
		assertEquals(IArrayType.DYNAMIC_ARRAY_TYPE, array.getElementType());
		assertEquals(1, expr.getArguments().length);
		assertEquals("size", expr.getArguments()[0].toString());
		assertPosition(expr.getArguments()[0], 9, 4);
	}
	
	public void testNewDynamicArray2() {
		String s = " new int[3]";
		INewExpression expr = (INewExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.NEW_EXPRESSION, expr.getElementType());
		assertPosition(expr, 1, s.length() - 1);
		
		IDynamicArrayType array = (IDynamicArrayType) expr.getType();
		assertEquals(IArrayType.DYNAMIC_ARRAY_TYPE, array.getElementType());
		assertEquals(1, expr.getArguments().length);
		assertEquals("3", expr.getArguments()[0].toString());
		assertPosition(expr.getArguments()[0], 9, 1);
	}

}
