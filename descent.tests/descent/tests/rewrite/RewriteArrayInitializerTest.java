package descent.tests.rewrite;

import descent.core.dom.ArrayInitializer;
import descent.core.dom.ArrayInitializerFragment;
import descent.core.dom.VariableDeclaration;

public class RewriteArrayInitializerTest extends RewriteTest {
	
	public void testAddFragment() throws Exception {
		begin("int[] x = [];");
		
		VariableDeclaration var = (VariableDeclaration) unit.declarations().get(0);
		ArrayInitializer init = (ArrayInitializer) var.fragments().get(0).getInitializer();
		
		ArrayInitializerFragment fragment1 = ast.newArrayInitializerFragment();
		fragment1.setInitializer(ast.newExpressionInitializer(ast.newSimpleName("a")));
		
		ArrayInitializerFragment fragment2 = ast.newArrayInitializerFragment();
		fragment2.setInitializer(ast.newExpressionInitializer(ast.newSimpleName("b")));
		
		init.fragments().add(fragment1);
		init.fragments().add(fragment2);
		
		assertEqualsTokenByToken("int[] x = [a, b];", end());
	}
	
	public void testChangeFragment() throws Exception {
		begin("int[] x = [a];");
		
		VariableDeclaration var = (VariableDeclaration) unit.declarations().get(0);
		ArrayInitializer init = (ArrayInitializer) var.fragments().get(0).getInitializer();
		
		ArrayInitializerFragment fragment1 = ast.newArrayInitializerFragment();
		fragment1.setInitializer(ast.newExpressionInitializer(ast.newSimpleName("b")));
		
		init.fragments().set(0, fragment1);
		
		assertEqualsTokenByToken("int[] x = [b];", end());
	}
	
	public void testRemoveFragments() throws Exception {
		begin("int[] x = [a, b];");
		
		VariableDeclaration var = (VariableDeclaration) unit.declarations().get(0);
		ArrayInitializer init = (ArrayInitializer) var.fragments().get(0).getInitializer();
		init.fragments().clear();
		
		assertEqualsTokenByToken("int[] x = [];", end());
	}
	
	public void testChangeFragmentInitializer() throws Exception {
		begin("int[] x = [a];");
		
		VariableDeclaration var = (VariableDeclaration) unit.declarations().get(0);
		ArrayInitializer init = (ArrayInitializer) var.fragments().get(0).getInitializer();
		init.fragments().get(0).setInitializer(ast.newExpressionInitializer(ast.newSimpleName("b")));
		
		assertEqualsTokenByToken("int[] x = [b];", end());
	}
	
	public void testAddFragmentInitializerExpression() throws Exception {
		begin("int[] x = [a];");
		
		VariableDeclaration var = (VariableDeclaration) unit.declarations().get(0);
		ArrayInitializer init = (ArrayInitializer) var.fragments().get(0).getInitializer();
		init.fragments().get(0).setExpression(ast.newSimpleName("x"));
		
		assertEqualsTokenByToken("int[] x = [x: a];", end());
	}
	
	public void testChangeFragmentInitializerExpression() throws Exception {
		begin("int[] x = [y: a];");
		
		VariableDeclaration var = (VariableDeclaration) unit.declarations().get(0);
		ArrayInitializer init = (ArrayInitializer) var.fragments().get(0).getInitializer();
		init.fragments().get(0).setExpression(ast.newSimpleName("x"));
		
		assertEqualsTokenByToken("int[] x = [x: a];", end());
	}
	
	public void testRemoveFragmentInitializerExpression() throws Exception {
		begin("int[] x = [x: a];");
		
		VariableDeclaration var = (VariableDeclaration) unit.declarations().get(0);
		ArrayInitializer init = (ArrayInitializer) var.fragments().get(0).getInitializer();
		init.fragments().get(0).getExpression().delete();
		
		assertEqualsTokenByToken("int[] x = [a];", end());
	}

}
