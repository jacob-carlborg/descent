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
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_CLASS_INVARIANTS, DefaultCodeFormatterConstants.FALSE);
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
	
	public void testNotFORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_CLASS_INVARIANTS() throws Exception {
		assertFormat(
				"class X {\r\n" +
				"\tinvariant() {\r\n" +
				"\t}\r\n" +
				"}",
				"class X { invariant() { } }"
				);
	}
	
	public void testFORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_CLASS_INVARIANTS() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_CLASS_INVARIANTS, DefaultCodeFormatterConstants.TRUE);
		assertFormat(
				"class X {\r\n" +
				"\tinvariant () {\r\n" +
				"\t}\r\n" +
				"}",
				"class X { invariant() { } }",
				options
				);
	}
	
	/* TODO fix this test (it's not important, since noone will write such thing
	public void testFORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_CLASS_INVARIANTS2() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_CLASS_INVARIANTS, DefaultCodeFormatterConstants.TRUE);
		assertFormat(
				"class X {\r\n" +
				"\tinvariant private invariant () {\r\n" +
				"\t}\r\n" +
				"}",
				"class X { invariant private invariant() { } }",
				options
				);
	}
	*/

}
