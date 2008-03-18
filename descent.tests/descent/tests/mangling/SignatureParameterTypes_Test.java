package descent.tests.mangling;

import descent.core.Signature;

public class SignatureParameterTypes_Test extends AbstractSignatureTest {
	
	public void testZero() {
		pt(F + Z + v);
	}
	
	public void testOnePrimitive() {
		pt(F + i + Z + v, i);
	}
	
	public void testTwoPrimitives() {
		pt(F + i + a + Z + v, i, a);
	}
	
	public void testPointer() {
		pt(F + P(i) + a + Z + v, P(i), a);
	}
	
	public void testStaticArray() {
		pt(F + G(i, "3") + a + Z + v, G(i, "3"), a);
	}
	
	public void testDynamicArray() {
		pt(F + A(i) + a + Z + v, A(i), a);
	}
	
	public void testAssociativeArray() {
		pt(F + H(i, a) + a + Z + v, H(i, a), a);
	}
	
	public void testTypeof() {
		pt(F + typeof("foo") + a + Z + v, typeof("foo"), a);
	}
	
	public void testSlice() {
		pt(F + slice(i, "1", "3") + a + Z + v, slice(i, "1", "3"), a);
	}
	
	public void testFunction() {
		pt(F + F + i + Z + v + a + Z + v, F + i + Z + v, a);
	}
	
	public void testDelegate() {
		pt(F + D + F + i + Z + v + a + Z + v, D + F + i + Z + v, a);
	}
	
	public void testIdentifier() {
		pt(F + IDENTIFIER + "3Foo" + a + Z + v, IDENTIFIER + "3Foo", a);
	}
	
	public void testSymbol() {
		pt(F + MODULE + "3Bar" + CLASS + "3Foo" + a + Z + v, MODULE + "3Bar" + CLASS + "3Foo", a);
	}
	
	public void testSymbol2() {
		pt(F + MODULE + "3Bar" + CLASS + "3Foo" + STRUCT + "4Test" + a + Z + v, MODULE + "3Bar" + CLASS + "3Foo" + STRUCT + "4Test", a);
	}
	
	public void testTemplate() {
		pt(F + MODULE + "3Bar" + TEMPLATE + "3Foo" + TEMPLATE_ALIAS_PARAMETER + TEMPLATE_PARAMETERS_BREAK + a + Z + v, MODULE + "3Bar" + TEMPLATE + "3Foo" + TEMPLATE_ALIAS_PARAMETER + TEMPLATE_PARAMETERS_BREAK, a);
	}
	
	public void testTemplateInstance() {
		pt(F + IDENTIFIER + "3Foo" + TEMPLATE_INSTANCE + TEMPLATE_INSTANCE_TYPE + i + TEMPLATE_PARAMETERS_BREAK + a + Z + v, IDENTIFIER + "3Foo" + TEMPLATE_INSTANCE + TEMPLATE_INSTANCE_TYPE + i + TEMPLATE_PARAMETERS_BREAK, a);
	}
	
	public void testReturnTypeNotFunction() {
		ptFail("");
	}
	
	public void testReturnTypeNotFunction2() {
		ptFail(i);
	}
	
	protected String fqn = MODULE + "4test" + FUNCTION + "3foo";
	
	protected void pt(String signature, String ... expected) {
		pt0(signature, expected);
		pt0(fqn + signature, expected);
	}
	
	protected void pt0(String signature, String ... expected) {
		String[] actual = Signature.getParameterTypes(signature);
		assertEquals(expected.length, actual.length);
		for (int i = 0; i < actual.length; i++) {
			assertEquals(expected[i], actual[i]);
		}
		
		char[][] actualC = Signature.getParameterTypes(signature.toCharArray());
		assertEquals(expected.length, actualC.length);
		for (int i = 0; i < actualC.length; i++) {
			assertEquals(expected[i], new String(actualC[i]));
		}
	}
	
	protected void ptFail(String signature) {
		try {
			Signature.getParameterTypes(signature);
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			
		}
		
		try {
			Signature.getParameterTypes(signature.toCharArray());
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			
		}
	}
	
}
