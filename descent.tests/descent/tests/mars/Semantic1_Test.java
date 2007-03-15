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
	
	public void testPropertiesCannotBeRedefined() {
		String s = "class C { int sizeof; int alignof; int mangleof; }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(3, p.length);
		
		assertError(p[0], IProblem.PropertyCanNotBeRedefined, 14, 6);
		assertError(p[1], IProblem.PropertyCanNotBeRedefined, 26, 7);
		assertError(p[2], IProblem.PropertyCanNotBeRedefined, 39, 8);
	}

}
