package descent.tests.mars;

import descent.core.dom.ASTNode;
import descent.core.dom.TypedefDeclaration;
import descent.core.dom.TypedefDeclarationFragment;

public class Typedef_Test extends Parser_Test {
	
	public void testOne() {
		String s = " typedef int Bla;";
		
		TypedefDeclaration t = (TypedefDeclaration) getSingleDeclarationNoProblems(s);
		assertEquals(ASTNode.TYPEDEF_DECLARATION, t.getNodeType0());
		assertPosition(t, 1, 16);
		
		assertEquals(1, t.fragments().size());
		
		TypedefDeclarationFragment fragment = t.fragments().get(0);
		
		assertEquals("Bla", fragment.getName().getFullyQualifiedName());
		assertNull(fragment.getInitializer());
		assertPosition(fragment.getName(), 13, 3);
	}
	
	public void testInitializer() {
		String s = " typedef int Bla = 1;";
		TypedefDeclaration t = (TypedefDeclaration) getSingleDeclarationNoProblems(s);
		assertEquals(ASTNode.TYPEDEF_DECLARATION, t.getNodeType0());
		assertPosition(t, 1, 20);
		
		TypedefDeclarationFragment fragment = t.fragments().get(0);
		
		assertEquals("Bla", fragment.getName().getFullyQualifiedName());
		assertPosition(fragment.getInitializer(), 19, 1);
		assertPosition(fragment.getName(), 13, 3);
	}
	
	public void testMany() {
		String s = " typedef int Bla, Ble;";
		TypedefDeclaration t = (TypedefDeclaration) getSingleDeclarationNoProblems(s);
		assertPosition(t, 1, s.length() - 1);
		
		TypedefDeclarationFragment fragment;
		
		fragment = t.fragments().get(0);
		assertEquals("Bla", fragment.getName().getFullyQualifiedName());
		assertNull(fragment.getInitializer());
		assertPosition(fragment.getName(), 13, 3);
		
		fragment = t.fragments().get(1);
		assertEquals("Ble", fragment.getName().getFullyQualifiedName());
		assertNull(fragment.getInitializer());
		assertPosition(fragment.getName(), 18, 3);
	}
	
	public void testThreeFragmentsWithNextDeclaration() {
		String s = " typedef int Bla, Ble, Bli; typedef int Blo;";
		TypedefDeclaration t = (TypedefDeclaration) getDeclarationsNoProblems(s).get(0);
		assertEquals(ASTNode.TYPEDEF_DECLARATION, t.getNodeType0());
		
		assertEquals("int", t.getType().toString());
		assertPosition(t.getType(), 9, 3);
		assertPosition(t, 1, 26);
		
		assertEquals(3, t.fragments().size());
		assertEquals("Bla", t.fragments().get(0).getName().getIdentifier());
		assertPosition(t.fragments().get(0), 13, 3);
		
		assertEquals("Ble", t.fragments().get(1).getName().getIdentifier());
		assertPosition(t.fragments().get(1), 18, 3);
		
		assertEquals("Bli", t.fragments().get(2).getName().getIdentifier());
		assertPosition(t.fragments().get(2), 23, 3);
	}

}
