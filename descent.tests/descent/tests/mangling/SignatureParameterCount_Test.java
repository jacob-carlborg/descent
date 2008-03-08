package descent.tests.mangling;

import descent.core.Signature;

public class SignatureParameterCount_Test extends AbstractSignatureTest {
	
	public void testParameterCountNotFunction() {
		try {
			Signature.getReturnType(i);
		} catch (IllegalArgumentException e) {
			
		}
	}
	
	public void testParameterCount0() {
		pc(0, func(""));
	}
	
	public void testParameterCount1() {
		pc(1, func(i));
	}
	
	public void testParameterCount2() {
		pc(2, func(i + i));
	}
	
	public void testParameterCountPointer() {
		pc(1,  func(P(i)));
	}
	
	public void testParameterCountTypeFunction() {
		pc(1, func(func(i)));
	}
	
	public void testParameterCountTypeFunction2() {
		pc(2, func(func(i) + func(i)));
	}
	
	public void testParameterCountTypeFunction3() {
		pc(1, func(func(func(i))));
	}
	
	public void testParameterCountTypeDelegate() {
		pc(1, func(D + func("")));
	}
	
	public void testParameterCountTypeSymbol() {
		pc(1, func(MODULE + "4test" + CLASS + "3Bar" + Z + v));
	}
	
	public void testParameterCountTypeSymbol2() {
		pc(1, func(func(MODULE + "4test" + CLASS + "3Bar")));
	}
	
	public void testParameterCountTypeSymbol3() {
		pc(1, func(fqn + func("")));
	}
	
	public void testParameterCount1WithFunctionReturnType() {
		pc(1, F + i + Z + F + i + i + Z + v);
	}
	
	protected String func(String parameters) {
		return F + parameters + Z + v;
	}
	
	protected String fqn = MODULE + "4test" + FUNCTION + "3foo";
	
	protected void pc(int count, String signature) {
		assertEquals(count, Signature.getParameterCount(signature));
		assertEquals(count, Signature.getParameterCount(fqn + signature));
		assertEquals(count, Signature.getParameterCount(signature.toCharArray()));
		assertEquals(count, Signature.getParameterCount((fqn + signature).toCharArray()));
	}
	
}
