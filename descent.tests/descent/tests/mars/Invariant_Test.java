package descent.tests.mars;

import descent.core.dom.AST;
import descent.core.dom.ASTNode;
import descent.core.dom.InvariantDeclaration;

public class Invariant_Test extends Parser_Test {
	
	public void test() {
		String s = " invariant { }";
		InvariantDeclaration inv = (InvariantDeclaration) getSingleDeclarationNoProblems(s);
		assertEquals(ASTNode.INVARIANT_DECLARATION, inv.getNodeType());
		assertPosition(inv, 1, s.length() - 1);
	}
	
	public void testParen1() {
		String s = " invariant() { }";
		InvariantDeclaration inv = (InvariantDeclaration) getSingleDeclarationNoProblems(s, AST.D1);
		assertEquals(ASTNode.INVARIANT_DECLARATION, inv.getNodeType());
		assertPosition(inv, 1, s.length() - 1);
	}
	
	public void testParen2() {
		String s = " invariant() { }";
		InvariantDeclaration inv = (InvariantDeclaration) getSingleDeclarationNoProblems(s, AST.D2);
		assertEquals(ASTNode.INVARIANT_DECLARATION, inv.getNodeType());
		assertPosition(inv, 1, s.length() - 1);
	}

}
