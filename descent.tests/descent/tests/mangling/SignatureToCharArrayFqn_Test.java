package descent.tests.mangling;

import descent.core.Signature;

public class SignatureToCharArrayFqn_Test extends AbstractSignatureTest {
	
	public void testVoid() {
		tcaFqn("void", v);
	}
	
	public void testInt() {
		tcaFqn("int", i);
	}
	
	public void testPointer() {
		tcaFqn("int*", P(i));
	}
	
	public void testDynamicArray() {
		tcaFqn("int[]", A(i));
	}
	
	public void testStaticArray() {
		tcaFqn("int[3]", G(i, "3"));
	}
	
	public void testAssociativeArray() {
		tcaFqn("int[char]", H(a, i));
	}
	
	public void testTypeof() {
		tcaFqn("typeof(3)", typeof("3"));
	}
	
	public void testSlice() {
		tcaFqn("int[0 .. 3]", slice(i, "0", "3"));
	}
	
	public void testSlice2() {
		tcaFqn("int[1 .. 3]", slice(i, "1", "3"));
	}
	
	public void testClass() {
		tcaFqn("foo.test.Bar", MODULE + "3foo4test" + CLASS + "3Bar");
	}
	
	public void testStruct() {
		tcaFqn("foo.test.Bar", MODULE + "3foo4test" + STRUCT + "3Bar");
	}
	
	public void testUnion() {
		tcaFqn("foo.test.Bar", MODULE + "3foo4test" + UNION + "3Bar");
	}
	
	public void testInterface() {
		tcaFqn("foo.test.Bar", MODULE + "3foo4test" + INTERFACE + "3Bar");
	}
	
	public void testIdentifier() {
		tcaFqn("Bar.Buzz", IDENTIFIER + "3Bar4Buzz");
	}
	
	public void testFunction() {
		tcaFqn("void function(int)", F + i + Z + v);
	}
	
	public void testFunction2() {
		tcaFqn("void function(int, char)", F + i + a + Z + v);
	}
	
	public void testFunctionWithFunctionParameter() {
		tcaFqn("void function(void function())", F + F + Z + v + Z + v);
	}
	
	public void testFunctionWithModifierOut() {
		tcaFqn("void function(out int)", F + MODIFIER_OUT + i + Z + v);
	}
	
	public void testFunctionWithModifierRef() {
		tcaFqn("void function(ref int)", F + MODIFIER_REF + i + Z + v);
	}
	
	public void testFunctionWithModifierLazy() {
		tcaFqn("void function(lazy int)", F + MODIFIER_LAZY + i + Z + v);
	}
	
	public void testDelegate() {
		tcaFqn("void delegate(int)", D + F + i + Z + v);
	}
	
	public void testDelegateWithDelegateReturnType() {
		tcaFqn("void delegate() delegate(int)", D + F + i + Z + D + F + Z + v);
	}
	
	public void testDelegateWithFunctionReturnType() {
		tcaFqn("void function() delegate(int)", D + F + i + Z + F + Z + v);
	}
	
	public void testDelegateWithFunctionParameter() {
		tcaFqn("void delegate(void function())", D + F + F + Z + v + Z + v);
	}
	
	public void testTemplate() {
		tcaFqn("foo.test.Bar!()", MODULE + "3foo4test" + TEMPLATE + "3Bar" + TEMPLATE_PARAMETERS_BREAK);
	}
	
	public void testTemplateWithAliasParameters() {
		tcaFqn("foo.test.Bar!(alias T, alias U)", MODULE + "3foo4test" + TEMPLATE + "3Bar" + TEMPLATE_ALIAS_PARAMETER + TEMPLATE_ALIAS_PARAMETER + TEMPLATE_PARAMETERS_BREAK);
	}
	
	public void testTemplateWithAliasParameter() {
		tcaFqn("foo.test.Bar!(alias T : x)", MODULE + "3foo4test" + TEMPLATE + "3Bar" + TEMPLATE_ALIAS_PARAMETER + TEMPLATE_ALIAS_PARAMETER2 + IDENTIFIER + "1x" + TEMPLATE_PARAMETERS_BREAK);
	}
	
	public void testTemplateWithTupleParameter() {
		tcaFqn("foo.test.Bar!(T...)", MODULE + "3foo4test" + TEMPLATE + "3Bar" + TEMPLATE_TUPLE_PARAMETER + TEMPLATE_PARAMETERS_BREAK);
	}
	
	public void testTemplateWithValueParameter() {
		tcaFqn("foo.test.Bar!(T)", MODULE + "3foo4test" + TEMPLATE + "3Bar" + TEMPLATE_VALUE_PARAMETER + TEMPLATE_PARAMETERS_BREAK);
	}
	
	public void testTemplateWithValueParameter2() {
		tcaFqn("foo.test.Bar!(int T)", MODULE + "3foo4test" + TEMPLATE + "3Bar" + TEMPLATE_VALUE_PARAMETER + i + TEMPLATE_PARAMETERS_BREAK);
	}
	
	public void testTemplateWithValueParameter3() {
		tcaFqn("foo.test.Bar!(int T : 3)", MODULE + "3foo4test" + TEMPLATE + "3Bar" + TEMPLATE_VALUE_PARAMETER + i + TEMPLATE_VALUE_PARAMETER2 + '1' + TEMPLATE_VALUE_PARAMETER + '3' + TEMPLATE_PARAMETERS_BREAK);
	}
	
	public void testTemplateWithTypeParameter() {
		tcaFqn("foo.test.Bar!(T)", MODULE + "3foo4test" + TEMPLATE + "3Bar" + TEMPLATE_TYPE_PARAMETER + TEMPLATE_PARAMETERS_BREAK);
	}
	
	public void testTemplateWithTypeParameter2() {
		tcaFqn("foo.test.Bar!(T : int)", MODULE + "3foo4test" + TEMPLATE + "3Bar" + TEMPLATE_TYPE_PARAMETER + TEMPLATE_TYPE_PARAMETER2 + i + TEMPLATE_PARAMETERS_BREAK);
	}
	
	public void testTemplateInstance() {
		tcaFqn("Bar.Buzz!()", IDENTIFIER + "3Bar4Buzz" + TEMPLATE_INSTANCE + TEMPLATE_PARAMETERS_BREAK);
	}
	
	public void testTemplateInstanceSymbol() {
		tcaFqn("Bar.Buzz!(int)", IDENTIFIER + "3Bar4Buzz" + TEMPLATE_INSTANCE + TEMPLATE_INSTANCE_SYMBOL + i + TEMPLATE_PARAMETERS_BREAK);
	}
	
	public void testTemplateInstanceType() {
		tcaFqn("Bar.Buzz!(int)", IDENTIFIER + "3Bar4Buzz" + TEMPLATE_INSTANCE + TEMPLATE_INSTANCE_TYPE + i + TEMPLATE_PARAMETERS_BREAK);
	}
	
	public void testTemplateInstanceValue() {
		tcaFqn("Bar.Buzz!(3)", IDENTIFIER + "3Bar4Buzz" + TEMPLATE_INSTANCE + TEMPLATE_INSTANCE_VALUE + '1' + TEMPLATE_INSTANCE_VALUE + '3' + i + TEMPLATE_PARAMETERS_BREAK);
	}
	
	public void testTemplateInstanceManyTypes() {
		tcaFqn("Bar.Buzz!(int, char)", IDENTIFIER + "3Bar4Buzz" + TEMPLATE_INSTANCE + TEMPLATE_INSTANCE_TYPE + i + TEMPLATE_INSTANCE_TYPE + a + TEMPLATE_PARAMETERS_BREAK);
	}
	
	public void testTemplateInstanceAfterTemplate() {
		tcaFqn("foo.test.Bar!(int, char)", MODULE + "3foo4test" + TEMPLATED_CLASS + "3Bar" + TEMPLATE_TYPE_PARAMETER + TEMPLATE_TYPE_PARAMETER + TEMPLATE_PARAMETERS_BREAK + TEMPLATE_INSTANCE + TEMPLATE_INSTANCE_TYPE + i + TEMPLATE_INSTANCE_TYPE + a + TEMPLATE_PARAMETERS_BREAK);
	}
	
	public void testFunctionWithName() {
		tcaFqn("void foo.test.Bar()", MODULE + "3foo4test" + FUNCTION + "3Bar" + F + Z + v);
	}
	
	public void testFunctionWithNameAndParameter() {
		tcaFqn("void foo.test.Bar(int)", MODULE + "3foo4test" + FUNCTION + "3Bar" + F + i + Z + v);
	}
	
	public void testTemplatedClassWithAliasParameter() {
		tcaFqn("foo.test.Bar!(alias T : x)", MODULE + "3foo4test" + TEMPLATED_CLASS + "3Bar" + TEMPLATE_ALIAS_PARAMETER + TEMPLATE_ALIAS_PARAMETER2 + IDENTIFIER + "1x" + TEMPLATE_PARAMETERS_BREAK);
	}
	
	public void testTemplatedInterfaceWithAliasParameter() {
		tcaFqn("foo.test.Bar!(alias T : x)", MODULE + "3foo4test" + TEMPLATED_INTERFACE + "3Bar" + TEMPLATE_ALIAS_PARAMETER + TEMPLATE_ALIAS_PARAMETER2 + IDENTIFIER + "1x" + TEMPLATE_PARAMETERS_BREAK);
	}
	
	public void testTemplatedStructWithAliasParameter() {
		tcaFqn("foo.test.Bar!(alias T : x)", MODULE + "3foo4test" + TEMPLATED_STRUCT + "3Bar" + TEMPLATE_ALIAS_PARAMETER + TEMPLATE_ALIAS_PARAMETER2 + IDENTIFIER + "1x" + TEMPLATE_PARAMETERS_BREAK);
	}
	
	public void testTemplatedUnionWithAliasParameter() {
		tcaFqn("foo.test.Bar!(alias T : x)", MODULE + "3foo4test" + TEMPLATED_UNION + "3Bar" + TEMPLATE_ALIAS_PARAMETER + TEMPLATE_ALIAS_PARAMETER2 + IDENTIFIER + "1x" + TEMPLATE_PARAMETERS_BREAK);
	}
	
	public void testTemplatedFunctionWithNameAndParameter() {
		tcaFqn("void foo.test.Bar!(alias T : x)(int)", MODULE + "3foo4test" + TEMPLATED_FUNCTION + "3Bar" + F + i + Z + v + TEMPLATE_ALIAS_PARAMETER + TEMPLATE_ALIAS_PARAMETER2 + IDENTIFIER + "1x" + TEMPLATE_PARAMETERS_BREAK);
	}
	
	public void testInstanceWithinTemplate() {
		tcaFqn("minmaxtype!(T) tango.math.Core.min!(T...)(T)", "@5tango4math4Core)3minF?1TZ?10minmaxtype!^?1T'%'"); 
	}
	
	protected void tcaFqn(String expected, String signature) {
		assertEquals(expected, new String(Signature.toCharArray(signature.toCharArray(),
				true /* 't fully qualify things */)));
	}
	
}
