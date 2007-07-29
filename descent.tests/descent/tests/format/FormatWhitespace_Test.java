package descent.tests.format;

import java.util.HashMap;
import java.util.Map;

import descent.core.formatter.DefaultCodeFormatterConstants;

public class FormatWhitespace_Test extends AbstractFormatInsideFunction_Test {
	
	@Override
	protected Map getDefaultOptions() {
		Map options = super.getDefaultOptions();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_FUNCTION_INVOCATION, DefaultCodeFormatterConstants.FALSE);
		return options;
	}
	
	public void testDontInsertSpaceBeforeOpeningParenInFunctionInvocation() throws Exception {
		assertFormat(
				"bla();"
				
				,
				
				"bla  (  )  ;"
				
				);
	}
	
	public void testInsertSpaceBeforeOpeningParenInFunctionInvocation() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_FUNCTION_INVOCATION, DefaultCodeFormatterConstants.TRUE);
		assertFormat(
				"bla ();"
				,
				"bla  (  )  ;",
				options				
				);
	}

}
