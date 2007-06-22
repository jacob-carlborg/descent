package descent.tests.format;

import java.util.Map;

import descent.core.formatter.DefaultCodeFormatterConstants;

public class FormatVersionStatement_Test extends AbstractFormatBraceElseWithSingleInsideFunction_Test {
	
	@Override
	protected Map getDefaultOptions() {
		Map options = super.getDefaultOptions();
		options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_CONDITIONAL_STATEMENT, DefaultCodeFormatterConstants.END_OF_LINE);
		return options;
	}
	
	@Override
	protected String getBracePositionOptionName() {
		return DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_CONDITIONAL_STATEMENT;
	}
	
	@Override
	protected String getKeepSimpleThenInSameLineOption() {
		return DefaultCodeFormatterConstants.FORMATTER_KEEP_SIMPLE_THEN_STATEMENT_ON_SAME_LINE;
	}
	
	@Override
	protected String getInsertNewLineBeforeElseOptionName() {
		return DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_BEFORE_ELSE;
	}
	
	@Override
	protected String getSimpleElseStatementInSameLineOption() {
		return DefaultCodeFormatterConstants.FORMATTER_KEEP_SIMPLE_ELSE_STATEMENT_ON_SAME_LINE;
	}
	
	@Override
	protected String getKeepElseConditionalOnSameLineOptionName() {
		return DefaultCodeFormatterConstants.FORMATTER_KEEP_ELSE_CONDITIONAL_ON_ONE_LINE;
	}
	
	@Override
	protected String getFormattedPrefixForBrace() {
		return "version(someVersion)";
	}
	
	@Override
	protected String getUnformattedPrefixForBrace() {
		return "version    (   someVersion   )";
	}

}
