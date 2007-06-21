package descent.tests.format;

import java.util.Map;

import descent.core.formatter.DefaultCodeFormatterConstants;


public abstract class AbstractFormatBraceElseWithSingleInsideFunction_Test extends AbstractFormatBraceElseInsideFunction_Test {
	
	@Override
	protected Map getDefaultOptions() {
		Map options = super.getDefaultOptions();
		options.put(getInsertNewLineInSimpleStatementOption(), DefaultCodeFormatterConstants.TRUE);
		return options;
	}
	
	protected abstract String getInsertNewLineInSimpleStatementOption();
	
	public void testNewLineInSingleStatement() throws Exception {
		assertFormat(
				getFormattedPrefixForBrace() + "\r\n" +
					"\tint x;", 
				
				getUnformattedPrefixForBrace() + "  int   x ;"
			);
	}
	
	public void testNoNewLineInSingleStatement() throws Exception {
		Map options = getDefaultOptions();
		options.put(getInsertNewLineInSimpleStatementOption(), DefaultCodeFormatterConstants.FALSE);
		assertFormat(
				getFormattedPrefixForBrace() + " int x;", 
				
				getUnformattedPrefixForBrace() + "  int   x ;",
					
				options
			);
	}
	
	public void testNewLineInSingleStatementWithElse() throws Exception {
		assertFormat(
				getFormattedPrefixForBrace() + "\r\n" +
					"\tint x;\r\n" +
				"else\r\n" +
					"\tfloat x;", 
				
				getUnformattedPrefixForBrace() + "  int   x ; else float x;"
			);
	}
	
	public void testNoNewLineInSingleStatementWithElse() throws Exception {
		Map options = getDefaultOptions();
		options.put(getInsertNewLineInSimpleStatementOption(), DefaultCodeFormatterConstants.FALSE);
		assertFormat(
				getFormattedPrefixForBrace() + " int x; else float x;", 
				
				getUnformattedPrefixForBrace() + "  int   x ; else float x;",
					
				options
			);
	}
	
	// TODO Descent formatter: add "keep else if" in new line option.

}
