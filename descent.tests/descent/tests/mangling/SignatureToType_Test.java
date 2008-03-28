package descent.tests.mangling;

import descent.core.Signature;
import descent.internal.compiler.parser.Argument;
import descent.internal.compiler.parser.ISignatureConstants;
import descent.internal.compiler.parser.IntegerExp;
import descent.internal.compiler.parser.LINK;
import descent.internal.compiler.parser.STC;
import descent.internal.compiler.parser.Type;
import descent.internal.compiler.parser.TypeAArray;
import descent.internal.compiler.parser.TypeDArray;
import descent.internal.compiler.parser.TypeDelegate;
import descent.internal.compiler.parser.TypeFunction;
import descent.internal.compiler.parser.TypeIdentifier;
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
	
	public void testFunction() {
		TypeFunction actual = (TypeFunction) InternalSignature.toType(F + i + Z + a);
		assertEquals(LINK.LINKd, actual.linkage);
		assertSame(Type.tchar, actual.next);
		
		assertEquals(1, actual.parameters.size());
		
		Argument arg = actual.parameters.get(0);
		assertSame(Type.tint32, arg.type);
		assertEquals(STC.STCin, arg.storageClass);
	}
	
	public void testFunctionManyParameters() {
		TypeFunction actual = (TypeFunction) InternalSignature.toType(F + i + a + Z + v);
		assertEquals(LINK.LINKd, actual.linkage);
		assertSame(Type.tvoid, actual.next);
		
		assertEquals(2, actual.parameters.size());
		
		Argument arg;
		
		arg = actual.parameters.get(0);
		assertSame(Type.tint32, arg.type);
		assertEquals(STC.STCin, arg.storageClass);
		
		arg = actual.parameters.get(1);
		assertSame(Type.tchar, arg.type);
		assertEquals(STC.STCin, arg.storageClass);
	}
	
	public void testFunctionParameterStorageClass() {
		TypeFunction actual = (TypeFunction) InternalSignature.toType(F + 'J' + i + Z + a);
		assertEquals(LINK.LINKd, actual.linkage);
		assertSame(Type.tchar, actual.next);
		
		assertEquals(1, actual.parameters.size());
		
		Argument arg = actual.parameters.get(0);
		assertSame(Type.tint32, arg.type);
		assertEquals(STC.STCout, arg.storageClass);
	}
	
	public void testDelegate() {
		TypeDelegate delegate = (TypeDelegate) InternalSignature.toType(D + F + i + Z + a);
		
		TypeFunction actual = (TypeFunction) delegate.next;
		assertEquals(LINK.LINKd, actual.linkage);
		assertSame(Type.tchar, actual.next);
		
		assertEquals(1, actual.parameters.size());
		
		Argument arg = actual.parameters.get(0);
		assertSame(Type.tint32, arg.type);
		assertEquals(STC.STCin, arg.storageClass);
	}

	public void testIdentifier() {
		TypeIdentifier actual = (TypeIdentifier) InternalSignature.toType(Signature.C_IDENTIFIER + "3Foo");
		
		assertEquals("Foo", new String(actual.ident.ident));
		assertEquals(0, actual.idents.size());
	}
	
	public void testIdentifier2() {
		TypeIdentifier actual = (TypeIdentifier) InternalSignature.toType(Signature.C_IDENTIFIER + "3Bar4Test3Foo");
		
		assertEquals("Foo", new String(actual.ident.ident));
		assertEquals(2, actual.idents.size());
		assertEquals("Bar", new String(actual.idents.get(0).ident));
		assertEquals("Test", new String(actual.idents.get(1).ident));
	}
	
//	public void testInstance() {
//		TypeInstance instance = (TypeInstance) InternalSignature.toType(Signature.C_IDENTIFIER + "3Foo" + Signature.C_TEMPLATE_INSTANCE + Signature.C_TEMPLATE_INSTANCE_TYPE_PARAMETER + i + Signature.C_TEMPLATE_PARAMETERS_BREAK);
//		
//		TypeIdentifier actual = (TypeIdentifier) instance.next;
//		assertEquals("Foo", new String(actual.ident.ident));
//		assertEquals(0, actual.idents.size());
//	}
	
}
