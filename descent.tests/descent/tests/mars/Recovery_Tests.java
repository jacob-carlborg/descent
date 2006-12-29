package descent.tests.mars;

import descent.core.dom.CompilationUnit;
import descent.core.dom.ModuleDeclaration;

public class Recovery_Tests extends Parser_Test {
	
	public void testModuleOnlyIsNothing() {
		String s = " module";
		CompilationUnit cu = getCompilationUnit(s);
		assertEquals(1, cu.getProblems().length);
		assertNull(cu.getModuleDeclaration());
	}
	
	public void testModuleWithNameIsMalformed() {
		String s = " module a";
		CompilationUnit cu = getCompilationUnit(s);
		assertEquals(1, cu.getProblems().length);
		
		ModuleDeclaration md = cu.getModuleDeclaration();
		assertNotNull(md);
		
		assertPosition(md, 1, s.length() - 1);
		assertMalformed(md);
		
		assertNotNull(md.getName());
		assertPosition(md.getName(), 8, 1);
		assertRecovered(md.getName());
	}
	
	public void testModuleErrorDosentExitParsing() {
		String s = " module int x = 2;";
		CompilationUnit cu = getCompilationUnit(s);
		assertEquals(1, cu.getProblems().length);
		
		assertNull(cu.getModuleDeclaration());
		assertEquals(1, cu.declarations().size());
	}
	
	public void testModuleErrorDosentExitParsing2() {
		String s = " module a int x = 2;";
		CompilationUnit cu = getCompilationUnit(s);
		assertEquals(1, cu.getProblems().length);
		
		assertNotNull(cu.getModuleDeclaration());
		assertEquals(1, cu.declarations().size());
	}
	
	public void testModuleErrorDosentExitParsing3() {
		String s = " module a. int x = 2;";
		CompilationUnit cu = getCompilationUnit(s);
		assertEquals(1, cu.getProblems().length);
		
		assertNull(cu.getModuleDeclaration());
		assertEquals(1, cu.declarations().size());
	}
	
	public void testClassOnlyIsNothing() {
		String s = " module a. int x = 2;";
		CompilationUnit cu = getCompilationUnit(s);
		assertEquals(1, cu.getProblems().length);
		
		assertNull(cu.getModuleDeclaration());
		assertEquals(1, cu.declarations().size());
	}

}
