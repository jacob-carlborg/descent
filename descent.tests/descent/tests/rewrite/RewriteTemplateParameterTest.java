package descent.tests.rewrite;

import descent.core.dom.AliasTemplateParameter;

public class RewriteTemplateParameterTest extends AbstractRewriteTest {
	
	public void testAliasTemplateParameterChangeName() throws Exception {
		AliasTemplateParameter param = (AliasTemplateParameter) beginTemplateParameter("alias X");
		param.setName(ast.newSimpleName("T"));		
		assertTemplateParameterEqualsTokenByToken("alias T", end());
	}
	
	public void testAliasTemplateParameterAddSpecificType() throws Exception {
		AliasTemplateParameter param = (AliasTemplateParameter) beginTemplateParameter("alias X");
		param.setSpecificObject(ast.newSimpleType(ast.newSimpleName("Y")));
		assertTemplateParameterEqualsTokenByToken("alias X : Y", end());
	}
	
	public void testAliasTemplateParameterRemoveSpecificType() throws Exception {
		AliasTemplateParameter param = (AliasTemplateParameter) beginTemplateParameter("alias X : Y");
		param.getSpecificObject().delete();
		assertTemplateParameterEqualsTokenByToken("alias X", end());
	}
	
	public void testAliasTemplateParameterAddDefaultType() throws Exception {
		AliasTemplateParameter param = (AliasTemplateParameter) beginTemplateParameter("alias X");
		param.setDefaultObject(ast.newSimpleType(ast.newSimpleName("Y")));
		assertTemplateParameterEqualsTokenByToken("alias X = Y", end());
	}
	
	public void testAliasTemplateParameterRemoveDefaultType() throws Exception {
		AliasTemplateParameter param = (AliasTemplateParameter) beginTemplateParameter("alias X = Y");
		param.getDefaultObject().delete();
		assertTemplateParameterEqualsTokenByToken("alias X", end());
	}
	
	public void testMultiChange() throws Exception {
		AliasTemplateParameter param = (AliasTemplateParameter) beginTemplateParameter("alias X");
		param.setName(ast.newSimpleName("T"));
		param.setSpecificObject(ast.newSimpleType(ast.newSimpleName("Y")));
		param.setDefaultObject(ast.newSimpleType(ast.newSimpleName("Z")));
		assertTemplateParameterEqualsTokenByToken("alias T : Y = Z", end());
	}

}
