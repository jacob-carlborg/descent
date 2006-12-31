package descent.tests.mars;

import descent.core.dom.ASTNode;
import descent.core.dom.CompilationUnit;
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
		assertEquals(" something ", cu.getScriptLine().getText());
	}
	
	public void testScriptLine2() {
		String s = "#! something \r\n module a;";
		CompilationUnit cu = getCompilationUnit(s);
		assertNotNull(cu.getScriptLine());
		assertPosition(cu.getScriptLine(), 0, 13);
		assertEquals(" something ", cu.getScriptLine().getText());
	}
	
	public void testScriptLine3() {
		String s = "#! something ";
		CompilationUnit cu = getCompilationUnit(s);
		assertNotNull(cu.getScriptLine());
		assertPosition(cu.getScriptLine(), 0, 13);
		assertEquals(" something ", cu.getScriptLine().getText());
	}

}
