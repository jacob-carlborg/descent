package descent.tests.format;

import java.util.HashMap;
import java.util.Map;

import descent.core.formatter.DefaultCodeFormatterConstants;

public abstract class AbstractFormatBraceInsideFunction_Test extends AbstractFormatInsideFunction_Test {
	
	protected abstract String getFormattedPrefixForBrace();
	
	protected abstract String getUnformattedPrefixForBrace();
	
	protected abstract String getBracePositionOptionName();
	
	public void testBracesAtEndOfLine() throws Exception {
		assertFormat(
				getFormattedPrefixForBrace() + " {\r\n" +
				"}", 
				
				getUnformattedPrefixForBrace() + " {    }"
			);
	}
	
	public void testBracesNextLine() throws Exception {
		Map options = new HashMap();
		options.put(getBracePositionOptionName(), DefaultCodeFormatterConstants.NEXT_LINE);
		assertFormat(
				getFormattedPrefixForBrace() + "\r\n" +
				"{\r\n" +
				"}", 
				
				getUnformattedPrefixForBrace() + "  {    }",
				
				options
			);
	}
	
	public void testBracesNextLineShifted() throws Exception {
		Map options = new HashMap();
		options.put(getBracePositionOptionName(), DefaultCodeFormatterConstants.NEXT_LINE_SHIFTED);
		assertFormat(
				getFormattedPrefixForBrace() + "\r\n" +
					"\t{\r\n" +
					"\t}", 
				
				getUnformattedPrefixForBrace() + "  {    }",
				
				options
			);
	}
	
	public void testWithComments() throws Exception {
		assertFormat(
				"/*\r\n" +
				" * Some\r\n" +
				" * comment\r\n" +
				" */\r\n" +
				getFormattedPrefixForBrace() + " { // comment\r\n" +
				"}", 
				
				"/*\r\n" +
				" * Some\r\n" +
				" * comment\r\n" +
				" */\r\n" +
				getUnformattedPrefixForBrace() +  "  { // comment\r\n   }"
			);
	}
	
	public void testBracesNextLineShiftedWithMembers() throws Exception {
		Map options = new HashMap();
		options.put(getBracePositionOptionName(), DefaultCodeFormatterConstants.NEXT_LINE_SHIFTED);
		assertFormat(
				getFormattedPrefixForBrace() + "\r\n" +
					"\t{\r\n" +
					"\t\tint x;\r\n" +
					"\t}", 
				
				getUnformattedPrefixForBrace() +  " {  int x;  }",
				
				options
			);
	}

}
