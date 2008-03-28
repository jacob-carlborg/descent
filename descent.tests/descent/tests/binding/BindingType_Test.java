package descent.tests.binding;

import descent.core.dom.AggregateDeclaration;
import descent.core.dom.CompilationUnit;
import descent.core.dom.DeclarationStatement;
import descent.core.dom.EnumDeclaration;
import descent.core.dom.FunctionDeclaration;
import descent.core.dom.ITypeBinding;
import descent.core.dom.IVariableBinding;
import descent.core.dom.VariableDeclaration;
import descent.internal.compiler.parser.ISignatureConstants;

public class BindingType_Test extends AbstractBinding_Test {
	
	private final static int CLASS = 1;
	private final static int STRUCT = 2;
	private final static int UNION = 3;
	private final static int INTERFACE = 4;
	
	private void assertTypeBinding(String keyword, char signatureStart, int type) throws Exception {
		CompilationUnit unit = createCU("test.d", keyword + " Foo { }");
		AggregateDeclaration agg = (AggregateDeclaration) unit.declarations().get(0);
		ITypeBinding binding = agg.resolveBinding();
		assertNotNull(binding);
		
		assertEquals(type == CLASS, binding.isClass());
		assertEquals(type == STRUCT, binding.isStruct());
		assertEquals(type == UNION, binding.isUnion());
		assertEquals(type == INTERFACE, binding.isInterface());
		
		assertEquals(ITypeBinding.TYPE, binding.getKind());
		assertEquals(MODULE + "4test" + signatureStart + "3Foo", binding.getKey());
		assertEquals("Foo", binding.getName());
		assertEquals(0, binding.getDimension());
		assertEquals("test.Foo", binding.getQualifiedName());
		assertEquals(true, binding.isFromSource());
		
		assertEquals(lastCompilationUnit.getAllTypes()[0], binding.getJavaElement());
		
		// The binding for the name of the class should be the same
		assertSame(binding, agg.getName().resolveBinding());
	}
	
	public void testTypeBindingInClassDeclaration() throws Exception {
		assertTypeBinding("class", ISignatureConstants.CLASS, CLASS);
	}
	
	public void testTypeBindingInStructDeclaration() throws Exception {
		assertTypeBinding("struct", ISignatureConstants.STRUCT, STRUCT);
	}

	public void testTypeBindingInUnionDeclaration() throws Exception {
		assertTypeBinding("union", ISignatureConstants.UNION, UNION);
	}
	

	public void testTypeBindingInInterfaceDeclaration() throws Exception {
		assertTypeBinding("interface", ISignatureConstants.INTERFACE, INTERFACE);
	}

	public void testTypeBindingInEnumDeclaration() throws Exception {
		CompilationUnit unit = createCU("test.d", "enum Foo { a }");
		EnumDeclaration agg = (EnumDeclaration) unit.declarations().get(0);
		ITypeBinding binding = agg.resolveBinding();
		assertNotNull(binding);

		assertTrue(binding.isEnum());
		
		assertEquals(ITypeBinding.TYPE, binding.getKind());
		assertEquals(MODULE + "4test" + ENUM + "3Foo", binding.getKey());
		assertEquals("Foo", binding.getName());
		assertEquals(0, binding.getDimension());
		assertEquals("test.Foo", binding.getQualifiedName());
		assertEquals(true, binding.isFromSource());
		
		assertEquals(lastCompilationUnit.getAllTypes()[0], binding.getJavaElement());
		
		// The binding for the name of the class should be the same
		assertSame(binding, agg.getName().resolveBinding());
	}
	
	public void testTypeTypeof() throws Exception {
		CompilationUnit unit = createCU("test.d", "enum Foo { a } void foo() { typeof(Foo) f; }");
		FunctionDeclaration func = (FunctionDeclaration) unit.declarations().get(1);
		DeclarationStatement stm = (DeclarationStatement) func.getBody().statements().get(0);
		VariableDeclaration var = (VariableDeclaration) stm.getDeclaration();
		
		IVariableBinding varBinding = (IVariableBinding) var.resolveBinding(); 
		ITypeBinding typeBinding = (ITypeBinding) varBinding.getType();
		assertEquals(lastCompilationUnit.getAllTypes()[0], typeBinding.getJavaElement());
	}

}
