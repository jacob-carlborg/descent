package descent.tests.format;

import java.util.HashMap;
import java.util.Map;

import descent.core.formatter.DefaultCodeFormatterConstants2;

public class FormatFunctionDeclaration_Tests extends AbstractFormatter_Test {
	
	public void testEmpty() throws Exception {
		assertFormat(
				"void bla();\r\n", 
				
				"void  bla  ()  ;"
			);
	}
	
	public void testEmptyInsertSpaceBeforeSemicolon() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants2.FORMATTER_INSERT_SPACE_BEFORE_SEMICOLON, DefaultCodeFormatterConstants2.TRUE);
		assertFormat(
				"void bla() ;\r\n", 
				
				"void  bla  ()  ;",
				
				options
			);
	}
	
	public void testEmptyInsertSpaceBeforeOpenParen() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants2.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_FUNCTION_DECLARATION, DefaultCodeFormatterConstants2.TRUE);
		assertFormat(
				"void bla ();\r\n", 
				
				"void  bla  ()  ;",
				
				options
			);
	}
	
	public void testEmptyInsertSpaceAfterOpenParen() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants2.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_FUNCTION_DECLARATION, DefaultCodeFormatterConstants2.TRUE);
		assertFormat(
				"void bla( int x);\r\n", 
				
				"void  bla  (int x)  ;",
				
				options
			);
	}
	
	public void testEmptyInsertSpaceBeforeCloseParen() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants2.FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_FUNCTION_DECLARATION, DefaultCodeFormatterConstants2.TRUE);
		assertFormat(
				"void bla(int x );\r\n", 
				
				"void  bla  (int x)  ;",
				
				options
			);
	}
	
	public void testEmptyInsertSpaceBetweenEmptyParen() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants2.FORMATTER_INSERT_SPACE_BETWEEN_EMPTY_PARENS_IN_FUNCTION_DECLARATION, DefaultCodeFormatterConstants2.TRUE);
		assertFormat(
				"void bla( );\r\n", 
				
				"void  bla  ()  ;",
				
				options
			);
	}
	
	public void testNoBodyTrailingComment() throws Exception {
		assertFormat(
				"void bla(); // comment", 
				
				"void  bla  ()  ;   // comment"
			);
	}
	
	/* TODO
	public void testBody() throws Exception {
		assertFormat(
				"void bla() {\r\n" +
				"}\r\n", 
				
				"void  bla  ()  {   }"
			);
	}
	*/

}
