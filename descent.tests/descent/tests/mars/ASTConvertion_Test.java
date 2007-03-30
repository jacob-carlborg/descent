package descent.tests.mars;

import descent.core.dom.Block;
import descent.core.dom.CompilationUnit;
import descent.core.dom.FunctionDeclaration;

public class ASTConvertion_Test extends Parser_Test {
	
	public void testFunction() {
		CompilationUnit unit = getCompilationUnit("void bla() { }");
		FunctionDeclaration func = (FunctionDeclaration) unit.declarations().get(0);
		Block block = (Block) func.getBody();
		assertEquals(0, block.statements().size());
	}

}
