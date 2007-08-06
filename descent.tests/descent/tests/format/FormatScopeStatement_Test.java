package descent.tests.format;

import java.util.HashMap;
import java.util.Map;

import descent.core.formatter.DefaultCodeFormatterConstants;

public class FormatScopeStatement_Test extends AbstractFormatBraceWithSingleInsideFunction_Test {
	
	@Override
	protected Map getDefaultOptions() {
		Map options = super.getDefaultOptions();
		options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_SCOPE_STATEMENT, DefaultCodeFormatterConstants.END_OF_LINE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_SCOPE_STATEMENTS, DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_SCOPE_STATEMENTS, DefaultCodeFormatterConstants.FALSE);
		return options;
	}
	
	@Override
	protected String getBracePositionOptionName() {
		return DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_SCOPE_STATEMENT;
	}
	
	@Override
	protected String getKeepSimpleStatementInSameLineOption() {
		return DefaultCodeFormatterConstants.FORMATTER_KEEP_SIMPLE_SCOPE_STATEMENT_ON_SAME_LINE;
	}
	
	@Override
	protected String getFormattedPrefixForBrace() {
		return "scope(exit)";
	}
	
	@Override
	protected String getUnformattedPrefixForBrace() {
		return "scope ( exit ) ";
	}
	
	public void testINSERT_SPACE_BEFORE_OPENING_PAREN_IN_SCOPE_STATEMENTS() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_SCOPE_STATEMENTS, DefaultCodeFormatterConstants.TRUE);
		assertFormat(
				"scope (exit) {\r\n" +
				"}\r\n",
				
				"scope(exit) { }",
				
				options
				);
	}
	
	public void testINSERT_SPACE_AFTER_OPENING_PAREN_IN_SCOPE_STATEMENTS() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_SCOPE_STATEMENTS, DefaultCodeFormatterConstants.TRUE);
		assertFormat(
				"scope( exit) {\r\n" +
				"}\r\n",
				
				"scope(exit) { }",
				
				options
				);
	}

}
