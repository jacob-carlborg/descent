package descent.tests.mars;

import descent.core.dom.IAggregateDeclaration;
import descent.core.dom.IBaseClass;
import descent.core.dom.IComment;
import descent.core.dom.ICompilationUnit;
import descent.core.dom.IElement;
import descent.core.dom.IModifier;
import descent.core.dom.ISimpleName;
import descent.internal.core.dom.AggregateDeclaration;
import descent.internal.core.dom.ParserFacade;

public class Interface_Test extends Parser_Test {
	
	public void testEmpty() {
		String s = " interface Clazz { }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IAggregateDeclaration c = (IAggregateDeclaration) declDefs[0];
		assertEquals(IElement.AGGREGATE_DECLARATION, c.getNodeType0());
		assertEquals(AggregateDeclaration.Kind.INTERFACE, c.getKind());
		assertPosition(c, 1, 19);
		
		ISimpleName name = c.getName();
		assertEquals(IElement.SIMPLE_NAME, name.getNodeType0());
		assertEquals("Clazz", name.getIdentifier());
		assertPosition(name, 11, 5);
		
		assertEquals(0, c.baseClasses().size());
	}
	
	public void testSemicolon() {
		String s = " interface Clazz;";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IAggregateDeclaration c = (IAggregateDeclaration) declDefs[0];
		assertPosition(c, 1, 16);
	}
	
	public void testBaseClasses() {
		String s = " interface Clazz : None, private Private, package Package, protected Protected, public Public { }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		IAggregateDeclaration c = (IAggregateDeclaration) declDefs[0];
		IBaseClass[] bs = c.baseClasses().toArray(new IBaseClass[c.baseClasses().size()]);
		assertEquals(5, bs.length);
		
		assertEquals(IElement.BASE_CLASS, bs[0].getNodeType0());
		assertEquals(IModifier.PUBLIC, bs[0].getModifierFlags());
		assertEquals(IModifier.PRIVATE, bs[1].getModifierFlags());
		assertEquals(IModifier.PACKAGE, bs[2].getModifierFlags());
		assertEquals(IModifier.PROTECTED, bs[3].getModifierFlags());
		assertEquals(IModifier.PUBLIC, bs[4].getModifierFlags());
	}
	
	public void testWithComments() {
		String s = " /** hola */ interface Clazz;";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IAggregateDeclaration c = (IAggregateDeclaration) declDefs[0];
		assertPosition(c, 13, 16);
		
		IComment[] comments = c.getComments();
		assertEquals(1, comments.length);
		assertEquals("/** hola */", comments[0].getComment());
	}

}
