package descent.tests.mars;

import java.util.List;

import descent.core.dom.AST;
import descent.core.dom.ASTNode;
import descent.core.dom.ArrayAccess;
import descent.core.dom.ArrayLiteral;
import descent.core.dom.AssertExpression;
import descent.core.dom.Assignment;
import descent.core.dom.AssociativeArrayLiteral;
import descent.core.dom.AssociativeArrayLiteralFragment;
import descent.core.dom.BooleanLiteral;
import descent.core.dom.CallExpression;
import descent.core.dom.CastExpression;
import descent.core.dom.CastToModifierExpression;
import descent.core.dom.CharacterLiteral;
import descent.core.dom.ConditionalExpression;
import descent.core.dom.DeleteExpression;
import descent.core.dom.DotIdentifierExpression;
import descent.core.dom.DotTemplateTypeExpression;
import descent.core.dom.DynamicArrayType;
import descent.core.dom.Expression;
import descent.core.dom.FileImportExpression;
import descent.core.dom.FunctionLiteralDeclarationExpression;
import descent.core.dom.InfixExpression;
import descent.core.dom.IsTypeExpression;
import descent.core.dom.IsTypeSpecializationExpression;
import descent.core.dom.NewAnonymousClassExpression;
import descent.core.dom.NewExpression;
import descent.core.dom.NumberLiteral;
import descent.core.dom.ParenthesizedExpression;
import descent.core.dom.PostfixExpression;
import descent.core.dom.PrefixExpression;
import descent.core.dom.PrimitiveType;
import descent.core.dom.SimpleName;
import descent.core.dom.SimpleType;
import descent.core.dom.SliceExpression;
import descent.core.dom.StringLiteral;
import descent.core.dom.StringsExpression;
import descent.core.dom.TemplateType;
import descent.core.dom.TraitsExpression;
import descent.core.dom.TypeExpression;
import descent.core.dom.TypeidExpression;
import descent.core.dom.TypeofType;
import descent.core.dom.FunctionLiteralDeclarationExpression.Syntax;

public class Expression_Test extends Parser_Test {
	
	public void testThis() {
		String s = " this";
		Expression expr = parseExpression(s);
		
		assertEquals(ASTNode.THIS_LITERAL, expr.getNodeType());
		assertPosition(expr, 1, s.length() - 1);
	}
	
	public void testSuper() {
		String s = " super";
		Expression expr = parseExpression(s);
		
		assertEquals(ASTNode.SUPER_LITERAL, expr.getNodeType());
		assertPosition(expr, 1, s.length() - 1);
	}
	
	public void testNull() {
		String s = " null";
		Expression expr = parseExpression(s);
		
		assertEquals(ASTNode.NULL_LITERAL, expr.getNodeType());
		assertEquals("null", expr.toString());
		assertPosition(expr, 1, s.length() - 1);
	}
	
	public void testTrue() {
		String s = " true";
		BooleanLiteral expr = (BooleanLiteral) parseExpression(s);
		
		assertEquals(ASTNode.BOOLEAN_LITERAL, expr.getNodeType());
		assertTrue(expr.booleanValue());
		assertPosition(expr, 1, s.length() - 1);
	}
	
	public void testFalse() {
		String s = " false";
		BooleanLiteral expr = (BooleanLiteral) parseExpression(s);
		
		assertEquals(ASTNode.BOOLEAN_LITERAL, expr.getNodeType());
		assertFalse(expr.booleanValue());
		assertPosition(expr, 1, s.length() - 1);
	}
	
	public void testString() {
		String s = " \"hola\"";
		StringLiteral expr = (StringLiteral) parseExpression(s);
		
		assertEquals(ASTNode.STRING_LITERAL, expr.getNodeType());
		assertEquals("\"hola\"", expr.getEscapedValue());
		assertPosition(expr, 1, s.length() - 1);
	}
	
	public void testStringMany() {
		String s = " \"hola\" \"chau\"";
		StringsExpression strings = (StringsExpression) parseExpression(s);
		
		assertEquals(2, strings.stringLiterals().size());
		
		assertEquals("\"hola\"", strings.stringLiterals().get(0).getEscapedValue());
		assertPosition(strings.stringLiterals().get(0), 1, 6);
		
		assertEquals("\"chau\"", strings.stringLiterals().get(1).getEscapedValue());
		assertPosition(strings.stringLiterals().get(1), 8, 6);
	}
	
	public void testStringPostfix() {
		String s = " \"hola\"c";
		StringLiteral expr = (StringLiteral) parseExpression(s);
		
		assertEquals(ASTNode.STRING_LITERAL, expr.getNodeType());
		assertEquals("\"hola\"c", expr.getEscapedValue());
		assertPosition(expr, 1, s.length() - 1);
	}
	
	public void testStringHex() {
		String s = " x\"1234\"";
		StringLiteral expr = (StringLiteral) parseExpression(s);
		
		assertEquals(ASTNode.STRING_LITERAL, expr.getNodeType());
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
			NumberLiteral number = (NumberLiteral) parseExpression(s);
			
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
				CharacterLiteral character = (CharacterLiteral) parseExpression(s);
				
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
				CharacterLiteral character = (CharacterLiteral) parseExpression(s);
				
				assertEquals(string, character.getEscapedValue());
				assertPosition(character, 1, string.length());
			}
	}
	
	public void testAssert() {
		String s = " assert(false)";
		AssertExpression expr = (AssertExpression) parseExpression(s);
		
		assertEquals(ASTNode.ASSERT_EXPRESSION, expr.getNodeType());
		assertEquals(ASTNode.BOOLEAN_LITERAL, expr.getExpression().getNodeType());
		assertNull(expr.getMessage());
		assertPosition(expr, 1, s.length() - 1);
	}
	
	public void testAssertWithMessage() {
		String s = " assert(false, true)";
		AssertExpression expr = (AssertExpression) parseExpression(s);
		
		assertEquals(ASTNode.ASSERT_EXPRESSION, expr.getNodeType());
		assertEquals(ASTNode.BOOLEAN_LITERAL, expr.getExpression().getNodeType());
		assertEquals(ASTNode.BOOLEAN_LITERAL, expr.getMessage().getNodeType());
		assertPosition(expr, 1, s.length() - 1);
	}
	
	public void testParenthesized() {
		String s = " ( false )";
		ParenthesizedExpression expr = (ParenthesizedExpression) parseExpression(s);
		
		assertEquals(ASTNode.PARENTHESIZED_EXPRESSION, expr.getNodeType());
		assertEquals(ASTNode.BOOLEAN_LITERAL, expr.getExpression().getNodeType());
		assertPosition(expr, 1, s.length() - 1);
	}
	
	public void testParenthesized2() {
		String s = " ( a )";
		ParenthesizedExpression expr = (ParenthesizedExpression) parseExpression(s);
		
		assertEquals(ASTNode.PARENTHESIZED_EXPRESSION, expr.getNodeType());
		assertEquals(ASTNode.SIMPLE_NAME, expr.getExpression().getNodeType());
		assertPosition(expr, 1, s.length() - 1);
	}
	
	public void testInfixExpression() {
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
			InfixExpression expr = (InfixExpression) parseExpression(s);
			
			try {
				assertEquals(ASTNode.INFIX_EXPRESSION, expr.getNodeType());
				assertEquals(pair[1], expr.getOperator());
				assertEquals(pair[0], pair[1].toString());
				assertEquals(ASTNode.NUMBER_LITERAL, expr.getLeftOperand().getNodeType());
				assertEquals(ASTNode.NUMBER_LITERAL, expr.getRightOperand().getNodeType());
				assertPosition(expr, 1, 6 + ((String) pair[0]).length());
			} catch (Exception e) {
				fail(s);
			}
		}
	}
	
	public void testInfixExpressionD1() {
		Object[][] objs = {
				{ "===", InfixExpression.Operator.IDENTITY },
				{ "!==", InfixExpression.Operator.NOT_IDENTITY },
			};
		
		for(Object[] pair : objs) {
			String s = " 1 " + pair[0] + " 1.0";
			InfixExpression expr = (InfixExpression) parseExpression(s, AST.D0);
			
			assertEquals(ASTNode.INFIX_EXPRESSION, expr.getNodeType());
			assertEquals(pair[1], expr.getOperator());
			assertEquals(pair[0], pair[1].toString());
			assertEquals(ASTNode.NUMBER_LITERAL, expr.getLeftOperand().getNodeType());
			assertEquals(ASTNode.NUMBER_LITERAL, expr.getRightOperand().getNodeType());
			assertPosition(expr, 1, 6 + ((String) pair[0]).length());
		}
	}
	
	public void testAssignment() {
		Object[][] objs = {
				{ "=", Assignment.Operator.ASSIGN },
				{ "+=", Assignment.Operator.PLUS_ASSIGN },
				{ "-=", Assignment.Operator.MINUS_ASSIGN },
				{ "*=", Assignment.Operator.TIMES_ASSIGN },
				{ "/=", Assignment.Operator.DIVIDE_ASSIGN },
				{ "%=", Assignment.Operator.REMAINDER_ASSIGN },
				{ "&=", Assignment.Operator.AND_ASSIGN },
				{ "|=", Assignment.Operator.OR_ASSIGN },
				{ "^=", Assignment.Operator.XOR_ASSIGN },
				{ "<<=", Assignment.Operator.LEFT_SHIFT_ASSIGN },
				{ ">>=", Assignment.Operator.RIGHT_SHIFT_SIGNED_ASSIGN },
				{ ">>>=", Assignment.Operator.RIGHT_SHIFT_UNSIGNED_ASSIGN },
				{ "~=", Assignment.Operator.CONCATENATE_ASSIGN },
			};
		
		for(Object[] pair : objs) {
			String s = " 1 " + pair[0] + " 1.0";
			Assignment expr = (Assignment) parseExpression(s);
			
			assertEquals(ASTNode.ASSIGNMENT, expr.getNodeType());
			assertEquals(pair[1], expr.getOperator());
			assertEquals(pair[0], pair[1].toString());
			assertEquals(ASTNode.NUMBER_LITERAL, expr.getLeftHandSide().getNodeType());
			assertEquals(ASTNode.NUMBER_LITERAL, expr.getRightHandSide().getNodeType());
			assertPosition(expr, 1, 6 + ((String) pair[0]).length());
		}
	}
	
	public void testPrefixExpression() {
		Object[][] objs = { 
				// { "&", PrefixExpression.Operator.ADDRESS },
				{ "++", PrefixExpression.Operator.INCREMENT },
				{ "--", PrefixExpression.Operator.DECREMENT },
//				{ "*", PrefixExpression.Operator.POINTER },
//				{ "-", PrefixExpression.Operator.NEGATIVE },
//				{ "+", PrefixExpression.Operator.POSITIVE },
//				{ "!", PrefixExpression.Operator.NOT },
//				{ "~", PrefixExpression.Operator.INVERT },
			};
		
		for(Object[] pair : objs) {
			String s = " " + pair[0] + "1";
			PrefixExpression expr = (PrefixExpression) parseExpression(s);
			
			try {
				assertEquals(ASTNode.PREFIX_EXPRESSION, expr.getNodeType());
				assertEquals(pair[1], expr.getOperator());
				assertEquals(ASTNode.NUMBER_LITERAL, expr.getOperand().getNodeType());
				assertPosition(expr, 1, 1 + ((String) pair[0]).length());
			} catch (Throwable e) {
				fail((String) pair[0]);
			}
		}
	}
	
	public void testPostfixExpression() {
		Object[][] objs = { 
				{ "++", PostfixExpression.Operator.INCREMENT },
				{ "--", PostfixExpression.Operator.DECREMENT },
			};
		
		for(Object[] pair : objs) {
			String s = " 1" + pair[0];
			PostfixExpression expr = (PostfixExpression) parseExpression(s);
			
			assertEquals(ASTNode.POSTFIX_EXPRESSION, expr.getNodeType());
			assertEquals(pair[1], expr.getOperator());
			assertEquals(ASTNode.NUMBER_LITERAL, expr.getOperand().getNodeType());
			assertPosition(expr, 1, 1 + ((String) pair[0]).length());
		}
	}
	
	public void testCondition() {
		String s = " 1 ? true : false";
		ConditionalExpression expr = (ConditionalExpression) parseExpression(s);
		
		assertEquals(ASTNode.CONDITIONAL_EXPRESSION, expr.getNodeType());
		assertEquals(ASTNode.NUMBER_LITERAL, expr.getExpression().getNodeType());
		assertEquals(ASTNode.BOOLEAN_LITERAL, expr.getThenExpression().getNodeType());
		assertEquals(ASTNode.BOOLEAN_LITERAL, expr.getElseExpression().getNodeType());
		assertPosition(expr, 1, 16);
	}
	
	public void testTypeDotId() {
		String s = " int.length";
		DotIdentifierExpression expr = (DotIdentifierExpression) parseExpression(s);
		
		assertEquals(ASTNode.DOT_IDENTIFIER_EXPRESSION, expr.getNodeType());
		assertPosition(expr, 1, 10);
		
		TypeExpression typeExp = (TypeExpression) expr.getExpression();		
		assertEquals(ASTNode.PRIMITIVE_TYPE, typeExp.getType().getNodeType());
		assertEquals("length", expr.getName().getFullyQualifiedName());
	}
	
	public void testDelete() {
		String s = " delete some";
		DeleteExpression expr = (DeleteExpression) parseExpression(s);
		
		assertEquals(ASTNode.DELETE_EXPRESSION, expr.getNodeType());
		assertPosition(expr, 1, 11);
		
		assertEquals("some", ((SimpleName) expr.getExpression()).getIdentifier().toString());
	}
	
	public void testCast() {
		String s = " cast(float) 1";
		CastExpression expr = (CastExpression) parseExpression(s);
		
		assertEquals(ASTNode.CAST_EXPRESSION, expr.getNodeType());
		assertPosition(expr, 1, 13);
		
		assertEquals("float", expr.getType().toString());
		assertEquals("1", ((NumberLiteral) expr.getExpression()).getToken());
	}
	
	public void testCastToModifier() {
		String s = " cast(invariant) 1";
		CastToModifierExpression expr = (CastToModifierExpression) parseExpression(s, AST.D2);
		
		assertEquals(ASTNode.CAST_TO_MODIFIER_EXPRESSION, expr.getNodeType());
		assertPosition(expr, 1, s.length() - 1);
		
		assertEquals(1, expr.modifiers().size());
		assertEquals("invariant", expr.modifiers().get(0).toString());
		assertPosition(expr.modifiers().get(0), 6, 9);
		assertEquals("1", ((NumberLiteral) expr.getExpression()).getToken());
	}
	
	public void testCastToModifier2() {
		String s = " cast(const) 1";
		CastToModifierExpression expr = (CastToModifierExpression) parseExpression(s, AST.D2);
		
		assertEquals(ASTNode.CAST_TO_MODIFIER_EXPRESSION, expr.getNodeType());
		assertPosition(expr, 1, s.length() - 1);
		
		assertEquals(1, expr.modifiers().size());
		assertEquals("const", expr.modifiers().get(0).toString());
		assertPosition(expr.modifiers().get(0), 6, 5);
		assertEquals("1", ((NumberLiteral) expr.getExpression()).getToken());
	}
	
	public void testCastToModifier3() {
		String s = " cast(const shared) 1";
		CastToModifierExpression expr = (CastToModifierExpression) parseExpression(s, AST.D2);
		
		assertEquals(ASTNode.CAST_TO_MODIFIER_EXPRESSION, expr.getNodeType());
		assertPosition(expr, 1, s.length() - 1);
		
		assertEquals(2, expr.modifiers().size());
		assertEquals("const", expr.modifiers().get(0).toString());
		assertPosition(expr.modifiers().get(0), 6, 5);
		assertEquals("shared", expr.modifiers().get(1).toString());
		assertPosition(expr.modifiers().get(1), 12, 6);
		assertEquals("1", ((NumberLiteral) expr.getExpression()).getToken());
	}
	
	public void testTemplateInstance() {
		String s = " some!(int, float)";
		TypeExpression expr = (TypeExpression) parseExpression(s);
		
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
		NewExpression expr = (NewExpression) parseExpression(s);
		
		assertEquals(ASTNode.NEW_EXPRESSION, expr.getNodeType());
		assertPosition(expr, 1, 13);
		
		List<Expression> args = expr.constructorArguments();
		assertEquals(2, args.size());
		
		assertEquals("1", ((NumberLiteral) args.get(0)).getToken());
		assertEquals("2", ((NumberLiteral) args.get(1)).getToken());
	}
	
	public void testArray() {
		String s = " bla[1, 2, 3]";
		ArrayAccess expr = (ArrayAccess) parseExpression(s);
		assertEquals(ASTNode.ARRAY_ACCESS, expr.getNodeType());
		
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
		ArrayLiteral expr = (ArrayLiteral) parseExpression(s);
		assertEquals(ASTNode.ARRAY_LITERAL, expr.getNodeType());
		
		Expression[] args = expr.arguments().toArray(new Expression[expr.arguments().size()]);
		assertEquals(3, args.length);
		
		assertEquals("1", ((NumberLiteral) args[0]).getToken());
		assertEquals("2", ((NumberLiteral) args[1]).getToken());
		assertEquals("3", ((NumberLiteral) args[2]).getToken());
		
		assertPosition(expr, 1, s.length() - 1);
	}
	
	public void testAssociativeArrayLiteral() {
		String s = " [1:a, 2:b, 3:c]";
		AssociativeArrayLiteral expr = (AssociativeArrayLiteral) parseExpression(s);
		assertEquals(ASTNode.ASSOCIATIVE_ARRAY_LITERAL, expr.getNodeType());
		
		AssociativeArrayLiteralFragment[] args = expr.fragments().toArray(new AssociativeArrayLiteralFragment[expr.fragments().size()]);
		assertEquals(3, args.length);
		
		assertEquals("1", args[0].getKey().toString());
		assertEquals("a", args[0].getValue().toString());
		assertPosition(args[0], 2, 3);
		
		assertEquals("2", args[1].getKey().toString());
		assertEquals("b", args[1].getValue().toString());
		assertPosition(args[1], 7, 3);
		
		assertEquals("3", args[2].getKey().toString());
		assertEquals("c", args[2].getValue().toString());
		assertPosition(args[2], 12, 3);
		
		assertPosition(expr, 1, s.length() - 1);
	}
	
	public void testSlice() {
		String s = " bla[1 .. 3]";
		SliceExpression expr = (SliceExpression) parseExpression(s);
		assertEquals(ASTNode.SLICE_EXPRESSION, expr.getNodeType());
		
		assertEquals("bla", ((SimpleName) expr.getExpression()).getIdentifier());
		
		assertEquals("1", ((NumberLiteral) expr.getFromExpression()).getToken());
		assertEquals("3", ((NumberLiteral) expr.getToExpression()).getToken());
		
		assertPosition(expr, 1, s.length() - 1);
	}
	
	public void testSliceEmpty() {
		String s = " bla[]";
		SliceExpression expr = (SliceExpression) parseExpression(s);
		assertEquals(ASTNode.SLICE_EXPRESSION, expr.getNodeType());
		
		assertEquals("bla", ((SimpleName) expr.getExpression()).getIdentifier());
		
		assertNull(expr.getFromExpression());
		assertNull(expr.getToExpression());
		
		assertPosition(expr, 1, s.length() - 1);
	}
	
	public void testDolar() {
		String s = " $";
		Expression expr = (Expression) parseExpression(s);
		assertEquals(Expression.DOLLAR_LITERAL, expr.getNodeType());
		
		assertPosition(expr, 1, 1);
	}
	
	public void testCall() {
		String s = " bla(1, 2, 3)";
		CallExpression expr = (CallExpression) parseExpression(s);
		assertEquals(ASTNode.CALL_EXPRESSION, expr.getNodeType());
		
		assertEquals("bla", ((SimpleName) expr.getExpression()).getIdentifier());
		
		List<Expression> args = expr.arguments();
		assertEquals(3, args.size());
		
		assertEquals("1", ((NumberLiteral) args.get(0)).getToken());
		assertEquals("2", ((NumberLiteral) args.get(1)).getToken());
		assertEquals("3", ((NumberLiteral) args.get(2)).getToken());
		
		assertPosition(expr, 1, s.length() - 1);
	}
	
	public void testTypeof() {
		String s = " typeof(3)";
		TypeExpression expr = (TypeExpression) parseExpression(s);
		assertEquals(ASTNode.TYPE_EXPRESSION, expr.getNodeType());
		
		assertEquals(ASTNode.TYPEOF_TYPE, expr.getType().getNodeType());
		assertPosition(expr, 1, s.length() - 1);
		
		TypeofType typeof = (TypeofType) expr.getType();
		assertEquals("3", ((NumberLiteral) typeof.getExpression()).getToken());
	}
	
	public void testTypeofDotId() {
		String s = " typeof(3).length";
		DotIdentifierExpression expr = (DotIdentifierExpression) parseExpression(s);
		assertEquals(ASTNode.DOT_IDENTIFIER_EXPRESSION, expr.getNodeType());
		
		TypeExpression typeExp = (TypeExpression) expr.getExpression();
		
		assertEquals(ASTNode.TYPEOF_TYPE, typeExp.getType().getNodeType());
		assertPosition(expr, 1, s.length() - 1);
		
		TypeofType typeof = (TypeofType) typeExp.getType();
		assertEquals("3", ((NumberLiteral) typeof.getExpression()).getToken());
		
		assertEquals("length", expr.getName().getFullyQualifiedName());
		assertPosition(expr.getName(), 11, 6);
	}
	
	public void testDotId() {
		String s = " .bla";
		DotIdentifierExpression expr = (DotIdentifierExpression) parseExpression(s);
		
		assertEquals(ASTNode.DOT_IDENTIFIER_EXPRESSION, expr.getNodeType());
		assertPosition(expr, 1, s.length() - 1);
		
		assertNull(expr.getExpression());
		
		assertEquals("bla", ((SimpleName) expr.getName()).getIdentifier());
		assertPosition(expr.getName(), 2, 3);
	}
	
	public void testExprDotId() {
		String s = " ble.bla";
		DotIdentifierExpression expr = (DotIdentifierExpression) parseExpression(s);
		
		assertEquals(ASTNode.DOT_IDENTIFIER_EXPRESSION, expr.getNodeType());
		assertPosition(expr, 1, s.length() - 1);
		
		assertEquals("ble", ((SimpleName) expr.getExpression()).getIdentifier());
		assertPosition(expr.getExpression(), 1, 3);
		
		assertEquals("bla", expr.getName().getIdentifier());
		assertPosition(expr.getName(), 5, 3);
	}
	
	public void testTypeid() {
		String s = " typeid(int)";
		TypeidExpression expr = (TypeidExpression) parseExpression(s);
		
		assertEquals(ASTNode.TYPEID_EXPRESSION, expr.getNodeType());
		assertPosition(expr, 1, s.length() - 1);
		
		assertEquals("int", expr.getType().toString());
	}
	
	public void testIftype() {
		String s = " is(x : float)";
		IsTypeExpression expr = (IsTypeExpression) parseExpression(s);
		
		assertEquals(ASTNode.IS_TYPE_EXPRESSION, expr.getNodeType());
		assertPosition(expr, 1, s.length() - 1);
		
		assertEquals("x", ((SimpleType) expr.getType()).getName().getFullyQualifiedName());
		assertEquals("float", expr.getSpecialization().toString());
		assertNull(expr.getName());
	}
	
	public void testIsTypeExpression() {
		String s = " is(int x : float)";
		
		IsTypeExpression expr = (IsTypeExpression) parseExpression(s);
		
		assertPosition(expr, 1, s.length() - 1);
		
		assertEquals("int", expr.getType().toString());
		assertEquals("float", expr.getSpecialization().toString());
		
		assertEquals("x", expr.getName().getIdentifier());
		assertPosition(expr.getName(), 8, 1);
		
		assertFalse(expr.isSameComparison());
	}
	
	public void testIsTypeSpecializationEmpty() {
		String s = " is(x)";
		
		IsTypeExpression expr = (IsTypeExpression) parseExpression(s);
		
		assertPosition(expr, 1, s.length() - 1);
		assertEquals("x", expr.getType().toString());
		assertNull(expr.getName());
		assertNull(expr.getSpecialization());
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
			IsTypeSpecializationExpression expr = (IsTypeSpecializationExpression) parseExpression(s);
			
			assertPosition(expr, 1, s.length() - 1);
			
			assertEquals("x", ((SimpleType) expr.getType()).getName().getFullyQualifiedName());
			assertEquals(pair[1], expr.getSpecialization());
			
			assertTrue(expr.isSameComparison());
		}
	}
	
	public void testFunctionLiteralFunction() {
		String s = " function () { }";
		FunctionLiteralDeclarationExpression expr = (FunctionLiteralDeclarationExpression) parseExpression(s);
		assertEquals(Syntax.FUNCTION, expr.getSyntax());
		
		assertPosition(expr, 1, s.length() - 1);
		
		assertEquals(0, expr.arguments().size());
	}
	
	public void testFunctionLiteralDelegate() {
		String s = " delegate { }";
		FunctionLiteralDeclarationExpression expr = (FunctionLiteralDeclarationExpression) parseExpression(s);
		assertEquals(Syntax.DELEGATE, expr.getSyntax());
		
		assertPosition(expr, 1, s.length() - 1);
		
		assertEquals(0, expr.arguments().size());
		
		assertNull(expr.getReturnType());
	}
	
	public void testFunctionLiteralDelegateWithReturnType() {
		String s = " delegate int { }";
		FunctionLiteralDeclarationExpression expr = (FunctionLiteralDeclarationExpression) parseExpression(s);
		assertEquals(Syntax.DELEGATE, expr.getSyntax());
		
		assertPosition(expr, 1, s.length() - 1);
		
		assertEquals(0, expr.arguments().size());
		
		PrimitiveType type = (PrimitiveType) expr.getReturnType();
		assertEquals(PrimitiveType.Code.INT, type.getPrimitiveTypeCode());
		assertPosition(type, 10, 3);
	}
	
	public void testFunctionLiteralEmpty() {
		String s = " { }";
		FunctionLiteralDeclarationExpression expr = (FunctionLiteralDeclarationExpression) parseExpression(s);
		assertEquals(Syntax.EMPTY, expr.getSyntax());
		
		assertEquals(ASTNode.FUNCTION_LITERAL_DECLARATION_EXPRESSION, expr.getNodeType());
		assertPosition(expr, 1, s.length() - 1);
		
		assertEquals(0, expr.arguments().size());
		assertPosition(expr.getBody(), 1, 3);
	}
	
	public void testFunctionLiteralEmpty2() {
		String s = " () { }";
		FunctionLiteralDeclarationExpression expr = (FunctionLiteralDeclarationExpression) parseExpression(s);
		assertEquals(Syntax.EMPTY, expr.getSyntax());
		
		assertEquals(ASTNode.FUNCTION_LITERAL_DECLARATION_EXPRESSION, expr.getNodeType());
		assertPosition(expr, 1, s.length() - 1);
		
		assertEquals(0, expr.arguments().size());
		assertPosition(expr.getBody(), 4, 3);
	}
	
	public void testFunctionLiteralWithParams() {
		String s = " (int x) { }";
		FunctionLiteralDeclarationExpression expr = (FunctionLiteralDeclarationExpression) parseExpression(s);
		
		assertEquals(1, expr.arguments().size());
		assertEquals("int", expr.arguments().get(0).getType().toString());
	}
	
	public void testFunctionLiteralWithoutParameters() {
		String s = " { }";
		FunctionLiteralDeclarationExpression expr = (FunctionLiteralDeclarationExpression) parseExpression(s);
		
		assertPosition(expr, 1, s.length() - 1);
		
		assertEquals(0, expr.arguments().size());
	}	
	
	public void testAnnonymousClass() {
		String s = " new class { }";
		NewAnonymousClassExpression expr = (NewAnonymousClassExpression) parseExpression(s);
		
		assertEquals(ASTNode.NEW_ANONYMOUS_CLASS_EXPRESSION, expr.getNodeType());
		assertPosition(expr, 1, s.length() - 1);
		
		assertEquals(0, expr.newArguments().size());
		assertEquals(0, expr.constructorArguments().size());
		assertEquals(0, expr.baseClasses().size());
	}
	
	public void testAnnonymousClass2() {
		String s = " new (1, 2) class (3, 4) { }";
		NewAnonymousClassExpression expr = (NewAnonymousClassExpression) parseExpression(s);
		
		assertEquals(ASTNode.NEW_ANONYMOUS_CLASS_EXPRESSION, expr.getNodeType());
		assertPosition(expr, 1, s.length() - 1);
		
		assertEquals(2, expr.newArguments().size());
		assertEquals(2, expr.constructorArguments().size());
		assertEquals(0, expr.baseClasses().size());
	}
	
	public void testAnnonymousClass3() {
		String s = " new class A, B { }";
		NewAnonymousClassExpression expr = (NewAnonymousClassExpression) parseExpression(s);
		
		assertEquals(ASTNode.NEW_ANONYMOUS_CLASS_EXPRESSION, expr.getNodeType());
		assertPosition(expr, 1, s.length() - 1);
		
		assertNull(expr.getExpression());
		assertEquals(0, expr.newArguments().size());
		assertEquals(0, expr.constructorArguments().size());
		assertEquals(2, expr.baseClasses().size());
	}
	
	public void testAnnonymousClass4() {
		String s = " id.new (1, 2) class (3, 4) { }";
		NewAnonymousClassExpression expr = (NewAnonymousClassExpression) parseExpression(s);
		
		assertEquals(ASTNode.NEW_ANONYMOUS_CLASS_EXPRESSION, expr.getNodeType());
		assertPosition(expr, 1, s.length() - 1);
		
		assertEquals("id", ((SimpleName) expr.getExpression()).getIdentifier());
		assertEquals(2, expr.newArguments().size());
		assertEquals(2, expr.constructorArguments().size());
		assertEquals(0, expr.baseClasses().size());
	}
	
	// TODO this is *ugly*
	public void testNewDynamicArray1() {
		String s = " new int[3]";
		NewExpression expr = (NewExpression) parseExpression(s);
		
		assertEquals(ASTNode.NEW_EXPRESSION, expr.getNodeType());
		assertPosition(expr, 1, s.length() - 1);
		
		DynamicArrayType array = (DynamicArrayType) expr.getType();
		assertEquals(ASTNode.DYNAMIC_ARRAY_TYPE, array.getNodeType());
		assertEquals(1, expr.constructorArguments().size());
		assertEquals("3", ((NumberLiteral) expr.constructorArguments().get(0)).getToken());
		assertPosition(expr.constructorArguments().get(0), 9, 1);
	}
	
	// TODO this is *ugly*
	public void testNewDynamicArray2() {
		String s = " new (1, 2) int[3]";
		NewExpression expr = (NewExpression) parseExpression(s);
		
		assertEquals(ASTNode.NEW_EXPRESSION, expr.getNodeType());
		assertPosition(expr, 1, s.length() - 1);
		
		DynamicArrayType array = (DynamicArrayType) expr.getType();
		assertEquals(ASTNode.DYNAMIC_ARRAY_TYPE, array.getNodeType());
		assertEquals(1, expr.constructorArguments().size());
	}
	
	public void testNewDynamicArray3() {
		String s = " new (1, 2) int[][30]";
		NewExpression expr = (NewExpression) parseExpression(s);
		
		assertEquals(ASTNode.NEW_EXPRESSION, expr.getNodeType());
		assertPosition(expr, 1, s.length() - 1);
		
		DynamicArrayType array = (DynamicArrayType) expr.getType();
		assertEquals(ASTNode.DYNAMIC_ARRAY_TYPE, array.getNodeType());
		assertEquals(1, expr.constructorArguments().size());
	}
	
	public void testNewDynamicArray4() {
		String s = " new (1, 2) int[][](30)";
		NewExpression expr = (NewExpression) parseExpression(s);
		
		assertEquals(ASTNode.NEW_EXPRESSION, expr.getNodeType());
		assertPosition(expr, 1, s.length() - 1);
		
		DynamicArrayType array = (DynamicArrayType) expr.getType();
		assertEquals(ASTNode.DYNAMIC_ARRAY_TYPE, array.getNodeType());
		assertEquals(1, expr.constructorArguments().size());
	}
	
	public void testDotTemplateTypeExpression() {
		String s = " .Temp!(int)";
		
		DotTemplateTypeExpression dot = (DotTemplateTypeExpression) parseExpression(s);
		assertPosition(dot, 1, 11);
		
		assertNull(dot.getExpression());
		
		TemplateType type2 = dot.getTemplateType();
		assertEquals("Temp", type2.getName().getFullyQualifiedName());
		assertEquals(1, type2.arguments().size());
	}

	public void testDotTemplateTypeExpression2() {
		String s = " bla.Temp!(int)";
		
		DotTemplateTypeExpression dot = (DotTemplateTypeExpression) parseExpression(s);
		assertPosition(dot, 1, 14);
		
		assertEquals("bla", ((SimpleName) dot.getExpression()).getFullyQualifiedName());
		
		TemplateType type2 = dot.getTemplateType();
		assertEquals("Temp", type2.getName().getFullyQualifiedName());
		assertEquals(1, type2.arguments().size());
	}
	
	public void testFileImport() {
		String s = " import(\"something\")";
		FileImportExpression expr = (FileImportExpression) parseExpression(s);
		assertPosition(expr, 1, s.length() - 1);
		
		StringLiteral e = (StringLiteral) expr.getExpression();
		assertEquals("\"something\"", e.getEscapedValue());
		assertPosition(e, 8, 11);
	}
	
	public void testTraitsExpression() throws Exception {
		String s = " __traits(isAbstractClass, a, b, c)";
		
		TraitsExpression exp = (TraitsExpression) parseExpression(s, AST.D2);
		assertPosition(exp, 1, s.length() - 1);
		
		assertEquals("isAbstractClass", exp.getName().toString());
		assertEquals(3, exp.arguments().size());
	}

}
