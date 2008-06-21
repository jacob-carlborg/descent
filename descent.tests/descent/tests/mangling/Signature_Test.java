package descent.tests.mangling;

import descent.core.Signature;
import descent.internal.compiler.parser.ASTNodeEncoder;
import descent.internal.compiler.parser.IntegerExp;
import descent.internal.compiler.parser.Loc;
import descent.internal.compiler.parser.TypeAArray;
import descent.internal.compiler.parser.TypeBasic;
import descent.internal.compiler.parser.TypeDArray;
import descent.internal.compiler.parser.TypePointer;
import descent.internal.compiler.parser.TypeSArray;
import descent.internal.compiler.parser.TypeSlice;
import descent.internal.compiler.parser.TypeTypeof;

public class Signature_Test extends AbstractSignatureTest implements ISignatureTest {
	
	public void testPointer() {
		assertEquals(new TypePointer(TypeBasic.tint32).getSignature(), 
			Signature.createPointerSignature(Signature.SIG_INT));
	}
	
	public void testPointer2() {
		assertEquals(new TypePointer(TypeBasic.tint32).getSignature(), 
				new String(Signature.createPointerSignature(Signature.SIG_INT.toCharArray())));
	}
	
	public void testStaticArray() {
		assertEquals(new TypeSArray(TypeBasic.tint32, new IntegerExp(3), new ASTNodeEncoder()).getSignature(), 
			Signature.createStaticArraySignature(Signature.SIG_INT, "3"));
	}
	
	public void testStaticArray2() {
		assertEquals(new TypeSArray(TypeBasic.tint32, new IntegerExp(3), new ASTNodeEncoder()).getSignature(), 
			new String(Signature.createStaticArraySignature(Signature.SIG_INT.toCharArray(), "3".toCharArray())));
	}
	
	public void testStaticArray3() {
		assertEquals(new TypeSArray(TypeBasic.tint32, new IntegerExp(123), new ASTNodeEncoder()).getSignature(), 
			new String(Signature.createStaticArraySignature(Signature.SIG_INT.toCharArray(), "123".toCharArray())));
	}
	
	public void testDynamicArray() {
		assertEquals(new TypeDArray(TypeBasic.tint32).getSignature(), 
			Signature.createDynamicArraySignature(Signature.SIG_INT));
	}
	
	public void testDynamicArray2() {
		assertEquals(new TypeDArray(TypeBasic.tint32).getSignature(), 
			new String(Signature.createDynamicArraySignature(Signature.SIG_INT.toCharArray())));
	}
	
	public void testAssociativeArray() {
		assertEquals(new TypeAArray(TypeBasic.tint32, TypeBasic.tchar).getSignature(), 
			Signature.createAssociativeArraySignature(Signature.SIG_INT, Signature.SIG_CHAR));
	}
	
	public void testAssociativeArray2() {
		assertEquals(new TypeAArray(TypeBasic.tint32, TypeBasic.tchar).getSignature(), 
			new String(Signature.createAssociativeArraySignature(Signature.SIG_INT.toCharArray(), Signature.SIG_CHAR.toCharArray())));
	}
	
	public void testTypeof() {
		assertEquals(new TypeTypeof(Loc.ZERO, new IntegerExp(3), new ASTNodeEncoder()).getSignature(), 
			Signature.createTypeofSignature("3"));
	}
	
	public void testTypeof2() {
		assertEquals(new TypeTypeof(Loc.ZERO, new IntegerExp(3), new ASTNodeEncoder()).getSignature(), 
			new String(Signature.createTypeofSignature("3".toCharArray())));
	}
	
	public void testSlice() {
		assertEquals(new TypeSlice(TypeBasic.tint32, new IntegerExp(1), new IntegerExp(23), new ASTNodeEncoder()).getSignature(), 
			Signature.createSliceSignature(Signature.SIG_INT, "1", "23"));
	}
	
	public void testSlice2() {
		assertEquals(new TypeSlice(TypeBasic.tint32, new IntegerExp(1), new IntegerExp(23), new ASTNodeEncoder()).getSignature(), 
			new String(Signature.createSliceSignature(Signature.SIG_INT.toCharArray(), "1".toCharArray(), "23".toCharArray())));
	}
	
	public void testSimpleNamePrimitive() {
		sn("int", "int");
	}
	
	public void testSimpleNameFunction() {
		sn("foo(int x, long z)", "foo(int x, long z)");
	}
	
	public void testSimpleNameInstance() {
		sn("foo!(int x, long z)", "foo!(int x, long z)");
	}
	
	public void testSimpleNameQualified() {
		sn("Bar", "foo.Bar");
	}
	
	public void testSimpleNameQualifiedMethod() {
		sn("method(int x, long z)", "foo.method(int x, long z)");
	}

	public void testSimpleNameQualifiedTemplate() {
		sn("method!(int x, long z)", "foo.method!(int x, long z)");
	}
	
	public void testSimpleNameQualifiedNested() {
		sn("Bar", "foo(some.Annoynace x).Bar");
	}
	
	public void testQualifierQualifiedMethod() {
		qf("foo.bar", "foo.bar.method(int x, long z)");
	}

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
		assertTrue(Signature.getVariadic(signature) != 0);
		assertTrue(Signature.getVariadic(signature.toCharArray()) != 0);
	}
	
	protected void varFalse(String signature) {
		assertFalse(Signature.getVariadic(signature.toCharArray()) != 0);
	}
	
	protected void varFail(String signature) {
		try {
			Signature.getVariadic(signature);
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			
		}
		
		try {
			Signature.getVariadic(signature.toCharArray());
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			
		}
	}
	
	protected void sn(String expected, String actual) {
		assertEquals(expected, Signature.getSimpleName(actual));
		assertEquals(expected, new String(Signature.getSimpleName(actual.toCharArray())));
	}
	
	protected void qf(String expected, String actual) {
		assertEquals(expected, Signature.getQualifier(actual));
		assertEquals(expected, new String(Signature.getQualifier(actual.toCharArray())));
	}
	
}
