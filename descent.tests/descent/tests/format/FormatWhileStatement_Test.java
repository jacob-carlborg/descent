package descent.tests.format;

import java.util.Map;

import descent.core.formatter.DefaultCodeFormatterConstants;

public class FormatWhileStatement_Test extends AbstractFormatBraceInsideFunction_Test {
	
	@Override
	protected void addMoreOptions(Map options) {
		options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_LOOP_STATEMENT, DefaultCodeFormatterConstants.END_OF_LINE);
	}
	
	@Override
	protected String getBracePositionOptionName() {
		return DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_LOOP_STATEMENT;
	}
	
	@Override
	protected String getFormattedPrefixForBrace() {
		return "while(true)";
	}
	
	@Override
	protected String getUnformattedPrefixForBrace() {
		return "while   (   true   )";
	}
	
	// TODO Descent formatter: make it configurable to write the substatement with an end line
	// Also, a space is missing after while(true)
	public void testSingleStatement() throws Exception {
		assertFormat(
				"while(true)\r\n" +
					"\tint x;", 
				
					"while   (   true   )  int   x ;"
			);
	}

}
