package descent.tests.format;

import java.util.HashMap;
import java.util.Map;

import descent.core.formatter.DefaultCodeFormatterConstants;

public class FormatTemplateDeclaration_Test extends AbstractFormatter_Test {
	
	@Override
	protected Map getDefaultOptions() {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_TEMPLATE_DECLARATION, DefaultCodeFormatterConstants.END_OF_LINE);
		return options;
	}
	
	public void testWithName() throws Exception {
		assertFormat(
				"template Type () {\r\n" +
				"}", 
				
				"template   Type()  {    }"
			);
	}
	
	public void testBracesNextLine() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_TEMPLATE_DECLARATION, DefaultCodeFormatterConstants.NEXT_LINE);
		assertFormat(
				"template Class ()\r\n" +
				"{\r\n" +
				"}", 
				
				"template   Class()  {    }",
				
				options
			);
	}
	
	public void testBracesNextLineShifted() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_TEMPLATE_DECLARATION, DefaultCodeFormatterConstants.NEXT_LINE_SHIFTED);
		assertFormat(
				"template Class ()\r\n" +
				"\t{\r\n" +
				"\t}", 
				
				"template  Class()  {    }",
				
				options
			);
	}
	
	public void testWithComments() throws Exception {
		assertFormat(
				"/*\r\n" +
				" * Some\r\n" +
				" * comment\r\n" +
				" */\r\n" +
				"template Type () { // comment\r\n" +
				"}", 
				
				"/*\r\n" +
				" * Some\r\n" +
				" * comment\r\n" +
				" */\r\n" +
				"template    Type()  { // comment\r\n   }"
			);
	}
	
	public void testBracesNextLineShiftedWithMembers() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_TEMPLATE_DECLARATION, DefaultCodeFormatterConstants.NEXT_LINE_SHIFTED);
		assertFormat(
				"template Class ()\r\n" +
				"\t{\r\n" +
				"\t\tint x;\r\n" +
				"\t}", 
				
				"template  Class ()  {  int x;  }",
				
				options
			);
	}
	

}
