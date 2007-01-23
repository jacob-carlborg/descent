package descent.tests.mars;

import descent.core.dom.ASTNode;
import descent.core.dom.AliasDeclaration;
import descent.core.dom.CompilationUnit;

public class ExtendedSourceRange_Test extends Parser_Test {
	
	public void testOnAlias() {
		String s = " // hola\nalias int Bla;";
		AliasDeclaration alias = (AliasDeclaration) getSingleDeclarationNoProblems(s);
		assertEquals(ASTNode.ALIAS_DECLARATION, alias.getNodeType());
		
		CompilationUnit unit = (CompilationUnit) alias.getRoot();
		System.out.println(unit.getExtendedStartPosition(alias));
		System.out.println(unit.getExtendedLength(alias));
	}

}
