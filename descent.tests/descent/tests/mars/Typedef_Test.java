package descent.tests.mars;

import descent.core.dom.ICompilationUnit;
import descent.core.dom.IElement;
import descent.core.dom.IType;
import descent.core.dom.ITypedefDeclaration;
import descent.internal.core.dom.ParserFacade;
import descent.internal.core.dom.TypedefDeclarationFragment;

public class Typedef_Test extends Parser_Test {
	
	public void testOne() {
		String s = " typedef int Bla;";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		assertEquals(0, unit.getProblems().length);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		ITypedefDeclaration t = (ITypedefDeclaration) declDefs[0];
		assertEquals(IType.TYPEDEF_DECLARATION, t.getNodeType0());
		assertPosition(t, 1, 16);
		
		assertEquals(1, t.fragments().size());
		
		TypedefDeclarationFragment fragment = t.fragments().get(0);
		
		assertEquals("Bla", fragment.getName().getFullyQualifiedName());
		assertNull(fragment.getInitializer());
		assertPosition(fragment.getName(), 13, 3);
	}
	
	public void testInitializer() {
		String s = " typedef int Bla = 1;";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		assertEquals(0, unit.getProblems().length);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		ITypedefDeclaration t = (ITypedefDeclaration) declDefs[0];
		assertEquals(IType.TYPEDEF_DECLARATION, t.getNodeType0());
		assertPosition(t, 1, 20);
		
		TypedefDeclarationFragment fragment = t.fragments().get(0);
		
		assertEquals("Bla", fragment.getName().getFullyQualifiedName());
		assertPosition(fragment.getInitializer(), 19, 1);
		assertPosition(fragment.getName(), 13, 3);
	}
	
	public void testMany() {
		String s = " typedef int Bla, Ble;";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		assertEquals(0, unit.getProblems().length);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		ITypedefDeclaration t;
		t = (ITypedefDeclaration) declDefs[0];
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
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		assertEquals(0, unit.getProblems().length);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(2, declDefs.length);
		
		ITypedefDeclaration typedef = (ITypedefDeclaration) declDefs[0];
		assertEquals(IElement.TYPEDEF_DECLARATION, typedef.getNodeType0());
		
		assertEquals("int", typedef.getType().toString());
		assertPosition(typedef.getType(), 9, 3);
		assertPosition(typedef, 1, 26);
		
		assertEquals(3, typedef.fragments().size());
		assertEquals("Bla", typedef.fragments().get(0).getName().getIdentifier());
		assertPosition(typedef.fragments().get(0), 13, 3);
		
		assertEquals("Ble", typedef.fragments().get(1).getName().getIdentifier());
		assertPosition(typedef.fragments().get(1), 18, 3);
		
		assertEquals("Bli", typedef.fragments().get(2).getName().getIdentifier());
		assertPosition(typedef.fragments().get(2), 23, 3);
	}

}
