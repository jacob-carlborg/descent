package descent.tests.mars;

import descent.core.compiler.IProblem;
import descent.core.dom.CompilationUnit;

public class Recovery_Tests extends Parser_Test {
	
	public void testInvalidDeclaration() {
		assertParsingErrorDeleteToken_NoDeclarations(" ,", 1, 1);
	}
	
	// Alias and typedefs are parsed the same wya
	public void testAliasAlone() {
		assertParsingErrorInsertTokenAfter_NoDeclarations(" alias", 1, 5);
	}
	
	/* TODO recovery
	public void testAliasWithType() {
		assertParsingErrorInsertTokenAfter_NoDeclarations(" alias int", 7, 3);
	}
	*/
	
	public void testAliasOk() {
		String s = " alias bool ble;";
		CompilationUnit cu = getCompilationUnit(s);
		assertEquals(0, cu.getProblems().length);
		assertEquals(1, cu.declarations().size());
	}
	
	public void testStaticAlone() {
		assertParsingErrorInsertToComplete_OneDeclaration(" static", 1, 6);
	}
	
	public void testModuleAlone() {
		assertParsingErrorDeleteToken_NoDeclarations(" module", 1, 6);
	}
	
	/* TODO recovery
	public void testModuleWithNameIsMalformed() {
		String s = " module a";
		CompilationUnit cu = getCompilationUnit(s);
		assertEquals(1, cu.getProblems().length);
		assertError(cu.getProblems()[0], IProblem.ParsingErrorInsertTokenAfter, 8, 1);
		
		ModuleDeclaration md = cu.getModuleDeclaration();
		assertNotNull(md);
		
		assertPosition(md, 1, s.length() - 1);
		assertMalformed(md);
		
		assertNotNull(md.getName());
		assertPosition(md.getName(), 8, 1);
		assertRecovered(md.getName());
	}
	*/
	
	public void testModuleErrorDosentExitParsing() {
		String s = " module int x = 2;";
		CompilationUnit cu = getCompilationUnit(s);
		assertEquals(1, cu.getProblems().length);
		assertError(cu.getProblems()[0], IProblem.ParsingErrorDeleteToken, 1, 6);
		
		assertNull(cu.getModuleDeclaration());
		assertEquals(1, cu.declarations().size());
	}
	
	public void testModuleErrorDosentExitParsing2() {
		String s = " module a int x = 2;";
		CompilationUnit cu = getCompilationUnit(s);
		assertEquals(1, cu.getProblems().length);
		assertError(cu.getProblems()[0], IProblem.ParsingErrorInsertTokenAfter, 8, 1);
		
		assertNotNull(cu.getModuleDeclaration());
		assertEquals(1, cu.declarations().size());
	}
	
	public void testModuleErrorDosentExitParsing3() {
		String s = " module a. int x = 2;";
		CompilationUnit cu = getCompilationUnit(s);
		assertEquals(1, cu.getProblems().length);
		assertError(cu.getProblems()[0], IProblem.ParsingErrorInsertTokenAfter, 9, 1);
		
		assertNull(cu.getModuleDeclaration());
		assertEquals(1, cu.declarations().size());
	}
	
	// Classes and interfaces are parsed the same way
	public void testClassAlone() {
		assertParsingErrorInsertTokenAfter_NoDeclarations(" class", 1, 5);
	}
	
	public void testClassWithIdentifier() {
		assertParsingErrorInsertToComplete_OneDeclaration(" class A", 7, 1);
	}
	
	public void testClassWithoutIdentifierWithBody() {
		assertParsingErrorInsertTokenAfter_OneDeclaration(" class { }", 1, 5);
	}
	
	/* TODO recovery
	public void testClassWithoutBaseClassesWithoutBody() {
		String s = " class A : ";
		CompilationUnit cu = getCompilationUnit(s);
		assertEquals(2, cu.getProblems().length);
		assertError(cu.getProblems()[0], IProblem.ParsingErrorInsertTokenAfter, 9, 1);
		assertError(cu.getProblems()[1], IProblem.ParsingErrorInsertToComplete, 9, 1);
		assertEquals(1, cu.declarations().size());
	}
	*/
	
	public void testClassWithoutBaseClassesWithBody() {
		assertParsingErrorInsertTokenAfter_OneDeclaration(" class A : { }", 9, 1);
	}
	
	/* TODO recovery
	public void testClassWithBaseClassesWithoutBody() {
		assertParsingErrorInsertToComplete_OneDeclaration(" class A : B", 11, 1);
	}
	*/

	// Unions and structs are parsed the same way
	public void testUnionOnlyIsNothing() {
		assertParsingErrorInsertToComplete_NoDeclarations(" union", 1, 5);
	}
	
	public void testAnnonymousUnion() {
		String s = " union { }";
		CompilationUnit cu = getCompilationUnit(s);
		assertEquals(0, cu.getProblems().length);
		assertEquals(1, cu.declarations().size());
	}
	
	/* TODO recovery
	public void testTemplateWithoutTemplateParameters() {
		String s = " template T { }";
		CompilationUnit cu = getCompilationUnit(s);
		assertEquals(1, cu.getProblems().length);
		assertEquals(1, cu.declarations().size());
		assertMalformed(cu.declarations().get(0));
	}
	*/
	
	private void assertParsingErrorDeleteToken_NoDeclarations(String s, int start, int length) {
		CompilationUnit cu = getCompilationUnit(s);
		assertEquals(1, cu.getProblems().length);
		assertError(cu.getProblems()[0], IProblem.ParsingErrorDeleteToken, start, length);
		assertNull(cu.getModuleDeclaration());
		assertEquals(0, cu.declarations().size());
	}
	
	private void assertParsingErrorInsertToComplete_NoDeclarations(String s, int start, int length) {
		CompilationUnit cu = getCompilationUnit(s);
		assertEquals(1, cu.getProblems().length);
		assertError(cu.getProblems()[0], IProblem.ParsingErrorInsertToComplete, start, length);
		assertNull(cu.getModuleDeclaration());
		assertEquals(0, cu.declarations().size());
	}
	
	private void assertParsingErrorInsertTokenAfter_NoDeclarations(String s, int start, int length) {
		CompilationUnit cu = getCompilationUnit(s);
		assertEquals(1, cu.getProblems().length);
		assertError(cu.getProblems()[0], IProblem.ParsingErrorInsertTokenAfter, start, length);
		assertNull(cu.getModuleDeclaration());
		assertEquals(0, cu.declarations().size());
	}
	
	private void assertParsingErrorInsertTokenAfter_OneDeclaration(String s, int start, int length) {
		CompilationUnit cu = getCompilationUnit(s);
		assertEquals(1, cu.getProblems().length);
		assertError(cu.getProblems()[0], IProblem.ParsingErrorInsertTokenAfter, start, length);
		assertNull(cu.getModuleDeclaration());
		assertEquals(1, cu.declarations().size());
	}
	
	private void assertParsingErrorInsertToComplete_OneDeclaration(String s, int start, int length) {
		CompilationUnit cu = getCompilationUnit(s);
		assertEquals(1, cu.getProblems().length);
		assertError(cu.getProblems()[0], IProblem.ParsingErrorInsertToComplete, start, length);
		assertNull(cu.getModuleDeclaration());
		assertEquals(1, cu.declarations().size());
	}
	
	/*
	private void assertParsingErrorDeleteToken_OneDeclaration(String s, int start, int length) {
		CompilationUnit cu = getCompilationUnit(s);
		assertEquals(1, cu.getProblems().length);
		assertError(cu.getProblems()[0], IProblem.ParsingErrorDeleteToken, start, length);
		assertNull(cu.getModuleDeclaration());
		assertEquals(1, cu.declarations().size());
	}
	*/

}
