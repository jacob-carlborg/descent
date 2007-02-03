package descent.tests.rewrite;

import descent.core.dom.AggregateDeclaration;
import descent.core.dom.AliasTemplateParameter;

public class RewriteTemplateTest extends RewriteTest {
	
	public void testAliasTemplateParameterChangeName() throws Exception {
		begin("class C (alias X) { }");
		
		AggregateDeclaration agg = (AggregateDeclaration) unit.declarations().get(0);
		AliasTemplateParameter param = (AliasTemplateParameter) agg.templateParameters().get(0);
		
		rewriter.set(param, AliasTemplateParameter.NAME_PROPERTY, ast.newSimpleName("T"), null);
		
		assertEqualsTokenByToken("class C (alias T) { }", end());
	}
	
	public void testAliasTemplateParameterAddSpecificType() throws Exception {
		begin("class C (alias X) { }");
		
		AggregateDeclaration agg = (AggregateDeclaration) unit.declarations().get(0);
		AliasTemplateParameter param = (AliasTemplateParameter) agg.templateParameters().get(0);
		
		rewriter.set(param, AliasTemplateParameter.SPECIFIC_TYPE_PROPERTY, ast.newSimpleType(ast.newSimpleName("Y")), null);
		
		assertEqualsTokenByToken("class C (alias X : Y) { }", end());
	}
	
	public void testAliasTemplateParameterRemoveSpecificType() throws Exception {
		begin("class C (alias X : Y) { }");
		
		AggregateDeclaration agg = (AggregateDeclaration) unit.declarations().get(0);
		AliasTemplateParameter param = (AliasTemplateParameter) agg.templateParameters().get(0);
		
		rewriter.remove(param.getSpecificType(), null);
		
		assertEqualsTokenByToken("class C (alias X) { }", end());
	}
	
	public void testAliasTemplateParameterAddDefaultType() throws Exception {
		begin("class C (alias X) { }");
		
		AggregateDeclaration agg = (AggregateDeclaration) unit.declarations().get(0);
		AliasTemplateParameter param = (AliasTemplateParameter) agg.templateParameters().get(0);
		
		rewriter.set(param, AliasTemplateParameter.DEFAULT_TYPE_PROPERTY, ast.newSimpleType(ast.newSimpleName("Y")), null);
		
		assertEqualsTokenByToken("class C (alias X = Y) { }", end());
	}
	
	public void testAliasTemplateParameterRemoveDefaultType() throws Exception {
		begin("class C (alias X = Y) { }");
		
		AggregateDeclaration agg = (AggregateDeclaration) unit.declarations().get(0);
		AliasTemplateParameter param = (AliasTemplateParameter) agg.templateParameters().get(0);
		
		rewriter.remove(param.getDefaultType(), null);
		
		assertEqualsTokenByToken("class C (alias X) { }", end());
	}
	
	public void testMultiChange() throws Exception {
		begin("class C (alias X) { }");
		
		AggregateDeclaration agg = (AggregateDeclaration) unit.declarations().get(0);
		AliasTemplateParameter param = (AliasTemplateParameter) agg.templateParameters().get(0);
		rewriter.set(param, AliasTemplateParameter.NAME_PROPERTY, ast.newSimpleName("T"), null);
		
		rewriter.set(param, AliasTemplateParameter.SPECIFIC_TYPE_PROPERTY, ast.newSimpleType(ast.newSimpleName("Y")), null);
		
		rewriter.set(param, AliasTemplateParameter.DEFAULT_TYPE_PROPERTY, ast.newSimpleType(ast.newSimpleName("Z")), null);
		
		assertEqualsTokenByToken("class C (alias T : Y = Z) { }", end());
	}

}
