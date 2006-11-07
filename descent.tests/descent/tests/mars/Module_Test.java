package descent.tests.mars;

import descent.core.dom.ICompilationUnit;
import descent.core.dom.IDElement;
import descent.core.dom.IModuleDeclaration;
import descent.core.dom.IQualifiedName;
import descent.internal.core.dom.ParserFacade;

public class Module_Test extends Parser_Test {
	
	public void testModuleDeclarationSingle() {
		String s = " module a; ";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IModuleDeclaration md = unit.getModuleDeclaration();
		
		assertNotNull(md);
		assertPosition(md, 1, 9);
		assertEquals(IDElement.MODULE_DECLARATION, md.getElementType());
		
		IQualifiedName qName = md.getQualifiedName();
		assertEquals("a", qName.toString());
		assertPosition(qName, 8, 1);
		
		assertVisitor(md, 2);
	}
	
	public void testModuleDeclarationMany() {
		String s = " module hola.chau.uno; ";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IModuleDeclaration md = unit.getModuleDeclaration();
		
		assertNotNull(md);
		assertPosition(md, 1, 21);
		
		IQualifiedName qName = md.getQualifiedName();
		assertEquals("hola.chau.uno", qName.toString());
		assertPosition(qName, 8, 13);
		
		assertVisitor(md, 2);
	}
	
	public void testModuleDeclarationWithComments() {
		String s = " /** hola */ module pepe; ";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IModuleDeclaration md = unit.getModuleDeclaration();
		assertEquals("hola", md.getComments());
		
		assertPosition(md, 1, 24);
	}
	
	public void testModuleDeclarationWithMultipleComments1() {
		String s = " /** hola */ /** chau */ module pepe; ";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IModuleDeclaration md = unit.getModuleDeclaration();
		assertEquals("hola\n\nchau", md.getComments());
		
		assertPosition(md, 1, 36);
	}

}
