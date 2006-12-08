package descent.tests.mars;

import descent.core.dom.IComment;
import descent.core.dom.ICompilationUnit;
import descent.core.dom.IElement;
import descent.core.dom.IModuleDeclaration;
import descent.internal.core.dom.ParserFacade;
import descent.internal.core.dom.QualifiedName;
import descent.internal.core.dom.SimpleName;

public class Module_Test extends Parser_Test {
	
	public void testModuleDeclarationSingle() {
		String s = " module a; ";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IModuleDeclaration md = unit.getModuleDeclaration();
		
		assertNotNull(md);
		assertPosition(md, 1, 9);
		assertEquals(IElement.MODULE_DECLARATION, md.getNodeType0());
		
		SimpleName qName = (SimpleName) md.getName();
		assertEquals("a", qName.toString());
		assertPosition(qName, 8, 1);
	}
	
	public void testModuleDeclarationMany() {
		String s = " module hola.chau.uno; ";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IModuleDeclaration md = unit.getModuleDeclaration();
		
		assertNotNull(md);
		assertPosition(md, 1, 21);
		
		QualifiedName qName = (QualifiedName) md.getName();
		assertEquals("hola.chau.uno", qName.getFullyQualifiedName());
		assertPosition(qName, 8, 13);
	}
	
	public void testModuleDeclarationWithComments() {
		String s = " /** hola */ module pepe; ";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IModuleDeclaration md = unit.getModuleDeclaration();
		
		IComment[] comments = md.getComments();
		assertEquals(1, comments.length);
		
		assertEquals("/** hola */", comments[0].getComment());
		
		assertPosition(md, 13, 12);
	}
	
	public void testModuleDeclarationWithMultipleComments() {
		String s = " /** hola */ /** chau */ module pepe; ";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IModuleDeclaration md = unit.getModuleDeclaration();
		
		IComment[] comments = md.getComments();
		assertEquals(2, comments.length);
		
		assertPosition(md, 25, 12);
	}
	
	public void testSkipFirstLine() {
		String s = "#! something, I don't mind\n module a; ";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IModuleDeclaration md = unit.getModuleDeclaration();
		
		assertNotNull(md);
		
		s = "#! something, I don't mind\r module a; ";
		unit = new ParserFacade().parseCompilationUnit(s);
		md = unit.getModuleDeclaration();
		
		assertNotNull(md);
		
		s = "#! something, I don't mind\r\n module a; ";
		unit = new ParserFacade().parseCompilationUnit(s);
		md = unit.getModuleDeclaration();
		
		assertNotNull(md);
	}

}
