package descent.tests.rewrite;

import descent.core.dom.AssociativeArrayType;

public class RewriteTypeTest extends AbstractRewriteTest {
	
	public void testAssociativeArrayTypeChangeComponentType() throws Exception {
		AssociativeArrayType type = (AssociativeArrayType) beginType("a[b]");
		type.setComponentType(ast.newSimpleType(ast.newSimpleName("xxx")));
		assertTypeEqualsTokenByToken("xxx[b]", end());
	}
	
	public void testAssociativeArrayTypeChangeKeyType() throws Exception {
		AssociativeArrayType type = (AssociativeArrayType) beginType("a[b]");
		type.setKeyType(ast.newSimpleType(ast.newSimpleName("xxx")));
		assertTypeEqualsTokenByToken("a[xxx]", end());
	}

}
