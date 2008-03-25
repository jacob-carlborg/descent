package descent.tests.mangling;

import descent.internal.compiler.parser.ISignatureConstants;
import descent.internal.compiler.parser.IntegerExp;
import descent.internal.compiler.parser.Type;
import descent.internal.compiler.parser.TypeAArray;
import descent.internal.compiler.parser.TypeDArray;
import descent.internal.compiler.parser.TypePointer;
import descent.internal.compiler.parser.TypeSArray;
import descent.internal.compiler.parser.TypeSlice;
import descent.internal.compiler.parser.TypeTypeof;
import descent.internal.core.InternalSignature;

public class SignatureToType_Test extends AbstractSignatureTest implements ISignatureConstants {
	
	public void testPrimitive() {
		Type actual = InternalSignature.toType(i);
		assertSame(Type.tint32, actual);
	}
	
	public void testPointer() {
		TypePointer actual = (TypePointer) InternalSignature.toType(P(i));
		assertSame(Type.tint32, actual.next);
	}
	
	public void testStaticArray() {
		TypeSArray actual = (TypeSArray) InternalSignature.toType(G(i, "3"));
		assertSame(Type.tint32, actual.next);
		assertSame(3, ((IntegerExp) actual.dim).value.intValue());
	}
	
	public void testDynamicArray() {
		TypeDArray actual = (TypeDArray) InternalSignature.toType(A(i));
		assertSame(Type.tint32, actual.next);
	}
	
	public void testAssociativeArray() {
		TypeAArray actual = (TypeAArray) InternalSignature.toType(H(i, a));
		assertSame(Type.tint32, actual.index);
		assertSame(Type.tchar, actual.next);
	}
	
	public void testTypeof() {
		TypeTypeof actual = (TypeTypeof) InternalSignature.toType(typeof("3"));
		assertSame(3, ((IntegerExp) actual.exp).value.intValue());
	}
	
	public void testSlice() {
		TypeSlice actual = (TypeSlice) InternalSignature.toType(slice(i, "1", "3"));
		assertSame(Type.tint32, actual.next);
		assertSame(1, ((IntegerExp) actual.lwr).value.intValue());
		assertSame(3, ((IntegerExp) actual.upr).value.intValue());
	}
	
}
