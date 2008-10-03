package descent.tests.mars;

import java.util.List;

import descent.core.dom.AggregateDeclaration;
import descent.core.dom.AliasDeclaration;
import descent.core.dom.CompilationUnit;
import descent.core.dom.DDocComment;
import descent.core.dom.Declaration;
import descent.core.dom.EnumDeclaration;
import descent.core.dom.EnumMember;
import descent.core.dom.ModuleDeclaration;
import descent.core.dom.TypedefDeclaration;
import descent.core.dom.VariableDeclaration;

public class Comment_Test extends Parser_Test {
	
	public void testPreviousComments() {
		String s = " /** hola */ class Clazz;";
		AggregateDeclaration c = (AggregateDeclaration) getSingleDeclarationNoProblems(s);
		assertPosition(c, 1, s.length() - 1);
		
		List<DDocComment> comments = c.preDDocs();
		assertEquals(1, comments.size());
		assertPosition(comments.get(0), 1, 11);
	}
	
	public void testPreviousComments2() {
		String s = " /** hola */ /** chau */ class Clazz;";
		AggregateDeclaration c = (AggregateDeclaration) getSingleDeclarationNoProblems(s);
		assertPosition(c, 1, s.length() - 1);
		
		List<DDocComment> comments = c.preDDocs();
		assertEquals(2, comments.size());
	}
	
	public void testPreviousComments3() {
		String s = " /** hola */ class Clazz; \n /** chau */ class Claxx;";
		List<Declaration> declarations = getDeclarationsNoProblems(s);
		assertEquals(2, declarations.size());
		
		AggregateDeclaration c;
		List<DDocComment> comments;
		
		c= (AggregateDeclaration) declarations.get(0);		
		assertPosition(c, 1, 24);
		
		comments = c.preDDocs();
		assertEquals(1, comments.size());
		assertPosition(comments.get(0), 1, 11);
		
		c= (AggregateDeclaration) declarations.get(1);		
		assertPosition(c, 28, 24);
		
		comments = c.preDDocs();
		assertEquals(1, comments.size());
		assertPosition(comments.get(0), 28, 11);
	}
	
	public void testPreviousComments4() {
		String s = " /** hola */ // Hola\n /** Pepe */ class Clazz; \n /** lala */ // Jeje \n /** chau */ class Claxx;";
		List<Declaration> declarations = getDeclarationsNoProblems(s);
		assertEquals(2, declarations.size());
		
		AggregateDeclaration c;
		
		c= (AggregateDeclaration) declarations.get(0);		
		assertEquals(1, c.preDDocs().size());
		
		c= (AggregateDeclaration) declarations.get(1);		
		assertEquals(1, c.preDDocs().size());
	}
	
	public void testPreviousComments5() {
		String s = " /**\r\n" +
				   "   hola\r\n" +
				   "   */\r\n" +
				   "\r\n" +
				   "abstract class Clazz;";
		AggregateDeclaration c = (AggregateDeclaration) getSingleDeclarationNoProblems(s);
		assertPosition(c, 1, s.length() - 1);
		
		List<DDocComment> comments = c.preDDocs();
		assertEquals(1, comments.size());
	}
	
	public void testDontCarryComments() {
		String s = " /** hola */ class A; class B;";
		List<Declaration> declDefs = getDeclarationsNoProblems(s);
		assertEquals(2, declDefs.size());
		
		AggregateDeclaration c;
		List<DDocComment> comments;
		
		c = (AggregateDeclaration) declDefs.get(0);
		comments = c.preDDocs();
		assertEquals(1, comments.size());
		
		c = (AggregateDeclaration) declDefs.get(1);
		comments = c.preDDocs();
		assertEquals(0, comments.size());
	}
	
	public void testDontCarryComments2() {
		String s = " /** hola */ class A { class B; }";
		List<Declaration> declDefs = getDeclarationsNoProblems(s);
		assertEquals(1, declDefs.size());
		
		AggregateDeclaration c;
		List<DDocComment> comments;
		
		c = (AggregateDeclaration) declDefs.get(0);
		comments = c.preDDocs();
		assertEquals(1, comments.size());
		
		assertEquals(0, c.declarations().get(0).preDDocs().size());
	}
	
	public void testDontCarryComments3() {
		String s = " /** hola */ int x /* foo */; int y;";
		List<Declaration> declDefs = getDeclarationsNoProblems(s);
		assertEquals(2, declDefs.size());
		
		VariableDeclaration v;
		List<DDocComment> comments;
		
		v = (VariableDeclaration) declDefs.get(0);
		comments = v.preDDocs();
		assertEquals(1, comments.size());
		
		v = (VariableDeclaration) declDefs.get(1);
		comments = v.preDDocs();
		assertEquals(0, comments.size());
	}
	
	public void testLeadingComment() {
		String s = " class Clazz; /** hola */";
		AggregateDeclaration c = (AggregateDeclaration) getSingleDeclarationNoProblems(s);
		assertPosition(c, 1, s.length() - 1);
		
		List<DDocComment> comments = c.preDDocs();
		assertEquals(0, comments.size());
		
		assertPosition(c.getPostDDoc(), 14, 11);
	}
	
	public void testLeadingCommentInAliasDeclaration() {
		String s = " alias int x; /** hola */";
		AliasDeclaration c = (AliasDeclaration) getSingleDeclarationNoProblems(s);
		assertPosition(c, 1, s.length() - 1);
		
		List<DDocComment> comments = c.preDDocs();
		assertEquals(0, comments.size());
		
		assertPosition(c.getPostDDoc(), 14, 11);
	}
	
	public void testLeadingCommentNotDDoc() {
		String s = " class Clazz; /* hola */";
		AggregateDeclaration c = (AggregateDeclaration) getSingleDeclarationNoProblems(s);
		assertPosition(c, 1, 12);
		
		List<DDocComment> comments = c.preDDocs();
		assertEquals(0, comments.size());
	}
	
	public void testPreviosAndLeadingComment() {
		String s = " /** hola */ class Clazz; /** hola */";
		AggregateDeclaration c = (AggregateDeclaration) getSingleDeclarationNoProblems(s);
		assertPosition(c, 1, s.length() - 1);
		
		List<DDocComment> comments = c.preDDocs();
		assertEquals(1, comments.size());
		assertPosition(comments.get(0), 1, 11);
		assertPosition(c.getPostDDoc(), 26, 11);
	}
	
	public void testLeadingAndPreviousInNextLineComment() {
		String s = " /** hola */ class Clazz; /** hola */ \n /** hola */";
		AggregateDeclaration c = (AggregateDeclaration) getSingleDeclarationNoProblems(s);
		assertPosition(c, 1, 36);
		
		List<DDocComment> comments = c.preDDocs();
		assertEquals(1, comments.size());
		assertPosition(comments.get(0), 1, 11);
		assertPosition(c.getPostDDoc(), 26, 11);
	}
	
	public void testLeadingAndPreviousInNextLineCommentInModule() {
		String s = " /** hola */ module a.bc; /** hola */ \n /** hola */";
		CompilationUnit compilationUnit = getCompilationUnit(s);
		assertEquals(0, compilationUnit.getProblems().length);
		ModuleDeclaration c = (ModuleDeclaration) compilationUnit.getModuleDeclaration();
		assertPosition(c, 1, 36);
		
		List<DDocComment> comments = c.preDDocs();
		assertEquals(1, comments.size());
		assertPosition(comments.get(0), 1, 11);
		assertPosition(c.getPostDDoc(), 26, 11);
	}
	
	public void testLeadingAndPreviousInNextLineCommentInVarDeclaration() {
		String s = " /** hola */ int abcdefg; /** hola */ \n /** hola */";
		CompilationUnit compilationUnit = getCompilationUnit(s);
		assertEquals(0, compilationUnit.getProblems().length);
		VariableDeclaration c = (VariableDeclaration) getSingleDeclarationNoProblems(s);
		assertPosition(c, 1, 36);
		
		List<DDocComment> comments = c.preDDocs();
		assertEquals(1, comments.size());
		assertPosition(comments.get(0), 1, 11);
		assertPosition(c.getPostDDoc(), 26, 11);
	}
	
	public void testLeadingAndPreviousInNextLineCommentInTypedefDeclaration() {
		String s = " /** hola */ typedef int abcdefg; /** hola */ \n /** hola */";
		CompilationUnit compilationUnit = getCompilationUnit(s);
		assertEquals(0, compilationUnit.getProblems().length);
		TypedefDeclaration c = (TypedefDeclaration) getSingleDeclarationNoProblems(s);
		assertPosition(c, 1, 44);
		
		List<DDocComment> comments = c.preDDocs();
		assertEquals(1, comments.size());
		assertPosition(comments.get(0), 1, 11);
		assertPosition(c.getPostDDoc(), 34, 11);
	}
	
	public void testLeadingCommentWithLineBreak() {
		String s = " int a; /** \n hola */";
		CompilationUnit compilationUnit = getCompilationUnit(s);
		assertEquals(0, compilationUnit.getProblems().length);
		VariableDeclaration c = (VariableDeclaration) getSingleDeclarationNoProblems(s);
		
		List<DDocComment> comments = c.preDDocs();
		assertEquals(0, comments.size());
		
		assertNotNull(c.getPostDDoc());
	}
	
	public void testLeadingCommentWithLineBreakBefore() {
		String s = " int a; \n /** hola */";
		CompilationUnit compilationUnit = getCompilationUnit(s);
		assertEquals(0, compilationUnit.getProblems().length);
		VariableDeclaration c = (VariableDeclaration) getSingleDeclarationNoProblems(s);
		
		List<DDocComment> comments = c.preDDocs();
		assertEquals(0, comments.size());
		
		assertNull(c.getPostDDoc());
	}
	
	public void testVariableFragmentsProblem() {
		String s = " /** hola */ int a, b;";
		Declaration c = getSingleDeclarationNoProblems(s);
		assertEquals(1, c.preDDocs().size());
	}
	
	public void testAliasFragmentsProblem() {
		String s = " /** hola */ alias int a, b;";
		Declaration c = getSingleDeclarationNoProblems(s);
		assertEquals(1, c.preDDocs().size());
	}
	
	public void testTypedefFragmentsProblem() {
		String s = " /** hola */ typedef int a, b;";
		Declaration c = getSingleDeclarationNoProblems(s);
		assertEquals(1, c.preDDocs().size());
	}
	
	public void testDontGlueComments() {
		String s = " int x; /// side comment\r\n" +
				   "/// top comment\r\n" +
				   "int y;";
		List<Declaration> decls = getDeclarationsNoProblems(s);
		assertEquals(2, decls.size());
		
		Declaration x = decls.get(0);
		assertEquals("/// side comment\r\n", x.getPostDDoc().getText());
		assertPosition(x, 1, 25);
		
		Declaration y = decls.get(1);
		assertEquals("/// top comment\r\n", y.preDDocs().get(0).getText());
		assertPosition(y, 26, 23);
	}
	
	public void testDontGlueComments2() {
		String s = 
			" class X {\r\n" +
			"\tint x; /// side comment\r\n" +
			"\t/// top comment\r\n" +
			"\tint y;\r\n" +
			"}";
		
		List<Declaration> decls = ((AggregateDeclaration) getSingleDeclarationNoProblems(s)).declarations();
		
		Declaration x = decls.get(0);
		assertEquals("/// side comment\r\n", x.getPostDDoc().getText());
		assertPosition(x, 13, 25);
		
		Declaration y = decls.get(1);
		assertEquals("/// top comment\r\n", y.preDDocs().get(0).getText());
		assertPosition(y, 39, 24);
	}
	
	public void testTicket121() {
		String s = 
			"enum {\r\n" + 
			"	/// no ddoc in case1\r\n" + 
			"	case1,\r\n" + 
			"	\r\n" + 
			"	/**\r\n" + 
			"	 * no ddoc in case2\r\n" + 
			"	 */ \r\n" + 
			"	case2,\r\n" + 
			"\r\n" + 
			"	case3, /// ok in case3\r\n" + 
			"	\r\n" + 
			"	/// shows both\r\n" + 
			"	case4, /// sections in case 4\r\n" + 
			"}\r\n" + 
			"";
		
		List<EnumMember> decls = ((EnumDeclaration) getSingleDeclarationNoProblems(s)).enumMembers();
		
		EnumMember x = decls.get(0);
		assertEquals(1, x.preDDocs().size());
		assertEquals("/// no ddoc in case1\r\n", x.preDDocs().get(0).getText());
		
		EnumMember y = decls.get(1);
		assertEquals(1, y.preDDocs().size());
		assertEquals("/**\r\n" + 
			"	 * no ddoc in case2\r\n" + 
			"	 */", y.preDDocs().get(0).getText());
		
		EnumMember z = decls.get(2);
		assertEquals("/// ok in case3\r\n", z.getPostDDoc().getText());
		
		EnumMember a = decls.get(3);
		assertEquals(1, a.preDDocs().size());
		assertEquals("/// shows both\r\n", a.preDDocs().get(0).getText());
		assertEquals("/// sections in case 4\r\n", a.getPostDDoc().getText());
	}
	
	public void testTicket121_a() {
		String s = 
			"enum {\r\n" + 
			"	/// no ddoc in case1\r\n" + 
			"	case1\r\n" + 
			"}\r\n" + 
			"";
		
		List<EnumMember> decls = ((EnumDeclaration) getSingleDeclarationNoProblems(s)).enumMembers();
		
		EnumMember x = decls.get(0);
		assertEquals(1, x.preDDocs().size());
		assertEquals("/// no ddoc in case1\r\n", x.preDDocs().get(0).getText());
	}

}
