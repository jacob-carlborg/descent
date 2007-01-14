package descent.tests.mars;

import descent.core.dom.ASTNode;
import descent.core.dom.CompilationUnit;
import descent.core.dom.Pragma;
import descent.core.dom.PragmaDeclaration;

public class Pragma_Test extends Parser_Test {
	
	public void testDeclaration() {
		String s = " pragma(lib, 1, 2, 3)";
		PragmaDeclaration p = (PragmaDeclaration) getSingleDeclarationNoProblems(s);
		assertEquals(ASTNode.PRAGMA_DECLARATION, p.getNodeType());
		
		assertPosition(p, 1, s.length() - 1);
		
		assertEquals("lib", p.getName().getIdentifier());
		assertPosition(p.getName(), 8, 3);
		
		assertEquals(3, p.arguments().size());
	}
	
	public void testScriptLine1() {
		String s = "#! something \n module a;";
		CompilationUnit cu = getCompilationUnit(s);
		assertNotNull(cu.getScriptLine());
		assertPosition(cu.getScriptLine(), 0, 13);
	}
	
	public void testScriptLine2() {
		String s = "#! something \r\n module a;";
		CompilationUnit cu = getCompilationUnit(s);
		assertNotNull(cu.getScriptLine());
		assertPosition(cu.getScriptLine(), 0, 13);
	}
	
	public void testScriptLine3() {
		String s = "#! something ";
		CompilationUnit cu = getCompilationUnit(s);
		assertNotNull(cu.getScriptLine());
		assertPosition(cu.getScriptLine(), 0, 13);
	}
	
	public void testPragmaBroken1() {
		String s = " # 1";
		CompilationUnit cu = getCompilationUnit(s);
		assertEquals(1, cu.getPragmaList().size());
		
		Pragma pragma = cu.getPragmaList().get(0); 
		assertPosition(pragma, 1, s.length() - 1);
		assertMalformed(pragma);
	}
	
	public void testPragmaBroken2() {
		String s = " # pragma";
		CompilationUnit cu = getCompilationUnit(s);
		assertEquals(1, cu.getPragmaList().size());
		
		Pragma pragma = cu.getPragmaList().get(0); 
		assertPosition(pragma, 1, s.length() - 1);
		assertMalformed(pragma);
	}
	
	public void testPragmaBroken3() {
		String s = " #line bla";
		CompilationUnit cu = getCompilationUnit(s);
		assertEquals(1, cu.getPragmaList().size());
		
		Pragma pragma = cu.getPragmaList().get(0); 
		assertPosition(pragma, 1, s.length() - 1);
		assertMalformed(pragma);
	}
	
	public void testPragmaOk1() {
		String s = " #line 1";
		CompilationUnit cu = getCompilationUnit(s);
		assertEquals(1, cu.getPragmaList().size());
		
		Pragma pragma = cu.getPragmaList().get(0); 
		assertPosition(pragma, 1, s.length() - 1);
	}
	
	public void testPragmaOk2() {
		String s = " #line 1\r\n";
		CompilationUnit cu = getCompilationUnit(s);
		assertEquals(1, cu.getPragmaList().size());
		
		Pragma pragma = cu.getPragmaList().get(0); 
		assertPosition(pragma, 1, 7);
	}
	
	public void testPragmaBroken4() {
		String s = " #line 1 something";
		CompilationUnit cu = getCompilationUnit(s);
		assertEquals(1, cu.getPragmaList().size());
		
		Pragma pragma = cu.getPragmaList().get(0); 
		assertPosition(pragma, 1, s.length() - 1);
		assertMalformed(pragma);
	}
	
	public void testPragmaBroken5() {
		String s = " #line 1 \"hola";
		CompilationUnit cu = getCompilationUnit(s);
		assertEquals(1, cu.getPragmaList().size());
		
		Pragma pragma = cu.getPragmaList().get(0); 
		assertPosition(pragma, 1, s.length() - 1);
		assertMalformed(pragma);
	}
	
	public void testPragmaOk3() {
		String s = " #line 1 \"hola\"";
		CompilationUnit cu = getCompilationUnit(s);
		assertEquals(1, cu.getPragmaList().size());
		
		Pragma pragma = cu.getPragmaList().get(0); 
		assertPosition(pragma, 1, s.length() - 1);
	}

}
