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
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_ASSERT_STATEMENTS, DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_MIXINS, DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_MIXINS, DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_PRAGMAS, DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_PRAGMAS, DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_WITH_STATEMENTS, DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_WITH_STATEMENTS, DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_TYPEOF_STATEMENTS, DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_TYPEOF_STATEMENTS, DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_TYPEID_STATEMENTS, DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_TYPEID_STATEMENTS, DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_DELEGATE, DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_DELEGATE, DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_NEW_ARGUMENTS, DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_NEW_ARGUMENTS, DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_FILE_IMPORTS, DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_FILE_IMPORTS, DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_IF_STATEMENTS, DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_IF_STATEMENTS, DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_IS_EXPRESSIONS, DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_IS_EXPRESSIONS, DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_CASTS, DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_CASTS, DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_TRAITS_EXPRESSION, DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_MODIFIED_TYPE, DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_TRAITS_EXPRESSION, DefaultCodeFormatterConstants.TRUE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_FUNCTION_INVOCATION, DefaultCodeFormatterConstants.FALSE);
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
	
	public void testFORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_ASSERT_STATEMENTS() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_ASSERT_STATEMENTS, DefaultCodeFormatterConstants.TRUE);
		assertFormat(
				"assert( false);",
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
	
	public void testFORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_MIXINS() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_MIXINS, DefaultCodeFormatterConstants.TRUE);
		assertFormat(
				"mixin( x);",
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
	
	public void testFORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_PRAGMAS() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_PRAGMAS, DefaultCodeFormatterConstants.TRUE);
		assertFormat(
				"pragma( msg, \"hello\");",
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
	
	public void testFORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_WITH() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_WITH_STATEMENTS, DefaultCodeFormatterConstants.TRUE);
		assertFormat(
				"with( x) {\r\n" +
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
	
	public void testFORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_TYPEOF() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_TYPEOF_STATEMENTS, DefaultCodeFormatterConstants.TRUE);
		assertFormat(
				"typeof( x);",
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
	
	public void testFORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_TYPEID() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_TYPEID_STATEMENTS, DefaultCodeFormatterConstants.TRUE);
		assertFormat(
				"typeid( x);",
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
	
	public void testFORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_DELEGATE() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_DELEGATE, DefaultCodeFormatterConstants.TRUE);
		assertFormat(
				"int delegate( x) a;",
				"int delegate(x) a;",
				options
				);
	}
	
	public void testNotFORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_NEW_ARGUMENTS() throws Exception {
		assertFormat(
				"new(x) Type();",
				"new (x) Type();"
				);
	}
	
	public void testFORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_NEW_ARGUMENTS() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_NEW_ARGUMENTS, DefaultCodeFormatterConstants.TRUE);
		assertFormat(
				"new (x) Type();",
				"new (x) Type();",
				options
				);
	}
	
	public void testNotFORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_NEW_ARGUMENTS2() throws Exception {
		assertFormat(
				"new(x) class {\r\n" +
				"};",
				"new (x) class { };"
				);
	}
	
	public void testFORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_NEW_ARGUMENTS2() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_NEW_ARGUMENTS, DefaultCodeFormatterConstants.TRUE);
		assertFormat(
				"new (x) class {\r\n" +
				"};",
				"new (x) class { };",
				options
				);
	}
	
	public void testFORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_NEW_ARGUMENTS() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_NEW_ARGUMENTS, DefaultCodeFormatterConstants.TRUE);
		assertFormat(
				"new( x) Type();",
				"new (x) Type();",
				options
				);
	}
	
	public void testFORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_NEW_ARGUMENTS2() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_NEW_ARGUMENTS, DefaultCodeFormatterConstants.TRUE);
		assertFormat(
				"new( x) class {\r\n" +
				"};",
				"new (x) class { };",
				options
				);
	}
	
	public void testNotFORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_FILE_IMPORTS() throws Exception {
		assertFormat(
				"x = import(\"file\");",
				"x = import(\"file\");"
				);
	}
	
	public void testFORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_FILE_IMPORTS() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_FILE_IMPORTS, DefaultCodeFormatterConstants.TRUE);
		assertFormat(
				"x = import (\"file\");",
				"x = import(\"file\");",
				options
				);
	}
	
	public void testFORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_FILE_IMPORTS() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_FILE_IMPORTS, DefaultCodeFormatterConstants.TRUE);
		assertFormat(
				"x = import( \"file\");",
				"x = import(\"file\");",
				options
				);
	}
	
	public void testNotFORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_IF_STATEMENTS() throws Exception {
		assertFormat(
				"if(true) {\r\n" +
				"}",
				"if (true) { }"
				);
	}
	
	public void testFORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_IF_STATEMENTS() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_IF_STATEMENTS, DefaultCodeFormatterConstants.TRUE);
		assertFormat(
				"if (true) {\r\n" +
				"}",
				"if (true) { }",
				options
				);
	}
	
	public void testFORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_IF_STATEMENTS() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_IF_STATEMENTS, DefaultCodeFormatterConstants.TRUE);
		assertFormat(
				"if( true) {\r\n" +
				"}",
				"if (true) { }",
				options
				);
	}
	
	public void testNotFORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_IS_EXPRESSIONS() throws Exception {
		assertFormat(
				"x = is(a : bool);",
				"x = is(a : bool);"
				);
	}
	
	public void testFORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_IS_EXPRESSIONS() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_IS_EXPRESSIONS, DefaultCodeFormatterConstants.TRUE);
		assertFormat(
				"x = is (a : bool);",
				"x = is(a : bool);",
				options
				);
	}
	
	public void testFORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_IS_EXPRESSIONS() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_IS_EXPRESSIONS, DefaultCodeFormatterConstants.TRUE);
		assertFormat(
				"x = is( a : bool);",
				"x = is(a : bool);",
				options
				);
	}
	
	public void testNotFORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_CASTS() throws Exception {
		assertFormat(
				"x = cast(int) y;",
				"x = cast(int) y;"
				);
	}
	
	public void testFORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_CASTS() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_CASTS, DefaultCodeFormatterConstants.TRUE);
		assertFormat(
				"x = cast (int) y;",
				"x = cast(int) y;",
				options
				);
	}
	
	public void testFORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_CASTS() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_CASTS, DefaultCodeFormatterConstants.TRUE);
		assertFormat(
				"x = cast( int) y;",
				"x = cast(int) y;",
				options
				);
	}
	
	public void testNotFORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_TRAITS_EXPRESSION() throws Exception {
		assertFormat(
				"x = __traits(x, y);",
				"x = __traits(x, y);"
				);
	}
	
	public void testFORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_TRAITS_EXPRESSION() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_TRAITS_EXPRESSION, DefaultCodeFormatterConstants.TRUE);
		assertFormat(
				"x = __traits (x, y);",
				"x = __traits(x, y);",
				options
				);
	}
	
	public void testNotFORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_MODIFIED_TYPE() throws Exception {
		assertFormat(
				"invariant(int) x;",
				"invariant(int) x;"
				);
	}
	
	public void testFORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_MODIFIED_TYPE() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_MODIFIED_TYPE, DefaultCodeFormatterConstants.TRUE);
		assertFormat(
				"invariant (int) x;",
				"invariant(int) x;",
				options
				);
	}
	
	public void testNotFORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_FUNCTION_INVOCATION() throws Exception {
		assertFormat(
				"bla(x);",
				"bla(x);"
				);
	}
	
	public void testFORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_FUNCTION_INVOCATION() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_FUNCTION_INVOCATION, DefaultCodeFormatterConstants.TRUE);
		assertFormat(
				"bla( x);",
				"bla(x);",
				options
				);
	}

}
