package descent.tests.mangling;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.jmock.integration.junit3.MockObjectTestCase;

import descent.core.Signature;
import descent.internal.compiler.parser.LINK;
import descent.internal.compiler.parser.STC;
import descent.internal.compiler.parser.Type;
import descent.internal.compiler.parser.TypeBasic;
import descent.internal.core.ISignatureRequestor;
import descent.internal.core.SignatureProcessor;

public class SignatureProcessor_Test extends MockObjectTestCase implements ISignatureTest {
	
	protected Mockery mockery = new Mockery();
	protected ISignatureRequestor requestor;
	
	@Override
	protected void setUp() throws Exception {
		requestor = mock(ISignatureRequestor.class);
	}
	
	public void testVoid() {
		checking(new Expectations() {{
			one(requestor).acceptPrimitive(Type.tvoid);
		}});
		
		SignatureProcessor.process("v", true, requestor);
		
		mockery.assertIsSatisfied();
	}
	
	public void testInt() {
		checking(new Expectations() {{
			one(requestor).acceptPrimitive(Type.tint32);
		}});
		
		SignatureProcessor.process("i", true, requestor);
		
		mockery.assertIsSatisfied();
	}
	
	public void testPointer() {
		final Sequence s = mockery.sequence("seq");
		
		checking(new Expectations() {{
			one(requestor).acceptPrimitive(Type.tint32); inSequence(s);
			one(requestor).acceptPointer("Pi"); inSequence(s);
		}});
		
		SignatureProcessor.process("Pi", true, requestor);
		
		mockery.assertIsSatisfied();
	}
	
	public void testDynamicArray() {
		final Sequence s = mockery.sequence("seq");
		
		checking(new Expectations() {{
			one(requestor).acceptPrimitive(Type.tint32); inSequence(s);
			one(requestor).acceptDynamicArray("Ai"); inSequence(s);
		}});
		
		SignatureProcessor.process("Ai", true, requestor);
		
		mockery.assertIsSatisfied();
	}
	
	public void testStaticArray() {
		final Sequence s = mockery.sequence("seq");
		
		checking(new Expectations() {{
			one(requestor).acceptPrimitive(Type.tint32); inSequence(s);
			one(requestor).acceptStaticArray(new char[] { '3' }, "GiG1G3"); inSequence(s);
		}});
		
		SignatureProcessor.process("GiG1G3", true, requestor);
		
		mockery.assertIsSatisfied();
	}
	
	public void testAssociativeArray() {
		final Sequence s = mockery.sequence("seq");
		
		checking(new Expectations() {{
			one(requestor).acceptPrimitive(Type.tint32); inSequence(s);
			one(requestor).acceptPrimitive(Type.tchar); inSequence(s);
			one(requestor).acceptAssociativeArray("Hia"); inSequence(s);
		}});
		
		SignatureProcessor.process("Hia", true, requestor);
		
		mockery.assertIsSatisfied();
	}
	
	public void testFunctionType() {
		final Sequence s = mockery.sequence("seq");
		
		checking(new Expectations() {{
			one(requestor).enterFunctionType(); inSequence(s);
			one(requestor).acceptArgumentModifier(STC.STCin); inSequence(s);
			one(requestor).acceptPrimitive(TypeBasic.tint32); inSequence(s);
			one(requestor).acceptArgumentModifier(STC.STCin); inSequence(s);
			one(requestor).acceptPrimitive(TypeBasic.tchar); inSequence(s);
			one(requestor).acceptArgumentBreak('Z'); inSequence(s);
			one(requestor).acceptPrimitive(TypeBasic.tchar); inSequence(s);
			one(requestor).exitFunctionType(LINK.LINKd, 'Z', "FiaZa"); inSequence(s);
		}});
		
		SignatureProcessor.process("FiaZa", true, requestor);
		
		mockery.assertIsSatisfied();
	}
	
	public void testFunctionType2() {
		final Sequence s = mockery.sequence("seq");
		
		checking(new Expectations() {{
			char[][] expectedModule = { "object".toCharArray() };
			char[] className = "Object".toCharArray();
			
			one(requestor).enterFunctionType(); inSequence(s);
			one(requestor).acceptArgumentModifier(STC.STCin); inSequence(s);
			one(requestor).acceptModule(expectedModule, "@6object");
			one(requestor).acceptSymbol(CLASS.charAt(0), className, -1, "@6objectC6Object");
			one(requestor).acceptArgumentModifier(STC.STCin); inSequence(s);
			one(requestor).acceptModule(expectedModule, "@6object");
			one(requestor).acceptSymbol(CLASS.charAt(0), className, -1, "@6objectC6Object");
			one(requestor).acceptArgumentBreak('Z'); inSequence(s);
			one(requestor).acceptModule(expectedModule, "@6object");
			one(requestor).acceptSymbol(CLASS.charAt(0), className, -1, "@6objectC6Object");
			one(requestor).exitFunctionType(LINK.LINKd, 'Z', "F@6objectC6Object@6objectC6ObjectZ@6objectC6Object"); inSequence(s);
		}});
		
		SignatureProcessor.process("F@6objectC6Object@6objectC6ObjectZ@6objectC6Object", true, requestor);
		
		mockery.assertIsSatisfied();
	}
	
	public void testModule() {
		final String sig = MODULE + "4test3foo";
		
		checking(new Expectations() {{
			char[][] expected = { "test".toCharArray(), "foo".toCharArray() };
			one(requestor).acceptModule(expected, sig);
		}});
		
		SignatureProcessor.process(sig, true, requestor);
		
		mockery.assertIsSatisfied();
	}
	
	public void testSymbol() {
		for(final String type : new String[] { 
			CLASS, STRUCT, UNION, INTERFACE, ENUM, ENUM_MEMBER, VARIABLE, ALIAS, TYPEDEF 
			}) {
			
			final String sig = type + "4test";
			
			checking(new Expectations() {{
				char[] expected = "test".toCharArray();
				one(requestor).acceptSymbol(type.charAt(0), expected, -1, sig);
			}});
			
			SignatureProcessor.process(sig, true, requestor);
			
			mockery.assertIsSatisfied();
		}
	}
	
	public void testFunction() {
		final Sequence s = mockery.sequence("seq");
		
		final char type = FUNCTION.charAt(0);
		final String sig = type + "4testFZv";
		
		checking(new Expectations() {{
			char[] expected = "test".toCharArray();
			one(requestor).enterFunctionType(); inSequence(s);
			one(requestor).acceptArgumentBreak('Z'); inSequence(s);
			one(requestor).acceptPrimitive(TypeBasic.tvoid); inSequence(s);
			one(requestor).exitFunctionType(LINK.LINKd, 'Z', "FZv"); inSequence(s);
			one(requestor).acceptSymbol(type, expected, -1, type + "4testFZv"); inSequence(s);
		}});
		
		SignatureProcessor.process(sig, true, requestor);
		
		mockery.assertIsSatisfied();
	}
	
	public void testClassInModule() {
		final Sequence s = mockery.sequence("seq");
		
		final String sigModule = MODULE + "4test3foo";
		final String sigClass = sigModule + CLASS.charAt(0) + "3Bar";
		
		checking(new Expectations() {{
			char[][] expectedModule = { "test".toCharArray(), "foo".toCharArray() };
			char[] expectedClass = "Bar".toCharArray();
			one(requestor).acceptModule(expectedModule, sigModule); inSequence(s);
			one(requestor).acceptSymbol(CLASS.charAt(0), expectedClass, -1, sigClass); inSequence(s);
		}});
		
		SignatureProcessor.process(sigClass, true, requestor);
		
		mockery.assertIsSatisfied();
	}
	
	public void testClassInClassInModule() {
		final Sequence s = mockery.sequence("seq");
		
		final String sigModule = MODULE + "4test3foo";
		final String sigClass1 = sigModule + CLASS.charAt(0) + "3Bar";
		final String sigClass2 = sigClass1 + CLASS.charAt(0) + "4Bazz";
		
		checking(new Expectations() {{
			char[][] expectedModule = { "test".toCharArray(), "foo".toCharArray() };
			char[] expectedClass1 = "Bar".toCharArray();
			char[] expectedClass2 = "Bazz".toCharArray();
			one(requestor).acceptModule(expectedModule, sigModule); inSequence(s);
			one(requestor).acceptSymbol(CLASS.charAt(0), expectedClass1, -1, sigClass1); inSequence(s);
			one(requestor).acceptSymbol(CLASS.charAt(0), expectedClass2, -1, sigClass2); inSequence(s);
		}});
		
		SignatureProcessor.process(sigClass2, true, requestor);
		
		mockery.assertIsSatisfied();
	}
	
	public void testFunctionInModule() {
		final Sequence s = mockery.sequence("seq");
		
		final String sigModule = MODULE + "4test3foo";
		final String sigFunction = sigModule + FUNCTION + "8someFuncFZv";
		
		checking(new Expectations() {{
			char[][] expectedModule = { "test".toCharArray(), "foo".toCharArray() };
			char[] expectedFunction = "someFunc".toCharArray();
			one(requestor).acceptModule(expectedModule, sigModule); inSequence(s);
			one(requestor).enterFunctionType(); inSequence(s);
			one(requestor).acceptArgumentBreak('Z'); inSequence(s);
			one(requestor).acceptPrimitive(TypeBasic.tvoid); inSequence(s);
			one(requestor).exitFunctionType(LINK.LINKd, 'Z', "FZv"); inSequence(s);
			one(requestor).acceptSymbol(FUNCTION.charAt(0), expectedFunction, -1, sigFunction); inSequence(s);
		}});
		
		SignatureProcessor.process(sigFunction, true, requestor);
		
		mockery.assertIsSatisfied();
	}
	
	public void testFunctionInModule2() {
		final Sequence s = mockery.sequence("seq");
		
		final String sigModule = MODULE + "4test3foo";
		final String sigClass = sigModule + CLASS.charAt(0) + "3Bar";
		final String sigFunctionType = "F" + sigClass + "Z" + sigClass;
		final String sigFunction = sigModule + FUNCTION + "8someFunc" + sigFunctionType;
		
		checking(new Expectations() {{
			char[][] expectedModule = { "test".toCharArray(), "foo".toCharArray() };
			char[] expectedClass = "Bar".toCharArray();
			char[] expectedFunction = "someFunc".toCharArray();
			one(requestor).acceptModule(expectedModule, sigModule); inSequence(s);
			one(requestor).enterFunctionType(); inSequence(s);
			one(requestor).acceptArgumentModifier(STC.STCin); inSequence(s);
			one(requestor).acceptModule(expectedModule, sigModule); inSequence(s);
			one(requestor).acceptSymbol(CLASS.charAt(0), expectedClass, -1, sigClass); inSequence(s);
			one(requestor).acceptArgumentBreak('Z'); inSequence(s);
			one(requestor).acceptModule(expectedModule, sigModule); inSequence(s);
			one(requestor).acceptSymbol(CLASS.charAt(0), expectedClass, -1, sigClass); inSequence(s);
			one(requestor).exitFunctionType(LINK.LINKd, 'Z', sigFunctionType); inSequence(s);
			one(requestor).acceptSymbol(FUNCTION.charAt(0), expectedFunction, -1, sigFunction); inSequence(s);
		}});
		
		SignatureProcessor.process(sigFunction, true, requestor);
		
		mockery.assertIsSatisfied();
	}
	
	public void testTemplate() {
		final Sequence s = mockery.sequence("seq");
		
		final String sigModule = MODULE + "4test3foo";
		final String sigTemplate = sigModule + TEMPLATE.charAt(0) + "3Bar'";
		
		checking(new Expectations() {{
			char[][] expectedModule = { "test".toCharArray(), "foo".toCharArray() };
			char[] expectedTemplate = "Bar".toCharArray();
			one(requestor).acceptModule(expectedModule, sigModule); inSequence(s);
			one(requestor).enterTemplateParameters(); inSequence(s);
			one(requestor).exitTemplateParameters(); inSequence(s);
			one(requestor).acceptSymbol(TEMPLATE.charAt(0), expectedTemplate, -1, sigTemplate); inSequence(s);
		}});
		
		SignatureProcessor.process(sigTemplate, true, requestor);
		
		mockery.assertIsSatisfied();
	}
	
	public void testTemplateTupleParameter() {
		final Sequence s = mockery.sequence("seq");
		
		final String sigModule = MODULE + "4test3foo";
		final String sigTemplate = sigModule + TEMPLATE.charAt(0) + "3Bar" + TEMPLATE_TUPLE_PARAMETER +"'";
		
		checking(new Expectations() {{
			char[][] expectedModule = { "test".toCharArray(), "foo".toCharArray() };
			char[] expectedTemplate = "Bar".toCharArray();
			one(requestor).acceptModule(expectedModule, sigModule); inSequence(s);
			one(requestor).enterTemplateParameters(); inSequence(s);
			one(requestor).acceptTemplateTupleParameter(); inSequence(s);
			one(requestor).exitTemplateParameters(); inSequence(s);
			one(requestor).acceptSymbol(TEMPLATE.charAt(0), expectedTemplate, -1, sigTemplate); inSequence(s);
		}});
		
		SignatureProcessor.process(sigTemplate, true, requestor);
		
		mockery.assertIsSatisfied();
	}
	
	public void testTemplateAliasParameter() {
		final Sequence s = mockery.sequence("seq");
		
		final String sigModule = MODULE + "4test3foo";
		final String sigTemplate = sigModule + TEMPLATE.charAt(0) + "3Bar" + TEMPLATE_ALIAS_PARAMETER + "'";
		
		checking(new Expectations() {{
			char[][] expectedModule = { "test".toCharArray(), "foo".toCharArray() };
			char[] expectedTemplate = "Bar".toCharArray();
			one(requestor).acceptModule(expectedModule, sigModule); inSequence(s);
			one(requestor).enterTemplateParameters(); inSequence(s);
			one(requestor).enterTemplateAliasParameter(); inSequence(s);
			one(requestor).exitTemplateAliasParameter(String.valueOf(TEMPLATE_ALIAS_PARAMETER)); inSequence(s);
			one(requestor).exitTemplateParameters(); inSequence(s);
			one(requestor).acceptSymbol(TEMPLATE.charAt(0), expectedTemplate, -1, sigTemplate); inSequence(s);
		}});
		
		SignatureProcessor.process(sigTemplate, true, requestor);
		
		mockery.assertIsSatisfied();
	}
	
	public void testTemplateAliasParameterWithSpecificType() {
		final Sequence s = mockery.sequence("seq");
		
		final String sigModule = MODULE + "4test3foo";
		final String sigTemplate = sigModule + TEMPLATE.charAt(0) + "3Bar" + TEMPLATE_ALIAS_PARAMETER + TEMPLATE_ALIAS_PARAMETER2 + "i'";
		
		checking(new Expectations() {{
			char[][] expectedModule = { "test".toCharArray(), "foo".toCharArray() };
			char[] expectedTemplate = "Bar".toCharArray();
			one(requestor).acceptModule(expectedModule, sigModule); inSequence(s);
			one(requestor).enterTemplateParameters(); inSequence(s);
			one(requestor).enterTemplateAliasParameter(); inSequence(s);
			one(requestor).acceptPrimitive(TypeBasic.tint32); inSequence(s);
			one(requestor).exitTemplateAliasParameter(String.valueOf(TEMPLATE_ALIAS_PARAMETER) + TEMPLATE_ALIAS_PARAMETER2 + "i"); inSequence(s);
			one(requestor).exitTemplateParameters(); inSequence(s);
			one(requestor).acceptSymbol(TEMPLATE.charAt(0), expectedTemplate, -1, sigTemplate); inSequence(s);
		}});
		
		SignatureProcessor.process(sigTemplate, true, requestor);
		
		mockery.assertIsSatisfied();
	}
	
	public void testTemplateTypeParameter() {
		final Sequence s = mockery.sequence("seq");
		
		final String sigModule = MODULE + "4test3foo";
		final String sigTemplate = sigModule + TEMPLATE.charAt(0) + "3Bar" + TEMPLATE_TYPE_PARAMETER + "'";
		
		checking(new Expectations() {{
			char[][] expectedModule = { "test".toCharArray(), "foo".toCharArray() };
			char[] expectedTemplate = "Bar".toCharArray();
			one(requestor).acceptModule(expectedModule, sigModule); inSequence(s);
			one(requestor).enterTemplateParameters(); inSequence(s);
			one(requestor).enterTemplateTypeParameter(); inSequence(s);
			one(requestor).exitTemplateTypeParameter(String.valueOf(TEMPLATE_TYPE_PARAMETER)); inSequence(s);
			one(requestor).exitTemplateParameters(); inSequence(s);
			one(requestor).acceptSymbol(TEMPLATE.charAt(0), expectedTemplate, -1, sigTemplate); inSequence(s);
		}});
		
		SignatureProcessor.process(sigTemplate, true, requestor);
		
		mockery.assertIsSatisfied();
	}
	
	public void testTemplateTypeParameterWithSpecificType() {
		final Sequence s = mockery.sequence("seq");
		
		final String sigModule = MODULE + "4test3foo";
		final String sigTemplate = sigModule + TEMPLATE.charAt(0) + "3Bar" + TEMPLATE_TYPE_PARAMETER + TEMPLATE_TYPE_PARAMETER2 + "i'";
		
		checking(new Expectations() {{
			char[][] expectedModule = { "test".toCharArray(), "foo".toCharArray() };
			char[] expectedTemplate = "Bar".toCharArray();
			one(requestor).acceptModule(expectedModule, sigModule); inSequence(s);
			one(requestor).enterTemplateParameters(); inSequence(s);
			one(requestor).enterTemplateTypeParameter(); inSequence(s);
			one(requestor).acceptPrimitive(TypeBasic.tint32); inSequence(s);
			one(requestor).exitTemplateTypeParameter(String.valueOf(TEMPLATE_TYPE_PARAMETER) + TEMPLATE_TYPE_PARAMETER2 + "i"); inSequence(s);
			one(requestor).exitTemplateParameters(); inSequence(s);
			one(requestor).acceptSymbol(TEMPLATE.charAt(0), expectedTemplate, -1, sigTemplate); inSequence(s);
		}});
		
		SignatureProcessor.process(sigTemplate, true, requestor);
		
		mockery.assertIsSatisfied();
	}
	
	public void testTemplateValueParameter() {
		final Sequence s = mockery.sequence("seq");
		
		final String sigModule = MODULE + "4test3foo";
		final String sigTemplate = sigModule + TEMPLATE.charAt(0) + "3Bar" + TEMPLATE_VALUE_PARAMETER + "i'";
		
		checking(new Expectations() {{
			char[][] expectedModule = { "test".toCharArray(), "foo".toCharArray() };
			char[] expectedTemplate = "Bar".toCharArray();
			one(requestor).acceptModule(expectedModule, sigModule); inSequence(s);
			one(requestor).enterTemplateParameters(); inSequence(s);
			one(requestor).enterTemplateValueParameter(); inSequence(s);
			one(requestor).acceptPrimitive(TypeBasic.tint32); inSequence(s);
			one(requestor).exitTemplateValueParameter(TEMPLATE_VALUE_PARAMETER + "i"); inSequence(s);
			one(requestor).exitTemplateParameters(); inSequence(s);
			one(requestor).acceptSymbol(TEMPLATE.charAt(0), expectedTemplate, -1, sigTemplate); inSequence(s);
		}});
		
		SignatureProcessor.process(sigTemplate, true, requestor);
		
		mockery.assertIsSatisfied();
	}
	
	public void testTemplatedClass() {
		final Sequence s = mockery.sequence("seq");
		
		final String sigModule = MODULE + "4test3foo";
		final String sigTemplate = sigModule + TEMPLATED_CLASS.charAt(0) + "3Bar" + TEMPLATE_VALUE_PARAMETER + "i'";
		
		checking(new Expectations() {{
			char[][] expectedModule = { "test".toCharArray(), "foo".toCharArray() };
			char[] expectedTemplate = "Bar".toCharArray();
			one(requestor).acceptModule(expectedModule, sigModule); inSequence(s);
			one(requestor).enterTemplateParameters(); inSequence(s);
			one(requestor).enterTemplateValueParameter(); inSequence(s);
			one(requestor).acceptPrimitive(TypeBasic.tint32); inSequence(s);
			one(requestor).exitTemplateValueParameter(TEMPLATE_VALUE_PARAMETER + "i"); inSequence(s);
			one(requestor).exitTemplateParameters(); inSequence(s);
			one(requestor).acceptSymbol(TEMPLATED_CLASS.charAt(0), expectedTemplate, -1, sigTemplate); inSequence(s);
		}});
		
		SignatureProcessor.process(sigTemplate, true, requestor);
		
		mockery.assertIsSatisfied();
	}
	
	public void testTemplatedFunction() {
		final Sequence s = mockery.sequence("seq");
		
		final String sigModule = MODULE + "4test3foo";
		final String sigTemplate = sigModule + TEMPLATED_FUNCTION + "3BarFZv" + TEMPLATE_VALUE_PARAMETER + "i'";
		
		checking(new Expectations() {{
			char[][] expectedModule = { "test".toCharArray(), "foo".toCharArray() };
			char[] expectedTemplate = "Bar".toCharArray();
			one(requestor).acceptModule(expectedModule, sigModule); inSequence(s);
			one(requestor).enterFunctionType();inSequence(s);
			one(requestor).acceptArgumentBreak('Z');inSequence(s);
			one(requestor).acceptPrimitive(TypeBasic.tvoid);inSequence(s);
			one(requestor).exitFunctionType(LINK.LINKd, 'Z', "FZv");inSequence(s);
			one(requestor).enterTemplateParameters(); inSequence(s);
			one(requestor).enterTemplateValueParameter(); inSequence(s);
			one(requestor).acceptPrimitive(TypeBasic.tint32); inSequence(s);
			one(requestor).exitTemplateValueParameter(TEMPLATE_VALUE_PARAMETER + "i"); inSequence(s);
			one(requestor).exitTemplateParameters(); inSequence(s);
			one(requestor).acceptSymbol(TEMPLATED_FUNCTION.charAt(0), expectedTemplate, -1, sigTemplate); inSequence(s);
		}});
		
		SignatureProcessor.process(sigTemplate, true, requestor);
		
		mockery.assertIsSatisfied();
	}
	
	public void testTemplateValueParameterWithSpecificValue() {
		final Sequence s = mockery.sequence("seq");
		
		final String sigModule = MODULE + "4test3foo";
		final String sigTemplate = sigModule + TEMPLATE.charAt(0) + "3Bar" + TEMPLATE_VALUE_PARAMETER + "i" + TEMPLATE_VALUE_PARAMETER2 + "3" + TEMPLATE_VALUE_PARAMETER + "123'";
		
		checking(new Expectations() {{
			char[][] expectedModule = { "test".toCharArray(), "foo".toCharArray() };
			char[] expectedTemplate = "Bar".toCharArray();
			one(requestor).acceptModule(expectedModule, sigModule); inSequence(s);
			one(requestor).enterTemplateParameters(); inSequence(s);
			one(requestor).enterTemplateValueParameter(); inSequence(s);
			one(requestor).acceptPrimitive(TypeBasic.tint32); inSequence(s);
			one(requestor).acceptTemplateValueParameterSpecificValue("123".toCharArray()); inSequence(s);
			one(requestor).exitTemplateValueParameter(TEMPLATE_VALUE_PARAMETER + "i" + TEMPLATE_VALUE_PARAMETER2 + "3" + TEMPLATE_VALUE_PARAMETER + "123"); inSequence(s);
			one(requestor).exitTemplateParameters(); inSequence(s);
			one(requestor).acceptSymbol(TEMPLATE.charAt(0), expectedTemplate, -1, sigTemplate); inSequence(s);
		}});
		
		SignatureProcessor.process(sigTemplate, true, requestor);
		
		mockery.assertIsSatisfied();
	}
	
	public void testTemplatedInstance() {
		final Sequence s = mockery.sequence("seq");
		
		final String sigInstance = String.valueOf(TEMPLATE_INSTANCE) + TEMPLATE_PARAMETERS_BREAK;
		
		checking(new Expectations() {{
			one(requestor).enterTemplateInstance(); inSequence(s);
			one(requestor).exitTemplateInstance(sigInstance); inSequence(s);
		}});
		
		SignatureProcessor.process(sigInstance, true, requestor);
		
		mockery.assertIsSatisfied();
	}
	
	public void testTemplatedInstanceType() {
		final Sequence s = mockery.sequence("seq");
		
		final String sigInstance = String.valueOf(TEMPLATE_INSTANCE) + TEMPLATE_INSTANCE_TYPE + "i" + TEMPLATE_PARAMETERS_BREAK;
		
		checking(new Expectations() {{
			one(requestor).enterTemplateInstance(); inSequence(s);
			one(requestor).enterTemplateInstanceType();
			one(requestor).acceptPrimitive(TypeBasic.tint32);
			one(requestor).exitTemplateInstanceType(TEMPLATE_INSTANCE_TYPE + "i");
			one(requestor).exitTemplateInstance(sigInstance); inSequence(s);
		}});
		
		SignatureProcessor.process(sigInstance, true, requestor);
		
		mockery.assertIsSatisfied();
	}
	
	public void testTemplatedInstanceValue() {
		final Sequence s = mockery.sequence("seq");
		
		final String sigInstance = String.valueOf(TEMPLATE_INSTANCE) + TEMPLATE_INSTANCE_VALUE + "3" + TEMPLATE_INSTANCE_VALUE + "123" + TEMPLATE_PARAMETERS_BREAK;
		
		checking(new Expectations() {{
			one(requestor).enterTemplateInstance(); inSequence(s);
			one(requestor).acceptTemplateInstanceValue("123".toCharArray(), TEMPLATE_INSTANCE_VALUE + "3" + TEMPLATE_INSTANCE_VALUE + "123"); inSequence(s);
			one(requestor).exitTemplateInstance(sigInstance); inSequence(s);
		}});
		
		SignatureProcessor.process(sigInstance, true, requestor);
		
		mockery.assertIsSatisfied();
	}
	
	public void testTemplatedInstanceSymbol() {
		final Sequence s = mockery.sequence("seq");
		
		final String sigInstance = String.valueOf(TEMPLATE_INSTANCE) + TEMPLATE_INSTANCE_SYMBOL  + "@4testC3Bar" + TEMPLATE_PARAMETERS_BREAK;
		
		checking(new Expectations() {{
			char[][] expectedModule = { "test".toCharArray() };
			
			one(requestor).enterTemplateInstance(); inSequence(s);
			one(requestor).enterTemplateInstanceSymbol(); inSequence(s);
			one(requestor).acceptModule(expectedModule, "@4test"); inSequence(s);
			one(requestor).acceptSymbol(CLASS.charAt(0), "Bar".toCharArray(), -1, "@4testC3Bar"); inSequence(s);
			one(requestor).exitTemplateInstanceSymbol(TEMPLATE_INSTANCE_SYMBOL  + "@4testC3Bar"); inSequence(s);
			one(requestor).exitTemplateInstance(sigInstance); inSequence(s);
		}});
		
		SignatureProcessor.process(sigInstance, true, requestor);
		
		mockery.assertIsSatisfied();
	}
	
	public void testTuple() {
		final Sequence s = mockery.sequence("seq");
		
		final String sigInstance = String.valueOf(Signature.C_TUPLE) + "2" + String.valueOf(Signature.C_TUPLE) + Signature.SIG_INT + Signature.SIG_BOOL;
		
		checking(new Expectations() {{
			one(requestor).acceptPrimitive(TypeBasic.tint32); inSequence(s);
			one(requestor).acceptPrimitive(TypeBasic.tbool); inSequence(s);
			one(requestor).acceptTuple(sigInstance, 2); inSequence(s);
		}});
		
		SignatureProcessor.process(sigInstance, true, requestor);
		
		mockery.assertIsSatisfied();
	}

}
