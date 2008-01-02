package descent.tests.mangling;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit3.MockObjectTestCase;

import descent.internal.compiler.parser.LINK;
import descent.internal.compiler.parser.STC;
import descent.internal.compiler.parser.Type;
import descent.internal.compiler.parser.TypeBasic;
import descent.internal.core.SignatureProcessor;
import descent.internal.core.SignatureProcessor.ISignatureRequestor;

public class SignatureProcessor_Test extends MockObjectTestCase {
	
	protected Mockery mockery = new Mockery();
	protected ISignatureRequestor requestor;
	
	@Override
	protected void setUp() throws Exception {
		requestor = mock(ISignatureRequestor.class);
	}
	
	public void testPrimitiveType() {
		checking(new Expectations() {{
			one(requestor).acceptPrimitive(Type.tint32);
		}});
		
		SignatureProcessor.process("i", requestor);
		
		mockery.assertIsSatisfied();
	}
	
	public void testPointer() {
		checking(new Expectations() {{
			one(requestor).acceptPrimitive(Type.tint32);
			one(requestor).acceptPointer("Pi");
		}});
		
		SignatureProcessor.process("Pi", requestor);
		
		mockery.assertIsSatisfied();
	}
	
	public void testDynamicArray() {
		checking(new Expectations() {{
			one(requestor).acceptPrimitive(Type.tint32);
			one(requestor).acceptDynamicArray("Ai");
		}});
		
		SignatureProcessor.process("Ai", requestor);
		
		mockery.assertIsSatisfied();
	}
	
	public void testStaticArray() {
		checking(new Expectations() {{
			one(requestor).acceptPrimitive(Type.tint32);
			one(requestor).acceptStaticArray(3, "G3i");
		}});
		
		SignatureProcessor.process("G3i", requestor);
		
		mockery.assertIsSatisfied();
	}
	
	public void testAssociativeArray() {
		checking(new Expectations() {{
			one(requestor).acceptPrimitive(Type.tchar);
			one(requestor).acceptPrimitive(Type.tint32);
			one(requestor).acceptAssociativeArray("Hia");
		}});
		
		SignatureProcessor.process("Hia", requestor);
		
		mockery.assertIsSatisfied();
	}
	
	public void testEnum() {
		checking(new Expectations() {{
			char[][] expected = { "test".toCharArray(), "foo".toCharArray() };
			one(requestor).acceptEnum(expected, "E4test3foo");
		}});
		
		SignatureProcessor.process("E4test3foo", requestor);
		
		mockery.assertIsSatisfied();
	}
	
	public void testClass() {
		checking(new Expectations() {{
			char[][] expected = { "test".toCharArray(), "foo".toCharArray() };
			one(requestor).acceptClass(expected, "C4test3foo");
		}});
		
		SignatureProcessor.process("C4test3foo", requestor);
		
		mockery.assertIsSatisfied();
	}
	
	public void testStruct() {
		checking(new Expectations() {{
			char[][] expected = { "test".toCharArray(), "foo".toCharArray() };
			one(requestor).acceptStruct(expected, "S4test3foo");
		}});
		
		SignatureProcessor.process("S4test3foo", requestor);
		
		mockery.assertIsSatisfied();
	}
	
	public void testVariableAliasOrTypedef() {
		checking(new Expectations() {{
			char[][] expected = { "test".toCharArray(), "foo".toCharArray() };
			one(requestor).acceptVariableOrAlias(expected, "Q4test3foo");
		}});
		
		SignatureProcessor.process("Q4test3foo", requestor);
		
		mockery.assertIsSatisfied();
	}
	
	public void testFunction() {
		checking(new Expectations() {{
			char[][] expectedName = { "test".toCharArray(), "foo".toCharArray() };
			one(requestor).enterFunctionType();
			one(requestor).acceptArgumentModifier(STC.STCin);
			one(requestor).acceptPrimitive(TypeBasic.tchar);
			one(requestor).acceptArgumentBreak('Z');
			one(requestor).acceptArgumentModifier(STC.STCin);
			one(requestor).acceptPrimitive(TypeBasic.tint32);
			one(requestor).exitFunctionType(LINK.LINKd, "FiZa");
			one(requestor).acceptFunction(expectedName, "O4test3fooFiZa");
		}});
		
		SignatureProcessor.process("O4test3fooFiZa", requestor);
		
		mockery.assertIsSatisfied();
	}
	
	public void testFunctionType() {
		checking(new Expectations() {{
			one(requestor).enterFunctionType();
			one(requestor).acceptArgumentModifier(STC.STCin);
			one(requestor).acceptPrimitive(TypeBasic.tchar);
			one(requestor).acceptArgumentBreak('Z');
			one(requestor).acceptArgumentModifier(STC.STCin);
			one(requestor).acceptPrimitive(TypeBasic.tint32);
			one(requestor).exitFunctionType(LINK.LINKd, "FiZa");
		}});
		
		SignatureProcessor.process("FiZa", requestor);
		
		mockery.assertIsSatisfied();
	}
	
	public void testFunction2() {
		checking(new Expectations() {{
			char[][] expectedName = { "test".toCharArray(), "foo".toCharArray() };
			one(requestor).enterFunctionType();
			one(requestor).acceptArgumentModifier(STC.STCin);
			one(requestor).acceptClass(expectedName, "C4test3foo");
			one(requestor).acceptArgumentBreak('Z');
			one(requestor).acceptArgumentModifier(STC.STCin);
			one(requestor).acceptClass(expectedName, "C4test3foo");
			one(requestor).acceptArgumentModifier(STC.STCin);
			one(requestor).acceptClass(expectedName, "C4test3foo");
			one(requestor).exitFunctionType(LINK.LINKd, "FC4test3fooC4test3fooZC4test3foo");
			one(requestor).acceptFunction(expectedName, "O4test3fooFC4test3fooC4test3fooZC4test3foo");
		}});
		
		SignatureProcessor.process("O4test3fooFC4test3fooC4test3fooZC4test3foo", requestor);
		
		mockery.assertIsSatisfied();
	}
	
	public void testNestedFunction() {
		checking(new Expectations() {{
			char[][] expectedName = { "test".toCharArray(), "foo".toCharArray() };
			one(requestor).enterFunctionType();
			one(requestor).acceptArgumentModifier(STC.STCin);
			one(requestor).acceptPrimitive(TypeBasic.tchar);
			one(requestor).acceptArgumentBreak('Z');
			one(requestor).acceptArgumentModifier(STC.STCin);
			one(requestor).acceptPrimitive(TypeBasic.tint32);
			one(requestor).exitFunctionType(LINK.LINKd, "FiZa");
			one(requestor).acceptFunction(expectedName, "O4test3fooFiZa");
			one(requestor).enterFunctionType();
			one(requestor).acceptArgumentModifier(STC.STCin);
			one(requestor).acceptPrimitive(TypeBasic.tchar);
			one(requestor).acceptArgumentBreak('Z');
			one(requestor).acceptArgumentModifier(STC.STCin);
			one(requestor).acceptPrimitive(TypeBasic.tint32);
			one(requestor).exitFunctionType(LINK.LINKd, "FiZa");
			one(requestor).acceptFunction(expectedName, "OO4test3fooFiZa@4test3fooFiZa");
		}});
		
		SignatureProcessor.process("OO4test3fooFiZa@4test3fooFiZa", requestor);
		
		mockery.assertIsSatisfied();
	}
	
	public void testNestedClass() {
		checking(new Expectations() {{
			char[][] expectedName = { "test".toCharArray(), "foo".toCharArray() };
			one(requestor).enterFunctionType();
			one(requestor).acceptArgumentModifier(STC.STCin);
			one(requestor).acceptPrimitive(TypeBasic.tchar);
			one(requestor).acceptArgumentBreak('Z');
			one(requestor).acceptArgumentModifier(STC.STCin);
			one(requestor).acceptPrimitive(TypeBasic.tint32);
			one(requestor).exitFunctionType(LINK.LINKd, "FiZa");
			one(requestor).acceptFunction(expectedName, "O4test3fooFiZa");
			one(requestor).acceptClass(expectedName, "CO4test3fooFiZa@4test3foo");
		}});
		
		SignatureProcessor.process("CO4test3fooFiZa@4test3foo", requestor);
		
		mockery.assertIsSatisfied();
	}
	
	public void testObject() {
		checking(new Expectations() {{
			char[][] expected = { "object".toCharArray(), "Object".toCharArray() };
			one(requestor).acceptClass(expected, "C6Object");
		}});
		
		SignatureProcessor.process("C6Object", requestor);
		
		mockery.assertIsSatisfied();
	}
	
	public void testFunction3() {
		checking(new Expectations() {{
			char[][] expectedObjName = { "object".toCharArray(), "Object".toCharArray() };
			char[][] expectedFuncName = { "test".toCharArray(), "foo".toCharArray() };
			one(requestor).enterFunctionType();
			one(requestor).acceptArgumentModifier(STC.STCin);
			one(requestor).acceptClass(expectedObjName, "C6Object");
			one(requestor).acceptArgumentBreak('Z');
			one(requestor).acceptArgumentModifier(STC.STCin);
			one(requestor).acceptClass(expectedObjName, "C6Object");
			one(requestor).exitFunctionType(LINK.LINKd, "FC6ObjectZC6Object");
			one(requestor).acceptFunction(expectedFuncName, "O4test3fooFC6ObjectZC6Object");
		}});
		
		SignatureProcessor.process("O4test3fooFC6ObjectZC6Object", requestor);
		
		mockery.assertIsSatisfied();
	}

}
