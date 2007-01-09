package descent.tests.mars;

import descent.core.dom.CompilationUnit;


public class Bugs_Test extends Parser_Test {
	
	// Used to fail in data[i].p
	public void testBug1() {
		String s = " void bla() { if (data[i].p == p) { } }";
		getCompilationUnit(s);
	}
	
	// Used to fail in (Pool).sizeof
	public void testBug2() {
		String s = " void bla() { (Pool).sizeof = 2; }";
		getCompilationUnit(s);
	}
	
	// Used to not parse correctly the asm statement
	public void testBug3() {
		String s = " void bla() { asm { pushad; } }";
		CompilationUnit cu = getCompilationUnit(s);
		assertEquals(1, cu.declarations().size());
	}
	
	public void testBug4() {
		String s = " a b ((void));";
		getCompilationUnit(s);
	}
	
	// Dont give error on UTF characters
	public void testBugUTF() {
		String s = " // ö";
		CompilationUnit unit = getCompilationUnit(s);
		assertEquals(0, unit.getProblems().length);
	}

}
