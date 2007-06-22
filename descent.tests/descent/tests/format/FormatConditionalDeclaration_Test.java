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
	
	// TODO Descent formatter: make it configurable to write the declaration without an end line
	public void testSingleDeclaration() throws Exception {
		for(String cond : conditionals) {
			assertFormat(
					cond + "(someVersion)\r\n" +
						"\tint x;", 
					
					cond + "    (   someVersion   )  int   x ;"
				);
		}
	}
	
	// TODO Descent formatter: make it configurable to write the "else" without an end line
	public void testBracesAtEndOfLineWithElse() throws Exception {
		for(String cond : conditionals) {
			assertFormat(
					cond + "(someVersion) {\r\n" +
					"}\r\n" +
					"else {\r\n" +
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
					"}\r\n" +
					"else\r\n" + 
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
						"\t}\r\n" +
					"else\r\n" +
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
						"\t}\r\n" +
					"else\r\n" +
						"\t{\r\n" +
							"\t\tfloat x;\r\n" +
						"\t}", 
					
					cond + "(someVersion) {  int x;  }  else   {   float  x ; }",
					
					options
				);
		}
	}
	

}
