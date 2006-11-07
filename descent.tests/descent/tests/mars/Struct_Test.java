package descent.tests.mars;

import descent.core.dom.IAggregateDeclaration;
import descent.core.dom.ICompilationUnit;
import descent.core.dom.IDElement;
import descent.core.dom.IName;
import descent.internal.core.dom.ParserFacade;

public class Struct_Test extends Parser_Test {
	
	public void testEmpty() {
		String s = " struct Clazz { }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IDElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IAggregateDeclaration c = (IAggregateDeclaration) declDefs[0];
		assertEquals(IDElement.AGGREGATE_DECLARATION, c.getElementType());
		assertEquals(IAggregateDeclaration.STRUCT_DECLARATION, c.getAggregateDeclarationType());
		assertPosition(c, 1, 16);
		
		IName name = c.getName();
		assertEquals(IDElement.NAME, name.getElementType());
		assertEquals("Clazz", name.toString());
		assertPosition(name, 8, 5);
		
		assertEquals(0, c.getBaseClasses().length);
	}
	
	public void testSemicolon() {
		String s = " struct Clazz;";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IDElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IAggregateDeclaration c = (IAggregateDeclaration) declDefs[0];
		assertPosition(c, 1, 13);
	}
	
	public void testWithComments() {
		String s = " /** hola */ struct Clazz;";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IDElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IAggregateDeclaration c = (IAggregateDeclaration) declDefs[0];
		assertPosition(c, 1, 25);
	}

}
