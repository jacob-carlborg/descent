package descent.tests.mars;

import descent.core.dom.IAggregateDeclaration;
import descent.core.dom.IComment;
import descent.core.dom.ICompilationUnit;
import descent.core.dom.IElement;
import descent.core.dom.ISimpleName;
import descent.internal.core.dom.AggregateDeclaration;
import descent.internal.core.dom.ParserFacade;

public class Struct_Test extends Parser_Test {
	
	public void testEmpty() {
		String s = " struct Clazz { }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IAggregateDeclaration c = (IAggregateDeclaration) declDefs[0];
		assertEquals(IElement.AGGREGATE_DECLARATION, c.getNodeType0());
		assertEquals(AggregateDeclaration.Kind.STRUCT, c.getKind());
		assertPosition(c, 1, 16);
		
		ISimpleName name = c.getName();
		assertEquals(IElement.SIMPLE_NAME, name.getNodeType0());
		assertEquals("Clazz", name.toString());
		assertPosition(name, 8, 5);
		
		assertEquals(0, c.baseClasses().size());
	}
	
	public void testSemicolon() {
		String s = " struct Clazz;";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IAggregateDeclaration c = (IAggregateDeclaration) declDefs[0];
		assertPosition(c, 1, 13);
	}
	
	public void testWithComments() {
		String s = " /** hola */ struct Clazz;";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IAggregateDeclaration c = (IAggregateDeclaration) declDefs[0];
		assertPosition(c, 13, 13);
		
		IComment[] comments = c.getComments();
		assertEquals(1, comments.length);
		assertEquals("/** hola */", comments[0].getComment());
	}

}
