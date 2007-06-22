package descent.tests.format;

import java.util.Map;

import descent.core.formatter.DefaultCodeFormatterConstants;

public class FormatSwitchStatement_Test extends AbstractFormatBraceWithSingleInsideFunction_Test {
	
	@Override
	protected Map getDefaultOptions() {
		Map options = super.getDefaultOptions();
		options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_SWITCH_STATEMENT, DefaultCodeFormatterConstants.END_OF_LINE);
		return options;
	}
	
	@Override
	protected String getBracePositionOptionName() {
		return DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_SWITCH_STATEMENT;
	}
	
	@Override
	protected String getKeepSimpleStatementInSameLineOption() {
		return DefaultCodeFormatterConstants.FORMATTER_KEEP_SIMPLE_SWITCH_STATEMENT_ON_SAME_LINE;
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
