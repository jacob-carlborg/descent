package descent.tests.rewrite;

import descent.core.dom.AggregateDeclaration;

public class RewriteDDocCommentTest extends AbstractRewriteTest {
	
	public void testChangeDDoc() throws Exception {
		begin("/** hola */ class One : Two");
		
		AggregateDeclaration agg = (AggregateDeclaration) unit.declarations().get(0);
		agg.preDDocs().get(0).setText("/** chau! */");
		
		assertEqualsTokenByToken("/** chau! */ class One : Two", end());
	}
	
}
