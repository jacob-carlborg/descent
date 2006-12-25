package descent.tests.mars;

import descent.core.dom.ASTNode;
import descent.core.dom.UnitTestDeclaration;

public class UnitTest_Test extends Parser_Test {
	
	public void test() {
		String s = " unittest { } ";
		
		UnitTestDeclaration inv = (UnitTestDeclaration) getSingleDeclarationNoProblems(s);
		assertEquals(ASTNode.UNIT_TEST_DECLARATION, inv.getNodeType());
		assertPosition(inv, 1, 12);
	}

}
