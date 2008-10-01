package descent.tests.mangling;

import descent.core.Signature;
import descent.internal.compiler.parser.TY;

public interface ISignatureTest {
	
	String F = String.valueOf(Signature.C_D_LINKAGE);	
	String Z = String.valueOf(Signature.C_FUNCTION_PARAMTERS_BREAK);
	String Y = String.valueOf(Signature.C_FUNCTION_PARAMETERS_BREAK_VARARGS_UNKNOWN_TYPES);
	String X = String.valueOf(Signature.C_FUNCTION_PARAMETERS_BREAK_VARARGS_SAME_TYPE);
	String i = String.valueOf(TY.Tint32.mangleChar);
	String a = String.valueOf(TY.Tchar.mangleChar);
	String v = String.valueOf(TY.Tvoid.mangleChar);
	String D = String.valueOf(Signature.C_DELEGATE);
	String ALIAS = String.valueOf(Signature.C_ALIAS);
	String MODULE = String.valueOf(Signature.C_MODULE);
	String CLASS = String.valueOf(Signature.C_CLASS);
	String TEMPLATE = String.valueOf(Signature.C_TEMPLATE);
	String TEMPLATED_CLASS = String.valueOf(Signature.C_TEMPLATED_CLASS);
	String TEMPLATED_FUNCTION = String.valueOf(Signature.C_TEMPLATED_FUNCTION);
	String TEMPLATED_INTERFACE = String.valueOf(Signature.C_TEMPLATED_INTERFACE);
	String TEMPLATED_STRUCT = String.valueOf(Signature.C_TEMPLATED_STRUCT);
	String TEMPLATED_UNION = String.valueOf(Signature.C_TEMPLATED_UNION);
	String VARIABLE = String.valueOf(Signature.C_VARIABLE);
	String UNION = String.valueOf(Signature.C_UNION);
	String TYPEDEF = String.valueOf(Signature.C_TYPEDEF);
	String STRUCT = String.valueOf(Signature.C_STRUCT);
	String INTERFACE = String.valueOf(Signature.C_INTERFACE);
	String ENUM = String.valueOf(Signature.C_ENUM);
	String ENUM_MEMBER = String.valueOf(Signature.C_ENUM_MEMBER);
	String FUNCTION = String.valueOf(Signature.C_FUNCTION);
	String TEMPLATE_TUPLE_PARAMETER = String.valueOf(Signature.C_TEMPLATE_TUPLE_PARAMETER);
	String TEMPLATE_VALUE_PARAMETER = String.valueOf(Signature.C_TEMPLATE_VALUE_PARAMETER);
	String TEMPLATE_VALUE_PARAMETER2 = String.valueOf(Signature.C_TEMPLATE_VALUE_PARAMETER_SPECIFIC_VALUE);
	String TEMPLATE_TYPE_PARAMETER = String.valueOf(Signature.C_TEMPLATE_TYPE_PARAMETER);
	String TEMPLATE_TYPE_PARAMETER2 = String.valueOf(Signature.C_TEMPLATE_TYPE_PARAMETER_SPECIFIC_TYPE);
	String TEMPLATE_ALIAS_PARAMETER = String.valueOf(Signature.C_TEMPLATE_ALIAS_PARAMETER);
	String TEMPLATE_ALIAS_PARAMETER2 = String.valueOf(Signature.C_TEMPLATE_ALIAS_PARAMETER_SPECIFIC_TYPE);
	String TEMPLATE_PARAMETERS_BREAK = String.valueOf(Signature.C_TEMPLATE_PARAMETERS_BREAK);
	String TEMPLATE_INSTANCE = String.valueOf(Signature.C_TEMPLATE_INSTANCE);
	String TEMPLATE_INSTANCE_VALUE = String.valueOf(Signature.C_TEMPLATE_INSTANCE_VALUE_PARAMETER);
	String TEMPLATE_INSTANCE_SYMBOL = String.valueOf(Signature.C_TEMPLATE_INSTANCE_SYMBOL_PARAMETER);
	String TEMPLATE_INSTANCE_TYPE = String.valueOf(Signature.C_TEMPLATE_INSTANCE_TYPE_PARAMETER);
	String MODIFIER_LAZY = String.valueOf(Signature.C_MODIFIER_LAZY);
	String MODIFIER_OUT = String.valueOf(Signature.C_MODIFIER_OUT);
	String MODIFIER_REF = String.valueOf(Signature.C_MODIFIER_REF);
	String IDENTIFIER = String.valueOf(Signature.C_IDENTIFIER);

}
