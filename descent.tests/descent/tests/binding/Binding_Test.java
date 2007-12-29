package descent.tests.binding;

import descent.core.ICompilationUnit;
import descent.core.IType;
import descent.core.dom.AST;
import descent.core.dom.ASTParser;
import descent.core.dom.AggregateDeclaration;
import descent.core.dom.CompilationUnit;
import descent.core.dom.EnumDeclaration;
import descent.core.dom.EnumMember;
import descent.core.dom.IPackageBinding;
import descent.core.dom.ITypeBinding;
import descent.core.dom.IVariableBinding;
import descent.core.dom.Import;
import descent.core.dom.ImportDeclaration;
import descent.core.dom.QualifiedName;
import descent.core.dom.QualifiedType;
import descent.core.dom.SimpleName;
import descent.core.dom.SimpleType;
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
	
	public void testTypeBindingForVar() throws Exception {
		CompilationUnit unit = createCU("test.d", "class Foo { } Foo f;");
		VariableDeclaration var = (VariableDeclaration) unit.declarations().get(1);
		VariableDeclarationFragment fragment = var.fragments().get(0);
		
		ITypeBinding typeBinding = var.resolveBinding();
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
		
		IVariableBinding varBinding = fragment.resolveBinding();
		assertNotNull(varBinding);
		assertEquals("f",varBinding.getName());
		assertEquals("Q4test1f", varBinding.getKey());
		
		assertSame(typeBinding, var.getType().resolveBinding());
		assertSame(varBinding, fragment.getName().resolveBinding());
	}
	
	public void testTypeBindingForVarInClass() throws Exception {
		CompilationUnit unit = createCU("test.d", "class Foo { Foo f; }");
		AggregateDeclaration agg = (AggregateDeclaration) unit.declarations().get(0);
		
		VariableDeclaration var = (VariableDeclaration) agg.declarations().get(0);
		VariableDeclarationFragment fragment = var.fragments().get(0);
		
		ITypeBinding typeBinding = var.resolveBinding();
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
		
		IVariableBinding varBinding = fragment.resolveBinding();
		assertNotNull(varBinding);
		assertEquals("f",varBinding.getName());
		assertEquals("Q4test3Foo1f", varBinding.getKey());
		
		assertSame(typeBinding, var.getType().resolveBinding());
		assertSame(varBinding, fragment.getName().resolveBinding());
	}
	
	public void testVarBindingForEnumConstant() throws Exception {
		CompilationUnit unit = createCU("test.d", "enum Foo { one }");
		
		EnumDeclaration e = (EnumDeclaration) unit.declarations().get(0);
		EnumMember member = e.enumMembers().get(0);
		
		ITypeBinding typeBinding = e.resolveBinding();
		assertNotNull(typeBinding);

		assertTrue(typeBinding.isEnum());
		
		assertEquals(ITypeBinding.TYPE, typeBinding.getKind());
		assertEquals("E4test3Foo", typeBinding.getKey());
		assertEquals("Foo", typeBinding.getName());
		assertEquals(0, typeBinding.getDimensions());
		assertEquals("test.Foo", typeBinding.getQualifiedName());
		assertEquals(true, typeBinding.isFromSource());
		
		IType element = (IType) typeBinding.getJavaElement();
		assertNotNull(element);
		
		IVariableBinding varBinding = member.resolveBinding();
		assertNotNull(varBinding);
		assertEquals("one", varBinding.getName());
		assertTrue(varBinding.isEnumConstant());
		
		assertSame(typeBinding, varBinding.getType());
		assertSame(varBinding, member.getName().resolveBinding());
	}
	
	public void testTypeBindingInFqnVar() throws Exception {
		createCU("imported.d", "class Foo { }");
		
		CompilationUnit unit = createCU("test.d", "import imported; imported.Foo f;");
		VariableDeclaration var = (VariableDeclaration) unit.declarations().get(1);
		QualifiedType qType = (QualifiedType) var.getType();
		
		ITypeBinding typeBinding = qType.resolveBinding();
		assertNotNull(typeBinding);
		
		SimpleType type = (SimpleType) qType.getType();
		assertSame(typeBinding, type.resolveBinding());
		
		assertSame(typeBinding, type.getName().resolveBinding());
	}
	
	public void testModuleBindingInImport() throws Exception {
		ICompilationUnit imported = createCompilationUnit("imported.d", "");
		
		CompilationUnit unit = createCU("test.d", "import imported;");
		ImportDeclaration importDeclaration = (ImportDeclaration) unit.declarations().get(0);
		Import imp = importDeclaration.imports().get(0);
		IPackageBinding binding = imp.resolveBinding();
		assertNotNull(binding);
		
		assertEquals("8imported", binding.getKey());
		assertEquals(imported, binding.getJavaElement());
		assertEquals("imported", binding.getName());
		assertEquals(1, binding.getNameComponents().length);
		assertEquals("imported", binding.getNameComponents()[0]);
		
		assertSame(binding, imp.getName().resolveBinding());
	}
	
	public void testModuleBindingInImport2() throws Exception {
		ICompilationUnit imported = createCompilationUnit("pack", "imported.d", "");
		
		CompilationUnit unit = createCU("test.d", "import pack.imported;");
		ImportDeclaration importDeclaration = (ImportDeclaration) unit.declarations().get(0);
		Import imp = importDeclaration.imports().get(0);
		IPackageBinding binding = imp.resolveBinding();
		assertNotNull(binding);
		
		assertEquals("4pack8imported", binding.getKey());
		assertEquals(imported, binding.getJavaElement());
		assertEquals("pack.imported", binding.getName());
		assertEquals(2, binding.getNameComponents().length);
		assertEquals("pack", binding.getNameComponents()[0]);
		assertEquals("imported", binding.getNameComponents()[1]);
		
		QualifiedName qName = (QualifiedName) imp.getName();
		assertSame(binding, qName.resolveBinding());
		
		SimpleName qualifier = (SimpleName) qName.getQualifier();
		assertNull(qualifier.resolveBinding());
		
		assertSame(binding, qName.getName().resolveBinding());
	}
	
	protected CompilationUnit createCU(String filename, String source) throws Exception {
		ICompilationUnit unitElem = createCompilationUnit(filename, source);
		
		ASTParser parser = ASTParser.newParser(AST.D2);
		parser.setSource(unitElem);
		parser.setResolveBindings(true);
		return (CompilationUnit) parser.createAST(null);
	}

}
