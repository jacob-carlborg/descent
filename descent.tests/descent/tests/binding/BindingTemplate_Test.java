package descent.tests.binding;

import descent.core.dom.CompilationUnit;
import descent.core.dom.ITypeBinding;
import descent.core.dom.MixinDeclaration;
import descent.core.dom.TemplateDeclaration;
import descent.core.dom.TemplateType;
import descent.core.dom.TypeExpression;

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

}
