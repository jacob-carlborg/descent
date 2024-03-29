package descent.tests.format;

import java.util.HashMap;
import java.util.Map;

import descent.core.formatter.DefaultCodeFormatterConstants;

public class FormatConditionalDeclaration_Test extends AbstractFormatter_Test {
	
	private final String[] conditionals = { "version", "debug", "static if" }; 
	
	@Override
	protected Map getDefaultOptions() {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_CONDITIONAL_DECLARATION, DefaultCodeFormatterConstants.END_OF_LINE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_BEFORE_ELSE_DECLARATION, DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_KEEP_SIMPLE_THEN_DECLARATION_ON_SAME_LINE, DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_KEEP_SIMPLE_ELSE_DECLARATION_ON_SAME_LINE, DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_VERSION_DEBUG, DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_VERSION_DEBUG, DefaultCodeFormatterConstants.FALSE);
		return options;
	}
	
	public void testBracesAtEndOfLine() throws Exception {
		for(String cond : conditionals) {
			assertFormat(
					cond + "(someVersion) {\r\n" +
					"}", 
					
					cond + "    (   someVersion   )  {    }"
				);
		}
	}
	
	public void testBracesNextLine() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_CONDITIONAL_DECLARATION, DefaultCodeFormatterConstants.NEXT_LINE);
		for(String cond : conditionals) {
			assertFormat(
					cond + "(someVersion)\r\n" +
					"{\r\n" +
					"}", 
					
					cond + "    (   someVersion   )  {    }",
					
					options
				);
		}
	}
	
	public void testBracesNextLineShifted() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_CONDITIONAL_DECLARATION, DefaultCodeFormatterConstants.NEXT_LINE_SHIFTED);
		for(String cond : conditionals) {
			assertFormat(
					cond + "(someVersion)\r\n" +
						"\t{\r\n" +
						"\t}", 
					
					cond + "    (   someVersion   )  {    }",
					
					options
				);
		}
	}
	
	public void testWithComments() throws Exception {
		for(String cond : conditionals) {
			assertFormat(
					"/*\r\n" +
					" * Some\r\n" +
					" * comment\r\n" +
					" */\r\n" +
					cond + "(someVersion) { // comment\r\n" +
					"}", 
					
					"/*\r\n" +
					" * Some\r\n" +
					" * comment\r\n" +
					" */\r\n" +
					cond + "    (   someVersion   )  { // comment\r\n   }"
				);
		}
	}
	
	public void testBracesNextLineShiftedWithMembers() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_CONDITIONAL_DECLARATION, DefaultCodeFormatterConstants.NEXT_LINE_SHIFTED);
		for(String cond : conditionals) {
			assertFormat(
					cond + "(someVersion)\r\n" +
						"\t{\r\n" +
						"\t\tint x;\r\n" +
						"\t}", 
					
					cond + "(someVersion) {  int x;  }",
					
					options
				);
		}
	}
	
	public void testSingleDeclaration() throws Exception {
		for(String cond : conditionals) {
			assertFormat(
					cond + "(someVersion)\r\n" +
						"\tint x;", 
					
					cond + "    (   someVersion   )  int   x ;"
				);
		}
	}
	
	public void testBracesAtEndOfLineWithElse() throws Exception {
		for(String cond : conditionals) {
			assertFormat(
					cond + "(someVersion) {\r\n" +
					"} else {\r\n" +
					"}", 
					
					cond + "    (   someVersion   )  {    }  else  {   }"
				);
		}
	}
	
	public void testBracesNextLineWithElse() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_CONDITIONAL_DECLARATION, DefaultCodeFormatterConstants.NEXT_LINE);
		for(String cond : conditionals) {
			assertFormat(
					cond + "(someVersion)\r\n" +
					"{\r\n" +
					"} else\r\n" + 
					"{\r\n" +
					"}", 
					
					cond + "    (   someVersion   )  {    }  else  {   }",
					
					options
				);
		}
	}
	
	public void testBracesNextLineShiftedWithElse() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_CONDITIONAL_DECLARATION, DefaultCodeFormatterConstants.NEXT_LINE_SHIFTED);
		for(String cond : conditionals) {
			assertFormat(
					cond + "(someVersion)\r\n" +
						"\t{\r\n" +
						"\t} else\r\n" +
						"\t{\r\n" +
						"\t}",
					
					cond + "    (   someVersion   )  {    }  else  {   }",
					
					options
				);
		}
	}
	
	public void testBracesNextLineShiftedWithMembersWithElse() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_CONDITIONAL_DECLARATION, DefaultCodeFormatterConstants.NEXT_LINE_SHIFTED);
		for(String cond : conditionals) {
			assertFormat(
					cond + "(someVersion)\r\n" +
						"\t{\r\n" +
							"\t\tint x;\r\n" +
						"\t} else\r\n" +
						"\t{\r\n" +
							"\t\tfloat x;\r\n" +
						"\t}", 
					
					cond + "(someVersion) {  int x;  }  else   {   float  x ; }",
					
					options
				);
		}
	}
	
	public void testDontKeepElseConditionalOnOneLine() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_KEEP_ELSE_VERSION_DEBUG_ON_ONE_LINE, DefaultCodeFormatterConstants.FALSE);
		for(String cond : conditionals) {
			assertFormat(
					cond + "(someVersion) {\r\n" +
					"} else\r\n" +
					cond + "(someVersion) {\r\n" +
					"}", 
					
					cond + "(someVersion)  {    }  else " + cond + "(someVersion) {   }",
					
					options
				);
		}
	}
	
	public void testKeepElseConditionalOnOneLineWithBraces() throws Exception {
		for(String cond : conditionals) {
			assertFormat(
					cond + "(someVersion) {\r\n" +
					"} else {\r\n" +
						"\t" + cond + "(someVersion) {\r\n" +
						"\t}\r\n" +
					"}", 
					
					cond + "(someVersion)  {    }  else { " + cond + "(someVersion) {   } }"
				);
		}
	}
	
	public void testDontKeepSimpleThenInSameLine() throws Exception {
		for(String cond : conditionals) {
			assertFormat(
					cond + "(someVersion)\r\n" +
						"\tint x;", 
					
						cond + "(someVersion)  int   x ;"
				);
		}
	}
	
	public void testKeepSimpleThenInSameLine() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_KEEP_SIMPLE_THEN_DECLARATION_ON_SAME_LINE, DefaultCodeFormatterConstants.TRUE);
		for(String cond : conditionals) {
			assertFormat(
					cond + "(someVersion) int x;", 
					
					cond + "(someVersion)  int   x ;",
						
					options
			);
		}
	}
	
	public void testDontKeepSimpleElseInSameLine() throws Exception {
		for(String cond : conditionals) {
			assertFormat(
					cond + "(someVersion)\r\n" +
						"\tint x;\r\n" +
						"else\r\n" +
						"\tfloat x;", 
					
					cond + "(someVersion)  int   x ; else float x;"
				);
		}
	}
	
	public void testKeepSimpleElseInSameLine() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_KEEP_SIMPLE_ELSE_DECLARATION_ON_SAME_LINE, DefaultCodeFormatterConstants.TRUE);
		for(String cond : conditionals) {
			assertFormat(
					cond + "(someVersion)\r\n" +
						"\tint x;\r\n" +
						"else float x;", 
					
					cond + "(someVersion)  int   x ; else float x;",
					
					options
				);
		}
	}
	
	public void testInsertNewLineBeforeElse() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_BEFORE_ELSE_DECLARATION, DefaultCodeFormatterConstants.TRUE);
		for(String cond : conditionals) {
			assertFormat(
					cond + "(someVersion) {\r\n" +
					"}\r\n" +
					"else {\r\n" +
					"}", 
					
					cond + "(someVersion)  {    }  else  {   }",
					
					options
				);
		}
	}
	
	public void testKeepSimpleElseInSameLineProblem() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_KEEP_SIMPLE_ELSE_DECLARATION_ON_SAME_LINE, DefaultCodeFormatterConstants.TRUE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_BEFORE_ELSE_DECLARATION, DefaultCodeFormatterConstants.TRUE);
		for(String cond : conditionals) {
			assertFormat(
					cond + "(someVersion)\r\n" +
						"\tint x;\r\n" +
					"else float x;", 
					
					cond + "(someVersion)  int   x ; else float x;",
					
					options
				);
		}
	}
	
	public void testKeepSimpleThenAndElseInSameLine() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_KEEP_SIMPLE_THEN_DECLARATION_ON_SAME_LINE, DefaultCodeFormatterConstants.TRUE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_KEEP_SIMPLE_ELSE_DECLARATION_ON_SAME_LINE, DefaultCodeFormatterConstants.TRUE);
		for(String cond : conditionals) {
			assertFormat(
					cond + "(someVersion) int x;\r\n" +
					"else float x;", 
					
					cond + "(someVersion)  int   x ; else float x;",
					
					options
				);
		}
	}
	
	public void testFORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_VERSION_DEBUG() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_VERSION_DEBUG, DefaultCodeFormatterConstants.TRUE);
		for(String cond : conditionals) {
			if (cond.equals("static if")) continue;
			assertFormat(
					cond + " (someVersion) {\r\n" +
					"}", 
					
					cond + "    (   someVersion   )  {    }",
					
					options
				);
		}
	}
	
	public void testFORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_VERSION_DEBUG() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_VERSION_DEBUG, DefaultCodeFormatterConstants.TRUE);
		for(String cond : conditionals) {
			if (cond.equals("static if")) continue;
			assertFormat(
					cond + "( someVersion) {\r\n" +
					"}", 
					
					cond + "    (   someVersion   )  {    }",
					
					options
				);
		}
	}

}
