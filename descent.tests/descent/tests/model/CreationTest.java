package descent.tests.model;

import descent.core.ICompilationUnit;
import descent.core.IField;
import descent.core.IImportDeclaration;
import descent.core.IInitializer;
import descent.core.IMethod;
import descent.core.IPackageDeclaration;
import descent.core.IType;
import descent.tests.utils.Util;

public class CreationTest extends AbstractModelTest {
	
	public void testPackageDeclaration() throws Exception {
		ICompilationUnit unit = createCompilationUnit("test.d", "");
		
		IPackageDeclaration pkg = unit.createPackageDeclaration("one.two.three", null);
		assertNotNull(pkg);
		
		assertEquals("one.two.three", pkg.getElementName());
		
		assertEqualsTokenByToken("module one.two.three;", unit.getSource());
	}
	
	public void testCreateImport() throws Exception {
		ICompilationUnit unit = createCompilationUnit("test.d", "");
		
		IImportDeclaration importDeclaration = unit.createImport("one.two.three", null, null);
		assertNotNull(importDeclaration);
		
		assertEquals("one.two.three", importDeclaration.getElementName());
		
		assertEqualsTokenByToken("import one.two.three;", unit.getSource());
	}
	
	public void testCreateType() throws Exception {
		ICompilationUnit unit = createCompilationUnit("test.d", "");
		
		IType type = unit.createType("class A { }", null, true, null);
		assertNotNull(type);
		
		assertEquals("A", type.getElementName());
		
		assertEqualsTokenByToken("class A { }", unit.getSource());
	}
	
	public void testCreateField() throws Exception {
		ICompilationUnit unit = createCompilationUnit("test.d", "class A { }");
		
		IType type = unit.getTypes()[0];
		IField field = type.createField("int x;", null, true, null);
		assertNotNull(field);
		
		assertEqualsTokenByToken("class A { int x; }", unit.getSource());
	}
	
	public void testCreateInitializer() throws Exception {
		ICompilationUnit unit = createCompilationUnit("test.d", "class A { }");
		
		IType type = unit.getTypes()[0];
		IInitializer initializer = type.createInitializer("static this() { }", null, null);
		assertNotNull(initializer);
		
		assertEqualsTokenByToken("class A { static this() { } }", unit.getSource());
	}
	
	public void testCreateMethod() throws Exception {
		ICompilationUnit unit = createCompilationUnit("test.d", "class A { }");
		
		IType type = unit.getTypes()[0];
		IMethod method = type.createMethod("void bla() { }", null, true, null);
		assertNotNull(method);
		
		assertEqualsTokenByToken("class A { void bla() { } }", unit.getSource());
	}
	
	public void testCreateInnerType() throws Exception {
		ICompilationUnit unit = createCompilationUnit("test.d", "class A { }");
		
		IType type = unit.getTypes()[0];
		IType inner = type.createType("class B { }", null, true, null);
		assertNotNull(inner);
		
		assertEqualsTokenByToken("class A { class B { } }", unit.getSource());
	}
	
	protected void assertEqualsTokenByToken(String document1, String document2) throws Exception {
		assertTrue(Util.equalsTokenByToken(document1, document2));
	}

}
