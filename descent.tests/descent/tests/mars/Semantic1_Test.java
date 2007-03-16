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
	
	public void testPropertiesCannotBeRedefined() {
		String s = "class C { int sizeof; int alignof; int mangleof; }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(3, p.length);
		
		assertError(p[0], IProblem.PropertyCanNotBeRedefined, 14, 6);
		assertError(p[1], IProblem.PropertyCanNotBeRedefined, 26, 7);
		assertError(p[2], IProblem.PropertyCanNotBeRedefined, 39, 8);
	}
	
	/* TODO semantic test
	public void testTypedefCircularDefinition() {
		String s = "typedef int a; typedef a b; typedef b a;";
		IProblem[] p = getModuleProblems(s);
		assertEquals(2, p.length);
		
		assertError(p[0], IProblem.CircularDefinition, 12, 1);
		assertError(p[1], IProblem.CircularDefinition, 14, 6);
	}
	*/
	
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

}
