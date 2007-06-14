package descent.tests.format;

import java.util.HashMap;
import java.util.Map;

import descent.core.formatter.DefaultCodeFormatterConstants;

public class FormatFunctionDeclaration_Tests extends AbstractFormatter_Test {
	
	public void testEmpty() throws Exception {
		assertFormat(
				"void bla();\r\n", 
				
				"void  bla  ()  ;"
			);
	}
	
	public void testEmptyInsertSpaceBeforeSemicolon() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_SEMICOLON, DefaultCodeFormatterConstants.TRUE);
		assertFormat(
				"void bla() ;\r\n", 
				
				"void  bla  ()  ;",
				
				options
			);
	}
	
	public void testEmptyInsertSpaceBeforeOpenParen() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_FUNCTION_DECLARATION, DefaultCodeFormatterConstants.TRUE);
		assertFormat(
				"void bla ();\r\n", 
				
				"void  bla  ()  ;",
				
				options
			);
	}
	
	public void testEmptyInsertSpaceAfterOpenParen() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_FUNCTION_DECLARATION, DefaultCodeFormatterConstants.TRUE);
		assertFormat(
				"void bla( int x);\r\n", 
				
				"void  bla  (int x)  ;",
				
				options
			);
	}
	
	public void testEmptyInsertSpaceBeforeCloseParen() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_FUNCTION_DECLARATION, DefaultCodeFormatterConstants.TRUE);
		assertFormat(
				"void bla(int x );\r\n", 
				
				"void  bla  (int x)  ;",
				
				options
			);
	}
	
	public void testEmptyInsertSpaceBetweenEmptyParen() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BETWEEN_EMPTY_PARENS_IN_FUNCTION_DECLARATION, DefaultCodeFormatterConstants.TRUE);
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
	
	public void testBracesNextLine() throws Exception {
		assertFormat(
				"void bla()\r\n" +
				"{\r\n" +
				"}\r\n", 
				
				"void  bla  ()  {   }"
			);
	}
	
	public void testBracesSameLine() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_FUNCTION_DECLARATION, DefaultCodeFormatterConstants.END_OF_LINE);
		assertFormat(
				"void bla() {\r\n" +
				"}\r\n", 
				
				"void  bla  ()  {   }",
				
				options
			);
	}
	
	public void testBracesNextLineShifted() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_FUNCTION_DECLARATION, DefaultCodeFormatterConstants.NEXT_LINE_SHIFTED);
		assertFormat(
				"void bla()\r\n" +
				"\t{\r\n" +
				"\t}\r\n", 
				
				"void  bla  ()  {   }",
				
				options
			);
	}
	
	public void testBracesNextLineShiftedWithContent() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_FUNCTION_DECLARATION, DefaultCodeFormatterConstants.NEXT_LINE_SHIFTED);
		assertFormat(
				"void bla()\r\n" +
				"\t{\r\n" +
				"\t\tint x;\r\n" +				
				"\t}\r\n", 
				
				"void  bla  ()  {   int x;  }",
				
				options
			);
	}

}
