package descent.tests.binding;

import descent.core.dom.CompilationUnit;
import descent.core.dom.EnumDeclaration;
import descent.core.dom.EnumMember;
import descent.core.dom.ITypeBinding;
import descent.core.dom.IVariableBinding;
import descent.internal.compiler.parser.ISignatureConstants;

public class BindingEnum_Test extends AbstractBinding_Test {
	
	public void testVarBindingForEnumConstant() throws Exception {
		CompilationUnit unit = createCU("test.d", "enum Foo { one }");
		
		EnumDeclaration e = (EnumDeclaration) unit.declarations().get(0);
		EnumMember member = e.enumMembers().get(0);
		
		ITypeBinding typeBinding = e.resolveBinding();
		assertNotNull(typeBinding);

		assertTrue(typeBinding.isEnum());
		
		assertEquals(ITypeBinding.TYPE, typeBinding.getKind());
		assertEquals(MODULE + "4test" + ENUM + "3Foo", typeBinding.getKey());
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

}
