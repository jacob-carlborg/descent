package descent.tests.mangling;

import descent.core.Signature;
import descent.core.dom.AST;
import descent.internal.compiler.parser.Argument;
import descent.internal.compiler.parser.ISignatureConstants;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.IntegerExp;
import descent.internal.compiler.parser.LINK;
import descent.internal.compiler.parser.Module;
import descent.internal.compiler.parser.Parser;
import descent.internal.compiler.parser.STC;
import descent.internal.compiler.parser.TemplateInstance;
import descent.internal.compiler.parser.TemplateInstanceWrapper;
import descent.internal.compiler.parser.Type;
import descent.internal.compiler.parser.TypeAArray;
import descent.internal.compiler.parser.TypeDArray;
import descent.internal.compiler.parser.TypeDelegate;
import descent.internal.compiler.parser.TypeFunction;
import descent.internal.compiler.parser.TypeIdentifier;
import descent.internal.compiler.parser.TypeInstance;
import descent.internal.compiler.parser.TypePointer;
import descent.internal.compiler.parser.TypeSArray;
import descent.internal.compiler.parser.TypeSlice;
import descent.internal.compiler.parser.TypeTypeof;
import descent.internal.compiler.parser.VarDeclaration;
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
		
		assertEquals("Bar", new String(actual.ident.ident));
		assertEquals(2, actual.idents.size());
		assertEquals("Test", new String(actual.idents.get(0).ident));
		assertEquals("Foo", new String(actual.idents.get(1).ident));
	}
	
	public void testInstance() {
		TypeInstance typeInstance = (TypeInstance) InternalSignature.toType(Signature.C_IDENTIFIER + "3Foo" + Signature.C_TEMPLATE_INSTANCE + Signature.C_TEMPLATE_PARAMETERS_BREAK);
		TemplateInstance templInstance = typeInstance.tempinst;
		
		assertEquals("Foo", new String(templInstance.name.ident));
		assertTrue(templInstance.tiargs == null || templInstance.tiargs.isEmpty());
	}
	
	public void testInstanceType() {
		TypeInstance typeInstance = (TypeInstance) InternalSignature.toType(Signature.C_IDENTIFIER + "3Foo" + Signature.C_TEMPLATE_INSTANCE + Signature.C_TEMPLATE_INSTANCE_TYPE_PARAMETER + i + Signature.C_TEMPLATE_PARAMETERS_BREAK);
		TemplateInstance templInstance = typeInstance.tempinst;
		
		assertEquals("Foo", new String(templInstance.name.ident));
		assertEquals(1, templInstance.tiargs.size());
		
		assertSame(Type.tint32, templInstance.tiargs.get(0));
	}
	
	public void testInstanceSymbol() {
		TypeInstance typeInstance = (TypeInstance) InternalSignature.toType(Signature.C_IDENTIFIER + "3Foo" + Signature.C_TEMPLATE_INSTANCE + Signature.C_TEMPLATE_INSTANCE_SYMBOL_PARAMETER + i + Signature.C_TEMPLATE_PARAMETERS_BREAK);
		TemplateInstance templInstance = typeInstance.tempinst;
		
		assertEquals("Foo", new String(templInstance.name.ident));
		assertEquals(1, templInstance.tiargs.size());
		
		assertSame(Type.tint32, templInstance.tiargs.get(0));
	}
	
	public void testInstanceValue() {
		TypeInstance typeInstance = (TypeInstance) InternalSignature.toType(Signature.C_IDENTIFIER + "3Foo" + Signature.C_TEMPLATE_INSTANCE + Signature.C_TEMPLATE_INSTANCE_VALUE_PARAMETER + "1" + Signature.C_TEMPLATE_INSTANCE_VALUE_PARAMETER + "3" + Signature.C_TEMPLATE_PARAMETERS_BREAK);
		TemplateInstance templInstance = typeInstance.tempinst;
		
		assertEquals("Foo", new String(templInstance.name.ident));
		assertEquals(1, templInstance.tiargs.size());
		
		assertSame(3, ((IntegerExp) templInstance.tiargs.get(0)).value.intValue());
	}
	
	public void testQualifiedInstance() {
		TypeIdentifier typeIdent = (TypeIdentifier) InternalSignature.toType(Signature.C_IDENTIFIER + "3Foo" + "3Bar" + Signature.C_TEMPLATE_INSTANCE + Signature.C_TEMPLATE_PARAMETERS_BREAK);
		assertEquals("Foo", new String(typeIdent.ident.ident));
		
		assertEquals(1, typeIdent.idents.size());
		
		TemplateInstanceWrapper wrap = (TemplateInstanceWrapper) typeIdent.idents.get(0);
		TemplateInstance tempInstance = wrap.tempinst;
		
		assertEquals("Bar", new String(tempInstance.name.ident));
		assertTrue(tempInstance.tiargs == null || tempInstance.tiargs.isEmpty());
	}
	
	public void testNestedInstance() {
		TypeInstance typeInstance = (TypeInstance) InternalSignature.toType(Signature.C_IDENTIFIER + "3Foo" + Signature.C_TEMPLATE_INSTANCE + Signature.C_TEMPLATE_PARAMETERS_BREAK + Signature.C_IDENTIFIER + "3Bar" + Signature.C_TEMPLATE_INSTANCE + Signature.C_TEMPLATE_PARAMETERS_BREAK);
		TemplateInstance tempInstance = typeInstance.tempinst;
		
		assertEquals("Foo", new String(tempInstance.name.ident));
		assertTrue(tempInstance.tiargs == null || tempInstance.tiargs.isEmpty());
		
		assertEquals(1, typeInstance.idents.size());
		
		TemplateInstanceWrapper wrap = (TemplateInstanceWrapper) typeInstance.idents.get(0);
		tempInstance = wrap.tempinst;
		
		assertEquals("Bar", new String(tempInstance.name.ident));
		assertTrue(tempInstance.tiargs == null || tempInstance.tiargs.isEmpty());
	}
	
	public void testNestedQualifiedInstance() {
		TypeIdentifier typeIdent = (TypeIdentifier) InternalSignature.toType(Signature.C_IDENTIFIER + "3Foo3Bar" + Signature.C_TEMPLATE_INSTANCE + Signature.C_TEMPLATE_PARAMETERS_BREAK + Signature.C_IDENTIFIER + "3One3Two" + Signature.C_TEMPLATE_INSTANCE + Signature.C_TEMPLATE_PARAMETERS_BREAK);
		assertEquals("Foo", new String(typeIdent.ident.ident));
		
		assertEquals(3, typeIdent.idents.size());
		
		TemplateInstanceWrapper wrap = (TemplateInstanceWrapper) typeIdent.idents.get(0);
		TemplateInstance tempInstance = wrap.tempinst;
		
		assertEquals("Bar", new String(tempInstance.name.ident));
		assertTrue(tempInstance.tiargs == null || tempInstance.tiargs.isEmpty());
		
		assertEquals("One", new String(typeIdent.idents.get(1).ident));
		
		wrap = (TemplateInstanceWrapper) typeIdent.idents.get(2);
		tempInstance = wrap.tempinst;
		
		assertEquals("Two", new String(tempInstance.name.ident));
		assertTrue(tempInstance.tiargs == null || tempInstance.tiargs.isEmpty());
	}
	
	public void testTypeGetSignature1() {
		assertEquals("?3Foo!^i'", getTypeSignature("Foo!(int)"));
	}
	
	public void testTypeGetSignature2() {
		assertEquals("?3Foo!^i'?3Bar!^i'", getTypeSignature("Foo!(int).Bar!(int)"));
	}
	
	public void testTypeGetSignature3() {
		assertEquals("?3one3two3Foo!^i'", getTypeSignature("one.two.Foo!(int)"));
	}
	
	public void testTypeGetSignature4() {
		assertEquals("?3one3two3Foo!^i'?5three4four3Bar!^i\'", getTypeSignature("one.two.Foo!(int).three.four.Bar!(int)"));
	}
	
	private String getTypeSignature(String type) {
		Parser parser = new Parser(AST.D1, type + "x;");
		parser.nextToken();
		Module module = parser.parseModuleObj();
		VarDeclaration var = (VarDeclaration) module.members.get(0);
		return var.type.getSignature();
	}
	
}
