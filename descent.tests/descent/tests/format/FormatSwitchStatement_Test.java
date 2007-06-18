package descent.tests.format;

import java.util.Map;

import descent.core.formatter.DefaultCodeFormatterConstants;

public class FormatSwitchStatement_Test extends AbstractFormatBraceInsideFunction_Test {
	
	@Override
	protected void addMoreOptions(Map options) {
		options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_SWITCH_STATEMENT, DefaultCodeFormatterConstants.END_OF_LINE);
	}
	
	@Override
	protected String getBracePositionOptionName() {
		return DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_SWITCH_STATEMENT;
	}
	
	@Override
	protected String getFormattedPrefixForBrace() {
		return "switch(1)";
	}
	
	@Override
	protected String getUnformattedPrefixForBrace() {
		return "switch   (   1   )";
	}

}
