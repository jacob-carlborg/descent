package descent.tests.format;

import java.util.HashMap;
import java.util.Map;

import descent.core.formatter.DefaultCodeFormatterConstants;

public class FormatEnumDeclaration_Test extends AbstractFormatter_Test {
	
	@Override
	protected Map getDefaultOptions() {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_ENUM_DECLARATION, DefaultCodeFormatterConstants.END_OF_LINE);
		return options;
	}
	
	public void testEmptyAnonymous() throws Exception {
		assertFormat(
				"enum {\r\n" +
				"}", 
				
				"enum   {    }"
			);
	}
	
	public void testWithName() throws Exception {
		assertFormat(
				"enum Enum {\r\n" +
				"}", 
				
				"enum  Enum  {    }"
			);
	}
	
	public void testBracesNextLine() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_ENUM_DECLARATION, DefaultCodeFormatterConstants.NEXT_LINE);
		assertFormat(
				"enum Enum\r\n" +
				"{\r\n" +
				"}", 
				
				"enum  Enum  {    }",
				
				options
			);
	}
	
	public void testBracesNextLineShifted() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_ENUM_DECLARATION, DefaultCodeFormatterConstants.NEXT_LINE_SHIFTED);
		assertFormat(
				"enum Enum\r\n" +
				"\t{\r\n" +
				"\t}", 
				
				"enum  Enum  {    }",
				
				options
			);
	}
	
	public void testMembers() throws Exception {
		assertFormat(
				"enum Enum {\r\n" +
				"\tx,\r\n" +
				"\ty,\r\n" +
				"\tz\r\n" +
				"}", 
				
				"enum  Enum  {  x  ,   y   ,   z   }"
			);
	}
	
	public void testBracesNextLineShiftedWithMembers() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_ENUM_DECLARATION, DefaultCodeFormatterConstants.NEXT_LINE_SHIFTED);
		assertFormat(
				"enum Enum\r\n" +
				"\t{\r\n" +
				"\t\tx,\r\n" +
				"\t\ty,\r\n" +
				"\t\tz\r\n" +
				"\t}", 
				
				"enum  Enum  {  x  ,   y   ,   z   }",
				
				options
			);
	}
	
	public void testWithComments() throws Exception {
		assertFormat(
				"/*\r\n" +
				" * Some\r\n" +
				" * comment\r\n" +
				" */\r\n" +
				"enum Enum { // comment\r\n" +
				"\tx, // comment\r\n" +
				"\ty, // comment\r\n" +
				"\tz // comment\r\n" +
				"}", 
				
				"/*\r\n" +
				" * Some\r\n" +
				" * comment\r\n" +
				" */\r\n" +
				"enum  Enum  { // comment\r\n x  , // comment\r\n  y   , // comment\r\n  z  // comment\r\n }"
			);
	}
	

}
