package descent.tests.format;

import java.util.HashMap;
import java.util.Map;

import descent.core.formatter.DefaultCodeFormatterConstants;

public class FormatTypeDeclaration_Test extends AbstractFormatter_Test {
	
	private final static String[] anonymous = { "struct", "union" };
	private final static String[] types = { "class", "interface", "struct", "union" };
	
	@Override
	protected Map getDefaultOptions() {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_TYPE_DECLARATION, DefaultCodeFormatterConstants.END_OF_LINE);
		return options;
	}
	
	public void testAnonymous() throws Exception {
		for(String type : anonymous) {
			assertFormat(
					type + " {\r\n" +
					"}", 
					
					type + "   {    }"
				);
		}
	}
	
	public void testWithName() throws Exception {
		for(String type : types) {
			assertFormat(
					type + " Type {\r\n" +
					"}", 
					
					type + "   Type  {    }"
				);
		}
	}
	
	public void testBracesNextLine() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_TYPE_DECLARATION, DefaultCodeFormatterConstants.NEXT_LINE);
		for(String type : types) {
			assertFormat(
					type + " Class\r\n" +
					"{\r\n" +
					"}", 
					
					type + "   Class  {    }",
					
					options
				);
		}
	}
	
	public void testBracesNextLineShifted() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_TYPE_DECLARATION, DefaultCodeFormatterConstants.NEXT_LINE_SHIFTED);
		for(String type : types) {
			assertFormat(
					type + " Class\r\n" +
						"\t{\r\n" +
						"\t}", 
					
					type + "  Class  {    }",
					
					options
				);
		}
	}
	
	public void testWithComments() throws Exception {
		for(String type : types) {
			assertFormat(
					"/*\r\n" +
					" * Some\r\n" +
					" * comment\r\n" +
					" */\r\n" +
					type + " Type { // comment\r\n" +
					"}", 
					
					"/*\r\n" +
					" * Some\r\n" +
					" * comment\r\n" +
					" */\r\n" +
					type + "    Type  { // comment\r\n   }"
				);
		}
	}
	
	public void testBracesNextLineShiftedWithMembers() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_TYPE_DECLARATION, DefaultCodeFormatterConstants.NEXT_LINE_SHIFTED);
		for(String type : types) {
			assertFormat(
					type + " Class\r\n" +
						"\t{\r\n" +
							"\t\tint x;\r\n" +
						"\t}", 
					
					type + "  Class  {  int x;  }",
					
					options
				);
		}
	}
	

}
