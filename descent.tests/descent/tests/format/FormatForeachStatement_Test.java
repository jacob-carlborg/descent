package descent.tests.format;

import java.util.HashMap;
import java.util.Map;

import descent.core.formatter.DefaultCodeFormatterConstants;

public class FormatForeachStatement_Test extends AbstractFormatBraceWithSingleInsideFunction_Test {
	
	@Override
	protected Map getDefaultOptions() {
		Map options = super.getDefaultOptions();
		options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_LOOP_STATEMENT, DefaultCodeFormatterConstants.END_OF_LINE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_SEMICOLON_IN_FOREACH_STATEMENT, DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_SEMICOLON_IN_FOREACH_STATEMENT, DefaultCodeFormatterConstants.TRUE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_FOREACH_STATEMENT, DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_FOREACH_STATEMENT, DefaultCodeFormatterConstants.TRUE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_FOREACH_LOOPS, DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_OUT_DECLARATION, DefaultCodeFormatterConstants.FALSE);
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
		return "foreach(element1, element2; collection)";
	}
	
	@Override
	protected String getUnformattedPrefixForBrace() {
		return "foreach   (    element1  ,   element2   ;    collection   )";
	}
	
	public void testInsertSpaceBeforeSemicolon() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_SEMICOLON_IN_FOREACH_STATEMENT, DefaultCodeFormatterConstants.TRUE);
		assertFormat(
				"foreach(x, y ; z) {\r\n" +
				"}\r\n",
				
				"foreach(x, y; z) { }",
				
				options
				);
	}
	
	public void testDontInsertSpaceAfterSemicolon() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_SEMICOLON_IN_FOREACH_STATEMENT, DefaultCodeFormatterConstants.FALSE);
		assertFormat(
				"foreach(x, y;z) {\r\n" +
				"}\r\n",
				
				"foreach(x, y; z) { }",
				
				options
				);
	}
	
	public void testInsertSpaceBeforeComma() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_FOREACH_STATEMENT, DefaultCodeFormatterConstants.TRUE);
		assertFormat(
				"foreach(x , y; z) {\r\n" +
				"}\r\n",
				
				"foreach(x , y; z) { }",
				
				options
				);
	}
	
	public void testDontInsertSpaceAfterComma() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_FOREACH_STATEMENT, DefaultCodeFormatterConstants.FALSE);
		assertFormat(
				"foreach(x,y; z) {\r\n" +
				"}\r\n",
				
				"foreach(x, y; z) { }",
				
				options
				);
	}
	
	public void testINSERT_SPACE_BEFORE_OPENING_PAREN() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_FOREACH_LOOPS, DefaultCodeFormatterConstants.TRUE);
		assertFormat(
				"foreach (x, y; z) {\r\n" +
				"}\r\n",
				
				"foreach(x, y; z) { }",
				
				options
				);
	}
	
	public void testINSERT_SPACE_AFTER_OPENING_PAREN() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_FOREACH_LOOPS, DefaultCodeFormatterConstants.TRUE);
		assertFormat(
				"foreach( x, y; z) {\r\n" +
				"}\r\n",
				
				"foreach(x, y; z) { }",
				
				options
				);
	}
	
	public void testArgumentModifier() throws Exception {
		Map options = new HashMap();
		assertFormat(
				"foreach(inout int x; z) {\r\n" +
				"}\r\n",
				
				"foreach(inout int x; z) {\r\n" +
				"}",
				
				options
				);
	}
	
	public void testArgumentModifierTypeInference() throws Exception {
		Map options = new HashMap();
		assertFormat(
				"foreach(inout x; z) {\r\n" +
				"}\r\n",
				
				"foreach(inout x; z) {\r\n" +
				"}",
				
				options
				);
	}
	
}
