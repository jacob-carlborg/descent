package descent.tests.mars;

import descent.core.dom.ASTNode;
import descent.core.dom.CompilationUnit;
import descent.core.dom.Declaration;
import descent.core.dom.ModuleDeclaration;
import descent.core.dom.QualifiedName;
import descent.core.dom.SimpleName;

public class CompilationUnit_Test extends Parser_Test {
	
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
	
	public void testExtendedLengthWithDeclaration() {
		String s = " /** hola */ int x = 2;";
		CompilationUnit unit = getCompilationUnit(s);
		assertEquals(0, unit.getProblems().length);
		assertEquals(1, unit.declarations().size());
		
		Declaration decl = unit.declarations().get(0);
		assertExtendedPosition(decl, 1, s.length() - 1, unit);
	}
	
	public void testExtendedLengthWithDeclaration2() {
		String s = " /** hola */ int x = 2; /// hola";
		CompilationUnit unit = getCompilationUnit(s);
		assertEquals(0, unit.getProblems().length);
		assertEquals(1, unit.declarations().size());
		
		Declaration decl = unit.declarations().get(0);
		assertExtendedPosition(decl, 1, s.length() - 1, unit);
	}
	
	public void testExtendedLengthWithModuleDeclaration() {
		String s = " /** hola */ module a;";
		CompilationUnit unit = getCompilationUnit(s);
		assertEquals(0, unit.getProblems().length);
		
		ModuleDeclaration decl = unit.getModuleDeclaration();
		assertExtendedPosition(decl, 1, s.length() - 1, unit);
	}
	
	public void testExtendedLengthWithModuleDeclaration2() {
		String s = " /** hola */ module a; /// hola";
		CompilationUnit unit = getCompilationUnit(s);
		assertEquals(0, unit.getProblems().length);
		
		ModuleDeclaration decl = unit.getModuleDeclaration();
		assertExtendedPosition(decl, 1, s.length() - 1, unit);
	}

}
