package descent.tests.format;

import java.util.HashMap;
import java.util.Map;

import descent.core.formatter.DefaultCodeFormatterConstants;


public abstract class AbstractFormatBraceElseInsideFunction_Test extends AbstractFormatBraceInsideFunction_Test {
	
	@Override
	protected Map getDefaultOptions() {
		Map options = super.getDefaultOptions();
		options.put(getInsertNewLineBeforeElseOptionName(), DefaultCodeFormatterConstants.FALSE);
		options.put(getKeepElseConditionalOnSameLineOptionName(), DefaultCodeFormatterConstants.TRUE);
		return options;
	}
	
	protected abstract String getInsertNewLineBeforeElseOptionName();
	
	protected abstract String getKeepElseConditionalOnSameLineOptionName();
	
	public void testBracesAtEndOfLineWithElse() throws Exception {
		assertFormat(
				getFormattedPrefixForBrace() + " {\r\n" +
				"} else {\r\n" +
				"}", 
				
				getUnformattedPrefixForBrace() + "  {    }  else  {   }"
			);
	}
	
	public void testBracesNextLineWithElse() throws Exception {
		Map options = new HashMap();
		options.put(getBracePositionOptionName(), DefaultCodeFormatterConstants.NEXT_LINE);
		assertFormat(
				getFormattedPrefixForBrace() + "\r\n" +
				"{\r\n" +
				"} else\r\n" + 
				"{\r\n" +
				"}", 
				
				getUnformattedPrefixForBrace() + "  {    }  else  {   }",
				
				options
			);
	}
	
	public void testBracesNextLineShiftedWithElse() throws Exception {
		Map options = new HashMap();
		options.put(getBracePositionOptionName(), DefaultCodeFormatterConstants.NEXT_LINE_SHIFTED);
		assertFormat(
				getFormattedPrefixForBrace() + "\r\n" +
					"\t{\r\n" +
					"\t} else\r\n" +
					"\t{\r\n" +
					"\t}",
				
					getUnformattedPrefixForBrace() + "  {    }  else  {   }",
				
				options
			);
	}
	
	public void testBracesNextLineShiftedWithMembersWithElse() throws Exception {
		Map options = new HashMap();
		options.put(getBracePositionOptionName(), DefaultCodeFormatterConstants.NEXT_LINE_SHIFTED);
		assertFormat(
				getFormattedPrefixForBrace() + "\r\n" +
					"\t{\r\n" +
						"\t\tint x;\r\n" +
					"\t} else\r\n" +
					"\t{\r\n" +
						"\t\tfloat x;\r\n" +
					"\t}", 
				
				getUnformattedPrefixForBrace() + " {  int x;  }  else   {   float  x ; }",
				
				options
			);
	}
	
	public void testInsertNewLineBeforeElseBracesAtEndOfLine() throws Exception {
		Map options = new HashMap();
		options.put(getInsertNewLineBeforeElseOptionName(), DefaultCodeFormatterConstants.TRUE);
		assertFormat(
				getFormattedPrefixForBrace() + " {\r\n" +
				"}\r\n" +
				"else {\r\n" +
				"}", 
				
				getUnformattedPrefixForBrace() + "  {    }  else  {   }",
				
				options
			);
	}
	
	public void testInsertNewLineBeforeElseBracesNextLineWithElse() throws Exception {
		Map options = new HashMap();
		options.put(getBracePositionOptionName(), DefaultCodeFormatterConstants.NEXT_LINE);
		options.put(getInsertNewLineBeforeElseOptionName(), DefaultCodeFormatterConstants.TRUE);
		assertFormat(
				getFormattedPrefixForBrace() + "\r\n" +
				"{\r\n" +
				"}\r\n" +
				"else\r\n" + 
				"{\r\n" +
				"}", 
				
				getUnformattedPrefixForBrace() + "  {    }  else  {   }",
				
				options
			);
	}
	
	public void testInsertNewLineBeforeElseBracesNextLineShiftedWithMembersWithElse() throws Exception {
		Map options = new HashMap();
		options.put(getBracePositionOptionName(), DefaultCodeFormatterConstants.NEXT_LINE_SHIFTED);
		options.put(getInsertNewLineBeforeElseOptionName(), DefaultCodeFormatterConstants.TRUE);
		assertFormat(
				getFormattedPrefixForBrace() + "\r\n" +
					"\t{\r\n" +
						"\t\tint x;\r\n" +
					"\t}\r\n" +
				"else\r\n" +
					"\t{\r\n" +
						"\t\tfloat x;\r\n" +
					"\t}", 
				
				getUnformattedPrefixForBrace() + " {  int x;  }  else   {   float  x ; }",
				
				options
			);
	}
	
	public void testDontKeepElseConditionalOnOneLine() throws Exception {
		Map options = new HashMap();
		options.put(getKeepElseConditionalOnSameLineOptionName(), DefaultCodeFormatterConstants.FALSE);
		assertFormat(
				getFormattedPrefixForBrace() + " {\r\n" +
				"} else\r\n" +
				getFormattedPrefixForBrace() + " {\r\n" +
				"}", 
				
				getUnformattedPrefixForBrace() + "  {    }  else " + getUnformattedPrefixForBrace() + " {   }",
				
				options
			);
	}

}
