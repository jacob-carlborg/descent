package descent.tests.mangling;

import descent.internal.compiler.parser.ISignatureConstants;
import descent.internal.compiler.parser.IntegerExp;
import descent.internal.compiler.parser.TemplateAliasParameter;
import descent.internal.compiler.parser.TemplateTupleParameter;
import descent.internal.compiler.parser.TemplateTypeParameter;
import descent.internal.compiler.parser.TemplateValueParameter;
import descent.internal.compiler.parser.Type;
import descent.internal.core.InternalSignature;

public class SignatureToTemplateParameter_Test extends AbstractSignatureTest implements ISignatureConstants {
	
	public void testTuple() {
		TemplateTupleParameter param = (TemplateTupleParameter) 
			InternalSignature.toTemplateParameter("" + TEMPLATE_TUPLE_PARAMETER);
		assertNull(param.ident);
	}
	
	public void testAlias() {
		TemplateAliasParameter param = (TemplateAliasParameter) 
			InternalSignature.toTemplateParameter("" + TEMPLATE_ALIAS_PARAMETER);
		assertNull(param.ident);
		assertNull(param.specAliasT);
		assertNull(param.defaultAlias);
	}
	
	public void testAliasSpecificType() {
		TemplateAliasParameter param = (TemplateAliasParameter) 
			InternalSignature.toTemplateParameter("" + TEMPLATE_ALIAS_PARAMETER + TEMPLATE_ALIAS_PARAMETER2 + i);
		assertNull(param.ident);
		assertSame(Type.tint32, param.specAliasT);
		assertNull(param.defaultAlias);
	}
	
	public void testType() {
		TemplateTypeParameter param = (TemplateTypeParameter) 
			InternalSignature.toTemplateParameter("" + TEMPLATE_TYPE_PARAMETER);
		assertNull(param.ident);
		assertNull(param.specType);
		assertNull(param.defaultType);
	}
	
	public void testTypeSpecificType() {
		TemplateTypeParameter param = (TemplateTypeParameter) 
			InternalSignature.toTemplateParameter("" + TEMPLATE_TYPE_PARAMETER + TEMPLATE_TYPE_PARAMETER2 + i);
		assertNull(param.ident);
		assertSame(Type.tint32, param.specType);
		assertNull(param.defaultType);
	}
	
	public void testValue() {
		TemplateValueParameter param = (TemplateValueParameter) 
			InternalSignature.toTemplateParameter("" + TEMPLATE_VALUE_PARAMETER + i);
		assertNull(param.ident);
		assertSame(Type.tint32, param.valType);
		assertNull(param.specValue);
	}
	
	public void testValueSpecific() {
		TemplateValueParameter param = (TemplateValueParameter) 
			InternalSignature.toTemplateParameter("" + TEMPLATE_VALUE_PARAMETER + i + TEMPLATE_VALUE_PARAMETER2 + "1" + TEMPLATE_VALUE_PARAMETER + "3");
		assertNull(param.ident);
		assertSame(Type.tint32, param.valType);
		assertEquals(3, ((IntegerExp) param.specValue).value.intValue());
	}
	
}
