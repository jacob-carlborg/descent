package descent.tests.mangling;

import descent.core.Signature;
import descent.internal.compiler.parser.ISignatureConstants;

public class Signature_Test extends AbstractSignatureTest implements ISignatureConstants {
	
	public void testIsVariadicFalse() {
		varFalse(F + Z + v);
	}
	

	public void testIsVariadicTrue1() {
		varTrue(F + Y + v);
	}
	
	public void testIsVariadicTrue2() {
		varTrue(F + X + v);
	}
	
	public void testIsVariadicFalse2() {
		varFalse(F + Z + F + X + v);
	}
	
	public void testIsVariadicTrue3() {
		varTrue(F + X + F + Z + v);
	}
	
	public void testIsVariadicEmpty() {
		varFail("");
	}
	
	public void testIsVariadicNotAFunction() {
		varFail(i);
	}
	
	public void testIsVariadicNotAFunction2() {
		varFail(MODULE + "3foo" + CLASS + "3Bar");
	}
	
	public void testIsVariadicNotAFunction3() {
		varFail(MODULE + "3foo" + FUNCTION + "3bar" + F + Z + v + CLASS + "3Bar");
	}
	
	protected void varTrue(String signature) {
		assertTrue(Signature.isVariadic(signature));
		assertTrue(Signature.isVariadic(signature.toCharArray()));
	}
	
	protected void varFalse(String signature) {
		assertFalse(Signature.isVariadic(signature.toCharArray()));
	}
	
	protected void varFail(String signature) {
		try {
			Signature.isVariadic(signature);
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			
		}
		
		try {
			Signature.isVariadic(signature.toCharArray());
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			
		}
	}
	
}
