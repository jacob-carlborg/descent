package descent.tests.mars;

import descent.core.compiler.IProblem;

public class Semantic1_Test extends Parser_Test {
	
	public void testDuplicatedVar1() {
		String s = "int a; int a;";
		IProblem[] p = getModuleProblems(s);
		assertEquals(2, p.length);
		
		assertError(p[0], IProblem.DuplicatedSymbol, 4, 1);
		assertError(p[1], IProblem.DuplicatedSymbol, 11, 1);
	}
	
	public void testDuplicatedVar2() {
		String s = "int a, a;";
		IProblem[] p = getModuleProblems(s);
		assertEquals(2, p.length);
		
		assertError(p[0], IProblem.DuplicatedSymbol, 4, 1);
		assertError(p[1], IProblem.DuplicatedSymbol, 7, 1);
	}
	
	public void testDuplicatedSymbol() {
		String s = "int a; class a { }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(2, p.length);
		
		assertError(p[0], IProblem.DuplicatedSymbol, 4, 1);
		assertError(p[1], IProblem.DuplicatedSymbol, 13, 1);
	}
	
	public void testDuplicatedSymbolWithProt() {
		String s = "int a; public class a { }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(2, p.length);
		
		assertError(p[0], IProblem.DuplicatedSymbol, 4, 1);
		assertError(p[1], IProblem.DuplicatedSymbol, 20, 1);
	}
	
	public void testDuplicatedSymbolWithStorageClass() {
		String s = "int a; static class a { }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(2, p.length);
		
		assertError(p[0], IProblem.DuplicatedSymbol, 4, 1);
		assertError(p[1], IProblem.DuplicatedSymbol, 20, 1);
	}
	
	public void testDuplicatedSymbolWithLink() {
		String s = "int a; extern { int a; }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(2, p.length);
		
		assertError(p[0], IProblem.DuplicatedSymbol, 4, 1);
		assertError(p[1], IProblem.DuplicatedSymbol, 20, 1);
	}
	
	public void testPropertiesCannotBeRedefined() {
		String s = "class C { int sizeof; int alignof; int mangleof; }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(3, p.length);
		
		assertError(p[0], IProblem.PropertyCanNotBeRedefined, 14, 6);
		assertError(p[1], IProblem.PropertyCanNotBeRedefined, 26, 7);
		assertError(p[2], IProblem.PropertyCanNotBeRedefined, 39, 8);
	}
	
	public void testTypedefCircularDefinition() {
		String s = "typedef one two; typedef two one;";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);
		
		assertError(p[0], IProblem.CircularDefinition, 29, 3);
	}
	
	public void testEnumBaseTypeMustBeOfIntegralType() {
		String s = "enum x : double { a }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);
		
		assertError(p[0], IProblem.EnumBaseTypeMustBeOfIntegralType, 9, 6);
	}
	
	public void testEnumBaseTypeMustBeOfIntegralType_OK() {
		assertNoSemanticErrors("enum x : int { a }");
	}
	
	public void testEnumMustHaveAtLeastOneMember() {
		String s = "enum x { }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);
		
		assertError(p[0], IProblem.EnumMustHaveAtLeastOneMember, 5, 1);
	}
	
	public void testEnumMustHaveAtLeastOneMember_Not() {
		assertNoSemanticErrors("enum x;");
	}
	
	public void testEnumAnonMustHaveAtLeastOneMember2() {
		String s = " enum { }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);
		
		assertError(p[0], IProblem.EnumMustHaveAtLeastOneMember, 1, 4);
	}
	
	public void testEnumMustHaveAtLeastOneMember_OK() {
		assertNoSemanticErrors("enum x { a }");
	}
	
	public void testEnumOverflowWithBool() {
		String s = "enum x : bool { a, b, c }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);
		
		assertError(p[0], IProblem.EnumValueOverflow, 22, 1);
	}
	
	public void testEnumOverflowWithBool2() {
		String s = "enum x : bool { a = 1, b }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);
		
		assertError(p[0], IProblem.EnumValueOverflow, 23, 1);
	}
	
	public void testAliasCannotBeConst() {
		String s = "alias const int x;";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);
		
		assertError(p[0], IProblem.IllegalModifier, 6, 5);
	}
	
	public void testAliasCircularDeclaration() {
		String s = "alias one two; alias two one;";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);
		
		assertError(p[0], IProblem.CircularDefinition, 25, 3);
	}
	
	public void testAliasCircularDeclaration2() {
		String s = "alias one two; alias two three; alias three one;";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);
		
		assertError(p[0], IProblem.CircularDefinition, 44, 3);
	}
	
	public void testAliasCircularDeclaration_Not() {
		assertNoSemanticErrors("int one = 2; alias one two; alias two three;");
	}
	
	public void testAliasForwardReference() {
		String s = "alias x y; int x = 2;";
		IProblem[] p = getModuleProblems(s);
		assertEquals(2, p.length);
		
		assertError(p[0], IProblem.ForwardReference, 6, 1);
		assertError(p[1], IProblem.ForwardReference, 15, 1);
	}
	
	public void testAliasForwardReference_Not() {
		assertNoSemanticErrors("int x = 2; alias x y;");
	}
	
	public void testTypedefUsedAsAType() {
		String s = "typedef x y; int x = 2;";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);
		
		assertError(p[0], IProblem.UsedAsAType, 8, 1);
	}
	
	public void testExternSymbolsCannotHaveInitializers() {
		String s = "extern int x = 1;";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);
		
		assertError(p[0], IProblem.ExternSymbolsCannotHaveInitializers, 15, 1);
	}
	
	public void testVoidsHaveNoValue() {
		String s = " void x;";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);
		
		assertError(p[0], IProblem.VoidsHaveNoValue, 1, 4);
	}
	
	public void testUsedAsAType() {
		String s = " class X  { } Y y;";
		IProblem[] p = getModuleProblems(s);
		assertEquals(2, p.length);
		
		assertError(p[0], IProblem.IdentifierNotDefined, 14, 1);
		assertError(p[1], IProblem.UsedAsAType, 14, 1);
	}
	
	public void testUsedAsAType_Not() {
		assertNoSemanticErrors(" class X { } X x;");
	}
	
	public void testCannotInferTypeFromArrayInitializer() {
		String s = " auto x = [];";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);
		
		assertError(p[0], IProblem.CannotInferType, 10, 2);
	}
	
	public void testCannotInferTypeFromArrayInitializer2() {
		String s = " auto x = [ 1 : 1 ];";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);
		
		assertError(p[0], IProblem.CannotInferType, 10, 9);
	}
	
	public void testVariableCannotBeSynchronized() {
		String s = " synchronized int x;";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);
		
		assertError(p[0], IProblem.IllegalModifier, 18, 1);
	}
	
	public void testVariableCannotBeOverride() {
		String s = " override int x;";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);
		
		assertError(p[0], IProblem.IllegalModifier, 14, 1);
	}
	
	public void testVariableCannotBeAbstract() {
		String s = " abstract int x;";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);
		
		assertError(p[0], IProblem.IllegalModifier, 14, 1);
	}
	
	public void testNoDefinitionOfStruct() {
		String s = " struct s; s x;";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);
		
		assertError(p[0], IProblem.NoDefinition, 11, 1);
	}
	
	public void testDuplicatedInterfaceInheritance() {
		String s = " interface A { } class B : A, A { }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);
		
		assertError(p[0], IProblem.DuplicatedInterfaceInheritance, 30, 1);
	}
	
	public void testCircularInterfaceInheritance() {
		String s = " class A : A { }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);
		
		assertError(p[0], IProblem.CircularDefinition, 11, 1);
	}
	
	public void testBaseTypeWrong() {
		String s = " int x; class A : x { }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);
		
		assertError(p[0], IProblem.UsedAsAType, 18, 1);
	}
	
	public void testInterfaceTypeWrong() {
		String s = " class B { } int x; class A : B, x { }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);
		
		assertError(p[0], IProblem.UsedAsAType, 33, 1);
	}
	
	public void testInterfaceCannotDeclareFields() {
		String s = " interface A { int x; }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);
		
		assertError(p[0], IProblem.FieldsNotAllowedInInterfaces, 19, 1);
	}

}
