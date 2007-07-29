package descent.tests.format;

import java.util.HashMap;
import java.util.Map;

import descent.core.formatter.DefaultCodeFormatterConstants;

public class FormatSwitchStatement_Test extends AbstractFormatBraceWithSingleInsideFunction_Test {
	
	@Override
	protected Map getDefaultOptions() {
		Map options = super.getDefaultOptions();
		options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_SWITCH_STATEMENT, DefaultCodeFormatterConstants.END_OF_LINE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_SWITCH_STATEMENTS, DefaultCodeFormatterConstants.END_OF_LINE);
		return options;
	}
	
	@Override
	protected String getBracePositionOptionName() {
		return DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_SWITCH_STATEMENT;
	}
	
	@Override
	protected String getKeepSimpleStatementInSameLineOption() {
		return null;
	}
	
	@Override
	protected String getFormattedPrefixForBrace() {
		return "switch(1)";
	}
	
	@Override
	protected String getUnformattedPrefixForBrace() {
		return "switch   (   1   )";
	}

	public void test_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_SWITCH_STATEMENTS() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_SWITCH_STATEMENTS, DefaultCodeFormatterConstants.TRUE);
		assertFormat(
				"switch (1) {\r\n" +
				"}\r\n",
				
				"switch(1) { }",
				
				options
				);
	}

}
