package descent.tests.mangling;

import descent.core.Signature;

public class SignatureParameterCount_Test extends AbstractSignatureTest {
	
	public void testParameterCount0() {
		pc(0, fqn + func(""));
	}
	
	public void testParameterCount0a() {
		pc(0, func(""));
	}
	
	public void testParameterCount1() {
		pc(1, fqn + func(i));
	}
	
	public void testParameterCount1a() {
		pc(1, func(i));
	}
	
	public void testParameterCount2() {
		pc(2, fqn + func(i + i));
	}
	
	public void testParameterCount2a() {
		pc(2, func(i + i));
	}
	
	public void testParameterCountPointer() {
		pc(1, fqn + func(P(i)));
	}
	
	public void testParameterCountPointera() {
		pc(1,  func(P(i)));
	}
	
	public void testParameterCountTypeFunction() {
		pc(1, fqn + func(func(i)));
	}
	
	public void testParameterCountTypeFunctiona() {
		pc(1, func(func(i)));
	}
	
	public void testParameterCountTypeFunction2() {
		pc(2, fqn + func(func(i) + func(i)));
	}
	
	public void testParameterCountTypeFunction2a() {
		pc(2, func(func(i) + func(i)));
	}
	
	public void testParameterCountTypeFunction3() {
		pc(1, fqn + func(func(func(i))));
	}
	
	public void testParameterCountTypeFunction3a() {
		pc(1, func(func(func(i))));
	}
	
	public void testParameterCountTypeDelegate() {
		pc(1, fqn + func(D + func("")));
	}
	
	public void testParameterCountTypeDelegatea() {
		pc(1, func(D + func("")));
	}
	
	public void testParameterCountTypeSymbol() {
		pc(1, fqn + func(MODULE + "4test" + CLASS + "3Bar" + Z + v));
	}
	
	public void testParameterCountTypeSymbola() {
		pc(1, func(MODULE + "4test" + CLASS + "3Bar" + Z + v));
	}
	
	public void testParameterCountTypeSymbol2() {
		pc(1, fqn + func(func(MODULE + "4test" + CLASS + "3Bar")));
	}
	
	public void testParameterCountTypeSymbol2a() {
		pc(1, func(func(MODULE + "4test" + CLASS + "3Bar")));
	}
	
	public void testParameterCountTypeSymbol3() {
		pc(1, fqn + func(fqn + func("")));
	}
	
	public void testParameterCountTypeSymbol3a() {
		pc(1, func(fqn + func("")));
	}
	
	protected String func(String parameters) {
		return F + parameters + Z + v;
	}
	
	protected String fqn = MODULE + "4test" + FUNCTION + "3foo";
	
	protected void pc(int count, String signature) {
		assertEquals(count, Signature.getParameterCount(signature));
	}
	
}
