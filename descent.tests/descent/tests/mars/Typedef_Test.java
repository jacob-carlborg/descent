package descent.tests.mars;

import descent.core.dom.ICompilationUnit;
import descent.core.dom.IElement;
import descent.core.dom.IInitializer;
import descent.core.dom.IType;
import descent.core.dom.ITypedefDeclaration;
import descent.internal.core.dom.ParserFacade;

public class Typedef_Test extends Parser_Test {
	
	public void testOne() {
		String s = " typedef int Bla;";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		ITypedefDeclaration t = (ITypedefDeclaration) declDefs[0];
		assertEquals(IType.TYPEDEF_DECLARATION, t.getNodeType0());
		assertPosition(t, 1, 16);
		
		assertEquals("int", t.getType().toString());
		assertEquals("Bla", t.getName().toString());
		assertPosition(t.getName(), 13, 3);
		
		assertVisitor(t, 3);
	}
	
	public void testInitializer() {
		String s = " typedef int Bla = 1;";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		ITypedefDeclaration t = (ITypedefDeclaration) declDefs[0];
		assertEquals(IType.TYPEDEF_DECLARATION, t.getNodeType0());
		assertPosition(t, 1, 20);
		
		assertEquals("int", t.getType().toString());
		assertEquals("Bla", t.getName().toString());
		assertPosition(t.getName(), 13, 3);
		
		assertEquals(IInitializer.EXPRESSION_INITIALIZER, t.getInitializer().getNodeType0());
		assertPosition(t.getInitializer(), 19, 1);
		
		assertVisitor(t, 5);
	}
	
	public void testMany() {
		String s = " typedef int Bla, Ble;";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(2, declDefs.length);
		
		ITypedefDeclaration t;
		t = (ITypedefDeclaration) declDefs[0];
		assertPosition(t, 1, 15);
		
		assertEquals("int", t.getType().toString());
		assertEquals("Bla", t.getName().toString());
		assertPosition(t.getName(), 13, 3);
		
		assertVisitor(t, 3);
		
		t = (ITypedefDeclaration) declDefs[1];
		assertPosition(t, 18, 4);
		
		assertEquals("int", t.getType().toString());
		assertEquals("Ble", t.getName().toString());
		assertPosition(t.getName(), 18, 3);
		
		assertVisitor(t, 3);
	}

}
