package descent.tests.format;

import java.util.HashMap;
import java.util.Map;

import descent.core.formatter.DefaultCodeFormatterConstants;

public class FormatWhitespaceDeclarations_Test extends AbstractFormatter_Test {
	
	@Override
	protected Map getDefaultOptions() {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_MIXINS, DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_PRAGMAS, DefaultCodeFormatterConstants.FALSE);
		return options;
	}
	
	public void testNotFORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_MIXINS() throws Exception {
		assertFormat(
				"mixin(x);",
				"mixin  ( x )  ;"
				);
	}
	
	public void testFORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_MIXINS() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_MIXINS, DefaultCodeFormatterConstants.TRUE);
		assertFormat(
				"mixin (x);",
				"mixin ( x )  ;",
				options
				);
	}
	
	public void testNotFORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_PRAGMAS() throws Exception {
		assertFormat(
				"pragma(msg, \"hello\");",
				"pragma  ( msg, \"hello\")  ;"
				);
	}
	
	public void testFORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_PRAGMAS() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_PRAGMAS, DefaultCodeFormatterConstants.TRUE);
		assertFormat(
				"pragma (msg, \"hello\");",
				"pragma  ( msg, \"hello\")  ;",
				options
				);
	}

}
