package descent.tests.format;

import java.util.Map;

import descent.core.formatter.DefaultCodeFormatterConstants;

public class FormatIfStatement_Test extends AbstractFormatBraceElseInsideFunction_Test {
	
	@Override
	protected void addMoreOptions(Map options) {
		options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_CONDITIONAL_STATEMENT, DefaultCodeFormatterConstants.END_OF_LINE);
	}
	
	@Override
	protected String getBracePositionOptionName() {
		return DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_CONDITIONAL_STATEMENT;
	}
	
	@Override
	protected String getFormattedPrefixForBrace() {
		return "if(someVersion)";
	}
	
	@Override
	protected String getUnformattedPrefixForBrace() {
		return "if    (   someVersion   )";
	}
	
	// TODO Descent formatter: make it configurable to write the declaration with an end line
	// Also, a space is missing after (someVersion)
	public void testSingleDeclaration() throws Exception {
		assertFormat(
				getFormattedPrefixForBrace() + "\r\n" +
					"\tint x;", 
				
				getUnformattedPrefixForBrace() + "  int   x ;"
			);
	}

}
