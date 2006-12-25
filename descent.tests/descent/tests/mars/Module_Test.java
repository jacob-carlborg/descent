package descent.tests.mars;

import java.util.List;

import descent.core.dom.ASTNode;
import descent.core.dom.Comment;
import descent.core.dom.CompilationUnit;
import descent.core.dom.ModuleDeclaration;
import descent.core.dom.QualifiedName;
import descent.core.dom.SimpleName;

public class Module_Test extends Parser_Test {
	
	public void testModuleDeclarationSingle() {
		String s = " module a; ";
		ModuleDeclaration md = getModuleDeclaration(s);
		
		assertNotNull(md);
		assertPosition(md, 1, 9);
		assertEquals(ASTNode.MODULE_DECLARATION, md.getNodeType());
		
		SimpleName qName = (SimpleName) md.getName();
		assertEquals("a", qName.getIdentifier());
		assertPosition(qName, 8, 1);
	}
	
	public void testModuleDeclarationMany() {
		String s = " module hola.chau.uno; ";
		ModuleDeclaration md = getModuleDeclaration(s);
		
		assertNotNull(md);
		assertPosition(md, 1, 21);
		
		QualifiedName qName = (QualifiedName) md.getName();
		assertEquals("hola.chau.uno", qName.getFullyQualifiedName());
		assertPosition(qName, 8, 13);
	}
	
	public void testModuleDeclarationWithComments() {
		String s = " /** hola */ module pepe; ";
		ModuleDeclaration md = getModuleDeclaration(s);
		
		List<Comment> comments = md.getComments();
		assertEquals(1, comments.size());
		
		assertEquals("/** hola */", comments.get(0).getComment());
		
		assertPosition(md, 13, 12);
	}
	
	public void testModuleDeclarationWithMultipleComments() {
		String s = " /** hola */ /** chau */ module pepe; ";
		ModuleDeclaration md = getModuleDeclaration(s);
		
		List<Comment> comments = md.getComments();
		assertEquals(2, comments.size());
		
		assertPosition(md, 25, 12);
	}
	
	// TODO I need this kind of declarations
	public void testSkipFirstLine() {
		String s = "#! something, I don't mind\n module a; ";
		CompilationUnit unit = getCompilationUnit(s);
		assertEquals(0, unit.getProblems().length);
		ModuleDeclaration md = unit.getModuleDeclaration();
		
		assertNotNull(md);
		
		s = "#! something, I don't mind\r module a; ";
		unit = getCompilationUnit(s);
		md = unit.getModuleDeclaration();
		
		assertNotNull(md);
		
		s = "#! something, I don't mind\r\n module a; ";
		unit = getCompilationUnit(s);
		md = unit.getModuleDeclaration();
		
		assertNotNull(md);
	}

}
