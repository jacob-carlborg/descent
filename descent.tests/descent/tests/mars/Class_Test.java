package descent.tests.mars;

import descent.core.dom.IAggregateDeclaration;
import descent.core.dom.IBaseClass;
import descent.core.dom.ICompilationUnit;
import descent.core.dom.IElement;
import descent.core.dom.IModifier;
import descent.core.dom.ISimpleName;
import descent.internal.core.dom.ParserFacade;

public class Class_Test extends Parser_Test {
	
	public void testEmpty() {
		String s = " class Clazz { }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IAggregateDeclaration c = (IAggregateDeclaration) declDefs[0];
		assertEquals(IElement.AGGREGATE_DECLARATION, c.getElementType());
		assertEquals(IAggregateDeclaration.CLASS_DECLARATION, c.getAggregateDeclarationType());
		assertPosition(c, 1, 15);
		
		ISimpleName name = c.getName();
		assertEquals(IElement.SIMPLE_NAME, name.getElementType());
		assertEquals("Clazz", name.toString());
		assertPosition(name, 7, 5);
		
		assertEquals(0, c.getBaseClasses().length);
		
		assertFalse(c.isTemplate());
		
		assertVisitor(c, 2);
	}
	
	public void testSemicolon() {
		String s = " class Clazz;";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IAggregateDeclaration c = (IAggregateDeclaration) declDefs[0];
		assertPosition(c, 1, 12);
	}
	
	public void testBaseClasses() {
		String s = " class Clazz : None, private Private, package Package, protected Protected, public Public { }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		IAggregateDeclaration c = (IAggregateDeclaration) declDefs[0];
		IBaseClass[] bs = c.getBaseClasses();
		assertEquals(5, bs.length);
		
		assertEquals(IElement.BASE_CLASS, bs[0].getElementType());
		assertEquals(IModifier.PUBLIC, bs[0].getModifiers());
		assertEquals(IModifier.PRIVATE, bs[1].getModifiers());
		assertEquals(IModifier.PACKAGE, bs[2].getModifiers());
		assertEquals(IModifier.PROTECTED, bs[3].getModifiers());
		assertEquals(IModifier.PUBLIC, bs[4].getModifiers());
		
		assertEquals("None", bs[0].getType().toString());
		assertPosition(bs[0].getType(), 15, 4);
		
		assertVisitor(c, 12);
	}
	
	public void testWithComments() {
		String s = " /** hola */ class Clazz;";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IAggregateDeclaration c = (IAggregateDeclaration) declDefs[0];
		assertPosition(c, 1, 24);
		
		assertEquals("hola", c.getComments());
	}
	
	public void testWithMembers() {
		String s = " class Clazz { int x; }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IAggregateDeclaration c = (IAggregateDeclaration) declDefs[0];
		
		assertEquals(1, c.getDeclarationDefinitions().length);
		
		assertVisitor(c, 5);
	}
	
	public void testClassAlias() {
		String s = " alias class Clazz { int x; }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IAggregateDeclaration c = (IAggregateDeclaration) declDefs[0];
		
		assertEquals(1, c.getDeclarationDefinitions().length);
	}

}
