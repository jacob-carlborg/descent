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
		param.setSpecificType(ast.newSimpleType(ast.newSimpleName("Y")));
		assertTemplateParameterEqualsTokenByToken("alias X : Y", end());
	}
	
	public void testAliasTemplateParameterRemoveSpecificType() throws Exception {
		AliasTemplateParameter param = (AliasTemplateParameter) beginTemplateParameter("alias X : Y");
		param.getSpecificType().delete();
		assertTemplateParameterEqualsTokenByToken("alias X", end());
	}
	
	public void testAliasTemplateParameterAddDefaultType() throws Exception {
		AliasTemplateParameter param = (AliasTemplateParameter) beginTemplateParameter("alias X");
		param.setDefaultType(ast.newSimpleType(ast.newSimpleName("Y")));
		assertTemplateParameterEqualsTokenByToken("alias X = Y", end());
	}
	
	public void testAliasTemplateParameterRemoveDefaultType() throws Exception {
		AliasTemplateParameter param = (AliasTemplateParameter) beginTemplateParameter("alias X = Y");
		param.getDefaultType().delete();
		assertTemplateParameterEqualsTokenByToken("alias X", end());
	}
	
	public void testMultiChange() throws Exception {
		AliasTemplateParameter param = (AliasTemplateParameter) beginTemplateParameter("alias X");
		param.setName(ast.newSimpleName("T"));
		param.setSpecificType(ast.newSimpleType(ast.newSimpleName("Y")));
		param.setDefaultType(ast.newSimpleType(ast.newSimpleName("Z")));
		assertTemplateParameterEqualsTokenByToken("alias T : Y = Z", end());
	}

}
