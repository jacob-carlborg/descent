package descent.tests.mangling;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit3.MockObjectTestCase;

import descent.internal.compiler.parser.ISignatureConstants;
import descent.internal.compiler.parser.LINK;
import descent.internal.compiler.parser.STC;
import descent.internal.compiler.parser.Type;
import descent.internal.compiler.parser.TypeBasic;
import descent.internal.core.SignatureProcessor;
import descent.internal.core.SignatureProcessor.ISignatureRequestor;

public class SignatureProcessor_Test extends MockObjectTestCase implements ISignatureConstants {
	
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
	
	public void testModule() {
		final String sig = MODULE + "4test3foo";
		
		checking(new Expectations() {{
			char[][] expected = { "test".toCharArray(), "foo".toCharArray() };
			one(requestor).acceptModule(expected, sig);
		}});
		
		SignatureProcessor.process(sig, requestor);
		
		mockery.assertIsSatisfied();
	}
	
	public void testSymbol() {
		for(final char type : new char[] { 
			CLASS, STRUCT, UNION, INTERFACE, ENUM, ENUM_MEMBER, VARIABLE, ALIAS, TYPEDEF 
			}) {
			
			final String sig = type + "4test";
			
			checking(new Expectations() {{
				char[] expected = "test".toCharArray();
				one(requestor).acceptSymbol(type, expected, -1, sig);
			}});
			
			SignatureProcessor.process(sig, requestor);
			
			mockery.assertIsSatisfied();
		}
	}
	
	public void testFunction() {
		final char type = FUNCTION;
		final String sig = type + "4testFZv";
		
		checking(new Expectations() {{
			char[] expected = "test".toCharArray();
			one(requestor).enterFunctionType();
			one(requestor).acceptArgumentBreak('Z');
			one(requestor).acceptArgumentModifier(STC.STCin);
			one(requestor).acceptPrimitive(TypeBasic.tvoid);
			one(requestor).exitFunctionType(LINK.LINKd, "FZv");
			one(requestor).acceptSymbol(type, expected, -1, type + "4testFZv");
		}});
		
		SignatureProcessor.process(sig, requestor);
		
		mockery.assertIsSatisfied();
	}
	
	public void testClassInModule() {
		final String sigModule = MODULE + "4test3foo";
		final String sigClass = sigModule + CLASS + "3Bar";
		
		checking(new Expectations() {{
			char[][] expectedModule = { "test".toCharArray(), "foo".toCharArray() };
			char[] expectedClass = "Bar".toCharArray();
			one(requestor).acceptModule(expectedModule, sigModule);
			one(requestor).acceptSymbol(CLASS, expectedClass, -1, sigClass);
		}});
		
		SignatureProcessor.process(sigClass, requestor);
		
		mockery.assertIsSatisfied();
	}
	
	public void testClassInClassInModule() {
		final String sigModule = MODULE + "4test3foo";
		final String sigClass1 = sigModule + CLASS + "3Bar";
		final String sigClass2 = sigClass1 + CLASS + "4Bazz";
		
		checking(new Expectations() {{
			char[][] expectedModule = { "test".toCharArray(), "foo".toCharArray() };
			char[] expectedClass1 = "Bar".toCharArray();
			char[] expectedClass2 = "Bazz".toCharArray();
			one(requestor).acceptModule(expectedModule, sigModule);
			one(requestor).acceptSymbol(CLASS, expectedClass1, -1, sigClass1);
			one(requestor).acceptSymbol(CLASS, expectedClass2, -1, sigClass2);
		}});
		
		SignatureProcessor.process(sigClass2, requestor);
		
		mockery.assertIsSatisfied();
	}
	
	public void testFunctionInModule() {
		final String sigModule = MODULE + "4test3foo";
		final String sigFunction = sigModule + FUNCTION + "8someFuncFZv";
		
		checking(new Expectations() {{
			char[][] expectedModule = { "test".toCharArray(), "foo".toCharArray() };
			char[] expectedFunction = "someFunc".toCharArray();
			one(requestor).acceptModule(expectedModule, sigModule);
			one(requestor).enterFunctionType();
			one(requestor).acceptArgumentBreak('Z');
			one(requestor).acceptArgumentModifier(STC.STCin);
			one(requestor).acceptPrimitive(TypeBasic.tvoid);
			one(requestor).exitFunctionType(LINK.LINKd, "FZv");
			one(requestor).acceptSymbol(FUNCTION, expectedFunction, -1, sigFunction);
		}});
		
		SignatureProcessor.process(sigFunction, requestor);
		
		mockery.assertIsSatisfied();
	}
	
	public void testFunctionInModule2() {
		final String sigModule = MODULE + "4test3foo";
		final String sigClass = sigModule + CLASS + "3Bar";
		final String sigFunctionType = "F" + sigClass + "Z" + sigClass;
		final String sigFunction = sigModule + FUNCTION + "8someFunc" + sigFunctionType;
		
		checking(new Expectations() {{
			char[][] expectedModule = { "test".toCharArray(), "foo".toCharArray() };
			char[] expectedClass = "Bar".toCharArray();
			char[] expectedFunction = "someFunc".toCharArray();
			one(requestor).acceptModule(expectedModule, sigModule);
			one(requestor).enterFunctionType();
			one(requestor).acceptArgumentModifier(STC.STCin);
			one(requestor).acceptModule(expectedModule, sigModule);
			one(requestor).acceptSymbol(CLASS, expectedClass, -1, sigClass);
			one(requestor).acceptArgumentBreak('Z');
			one(requestor).acceptArgumentModifier(STC.STCin);
			one(requestor).acceptModule(expectedModule, sigModule);
			one(requestor).acceptSymbol(CLASS, expectedClass, -1, sigClass);
			one(requestor).exitFunctionType(LINK.LINKd, sigFunctionType);
			one(requestor).acceptSymbol(FUNCTION, expectedFunction, -1, sigFunction);
		}});
		
		SignatureProcessor.process(sigFunction, requestor);
		
		mockery.assertIsSatisfied();
	}

}
