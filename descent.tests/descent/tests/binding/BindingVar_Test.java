package descent.tests.binding;

import descent.core.ICompilationUnit;
import descent.core.dom.AggregateDeclaration;
import descent.core.dom.AliasDeclaration;
import descent.core.dom.AliasDeclarationFragment;
import descent.core.dom.Assignment;
import descent.core.dom.CompilationUnit;
import descent.core.dom.EnumDeclaration;
import descent.core.dom.EnumMember;
import descent.core.dom.ExpressionStatement;
import descent.core.dom.FunctionDeclaration;
import descent.core.dom.ITypeBinding;
import descent.core.dom.IVariableBinding;
import descent.core.dom.QualifiedType;
import descent.core.dom.SimpleName;
import descent.core.dom.SimpleType;
import descent.core.dom.TypedefDeclaration;
import descent.core.dom.TypedefDeclarationFragment;
import descent.core.dom.VariableDeclaration;
import descent.core.dom.VariableDeclarationFragment;

public class BindingVar_Test extends AbstractBinding_Test {
	
	public void testTypeBindingForVar() throws Exception {
		CompilationUnit unit = createCU("test.d", "class Foo { } Foo f;");
		VariableDeclaration var = (VariableDeclaration) unit.declarations().get(1);
		VariableDeclarationFragment fragment = var.fragments().get(0);
		
		ITypeBinding typeBinding = (ITypeBinding) var.resolveBinding();
		assertNotNull(typeBinding);

		assertTrue(typeBinding.isClass());
		
		assertEquals(ITypeBinding.TYPE, typeBinding.getKind());
		assertEquals("C4test3Foo", typeBinding.getKey());
		assertEquals("Foo", typeBinding.getName());
		assertEquals(0, typeBinding.getDimension());
		assertEquals("test.Foo", typeBinding.getQualifiedName());
		assertEquals(true, typeBinding.isFromSource());
		
		assertEquals(lastCompilationUnit.getAllTypes()[0], typeBinding.getJavaElement());
		
		IVariableBinding varBinding = fragment.resolveBinding();
		assertNotNull(varBinding);
		assertEquals("f",varBinding.getName());
		assertEquals("Q4test1f", varBinding.getKey());
		
		assertEquals(getVariable(lastCompilationUnit, 0), varBinding.getJavaElement());
		
		assertSame(typeBinding, var.getType().resolveBinding());
		assertSame(varBinding, fragment.getName().resolveBinding());
	}
	
	public void testTypeBindingForVarInClass() throws Exception {
		CompilationUnit unit = createCU("test.d", "class Foo { Foo f; }");
		AggregateDeclaration agg = (AggregateDeclaration) unit.declarations().get(0);
		
		VariableDeclaration var = (VariableDeclaration) agg.declarations().get(0);
		VariableDeclarationFragment fragment = var.fragments().get(0);
		
		ITypeBinding typeBinding = (ITypeBinding) var.resolveBinding();
		assertNotNull(typeBinding);

		assertTrue(typeBinding.isClass());
		
		assertEquals(ITypeBinding.TYPE, typeBinding.getKind());
		assertEquals("C4test3Foo", typeBinding.getKey());
		assertEquals("Foo", typeBinding.getName());
		assertEquals(0, typeBinding.getDimension());
		assertEquals("test.Foo", typeBinding.getQualifiedName());
		assertEquals(true, typeBinding.isFromSource());
		
		assertEquals(lastCompilationUnit.getAllTypes()[0], typeBinding.getJavaElement());
		
		IVariableBinding varBinding = fragment.resolveBinding();
		assertNotNull(varBinding);
		assertEquals("f",varBinding.getName());
		assertEquals("Q4test3Foo1f", varBinding.getKey());
		
		assertEquals(lastCompilationUnit.getAllTypes()[0].getChildren()[0], varBinding.getJavaElement());
		
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
		assertEquals(0, typeBinding.getDimension());
		assertEquals("test.Foo", typeBinding.getQualifiedName());
		assertEquals(true, typeBinding.isFromSource());
		
		assertEquals(lastCompilationUnit.getAllTypes()[0], typeBinding.getJavaElement());
		
		IVariableBinding varBinding = member.resolveBinding();
		assertNotNull(varBinding);
		assertEquals("one", varBinding.getName());
		assertTrue(varBinding.isEnumConstant());
		
		assertEquals(lastCompilationUnit.getAllTypes()[0].getChildren()[0], varBinding.getJavaElement());
		
		assertSame(typeBinding, varBinding.getType());
		assertSame(varBinding, member.getName().resolveBinding());
	}
	
	public void testTypeBindingInFqnVar() throws Exception {
		ICompilationUnit imported = createCompilationUnit("imported.d", "class Foo { }");
		
		CompilationUnit unit = createCU("test.d", "import imported; imported.Foo f;");
		VariableDeclaration var = (VariableDeclaration) unit.declarations().get(1);
		QualifiedType qType = (QualifiedType) var.getType();
		
		ITypeBinding typeBinding = (ITypeBinding) qType.resolveBinding();
		assertNotNull(typeBinding);
		
		assertEquals(imported.getAllTypes()[0], typeBinding.getJavaElement());
		
		SimpleType type = (SimpleType) qType.getType();
		assertSame(typeBinding, type.resolveBinding());
		
		assertSame(typeBinding, type.getName().resolveBinding());
	}
	
	public void testReferenceToVarBinding() throws Exception {
		CompilationUnit unit = createCU("test.d", "int x; void foo() { x = 2; }");
		FunctionDeclaration func = (FunctionDeclaration) unit.declarations().get(1);
		ExpressionStatement statement = (ExpressionStatement) func.getBody().statements().get(0);
		Assignment assignment = (Assignment) statement.getExpression();
		SimpleName name = (SimpleName) assignment.getLeftHandSide();
		
		IVariableBinding varBinding = (IVariableBinding) name.resolveBinding(); 
		assertNotNull(varBinding);
		
		assertEquals(getVariable(lastCompilationUnit, 0), varBinding.getJavaElement());
	}
	
	public void testReferenceToExternalVarBinding() throws Exception {
		ICompilationUnit imported = createCompilationUnit("imported.d", "int x;");
		CompilationUnit unit = createCU("test.d", "import imported; void foo() { x = 2; }");
		FunctionDeclaration func = (FunctionDeclaration) unit.declarations().get(1);
		ExpressionStatement statement = (ExpressionStatement) func.getBody().statements().get(0);
		Assignment assignment = (Assignment) statement.getExpression();
		SimpleName name = (SimpleName) assignment.getLeftHandSide();
		
		IVariableBinding varBinding = (IVariableBinding) name.resolveBinding(); 
		assertNotNull(varBinding);
		
		assertEquals("Q8imported1x", varBinding.getKey());
		
		assertEquals(getVariable(imported, 0), varBinding.getJavaElement());
	}
	
	public void testTypeBindingForAlias() throws Exception {
		CompilationUnit unit = createCU("test.d", "class Foo { } alias Foo f;");
		AliasDeclaration var = (AliasDeclaration) unit.declarations().get(1);
		AliasDeclarationFragment fragment = var.fragments().get(0);
		
		ITypeBinding typeBinding = (ITypeBinding) var.resolveBinding();
		assertNotNull(typeBinding);

		assertTrue(typeBinding.isClass());
		
		assertEquals(ITypeBinding.TYPE, typeBinding.getKind());
		assertEquals("C4test3Foo", typeBinding.getKey());
		assertEquals("Foo", typeBinding.getName());
		assertEquals(0, typeBinding.getDimension());
		assertEquals("test.Foo", typeBinding.getQualifiedName());
		assertEquals(true, typeBinding.isFromSource());
		
		assertEquals(lastCompilationUnit.getAllTypes()[0], typeBinding.getJavaElement());
		
		IVariableBinding varBinding = fragment.resolveBinding();
		assertNotNull(varBinding);
		assertEquals("f",varBinding.getName());
		assertEquals("Q4test1f", varBinding.getKey());
		
		assertEquals(getVariable(lastCompilationUnit, 0), varBinding.getJavaElement());
		
		assertSame(typeBinding, var.getType().resolveBinding());
		assertSame(varBinding, fragment.getName().resolveBinding());
	}
	
	public void testTypeBindingForTypedef() throws Exception {
		CompilationUnit unit = createCU("test.d", "class Foo { } typedef Foo f;");
		TypedefDeclaration var = (TypedefDeclaration) unit.declarations().get(1);
		TypedefDeclarationFragment fragment = var.fragments().get(0);
		
		ITypeBinding typeBinding = (ITypeBinding) var.resolveBinding();
		assertNotNull(typeBinding);

		assertTrue(typeBinding.isClass());
		
		assertEquals(ITypeBinding.TYPE, typeBinding.getKind());
		assertEquals("C4test3Foo", typeBinding.getKey());
		assertEquals("Foo", typeBinding.getName());
		assertEquals(0, typeBinding.getDimension());
		assertEquals("test.Foo", typeBinding.getQualifiedName());
		assertEquals(true, typeBinding.isFromSource());
		
		assertEquals(lastCompilationUnit.getAllTypes()[0], typeBinding.getJavaElement());
		
		IVariableBinding varBinding = fragment.resolveBinding();
		assertNotNull(varBinding);
		assertEquals("f",varBinding.getName());
		assertEquals("Q4test1f", varBinding.getKey());
		
		assertEquals(getVariable(lastCompilationUnit, 0), varBinding.getJavaElement());
		
		assertSame(typeBinding, var.getType().resolveBinding());
		assertSame(varBinding, fragment.getName().resolveBinding());
	}

}
