package descent.tests.binding;

import descent.core.dom.AggregateDeclaration;
import descent.core.dom.CallExpression;
import descent.core.dom.CompilationUnit;
import descent.core.dom.ExpressionStatement;
import descent.core.dom.FunctionDeclaration;
import descent.core.dom.IMethodBinding;
import descent.core.dom.ITypeBinding;
import descent.core.dom.IVariableBinding;
import descent.core.dom.MixinDeclaration;
import descent.core.dom.TemplateDeclaration;
import descent.core.dom.TemplateType;
import descent.core.dom.TypeExpression;
import descent.core.dom.VariableDeclaration;
import descent.core.dom.VariableDeclarationFragment;

public class BindingTemplate_Test extends AbstractBinding_Test {
	
	public void testTypeBindingForTemplate() throws Exception {
		CompilationUnit unit = createCU("test.d", "template Foo() { }");
		TemplateDeclaration decl = (TemplateDeclaration) unit.declarations().get(0);
		ITypeBinding binding = decl.resolveBinding();
		assertTrue(binding.isTemplate());
		
		assertEquals(lastCompilationUnit.getAllTypes()[0], binding.getJavaElement());
		
		assertSame(binding, decl.getName().resolveBinding());
		assertSame(binding, decl.getName().resolveTypeBinding());
	}
	
	public void testTypeBindingForTemplateReference() throws Exception {
		CompilationUnit unit = createCU("test.d", "template Foo() { const char[] Foo = \"int x;\"; } mixin(Foo!());");
		MixinDeclaration decl = (MixinDeclaration) unit.declarations().get(1);
		TypeExpression exp = (TypeExpression) decl.getExpression();
		TemplateType type = (TemplateType) exp.getType();
		ITypeBinding binding = (ITypeBinding) type.resolveBinding();
		assertTrue(binding.isTemplate());
		
		assertEquals(lastCompilationUnit.getAllTypes()[0], binding.getJavaElement());
		
		assertSame(binding, type.getName().resolveBinding());
	}
	
	public void testTypeBindingForTemplatedClass() throws Exception {
		CompilationUnit unit = createCU("test.d", "class Foo() { }");
		AggregateDeclaration decl = (AggregateDeclaration) unit.declarations().get(0);
		ITypeBinding binding = decl.resolveBinding();
		assertTrue(binding.isClass());
		assertTrue(binding.isTemplate());
		
		assertEquals(lastCompilationUnit.getAllTypes()[0], binding.getJavaElement());
		
		assertSame(binding, decl.getName().resolveBinding());
		assertSame(binding, decl.getName().resolveTypeBinding());
	}
	
	public void testTypeBindingForTemplatedClassReference() throws Exception {
		CompilationUnit unit = createCU("test.d", "class Foo() { } Foo!() x;");
		VariableDeclaration decl = (VariableDeclaration) unit.declarations().get(1);
		
		IVariableBinding varBinding = (IVariableBinding) decl.resolveBinding();
		ITypeBinding binding = (ITypeBinding) varBinding.getType();
		assertTrue(binding.isClass());
		assertTrue(binding.isTemplate());
		
		assertEquals(lastCompilationUnit.getAllTypes()[0], binding.getJavaElement());
		
		VariableDeclarationFragment frag = decl.fragments().get(0);
		assertSame(binding, frag.resolveBinding().getType());
		assertSame(binding, frag.getName().resolveTypeBinding());
		assertSame(frag.resolveBinding(), frag.getName().resolveBinding());
	}
	
	public void testTypeBindingForTemplatedFunction() throws Exception {
		CompilationUnit unit = createCU("test.d", "void foo()() { }");
		FunctionDeclaration decl = (FunctionDeclaration) unit.declarations().get(0);
		IMethodBinding binding = decl.resolveBinding();
		assertTrue(binding.isTemplate());
		
		assertEquals(getFunction(lastCompilationUnit, 0), binding.getJavaElement());
		
		assertSame(binding, decl.getName().resolveBinding());
	}
	
	public void testTypeBindingForTemplatedFunctionReference() throws Exception {
		CompilationUnit unit = createCU("test.d", "void foo()() { } void main() { foo!()(); }");
		FunctionDeclaration decl = (FunctionDeclaration) unit.declarations().get(1);
		ExpressionStatement stm = (ExpressionStatement) decl.getBody().statements().get(0);
		CallExpression exp = (CallExpression) stm.getExpression();
		IMethodBinding binding = exp.resolveCallBinding();
		assertTrue(binding.isTemplate());
		
		assertEquals(getFunction(lastCompilationUnit, 0), binding.getJavaElement());
		
		TypeExpression typeExp = (TypeExpression) exp.getExpression();
		TemplateType templateType = (TemplateType) typeExp.getType();
		assertSame(binding, templateType.getName().resolveBinding());
	}

}
