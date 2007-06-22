package descent.tests.format;

import java.util.HashMap;
import java.util.Map;

import descent.core.formatter.DefaultCodeFormatterConstants;


public abstract class AbstractFormatBraceElseWithSingleInsideFunction_Test extends AbstractFormatBraceElseInsideFunction_Test {
	
	@Override
	protected Map getDefaultOptions() {
		Map options = super.getDefaultOptions();
		options.put(getKeepSimpleThenInSameLineOption(), DefaultCodeFormatterConstants.FALSE);
		options.put(getSimpleElseStatementInSameLineOption(), DefaultCodeFormatterConstants.FALSE);
		return options;
	}
	
	protected abstract String getKeepSimpleThenInSameLineOption();
	protected abstract String getSimpleElseStatementInSameLineOption();
	
	public void testDontKeepSimpleThenInSameLine() throws Exception {
		assertFormat(
				getFormattedPrefixForBrace() + "\r\n" +
					"\tint x;", 
				
				getUnformattedPrefixForBrace() + "  int   x ;"
			);
	}
	
	public void testKeepSimpleThenInSameLine() throws Exception {
		Map options = new HashMap();
		options.put(getKeepSimpleThenInSameLineOption(), DefaultCodeFormatterConstants.TRUE);
		assertFormat(
				getFormattedPrefixForBrace() + " int x;", 
				
				getUnformattedPrefixForBrace() + "  int   x ;",
					
				options
			);
	}
	
	public void testDontKeepSimpleElseInSameLine() throws Exception {
		assertFormat(
				getFormattedPrefixForBrace() + "\r\n" +
					"\tint x;\r\n" +
					"else\r\n" +
					"\tfloat x;", 
				
				getUnformattedPrefixForBrace() + "  int   x ; else float x;"
			);
	}
	
	public void testKeepSimpleElseInSameLine() throws Exception {
		Map options = new HashMap();
		options.put(getSimpleElseStatementInSameLineOption(), DefaultCodeFormatterConstants.TRUE);
		assertFormat(
				getFormattedPrefixForBrace() + "\r\n" +
					"\tint x;\r\n" +
					"else float x;", 
				
				getUnformattedPrefixForBrace() + "  int   x ; else float x;",
				
				options
			);
	}

}
