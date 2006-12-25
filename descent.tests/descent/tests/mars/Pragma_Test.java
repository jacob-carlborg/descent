package descent.tests.mars;

import descent.core.dom.ASTNode;
import descent.core.dom.PragmaDeclaration;

public class Pragma_Test extends Parser_Test {
	
	public void testOne() {
		String s = " pragma(lib, 1, 2, 3)";
		PragmaDeclaration p = (PragmaDeclaration) getSingleDeclarationNoProblems(s);
		assertEquals(ASTNode.PRAGMA_DECLARATION, p.getNodeType());
		
		assertPosition(p, 1, s.length() - 1);
		
		assertEquals("lib", p.getName().getIdentifier());
		assertPosition(p.getName(), 8, 3);
		
		assertEquals(3, p.arguments().size());
	}

}
