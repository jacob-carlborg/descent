package descent.tests.format;

import java.util.HashMap;
import java.util.Map;

import descent.core.formatter.DefaultCodeFormatterConstants;

public class FormatDoStatement_Test extends AbstractFormatInsideFunction_Test {
	
	@Override
	protected Map getDefaultOptions() {
		Map options = super.getDefaultOptions();
		options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_LOOP_STATEMENT, DefaultCodeFormatterConstants.END_OF_LINE);
		return options;
	}
	
	// TODO Descent formatter: make it configurable to write the "while" without an end line
	public void testBracesAtEndOfLine() throws Exception {
		assertFormat(
				"do {\r\n" +
				"}\r\n" +
				"while(true);", 
				
				"do {   }   while   (   true   );"
			);
	}
	
	public void testBracesNextLine() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_LOOP_STATEMENT, DefaultCodeFormatterConstants.NEXT_LINE);
		assertFormat(
				"do\r\n" +
				"{\r\n" +
				"}\r\n" +
				"while(true);", 
				
				"do  {    }   while   (   true   );",
				
				options
			);
	}
	
	public void testBracesNextLineShifted() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_LOOP_STATEMENT, DefaultCodeFormatterConstants.NEXT_LINE_SHIFTED);
		assertFormat(
				"do\r\n" +
					"\t{\r\n" +
					"\t}\r\n" +
				"while(true);", 
				
				"do  {    }    while   (   true   );",
				
				options
			);
	}
	
	public void testWithComments() throws Exception {
		assertFormat(
				"/*\r\n" +
				" * Some\r\n" +
				" * comment\r\n" +
				" */\r\n" +
				"do { // comment\r\n" +
				"}\r\n" +
				"while(true);", 
				
				"/*\r\n" +
				" * Some\r\n" +
				" * comment\r\n" +
				" */\r\n" +
				"do  { // comment\r\n   } while   (   true   );"
			);
	}
	
	public void testBracesNextLineShiftedWithMembers() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_LOOP_STATEMENT, DefaultCodeFormatterConstants.NEXT_LINE_SHIFTED);
		assertFormat(
				"do\r\n" +
					"\t{\r\n" +
					"\t\tint x;\r\n" +
					"\t}\r\n" +
				"while(true);", 
				
				"do {  int x;  }  while(true);",
				
				options
			);
	}

}
