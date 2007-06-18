package descent.tests.format;

import java.util.HashMap;
import java.util.Map;

import descent.core.formatter.DefaultCodeFormatterConstants;

public class FormatEnumDeclaration_Test extends AbstractFormatter_Test {
	
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

}
