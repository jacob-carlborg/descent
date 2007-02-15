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
	
	public void testBug5a() {
		String s = " void bla() { return std.string.toString(buffer).dup; }";
		CompilationUnit cu = getCompilationUnit(s);
		assertEquals(1, cu.declarations().size());
	}
	
	public void testBug5b() {
		String s = " void bla() { return std.string.toString--.dup; }";
		CompilationUnit cu = getCompilationUnit(s);
		assertEquals(1, cu.declarations().size());
	}
	
	public void testBug5c() {
		String s = " void bla() { return std.string.toString++.dup; }";
		CompilationUnit cu = getCompilationUnit(s);
		assertEquals(1, cu.declarations().size());
	}
	
	public void testBug6() {
		String s = " void bla() { return isNumeric(va_arg!(char[])(_argptr)); }";
		CompilationUnit cu = getCompilationUnit(s);
		assertEquals(1, cu.declarations().size());
	}
	
	public void testBug7() {
		String s = " const char[16] hexdigits = \"0123456789ABCDEF\\n\";			/// 0..9A..F";
		CompilationUnit cu = getCompilationUnit(s);
		assertEquals(1, cu.declarations().size());
	}
	
	// Dont give error on UTF characters
	public void testBugUTF() {
		String s = " // ö";
		CompilationUnit unit = getCompilationUnit(s);
		assertEquals(0, unit.getProblems().length);
	}
	
	public void testBug8() {
		String s = " /** hola */ void bla() { }";
		CompilationUnit unit = getCompilationUnit(s);
		assertEquals(1, unit.declarations().get(0).preDDocs().size());
	}

}
