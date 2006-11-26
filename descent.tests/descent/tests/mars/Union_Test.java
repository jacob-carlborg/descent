package descent.tests.mars;

import descent.core.dom.IAggregateDeclaration;
import descent.core.dom.ICompilationUnit;
import descent.core.dom.IElement;
import descent.core.dom.IName;
import descent.internal.core.dom.ParserFacade;

public class Union_Test extends Parser_Test {
	
	public void testEmpty() {
		String s = " union Clazz { }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IAggregateDeclaration c = (IAggregateDeclaration) declDefs[0];
		assertEquals(IElement.AGGREGATE_DECLARATION, c.getElementType());
		assertEquals(IAggregateDeclaration.UNION_DECLARATION, c.getAggregateDeclarationType());
		assertPosition(c, 1, 15);
		
		IName name = c.getName();
		assertEquals(IElement.NAME, name.getElementType());
		assertEquals("Clazz", name.toString());
		assertPosition(name, 7, 5);
		
		assertEquals(0, c.getBaseClasses().length);
	}
	
	public void testSemicolon() {
		String s = " union Clazz;";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IAggregateDeclaration c = (IAggregateDeclaration) declDefs[0];
		assertPosition(c, 1, 12);
	}
	
	public void testWithComments() {
		String s = " /** hola */ union Clazz;";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IAggregateDeclaration c = (IAggregateDeclaration) declDefs[0];
		assertPosition(c, 1, 24);
	}

}
