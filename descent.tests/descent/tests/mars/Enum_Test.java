package descent.tests.mars;

import java.math.BigInteger;

import descent.core.dom.IComment;
import descent.core.dom.ICompilationUnit;
import descent.core.dom.IElement;
import descent.core.dom.IEnumDeclaration;
import descent.core.dom.IEnumMember;
import descent.core.dom.IIntegerExpression;
import descent.internal.core.dom.ParserFacade;

public class Enum_Test extends Parser_Test {
	
	public void testEnumClosed() {
		String s = " enum En;";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IEnumDeclaration e = (IEnumDeclaration) declDefs[0];
		assertEquals(IElement.ENUM_DECLARATION, e.getNodeType0());
		assertPosition(e, 1, 8);
		assertPosition(e.getName(), 6, 2);
		assertEquals("En", e.getName().toString());
		assertEquals(0, e.getMembers().length);
		
		assertVisitor(e, 2);
	}
	
	public void testEnumSimple() {
		String s = " enum En { x, y = 1, z }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IEnumDeclaration e = (IEnumDeclaration) declDefs[0];
		assertPosition(e, 1, 23);
		assertPosition(e.getName(), 6, 2);
		assertEquals("En", e.getName().toString());
		assertEquals(3, e.getMembers().length);
		
		IEnumMember em;
		
		em = e.getMembers()[0];
		assertEquals(IElement.ENUM_MEMBER, em.getNodeType0());
		assertEquals("x", em.getName().toString());
		assertNull(em.getValue());
		assertPosition(em, 11, 1);
		
		em = e.getMembers()[1];
		assertEquals("y", em.getName().toString());
		assertEquals(BigInteger.ONE, ((IIntegerExpression) em.getValue()).getValue());
		assertPosition(em, 14, 5);
		
		em = e.getMembers()[2];
		assertEquals("z", em.getName().toString());
		assertNull(em.getValue());
		assertPosition(em, 21, 1);
		
		assertVisitor(e, 9);
	}
	
	public void testEnumNameless() {
		String s = " enum { x }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IEnumDeclaration e = (IEnumDeclaration) declDefs[0];
		assertPosition(e, 1, 10);
		assertNull(e.getName());
		
		assertVisitor(e, 3);
	}
	
	public void testEnumWithBaseType() {
		String s = " enum En : int;";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IEnumDeclaration e = (IEnumDeclaration) declDefs[0];
		assertPosition(e, 1, 14);
		assertPosition(e.getName(), 6, 2);
		assertEquals("En", e.getName().toString());
		assertEquals("int", e.getBaseType().toString());
		assertEquals(0, e.getMembers().length);
		
		assertVisitor(e, 3);
	}
	
	public void testEnumNamelessWithBaseType() {
		String s = " enum : int { x }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IEnumDeclaration e = (IEnumDeclaration) declDefs[0];
		assertPosition(e, 1, 16);
		assertNull(e.getName());
	}
	
	public void testEnumWithComments() {
		String s = " /** hola */ enum En;";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IEnumDeclaration e = (IEnumDeclaration) declDefs[0];
		assertPosition(e, 13, 8);
		
		IComment[] comments = e.getComments();
		assertEquals(1, comments.length);
		assertEquals("/** hola */", comments[0].getComment());
	}

}
