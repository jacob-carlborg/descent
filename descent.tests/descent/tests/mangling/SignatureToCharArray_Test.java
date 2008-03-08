package descent.tests.mangling;

import descent.core.Signature;

public class SignatureToCharArray_Test extends AbstractSignatureTest {
	
	public void testVoid() {
		tca("void", v);
	}
	
	public void testInt() {
		tca("int", i);
	}
	
	public void testPointer() {
		tca("int*", P(i));
	}
	
	public void testDynamicArray() {
		tca("int[]", A(i));
	}
	
	public void testStaticArray() {
		tca("int[3]", G(i, "3"));
	}
	
	public void testAssociativeArray() {
		tca("int[char]", H(a, i));
	}
	
	public void testTypeof() {
		tca("typeof(3)", typeof("3"));
	}
	
	public void testSlice() {
		tca("int[0 .. 3]", slice(i, "0", "3"));
	}
	
	public void testSlice2() {
		tca("int[1 .. 3]", slice(i, "1", "3"));
	}
	
	public void testClass() {
		tca("foo.test.Bar", MODULE + "3foo4test" + CLASS + "3Bar");
	}
	
	public void testStruct() {
		tca("foo.test.Bar", MODULE + "3foo4test" + STRUCT + "3Bar");
	}
	
	public void testUnion() {
		tca("foo.test.Bar", MODULE + "3foo4test" + UNION + "3Bar");
	}
	
	public void testInterface() {
		tca("foo.test.Bar", MODULE + "3foo4test" + INTERFACE + "3Bar");
	}
	
	public void testIdentifier() {
		tca("Bar.Buzz", IDENTIFIER + "3Bar4Buzz");
	}
	
	public void testFunction() {
		tca("void function(int)", F + i + Z + v);
	}
	
	public void testFunction2() {
		tca("void function(int, char)", F + i + a + Z + v);
	}
	
	public void testFunctionWithFunctionParameter() {
		tca("void function(void function())", F + F + Z + v + Z + v);
	}
	
	public void testFunctionWithModifierOut() {
		tca("void function(out int)", F + MODIFIER_OUT + i + Z + v);
	}
	
	public void testFunctionWithModifierRef() {
		tca("void function(ref int)", F + MODIFIER_REF + i + Z + v);
	}
	
	public void testFunctionWithModifierLazy() {
		tca("void function(lazy int)", F + MODIFIER_LAZY + i + Z + v);
	}
	
	public void testDelegate() {
		tca("void delegate(int)", D + F + i + Z + v);
	}
	
	public void testDelegateWithDelegateReturnType() {
		tca("void delegate() delegate(int)", D + F + i + Z + D + F + Z + v);
	}
	
	public void testDelegateWithFunctionReturnType() {
		tca("void function() delegate(int)", D + F + i + Z + F + Z + v);
	}
	
	public void testDelegateWithFunctionParameter() {
		tca("void delegate(void function())", D + F + F + Z + v + Z + v);
	}
	
	public void testTemplate() {
		tca("foo.test.Bar!()", MODULE + "3foo4test" + TEMPLATE + "3Bar" + TEMPLATE_PARAMETERS_BREAK);
	}
	
	public void testTemplateWithAliasParameters() {
		tca("foo.test.Bar!(alias T, alias U)", MODULE + "3foo4test" + TEMPLATE + "3Bar" + TEMPLATE_ALIAS_PARAMETER + TEMPLATE_ALIAS_PARAMETER + TEMPLATE_PARAMETERS_BREAK);
	}
	
	public void testTemplateWithAliasParameter() {
		tca("foo.test.Bar!(alias T : x)", MODULE + "3foo4test" + TEMPLATE + "3Bar" + TEMPLATE_ALIAS_PARAMETER + TEMPLATE_ALIAS_PARAMETER2 + IDENTIFIER + "1x" + TEMPLATE_PARAMETERS_BREAK);
	}
	
	public void testTemplateWithTupleParameter() {
		tca("foo.test.Bar!(T...)", MODULE + "3foo4test" + TEMPLATE + "3Bar" + TEMPLATE_TUPLE_PARAMETER + TEMPLATE_PARAMETERS_BREAK);
	}
	
	public void testTemplateWithValueParameter() {
		tca("foo.test.Bar!(T)", MODULE + "3foo4test" + TEMPLATE + "3Bar" + TEMPLATE_VALUE_PARAMETER + TEMPLATE_PARAMETERS_BREAK);
	}
	
	public void testTemplateWithValueParameter2() {
		tca("foo.test.Bar!(int T)", MODULE + "3foo4test" + TEMPLATE + "3Bar" + TEMPLATE_VALUE_PARAMETER + i + TEMPLATE_PARAMETERS_BREAK);
	}
	
	public void testTemplateWithValueParameter3() {
		tca("foo.test.Bar!(int T : 3)", MODULE + "3foo4test" + TEMPLATE + "3Bar" + TEMPLATE_VALUE_PARAMETER + i + '1' + TEMPLATE_VALUE_PARAMETER + '3' + TEMPLATE_PARAMETERS_BREAK);
	}
	
	public void testTemplateWithTypeParameter() {
		tca("foo.test.Bar!(T)", MODULE + "3foo4test" + TEMPLATE + "3Bar" + TEMPLATE_TYPE_PARAMETER + TEMPLATE_PARAMETERS_BREAK);
	}
	
	public void testTemplateWithTypeParameter2() {
		tca("foo.test.Bar!(T : int)", MODULE + "3foo4test" + TEMPLATE + "3Bar" + TEMPLATE_TYPE_PARAMETER + TEMPLATE_TYPE_PARAMETER2 + i + TEMPLATE_PARAMETERS_BREAK);
	}
	
	public void testTemplateInstance() {
		tca("Bar.Buzz!()", IDENTIFIER + "3Bar4Buzz" + TEMPLATE_INSTANCE + TEMPLATE_PARAMETERS_BREAK);
	}
	
	public void testTemplateInstanceSymbol() {
		tca("Bar.Buzz!(int)", IDENTIFIER + "3Bar4Buzz" + TEMPLATE_INSTANCE + TEMPLATE_INSTANCE_SYMBOL + i + TEMPLATE_PARAMETERS_BREAK);
	}
	
	public void testTemplateInstanceType() {
		tca("Bar.Buzz!(int)", IDENTIFIER + "3Bar4Buzz" + TEMPLATE_INSTANCE + TEMPLATE_INSTANCE_TYPE + i + TEMPLATE_PARAMETERS_BREAK);
	}
	
	public void testTemplateInstanceValue() {
		tca("Bar.Buzz!(3)", IDENTIFIER + "3Bar4Buzz" + TEMPLATE_INSTANCE + TEMPLATE_INSTANCE_VALUE + '1' + TEMPLATE_INSTANCE_VALUE + '3' + i + TEMPLATE_PARAMETERS_BREAK);
	}
	
	public void testTemplateInstanceManyTypes() {
		tca("Bar.Buzz!(int, char)", IDENTIFIER + "3Bar4Buzz" + TEMPLATE_INSTANCE + TEMPLATE_INSTANCE_TYPE + i + TEMPLATE_INSTANCE_TYPE + a + TEMPLATE_PARAMETERS_BREAK);
	}
	
	public void testFunctionWithName() {
		tca("void foo.test.Bar()", MODULE + "3foo4test" + FUNCTION + "3Bar" + F + Z + v);
	}
	
	public void testFunctionWithNameAndParameter() {
		tca("void foo.test.Bar(int)", MODULE + "3foo4test" + FUNCTION + "3Bar" + F + i + Z + v);
	}
	
	public void testTemplatedClassWithAliasParameter() {
		tca("foo.test.Bar!(alias T : x)", MODULE + "3foo4test" + TEMPLATED_CLASS + "3Bar" + TEMPLATE_ALIAS_PARAMETER + TEMPLATE_ALIAS_PARAMETER2 + IDENTIFIER + "1x" + TEMPLATE_PARAMETERS_BREAK);
	}
	
	public void testTemplatedInterfaceWithAliasParameter() {
		tca("foo.test.Bar!(alias T : x)", MODULE + "3foo4test" + TEMPLATED_INTERFACE + "3Bar" + TEMPLATE_ALIAS_PARAMETER + TEMPLATE_ALIAS_PARAMETER2 + IDENTIFIER + "1x" + TEMPLATE_PARAMETERS_BREAK);
	}
	
	public void testTemplatedStructWithAliasParameter() {
		tca("foo.test.Bar!(alias T : x)", MODULE + "3foo4test" + TEMPLATED_STRUCT + "3Bar" + TEMPLATE_ALIAS_PARAMETER + TEMPLATE_ALIAS_PARAMETER2 + IDENTIFIER + "1x" + TEMPLATE_PARAMETERS_BREAK);
	}
	
	public void testTemplatedUnionWithAliasParameter() {
		tca("foo.test.Bar!(alias T : x)", MODULE + "3foo4test" + TEMPLATED_UNION + "3Bar" + TEMPLATE_ALIAS_PARAMETER + TEMPLATE_ALIAS_PARAMETER2 + IDENTIFIER + "1x" + TEMPLATE_PARAMETERS_BREAK);
	}
	
	public void testTemplatedFunctionWithNameAndParameter() {
		tca("void foo.test.Bar!(alias T : x)(int)", MODULE + "3foo4test" + TEMPLATED_FUNCTION + "3Bar" + F + i + Z + v + TEMPLATE_ALIAS_PARAMETER + TEMPLATE_ALIAS_PARAMETER2 + IDENTIFIER + "1x" + TEMPLATE_PARAMETERS_BREAK);
	}
	
	protected void tca(String expected, String signature) {
		assertEquals(expected, new String(Signature.toCharArray(signature.toCharArray())));
	}
	
}
