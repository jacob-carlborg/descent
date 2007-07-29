package descent.tests.format;

import java.util.HashMap;
import java.util.Map;

import descent.core.formatter.DefaultCodeFormatterConstants;

public class FormatForStatement_Test extends AbstractFormatBraceWithSingleInsideFunction_Test {
	
	@Override
	protected Map getDefaultOptions() {
		Map options = super.getDefaultOptions();
		options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_LOOP_STATEMENT, DefaultCodeFormatterConstants.END_OF_LINE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_SEMICOLON_IN_FOR_STATEMENT, DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_SEMICOLON_IN_FOR_STATEMENT, DefaultCodeFormatterConstants.TRUE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_FOR_LOOPS, DefaultCodeFormatterConstants.FALSE);
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
		return "for(int i = 0; i < 10; i++)";
	}
	
	@Override
	protected String getUnformattedPrefixForBrace() {
		return "for    (   int  i = 0; i  <  10  ; i  ++   )";
	}
	
	public void testInsertSpaceBeforeSemicolon() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_SEMICOLON_IN_FOR_STATEMENT, DefaultCodeFormatterConstants.TRUE);
		assertFormat(
				"for(int x = 0 ; i < 10 ; i++) {\r\n" +
				"}\r\n",
				
				"for(int x = 0; i < 10; i++) { }",
				
				options
				);
	}
	
	public void testInsertSpaceBeforeSemicolon2() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_SEMICOLON_IN_FOR_STATEMENT, DefaultCodeFormatterConstants.TRUE);
		assertFormat(
				"for(x = 0 ; i < 10 ; i++) {\r\n" +
				"}\r\n",
				
				"for(x = 0; i < 10; i++) { }",
				
				options
				);
	}
	
	public void testDontInsertSpaceAfterSemicolon() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_SEMICOLON_IN_FOR_STATEMENT, DefaultCodeFormatterConstants.FALSE);
		assertFormat(
				"for(int x = 0;i < 10;i++) {\r\n" +
				"}\r\n",
				
				"for(int x = 0; i < 10; i++) { }",
				
				options
				);
	}
	
	public void testINSERT_SPACE_BEFORE_OPENING_PAREN_IN_FOR_LOOPS() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_FOR_LOOPS, DefaultCodeFormatterConstants.TRUE);
		assertFormat(
				"for (int x = 0; i < 10; i++) {\r\n" +
				"}\r\n",
				
				"for(int x = 0; i < 10; i++) { }",
				
				options
				);
	}

}
