package descent.tests.mars;

import descent.core.dom.IAliasDeclaration;
import descent.core.dom.ICompilationUnit;
import descent.core.dom.IElement;
import descent.internal.core.dom.ParserFacade;

public class Alias_Test extends Parser_Test {
	
	public void testOneFragment() {
		String s = " alias int Bla;";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IAliasDeclaration alias = (IAliasDeclaration) declDefs[0];
		assertEquals(IElement.ALIAS_DECLARATION, alias.getNodeType0());
		
		assertEquals("int", alias.getType().toString());
		assertPosition(alias.getType(), 7, 3);
		assertPosition(alias, 1, s.length() - 1);
		
		assertEquals(1, alias.fragments().size());
		assertEquals("Bla", alias.fragments().get(0).getName().getIdentifier());
		assertPosition(alias.fragments().get(0), 11, 3);		
	}
	
	public void testTwoFragments() {
		String s = " alias int Bla, Ble;";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IAliasDeclaration alias = (IAliasDeclaration) declDefs[0];
		assertEquals(IElement.ALIAS_DECLARATION, alias.getNodeType0());
		
		assertEquals("int", alias.getType().toString());
		assertPosition(alias.getType(), 7, 3);
		assertPosition(alias, 1, s.length() - 1);
		
		assertEquals(2, alias.fragments().size());
		assertEquals("Bla", alias.fragments().get(0).getName().getIdentifier());
		assertPosition(alias.fragments().get(0), 11, 3);
		
		assertEquals("Ble", alias.fragments().get(1).getName().getIdentifier());
		assertPosition(alias.fragments().get(1), 16, 3);
	}
	
	public void testThreeFragments() {
		String s = " alias int Bla, Ble, Bli;";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IAliasDeclaration alias = (IAliasDeclaration) declDefs[0];
		assertEquals(IElement.ALIAS_DECLARATION, alias.getNodeType0());
		
		assertEquals("int", alias.getType().toString());
		assertPosition(alias.getType(), 7, 3);
		assertPosition(alias, 1, s.length() - 1);
		
		assertEquals(3, alias.fragments().size());
		assertEquals("Bla", alias.fragments().get(0).getName().getIdentifier());
		assertPosition(alias.fragments().get(0), 11, 3);
		
		assertEquals("Ble", alias.fragments().get(1).getName().getIdentifier());
		assertPosition(alias.fragments().get(1), 16, 3);
		
		assertEquals("Bli", alias.fragments().get(2).getName().getIdentifier());
		assertPosition(alias.fragments().get(2), 21, 3);
	}
	
	public void testThreeFragmentsWithNextDeclaration() {
		String s = " alias int Bla, Ble, Bli; alias int Blo;";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(2, declDefs.length);
		
		IAliasDeclaration alias = (IAliasDeclaration) declDefs[0];
		assertEquals(IElement.ALIAS_DECLARATION, alias.getNodeType0());
		
		assertEquals("int", alias.getType().toString());
		assertPosition(alias.getType(), 7, 3);
		assertPosition(alias, 1, 24);
		
		assertEquals(3, alias.fragments().size());
		assertEquals("Bla", alias.fragments().get(0).getName().getIdentifier());
		assertPosition(alias.fragments().get(0), 11, 3);
		
		assertEquals("Ble", alias.fragments().get(1).getName().getIdentifier());
		assertPosition(alias.fragments().get(1), 16, 3);
		
		assertEquals("Bli", alias.fragments().get(2).getName().getIdentifier());
		assertPosition(alias.fragments().get(2), 21, 3);
	}

}
