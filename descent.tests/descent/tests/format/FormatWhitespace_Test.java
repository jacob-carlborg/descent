package descent.tests.format;

import java.util.HashMap;
import java.util.Map;

import descent.core.formatter.DefaultCodeFormatterConstants;

public class FormatWhitespace_Test extends AbstractFormatInsideFunction_Test {
	
	@Override
	protected Map getDefaultOptions() {
		Map options = super.getDefaultOptions();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_FUNCTION_INVOCATION, DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_ASSERT_STATEMENTS, DefaultCodeFormatterConstants.FALSE);
		return options;
	}
	
	public void testNotFORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_FUNCTION_INVOCATION() throws Exception {
		assertFormat(
				"bla();"
				
				,
				
				"bla  (  )  ;"
				
				);
	}
	
	public void testFORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_FUNCTION_INVOCATION() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_FUNCTION_INVOCATION, DefaultCodeFormatterConstants.TRUE);
		assertFormat(
				"bla ();"
				,
				"bla  (  )  ;",
				options				
				);
	}
	
	public void testNotFORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_ASSERT_STATEMENTS() throws Exception {
		assertFormat(
				"assert(false);"
				
				,
				
				"assert  ( false )  ;"
				
				);
	}
	
	public void testFORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_ASSERT_STATEMENTS() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_ASSERT_STATEMENTS, DefaultCodeFormatterConstants.TRUE);
		assertFormat(
				"assert (false);"
				,
				"assert ( false )  ;",
				options				
				);
	}

}
