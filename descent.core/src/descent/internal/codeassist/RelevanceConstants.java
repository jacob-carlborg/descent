package descent.internal.codeassist;

public interface RelevanceConstants {
	
	int R_DEFAULT = 0;
	int R_BUILTIN_PROPERTY = 1;
	int R_INTERESTING_BUILTIN_PROPERTY = 3;
	int R_VERY_INTERESTING_BUILTIN_PROPERTY = 4;
	int R_INTERESTING = 5;
	int R_CASE = 10;
	int R_CAMEL_CASE = 5;
	int R_EXACT_NAME = 4;
	int R_EXPECTED_TYPE = 20;
	int R_EXACT_EXPECTED_TYPE = 30;
	int R_LABEL = 10;
	int R_INTERFACE = 20;
	int R_CLASS = 20;
	int R_STRUCT = 20;
	int R_UNION = 20;
	int R_TEMPLATE = 20;
	int R_ENUM = 20;
	int R_METHOD = 20;
	int R_ALIAS = 18;
	int R_TYPEDEF = 19;
	int R_VAR = 20;
	int R_LOCAL_VAR = 25;
	int R_ANNOTATION = 20;
	int R_EXCEPTION = 20;
	int R_COMPILATION_UNIT = 20;
	int R_ENUM_CONSTANT = 70;
	int R_ABSTRACT_METHOD = 20;
	int R_NON_STATIC = 11;
	int R_UNQUALIFIED = 3;
	int R_QUALIFIED = 2;
	int R_NAME_FIRST_PREFIX = 6;
	int R_NAME_PREFIX = 5;
	int R_NAME_FIRST_SUFFIX = 4;
	int R_NAME_SUFFIX = 3;
	int R_NAME_LESS_NEW_CHARACTERS = 15;
	int R_METHOD_OVERIDE = 30;
	int R_NON_RESTRICTED = 3;
	int R_TRUE_OR_FALSE = 40;
	int R_INLINE_TAG = 31;
	int R_VALUE_TAG = 31;
	int R_CONSTRUCTOR = 30;
	int R_FUNCTION_POINTER_CALL = 20;
	int R_FUNCTION_POINTER_CALL_WANT = 20;
	int R_WITH = 20;
	int R_NEW = 30;

}
