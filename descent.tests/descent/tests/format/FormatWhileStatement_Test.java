package descent.tests.format;

import java.util.HashMap;
import java.util.Map;

import descent.core.formatter.DefaultCodeFormatterConstants;

public class FormatWhileStatement_Test extends AbstractFormatBraceWithSingleInsideFunction_Test {
	
	@Override
	protected Map getDefaultOptions() {
		Map options = super.getDefaultOptions();
		options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_LOOP_STATEMENT, DefaultCodeFormatterConstants.END_OF_LINE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_WHILE_LOOPS, DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_WHILE_LOOPS, DefaultCodeFormatterConstants.FALSE);
		return options;
	}
	
	@Override
	protected String getBracePositionOptionName() {
		return DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_LOOP_STATEMENT;
	}
	
	@Override
	protected String getKeepSimpleStatementInSameLineOption() {
		return DefaultCodeFormatterConstants.FORMATTER_KEEP_SIMPLE_LOOP_STATEMENT_ON_SAME_LINE;
	}
	
	@Override
	protected String getFormattedPrefixForBrace() {
		return "while(true)";
	}
	
	@Override
	protected String getUnformattedPrefixForBrace() {
		return "while   (   true   )";
	}
	
	public void testINSERT_SPACE_BEFORE_OPENING_PAREN_IN_FOR_LOOPS() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_WHILE_LOOPS, DefaultCodeFormatterConstants.TRUE);
		assertFormat(
				"while (true) {\r\n" +
				"}\r\n",
				
				"while(true) { }",
				
				options
				);
	}
	
	public void testNotFORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_WHILE_LOOPS() throws Exception {
		assertFormat(
				"while(true) {\r\n" +
				"}",
				"while(true) { }"
				);
	}
	
	public void testFORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_WHILE_LOOPS() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_WHILE_LOOPS, DefaultCodeFormatterConstants.TRUE);
		assertFormat(
				"while( true) {\r\n" +
				"}",
				"while(true) { }",
				options
				);
	}

}
