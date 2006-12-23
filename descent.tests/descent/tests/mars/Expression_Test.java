package descent.tests.mars;

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
import descent.core.dom.IIsTypeExpression;
import descent.core.dom.INewAnonymousClassExpression;
import descent.core.dom.INewExpression;
import descent.core.dom.IParenthesizedExpression;
import descent.core.dom.IPostfixExpression;
import descent.core.dom.IPrefixExpression;
import descent.core.dom.IScopeExpression;
import descent.core.dom.ISliceExpression;
import descent.core.dom.IStringExpression;
import descent.core.dom.IType;
import descent.core.dom.ITypeDotIdentifierExpression;
import descent.core.dom.ITypeExpression;
import descent.core.dom.ITypeidExpression;
import descent.core.dom.ITypeofType;
import descent.internal.core.dom.CharacterLiteral;
import descent.internal.core.dom.DotTemplateTypeExpression;
import descent.internal.core.dom.Expression;
import descent.internal.core.dom.InfixExpression;
import descent.internal.core.dom.IsTypeExpression;
import descent.internal.core.dom.IsTypeSpecializationExpression;
import descent.internal.core.dom.NumberLiteral;
import descent.internal.core.dom.ParserFacade;
import descent.internal.core.dom.PostfixExpression;
import descent.internal.core.dom.PrefixExpression;
import descent.internal.core.dom.QualifiedType;
import descent.internal.core.dom.SimpleName;
import descent.internal.core.dom.SimpleType;
import descent.internal.core.dom.StringsExpression;
import descent.internal.core.dom.TemplateType;
import descent.internal.core.dom.TypeExpression;
import descent.internal.core.dom.FunctionLiteralDeclarationExpression.Syntax;

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
		
		assertEquals(IExpression.STRING_LITERAL, expr.getNodeType0());
		assertEquals("\"hola\"", expr.getEscapedValue());
		assertPosition(expr, 1, s.length() - 1);
		
		assertVisitor(expr, 1);
	}
	
	public void testStringMany() {
		String s = " \"hola\" \"chau\"";
		StringsExpression strings = (StringsExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(2, strings.stringLiterals().size());
		
		assertEquals("\"hola\"", strings.stringLiterals().get(0).getEscapedValue());
		assertPosition(strings.stringLiterals().get(0), 1, 6);
		
		assertEquals("\"chau\"", strings.stringLiterals().get(1).getEscapedValue());
		assertPosition(strings.stringLiterals().get(1), 8, 6);
	}
	
	public void testStringPostfix() {
		String s = " \"hola\"c";
		IStringExpression expr = (IStringExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.STRING_LITERAL, expr.getNodeType0());
		assertEquals("\"hola\"c", expr.getEscapedValue());
		assertPosition(expr, 1, s.length() - 1);
	}
	
	public void testStringHex() {
		String s = " x\"1234\"";
		IStringExpression expr = (IStringExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.STRING_LITERAL, expr.getNodeType0());
		assertPosition(expr, 1, s.length() - 1);
	}
	
	public void testNumbers() {
		String[] strings = { 
			"1", 
			"123_456_789", 
			"1_2_3_4_5_6_", 
			"789L", 
			"789u",
			"789U",
			"789Lu",
			"123_456.567_8",
			"1_2_3_4_5_6_._5_6_7_8",
			"1_2_3_4_5_6_._5e-6_",
			"0x1.FFFFFFFFFFFFFp1023",
			"0x1p-52",
			"0b1010",
			"01234",
			"0.",
			"1.",
			"1.175494351e-38F",
			".175494351e-38F",
			"6.3i",
			"6.3fi",
			"6.3Li",
		};
		for(String string : strings) {
			String s = " " + string;
			NumberLiteral number = (NumberLiteral) new ParserFacade().parseExpression(s);
			
			assertEquals(string, number.getToken());
			assertPosition(number, 1, string.length());
		}
	}
	
	public void testCharsWithoutEscape() {
		String[] strings = { 
				"'c'", 
			};
			for(String string : strings) {
				String s = " " + string;
				CharacterLiteral character = (CharacterLiteral) new ParserFacade().parseExpression(s);
				
				assertEquals(string, character.getEscapedValue());
				assertPosition(character, 1, string.length());
			}
	}
	
	public void testCharsWithEscape() {
		String[] strings = { 
				"'\\n'",
				"'\\123'", 
				"'\\u1234'", 
				"'\\U12345678'",
			};
			for(String string : strings) {
				String s = " " + string;
				CharacterLiteral character = (CharacterLiteral) new ParserFacade().parseExpression(s);
				
				assertEquals(string, character.getEscapedValue());
				assertPosition(character, 1, string.length());
			}
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
			assertEquals(IExpression.NUMBER_LITERAL, expr.getLeftOperand().getNodeType0());
			assertEquals(IExpression.NUMBER_LITERAL, expr.getRightOperand().getNodeType0());
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
			assertEquals(IExpression.NUMBER_LITERAL, expr.getLeftOperand().getNodeType0());
			assertEquals(IExpression.NUMBER_LITERAL, expr.getRightOperand().getNodeType0());
			assertPosition(expr, 1, 6 + ((String) pair[0]).length());
			
			assertVisitor(expr, 3);
		}
	}
	
	public void testPrefixExpression() {
		Object[][] objs = { 
				{ "&", PrefixExpression.Operator.ADDRESS },
				{ "++", PrefixExpression.Operator.INCREMENT },
				{ "--", PrefixExpression.Operator.DECREMENT },
				{ "*", PrefixExpression.Operator.POINTER },
				{ "-", PrefixExpression.Operator.NEGATIVE },
				{ "+", PrefixExpression.Operator.POSITIVE },
				{ "!", PrefixExpression.Operator.NOT },
				{ "~", PrefixExpression.Operator.INVERT },
			};
		
		for(Object[] pair : objs) {
			String s = " " + pair[0] + "1";
			IPrefixExpression expr = (IPrefixExpression) new ParserFacade().parseExpression(s);
			
			assertEquals(IExpression.PREFIX_EXPRESSION, expr.getNodeType0());
			assertEquals(pair[1], expr.getOperator());
			assertEquals(IExpression.NUMBER_LITERAL, expr.getExpression().getNodeType0());
			assertPosition(expr, 1, 1 + ((String) pair[0]).length());
			
			assertVisitor(expr, 2);
		}
	}
	
	public void testPostfixExpression() {
		Object[][] objs = { 
				{ "++", PostfixExpression.Operator.INCREMENT },
				{ "--", PostfixExpression.Operator.DECREMENT },
			};
		
		for(Object[] pair : objs) {
			String s = " 1" + pair[0];
			IPostfixExpression expr = (IPostfixExpression) new ParserFacade().parseExpression(s);
			
			assertEquals(IExpression.POSTFIX_EXPRESSION, expr.getNodeType0());
			assertEquals(pair[1], expr.getOperator());
			assertEquals(IExpression.NUMBER_LITERAL, expr.getExpression().getNodeType0());
			assertPosition(expr, 1, 1 + ((String) pair[0]).length());
			
			assertVisitor(expr, 2);
		}
	}
	
	public void testCondition() {
		String s = " 1 ? true : false";
		IConditionExpression expr = (IConditionExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.CONDITIONAL_EXPRESSION, expr.getNodeType0());
		assertEquals(IExpression.NUMBER_LITERAL, expr.getExpression().getNodeType0());
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
		assertEquals("length", expr.getName().getFullyQualifiedName());
		
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
		assertEquals("1", ((NumberLiteral) expr.getExpression()).getToken());
		
		assertVisitor(expr, 3);
	}
	
	public void testTemplateInstance() {
		String s = " some!(int, float)";
		TypeExpression expr = (TypeExpression) new ParserFacade().parseExpression(s);
		
		assertPosition(expr, 1, 17);
		
		TemplateType templateType = (TemplateType) expr.getType();
		
		assertEquals("some", templateType.getName().getFullyQualifiedName());
		assertPosition(templateType.getName(), 1, 4);
		
		assertEquals(2, templateType.arguments().size());
		
		assertEquals("int", templateType.arguments().get(0).toString());
		assertEquals("float", templateType.arguments().get(1).toString());
	}
	
	public void testNew() {
		String s = " new Bla(1, 2)";
		INewExpression expr = (INewExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.NEW_EXPRESSION, expr.getNodeType0());
		assertPosition(expr, 1, 13);
		
		List<Expression> args = expr.constructorArguments();
		assertEquals(2, args.size());
		
		assertEquals("1", ((NumberLiteral) args.get(0)).getToken());
		assertEquals("2", ((NumberLiteral) args.get(1)).getToken());
	}
	
	public void testArray() {
		String s = " bla[1, 2, 3]";
		IArrayExpression expr = (IArrayExpression) new ParserFacade().parseExpression(s);
		assertEquals(IExpression.ARRAY_ACCESS, expr.getNodeType0());
		
		assertEquals("bla", ((SimpleName) expr.getArray()).getIdentifier().toString());
		
		List<Expression> args = expr.indexes();
		assertEquals(3, args.size());
		
		assertEquals("1",((NumberLiteral) args.get(0)).getToken());
		assertEquals("2", ((NumberLiteral) args.get(1)).getToken());
		assertEquals("3", ((NumberLiteral) args.get(2)).getToken());
		
		assertPosition(expr, 1, s.length() - 1);
	}
	
	public void testArrayLiteral() {
		String s = " [1, 2, 3]";
		IArrayLiteralExpression expr = (IArrayLiteralExpression) new ParserFacade().parseExpression(s);
		assertEquals(IExpression.ARRAY_LITERAL, expr.getNodeType0());
		
		IExpression[] args = expr.arguments().toArray(new IExpression[expr.arguments().size()]);
		assertEquals(3, args.length);
		
		assertEquals("1", ((NumberLiteral) args[0]).getToken());
		assertEquals("2", ((NumberLiteral) args[1]).getToken());
		assertEquals("3", ((NumberLiteral) args[2]).getToken());
		
		assertPosition(expr, 1, s.length() - 1);
	}
	
	public void testSlice() {
		String s = " bla[1 .. 3]";
		ISliceExpression expr = (ISliceExpression) new ParserFacade().parseExpression(s);
		assertEquals(IExpression.SLICE_EXPRESSION, expr.getNodeType0());
		
		assertEquals("bla", ((SimpleName) expr.getExpression()).getIdentifier());
		
		assertEquals("1", ((NumberLiteral) expr.getFromExpression()).getToken());
		assertEquals("3", ((NumberLiteral) expr.getToExpression()).getToken());
		
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
		
		assertEquals("1", ((NumberLiteral) args[0]).getToken());
		assertEquals("2", ((NumberLiteral) args[1]).getToken());
		assertEquals("3", ((NumberLiteral) args[2]).getToken());
		
		assertPosition(expr, 1, s.length() - 1);
	}
	
	public void testTypeof() {
		String s = " typeof(3)";
		ITypeExpression expr = (ITypeExpression) new ParserFacade().parseExpression(s);
		assertEquals(IExpression.TYPE_EXPRESSION, expr.getNodeType0());
		
		assertEquals(IType.TYPEOF_TYPE, expr.getType().getNodeType0());
		assertPosition(expr, 1, s.length() - 1);
		
		ITypeofType typeof = (ITypeofType) expr.getType();
		assertEquals("3", ((NumberLiteral) typeof.getExpression()).getToken());
	}
	
	public void testTypeofDotId() {
		String s = " typeof(3).length";
		ITypeDotIdentifierExpression expr = (ITypeDotIdentifierExpression) new ParserFacade().parseExpression(s);
		assertEquals(IExpression.TYPE_DOT_IDENTIFIER_EXPRESSION, expr.getNodeType0());
		
		assertEquals(IType.TYPEOF_TYPE, expr.getType().getNodeType0());
		assertPosition(expr, 1, s.length() - 1);
		
		ITypeofType typeof = (ITypeofType) expr.getType();
		assertEquals("3", ((NumberLiteral) typeof.getExpression()).getToken());
		
		assertEquals("length", expr.getName().getFullyQualifiedName());
		assertPosition(expr.getName(), 11, 6);
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
		IIsTypeExpression expr = (IIsTypeExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.IS_TYPE_EXPRESSION, expr.getNodeType0());
		assertPosition(expr, 1, s.length() - 1);
		
		assertEquals("x", ((SimpleType) expr.getType()).getName().getFullyQualifiedName());
		assertEquals("float", expr.getSpecialization().toString());
		assertNull(expr.getName());
	}
	
	public void testIsTypeExpression() {
		String s = " is(int x : float)";
		
		IsTypeExpression expr = (IsTypeExpression) new ParserFacade().parseExpression(s);
		
		assertPosition(expr, 1, s.length() - 1);
		
		assertEquals("int", expr.getType().toString());
		assertEquals("float", expr.getSpecialization().toString());
		
		assertEquals("x", expr.getName().getIdentifier());
		assertPosition(expr.getName(), 8, 1);
		
		assertFalse(expr.isSameComparison());
	}
	
	public void testIsTypeSpecializationExpression() {
		Object[][] objs = {
				{ "typedef", IsTypeSpecializationExpression.TypeSpecialization.TYPEDEF },
				{ "struct", IsTypeSpecializationExpression.TypeSpecialization.STRUCT },
				{ "union", IsTypeSpecializationExpression.TypeSpecialization.UNION },
				{ "class", IsTypeSpecializationExpression.TypeSpecialization.CLASS },
				{ "super", IsTypeSpecializationExpression.TypeSpecialization.SUPER },
				{ "enum", IsTypeSpecializationExpression.TypeSpecialization.ENUM },
				{ "interface", IsTypeSpecializationExpression.TypeSpecialization.INTERFACE },
				{ "function", IsTypeSpecializationExpression.TypeSpecialization.FUNCTION },
				{ "delegate", IsTypeSpecializationExpression.TypeSpecialization.DELEGATE },
				{ "return", IsTypeSpecializationExpression.TypeSpecialization.RETURN },
		};
		
		for(Object[] pair : objs) {
			String s = " is(x == " + pair[0] + ")";
			IsTypeSpecializationExpression expr = (IsTypeSpecializationExpression) new ParserFacade().parseExpression(s);
			
			assertPosition(expr, 1, s.length() - 1);
			
			assertEquals("x", ((SimpleType) expr.getType()).getName().getFullyQualifiedName());
			assertEquals(pair[1], expr.getSpecialization());
			
			assertTrue(expr.isSameComparison());
		}
	}
	
	public void testFunctionLiteralFunction() {
		String s = " function () { }";
		IFunctionExpression expr = (IFunctionExpression) new ParserFacade().parseExpression(s);
		assertEquals(Syntax.FUNCTION, expr.getSyntax());
		
		assertPosition(expr, 1, s.length() - 1);
		
		assertEquals(0, expr.arguments().size());
	}
	
	public void testFunctionLiteralDelegate() {
		String s = " delegate { }";
		IFunctionExpression expr = (IFunctionExpression) new ParserFacade().parseExpression(s);
		assertEquals(Syntax.DELEGATE, expr.getSyntax());
		
		assertPosition(expr, 1, s.length() - 1);
		
		assertEquals(0, expr.arguments().size());
	}
	
	public void testFunctionLiteralEmpty() {
		String s = " { }";
		IFunctionExpression expr = (IFunctionExpression) new ParserFacade().parseExpression(s);
		assertEquals(Syntax.EMPTY, expr.getSyntax());
		
		assertEquals(IExpression.FUNCTION_LITERAL_DECLARATION_EXPRESSION, expr.getNodeType0());
		assertPosition(expr, 1, s.length() - 1);
		
		assertEquals(0, expr.arguments().size());
		assertPosition(expr.getBody(), 1, 3);
	}
	
	public void testFunctionLiteralEmpty2() {
		String s = " () { }";
		IFunctionExpression expr = (IFunctionExpression) new ParserFacade().parseExpression(s);
		assertEquals(Syntax.EMPTY, expr.getSyntax());
		
		assertEquals(IExpression.FUNCTION_LITERAL_DECLARATION_EXPRESSION, expr.getNodeType0());
		assertPosition(expr, 1, s.length() - 1);
		
		assertEquals(0, expr.arguments().size());
		assertPosition(expr.getBody(), 4, 3);
	}
	
	public void testFunctionLiteralWithParams() {
		String s = " (int x) { }";
		IFunctionExpression expr = (IFunctionExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(1, expr.arguments().size());
		assertEquals("int", expr.arguments().get(0).getType().toString());
	}
	
	public void testFunctionLiteralWithoutParameters() {
		String s = " { }";
		IFunctionExpression expr = (IFunctionExpression) new ParserFacade().parseExpression(s);
		
		assertPosition(expr, 1, s.length() - 1);
		
		assertEquals(0, expr.arguments().size());
	}	
	
	public void testAnnonymousClass() {
		String s = " new class { }";
		INewAnonymousClassExpression expr = (INewAnonymousClassExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.NEW_ANONYMOUS_CLASS_EXPRESSION, expr.getNodeType0());
		assertPosition(expr, 1, s.length() - 1);
		
		assertEquals(0, expr.newArguments().size());
		assertEquals(0, expr.constructorArguments().size());
		assertEquals(0, expr.baseClasses().size());
	}
	
	public void testAnnonymousClass2() {
		String s = " new (1, 2) class (3, 4) { }";
		INewAnonymousClassExpression expr = (INewAnonymousClassExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.NEW_ANONYMOUS_CLASS_EXPRESSION, expr.getNodeType0());
		assertPosition(expr, 1, s.length() - 1);
		
		assertEquals(2, expr.newArguments().size());
		assertEquals(2, expr.constructorArguments().size());
		assertEquals(0, expr.baseClasses().size());
	}
	
	public void testAnnonymousClass3() {
		String s = " new class A, B { }";
		INewAnonymousClassExpression expr = (INewAnonymousClassExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.NEW_ANONYMOUS_CLASS_EXPRESSION, expr.getNodeType0());
		assertPosition(expr, 1, s.length() - 1);
		
		assertNull(expr.getExpression());
		assertEquals(0, expr.newArguments().size());
		assertEquals(0, expr.constructorArguments().size());
		assertEquals(2, expr.baseClasses().size());
	}
	
	public void testAnnonymousClass4() {
		String s = " id.new (1, 2) class (3, 4) { }";
		INewAnonymousClassExpression expr = (INewAnonymousClassExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.NEW_ANONYMOUS_CLASS_EXPRESSION, expr.getNodeType0());
		assertPosition(expr, 1, s.length() - 1);
		
		assertEquals("id", ((SimpleName) expr.getExpression()).getIdentifier());
		assertEquals(2, expr.newArguments().size());
		assertEquals(2, expr.constructorArguments().size());
		assertEquals(0, expr.baseClasses().size());
	}
	
	// TODO this is *ugly*
	public void testNewDynamicArray1() {
		String s = " new int[3]";
		INewExpression expr = (INewExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.NEW_EXPRESSION, expr.getNodeType0());
		assertPosition(expr, 1, s.length() - 1);
		
		IDynamicArrayType array = (IDynamicArrayType) expr.getType();
		assertEquals(IArrayType.DYNAMIC_ARRAY_TYPE, array.getNodeType0());
		assertEquals(1, expr.constructorArguments().size());
		assertEquals("3", ((NumberLiteral) expr.constructorArguments().get(0)).getToken());
		assertPosition(expr.constructorArguments().get(0), 9, 1);
	}
	
	// TODO this is *ugly*
	public void testNewDynamicArray2() {
		String s = " new (1, 2) int[3]";
		INewExpression expr = (INewExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.NEW_EXPRESSION, expr.getNodeType0());
		assertPosition(expr, 1, s.length() - 1);
		
		IDynamicArrayType array = (IDynamicArrayType) expr.getType();
		assertEquals(IArrayType.DYNAMIC_ARRAY_TYPE, array.getNodeType0());
		assertEquals(1, expr.constructorArguments().size());
	}
	
	public void testNewDynamicArray3() {
		String s = " new (1, 2) int[][30]";
		INewExpression expr = (INewExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.NEW_EXPRESSION, expr.getNodeType0());
		assertPosition(expr, 1, s.length() - 1);
		
		IDynamicArrayType array = (IDynamicArrayType) expr.getType();
		assertEquals(IArrayType.DYNAMIC_ARRAY_TYPE, array.getNodeType0());
		assertEquals(1, expr.constructorArguments().size());
	}
	
	public void testNewDynamicArray4() {
		String s = " new (1, 2) int[][](30)";
		INewExpression expr = (INewExpression) new ParserFacade().parseExpression(s);
		
		assertEquals(IExpression.NEW_EXPRESSION, expr.getNodeType0());
		assertPosition(expr, 1, s.length() - 1);
		
		IDynamicArrayType array = (IDynamicArrayType) expr.getType();
		assertEquals(IArrayType.DYNAMIC_ARRAY_TYPE, array.getNodeType0());
		assertEquals(1, expr.constructorArguments().size());
	}
	
	public void testDotTemplateTypeExpression() {
		String s = " .Temp!(int)";
		
		
		DotTemplateTypeExpression dot = (DotTemplateTypeExpression) new ParserFacade().parseExpression(s);
		assertPosition(dot, 1, 11);
		
		assertNull(dot.getExpression());
		
		TemplateType type2 = dot.getTemplateType();
		assertEquals("Temp", type2.getName().getFullyQualifiedName());
		assertEquals(1, type2.arguments().size());
	}

	public void testDotTemplateTypeExpression2() {
		String s = " bla.Temp!(int)";
		
		
		DotTemplateTypeExpression dot = (DotTemplateTypeExpression) new ParserFacade().parseExpression(s);
		assertPosition(dot, 1, 14);
		
		assertEquals("bla", ((SimpleName) dot.getExpression()).getFullyQualifiedName());
		
		TemplateType type2 = dot.getTemplateType();
		assertEquals("Temp", type2.getName().getFullyQualifiedName());
		assertEquals(1, type2.arguments().size());
	}

}
