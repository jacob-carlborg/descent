package descent.tests.rewrite;

import descent.core.dom.ArrayInitializer;
import descent.core.dom.ArrayInitializerFragment;

public class RewriteInitializerTest extends AbstractRewriteTest {
	
	public void testArrayInitializerAddFragment() throws Exception {
		ArrayInitializer init = (ArrayInitializer) beginInitializer("[]");
		
		ArrayInitializerFragment fragment1 = ast.newArrayInitializerFragment();
		fragment1.setInitializer(ast.newExpressionInitializer(ast.newSimpleName("a")));
		
		ArrayInitializerFragment fragment2 = ast.newArrayInitializerFragment();
		fragment2.setInitializer(ast.newExpressionInitializer(ast.newSimpleName("b")));
		
		init.fragments().add(fragment1);
		init.fragments().add(fragment2);
		
		assertInitializerEqualsTokenByToken("[a, b]", end());
	}
	
	public void testArrayInitializerChangeFragment() throws Exception {
		ArrayInitializer init = (ArrayInitializer) beginInitializer("[a]");
		
		ArrayInitializerFragment fragment1 = ast.newArrayInitializerFragment();
		fragment1.setInitializer(ast.newExpressionInitializer(ast.newSimpleName("b")));
		init.fragments().set(0, fragment1);
		
		assertInitializerEqualsTokenByToken("[b]", end());
	}
	
	public void testArrayInitializerRemoveFragments() throws Exception {
		ArrayInitializer init = (ArrayInitializer) beginInitializer("[a, b]");
		init.fragments().clear();
		assertInitializerEqualsTokenByToken("[]", end());
	}
	
	public void testArrayInitializerChangeFragmentInitializer() throws Exception {
		ArrayInitializer init = (ArrayInitializer) beginInitializer("[a]");
		init.fragments().get(0).setInitializer(ast.newExpressionInitializer(ast.newSimpleName("b")));
		assertInitializerEqualsTokenByToken("[b]", end());
	}
	
	public void testArrayInitializerAddFragmentInitializerExpression() throws Exception {
		ArrayInitializer init = (ArrayInitializer) beginInitializer("[a]");
		init.fragments().get(0).setExpression(ast.newSimpleName("x"));		
		assertInitializerEqualsTokenByToken("[x: a]", end());
	}
	
	public void testArrayInitializerChangeFragmentInitializerExpression() throws Exception {
		ArrayInitializer init = (ArrayInitializer) beginInitializer("[y: a]");
		init.fragments().get(0).setExpression(ast.newSimpleName("x"));		
		assertInitializerEqualsTokenByToken("[x: a]", end());
	}
	
	public void testArrayInitializerRemoveFragmentInitializerExpression() throws Exception {
		ArrayInitializer init = (ArrayInitializer) beginInitializer("[x: a]");
		init.fragments().get(0).getExpression().delete();		
		assertInitializerEqualsTokenByToken("[a]", end());
	}

}
