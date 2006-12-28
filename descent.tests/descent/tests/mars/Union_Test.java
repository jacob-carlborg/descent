package descent.tests.mars;

import java.util.List;

import descent.core.dom.ASTNode;
import descent.core.dom.AggregateDeclaration;
import descent.core.dom.Comment;
import descent.core.dom.SimpleName;

public class Union_Test extends Parser_Test {
	
	public void testEmpty() {
		String s = " union Clazz { }";
		
		AggregateDeclaration c = (AggregateDeclaration) getSingleDeclarationNoProblems(s);
		assertEquals(ASTNode.AGGREGATE_DECLARATION, c.getNodeType());
		assertEquals(AggregateDeclaration.Kind.UNION, c.getKind());
		assertPosition(c, 1, 15);
		
		SimpleName name = c.getName();
		assertEquals(ASTNode.SIMPLE_NAME, name.getNodeType());
		assertEquals("Clazz", name.getIdentifier());
		assertPosition(name, 7, 5);
		
		assertEquals(0, c.baseClasses().size());
	}
	
	public void testSemicolon() {
		String s = " union Clazz;";
		AggregateDeclaration c = (AggregateDeclaration) getSingleDeclarationNoProblems(s);
		assertPosition(c, 1, 12);
	}
	
	public void testWithComments() {
		String s = " /** hola */ union Clazz;";
		AggregateDeclaration c = (AggregateDeclaration) getSingleDeclarationNoProblems(s);
		assertPosition(c, 13, 12);
		
		List<Comment> comments = c.dDocs();
		assertEquals(1, comments.size());
		assertPosition(comments.get(0), 1, 11);
	}

}
