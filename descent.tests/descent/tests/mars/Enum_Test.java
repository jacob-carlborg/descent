package descent.tests.mars;

import descent.core.dom.IComment;
import descent.core.dom.ICompilationUnit;
import descent.core.dom.IElement;
import descent.core.dom.IEnumDeclaration;
import descent.core.dom.IEnumMember;
import descent.internal.core.dom.NumberLiteral;
import descent.internal.core.dom.ParserFacade;

public class Enum_Test extends Parser_Test {
	
	public void testEnumClosed() {
		String s = " enum En;";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		assertEquals(0, unit.getProblems().length);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IEnumDeclaration e = (IEnumDeclaration) declDefs[0];
		assertEquals(IElement.ENUM_DECLARATION, e.getNodeType0());
		assertPosition(e, 1, 8);
		assertPosition(e.getName(), 6, 2);
		assertEquals("En", e.getName().getIdentifier());
		assertEquals(0, e.enumMembers().size());
		
		assertVisitor(e, 2);
	}
	
	public void testEnumSimple() {
		String s = " enum En { x, y = 1, z }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		assertEquals(0, unit.getProblems().length);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IEnumDeclaration e = (IEnumDeclaration) declDefs[0];
		assertPosition(e, 1, 23);
		assertPosition(e.getName(), 6, 2);
		assertEquals("En", e.getName().getIdentifier());
		assertEquals(3, e.enumMembers().size());
		
		IEnumMember em;
		
		em = e.enumMembers().get(0);
		assertEquals(IElement.ENUM_MEMBER, em.getNodeType0());
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
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		assertEquals(0, unit.getProblems().length);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IEnumDeclaration e = (IEnumDeclaration) declDefs[0];
		assertPosition(e, 1, 10);
		assertNull(e.getName());
	}
	
	public void testEnumWithBaseType() {
		String s = " enum En : int;";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		assertEquals(0, unit.getProblems().length);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IEnumDeclaration e = (IEnumDeclaration) declDefs[0];
		assertPosition(e, 1, 14);
		assertPosition(e.getName(), 6, 2);
		assertEquals("En", e.getName().getIdentifier());
		assertEquals("int", e.getBaseType().toString());
		assertEquals(0, e.enumMembers().size());
		
		assertVisitor(e, 3);
	}
	
	public void testEnumNamelessWithBaseType() {
		String s = " enum : int { x }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		assertEquals(0, unit.getProblems().length);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IEnumDeclaration e = (IEnumDeclaration) declDefs[0];
		assertPosition(e, 1, 16);
		assertNull(e.getName());
	}
	
	public void testEnumWithComments() {
		String s = " /** hola */ enum En;";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		assertEquals(0, unit.getProblems().length);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IEnumDeclaration e = (IEnumDeclaration) declDefs[0];
		assertPosition(e, 13, 8);
		
		IComment[] comments = e.getComments();
		assertEquals(1, comments.length);
		assertEquals("/** hola */", comments[0].getComment());
	}

}
