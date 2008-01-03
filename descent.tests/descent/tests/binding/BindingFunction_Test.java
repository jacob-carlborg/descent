package descent.tests.binding;

import descent.core.dom.CompilationUnit;
import descent.core.dom.ExpressionStatement;
import descent.core.dom.FunctionDeclaration;
import descent.core.dom.IMethodBinding;
import descent.core.dom.ITypeBinding;
import descent.core.dom.NewExpression;

public class BindingFunction_Test extends AbstractBinding_Test {
	
	public void testFunctionBinding() throws Exception {
		CompilationUnit unit = createCU("test.d", "int foo(char x) { }");
		FunctionDeclaration func = (FunctionDeclaration) unit.declarations().get(0);
		IMethodBinding binding = func.resolveBinding();
		
		assertEquals("foo", binding.getName());
		assertEquals(getFunction(lastCompilationUnit, 0), binding.getJavaElement());
		
		ITypeBinding retType = binding.getReturnType();
		assertEquals("i", retType.getKey());
		
		ITypeBinding[] params = binding.getParameterTypes();
		assertEquals(1, params.length);
		assertEquals("a", params[0].getKey());
		
		assertSame(binding, func.getName().resolveBinding());
	}
	
	public void testCtorBinding() throws Exception {
		CompilationUnit unit = createCU("test.d", "class Foo { this(int x) { } } void foo() { new Foo(); }");
		FunctionDeclaration func = (FunctionDeclaration) unit.declarations().get(1);
		ExpressionStatement stm = (ExpressionStatement) func.getBody().statements().get(0);
		NewExpression exp = (NewExpression) stm.getExpression();
		IMethodBinding methodBinding = exp.resolveNewBinding();
		
		assertEquals("_ctor", methodBinding.getName());
		assertEquals(lastCompilationUnit.getAllTypes()[0].getChildren()[0], methodBinding.getJavaElement());
		
		ITypeBinding typeBinding = (ITypeBinding) exp.getType().resolveBinding();
		assertEquals(lastCompilationUnit.getAllTypes()[0], typeBinding.getJavaElement());
	}

}
