package descent.tests.mangling;

import descent.core.Signature;

public class SignatureKind_Test extends AbstractSignatureTest {
	
	public void testVoid() {
		kind(Signature.PRIMITIVE_TYPE_SIGNATURE, v);
	}
	
	public void testInt() {
		kind(Signature.PRIMITIVE_TYPE_SIGNATURE, i);
	}
	
	public void testPointer() {
		kind(Signature.POINTER_TYPE_SIGNATURE, P(i));
	}
	
	public void testDynamicArray() {
		kind(Signature.DYNAMIC_ARRAY_TYPE_SIGNATURE, A(i));
	}
	
	public void testStaticArray() {
		kind(Signature.STATIC_ARRAY_TYPE_SIGNATURE, G(i, "3"));
	}
	
	public void testAssociativeArray() {
		kind(Signature.ASSOCIATIVE_ARRAY_TYPE_SIGNATURE, H(a, i));
	}
	
	public void testTypeof() {
		kind(Signature.TYPEOF_TYPE_SIGNATURE, typeof("3"));
	}
	
	public void testSlice() {
		kind(Signature.SLICE_TYPE_SIGNATURE, slice(i, "0", "3"));
	}
	
	public void testSlice2() {
		kind(Signature.SLICE_TYPE_SIGNATURE, slice(i, "1", "3"));
	}
	
	public void testModule() {
		kind(Signature.MODULE_SIGNATURE, MODULE + "3foo4test");
	}
	
	public void testClass() {
		kind(Signature.CLASS_TYPE_SIGNATURE, MODULE + "3foo4test" + CLASS + "3Bar");
	}
	
	public void testStruct() {
		kind(Signature.STRUCT_TYPE_SIGNATURE, MODULE + "3foo4test" + STRUCT + "3Bar");
	}
	
	public void testUnion() {
		kind(Signature.UNION_TYPE_SIGNATURE, MODULE + "3foo4test" + UNION + "3Bar");
	}
	
	public void testInterface() {
		kind(Signature.INTERFACE_TYPE_SIGNATURE, MODULE + "3foo4test" + INTERFACE + "3Bar");
	}
	
	public void testIdentifier() {
		kind(Signature.IDENTIFIER_TYPE_SIGNATURE, IDENTIFIER + "3Bar4Buzz");
	}
	
	public void testFunction() {
		kind(Signature.FUNCTION_TYPE_SIGNATURE, F + i + Z + v);
	}
	
	public void testFunction2() {
		kind(Signature.FUNCTION_TYPE_SIGNATURE, F + i + a + Z + v);
	}
	
	public void testFunctionWithFunctionParameter() {
		kind(Signature.FUNCTION_TYPE_SIGNATURE, F + F + Z + v + Z + v);
	}
	
	public void testFunctionWithModifierOut() {
		kind(Signature.FUNCTION_TYPE_SIGNATURE, F + MODIFIER_OUT + i + Z + v);
	}
	
	public void testFunctionWithModifierRef() {
		kind(Signature.FUNCTION_TYPE_SIGNATURE, F + MODIFIER_REF + i + Z + v);
	}
	
	public void testFunctionWithModifierLazy() {
		kind(Signature.FUNCTION_TYPE_SIGNATURE, F + MODIFIER_LAZY + i + Z + v);
	}
	
	public void testDelegate() {
		kind(Signature.DELEGATE_TYPE_SIGNATURE, D + F + i + Z + v);
	}
	
	public void testDelegateWithDelegateReturnType() {
		kind(Signature.DELEGATE_TYPE_SIGNATURE, D + F + i + Z + D + F + Z + v);
	}
	
	public void testDelegateWithFunctionReturnType() {
		kind(Signature.DELEGATE_TYPE_SIGNATURE, D + F + i + Z + F + Z + v);
	}
	
	public void testDelegateWithFunctionParameter() {
		kind(Signature.DELEGATE_TYPE_SIGNATURE, D + F + F + Z + v + Z + v);
	}
	
	public void testTemplate() {
		kind(Signature.TEMPLATE_TYPE_SIGNATURE, MODULE + "3foo4test" + TEMPLATE + "3Bar" + TEMPLATE_PARAMETERS_BREAK);
	}
	
	public void testTemplateWithAliasParameters() {
		kind(Signature.TEMPLATE_TYPE_SIGNATURE, MODULE + "3foo4test" + TEMPLATE + "3Bar" + TEMPLATE_ALIAS_PARAMETER + TEMPLATE_ALIAS_PARAMETER + TEMPLATE_PARAMETERS_BREAK);
	}
	
	public void testTemplateWithAliasParameter() {
		kind(Signature.TEMPLATE_TYPE_SIGNATURE, MODULE + "3foo4test" + TEMPLATE + "3Bar" + TEMPLATE_ALIAS_PARAMETER + TEMPLATE_ALIAS_PARAMETER2 + IDENTIFIER + "1x" + TEMPLATE_PARAMETERS_BREAK);
	}
	
	public void testTemplateWithTupleParameter() {
		kind(Signature.TEMPLATE_TYPE_SIGNATURE, MODULE + "3foo4test" + TEMPLATE + "3Bar" + TEMPLATE_TUPLE_PARAMETER + TEMPLATE_PARAMETERS_BREAK);
	}
	
	public void testTemplateWithValueParameter() {
		kind(Signature.TEMPLATE_TYPE_SIGNATURE, MODULE + "3foo4test" + TEMPLATE + "3Bar" + TEMPLATE_VALUE_PARAMETER + TEMPLATE_PARAMETERS_BREAK);
	}
	
	public void testTemplateWithValueParameter2() {
		kind(Signature.TEMPLATE_TYPE_SIGNATURE, MODULE + "3foo4test" + TEMPLATE + "3Bar" + TEMPLATE_VALUE_PARAMETER + i + TEMPLATE_PARAMETERS_BREAK);
	}
	
	public void testTemplateWithValueParameter3() {
		kind(Signature.TEMPLATE_TYPE_SIGNATURE, MODULE + "3foo4test" + TEMPLATE + "3Bar" + TEMPLATE_VALUE_PARAMETER + i + TEMPLATE_VALUE_PARAMETER2 + '1' + TEMPLATE_VALUE_PARAMETER + '3' + TEMPLATE_PARAMETERS_BREAK);
	}
	
	public void testTemplateWithTypeParameter() {
		kind(Signature.TEMPLATE_TYPE_SIGNATURE, MODULE + "3foo4test" + TEMPLATE + "3Bar" + TEMPLATE_TYPE_PARAMETER + TEMPLATE_PARAMETERS_BREAK);
	}
	
	public void testTemplateWithTypeParameter2() {
		kind(Signature.TEMPLATE_TYPE_SIGNATURE, MODULE + "3foo4test" + TEMPLATE + "3Bar" + TEMPLATE_TYPE_PARAMETER + TEMPLATE_TYPE_PARAMETER2 + i + TEMPLATE_PARAMETERS_BREAK);
	}
	
	public void testTemplateInstance() {
		kind(Signature.TEMPLATE_INSTANCE_TYPE_SIGNATURE, IDENTIFIER + "3Bar4Buzz" + TEMPLATE_INSTANCE + TEMPLATE_PARAMETERS_BREAK);
	}
	
	public void testTemplateInstanceSymbol() {
		kind(Signature.TEMPLATE_INSTANCE_TYPE_SIGNATURE, IDENTIFIER + "3Bar4Buzz" + TEMPLATE_INSTANCE + TEMPLATE_INSTANCE_SYMBOL + i + TEMPLATE_PARAMETERS_BREAK);
	}
	
	public void testTemplateInstanceType() {
		kind(Signature.TEMPLATE_INSTANCE_TYPE_SIGNATURE, IDENTIFIER + "3Bar4Buzz" + TEMPLATE_INSTANCE + TEMPLATE_INSTANCE_TYPE + i + TEMPLATE_PARAMETERS_BREAK);
	}
	
	public void testTemplateInstanceValue() {
		kind(Signature.TEMPLATE_INSTANCE_TYPE_SIGNATURE, IDENTIFIER + "3Bar4Buzz" + TEMPLATE_INSTANCE + TEMPLATE_INSTANCE_VALUE + '1' + TEMPLATE_INSTANCE_VALUE + '3' + i + TEMPLATE_PARAMETERS_BREAK);
	}
	
	public void testTemplateInstanceManyTypes() {
		kind(Signature.TEMPLATE_INSTANCE_TYPE_SIGNATURE, IDENTIFIER + "3Bar4Buzz" + TEMPLATE_INSTANCE + TEMPLATE_INSTANCE_TYPE + i + TEMPLATE_INSTANCE_TYPE + a + TEMPLATE_PARAMETERS_BREAK);
	}
	
	public void testFunctionWithName() {
		kind(Signature.FUNCTION_SIGNATURE, MODULE + "3foo4test" + FUNCTION + "3Bar" + F + Z + v);
	}
	
	public void testFunctionWithNameAndParameter() {
		kind(Signature.FUNCTION_SIGNATURE, MODULE + "3foo4test" + FUNCTION + "3Bar" + F + i + Z + v);
	}
	
	public void testTemplatedClassWithAliasParameter() {
		kind(Signature.TEMPLATED_CLASS_TYPE_SIGNATURE, MODULE + "3foo4test" + TEMPLATED_CLASS + "3Bar" + TEMPLATE_ALIAS_PARAMETER + TEMPLATE_ALIAS_PARAMETER2 + IDENTIFIER + "1x" + TEMPLATE_PARAMETERS_BREAK);
	}
	
	public void testTemplatedInterfaceWithAliasParameter() {
		kind(Signature.TEMPLATED_INTERFACE_TYPE_SIGNATURE, MODULE + "3foo4test" + TEMPLATED_INTERFACE + "3Bar" + TEMPLATE_ALIAS_PARAMETER + TEMPLATE_ALIAS_PARAMETER2 + IDENTIFIER + "1x" + TEMPLATE_PARAMETERS_BREAK);
	}
	
	public void testTemplatedStructWithAliasParameter() {
		kind(Signature.TEMPLATED_STRUCT_TYPE_SIGNATURE, MODULE + "3foo4test" + TEMPLATED_STRUCT + "3Bar" + TEMPLATE_ALIAS_PARAMETER + TEMPLATE_ALIAS_PARAMETER2 + IDENTIFIER + "1x" + TEMPLATE_PARAMETERS_BREAK);
	}
	
	public void testTemplatedUnionWithAliasParameter() {
		kind(Signature.TEMPLATED_UNION_TYPE_SIGNATURE, MODULE + "3foo4test" + TEMPLATED_UNION + "3Bar" + TEMPLATE_ALIAS_PARAMETER + TEMPLATE_ALIAS_PARAMETER2 + IDENTIFIER + "1x" + TEMPLATE_PARAMETERS_BREAK);
	}
	
	public void testTemplatedFunctionWithNameAndParameter() {
		kind(Signature.TEMPLATED_FUNCTION_SIGNATURE, MODULE + "3foo4test" + TEMPLATED_FUNCTION + "3Bar" + F + i + Z + v + TEMPLATE_ALIAS_PARAMETER + TEMPLATE_ALIAS_PARAMETER2 + IDENTIFIER + "1x" + TEMPLATE_PARAMETERS_BREAK);
	}
	
	protected void kind(int expected, String signature) {
		assertEquals(expected, Signature.getTypeSignatureKind(signature));
		assertEquals(expected, Signature.getTypeSignatureKind(signature.toCharArray()));
	}
	
}
