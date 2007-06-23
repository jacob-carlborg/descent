package descent.tests.format;

import java.util.HashMap;
import java.util.Map;

import descent.core.formatter.DefaultCodeFormatterConstants;

public abstract class AbstractFormatBrace_Test extends AbstractFormatter_Test {
	
	protected abstract String getFormattedPrefixForBrace();
	
	protected abstract String getUnformattedPrefixForBrace();
	
	protected abstract String getBracePositionOptionName();
	
	protected abstract String getIndentCompareToParentOptionName();
	
	@Override
	protected Map getDefaultOptions() {
		Map options = new HashMap();
		options.put(getIndentCompareToParentOptionName(), DefaultCodeFormatterConstants.TRUE);
		return options;
	}
	
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
	
	public void testDontIndentCompareToParent() throws Exception {
		Map options = new HashMap();
		options.put(getIndentCompareToParentOptionName(), DefaultCodeFormatterConstants.FALSE);
		assertFormat(
				getFormattedPrefixForBrace() + " {\r\n" +
				"int x;\r\n" +
				"}", 
				
				getUnformattedPrefixForBrace() + " {  int x;  }",
				
				options
			);
	}

}
