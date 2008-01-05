package descent.tests.binding;

import descent.core.dom.AggregateDeclaration;
import descent.core.dom.CompilationUnit;
import descent.core.dom.DeclarationStatement;
import descent.core.dom.FunctionDeclaration;
import descent.core.dom.IMethodBinding;
import descent.core.dom.ITypeBinding;

public class BindingLocalVar_Test extends AbstractBinding_Test {
	
	public void testLocalClassBinding() throws Exception {
		CompilationUnit unit = createCU("test.d", "void foo() { class Bar { } }");
		FunctionDeclaration func = (FunctionDeclaration) unit.declarations().get(0);
		DeclarationStatement statement = (DeclarationStatement) func.getBody().statements().get(0);
		AggregateDeclaration agg = (AggregateDeclaration) statement.getDeclaration();
		
		ITypeBinding typeBinding = (ITypeBinding) agg.resolveBinding();
		assertTrue(typeBinding.isClass());
		assertEquals("Bar", typeBinding.getName());
		
		assertSame(typeBinding, agg.getName().resolveBinding());
	}
	
	public void testLocalClassBinding2() throws Exception {
		CompilationUnit unit = createCU("test.d", "void foo() { void bar() { class Bar { } } }");
		FunctionDeclaration func = (FunctionDeclaration) unit.declarations().get(0);
		DeclarationStatement statement = (DeclarationStatement) func.getBody().statements().get(0);
		
		FunctionDeclaration func2 = (FunctionDeclaration) statement.getDeclaration();
		DeclarationStatement statement2 = (DeclarationStatement) func2.getBody().statements().get(0);
		
		IMethodBinding funcBinding = func2.resolveBinding();
		assertEquals("bar", funcBinding.getName());
		
		AggregateDeclaration agg = (AggregateDeclaration) statement2.getDeclaration();
		
		ITypeBinding typeBinding = (ITypeBinding) agg.resolveBinding();
		assertTrue(typeBinding.isClass());
		assertEquals("Bar", typeBinding.getName());
		
		assertSame(typeBinding, agg.getName().resolveBinding());
	}

}
