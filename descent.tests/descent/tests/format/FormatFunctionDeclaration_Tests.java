package descent.tests.format;

import java.util.HashMap;
import java.util.Map;

import descent.core.formatter.DefaultCodeFormatterConstants;

public class FormatFunctionDeclaration_Tests extends AbstractFormatter_Test {
	
	private static String[] prefixes = { "void bla", "this" };
	
	@Override
	protected Map getDefaultOptions() {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_SEMICOLON, DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_FUNCTION_DECLARATION_PARAMETERS, DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_FUNCTION_DECLARATION_PARAMETERS, DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_FUNCTION_DECLARATION_PARAMETERS, DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BETWEEN_EMPTY_PARENS_IN_FUNCTION_DECLARATION, DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_FUNCTION_DECLARATION, DefaultCodeFormatterConstants.END_OF_LINE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_KEEP_FUNCTIONS_WITH_NO_STATEMENT_IN_ONE_LINE, DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_KEEP_FUNCTIONS_WITH_ONE_STATEMENT_IN_ONE_LINE, DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INDENT_STATEMENTS_COMPARE_TO_FUNCTION_HEADER, DefaultCodeFormatterConstants.TRUE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INDENT_STATEMENTS_COMPARE_TO_FUNCTION_IN_HEADER, DefaultCodeFormatterConstants.TRUE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INDENT_STATEMENTS_COMPARE_TO_FUNCTION_OUT_HEADER, DefaultCodeFormatterConstants.TRUE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INDENT_STATEMENTS_COMPARE_TO_FUNCTION_BODY_HEADER, DefaultCodeFormatterConstants.TRUE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INDENT_IN_OUT_BODY_COMPARE_TO_FUNCTION_HEADER, DefaultCodeFormatterConstants.FALSE);
		return options;
	}
	
	public void testEmpty() throws Exception {
		for(String prefix : prefixes) {
			assertFormat(
					prefix + "();", 
					
					prefix + "()  ;"
				);
		}
	}
	
	public void testEmptyInsertSpaceBeforeSemicolon() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_SEMICOLON, DefaultCodeFormatterConstants.TRUE);
		for(String prefix : prefixes) {
			assertFormat(
					prefix + "() ;", 
					
					prefix + "()  ;",
					
					options
				);
		}
	}
	
	public void testEmptyInsertSpaceBeforeOpenParen() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_FUNCTION_DECLARATION_PARAMETERS, DefaultCodeFormatterConstants.TRUE);
		for(String prefix : prefixes) {
			assertFormat(
					prefix + " ();", 
					
					prefix + "()  ;",
					
					options
				);
		}
	}
	
	public void testEmptyInsertSpaceAfterOpenParen() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_FUNCTION_DECLARATION_PARAMETERS, DefaultCodeFormatterConstants.TRUE);
		for(String prefix : prefixes) {
			assertFormat(
					prefix + "( int x);", 
					
					prefix + "(int x)  ;",
					
					options
				);
		}
	}
	
	public void testEmptyInsertSpaceBeforeCloseParen() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_FUNCTION_DECLARATION_PARAMETERS, DefaultCodeFormatterConstants.TRUE);
		for(String prefix : prefixes) {
			assertFormat(
					prefix + "(int x );", 
					
					prefix + "(int x)  ;",
					
					options
				);
		}
	}
	
	public void testEmptyInsertSpaceBetweenEmptyParen() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BETWEEN_EMPTY_PARENS_IN_FUNCTION_DECLARATION, DefaultCodeFormatterConstants.TRUE);
		for(String prefix : prefixes) {
			assertFormat(
					prefix + "( );", 
					
					prefix + "()  ;",
					
					options
				);
		}
	}
	
	public void testNoBodyTrailingComment() throws Exception {
		for(String prefix : prefixes) {
			assertFormat(
					prefix + "(); // comment", 
					
					prefix + "()  ;   // comment"
				);
		}
	}
	
	public void testBracesNextLine() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_FUNCTION_DECLARATION, DefaultCodeFormatterConstants.NEXT_LINE);
		for(String prefix : prefixes) {
			assertFormat(
					prefix + "()\r\n" +
					"{\r\n" +
					"}", 
					
					prefix + "()  {   }",
					
					options
				);
		}
	}
	
	public void testBracesSameLine() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_FUNCTION_DECLARATION, DefaultCodeFormatterConstants.END_OF_LINE);
		for(String prefix : prefixes) {
			assertFormat(
					prefix + "() {\r\n" +
					"}", 
					
					prefix + "()  {   }",
					
					options
				);
		}
	}
	
	public void testBracesNextLineShifted() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_FUNCTION_DECLARATION, DefaultCodeFormatterConstants.NEXT_LINE_SHIFTED);
		for(String prefix : prefixes) {
			assertFormat(
					prefix + "()\r\n" +
						"\t{\r\n" +
						"\t}", 
					
					prefix + "()  {   }",
					
					options
				);
		}
	}
	
	public void testBracesNextLineShiftedWithContent() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_FUNCTION_DECLARATION, DefaultCodeFormatterConstants.NEXT_LINE_SHIFTED);
		for(String prefix : prefixes) {
			assertFormat(
					prefix + "()\r\n" +
						"\t{\r\n" +
							"\t\tint x;\r\n" +				
						"\t}", 
					
					prefix + "()  {   int x;  }",
					
					options
				);
		}
	}
	
	public void testKeepInOneLineIfNoStatement() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_KEEP_FUNCTIONS_WITH_NO_STATEMENT_IN_ONE_LINE, DefaultCodeFormatterConstants.TRUE);
		for(String prefix : prefixes) {
			assertFormat(
					prefix + "() { }", 
					
					prefix + "()  {   }",
					
					options
				);
		}
	}
	
	public void testKeepInOneLineIfOneStatement() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_KEEP_FUNCTIONS_WITH_ONE_STATEMENT_IN_ONE_LINE, DefaultCodeFormatterConstants.TRUE);
		for(String prefix : prefixes) {
			assertFormat(
					prefix + "() { return 2; }", 
					
					prefix + "()  {  return   2; }",
					
					options
				);
		}
	}
	
	public void testDontIndentCompareToParent() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INDENT_STATEMENTS_COMPARE_TO_FUNCTION_HEADER, DefaultCodeFormatterConstants.FALSE);
		for(String prefix : prefixes) {
			assertFormat(
					prefix + "() {\r\n" +
					"int x;\r\n" +
					"}", 
					
					prefix + "() {  int x;  }",
					
					options
				);
		}
	}
	
	public void testIndentCompareToIn() throws Exception {
		for(String prefix : prefixes) {
			assertFormat(
					prefix + "()\r\n" +
					"in {\r\n" +
					"\tint x;\r\n" +
					"}\r\n" +
					"body {\r\n" +
					"}", 
					
					prefix + "() in {  int x;  } body { }"
				);
		}
	}
	
	public void testDontIndentCompareToIn() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INDENT_STATEMENTS_COMPARE_TO_FUNCTION_IN_HEADER, DefaultCodeFormatterConstants.FALSE);
		for(String prefix : prefixes) {
			assertFormat(
					prefix + "()\r\n" +
					"in {\r\n" +
					"int x;\r\n" +
					"}\r\n" +
					"body {\r\n" +
					"}", 
					
					prefix + "() in {  int x;  } body { }",
					
					options
				);
		}
	}
	
	public void testIndentCompareToOut() throws Exception {
		for(String prefix : prefixes) {
			assertFormat(
					prefix + "()\r\n" +
					"out {\r\n" +
					"\tint x;\r\n" +
					"}\r\n" +
					"body {\r\n" +
					"}", 
					
					prefix + "() out {  int x;  } body { }"
				);
		}
	}
	
	public void testDontIndentCompareToOut() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INDENT_STATEMENTS_COMPARE_TO_FUNCTION_OUT_HEADER, DefaultCodeFormatterConstants.FALSE);
		for(String prefix : prefixes) {
			assertFormat(
					prefix + "()\r\n" +
					"out {\r\n" +
					"int x;\r\n" +
					"}\r\n" +
					"body {\r\n" +
					"}", 
					
					prefix + "() out {  int x;  } body { }",
					
					options
				);
		}
	}
	
	public void testIndentCompareToBody() throws Exception {
		for(String prefix : prefixes) {
			assertFormat(
					prefix + "()\r\n" +
					"out {\r\n" +
					"}\r\n" +
					"body {\r\n" +
					"\tint x;\r\n" +
					"}", 
					
					prefix + "() out {  } body { int x;  }"
				);
		}
	}
	
	public void testDontIndentCompareToBody() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INDENT_STATEMENTS_COMPARE_TO_FUNCTION_BODY_HEADER, DefaultCodeFormatterConstants.FALSE);
		for(String prefix : prefixes) {
			assertFormat(
					prefix + "()\r\n" +
					"out {\r\n" +
					"}\r\n" +
					"body {\r\n" +
					"int x;\r\n" +
					"}", 
					
					prefix + "() out {  } body { int x;  }",
					
					options
				);
		}
	}
	
	public void testIndentCompareToBodySingle() throws Exception {
		for(String prefix : prefixes) {
			assertFormat(
					prefix + "()\r\n" +
					"body {\r\n" +
					"\tint x;\r\n" +
					"}", 
					
					prefix + "() body { int x;  }"
				);
		}
	}
	
	public void testDontIndentCompareToBodySingle() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INDENT_STATEMENTS_COMPARE_TO_FUNCTION_BODY_HEADER, DefaultCodeFormatterConstants.FALSE);
		for(String prefix : prefixes) {
			assertFormat(
					prefix + "()\r\n" +
					"body {\r\n" +
					"int x;\r\n" +
					"}", 
					
					prefix + "() body { int x;  }",
					
					options
				);
		}
	}
	
	public void testIndentInOutBodyCompareToHeader() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INDENT_IN_OUT_BODY_COMPARE_TO_FUNCTION_HEADER, DefaultCodeFormatterConstants.TRUE);
		for(String prefix : prefixes) {
			assertFormat(
					prefix + "()\r\n" +
					"\tin {\r\n" +
					"\t}\r\n" +
					"\tout {\r\n" +
					"\t}\r\n" +
					"\tbody {\r\n" +
					"\t\tint x;\r\n" +
					"\t}", 
					
					prefix + "() in   {    } out {  } body { int x;  }",
					
					options
				);
		}
	}

}
