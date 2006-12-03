package descent.core.compiler;

/**
 * A problem found while compiling or building a model
 * from source.
 */
public interface IProblem {
	
	int SEVERITY_ERROR = 1;
	int SEVERITY_WARNING = 2;
	
	int UNTERMINATED_BLOCK_COMMENT = 1;
	int UNTERMINATED_PLUS_BLOCK_COMMENT = 2;
	int INCORRECT_NUMBER_OF_HEX_DIGITS_IN_ESCAPE_SEQUENCE = 3;
	int UNDEFINED_ESCAPE_HEX_SEQUENCE = 4;
	int UNTERMINATED_STRING_CONSTANT = 5;
	int ODD_NUMBER_OF_CHARACTERS_IN_HEX_STRING = 6;
	int NON_HEX_CHARACTER = 7;
	int UNTERMINATED_CHARACTER_CONSTANT = 8;
	int BINARY_DIGIT_EXPECTED = 9;
	int OCTAL_DIGIT_EXPECTED = 10;
	int HEX_DIGIT_EXPECTED = 11;
	int UNSUPPORTED_CHAR = 12;
	int INVALID_UTF_CHARACTER = 13;
	int THREE_EQUALS_IS_NO_LONGER_LEGAL = 14;
	int NOT_TWO_EQUALS_IS_NO_LONGER_LEGAL = 15;
	int L_SUFFIX_DEPRECATED = 16;
	int INVALID_PRAGMA_SYNTAX = 17;
	int UNRECOGNIZED_CHARACTER_ENTITY = 18;
	int UNTERMINATED_NAMED_ENTITY = 19;
	int UNDEFINED_ESCAPE_SEQUENCE = 20;
	int INVALID_UTF_8_SEQUENCE = 21;
	int INTEGER_OVERFLOW = 22;
	int SIGNED_INTEGER_OVERFLOW = 23;
	int UNRECOGNIZED_TOKEN = 24;
	int BINARY_EXPONENT_PART_REQUIRED = 25;
	int EXPONENT_EXPECTED = 26;
	int I_SUFFIX_DEPRECATED = 27;
	
	int ENUM_MEMBER_EXPECTED = 108;
	int ENUM_DECLARATION_IS_INVALID = 109;
	int MISMATCHED_STRING_LITERAL_POSTFIXES = 110;
	int ANONYMOUS_CLASSES_NOT_ALLOWED = 101;
	int MEMBERS_EXPECTED = 102;
	int RIGHT_CURLY_EXPECTED_FOLLOWING_MEMBER_DECLARATIONS_IN_AGGREGATE = 103;
	int CURLIES_EXPECTED_FOLLOWING_AGGREGATE_DECLARATION = 104;
	int NO_IDENTIFIER_FOR_DECLARATION = 105;
	int NO_IDENTIFIER_FOR_DECLARATOR = 106;
	int ALIAS_CANNOT_HAVE_INITIALIZER = 107;
	int C_STYLE_CAST_ILLEGAL = 108;
	int UNRECOGNIZED_DECLARATION = 109;
	int INTEGER_EXPECTED = 110;
	int MATCHING_CURLY_EXPECTED = 112;
	int IDENTIFIER_OR_INTEGER_EXPECTED = 113;
	int SEMICOLON_EXPECTED = 114;
	int CONDITION_EXPECTED_FOLLOWING_VERSION = 115;
	int EXPRESSION_EXPECTED = 116;
	int DECLARATION_EXPECTED = 117;
	int INVALID_LINKAGE_IDENTIFIER = 119;
	int VARIADIC_ARGUMENT_CANNOT_BE_OUT_OR_INOUT = 120;
	int DEFAULT_ARGUMENT_EXPECTED = 121;
	int VARIADIC_NOT_ALLOWED_IN_DELETE = 122;
	int BASE_CLASS_EXPECTED = 123;
	int TEMPLATE_IDENTIFIER_EXPECTED = 124;
	int PARENTHESIZED_TEMPLATE_PARAMETER_LIST_EXPECTED = 125;
	int IDENTIFIER_EXPECTED = 127;
	int BASIC_TYPE_EXPECTED = 129;
	int UNEXPECTED_IDENTIFIER_IN_DECLARATOR = 130;
	int REDUNDANT_STORAGE_CLASS = 131;
	int USE_BRACES_FOR_AN_EMPTY_STATEMENT = 132;
	int TEMPLATE_ARGUMENT_LIST_EXPECTED = 133;
	int MULTIPLE_DECLARATIONS_MUST_HAVE_THE_SAME_TYPE = 134;
	int MISSING_BODY_AFTER_IN_OR_OUT = 135;
	int REDUNDANT_IN_STATEMENT = 136;
	int REDUNDANT_OUT_STATEMENT = 137;
	int COMMA_EXPECTED = 138;
	int STATEMENT_EXPECTED_TO_BE_CURLIES = 139;
	int EQUALS_EXPECTED = 140;
	int INVALID_SCOPE_IDENTIFIER = 141;
	int ON_SCOPE_DEPRECATED = 142;
	int CATCH_OR_FINALLY_EXPECTED = 143;
	int DOLLAR_INVALID_OUTSIDE_BRACKETS = 144;
	int STATEMENT_EXPECTED = 145;
	int IFTYPE_DEPRECATED = 146;
	int INVALID_IFTYPE_SYNTAX = 147;
	int DEPRECATED_IF_AUTO = 148;
	int FOUND_SOMETHING_WHEN_EXPECTING_SOMETHING = 150;
	int VARIADIC_TEMPLATE_PARAMETER_MUST_BE_LAST_ONE = 151;
	int NEED_SIZE_OF_RIGHTMOST_ARRAY = 152;
	
	int PROPERTY_CANNOT_BE_REDEFINED = 1001;
	int STRUCTS_UNIONS_CANT_BE_ABSTRACT = 1002;
	
	/**
	 * A unique identifier of the problem, listed
	 * in this interface.
	 */
	int getId();
	
	/**
	 * The error message.
	 */
	String getMessage();
	
	/**
	 * Returns the severity of the problem. One of
	 * SEVERITY_ERROR or SEVERITY_WARNING.
	 */
	int getSeverity();
	
	/**
	 * The offset of the problem starts.
	 */
	int getOffset();
	
	/**
	 * The length of the problem.
	 */
	int getLength();

}
