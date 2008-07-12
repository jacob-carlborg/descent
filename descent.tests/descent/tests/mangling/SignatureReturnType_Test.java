package descent.tests.mangling;

import descent.core.Signature;

public class SignatureReturnType_Test extends AbstractSignatureTest {
	
	public void testVoid() {
		rt(v, F + Z + v);
	}
	
	public void testInt() {
		rt(i, F + Z + i);
	}
	
	public void testInt2() {
		rt(i, F + a + Z + i);
	}
	
	public void testPointer() {
		rt(P(i), F + Z + P(i));
	}
	
	public void testStaticArray() {
		rt(G(i, "3"), F + Z + G(i, "3"));
	}
	
	public void testDynamicArray() {
		rt(A(i), F + Z + A(i));
	}
	
	public void testAssociativeArray() {
		rt(H(i, a), F + Z + H(i, a));
	}
	
	public void testConst() {
		rt(Signature.C_CONST + i, F + Z + Signature.C_CONST +i);
	}
	
	public void testInvariant() {
		rt(Signature.C_INVARIANT + i, F + Z + Signature.C_INVARIANT +i);
	}
	
	public void testTypeof() {
		rt(typeof("foo"), F + Z + typeof("foo"));
	}
	
	public void testSlice() {
		rt(slice(i, "1", "3"), F + Z + slice(i, "1", "3"));
	}
	
	public void testFunction() {
		rt(F + Z + i, F + Z + F + Z + i);
	}
	
	public void testDelegate() {
		rt(D + F + Z + i, F + Z + D + F + Z + i);
	}
	
	public void testIdentifier() {
		rt(IDENTIFIER + "3Foo", F + Z + IDENTIFIER + "3Foo");
	}
	
	public void testSymbol() {
		rt(MODULE + "3Bar" + CLASS + "3Foo", F + Z + MODULE + "3Bar" + CLASS + "3Foo");
	}
	
	public void testSymbol2() {
		rt(MODULE + "3Bar" + CLASS + "3Foo" + STRUCT + "4Test", F + Z + MODULE + "3Bar" + CLASS + "3Foo" + STRUCT + "4Test");
	}
	
	public void testTemplate() {
		rt(MODULE + "3Bar" + TEMPLATE + "3Foo" + TEMPLATE_ALIAS_PARAMETER + TEMPLATE_PARAMETERS_BREAK, F + Z + MODULE + "3Bar" + TEMPLATE + "3Foo" + TEMPLATE_ALIAS_PARAMETER + TEMPLATE_PARAMETERS_BREAK);
	}
	
	public void testTemplateInstance() {
		rt(IDENTIFIER + "3Foo" + TEMPLATE_INSTANCE + TEMPLATE_INSTANCE_TYPE + i + TEMPLATE_PARAMETERS_BREAK, F + Z + IDENTIFIER + "3Foo" + TEMPLATE_INSTANCE + TEMPLATE_INSTANCE_TYPE + i + TEMPLATE_PARAMETERS_BREAK);
	}
	
	public void testReturnTypeNotFunction() {
		rtFail("");
	}
	
	public void testReturnTypeNotFunction2() {
		rtFail(i);
	}
	
	protected String fqn = MODULE + "4test" + FUNCTION + "3foo";
	
	protected void rt(String expected, String signature) {
		assertEquals(expected, Signature.getReturnType(signature));
		assertEquals(expected, Signature.getReturnType(fqn + signature));
		assertEquals(expected, new String(Signature.getReturnType(signature.toCharArray())));
		assertEquals(expected, new String(Signature.getReturnType((fqn + signature).toCharArray())));
	}
	
	protected void rtFail(String signature) {
		try {
			Signature.getReturnType(signature);
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			
		}
		
		try {
			Signature.getReturnType(signature.toCharArray());
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			
		}
	}
	
}
