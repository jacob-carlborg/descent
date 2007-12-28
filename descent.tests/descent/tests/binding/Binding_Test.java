package descent.tests.binding;

import descent.core.ICompilationUnit;
import descent.core.IType;
import descent.core.dom.AST;
import descent.core.dom.ASTParser;
import descent.core.dom.AggregateDeclaration;
import descent.core.dom.CompilationUnit;
import descent.core.dom.EnumDeclaration;
import descent.core.dom.ITypeBinding;
import descent.core.dom.IVariableBinding;
import descent.core.dom.VariableDeclaration;
import descent.core.dom.VariableDeclarationFragment;
import descent.tests.model.AbstractModelTest;

public class Binding_Test extends AbstractModelTest {
	
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
		assertEquals(0, binding.getDimensions());
		assertEquals("test.Foo", binding.getQualifiedName());
		assertEquals(true, binding.isFromSource());
		
		IType element = (IType) binding.getJavaElement();
		assertNotNull(element);
		
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
		assertEquals(0, binding.getDimensions());
		assertEquals("test.Foo", binding.getQualifiedName());
		assertEquals(true, binding.isFromSource());
		
		IType element = (IType) binding.getJavaElement();
		assertNotNull(element);
		
		// The binding for the name of the class should be the same
		assertSame(binding, agg.getName().resolveBinding());
	}
	
	public void testTypeBindingForClassInVar() throws Exception {
		CompilationUnit unit = createCU("test.d", "class Foo { } Foo f;");
		VariableDeclaration var = (VariableDeclaration) unit.declarations().get(1);
		ITypeBinding binding = var.resolveBinding();
		assertNotNull(binding);

		assertTrue(binding.isClass());
		
		assertEquals(ITypeBinding.TYPE, binding.getKind());
		assertEquals("C4test3Foo", binding.getKey());
		assertEquals("Foo", binding.getName());
		assertEquals(0, binding.getDimensions());
		assertEquals("test.Foo", binding.getQualifiedName());
		assertEquals(true, binding.isFromSource());
		
		IType element = (IType) binding.getJavaElement();
		assertNotNull(element);
		
		assertSame(binding, var.getType().resolveBinding());
	}
	
	public void testTypeBindingForClassInVar2() throws Exception {
		CompilationUnit unit = createCU("test.d", "class Foo { } Foo f;");
		VariableDeclaration var = ((VariableDeclaration) unit.declarations().get(1));
		VariableDeclarationFragment fragment = var.fragments().get(0);
		IVariableBinding varBinding = fragment.resolveBinding();
		assertNotNull(varBinding);
		
		assertEquals("f",varBinding.getName());
		
		ITypeBinding typeBinding = varBinding.getType();
		assertNotNull(typeBinding);

		assertTrue(typeBinding.isClass());
		
		assertEquals(ITypeBinding.TYPE, typeBinding.getKind());
		assertEquals("C4test3Foo", typeBinding.getKey());
		assertEquals("Foo", typeBinding.getName());
		assertEquals(0, typeBinding.getDimensions());
		assertEquals("test.Foo", typeBinding.getQualifiedName());
		assertEquals(true, typeBinding.isFromSource());
		
		IType element = (IType) typeBinding.getJavaElement();
		assertNotNull(element);
		
		assertSame(varBinding, fragment.getName().resolveBinding());
		assertSame(typeBinding, var.getType().resolveBinding());
	}
	
	protected CompilationUnit createCU(String filename, String source) throws Exception {
		ICompilationUnit unitElem = createCompilationUnit(filename, source);
		
		ASTParser parser = ASTParser.newParser(AST.D2);
		parser.setSource(unitElem);
		parser.setResolveBindings(true);
		return (CompilationUnit) parser.createAST(null);
	}

}
