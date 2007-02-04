package descent.tests.rewrite;

import descent.core.dom.AggregateDeclaration;
import descent.core.dom.AliasTemplateParameter;

public class RewriteAliasTemplateParameterTest extends RewriteTest {
	
	public void testAliasTemplateParameterChangeName() throws Exception {
		begin("class C (alias X) { }");
		
		AggregateDeclaration agg = (AggregateDeclaration) unit.declarations().get(0);
		AliasTemplateParameter param = (AliasTemplateParameter) agg.templateParameters().get(0);
		param.setName(ast.newSimpleName("T"));
		
		assertEqualsTokenByToken("class C (alias T) { }", end());
	}
	
	public void testAliasTemplateParameterAddSpecificType() throws Exception {
		begin("class C (alias X) { }");
		
		AggregateDeclaration agg = (AggregateDeclaration) unit.declarations().get(0);
		AliasTemplateParameter param = (AliasTemplateParameter) agg.templateParameters().get(0);
		param.setSpecificType(ast.newSimpleType(ast.newSimpleName("Y")));
		
		assertEqualsTokenByToken("class C (alias X : Y) { }", end());
	}
	
	public void testAliasTemplateParameterRemoveSpecificType() throws Exception {
		begin("class C (alias X : Y) { }");
		
		AggregateDeclaration agg = (AggregateDeclaration) unit.declarations().get(0);
		AliasTemplateParameter param = (AliasTemplateParameter) agg.templateParameters().get(0);
		param.getSpecificType().delete();
		
		assertEqualsTokenByToken("class C (alias X) { }", end());
	}
	
	public void testAliasTemplateParameterAddDefaultType() throws Exception {
		begin("class C (alias X) { }");
		
		AggregateDeclaration agg = (AggregateDeclaration) unit.declarations().get(0);
		AliasTemplateParameter param = (AliasTemplateParameter) agg.templateParameters().get(0);
		param.setDefaultType(ast.newSimpleType(ast.newSimpleName("Y")));
		
		assertEqualsTokenByToken("class C (alias X = Y) { }", end());
	}
	
	public void testAliasTemplateParameterRemoveDefaultType() throws Exception {
		begin("class C (alias X = Y) { }");
		
		AggregateDeclaration agg = (AggregateDeclaration) unit.declarations().get(0);
		AliasTemplateParameter param = (AliasTemplateParameter) agg.templateParameters().get(0);
		param.getDefaultType().delete();
		
		assertEqualsTokenByToken("class C (alias X) { }", end());
	}
	
	public void testMultiChange() throws Exception {
		begin("class C (alias X) { }");
		
		AggregateDeclaration agg = (AggregateDeclaration) unit.declarations().get(0);
		AliasTemplateParameter param = (AliasTemplateParameter) agg.templateParameters().get(0);
		param.setName(ast.newSimpleName("T"));
		param.setSpecificType(ast.newSimpleType(ast.newSimpleName("Y")));
		param.setDefaultType(ast.newSimpleType(ast.newSimpleName("Z")));
		
		assertEqualsTokenByToken("class C (alias T : Y = Z) { }", end());
	}

}
