package descent.tests.format;

import java.util.HashMap;
import java.util.Map;

import descent.core.formatter.DefaultCodeFormatterConstants;

public class FormatWhitespaceStatements_Test extends AbstractFormatInsideFunction_Test {
	
	@Override
	protected Map getDefaultOptions() {
		Map options = super.getDefaultOptions();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_FUNCTION_INVOCATION, DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_ASSERT_STATEMENTS, DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_MIXINS, DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_PRAGMAS, DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_WITH_STATEMENTS, DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_TYPEOF_STATEMENTS, DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_TYPEID_STATEMENTS, DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_DELEGATE, DefaultCodeFormatterConstants.FALSE);
		return options;
	}
	
	public void testNotFORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_FUNCTION_INVOCATION() throws Exception {
		assertFormat(
				"bla();",
				"bla  (  )  ;"
				);
	}
	
	public void testFORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_FUNCTION_INVOCATION() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_FUNCTION_INVOCATION, DefaultCodeFormatterConstants.TRUE);
		assertFormat(
				"bla ();",
				"bla  (  )  ;",
				options
				);
	}
	
	public void testNotFORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_ASSERT_STATEMENTS() throws Exception {
		assertFormat(
				"assert(false);",
				"assert  ( false )  ;"
				);
	}
	
	public void testFORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_ASSERT_STATEMENTS() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_ASSERT_STATEMENTS, DefaultCodeFormatterConstants.TRUE);
		assertFormat(
				"assert (false);",
				"assert ( false )  ;",
				options
				);
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
	
	public void testNotFORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_WITH() throws Exception {
		assertFormat(
				"with(x) {\r\n" +
				"}",
				"with ( x ) {  }"
				);
	}
	
	public void testFORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_WITH() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_WITH_STATEMENTS, DefaultCodeFormatterConstants.TRUE);
		assertFormat(
				"with (x) {\r\n" +
				"}",
				"with ( x ) {  }",
				options
				);
	}
	
	public void testNotFORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_TYPEOF() throws Exception {
		assertFormat(
				"typeof(x);",
				"typeof(x);"
				);
	}
	
	public void testFORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_TYPEOF() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_TYPEOF_STATEMENTS, DefaultCodeFormatterConstants.TRUE);
		assertFormat(
				"typeof (x);",
				"typeof(x);",
				options
				);
	}
	
	public void testNotFORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_TYPEID() throws Exception {
		assertFormat(
				"typeid(x);",
				"typeid(x);"
				);
	}
	
	public void testFORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_TYPEID() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_TYPEID_STATEMENTS, DefaultCodeFormatterConstants.TRUE);
		assertFormat(
				"typeid (x);",
				"typeid(x);",
				options
				);
	}
	
	public void testNotFORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_DELEGATE() throws Exception {
		assertFormat(
				"int delegate(x) a;",
				"int delegate(x) a;"
				);
	}
	
	public void testFORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_DELEGATE() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_DELEGATE, DefaultCodeFormatterConstants.TRUE);
		assertFormat(
				"int delegate (x) a;",
				"int delegate(x) a;",
				options
				);
	}

}
