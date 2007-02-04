package descent.tests.rewrite;

import descent.core.dom.AggregateDeclaration;
import descent.core.dom.BaseClass;
import descent.core.dom.Modifier.ModifierKeyword;

public class RewriteBaseClassTest extends AbstractRewriteTest {
	
	public void testAddModifier() throws Exception {
		begin("class One : Two");
		
		AggregateDeclaration agg = (AggregateDeclaration) unit.declarations().get(0);
		BaseClass base = agg.baseClasses().get(0);
		base.setModifier(ast.newModifier(ModifierKeyword.PRIVATE_KEYWORD));
		
		assertEqualsTokenByToken("class One : private Two", end());
	}
	
	public void testChangeModifier() throws Exception {
		begin("class One : private Two");
		
		AggregateDeclaration agg = (AggregateDeclaration) unit.declarations().get(0);
		BaseClass base = agg.baseClasses().get(0);
		base.setModifier(ast.newModifier(ModifierKeyword.PUBLIC_KEYWORD));
		
		assertEqualsTokenByToken("class One : public Two", end());
	}
	
	public void testRemoveModifier() throws Exception {
		begin("class One : private Two");
		
		AggregateDeclaration agg = (AggregateDeclaration) unit.declarations().get(0);
		BaseClass base = agg.baseClasses().get(0);
		base.getModifier().delete();
		
		assertEqualsTokenByToken("class One : Two", end());
	}
	
	public void testChangeType() throws Exception {
		begin("class One : private Two");
		
		AggregateDeclaration agg = (AggregateDeclaration) unit.declarations().get(0);
		BaseClass base = agg.baseClasses().get(0);
		base.setType(ast.newSimpleType(ast.newSimpleName("Three")));
		
		assertEqualsTokenByToken("class One : private Three", end());
	}
}
