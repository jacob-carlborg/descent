package descent.tests.mars;

import descent.core.compiler.IProblem;
import descent.core.dom.AST;
import descent.core.dom.CompilationUnit;

public class Problems_Test extends Parser_Test {

	public void test_INVALID_UTF_CHARACTER() {
		IProblem p = getProblem(" char c = \"\\UFFFFFFFF\";");
		assertError(p, IProblem.InvalidUtfCharacter, 11, 10);
	}

	/*
	public void test_NON_HEX_CHARACTER2() {
		IProblem[] p = getProblems(" char c = x\"€1\";", 2);

		assertEquals(IProblem.INVALID_UTF_8_SEQUENCE, p[0].getId());
		assertEquals(IProblem.SEVERITY_ERROR, p[0].getSeverity());
		assertEquals(12, p[0].getOffset());
		assertEquals(1, p[0].getLength());

		assertEquals(IProblem.NON_HEX_CHARACTER, p[1].getId());
		assertEquals(IProblem.SEVERITY_ERROR, p[1].getSeverity());
		assertEquals(12, p[1].getOffset());
		assertEquals(1, p[1].getLength());
	}

	public void test_INVALID_UTF_8_SEQUENCE() {
		IProblem[] p = getProblems(" char c = \"€\";", 1);

		assertEquals(IProblem.INVALID_UTF_8_SEQUENCE, p[0].getId());
		assertEquals(IProblem.SEVERITY_ERROR, p[0].getSeverity());
		assertEquals(11, p[0].getOffset());
		assertEquals(1, p[0].getLength());
	}

	public void test_INVALID_UTF_8_SEQUENCE_2() {
		IProblem[] p = getProblems(" €", 2);

		assertEquals(IProblem.INVALID_UTF_8_SEQUENCE, p[0].getId());
		assertEquals(IProblem.SEVERITY_ERROR, p[0].getSeverity());
		assertEquals(1, p[0].getOffset());
		assertEquals(1, p[0].getLength());
	}

	public void test_INVALID_UTF_8_SEQUENCE_3() {
		IProblem[] p = getProblems("#!€", 1);

		assertEquals(IProblem.INVALID_UTF_8_SEQUENCE, p[0].getId());
		assertEquals(IProblem.SEVERITY_ERROR, p[0].getSeverity());
		assertEquals(2, p[0].getOffset());
		assertEquals(1, p[0].getLength());
	}
	
	public void test_INVALID_UTF_8_SEQUENCE_4() {
		//IProblem[] p = getProblems("/*€ int a = 2;", 1);

		assertEquals(IProblem.INVALID_UTF_8_SEQUENCE, p[0].getId());
		assertEquals(IProblem.SEVERITY_ERROR, p[0].getSeverity());
		assertEquals(2, p[0].getOffset());
		assertEquals(1, p[0].getLength());
	}

	public void test_INVALID_UTF_8_SEQUENCE_5() {
		IProblem[] p = getProblems("//€", 1);

		assertEquals(IProblem.INVALID_UTF_8_SEQUENCE, p[0].getId());
		assertEquals(IProblem.SEVERITY_ERROR, p[0].getSeverity());
		assertEquals(2, p[0].getOffset());
		assertEquals(1, p[0].getLength());
	}

	public void test_INVALID_UTF_8_SEQUENCE_6() {
		IProblem[] p = getProblems("char c = `€`;", 1);

		assertEquals(IProblem.INVALID_UTF_8_SEQUENCE, p[0].getId());
		assertEquals(IProblem.SEVERITY_ERROR, p[0].getSeverity());
		assertEquals(10, p[0].getOffset());
		assertEquals(1, p[0].getLength());
	}
	*/

	public void test_UNTERMINATED_BLOCK_COMMENT() {
		IProblem p = getProblem(" /*  ");
		assertError(p, IProblem.UnterminatedBlockComment, 1, 4);
	}

	public void test_UNTERMINATED_PLUS_BLOCK_COMMENT() {
		IProblem p = getProblem(" /+  ");
		assertError(p, IProblem.UnterminatedPlusBlockComment, 1, 4);
	}

	public void test_INCORRECT_NUMBER_OF_HEX_DIGITS_IN_ESCAPE_SEQUENCE_x() {
		IProblem p = getProblem(" char[] c = \"\\x1\";");
		assertError(p, IProblem.IncorrectNumberOfHexDigitsInEscapeSequence, 13, 3);
	}

	public void test_INCORRECT_NUMBER_OF_HEX_DIGITS_IN_ESCAPE_SEQUENCE_u() {
		IProblem p = getProblem(" char[] c = \"\\u1\";");
		assertError(p, IProblem.IncorrectNumberOfHexDigitsInEscapeSequence, 13, 3);
	}

	public void test_INCORRECT_NUMBER_OF_HEX_DIGITS_IN_ESCAPE_SEQUENCE_U() {
		IProblem p = getProblem(" char[] c = \"\\U1\";");
		assertError(p, IProblem.IncorrectNumberOfHexDigitsInEscapeSequence, 13, 3);
	}

	public void test_UNDEFINED_ESCAPE_HEX_SEQUENCE() {
		IProblem p = getProblem(" char[] c = \"\\xT\";");
		assertError(p, IProblem.UndefinedEscapeHexSequence, 13, 3);
	}

	public void test_UNDEFINED_ESCAPE_SEQUENCE() {
		IProblem p = getProblem(" char[] c = \"\\T\";");
		assertError(p, IProblem.UndefinedEscapeSequence, 13, 2);
	}

	public void test_UNTERMINATED_STRING_CONSTANT() {
		IProblem p = getProblems(" char[] c = \"hola ;", 2)[0];
		assertError(p, IProblem.UnterminatedStringConstant, 12, 7);
	}

	public void test_UNTERMINATED_STRING_CONSTANT_WYSIWYG() {
		IProblem p = getProblems(" char[] c = `hola ;", 2)[0];
		assertError(p, IProblem.UnterminatedStringConstant, 12, 7);
	}

	public void test_UNTERMINATED_STRING_CONSTANT_HEX() {
		IProblem p = getProblems(" char[] c = x\"ABCD ;", 3)[1];
		assertError(p, IProblem.UnterminatedStringConstant, 12, 8);
	}

	public void test_ODD_NUMBER_OF_CHARACTERS_IN_HEX_STRING() {
		IProblem p = getProblem(" char[] c = x\"123\";");
		assertError(p, IProblem.OddNumberOfCharactersInHexString, 12, 6);
	}

	public void test_NON_HEX_CHARACTER() {
		IProblem p = getProblem(" char[] c = x\"1T34\";");
		assertError(p, IProblem.NonHexCharacter, 15, 1);
	}

	public void test_UNTERMINATED_CHARACTER_CONSTANT() {
		IProblem p = getProblem(" char[] c = ' ;");
		assertError(p, IProblem.UnterminatedCharacterConstant, 12, 2);
	}

	public void test_UNTERMINATED_CHARACTER_CONSTANT_2() {
		IProblem p = getProblem(" char[] c = '';");
		assertError(p, IProblem.UnterminatedCharacterConstant, 12, 2);
	}

	public void test_L_SUFFIX_DEPRECATED() {
		IProblem p = getProblem(" long x = 1l;");
		assertError(p, IProblem.LSuffixDeprecated, 11, 1);
	}
	
	public void test_L_SUFFIX_DEPRECATED_2() {
		IProblem p = getProblem(" long x = 1.0l;");
		assertError(p, IProblem.LSuffixDeprecated, 13, 1);
	}
	
	public void test_I_SUFFIX_DEPRECATED_2() {
		IProblem p = getProblem(" long x = 1.0I;");
		assertError(p, IProblem.ISuffixDeprecated, 13, 1);
	}

	public void test_INTEGER_OVERFLOW() {
		IProblem p = getProblem(" int x = 18446744073709551616L;");
		assertError(p, IProblem.IntegerOverflow, 9, 21);
	}
	
	public void test_SIGNED_INTEGER_OVERFLOW() {
		IProblem p = getProblem(" int x = 9223372036854775808;");
		assertError(p, IProblem.SignedIntegerOverflow, 9, 19);
	}

	public void test_SIGNED_INTEGER_OVERFLOW_2() {
		IProblem p = getProblem(" int x = 9223372036854775808L;");
		assertError(p, IProblem.SignedIntegerOverflow, 9, 20);
	}
	
	public void test_UNRECOGNIZED_TOKEN() {
		IProblem p = getProblem(" int x = 1uu;");
		assertError(p, IProblem.UnrecognizedToken, 11, 1);
	}
	
	public void test_UNRECOGNIZED_TOKEN_2() {
		IProblem p = getProblem(" int x = 1LL;");
		assertError(p, IProblem.UnrecognizedToken, 11, 1);
	}

	public void test_BINARY_DIGIT_EXPECTED() {
		IProblem p = getProblem(" int a = 0bT;");
		assertError(p, IProblem.BinaryDigitExpected, 11, 1);
	}
	
	public void test_BINARY_EXPONENT_PART_REQUIRED() {
		IProblem p = getProblem(" int a = 0xA.ABCDE;");
		assertError(p, IProblem.BinaryExponentPartRequired, 18, 1);
	}
	
	public void test_EXPONENT_EXPECTED() {
		IProblem p = getProblem(" int a = 0xA.ABCDEp;12;");
		assertError(p, IProblem.ExponentExpected, 19, 1);
	}

	 public void test_OCTAL_DIGIT_EXPECTED() {
		 IProblem p = getProblem("int a = 019;");
		 assertError(p, IProblem.OctalDigitExpected, 10, 1);
	 }

	public void test_HEX_DIGIT_EXPECTED() {
		IProblem p = getProblems(" int a = 0xX c = new C();", 2)[0];
		assertError(p, IProblem.HexDigitExpected, 11, 1);
	}

	public void test_UNSUPPORTED_CHAR() {
		IProblem p = getProblem(" ");
		assertError(p, IProblem.UnsupportedCharacter, 1, 1);
	}

	/*
	 public void test_INVALID_UTF_CHARACTER() {
	 IProblem p = getProblem("char c = \\uFFFF;");
	 
	 assertEquals(IProblem.INVALID_UTF_CHARACTER, p.getId());
	 assertEquals(0, p.getOffset());
	 assertEquals(1, p.getLength());
	 }
	 */

	public void test_IDENTIFIER_EXPECTED_FOLLOWING_IMPORT() {
		IProblem p = getProblem(" import ;");
		assertError(p, IProblem.ParsingErrorInsertTokenAfter, 1, 6);
	}

	public void test_IDENTIFIER_EXPECTED_FOLLOWING_PACKAGE_FOR_IMPORT() {
		IProblem p = getProblem(" import bla.;");
		assertError(p, IProblem.ParsingErrorInsertTokenAfter, 11, 1);
	}

	public void test_IDENTIFIER_EXPECTED_FOLLOWING_COLON() {
		IProblem p = getProblem(" import stdio.std : ;");
		assertError(p, IProblem.ParsingErrorInsertTokenAfter, 18, 1);
	}

	public void test_IDENTIFIER_EXPECTED_FOLLOWING_ALIAS() {
		IProblem p = getProblem(" import stdio.std : al = ;");
		assertError(p, IProblem.ParsingErrorInsertTokenAfter, 23, 1);
	}

	public void test_SEMICOLON_EXPECTED_FOLLOWING_IMPORT_DECLARATION() {
		IProblem p = getProblem(" import bla");
		assertError(p, IProblem.ParsingErrorInsertTokenAfter, 8, 3);
	}

	public void test_ENUM_MEMBER_EXPECTED() {
		IProblem p = getProblem(" enum Bla { a, class }");
		assertError(p, IProblem.ParsingErrorInsertToComplete, 13, 1);
	}

	public void test_ENUM_DECLARATION_INVALID() {
		IProblem p = getProblem(" enum int a = 2;");
		assertError(p, IProblem.EnumDeclarationIsInvalid, 1, 4);
	}

	public void test_MISMATCHED_STRING_LITERAL_POSTFIXES() {
		IProblem p = getProblem(" char[] s = \"hola\"c \"hola\"c \"chau\"d;");
		assertError(p, IProblem.MismatchedStringLiteralPostfixes, 20, 15);
	}

	public void test_RIGHT_CURLY_EXPECTED_FOLLOWING_MEMBER_DECLARATIONS_IN_AGGREGATE_2() {
		IProblem p = getProblem(" class Ble { ");
		assertError(p, IProblem.ParsingErrorInsertTokenAfter, 11, 1);
	}

	public void test_SEMICOLON_EXPECTED_TO_CLOSE_DECLARATION() {
		IProblem p = getProblem(" int x");
		assertError(p, IProblem.ParsingErrorInsertTokenAfter, 5, 1);
	}

	public void test_THREE_EQUALS_IS_DEPRECATED() {
		IProblem p = getProblem(" bool x = 1 === 2;", AST.D1);
		assertError(p, IProblem.ThreeEqualsIsNoLongerLegal, 12, 3);
	}

	public void test_ALIAS_CANNOT_HAVE_INITIALIZER() {
		IProblem p = getProblem(" alias int Bla = 1;");
		assertError(p, IProblem.AliasCannotHaveInitializer, 15, 3);
	}

	public void test_UNRECOGNIZED_DECLARATION() {
		IProblem p = getProblem(" }");
		assertError(p, IProblem.ParsingErrorDeleteToken, 1, 1);
	}

	public void test_INTEGER_EXPECTED() {
		IProblem p = getProblem(" align(bla) { }");
		assertError(p, IProblem.ParsingErrorInsertTokenAfter, 6, 1);
	}

	public void test_DECLARATION_EXPECTED_FOLLOWING_ATTRIBUTE() {
		IProblem p = getProblem(" public;");
		assertError(p, IProblem.ParsingErrorInsertToComplete, 1, 6);
	}

	public void test_SEMICOLON_EXPECTED_1() {
		IProblem p = getProblem(" version = 2 bla");
		assertError(p, IProblem.ParsingErrorInsertTokenAfter, 11, 1);
	}

	public void test_SEMICOLON_EXPECTED_2() {
		IProblem p = getProblem(" debug = 2 bla");
		assertError(p, IProblem.ParsingErrorInsertTokenAfter, 9, 1);
	}

	public void test_IDENTIFIER_OR_INTEGER_EXPECTED_1() {
		IProblem p = getProblem(" debug = 2.0;");
		assertError(p, IProblem.ParsingErrorInsertTokenAfter, 7, 1);
	}

	public void test_IDENTIFIER_OR_INTEGER_EXPECTED_2() {
		IProblem p = getProblem(" version = 2.0;");
		assertError(p, IProblem.ParsingErrorInsertTokenAfter, 9, 1);
	}

	public void test_IDENTIFIER_OR_INTEGER_EXPECTED_3() {
		IProblem p = getProblem(" version(2.0) { }");
		assertError(p, IProblem.ParsingErrorInsertTokenAfter, 8, 1);
	}

	public void test_IDENTIFIER_OR_INTEGER_EXPECTED_4() {
		IProblem p = getProblem(" debug(2.0) { }");
		assertError(p, IProblem.ParsingErrorInsertTokenAfter, 6, 1);
	}

	public void test_CONDITION_EXPECTED_FOLLOWING_VERSION() {
		IProblem p = getProblems(" version 2", 2)[0];
		assertError(p, IProblem.ParsingErrorInsertToComplete, 1, 7);
	}

	public void test_INVALID_LINKAGE_IDENTIFIER() {
		IProblem p = getProblem(" extern(bla) { }");
		assertError(p, IProblem.InvalidLinkageIdentifier, 8, 3);
	}

	public void test_EXPRESSION_EXPECTED_FOLLOWING_STATIC_IF() {
		IProblem p = getProblems(" static if 2", 2)[0];
		assertError(p, IProblem.ParsingErrorInsertToComplete, 8, 2);
	}

	public void test_VARIADIC_ARGUMENT_CANNOT_BE_OUT_OR_INOUT() {
		IProblem p = getProblem(" void bla(out int ...) { }");
		assertError(p, IProblem.VariadicArgumentCannotBeOutOrInout, 10, 3);
	}

	public void test_DEFAULT_ARGUMENT_EXPECTED() {
		IProblem p = getProblem(" void bla(int a = 2, int b) { }");
		assertError(p, IProblem.ParsingErrorInsertTokenAfter, 25, 1);
	}

	public void test_VARIADIC_NOT_ALLOWED_IN_DELETE() {
		IProblem p = getProblem(" delete(int ...) { }");
		assertError(p, IProblem.VariadicNotAllowedInDelete, 1, 6);
	}

	public void test_TEMPLATE_IDENTIFIER_EXPECTED() {
		IProblem p = getProblem(" template class Bla { }");
		assertError(p, IProblem.ParsingErrorInsertTokenAfter, 1, 8);
	}

	public void test_PARENTHESIZED_TEMPLATE_PARAMETER_LIST_EXPECTED() {
		IProblem p = getProblems(" template Bla class Bla { }", 2)[0];
		assertError(p, IProblem.ParsingErrorInsertToComplete, 10, 3);
	}

	public void test_IDENTIFIER_EXPECTED_FOR_TEMPLATE_PARAMETER() {
		IProblem p = getProblems(" template Bla(alias class Bla { }", 2)[0];
		assertError(p, IProblem.ParsingErrorInsertTokenAfter, 14, 5);
	}

	public void test_MULTIPLE_DECLARATIONS_MUST_HAVE_THE_SAME_TYPE() {
		IProblem p = getProblem(" int *x, y[];");
		assertError(p, IProblem.MultipleDeclarationsMustHaveTheSameType, 9, 1);
	}

	public void test_INVALID_PRAGMA_SYNTAX() {
		IProblem p = getProblem(" #");
		assertError(p, IProblem.InvalidPragmaSyntax, 1, 1);
	}
	
	public void test_INVALID_PRAGMA_SYNTAX_2() {
		IProblem p = getProblem(" #line");
		assertError(p, IProblem.InvalidPragmaSyntax, 1, 5);
	}
	
	public void test_INVALID_PRAGMA_SYNTAX_3() {
		IProblem p = getProblem(" #line 1 int x = 2;");
		assertError(p, IProblem.InvalidPragmaSyntax, 1, 18);
	}
	
	public void test_INVALID_PRAGMA_SYNTAX_4() {
		IProblem p = getProblems(" #line 1 __FILE__ \" int x = 2;", 1)[0];
		assertError(p, IProblem.InvalidPragmaSyntax, 1, 29);
	}

	public void test_UNRECOGNIZED_CHARACTER_ENTITY() {
		IProblem p = getProblem("char[] s = \"\\&pepe;\";");
		assertError(p, IProblem.UnrecognizedCharacterEntity, 13, 6);
	}

	public void test_UNTERMINATED_NAMED_ENTITY() {
		IProblem p = getProblem("char[] s = \"\\&pepe\";");
		assertError(p, IProblem.UnterminatedNamedEntity, 13, 5);
	}

	public void test_VARIADIC_TEMPLATE_PARAMETER_MUST_BE_LAST_ONE() {
		IProblem p = getProblem("template Foo(T ..., U, V ...) { }");
		assertError(p, IProblem.VariadicTemplateParameterMustBeTheLastOne, 23, 5);
	}

	public void test_PRAGMA_IDENTIFIER_EXPECTED() {
		IProblem p = getProblem("pragma(2);");
		assertError(p, IProblem.ParsingErrorInsertTokenAfter, 6, 1);
	}

	public void test_PRAGMA_CHECK_SINGLE_PARAMETER_NOT_RPAREN() {
		IProblem p = getProblem("pragma(msg;");
		assertError(p, IProblem.ParsingErrorInsertTokenAfter, 7, 3);
	}

	public void test_PRAGMA_CHECK_SINGLE_PARAMETER_NOT_RPAREN_OUT_OF_MEMORY_BUG() {
		IProblem p = getProblem("pragma(msg,;");
		assertError(p, IProblem.ParsingErrorInsertTokenAfter, 10, 1);
	}

	public void test_ENUM_DECLARATION_IS_INVALID() {
		IProblem p = getProblems(" enum {", 2)[0]; // The second one is "it dosen't have members"
		assertError(p, IProblem.EnumDeclarationIsInvalid, 1, 4);
	}

	public void test_MEMBERS_OF_TEMPLATE_DECLARATION_EXPECTED() {
		IProblem p = getProblems(" template T() int", 2)[0];
		assertError(p, IProblem.ParsingErrorInsertToComplete, 12, 1);
	}

	public void test_SEMICOLON_EXPECTED_FOLLOWING_AUTO_DECLARATION() {
		IProblem p = getProblem(" auto x = 2");
		assertError(p, IProblem.ParsingErrorInsertTokenAfter, 10, 1);
	}

	public void test_IFTYPE_DECPRECATED() {
		IProblem p = getProblem(" iftype(x) { }", AST.D1);
		assertError(p, IProblem.IftypeDeprecated, 1, 6);
	}

	public void test_INVALID_IFTYPE_SYNTAX() {
		IProblem p = getProblems(" iftype int", 2, AST.D1)[0];
		assertError(p, IProblem.ParsingErrorInsertToComplete, 1, 6);
	}

	public void test_IDENTIFIER_EXPECTED_FOR_TEMPLATE_PARAMETER_2() {
		IProblem p = getProblems(" template T(2)", 4)[0];
		assertError(p, IProblem.ParsingErrorInsertTokenAfter, 11, 1);
	}

	public void test_IDENTIFIER_EXPECTED_FOR_MIXIN() {
		IProblem p = getProblem(" mixin typeof(2).");
		assertError(p, IProblem.ParsingErrorDeleteToken, 16, 1);
	}

	public void test_NO_IDENTIFIER_FOR_TEMPLATE_VALUE_PARAMETER() {
		IProblem p = getProblems(" template Temp(int 2) { }", 6)[0];
		assertError(p, IProblem.NoIdentifierForTemplateValueParameter, 19, 1);
	}

	public void test_IDENTIFIER_EXPECTED_FOR_MIXIN_2() {
		IProblem p = getProblem(" mixin ");
		assertError(p, IProblem.ParsingErrorDeleteToken, 1, 5);
	}

	public void test_SEMICOLON_EXPECTED_FOLLOWING_MIXIN() {
		IProblem p = getProblem(" mixin Foo!()");
		assertError(p, IProblem.ParsingErrorInsertTokenAfter, 12, 1);
	}

	public void test_TEMPLATE_ARGUMENT_LIST_EXPECTED() {
		IProblem p = getProblems(" mixin Foo!", 2)[0];
		assertError(p, IProblem.ParsingErrorInsertToComplete, 10, 1);
	}

	public void test_COMMA_EXPECTED_SEPARATING_STRUCT_INITIALIZER() {
		IProblem p = getProblem(" X x = { a b };");
		assertError(p, IProblem.ParsingErrorInsertTokenAfter, 9, 1);
	}

	public void test_COMMA_EXPECTED_SEPARATING_ARRAY_INITIALIZER() {
		IProblem p = getProblems(" X x = [ 1 2 ];", 3)[0];
		assertError(p, IProblem.ParsingErrorInsertTokenAfter, 9, 1);
	}

	public void test_REDUNDANT_STORAGE_CLASS() {
		IProblem p = getProblem(" alias static static int x;");
		assertError(p, IProblem.RedundantStorageClass, 14, 6);
	}

	public void test_REDUNDANT_STORAGE_CLASS_2() {
		IProblem p = getProblem(" alias extern extern int x;");
		assertError(p, IProblem.RedundantStorageClass, 14, 6);
	}

	public void test_MISSING_BODY_AFTER_IN_OR_OUT() {
		IProblem p = getProblem(" void bla() in { };");
		assertError(p, IProblem.ParsingErrorInsertToComplete, 17, 1);
	}

	public void test_REDUNDANT_IN_STATEMENT() {
		IProblem p = getProblem(" void bla() in { } in { } body { }");
		assertError(p, IProblem.RedundantInStatement, 19, 2);
	}

	public void test_REDUNDANT_OUT_STATEMENT() {
		IProblem p = getProblem(" void bla() out { } out { } body { }");
		assertError(p, IProblem.RedundantOutStatement, 20, 3);
	}

	public void test_OUT_IDENTIFIER_EXPECTED() {
		IProblem p = getProblem(" void bla() out () { } body { }");
		assertError(p, IProblem.ParsingErrorInsertTokenAfter, 16, 1);
	}

	public void test_EQUALS_EXPECTED_IN_IF_AUTO() {
		IProblem p = getProblems(" void x() { if (auto i) { } }", 2)[0];
		assertError(p, IProblem.ParsingErrorInsertTokenAfter, 16, 4);
	}

	public void test_IDENTIFIER_EXPECTED_IN_IF_AUTO() {
		IProblem p = getProblems(" void x() { if (auto = ) { } }", 2)[0];
		assertError(p, IProblem.ParsingErrorInsertTokenAfter, 16, 4);
	}

	public void test_DEPRECATED_IF_AUTO() {
		IProblem p = getProblem(" void x() { if (a; b) { } }");
		assertError(p, IProblem.IfAutoDeprecated, 16, 4);
	}

	public void test_SCOPE_IDENTIFIER_EXPECTED() {
		IProblem p = getProblem(" void x() { scope(); }");
		assertError(p, IProblem.ParsingErrorInsertTokenAfter, 17, 1);
	}

	public void test_INVALID_SCOPE_IDENTIFIER() {
		IProblem p = getProblem(" void x() { scope(bla) { } }");
		assertError(p, IProblem.InvalidScopeIdentifier, 18, 3);
	}

	public void test_ON_SCOPE_DEPRECATED() {
		IProblem p = getProblem(" void bla() { on_scope_exit { } }", AST.D1);
		assertError(p, IProblem.OnScopeDeprecated, 14, 13);
	}

	public void test_STATEMENT_PRAGMA_IDENTIFIER_EXPECTED() {
		IProblem p = getProblems(" void bla() { pragma(2) { } }", 2)[0];
		assertError(p, IProblem.ParsingErrorInsertTokenAfter, 20, 1);
	}

	public void test_IDENTIFIER_EXPECTED_FOLLOWING_GOTO() {
		IProblem p = getProblems(" void bla() { goto 2; }", 2)[0];
		assertError(p, IProblem.ParsingErrorInsertTokenAfter, 14, 4);
	}

	public void test_CATCH_OR_FINALLY_EXPECTED() {
		IProblem p = getProblem(" void bla() { try { } }");
		assertError(p, IProblem.ParsingErrorInsertToComplete, 20, 1);
	}

	public void test_TYPE_DOT_ID_EXPECTED() {
		IProblem p = getProblem(" void bla() { int a = typeof(1).;; }");
		assertError(p, IProblem.ParsingErrorInsertTokenAfter, 31, 1);
	}

	public void test_INVALID_IFTYPE_SYNTAX_2() {
		IProblem p = getProblem(" void bla() { int a = is ; ; }");
		assertError(p, IProblem.ParsingErrorInsertToComplete, 22, 2);
	}
	
	public void test_NEED_SIZE_OF_RIGHTMOST_ARRAY() {
		IProblem p = getProblem(" int[char] a = new int[char];");
		assertError(p, IProblem.NeedSizeOfRightmostArray, 23, 4);
	}
	
	public void test_C_STYLE_CAST_ILLEGAL() {
		IProblem p = getProblem(" int a = (int) 2;");
		assertError(p, IProblem.CStyleCastIllegal, 9, 7);
	}
	
	public void test_IDENTIFIER_EXPECTED() {
		IProblem p = getProblem(" int a = (int).;");
		assertError(p, IProblem.ParsingErrorInsertTokenAfter, 14, 1);
	}
	
	public void test_CLASS_MEMBERS_EXPECTED() {
		IProblem p = getProblems(" int a = new class { ", 2)[0];
		assertError(p, IProblem.ParsingErrorInsertToComplete, 19, 1);
	}
	
	public void test_CLASS_MEMBERS_EXPECTED_2() {
		IProblem p = getProblems(" int a = new class A 2; ", 3)[0];
		assertError(p, IProblem.ParsingErrorInsertToComplete, 19, 1);
	}

	private IProblem getProblem(String s) {
		return getProblem(s, AST.LATEST);
	}
	
	private IProblem getProblem(String s, int apiLevel) {
		CompilationUnit unit = getCompilationUnit(s, apiLevel);
		IProblem[] problems = unit.getProblems();
		assertEquals(1, problems.length);

		return problems[0];
	}

	private IProblem[] getProblems(String s, int expected) {
		return getProblems(s, expected, AST.LATEST);
	}
	
	private IProblem[] getProblems(String s, int expected, int apiLevel) {
		CompilationUnit unit = getCompilationUnit(s, apiLevel);
		IProblem[] problems = unit.getProblems();
		assertEquals(expected, problems.length);

		return problems;
	}

}
