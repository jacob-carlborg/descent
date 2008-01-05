package descent.tests.mars;

import java.util.List;

import descent.core.dom.ASTNode;
import descent.core.dom.AliasDeclaration;
import descent.core.dom.CompilationUnit;
import descent.core.dom.Declaration;

public class ExtendedSourceRange_Test extends Parser_Test {
	
	public void testOnAlias() {
		String s = "\n// hola\nalias int Bla;";
		AliasDeclaration alias = (AliasDeclaration) getSingleDeclarationNoProblems(s);
		assertEquals(ASTNode.ALIAS_DECLARATION, alias.getNodeType());
		
		CompilationUnit unit = (CompilationUnit) alias.getRoot();
		assertEquals(1, unit.getExtendedStartPosition(alias));
		assertEquals(22, unit.getExtendedLength(alias));
	}
	

	public void testOnAlias2() {
		String s = "\r\n// hola\r\nalias int Bla;";
		AliasDeclaration alias = (AliasDeclaration) getSingleDeclarationNoProblems(s);
		assertEquals(ASTNode.ALIAS_DECLARATION, alias.getNodeType());
		
		CompilationUnit unit = (CompilationUnit) alias.getRoot();
		assertEquals(2, unit.getExtendedStartPosition(alias));
		assertEquals(23, unit.getExtendedLength(alias));
	}
	
	public void testEnumMemberCommentAndFunction() {
		String s = "enum Foo { /** comment */ a } void foo() { }";
		List<Declaration> declarations = getDeclarationsNoProblems(s);
		Declaration decl = declarations.get(1);
		
		CompilationUnit unit = (CompilationUnit) decl.getRoot();
		assertEquals(30, unit.getExtendedStartPosition(decl));
		assertEquals(14, unit.getExtendedLength(decl));
	}

}
