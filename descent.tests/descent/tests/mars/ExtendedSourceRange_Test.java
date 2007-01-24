package descent.tests.mars;

import descent.core.dom.ASTNode;
import descent.core.dom.AliasDeclaration;
import descent.core.dom.CompilationUnit;

public class ExtendedSourceRange_Test extends Parser_Test {
	
	public void testOnAlias() {
		String s = "\n// hola\nalias int Bla;";
		AliasDeclaration alias = (AliasDeclaration) getSingleDeclarationNoProblems(s);
		assertEquals(ASTNode.ALIAS_DECLARATION, alias.getNodeType());
		
		CompilationUnit unit = (CompilationUnit) alias.getRoot();
		assertEquals(1, unit.getExtendedStartPosition(alias));
		assertEquals(22, unit.getExtendedLength(alias));
	}

}
