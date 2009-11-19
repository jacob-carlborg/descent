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
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_FUNCTION_TEMPLATE_ARGS, DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_FUNCTION_DECLARATION_PARAMETERS, DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_FUNCTION_DECLARATION_PARAMETERS, DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BETWEEN_EMPTY_PARENS_IN_FUNCTION_DECLARATION_PARAMETERS, DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_FUNCTION_DECLARATION, DefaultCodeFormatterConstants.END_OF_LINE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_KEEP_FUNCTIONS_WITH_NO_STATEMENT_IN_ONE_LINE, DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_KEEP_FUNCTIONS_WITH_ONE_STATEMENT_IN_ONE_LINE, DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INDENT_STATEMENTS_COMPARE_TO_FUNCTION_HEADER, DefaultCodeFormatterConstants.TRUE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INDENT_STATEMENTS_COMPARE_TO_FUNCTION_IN_HEADER, DefaultCodeFormatterConstants.TRUE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INDENT_STATEMENTS_COMPARE_TO_FUNCTION_OUT_HEADER, DefaultCodeFormatterConstants.TRUE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INDENT_STATEMENTS_COMPARE_TO_FUNCTION_BODY_HEADER, DefaultCodeFormatterConstants.TRUE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INDENT_IN_OUT_BODY_COMPARE_TO_FUNCTION_HEADER, DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_FUNCTION_TEMPLATE_ARGS, DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_OUT_DECLARATION, DefaultCodeFormatterConstants.FALSE);
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
	
	public void testEmptyInsertSpaceBeforeOpenParenInTemplateDont() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_FUNCTION_TEMPLATE_ARGS, DefaultCodeFormatterConstants.FALSE);
		assertFormat(
				"void bla(T)();", 
				
				"void bla(T)()  ;",
				
				options
			);
	}
	
	public void testEmptyInsertSpaceBeforeOpenParenInTemplate() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_FUNCTION_TEMPLATE_ARGS, DefaultCodeFormatterConstants.TRUE);
		assertFormat(
				"void bla (T)();", 
				
				"void bla(T)()  ;",
				
				options
			);
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
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BETWEEN_EMPTY_PARENS_IN_FUNCTION_DECLARATION_PARAMETERS, DefaultCodeFormatterConstants.TRUE);
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
	
	public void testBodyTrailingComment() throws Exception {
		for(String prefix : prefixes) {
			assertFormat(
					prefix + "() { // comment\r\n" +
					"}", 
					
					prefix + "()  {   // comment\r\n" +
					"}"
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
	
	public void testNotFORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_FUNCTION_TEMPLATE_ARGS() throws Exception {
		assertFormat(
				"void bla(T)(int x) {\r\n" +
				"}",
				"void bla(T)(int x) { }"
				);
	}
	
	public void testFORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_FUNCTION_TEMPLATE_ARGS() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_FUNCTION_TEMPLATE_ARGS, DefaultCodeFormatterConstants.TRUE);
		assertFormat(
				"void bla( T)(int x) {\r\n" +
				"}",
				"void bla(T)(int x) { }",
				options
				);
	}
	
	public void testNotFORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_OUT_DECLARATION() throws Exception {
		assertFormat(
				"void bla()\r\n" +
				"out(x) {\r\n" +
				"}\r\n" +
				"body {\r\n" +
				"}",
				"void bla() out(x) { } body { }"
				);
	}
	
	public void testFORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_OUT_DECLARATION() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_OUT_DECLARATION, DefaultCodeFormatterConstants.TRUE);
		assertFormat(
				"void bla()\r\n" +
				"out( x) {\r\n" +
				"}\r\n" +
				"body {\r\n" +
				"}",
				"void bla() out(x) { } body { }",
				options
				);
	}
	
	public void testModifierAfterFunction() throws Exception {
		Map options = new HashMap();
		assertFormat(
				"void bla() pure {\r\n" +
				"}",
				"void   bla  (  )   pure   {     }",
				options
				);
	}
	
	public void testUnittestBracesNextLine() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_UNITTEST, DefaultCodeFormatterConstants.NEXT_LINE);
		assertFormat(
				"unittest\r\n" +
				"{\r\n" +
				"}", 
				
				"unittest  {   }",
				
				options
			);
	}
	
	public void testUnittestBracesSameLine() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_UNITTEST, DefaultCodeFormatterConstants.END_OF_LINE);
		assertFormat(
				"unittest {\r\n" +
				"}", 
				
				"unittest  {   }",
				
				options
			);
	}
	
	public void testUnittestBracesNextLineShifted() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_UNITTEST, DefaultCodeFormatterConstants.NEXT_LINE_SHIFTED);
		assertFormat(
				"unittest\r\n" +
					"\t{\r\n" +
					"\t}", 
				
				"unittest  {   }",
				
				options
			);
	}
	
	public void testInvariantBracesNextLine() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_CLASS_INVARIANT, DefaultCodeFormatterConstants.NEXT_LINE);
		assertFormat(
				"invariant()\r\n" +
				"{\r\n" +
				"}", 
				
				"invariant()  {   }",
				
				options
			);
	}
	
	public void testInvariantBracesSameLine() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_CLASS_INVARIANT, DefaultCodeFormatterConstants.END_OF_LINE);
		assertFormat(
				"invariant() {\r\n" +
				"}", 
				
				"invariant()  {   }",
				
				options
			);
	}
	
	public void testInvariantBracesNextLineShifted() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_CLASS_INVARIANT, DefaultCodeFormatterConstants.NEXT_LINE_SHIFTED);
		assertFormat(
				"invariant()\r\n" +
					"\t{\r\n" +
					"\t}", 
				
				"invariant()  {   }",
				
				options
			);
	}
	
	public void testAutoFunction() throws Exception {
		Map options = new HashMap();
		assertFormat(
				"auto bla() {\r\n" +
				"}",
				"auto   bla  (  )   {     }",
				options
				);
	}
	
	public void testSemicolonAtTheEnd() throws Exception {
		Map options = new HashMap();
		assertFormat(
				"void bla() {\r\n" +
				"};",
				"void   bla  (  )   {     }   ;",
				options
				);
	}

}
