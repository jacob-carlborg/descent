package descent.tests.format;

import java.util.Map;

import descent.core.formatter.DefaultCodeFormatterConstants;


public abstract class AbstractFormatBraceWithSingleInsideFunction_Test extends AbstractFormatBraceInsideFunction_Test {
	
	@Override
	protected Map getDefaultOptions() {
		Map options = super.getDefaultOptions();
		options.put(getInsertNewLineInSimpleStatementOption(), DefaultCodeFormatterConstants.TRUE);
		return options;
	}
	
	protected abstract String getInsertNewLineInSimpleStatementOption();
	
	public void testIdentSingleStatement() throws Exception {
		assertFormat(
				getFormattedPrefixForBrace() + "\r\n" +
					"\tint x;", 
				
				getUnformattedPrefixForBrace() + "  int   x ;"
			);
	}
	
	public void testDontIndentSingleStatement() throws Exception {
		Map options = getDefaultOptions();
		options.put(getInsertNewLineInSimpleStatementOption(), DefaultCodeFormatterConstants.FALSE);
		assertFormat(
				getFormattedPrefixForBrace() + " int x;", 
				
				getUnformattedPrefixForBrace() + "  int   x ;",
					
				options
			);
	}

}
