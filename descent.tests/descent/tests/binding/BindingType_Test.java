package descent.tests.binding;

import descent.core.dom.AggregateDeclaration;
import descent.core.dom.CompilationUnit;
import descent.core.dom.EnumDeclaration;
import descent.core.dom.ITypeBinding;

public class BindingType_Test extends AbstractBinding_Test {
	
	private final static int CLASS = 1;
	private final static int STRUCT = 2;
	private final static int UNION = 3;
	private final static int INTERFACE = 4;
	
	private void assertTypeBinding(String keyword, String signatureStart, int type) throws Exception {
		CompilationUnit unit = createCU("test.d", keyword + " Foo { }");
		AggregateDeclaration agg = (AggregateDeclaration) unit.declarations().get(0);
		ITypeBinding binding = agg.resolveBinding();
		assertNotNull(binding);
		
		assertEquals(type == CLASS, binding.isClass());
		assertEquals(type == STRUCT, binding.isStruct());
		assertEquals(type == UNION, binding.isUnion());
		assertEquals(type == INTERFACE, binding.isInterface());
		
		assertEquals(ITypeBinding.TYPE, binding.getKind());
		assertEquals(signatureStart + "4test3Foo", binding.getKey());
		assertEquals("Foo", binding.getName());
		assertEquals(0, binding.getDimension());
		assertEquals("test.Foo", binding.getQualifiedName());
		assertEquals(true, binding.isFromSource());
		
		assertEquals(lastCompilationUnit.getAllTypes()[0], binding.getJavaElement());
		
		// The binding for the name of the class should be the same
		assertSame(binding, agg.getName().resolveBinding());
	}
	
	public void testTypeBindingInClassDeclaration() throws Exception {
		assertTypeBinding("class", "C", CLASS);
	}
	
	public void testTypeBindingInStructDeclaration() throws Exception {
		assertTypeBinding("struct", "S", STRUCT);
	}

	public void testTypeBindingInUnionDeclaration() throws Exception {
		assertTypeBinding("union", "S", UNION);
	}
	

	public void testTypeBindingInInterfaceDeclaration() throws Exception {
		assertTypeBinding("interface", "C", INTERFACE);
	}

	public void testTypeBindingInEnumDeclaration() throws Exception {
		CompilationUnit unit = createCU("test.d", "enum Foo { a }");
		EnumDeclaration agg = (EnumDeclaration) unit.declarations().get(0);
		ITypeBinding binding = agg.resolveBinding();
		assertNotNull(binding);

		assertTrue(binding.isEnum());
		
		assertEquals(ITypeBinding.TYPE, binding.getKind());
		assertEquals("E4test3Foo", binding.getKey());
		assertEquals("Foo", binding.getName());
		assertEquals(0, binding.getDimension());
		assertEquals("test.Foo", binding.getQualifiedName());
		assertEquals(true, binding.isFromSource());
		
		assertEquals(lastCompilationUnit.getAllTypes()[0], binding.getJavaElement());
		
		// The binding for the name of the class should be the same
		assertSame(binding, agg.getName().resolveBinding());
	}

}
