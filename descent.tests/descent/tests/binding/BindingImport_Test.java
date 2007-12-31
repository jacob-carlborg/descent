package descent.tests.binding;

import descent.core.ICompilationUnit;
import descent.core.dom.CompilationUnit;
import descent.core.dom.IPackageBinding;
import descent.core.dom.Import;
import descent.core.dom.ImportDeclaration;
import descent.core.dom.QualifiedName;
import descent.core.dom.SimpleName;

public class BindingImport_Test extends AbstractBinding_Test {
	
	public void testModuleBindingInImport() throws Exception {
		ICompilationUnit imported = createCompilationUnit("imported.d", "");
		
		CompilationUnit unit = createCU("test.d", "import imported;");
		ImportDeclaration importDeclaration = (ImportDeclaration) unit.declarations().get(0);
		Import imp = importDeclaration.imports().get(0);
		IPackageBinding binding = imp.resolveBinding();
		assertNotNull(binding);
		
		assertEquals(imported, binding.getJavaElement());
		
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
		
		assertEquals(imported, binding.getJavaElement());
		
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

}
