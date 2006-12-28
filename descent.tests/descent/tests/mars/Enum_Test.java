package descent.tests.mars;

import java.util.List;

import descent.core.dom.ASTNode;
import descent.core.dom.Comment;
import descent.core.dom.EnumDeclaration;
import descent.core.dom.EnumMember;
import descent.core.dom.NumberLiteral;

public class Enum_Test extends Parser_Test {
	
	public void testEnumClosed() {
		String s = " enum En;";
		EnumDeclaration e = (EnumDeclaration) getSingleDeclarationNoProblems(s);
		assertEquals(ASTNode.ENUM_DECLARATION, e.getNodeType());
		assertPosition(e, 1, 8);
		assertPosition(e.getName(), 6, 2);
		assertEquals("En", e.getName().getIdentifier());
		assertEquals(0, e.enumMembers().size());
	}
	
	public void testEnumSimple() {
		String s = " enum En { x, y = 1, z }";
		EnumDeclaration e = (EnumDeclaration) getSingleDeclarationNoProblems(s);
		assertPosition(e, 1, 23);
		assertPosition(e.getName(), 6, 2);
		assertEquals("En", e.getName().getIdentifier());
		assertEquals(3, e.enumMembers().size());
		
		EnumMember em;
		
		em = e.enumMembers().get(0);
		assertEquals(ASTNode.ENUM_MEMBER, em.getNodeType());
		assertEquals("x", em.getName().getIdentifier());
		assertNull(em.getValue());
		assertPosition(em, 11, 1);
		
		em = e.enumMembers().get(1);
		assertEquals("y", em.getName().getIdentifier());
		assertEquals("1", ((NumberLiteral) em.getValue()).getToken());
		assertPosition(em, 14, 5);
		
		em = e.enumMembers().get(2);
		assertEquals("z", em.getName().getIdentifier());
		assertNull(em.getValue());
		assertPosition(em, 21, 1);
	}
	
	public void testEnumNameless() {
		String s = " enum { x }";
		EnumDeclaration e = (EnumDeclaration) getSingleDeclarationNoProblems(s);
		assertPosition(e, 1, 10);
		assertNull(e.getName());
	}
	
	public void testEnumWithBaseType() {
		String s = " enum En : int;";
		EnumDeclaration e = (EnumDeclaration) getSingleDeclarationNoProblems(s);
		assertPosition(e, 1, 14);
		assertPosition(e.getName(), 6, 2);
		assertEquals("En", e.getName().getIdentifier());
		assertEquals("int", e.getBaseType().toString());
		assertEquals(0, e.enumMembers().size());
	}
	
	public void testEnumNamelessWithBaseType() {
		String s = " enum : int { x }";
		EnumDeclaration e = (EnumDeclaration) getSingleDeclarationNoProblems(s);
		assertPosition(e, 1, 16);
		assertNull(e.getName());
	}
	
	public void testEnumWithComments() {
		String s = " /** hola */ enum En;";
		EnumDeclaration e = (EnumDeclaration) getSingleDeclarationNoProblems(s);
		assertPosition(e, 13, 8);
		
		List<Comment> comments = e.dDocs();
		assertEquals(1, comments.size());
		assertPosition(comments.get(0), 1, 11);
	}

}
