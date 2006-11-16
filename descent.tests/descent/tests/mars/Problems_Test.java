package descent.tests.mars;

import junit.framework.TestCase;
import descent.core.dom.ICompilationUnit;
import descent.core.dom.IProblem;
import descent.internal.core.dom.ParserFacade;

public class Problems_Test extends TestCase {
	
	public void test_UNTERMINATED_BLOCK_COMMENT() {
		  IProblem p = getProblem(" /*  ");
		  
		  assertEquals(IProblem.UNTERMINATED_BLOCK_COMMENT, p.getId());
		  assertEquals(IProblem.SEVERITY_ERROR, p.getSeverity());
		  assertEquals(1, p.getOffset());
		  assertEquals(4, p.getLength());
	}
	
	public void test_UNTERMINATED_PLUS_BLOCK_COMMENT() {
		  IProblem p = getProblem(" /+  ");
		  
		  assertEquals(IProblem.UNTERMINATED_PLUS_BLOCK_COMMENT, p.getId());
		  assertEquals(IProblem.SEVERITY_ERROR, p.getSeverity());
		  assertEquals(1, p.getOffset());
		  assertEquals(4, p.getLength());
	}
	
	public void test_INCORRECT_NUMBER_OF_HEX_DIGITS_IN_ESCAPE_SEQUENCE_x() {
		  IProblem p = getProblem(" char[] c = \"\\x1\";");
		  
		  assertEquals(IProblem.INCORRECT_NUMBER_OF_HEX_DIGITS_IN_ESCAPE_SEQUENCE, p.getId());
		  assertEquals(IProblem.SEVERITY_ERROR, p.getSeverity());
		  assertEquals(13, p.getOffset());
		  assertEquals(3, p.getLength());
	}
	
	public void test_INCORRECT_NUMBER_OF_HEX_DIGITS_IN_ESCAPE_SEQUENCE_u() {
		  IProblem p = getProblem(" char[] c = \"\\u1\";");
		  
		  assertEquals(IProblem.INCORRECT_NUMBER_OF_HEX_DIGITS_IN_ESCAPE_SEQUENCE, p.getId());
		  assertEquals(IProblem.SEVERITY_ERROR, p.getSeverity());
		  assertEquals(13, p.getOffset());
		  assertEquals(3, p.getLength());
	}
	
	public void test_INCORRECT_NUMBER_OF_HEX_DIGITS_IN_ESCAPE_SEQUENCE_U() {
		  IProblem p = getProblem(" char[] c = \"\\U1\";");
		  
		  assertEquals(IProblem.INCORRECT_NUMBER_OF_HEX_DIGITS_IN_ESCAPE_SEQUENCE, p.getId());
		  assertEquals(IProblem.SEVERITY_ERROR, p.getSeverity());
		  assertEquals(13, p.getOffset());
		  assertEquals(3, p.getLength());
	}
	
	public void test_UNDEFINED_ESCAPE_HEX_SEQUENCE() {
		  IProblem p = getProblem(" char[] c = \"\\xT\";");
		  
		  assertEquals(IProblem.UNDEFINED_ESCAPE_HEX_SEQUENCE, p.getId());
		  assertEquals(IProblem.SEVERITY_ERROR, p.getSeverity());
		  assertEquals(13, p.getOffset());
		  assertEquals(3, p.getLength());
	}
	
	public void test_UNDEFINED_ESCAPE_SEQUENCE() {
		  IProblem p = getProblem(" char[] c = \"\\T\";");
		  
		  assertEquals(IProblem.UNDEFINED_ESCAPE_SEQUENCE, p.getId());
		  assertEquals(IProblem.SEVERITY_ERROR, p.getSeverity());
		  assertEquals(13, p.getOffset());
		  assertEquals(2, p.getLength());
	}
	
	public void test_UNTERMINATED_STRING_CONSTANT() {
		  IProblem[] p = getProblems(" char[] c = \"hola ;", 2);
		  
		  assertEquals(IProblem.UNTERMINATED_STRING_CONSTANT, p[0].getId());
		  assertEquals(IProblem.SEVERITY_ERROR, p[0].getSeverity());
		  assertEquals(12, p[0].getOffset());
		  assertEquals(7, p[0].getLength());
	}
	
	public void test_UNTERMINATED_STRING_CONSTANT_WYSIWYG() {
		  IProblem[] p = getProblems(" char[] c = `hola ;", 2);
		  
		  assertEquals(IProblem.UNTERMINATED_STRING_CONSTANT, p[0].getId());
		  assertEquals(IProblem.SEVERITY_ERROR, p[0].getSeverity());
		  assertEquals(12, p[0].getOffset());
		  assertEquals(7, p[0].getLength());
	}
	
	public void test_UNTERMINATED_STRING_CONSTANT_HEX() {
		  IProblem[] p = getProblems(" char[] c = x\"ABCD ;", 3);
		  
		  assertEquals(IProblem.UNTERMINATED_STRING_CONSTANT, p[1].getId());
		  assertEquals(IProblem.SEVERITY_ERROR, p[1].getSeverity());
		  assertEquals(12, p[1].getOffset());
		  assertEquals(8, p[1].getLength());
	}
	
	public void test_ODD_NUMBER_OF_CHARACTERS_IN_HEX_STRING() {
		  IProblem p = getProblem(" char[] c = x\"123\";");
		  
		  assertEquals(IProblem.ODD_NUMBER_OF_CHARACTERS_IN_HEX_STRING, p.getId());
		  assertEquals(IProblem.SEVERITY_ERROR, p.getSeverity());
		  assertEquals(12, p.getOffset());
		  assertEquals(6, p.getLength());
	}
	
	public void test_NON_HEX_CHARACTER() {
		  IProblem p = getProblem(" char[] c = x\"1T34\";");
		  
		  assertEquals(IProblem.NON_HEX_CHARACTER, p.getId());
		  assertEquals(IProblem.SEVERITY_ERROR, p.getSeverity());
		  assertEquals(15, p.getOffset());
		  assertEquals(1, p.getLength());
	}
	
	public void test_UNTERMINATED_CHARACTER_CONSTANT() {
		  IProblem p = getProblem(" char[] c = ' ;");
		  
		  assertEquals(IProblem.UNTERMINATED_CHARACTER_CONSTANT, p.getId());
		  assertEquals(IProblem.SEVERITY_ERROR, p.getSeverity());
		  assertEquals(12, p.getOffset());
		  assertEquals(2, p.getLength());
	}
	
	public void test_BINARY_DIGIT_EXPECTED() {
		  IProblem p = getProblem(" int a = 0bT;");
		  
		  assertEquals(IProblem.BINARY_DIGIT_EXPECTED, p.getId());
		  assertEquals(IProblem.SEVERITY_ERROR, p.getSeverity());
		  assertEquals(11, p.getOffset());
		  assertEquals(1, p.getLength());
	}
	
	/* TODO:
	public void test_OCTAL_DIGIT_EXPECTED() {
		  IProblem p = getProblem("int a = 09;");
		  
		  assertEquals(IProblem.BINARY_DIGIT_EXPECTED, p.getId());
		  assertEquals(9, p.getOffset());
		  assertEquals(1, p.getLength());
	}
	*/
	
	public void test_HEX_DIGIT_EXPECTED() {
		  IProblem[] p = getProblems(" int a = 0xX c = new C();", 2);
		  
		  assertEquals(IProblem.HEX_DIGIT_EXPECTED, p[0].getId());
		  assertEquals(IProblem.SEVERITY_ERROR, p[0].getSeverity());
		  assertEquals(11, p[0].getOffset());
		  assertEquals(1, p[0].getLength());
	}
	
	public void test_UNSUPPORTED_CHAR() {
		  IProblem p = getProblem(" ");
		  
		  assertEquals(IProblem.UNSUPPORTED_CHAR, p.getId());
		  assertEquals(IProblem.SEVERITY_ERROR, p.getSeverity());
		  assertEquals(1, p.getOffset());
		  assertEquals(1, p.getLength());
	}
	
	/* TODO:
	public void test_INVALID_UTF_CHARACTER() {
		  IProblem p = getProblem("char c = \\uFFFF;");
		  
		  assertEquals(IProblem.INVALID_UTF_CHARACTER, p.getId());
		  assertEquals(0, p.getOffset());
		  assertEquals(1, p.getLength());
	}
	*/
	
	public void test_IDENTIFIER_EXPECTED_FOLLOWING_MODULE() {
		  IProblem p = getProblem(" module ;");
		  
		  assertEquals(IProblem.IDENTIFIER_EXPECTED, p.getId());
		  assertEquals(IProblem.SEVERITY_ERROR, p.getSeverity());
		  assertEquals(1, p.getOffset());
		  assertEquals(6, p.getLength());
	}
	
	public void test_SEMICOLON_EXPECTED_FOLLOWING_MODULE_DECLARATION() {
		  IProblem p = getProblem(" module bla");
		  
		  assertEquals(IProblem.SEMICOLON_EXPECTED, p.getId());
		  assertEquals(IProblem.SEVERITY_ERROR, p.getSeverity());
		  assertEquals(1, p.getOffset());
		  assertEquals(6, p.getLength());
	}
	
	public void test_IDENTIFIER_EXPECTED_FOLLOWING_PACKAGE_FOR_MODULE() {
		  IProblem p = getProblem(" module bla.;");
		  
		  assertEquals(IProblem.IDENTIFIER_EXPECTED, p.getId());
		  assertEquals(IProblem.SEVERITY_ERROR, p.getSeverity());
		  assertEquals(8, p.getOffset());
		  assertEquals(4, p.getLength());
	}
	
	public void test_IDENTIFIER_EXPECTED_FOLLOWING_IMPORT() {
		  IProblem p = getProblem(" import ;");
		  
		  assertEquals(IProblem.IDENTIFIER_EXPECTED, p.getId());
		  assertEquals(IProblem.SEVERITY_ERROR, p.getSeverity());
		  assertEquals(1, p.getOffset());
		  assertEquals(6, p.getLength());
	}
	
	public void test_IDENTIFIER_EXPECTED_FOLLOWING_PACKAGE_FOR_IMPORT() {
		  IProblem p = getProblem(" import bla.;");
		  
		  assertEquals(IProblem.IDENTIFIER_EXPECTED, p.getId());
		  assertEquals(IProblem.SEVERITY_ERROR, p.getSeverity());
		  assertEquals(8, p.getOffset());
		  assertEquals(4, p.getLength());
	}
	
	public void test_IDENTIFIER_EXPECTED_FOLLOWING_COLON() {
		  IProblem p = getProblem(" import stdio.std : ;");
		  
		  assertEquals(IProblem.IDENTIFIER_EXPECTED, p.getId());
		  assertEquals(IProblem.SEVERITY_ERROR, p.getSeverity());
		  assertEquals(18, p.getOffset());
		  assertEquals(1, p.getLength());
	}
	
	public void test_IDENTIFIER_EXPECTED_FOLLOWING_ALIAS() {
		  IProblem p = getProblem(" import stdio.std : al = ;");
		  
		  assertEquals(IProblem.IDENTIFIER_EXPECTED, p.getId());
		  assertEquals(IProblem.SEVERITY_ERROR, p.getSeverity());
		  assertEquals(20, p.getOffset());
		  assertEquals(4, p.getLength());
	}
	
	public void test_SEMICOLON_EXPECTED_FOLLOWING_IMPORT_DECLARATION() {
		  IProblem p = getProblem(" import bla");
		  
		  assertEquals(IProblem.SEMICOLON_EXPECTED, p.getId());
		  assertEquals(IProblem.SEVERITY_ERROR, p.getSeverity());
		  assertEquals(1, p.getOffset());
		  assertEquals(6, p.getLength());
	}
	
	public void test_ENUM_MEMBER_EXPECTED() {
		  IProblem p = getProblem(" enum Bla { class }");
		  
		  assertEquals(IProblem.ENUM_MEMBER_EXPECTED, p.getId());
		  assertEquals(IProblem.SEVERITY_ERROR, p.getSeverity());
		  assertEquals(12, p.getOffset());
		  assertEquals(5, p.getLength());
	}
	
	public void test_ENUM_DECLARATION_INVALID() {
		  IProblem p = getProblem(" enum int a = 2;");
		  
		  assertEquals(IProblem.ENUM_DECLARATION_IS_INVALID, p.getId());
		  assertEquals(IProblem.SEVERITY_ERROR, p.getSeverity());
		  assertEquals(1, p.getOffset());
		  assertEquals(4, p.getLength());
	}
	
	public void test_MISMATCHED_STRING_LITERAL_POSTFIXES() {
		  IProblem p = getProblem(" char[] s = \"hola\"c \"hola\"c \"chau\"d;");
		  
		  assertEquals(IProblem.MISMATCHED_STRING_LITERAL_POSTFIXES, p.getId());
		  assertEquals(IProblem.SEVERITY_ERROR, p.getSeverity());
		  assertEquals(20, p.getOffset());
		  assertEquals(15, p.getLength());
	}
	
	public void test_ANONYMOUS_CLASSES_NOT_ALLOWED() {
		  IProblem p = getProblem(" class { }");
		  
		  assertEquals(IProblem.ANONYMOUS_CLASSES_NOT_ALLOWED, p.getId());
		  assertEquals(IProblem.SEVERITY_ERROR, p.getSeverity());
		  assertEquals(1, p.getOffset());
		  assertEquals(5, p.getLength());
	}
	
	public void test_MEMBERS_EXPECTED_AFTER_BASE_CLASSES() {
		  IProblem[] p = getProblems(" class Ble : Bla, Bli ", 2);
		  
		  assertEquals(IProblem.MEMBERS_EXPECTED, p[0].getId());
		  assertEquals(IProblem.CURLIES_EXPECTED_FOLLOWING_AGGREGATE_DECLARATION, p[1].getId());
		  assertEquals(IProblem.SEVERITY_ERROR, p[0].getSeverity());
		  /* TODO: hacer que el error vaya a la �ltima clase heredada
		  assertEquals(16, p.getOffset());
		  assertEquals(3, p.getLength());
		  */
	}
	
	public void test_RIGHT_CURLY_EXPECTED_FOLLOWING_MEMBER_DECLARATIONS_IN_AGGREGATE_1() {
		  IProblem[] p = getProblems(" class { ", 2);
		  
		  assertEquals(IProblem.ANONYMOUS_CLASSES_NOT_ALLOWED, p[0].getId());
		  assertEquals(IProblem.RIGHT_CURLY_EXPECTED_FOLLOWING_MEMBER_DECLARATIONS_IN_AGGREGATE, p[1].getId());
		  assertEquals(IProblem.SEVERITY_ERROR, p[0].getSeverity());
		  assertEquals(7, p[1].getOffset());
		  assertEquals(1, p[1].getLength());
	}
	
	public void test_RIGHT_CURLY_EXPECTED_FOLLOWING_MEMBER_DECLARATIONS_IN_AGGREGATE_2() {
		  IProblem p = getProblem(" class Ble { ");
		  
		  assertEquals(IProblem.RIGHT_CURLY_EXPECTED_FOLLOWING_MEMBER_DECLARATIONS_IN_AGGREGATE, p.getId());
		  assertEquals(IProblem.SEVERITY_ERROR, p.getSeverity());
		  assertEquals(11, p.getOffset());
		  assertEquals(1, p.getLength());
	}
	
	public void test_CURLIES_EXPECTED_FOLLOWING_AGGREGATE_DECLARATION_1() {
		  IProblem[] p = getProblems(" class ", 2);
		  
		  assertEquals(IProblem.CURLIES_EXPECTED_FOLLOWING_AGGREGATE_DECLARATION, p[1].getId());
		  assertEquals(IProblem.SEVERITY_ERROR, p[1].getSeverity());
		  assertEquals(1, p[1].getOffset());
		  assertEquals(5, p[1].getLength());
	}
	
	public void test_CURLIES_EXPECTED_FOLLOWING_AGGREGATE_DECLARATION_2() {
		  IProblem p = getProblem(" class Ble ");
		  
		  assertEquals(IProblem.CURLIES_EXPECTED_FOLLOWING_AGGREGATE_DECLARATION, p.getId());
		  assertEquals(IProblem.SEVERITY_ERROR, p.getSeverity());
		  assertEquals(1, p.getOffset());
		  assertEquals(9, p.getLength());
	}
	
	public void test_NO_IDENTIFIER_FOR_DECLARATOR() {
		  IProblem p = getProblem(" int ;");
		  
		  assertEquals(IProblem.NO_IDENTIFIER_FOR_DECLARATION, p.getId());
		  assertEquals(IProblem.SEVERITY_ERROR, p.getSeverity());
		  assertEquals(1, p.getOffset());
		  assertEquals(3, p.getLength());
	}
	
	public void test_NO_IDENTIFIER_FOR_DECLARATOR2() {
		  IProblem p = getProblem(" int x, ;");
		  
		  assertEquals(IProblem.NO_IDENTIFIER_FOR_DECLARATION, p.getId());
		  assertEquals(IProblem.SEVERITY_ERROR, p.getSeverity());
		  assertEquals(1, p.getOffset());
		  assertEquals(3, p.getLength());
	}
	
	public void test_SEMICOLON_EXPECTED_TO_CLOSE_DECLARATION() {
		  IProblem p = getProblem(" int x");
		  
		  assertEquals(IProblem.SEMICOLON_EXPECTED, p.getId());
		  assertEquals(IProblem.SEVERITY_ERROR, p.getSeverity());
		  assertEquals(1, p.getOffset());
		  assertEquals(5, p.getLength());
	}
	
	public void test_THREE_EQUALS_IS_DEPRECATED() {
		  IProblem p = getProblem(" bool x = 1 === 2;");
		  
		  assertEquals(IProblem.THREE_EQUALS_IS_DEPRECATED, p.getId());
		  assertEquals(IProblem.SEVERITY_ERROR, p.getSeverity());
		  assertEquals(12, p.getOffset());
		  assertEquals(3, p.getLength());
	}
	
	public void test_ALIAS_CANNOT_HAVE_INITIALIZER() {
		  IProblem p = getProblem(" alias int Bla = 1;");
		  
		  assertEquals(IProblem.ALIAS_CANNOT_HAVE_INITIALIZER, p.getId());
		  assertEquals(IProblem.SEVERITY_ERROR, p.getSeverity());
		  assertEquals(15, p.getOffset());
		  assertEquals(3, p.getLength());
	}
	
	public void test_UNRECOGNIZED_DECLARATION() {
		  IProblem p = getProblem(" }");
		  
		  assertEquals(IProblem.UNRECOGNIZED_DECLARATION, p.getId());
		  assertEquals(IProblem.SEVERITY_ERROR, p.getSeverity());
		  assertEquals(1, p.getOffset());
		  assertEquals(1, p.getLength());
	}
	
	public void test_INTEGER_EXPECTED() {
		  IProblem p = getProblem(" align(bla) { }");
		  
		  assertEquals(IProblem.INTEGER_EXPECTED, p.getId());
		  assertEquals(IProblem.SEVERITY_ERROR, p.getSeverity());
		  assertEquals(7, p.getOffset());
		  assertEquals(3, p.getLength());
	}
	
	public void test_DECLARATION_EXPECTED_FOLLOWING_ATTRIBUTE() {
		  IProblem p = getProblem(" public;");
		  
		  assertEquals(IProblem.DECLARATION_EXPECTED, p.getId());
		  assertEquals(IProblem.SEVERITY_ERROR, p.getSeverity());
		  assertEquals(7, p.getOffset());
		  assertEquals(1, p.getLength());
	}
	
	public void test_SEMICOLON_EXPECTED_1() {
		  IProblem p = getProblem(" version = 2 bla");
		  
		  assertEquals(IProblem.SEMICOLON_EXPECTED, p.getId());
		  assertEquals(IProblem.SEVERITY_ERROR, p.getSeverity());
		  assertEquals(13, p.getOffset());
		  assertEquals(3, p.getLength());
	}
	
	public void test_SEMICOLON_EXPECTED_2() {
		  IProblem p = getProblem(" debug = 2 bla");
		  
		  assertEquals(IProblem.SEMICOLON_EXPECTED, p.getId());
		  assertEquals(IProblem.SEVERITY_ERROR, p.getSeverity());
		  assertEquals(11, p.getOffset());
		  assertEquals(3, p.getLength());
	}
	
	public void test_IDENTIFIER_OR_INTEGER_EXPECTED_1() {
		  IProblem p = getProblem(" debug = 2.0;");
		  
		  assertEquals(IProblem.IDENTIFIER_OR_INTEGER_EXPECTED, p.getId());
		  assertEquals(IProblem.SEVERITY_ERROR, p.getSeverity());
		  assertEquals(9, p.getOffset());
		  assertEquals(3, p.getLength());
	}
	
	public void test_IDENTIFIER_OR_INTEGER_EXPECTED_2() {
		  IProblem p = getProblem(" version = 2.0;");
		  
		  assertEquals(IProblem.IDENTIFIER_OR_INTEGER_EXPECTED, p.getId());
		  assertEquals(IProblem.SEVERITY_ERROR, p.getSeverity());
		  assertEquals(11, p.getOffset());
		  assertEquals(3, p.getLength());
	}
	
	public void test_IDENTIFIER_OR_INTEGER_EXPECTED_3() {
		  IProblem p = getProblem(" version(2.0) { }");
		  
		  assertEquals(IProblem.IDENTIFIER_OR_INTEGER_EXPECTED, p.getId());
		  assertEquals(IProblem.SEVERITY_ERROR, p.getSeverity());
		  assertEquals(9, p.getOffset());
		  assertEquals(3, p.getLength());
	}
	
	public void test_IDENTIFIER_OR_INTEGER_EXPECTED_4() {
		  IProblem p = getProblem(" debug(2.0) { }");
		  
		  assertEquals(IProblem.IDENTIFIER_OR_INTEGER_EXPECTED, p.getId());
		  assertEquals(IProblem.SEVERITY_ERROR, p.getSeverity());
		  assertEquals(7, p.getOffset());
		  assertEquals(3, p.getLength());
	}
	
	public void test_CONDITION_EXPECTED_FOLLOWING_VERSION() {
		  IProblem[] p = getProblems(" version 2", 2);
		  
		  assertEquals(IProblem.CONDITION_EXPECTED_FOLLOWING_VERSION, p[0].getId());
		  assertEquals(IProblem.SEVERITY_ERROR, p[0].getSeverity());
		  assertEquals(9, p[0].getOffset());
		  assertEquals(1, p[0].getLength());
	}
	
	public void test_INVALID_LINKAGE_IDENTIFIER() {
		  IProblem p = getProblem(" extern(bla) { }");
		  
		  assertEquals(IProblem.INVALID_LINKAGE_IDENTIFIER, p.getId());
		  assertEquals(IProblem.SEVERITY_ERROR, p.getSeverity());
		  assertEquals(8, p.getOffset());
		  assertEquals(3, p.getLength());
	}
	
	public void test_EXPRESSION_EXPECTED_FOLLOWING_STATIC_IF() {
		  IProblem[] p = getProblems(" static if 2", 2);
		  
		  assertEquals(IProblem.EXPRESSION_EXPECTED, p[0].getId());
		  assertEquals(IProblem.SEVERITY_ERROR, p[0].getSeverity());
		  assertEquals(11, p[0].getOffset());
		  assertEquals(1, p[0].getLength());
	}
	
	public void test_VARIADIC_ARGUMENT_CANNOT_BE_OUT_OR_INOUT() {
		  IProblem p = getProblem(" void bla(out int ...) { }");
		  
		  assertEquals(IProblem.VARIADIC_ARGUMENT_CANNOT_BE_OUT_OR_INOUT, p.getId());
		  assertEquals(IProblem.SEVERITY_ERROR, p.getSeverity());
		  assertEquals(10, p.getOffset());
		  assertEquals(3, p.getLength());
	}
	
	public void test_DEFAULT_ARGUMENT_EXPECTED() {
		  IProblem p = getProblem(" void bla(int a = 2, int b) { }");
		  
		  assertEquals(IProblem.DEFAULT_ARGUMENT_EXPECTED, p.getId());
		  assertEquals(IProblem.SEVERITY_ERROR, p.getSeverity());
		  assertEquals(25, p.getOffset());
		  assertEquals(1, p.getLength());
	}
	
	public void test_VARIADIC_NOT_ALLOWED_IN_DELETE() {
		  IProblem p = getProblem(" delete(int ...) { }");
		  
		  assertEquals(IProblem.VARIADIC_NOT_ALLOWED_IN_DELETE, p.getId());
		  assertEquals(IProblem.SEVERITY_ERROR, p.getSeverity());
		  assertEquals(1, p.getOffset());
		  assertEquals(6, p.getLength());
	}
	
	public void test_BASE_CLASS_EXPECTED() {
		  IProblem p = getProblem(" class Bla : { }");
		  
		  assertEquals(IProblem.BASE_CLASS_EXPECTED, p.getId());
		  assertEquals(IProblem.SEVERITY_ERROR, p.getSeverity());
		  assertEquals(13, p.getOffset());
		  assertEquals(1, p.getLength());
	}
	
	public void test_TEMPLATE_IDENTIFIER_EXPECTED() {
		  IProblem p = getProblem(" template class Bla { }");
		  
		  assertEquals(IProblem.TEMPLATE_IDENTIFIER_EXPECTED, p.getId());
		  assertEquals(IProblem.SEVERITY_ERROR, p.getSeverity());
		  assertEquals(10, p.getOffset());
		  assertEquals(5, p.getLength());
	}
	
	public void test_PARENTHESIZED_TEMPLATE_PARAMETER_LIST_EXPECTED() {
		  IProblem p = getProblem(" template Bla class Bla { }");
		  
		  assertEquals(IProblem.PARENTHESIZED_TEMPLATE_PARAMETER_LIST_EXPECTED, p.getId());
		  assertEquals(IProblem.SEVERITY_ERROR, p.getSeverity());
		  assertEquals(14, p.getOffset());
		  assertEquals(5, p.getLength());
	}
	
	public void test_IDENTIFIER_EXPECTED_FOR_TEMPLATE_PARAMETER() {
		  IProblem p = getProblem(" template Bla(alias class Bla { }");
		  
		  assertEquals(IProblem.IDENTIFIER_EXPECTED, p.getId());
		  assertEquals(IProblem.SEVERITY_ERROR, p.getSeverity());
		  assertEquals(20, p.getOffset());
		  assertEquals(5, p.getLength());
	}
	
	public void test_MULTIPLE_DECLARATIONS_MUST_HAVE_THE_SAME_TYPE() {
		  IProblem p = getProblem(" int *x, y[];");
		  
		  assertEquals(IProblem.MULTIPLE_DECLARATIONS_MUST_HAVE_THE_SAME_TYPE, p.getId());
		  assertEquals(IProblem.SEVERITY_ERROR, p.getSeverity());
		  assertEquals(9, p.getOffset());
		  assertEquals(1, p.getLength());
	}
	
	public void test_INVALID_PRAGMA_SYNTAX() {
		  IProblem[] p = getProblems(" #", 1);
		  
		  assertEquals(IProblem.INVALID_PRAGMA_SYNTAX, p[0].getId());
		  assertEquals(IProblem.SEVERITY_ERROR, p[0].getSeverity());
		  assertEquals(1, p[0].getOffset());
		  assertEquals(1, p[0].getLength());
	}
	
	public void test_UNRECOGNIZED_CHARACTER_ENTITY() {
		  IProblem p = getProblem("char[] s = \"\\&pepe;\";");
		  
		  assertEquals(IProblem.UNRECOGNIZED_CHARACTER_ENTITY, p.getId());
		  assertEquals(IProblem.SEVERITY_ERROR, p.getSeverity());
		  assertEquals(13, p.getOffset());
		  assertEquals(6, p.getLength());
	}
	
	public void test_UNTERMINATED_NAMED_ENTITY() {
		IProblem p = getProblem("char[] s = \"\\&pepe\";");
		  
		assertEquals(IProblem.UNTERMINATED_NAMED_ENTITY, p.getId());
		assertEquals(IProblem.SEVERITY_ERROR, p.getSeverity());
		assertEquals(13, p.getOffset());
		assertEquals(5, p.getLength());
	}
	
	public void test_VARIADIC_TEMPLATE_PARAMETER_MUST_BE_LAST_ONE() {
		IProblem p = getProblem("template Foo(T ..., U, V ...) { }");
		  
		assertEquals(IProblem.VARIADIC_TEMPLATE_PARAMETER_MUST_BE_LAST_ONE, p.getId());
		assertEquals(IProblem.SEVERITY_ERROR, p.getSeverity());
		assertEquals(23, p.getOffset());
		assertEquals(5, p.getLength());
	}
	
	public void test_PRAGMA_IDENTIFIER_EXPECTED() {
		IProblem p = getProblem("pragma(2);");
		  
		assertEquals(IProblem.IDENTIFIER_EXPECTED, p.getId());
		assertEquals(IProblem.SEVERITY_ERROR, p.getSeverity());
		assertEquals(7, p.getOffset());
		assertEquals(1, p.getLength());
	}
	
	public void test_PRAGMA_CHECK_SINGLE_PARAMETER_NOT_RPAREN() {
		IProblem p = getProblem("pragma(msg(;");
		  
		assertEquals(IProblem.FOUND_SOMETHING_WHEN_EXPECTING_SOMETHING, p.getId());
		assertEquals(IProblem.SEVERITY_ERROR, p.getSeverity());
		assertEquals(10, p.getOffset());
		assertEquals(1, p.getLength());
	}
	
	public void test_PRAGMA_CHECK_SINGLE_PARAMETER_NOT_RPAREN_OUT_OF_MEMORY_BUG() {
		IProblem[] p = getProblems("pragma(msg,(;", 2);
		  
		assertEquals(IProblem.EXPRESSION_EXPECTED, p[0].getId());
		assertEquals(IProblem.SEVERITY_ERROR, p[0].getSeverity());
		assertEquals(12, p[0].getOffset());
		assertEquals(1, p[0].getLength());
	}
	
	public void test_ENUM_DECLARATION_IS_INVALID() {
		IProblem p = getProblem(" enum {");
		  
		assertEquals(IProblem.ENUM_DECLARATION_IS_INVALID, p.getId());
		assertEquals(IProblem.SEVERITY_ERROR, p.getSeverity());
		assertEquals(1, p.getOffset());
		assertEquals(4, p.getLength());
	}
	
	public void test_MEMBERS_OF_TEMPLATE_DECLARATION_EXPECTED() {
		IProblem[] p = getProblems(" template T() int", 3);
		  
		assertEquals(IProblem.MEMBERS_EXPECTED, p[0].getId());
		assertEquals(IProblem.SEVERITY_ERROR, p[0].getSeverity());
		assertEquals(14, p[0].getOffset());
		assertEquals(3, p[0].getLength());
	}

	public void test_SEMICOLON_EXPECTED_FOLLOWING_AUTO_DECLARATION() {
		  IProblem[] p = getProblems(" auto x = 2 2", 2);
		  
		  assertEquals(IProblem.SEMICOLON_EXPECTED, p[0].getId());
		  assertEquals(IProblem.SEVERITY_ERROR, p[0].getSeverity());
		  assertEquals(12, p[0].getOffset());
		  assertEquals(1, p[0].getLength());
	}
	
	public void test_IFTYPE_DECPRECATED() {
		  IProblem p = getProblem(" iftype(x) { }");
		  
		  assertEquals(IProblem.IFTYPE_DEPRECATED, p.getId());
		  assertEquals(IProblem.SEVERITY_WARNING, p.getSeverity());
		  assertEquals(1, p.getOffset());
		  assertEquals(6, p.getLength());
	}
	
	public void test_INVALID_IFTYPE_SYNTAX() {
		  IProblem p[] = getProblems(" iftype int", 3);
		  
		  assertEquals(IProblem.INVALID_IFTYPE_SYNTAX, p[0].getId());
		  assertEquals(IProblem.SEVERITY_ERROR, p[0].getSeverity());
		  assertEquals(8, p[0].getOffset());
		  assertEquals(3, p[0].getLength());
	}
	
	public void test_IDENTIFIER_EXPECTED_FOR_TEMPLATE_PARAMETER_2() {
		IProblem[] p = getProblems(" template T(2)", 2);
		  
		assertEquals(IProblem.IDENTIFIER_EXPECTED, p[0].getId());
		assertEquals(IProblem.SEVERITY_ERROR, p[0].getSeverity());
		assertEquals(12, p[0].getOffset());
		assertEquals(1, p[0].getLength());
	}
	
	public void test_IDENTIFIER_EXPECTED_FOR_MIXIN() {
		IProblem[] p = getProblems(" mixin typeof(2).!() m;", 2);
		  
		assertEquals(IProblem.IDENTIFIER_EXPECTED, p[0].getId());
		assertEquals(IProblem.SEVERITY_ERROR, p[0].getSeverity());
		assertEquals(17, p[0].getOffset());
		assertEquals(1, p[0].getLength());
	}
	
	public void test_NO_IDENTIFIER_FOR_TEMPLATE_VALUE_PARAMETER() {
		IProblem[] p = getProblems(" template Temp(int 2) { }", 2);
		  
		assertEquals(IProblem.IDENTIFIER_EXPECTED, p[0].getId());
		assertEquals(IProblem.SEVERITY_ERROR, p[0].getSeverity());
		assertEquals(19, p[0].getOffset());
		assertEquals(1, p[0].getLength());
	}
	
	public void test_IDENTIFIER_EXPECTED_FOR_MIXIN_2() {
		IProblem[] p = getProblems(" mixin !() m;", 2);
		  
		assertEquals(IProblem.IDENTIFIER_EXPECTED, p[0].getId());
		assertEquals(IProblem.SEVERITY_ERROR, p[0].getSeverity());
		assertEquals(7, p[0].getOffset());
		assertEquals(1, p[0].getLength());
	}
	
	public void test_IDENTIFIER_EXPECTED_FOR_MIXIN_3() {
		IProblem[] p = getProblems(" mixin .!() m;", 3);
		  
		assertEquals(IProblem.IDENTIFIER_EXPECTED, p[0].getId());
		assertEquals(IProblem.SEVERITY_ERROR, p[0].getSeverity());
		assertEquals(8, p[0].getOffset());
		assertEquals(1, p[0].getLength());
	}
	
	public void test_SEMICOLON_EXPECTED_FOLLOWING_MIXIN() {
		IProblem p = getProblem(" mixin Foo!() m 2");
		  
		assertEquals(IProblem.SEMICOLON_EXPECTED, p.getId());
		assertEquals(IProblem.SEVERITY_ERROR, p.getSeverity());
		assertEquals(16, p.getOffset());
		assertEquals(1, p.getLength());
	}
	
	public void test_TEMPLATE_ARGUMENT_LIST_EXPECTED() {
		IProblem[] p = getProblems(" mixin Foo! m 2", 2);
		  
		assertEquals(IProblem.TEMPLATE_ARGUMENT_LIST_EXPECTED, p[0].getId());
		assertEquals(IProblem.SEVERITY_ERROR, p[0].getSeverity());
		assertEquals(12, p[0].getOffset());
		assertEquals(1, p[0].getLength());
	}
	
	public void test_COMMA_EXPECTED_SEPARATING_STRUCT_INITIALIZER() {
		IProblem p = getProblem(" X x = { a b };");
		  
		assertEquals(IProblem.COMMA_EXPECTED, p.getId());
		assertEquals(IProblem.SEVERITY_ERROR, p.getSeverity());
		assertEquals(11, p.getOffset());
		assertEquals(1, p.getLength());
	}
	
	public void test_COMMA_EXPECTED_SEPARATING_ARRAY_INITIALIZER() {
		IProblem[] p = getProblems(" X x = [ 1 2 ];", 3);
		  
		assertEquals(IProblem.COMMA_EXPECTED, p[0].getId());
		assertEquals(IProblem.SEVERITY_ERROR, p[0].getSeverity());
		assertEquals(11, p[0].getOffset());
		assertEquals(1, p[0].getLength());
	}
	
	public void test_REDUNDANT_STORAGE_CLASS() {
		IProblem p = getProblem(" alias static static int x;");
		  
		assertEquals(IProblem.REDUNDANT_STORAGE_CLASS, p.getId());
		assertEquals(IProblem.SEVERITY_ERROR, p.getSeverity());
		assertEquals(14, p.getOffset());
		assertEquals(6, p.getLength());
	}
	
	public void test_REDUNDANT_STORAGE_CLASS_2() {
		IProblem p = getProblem(" alias extern extern int x;");
		  
		assertEquals(IProblem.REDUNDANT_STORAGE_CLASS, p.getId());
		assertEquals(IProblem.SEVERITY_ERROR, p.getSeverity());
		assertEquals(14, p.getOffset());
		assertEquals(6, p.getLength());
	}
	
	public void test_MISSING_BODY_AFTER_IN_OR_OUT() {
		IProblem p = getProblem(" void bla() in { };");
		  
		assertEquals(IProblem.MISSING_BODY_AFTER_IN_OR_OUT, p.getId());
		assertEquals(IProblem.SEVERITY_ERROR, p.getSeverity());
		assertEquals(6, p.getOffset());
		assertEquals(3, p.getLength());
	}
	
	public void test_REDUNDANT_IN_STATEMENT() {
		IProblem p = getProblem(" void bla() in { } in { } body { }");
		  
		assertEquals(IProblem.REDUNDANT_IN_STATEMENT, p.getId());
		assertEquals(IProblem.SEVERITY_ERROR, p.getSeverity());
		assertEquals(19, p.getOffset());
		assertEquals(2, p.getLength());
	}
	
	public void test_REDUNDANT_OUT_STATEMENT() {
		IProblem p = getProblem(" void bla() out { } out { } body { }");
		  
		assertEquals(IProblem.REDUNDANT_OUT_STATEMENT, p.getId());
		assertEquals(IProblem.SEVERITY_ERROR, p.getSeverity());
		assertEquals(20, p.getOffset());
		assertEquals(3, p.getLength());
	}
	
	public void test_OUT_IDENTIFIER_EXPECTED() {
		IProblem[] p = getProblems(" void bla() out () body { }", 3);
		  
		assertEquals(IProblem.IDENTIFIER_EXPECTED, p[0].getId());
		assertEquals(IProblem.SEVERITY_ERROR, p[0].getSeverity());
		assertEquals(17, p[0].getOffset());
		assertEquals(1, p[0].getLength());
	}
	
	private IProblem getProblem(String s) {
		ParserFacade facade = new ParserFacade();
		ICompilationUnit unit = facade.parseCompilationUnit(s);
		IProblem[] problems = unit.getProblems();
		assertEquals(1, problems.length);
  
		return problems[0];
	}
	
	private IProblem[] getProblems(String s, int expected) {
		ParserFacade facade = new ParserFacade();
		ICompilationUnit unit = facade.parseCompilationUnit(s);
		IProblem[] problems = unit.getProblems();
		assertEquals(expected, problems.length);
		
		return problems;
	}

}
