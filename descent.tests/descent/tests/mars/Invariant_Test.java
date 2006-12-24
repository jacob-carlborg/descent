package descent.tests.mars;

import descent.core.dom.ASTNode;
import descent.core.dom.InvariantDeclaration;

public class Invariant_Test extends Parser_Test {
	
	public void test() {
		String s = " invariant { } ";
		InvariantDeclaration inv = (InvariantDeclaration) getSingleDeclarationNoProblems(s);
		assertEquals(ASTNode.INVARIANT_DECLARATION, inv.getNodeType0());
		assertPosition(inv, 1, 13);
	}

}
