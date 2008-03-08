package descent.tests.mangling;

import descent.core.Signature;

public class SignatureParameterTypes_Test extends AbstractSignatureTest {
	
	public void testZero() {
		pt(F + Z + v);
	}
	
//	public void testReturnTypeNotFunction() {
//		ptFail("");
//	}
//	
//	public void testReturnTypeNotFunction2() {
//		ptFail(i);
//	}
	
	protected String fqn = MODULE + "4test" + FUNCTION + "3foo";
	
	protected void pt(String signature, String ... expected) {
		String[] actual = Signature.getParameterTypes(signature);
		assertEquals(expected.length, actual.length);
	}
	
	protected void ptFail(String signature) {
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
