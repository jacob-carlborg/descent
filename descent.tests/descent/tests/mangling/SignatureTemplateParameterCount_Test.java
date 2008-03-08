package descent.tests.mangling;

import descent.core.Signature;

public class SignatureTemplateParameterCount_Test extends AbstractSignatureTest {

	public void testParameterCountNotTemplate() {
		assertEquals(0, Signature.getTemplateParameterCount(i));
	}
	
	public void testZero() {
		pc(0, String.valueOf(TEMPLATE_PARAMETERS_BREAK));
	}
	
	public void testAlias() {
		pc(1, String.valueOf(TEMPLATE_ALIAS_PARAMETER) + TEMPLATE_PARAMETERS_BREAK);
	}
	
	public void testTwoAlias() {
		pc(2, String.valueOf(TEMPLATE_ALIAS_PARAMETER) + TEMPLATE_ALIAS_PARAMETER + TEMPLATE_PARAMETERS_BREAK);
	}
	
	public void testType() {
		pc(1, String.valueOf(TEMPLATE_TYPE_PARAMETER) + i + TEMPLATE_PARAMETERS_BREAK);
	}
	
	public void testValue() {
		pc(1, String.valueOf(TEMPLATE_VALUE_PARAMETER) + i + TEMPLATE_PARAMETERS_BREAK);
	}
	
	public void testTuple() {
		pc(1, String.valueOf(TEMPLATE_TUPLE_PARAMETER) + TEMPLATE_PARAMETERS_BREAK);
	}
	
	public void testSecondTemplate() {
		pc(2, TEMPLATE_ALIAS_PARAMETER + String.valueOf(TEMPLATE_PARAMETERS_BREAK) + TEMPLATE + "3bar" + TEMPLATE_ALIAS_PARAMETER + TEMPLATE_ALIAS_PARAMETER + TEMPLATE_PARAMETERS_BREAK);
	}

	protected String fqn = MODULE + "4test" + TEMPLATE + "3foo";
	
	protected void pc(int count, String signature) {
		assertEquals(count, Signature.getTemplateParameterCount(fqn + signature));
		assertEquals(count, Signature.getTemplateParameterCount((fqn + signature).toCharArray()));
	}
	
}
