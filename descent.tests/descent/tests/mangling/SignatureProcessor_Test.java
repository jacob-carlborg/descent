package descent.tests.mangling;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.jmock.integration.junit3.MockObjectTestCase;

import descent.internal.compiler.parser.ISignatureConstants;
import descent.internal.compiler.parser.IntegerExp;
import descent.internal.compiler.parser.LINK;
import descent.internal.compiler.parser.STC;
import descent.internal.compiler.parser.Type;
import descent.internal.compiler.parser.TypeBasic;
import descent.internal.core.ISignatureRequestor;
import descent.internal.core.SignatureProcessor;

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
		final Sequence s = mockery.sequence("seq");
		
		checking(new Expectations() {{
			one(requestor).acceptPrimitive(Type.tint32); inSequence(s);
			one(requestor).acceptPointer("Pi"); inSequence(s);
		}});
		
		SignatureProcessor.process("Pi", requestor);
		
		mockery.assertIsSatisfied();
	}
	
	public void testDynamicArray() {
		final Sequence s = mockery.sequence("seq");
		
		checking(new Expectations() {{
			one(requestor).acceptPrimitive(Type.tint32); inSequence(s);
			one(requestor).acceptDynamicArray("Ai"); inSequence(s);
		}});
		
		SignatureProcessor.process("Ai", requestor);
		
		mockery.assertIsSatisfied();
	}
	
	public void testStaticArray() {
		final Sequence s = mockery.sequence("seq");
		
		checking(new Expectations() {{
			one(requestor).acceptPrimitive(Type.tint32); inSequence(s);
			one(requestor).acceptStaticArray(3, "G3i"); inSequence(s);
		}});
		
		SignatureProcessor.process("G3i", requestor);
		
		mockery.assertIsSatisfied();
	}
	
	public void testAssociativeArray() {
		final Sequence s = mockery.sequence("seq");
		
		checking(new Expectations() {{
			one(requestor).acceptPrimitive(Type.tint32); inSequence(s);
			one(requestor).acceptPrimitive(Type.tchar); inSequence(s);
			one(requestor).acceptAssociativeArray("Hia"); inSequence(s);
		}});
		
		SignatureProcessor.process("Hia", requestor);
		
		mockery.assertIsSatisfied();
	}
	
	public void testFunctionType() {
		final Sequence s = mockery.sequence("seq");
		
		checking(new Expectations() {{
			one(requestor).enterFunctionType(); inSequence(s);
			one(requestor).acceptArgumentModifier(STC.STCin); inSequence(s);
			one(requestor).acceptPrimitive(TypeBasic.tint32); inSequence(s);
			one(requestor).acceptArgumentBreak('Z'); inSequence(s);
			one(requestor).acceptPrimitive(TypeBasic.tchar); inSequence(s);
			one(requestor).exitFunctionType(LINK.LINKd, "FiZa"); inSequence(s);
		}});
		
		SignatureProcessor.process("FiZa", requestor);
		
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
			one(requestor).acceptSymbol(CLASS, className, -1, "@6objectC6Object");
			one(requestor).acceptArgumentModifier(STC.STCin); inSequence(s);
			one(requestor).acceptModule(expectedModule, "@6object");
			one(requestor).acceptSymbol(CLASS, className, -1, "@6objectC6Object");
			one(requestor).acceptArgumentBreak('Z'); inSequence(s);
			one(requestor).acceptModule(expectedModule, "@6object");
			one(requestor).acceptSymbol(CLASS, className, -1, "@6objectC6Object");
			one(requestor).exitFunctionType(LINK.LINKd, "F@6objectC6Object@6objectC6ObjectZ@6objectC6Object"); inSequence(s);
		}});
		
		SignatureProcessor.process("F@6objectC6Object@6objectC6ObjectZ@6objectC6Object", requestor);
		
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
		final Sequence s = mockery.sequence("seq");
		
		final char type = FUNCTION;
		final String sig = type + "4testFZv";
		
		checking(new Expectations() {{
			char[] expected = "test".toCharArray();
			one(requestor).enterFunctionType(); inSequence(s);
			one(requestor).acceptArgumentBreak('Z'); inSequence(s);
			one(requestor).acceptPrimitive(TypeBasic.tvoid); inSequence(s);
			one(requestor).exitFunctionType(LINK.LINKd, "FZv"); inSequence(s);
			one(requestor).acceptSymbol(type, expected, -1, type + "4testFZv"); inSequence(s);
		}});
		
		SignatureProcessor.process(sig, requestor);
		
		mockery.assertIsSatisfied();
	}
	
	public void testClassInModule() {
		final Sequence s = mockery.sequence("seq");
		
		final String sigModule = MODULE + "4test3foo";
		final String sigClass = sigModule + CLASS + "3Bar";
		
		checking(new Expectations() {{
			char[][] expectedModule = { "test".toCharArray(), "foo".toCharArray() };
			char[] expectedClass = "Bar".toCharArray();
			one(requestor).acceptModule(expectedModule, sigModule); inSequence(s);
			one(requestor).acceptSymbol(CLASS, expectedClass, -1, sigClass); inSequence(s);
		}});
		
		SignatureProcessor.process(sigClass, requestor);
		
		mockery.assertIsSatisfied();
	}
	
	public void testClassInClassInModule() {
		final Sequence s = mockery.sequence("seq");
		
		final String sigModule = MODULE + "4test3foo";
		final String sigClass1 = sigModule + CLASS + "3Bar";
		final String sigClass2 = sigClass1 + CLASS + "4Bazz";
		
		checking(new Expectations() {{
			char[][] expectedModule = { "test".toCharArray(), "foo".toCharArray() };
			char[] expectedClass1 = "Bar".toCharArray();
			char[] expectedClass2 = "Bazz".toCharArray();
			one(requestor).acceptModule(expectedModule, sigModule); inSequence(s);
			one(requestor).acceptSymbol(CLASS, expectedClass1, -1, sigClass1); inSequence(s);
			one(requestor).acceptSymbol(CLASS, expectedClass2, -1, sigClass2); inSequence(s);
		}});
		
		SignatureProcessor.process(sigClass2, requestor);
		
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
			one(requestor).exitFunctionType(LINK.LINKd, "FZv"); inSequence(s);
			one(requestor).acceptSymbol(FUNCTION, expectedFunction, -1, sigFunction); inSequence(s);
		}});
		
		SignatureProcessor.process(sigFunction, requestor);
		
		mockery.assertIsSatisfied();
	}
	
	public void testFunctionInModule2() {
		final Sequence s = mockery.sequence("seq");
		
		final String sigModule = MODULE + "4test3foo";
		final String sigClass = sigModule + CLASS + "3Bar";
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
			one(requestor).acceptSymbol(CLASS, expectedClass, -1, sigClass); inSequence(s);
			one(requestor).acceptArgumentBreak('Z'); inSequence(s);
			one(requestor).acceptModule(expectedModule, sigModule); inSequence(s);
			one(requestor).acceptSymbol(CLASS, expectedClass, -1, sigClass); inSequence(s);
			one(requestor).exitFunctionType(LINK.LINKd, sigFunctionType); inSequence(s);
			one(requestor).acceptSymbol(FUNCTION, expectedFunction, -1, sigFunction); inSequence(s);
		}});
		
		SignatureProcessor.process(sigFunction, requestor);
		
		mockery.assertIsSatisfied();
	}
	
	public void testTemplate() {
		final Sequence s = mockery.sequence("seq");
		
		final String sigModule = MODULE + "4test3foo";
		final String sigTemplate = sigModule + TEMPLATE + "3Bar'";
		
		checking(new Expectations() {{
			char[][] expectedModule = { "test".toCharArray(), "foo".toCharArray() };
			char[] expectedTemplate = "Bar".toCharArray();
			one(requestor).acceptModule(expectedModule, sigModule); inSequence(s);
			one(requestor).enterTemplateParameters(); inSequence(s);
			one(requestor).exitTemplateParameters(); inSequence(s);
			one(requestor).acceptSymbol(TEMPLATE, expectedTemplate, -1, sigTemplate); inSequence(s);
		}});
		
		SignatureProcessor.process(sigTemplate, requestor);
		
		mockery.assertIsSatisfied();
	}
	
	public void testTemplateTupleParameter() {
		final Sequence s = mockery.sequence("seq");
		
		final String sigModule = MODULE + "4test3foo";
		final String sigTemplate = sigModule + TEMPLATE + "3Bar" + TEMPLATE_TUPLE_PARAMETER +"'";
		
		checking(new Expectations() {{
			char[][] expectedModule = { "test".toCharArray(), "foo".toCharArray() };
			char[] expectedTemplate = "Bar".toCharArray();
			one(requestor).acceptModule(expectedModule, sigModule); inSequence(s);
			one(requestor).enterTemplateParameters(); inSequence(s);
			one(requestor).acceptTemplateTupleParameter(); inSequence(s);
			one(requestor).exitTemplateParameters(); inSequence(s);
			one(requestor).acceptSymbol(TEMPLATE, expectedTemplate, -1, sigTemplate); inSequence(s);
		}});
		
		SignatureProcessor.process(sigTemplate, requestor);
		
		mockery.assertIsSatisfied();
	}
	
	public void testTemplateAliasParameter() {
		final Sequence s = mockery.sequence("seq");
		
		final String sigModule = MODULE + "4test3foo";
		final String sigTemplate = sigModule + TEMPLATE + "3Bar" + TEMPLATE_ALIAS_PARAMETER + "'";
		
		checking(new Expectations() {{
			char[][] expectedModule = { "test".toCharArray(), "foo".toCharArray() };
			char[] expectedTemplate = "Bar".toCharArray();
			one(requestor).acceptModule(expectedModule, sigModule); inSequence(s);
			one(requestor).enterTemplateParameters(); inSequence(s);
			one(requestor).enterTemplateAliasParameter(); inSequence(s);
			one(requestor).exitTemplateAliasParameter(String.valueOf(TEMPLATE_ALIAS_PARAMETER)); inSequence(s);
			one(requestor).exitTemplateParameters(); inSequence(s);
			one(requestor).acceptSymbol(TEMPLATE, expectedTemplate, -1, sigTemplate); inSequence(s);
		}});
		
		SignatureProcessor.process(sigTemplate, requestor);
		
		mockery.assertIsSatisfied();
	}
	
	public void testTemplateAliasParameterWithSpecificType() {
		final Sequence s = mockery.sequence("seq");
		
		final String sigModule = MODULE + "4test3foo";
		final String sigTemplate = sigModule + TEMPLATE + "3Bar" + TEMPLATE_ALIAS_PARAMETER + TEMPLATE_ALIAS_PARAMETER + "i'";
		
		checking(new Expectations() {{
			char[][] expectedModule = { "test".toCharArray(), "foo".toCharArray() };
			char[] expectedTemplate = "Bar".toCharArray();
			one(requestor).acceptModule(expectedModule, sigModule); inSequence(s);
			one(requestor).enterTemplateParameters(); inSequence(s);
			one(requestor).enterTemplateAliasParameter(); inSequence(s);
			one(requestor).acceptPrimitive(TypeBasic.tint32); inSequence(s);
			one(requestor).exitTemplateAliasParameter(String.valueOf(TEMPLATE_ALIAS_PARAMETER) + TEMPLATE_ALIAS_PARAMETER + "i"); inSequence(s);
			one(requestor).exitTemplateParameters(); inSequence(s);
			one(requestor).acceptSymbol(TEMPLATE, expectedTemplate, -1, sigTemplate); inSequence(s);
		}});
		
		SignatureProcessor.process(sigTemplate, requestor);
		
		mockery.assertIsSatisfied();
	}
	
	public void testTemplateTypeParameter() {
		final Sequence s = mockery.sequence("seq");
		
		final String sigModule = MODULE + "4test3foo";
		final String sigTemplate = sigModule + TEMPLATE + "3Bar" + TEMPLATE_TYPE_PARAMETER + "'";
		
		checking(new Expectations() {{
			char[][] expectedModule = { "test".toCharArray(), "foo".toCharArray() };
			char[] expectedTemplate = "Bar".toCharArray();
			one(requestor).acceptModule(expectedModule, sigModule); inSequence(s);
			one(requestor).enterTemplateParameters(); inSequence(s);
			one(requestor).enterTemplateTypeParameter(); inSequence(s);
			one(requestor).exitTemplateTypeParameter(String.valueOf(TEMPLATE_TYPE_PARAMETER)); inSequence(s);
			one(requestor).exitTemplateParameters(); inSequence(s);
			one(requestor).acceptSymbol(TEMPLATE, expectedTemplate, -1, sigTemplate); inSequence(s);
		}});
		
		SignatureProcessor.process(sigTemplate, requestor);
		
		mockery.assertIsSatisfied();
	}
	
	public void testTemplateTypeParameterWithSpecificType() {
		final Sequence s = mockery.sequence("seq");
		
		final String sigModule = MODULE + "4test3foo";
		final String sigTemplate = sigModule + TEMPLATE + "3Bar" + TEMPLATE_TYPE_PARAMETER + TEMPLATE_TYPE_PARAMETER + "i'";
		
		checking(new Expectations() {{
			char[][] expectedModule = { "test".toCharArray(), "foo".toCharArray() };
			char[] expectedTemplate = "Bar".toCharArray();
			one(requestor).acceptModule(expectedModule, sigModule); inSequence(s);
			one(requestor).enterTemplateParameters(); inSequence(s);
			one(requestor).enterTemplateTypeParameter(); inSequence(s);
			one(requestor).acceptPrimitive(TypeBasic.tint32); inSequence(s);
			one(requestor).exitTemplateTypeParameter(String.valueOf(TEMPLATE_TYPE_PARAMETER) + TEMPLATE_TYPE_PARAMETER + "i"); inSequence(s);
			one(requestor).exitTemplateParameters(); inSequence(s);
			one(requestor).acceptSymbol(TEMPLATE, expectedTemplate, -1, sigTemplate); inSequence(s);
		}});
		
		SignatureProcessor.process(sigTemplate, requestor);
		
		mockery.assertIsSatisfied();
	}
	
	public void testTemplateValueParameter() {
		final Sequence s = mockery.sequence("seq");
		
		final String sigModule = MODULE + "4test3foo";
		final String sigTemplate = sigModule + TEMPLATE + "3Bar" + TEMPLATE_VALUE_PARAMETER + "i'";
		
		checking(new Expectations() {{
			char[][] expectedModule = { "test".toCharArray(), "foo".toCharArray() };
			char[] expectedTemplate = "Bar".toCharArray();
			one(requestor).acceptModule(expectedModule, sigModule); inSequence(s);
			one(requestor).enterTemplateParameters(); inSequence(s);
			one(requestor).enterTemplateValueParameter(); inSequence(s);
			one(requestor).acceptPrimitive(TypeBasic.tint32); inSequence(s);
			one(requestor).exitTemplateValueParameter(TEMPLATE_VALUE_PARAMETER + "i"); inSequence(s);
			one(requestor).exitTemplateParameters(); inSequence(s);
			one(requestor).acceptSymbol(TEMPLATE, expectedTemplate, -1, sigTemplate); inSequence(s);
		}});
		
		SignatureProcessor.process(sigTemplate, requestor);
		
		mockery.assertIsSatisfied();
	}
	
	public void testTemplatedClass() {
		final Sequence s = mockery.sequence("seq");
		
		final String sigModule = MODULE + "4test3foo";
		final String sigTemplate = sigModule + TEMPLATED_AGGREGATE + "3Bar" + TEMPLATE_VALUE_PARAMETER + "i'";
		
		checking(new Expectations() {{
			char[][] expectedModule = { "test".toCharArray(), "foo".toCharArray() };
			char[] expectedTemplate = "Bar".toCharArray();
			one(requestor).acceptModule(expectedModule, sigModule); inSequence(s);
			one(requestor).enterTemplateParameters(); inSequence(s);
			one(requestor).enterTemplateValueParameter(); inSequence(s);
			one(requestor).acceptPrimitive(TypeBasic.tint32); inSequence(s);
			one(requestor).exitTemplateValueParameter(TEMPLATE_VALUE_PARAMETER + "i"); inSequence(s);
			one(requestor).exitTemplateParameters(); inSequence(s);
			one(requestor).acceptSymbol(TEMPLATED_AGGREGATE, expectedTemplate, -1, sigTemplate); inSequence(s);
		}});
		
		SignatureProcessor.process(sigTemplate, requestor);
		
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
			one(requestor).exitFunctionType(LINK.LINKd, "FZv");inSequence(s);
			one(requestor).enterTemplateParameters(); inSequence(s);
			one(requestor).enterTemplateValueParameter(); inSequence(s);
			one(requestor).acceptPrimitive(TypeBasic.tint32); inSequence(s);
			one(requestor).exitTemplateValueParameter(TEMPLATE_VALUE_PARAMETER + "i"); inSequence(s);
			one(requestor).exitTemplateParameters(); inSequence(s);
			one(requestor).acceptSymbol(TEMPLATED_FUNCTION, expectedTemplate, -1, sigTemplate); inSequence(s);
		}});
		
		SignatureProcessor.process(sigTemplate, requestor);
		
		mockery.assertIsSatisfied();
	}
	
	public void testTemplateValueParameterWithSpecificValue() {
		final Sequence s = mockery.sequence("seq");
		
		final String sigModule = MODULE + "4test3foo";
		final String sigTemplate = sigModule + TEMPLATE + "3Bar" + TEMPLATE_VALUE_PARAMETER + "i3" + TEMPLATE_VALUE_PARAMETER + "123'";
		
		checking(new Expectations() {{
			char[][] expectedModule = { "test".toCharArray(), "foo".toCharArray() };
			char[] expectedTemplate = "Bar".toCharArray();
			one(requestor).acceptModule(expectedModule, sigModule); inSequence(s);
			one(requestor).enterTemplateParameters(); inSequence(s);
			one(requestor).enterTemplateValueParameter(); inSequence(s);
			one(requestor).acceptPrimitive(TypeBasic.tint32); inSequence(s);
			one(requestor).acceptTemplateValueParameterSpecificValue(with(any(IntegerExp.class))); inSequence(s);
			one(requestor).exitTemplateValueParameter(TEMPLATE_VALUE_PARAMETER + "i3" + TEMPLATE_VALUE_PARAMETER + "123"); inSequence(s);
			one(requestor).exitTemplateParameters(); inSequence(s);
			one(requestor).acceptSymbol(TEMPLATE, expectedTemplate, -1, sigTemplate); inSequence(s);
		}});
		
		SignatureProcessor.process(sigTemplate, requestor);
		
		mockery.assertIsSatisfied();
	}
	
	public void testTemplatedInstance() {
		final Sequence s = mockery.sequence("seq");
		
		final String sigInstance = String.valueOf(TEMPLATE_INSTANCE) + TEMPLATE_PARAMETERS_BREAK;
		
		checking(new Expectations() {{
			one(requestor).enterTemplateInstance(); inSequence(s);
			one(requestor).exitTemplateInstance(sigInstance); inSequence(s);
		}});
		
		SignatureProcessor.process(sigInstance, requestor);
		
		mockery.assertIsSatisfied();
	}
	
	public void testTemplatedInstanceType() {
		final Sequence s = mockery.sequence("seq");
		
		final String sigInstance = String.valueOf(TEMPLATE_INSTANCE) + TEMPLATE_INSTANCE_TYPE + "i" + TEMPLATE_PARAMETERS_BREAK;
		
		checking(new Expectations() {{
			one(requestor).enterTemplateInstance(); inSequence(s);
			one(requestor).enterTemplateInstanceType();
			one(requestor).acceptPrimitive(TypeBasic.tint32);
			one(requestor).exitTemplateInstanceTypeParameter(TEMPLATE_INSTANCE_TYPE + "i");
			one(requestor).exitTemplateInstance(sigInstance); inSequence(s);
		}});
		
		SignatureProcessor.process(sigInstance, requestor);
		
		mockery.assertIsSatisfied();
	}
	
	public void testTemplatedInstanceValue() {
		final Sequence s = mockery.sequence("seq");
		
		final String sigInstance = String.valueOf(TEMPLATE_INSTANCE) + TEMPLATE_INSTANCE_VALUE + "3" + TEMPLATE_INSTANCE_VALUE + "123" + TEMPLATE_PARAMETERS_BREAK;
		
		checking(new Expectations() {{
			one(requestor).enterTemplateInstance(); inSequence(s);
			one(requestor).acceptTemplateInstanceValue(with(any(IntegerExp.class)), with(any(String.class))); inSequence(s);
			one(requestor).exitTemplateInstance(sigInstance); inSequence(s);
		}});
		
		SignatureProcessor.process(sigInstance, requestor);
		
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
			one(requestor).acceptSymbol(CLASS, "Bar".toCharArray(), -1, "@4testC3Bar"); inSequence(s);
			one(requestor).exitTemplateInstanceSymbol(TEMPLATE_INSTANCE_SYMBOL  + "@4testC3Bar"); inSequence(s);
			one(requestor).exitTemplateInstance(sigInstance); inSequence(s);
		}});
		
		SignatureProcessor.process(sigInstance, requestor);
		
		mockery.assertIsSatisfied();
	}

}
