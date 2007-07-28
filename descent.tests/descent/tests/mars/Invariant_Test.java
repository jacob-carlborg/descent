package descent.tests.mars;

import descent.core.dom.ASTNode;
import descent.core.dom.InvariantDeclaration;

public class Invariant_Test extends Parser_Test {
	
	public void test() {
		String s = " invariant { }";
		InvariantDeclaration inv = (InvariantDeclaration) getSingleDeclarationNoProblems(s);
		assertEquals(ASTNode.INVARIANT_DECLARATION, inv.getNodeType());
		assertPosition(inv, 1, s.length() - 1);
	}
	
	public void test2() {
		String s = " invariant() { }";
		InvariantDeclaration inv = (InvariantDeclaration) getSingleDeclarationNoProblems(s);
		assertEquals(ASTNode.INVARIANT_DECLARATION, inv.getNodeType());
		assertPosition(inv, 1, s.length() - 1);
	}

}
