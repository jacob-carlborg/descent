package descent.tests.mars;

import descent.core.dom.ASTNode;
import descent.core.dom.AggregateDeclaration;
import descent.core.dom.SimpleName;

public class Struct_Test extends Parser_Test {
	
	public void testEmpty() {
		String s = " struct Clazz { }";
		AggregateDeclaration c = (AggregateDeclaration) getSingleDeclarationNoProblems(s);
		assertEquals(ASTNode.AGGREGATE_DECLARATION, c.getNodeType());
		assertEquals(AggregateDeclaration.Kind.STRUCT, c.getKind());
		assertPosition(c, 1, 16);
		
		SimpleName name = c.getName();
		assertEquals(ASTNode.SIMPLE_NAME, name.getNodeType());
		assertEquals("Clazz", name.getIdentifier());
		assertPosition(name, 8, 5);
		
		assertEquals(0, c.baseClasses().size());
	}
	
	public void testSemicolon() {
		String s = " struct Clazz;";
		AggregateDeclaration c = (AggregateDeclaration) getSingleDeclarationNoProblems(s);
		assertPosition(c, 1, 13);
	}

}
