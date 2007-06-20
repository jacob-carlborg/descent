package descent.tests.format;

import java.util.Map;

import descent.core.formatter.DefaultCodeFormatterConstants;

public class FormatDebugStatement_Test extends AbstractFormatBraceElseWithSingleInsideFunction_Test {
	
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
		return "debug(someVersion)";
	}
	
	@Override
	protected String getUnformattedPrefixForBrace() {
		return "debug    (   someVersion   )";
	}

}
