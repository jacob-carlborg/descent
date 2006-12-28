package descent.tests.mars;

import java.util.List;

import descent.core.dom.AggregateDeclaration;
import descent.core.dom.Comment;
import descent.core.dom.CompilationUnit;
import descent.core.dom.Declaration;
import descent.core.dom.EnumDeclaration;
import descent.core.dom.EnumMember;
import descent.core.dom.ModuleDeclaration;
import descent.core.dom.VariableDeclaration;

public class Comment_Test extends Parser_Test {
	
	public void testPreviousComments() {
		String s = " /** hola */ class Clazz;";
		AggregateDeclaration c = (AggregateDeclaration) getSingleDeclarationNoProblems(s);
		assertPosition(c, 13, 12);
		
		List<Comment> comments = c.dDocs();
		assertEquals(1, comments.size());
		assertPosition(comments.get(0), 1, 11);
	}
	
	public void testDontCarryComments() {
		String s = " /** hola */ class A; class B;";
		List<Declaration> declDefs = getDeclarationsNoProblems(s);
		assertEquals(2, declDefs.size());
		
		AggregateDeclaration c;
		List<Comment> comments;
		
		c = (AggregateDeclaration) declDefs.get(0);
		comments = c.dDocs();
		assertEquals(1, comments.size());
		
		c = (AggregateDeclaration) declDefs.get(1);
		comments = c.dDocs();
		assertEquals(0, comments.size());
	}
	
	public void testLeadingComment() {
		String s = " class Clazz; /** hola */";
		AggregateDeclaration c = (AggregateDeclaration) getSingleDeclarationNoProblems(s);
		assertPosition(c, 1, 12);
		
		List<Comment> comments = c.dDocs();
		assertEquals(1, comments.size());
		assertPosition(comments.get(0), 14, 11);
	}
	
	public void testPreviosAndLeadingComment() {
		String s = " /** hola */ class Clazz; /** hola */";
		AggregateDeclaration c = (AggregateDeclaration) getSingleDeclarationNoProblems(s);
		assertPosition(c, 13, 12);
		
		List<Comment> comments = c.dDocs();
		assertEquals(2, comments.size());
		assertPosition(comments.get(0), 1, 11);
		assertPosition(comments.get(1), 26, 11);
	}
	
	public void testLeadingAndPreviousInNextLineComment() {
		String s = " /** hola */ class Clazz; /** hola */ \n /** hola */";
		AggregateDeclaration c = (AggregateDeclaration) getSingleDeclarationNoProblems(s);
		assertPosition(c, 13, 12);
		
		List<Comment> comments = c.dDocs();
		assertEquals(2, comments.size());
		assertPosition(comments.get(0), 1, 11);
		assertPosition(comments.get(1), 26, 11);
	}
	
	public void testLeadingAndPreviousInNextLineCommentInModule() {
		String s = " /** hola */ module a.bc; /** hola */ \n /** hola */";
		CompilationUnit compilationUnit = getCompilationUnit(s);
		assertEquals(0, compilationUnit.getProblems().length);
		ModuleDeclaration c = (ModuleDeclaration) compilationUnit.getModuleDeclaration();
		assertPosition(c, 13, 12);
		
		List<Comment> comments = c.dDocs();
		assertEquals(2, comments.size());
		assertPosition(comments.get(0), 1, 11);
		assertPosition(comments.get(1), 26, 11);
	}
	
	public void testLeadingAndPreviousInNextLineCommentInVarDeclaration() {
		String s = " /** hola */ int abcdefg; /** hola */ \n /** hola */";
		CompilationUnit compilationUnit = getCompilationUnit(s);
		assertEquals(0, compilationUnit.getProblems().length);
		VariableDeclaration c = (VariableDeclaration) getSingleDeclarationNoProblems(s);
		assertPosition(c, 13, 12);
		
		List<Comment> comments = c.dDocs();
		assertEquals(2, comments.size());
		assertPosition(comments.get(0), 1, 11);
		assertPosition(comments.get(1), 26, 11);
	}
	
	public void testLeadingAndPreviousInNextLineCommentInEnumMember() {
		String s = " enum X { /** hola */ a, /** hola */ \n /** hola */ }";
		EnumDeclaration enumDeclaration = (EnumDeclaration) getSingleDeclarationNoProblems(s);
		assertPosition(enumDeclaration, 1, s.length() - 1);
		
		assertEquals(1, enumDeclaration.enumMembers().size());
		
		EnumMember c = enumDeclaration.enumMembers().get(0);
		
		/* TODO
		List<Comment> comments = c.dDocs();
		assertEquals(2, comments.size());
		assertPosition(comments.get(0), 1, 11);
		assertPosition(comments.get(1), 26, 11);
		*/
	}

}
