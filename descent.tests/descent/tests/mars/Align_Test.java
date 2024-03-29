package descent.tests.mars;

import descent.core.dom.ASTNode;
import descent.core.dom.AlignDeclaration;
import descent.internal.compiler.parser.Global;

public class Align_Test extends Parser_Test {
	
	public void testWithNumber() {
		String s = " align(4): class Pub { }";
		AlignDeclaration align = (AlignDeclaration) getSingleDeclarationNoProblems(s);
		assertEquals(ASTNode.ALIGN_DECLARATION, align.getNodeType());
		assertEquals(4, align.getAlign());
		
		assertEquals(1, align.declarations().size());
	}
	
	public void testWithoutNumber() {
		String s = " align: class Pub { }";
		AlignDeclaration align = (AlignDeclaration) getSingleDeclarationNoProblems(s);
		assertEquals(ASTNode.ALIGN_DECLARATION, align.getNodeType());
		assertEquals(new Global().structalign, align.getAlign());
		
		assertEquals(1, align.declarations().size());
	}

}
