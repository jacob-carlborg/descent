package descent.tests.mangling;

import junit.framework.TestCase;
import descent.core.Signature;
import descent.internal.compiler.parser.ISignatureConstants;

public class Signature_Test extends TestCase implements ISignatureConstants {
	
	public void testParameterCount0() {
		pc(0, MODULE + "4test" + FUNCTION + "3fooFZv");
	}
	
	public void testParameterCount1() {
		pc(1, MODULE + "4test" + FUNCTION + "3fooFiZv");
	}
	
	public void testParameterCount2() {
		pc(2, MODULE + "4test" + FUNCTION + "3fooFiiZv");
	}
	
	public void testParameterCountPointer() {
		pc(1, MODULE + "4test" + FUNCTION + "3fooFPiZv");
	}
	
	public void testParameterCountTypeFunction() {
		pc(1, MODULE + "4test" + FUNCTION + "3fooFFiZvZv");
	}
	
	public void testParameterCountTypeFunction2() {
		pc(2, MODULE + "4test" + FUNCTION + "3fooFFiZvFiZvZv");
	}
	
	public void testParameterCountTypeFunction3() {
		pc(1, MODULE + "4test" + FUNCTION + "3fooFFFZvZvZv");
	}
	
	public void testParameterCountTypeSymbol() {
		pc(1, MODULE + "4test" + FUNCTION + "3fooF" + MODULE + "4test" + CLASS + "3BarZv");
	}
	
	public void testParameterCountTypeSymbol2() {
		pc(1, MODULE + "4test" + FUNCTION + "3fooFF" + MODULE + "4test" + CLASS + "3BarZvZv");
	}
	
	public void testParameterCountTypeSymbol3() {
		pc(1, MODULE + "4test" + FUNCTION + "3fooF" + MODULE + "4test" + FUNCTION + "3fooFZvZv");
	}
	
	public void testToCharArray() {
		tca("void foo()", "FZv", "foo", new String[] { }, false, true, false);
	}
	
	public void testToCharArrayDontIncludeReturnType() {
		tca("foo()", "FZv", "foo", new String[] { }, false, false, false);
	}
	
	public void testToCharArray2() {
		tca("void foo(int x)", "FiZv", "foo", new String[] { "x" }, false, true, false);
	}
	
	public void testToCharArray3() {
		tca("void foo(int x, float foo)", "FifZv", "foo", new String[] { "x", "foo" }, false, true, false);
	}
	
	public void testToCharArray4() {
		tca("int foo(int x, float foo)", "FifZi", "foo", new String[] { "x", "foo" }, false, true, false);
	}
	
	public void testToCharArray5() {
		tca("void foo(Object o)", "F" + MODULE + "6objectC6ObjectZv", "foo", new String[] { "o" }, false, true, false);
	}
	
	public void testToCharArray6() {
		tca("void foo(object.Object o)", "F" + MODULE + "6object" + CLASS + "6ObjectZv", "foo", new String[] { "o" }, true, true, false);
	}
	
	public void testToCharArray7() {
		tca("void foo(void function() x)", "FFZvZv", "foo", new String[] { "x" }, true, true, false);
	}
	
	public void testToCharArray8() {
		tca("int foo(int* x)", "FPiZi", "foo", new String[] { "x" }, false, true, false);
	}
	
	public void testToCharArray9() {
		tca("int foo(int[char] x)", "FHaiZi", "foo", new String[] { "x" }, false, true, false);
	}
	
	public void testToCharArray10() {
		tca("void foo(one.two(int).Foo o)", "F" + MODULE + "3one" + FUNCTION + "3twoFiZv" + POSITION + "10" + CLASS + "3FooZv", "foo", new String[] { "o" }, true, true, false);
	}
	
	public void testToCharArray11() {
		tca("void foo(void delegate() x)", "FDFZvZv", "foo", new String[] { "x" }, true, true, false);
	}
	
	public void testGetParameterTypes() {
		gpa(new String[] { }, "FZv");
	}
	
	public void testGetParameterTypes2() {
		gpa(new String[] { "FZv" }, "FFZvZv");
	}
	
	public void testGetParameterTypes3() {
		gpa(new String[] { "i", "f", "a" }, "FifaZv");
	}
	
	public void testGetParameterTypes4() {
		gpa(new String[] { "FFZvZv" }, "FFFZvZvZv");
	}
	
	public void testGetParameterTypes5() {
		gpa(new String[] { "FFFZvZvZv" }, "FFFFZvZvZvZv");
	}
	
	public void testToCharArray12() {
		tca("object.Object", MODULE + "6object" + CLASS + "6Object");
	}
	
	public void testToCharArray13() {
		tca("object.Object.foo()", MODULE + "6object" + CLASS + "6Object" + FUNCTION + "3fooFZv");
	}
	
	public void testToCharArray14() {
		tca("object.Object.foo(int)", MODULE + "6object" + CLASS + "6Object" + FUNCTION + "3fooFiZv");
	}
	
	public void testToCharArray15() {
		tca("int", "i");
	}
	
	public void testToCharArray16() {
		tca("int*", "Pi");
	}
	
	public void testTemplateParameterCount1() {
		tpc(0, String.valueOf(TEMPLATE) + "3Foo" + TEMPLATE_PARAMETERS_BREAK);
	}
	
	public void testTemplateParameterCount2() {
		tpc(1, String.valueOf(TEMPLATE) + "3Foo" + TEMPLATE_TUPLE_PARAMETER + TEMPLATE_PARAMETERS_BREAK);
	}
	
	protected void tca(String expected, String signature) {
		assertEquals(expected, new String(Signature.toCharArray(signature.toCharArray())));
	}
	
	protected void tca(String expected, String signature, String methodName, String[] parameterNames, boolean fullyQualifyTypeNames, boolean includeReturnType, boolean isVargArgs) {
		char[][] c = new char[parameterNames.length][];
		for(int i = 0; i < parameterNames.length; i++) {
			c[i] = parameterNames[i].toCharArray();
		}
		
		assertEquals(expected, new String(Signature.toCharArray(signature.toCharArray(), methodName.toCharArray(), c, fullyQualifyTypeNames, includeReturnType, isVargArgs)));
	}
	
	protected void pc(int count, String signature) {
		assertEquals(count, Signature.getParameterCount(signature));
	}
	
	protected void tpc(int count, String signature) {
		assertEquals(count, Signature.getTemplateParameterCount(signature));
	}
	
	protected void gpa(String[] expected, String signature) {
		String[] actual = Signature.getParameterTypes(signature);
		assertEquals(expected.length, actual.length);
		for(int i = 0; i < actual.length; i++) {
			assertEquals(expected[i], actual[i]);
		}
	}
	
}
