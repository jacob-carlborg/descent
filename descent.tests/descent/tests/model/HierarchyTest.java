package descent.tests.model;

import descent.core.ICompilationUnit;
import descent.core.IField;
import descent.core.IImportContainer;
import descent.core.IImportDeclaration;
import descent.core.IInitializer;
import descent.core.IJavaElement;
import descent.core.IMethod;
import descent.core.IPackageDeclaration;
import descent.core.IType;

public class HierarchyTest extends AbstractModelTest {
	
	public void testPackageDeclaration() throws Exception {
		ICompilationUnit unit = createCompilationUnit("test.d", " module a;");
		
		IPackageDeclaration[] packages = unit.getPackageDeclarations();
		assertEquals(1, packages.length);
		
		assertEquals("a", packages[0].getElementName());
		assertEquals("module a;", packages[0].getSource());
		assertEquals(1, packages[0].getSourceRange().getOffset());
		assertEquals(9, packages[0].getSourceRange().getLength());
	}
	
	public void testImports() throws Exception {
		ICompilationUnit unit = createCompilationUnit("test.d", " import one;");
		
		IImportContainer container = unit.getImportContainer();
		assertNotNull(container);
		
		IJavaElement[] imports = container.getChildren();
		assertEquals(1, imports.length);
		
		IImportDeclaration imp = (IImportDeclaration) imports[0];		
		assertEquals("one", imp.getElementName());
		assertEquals("import one;", imp.getSource());
		assertEquals(1, imp.getSourceRange().getOffset());
		assertEquals(11, imp.getSourceRange().getLength());
	}
	
	public void testType() throws Exception {
		ICompilationUnit unit = createCompilationUnit("test.d", " /** hola */ class Clazz1 { }");
		
		IType[] types = unit.getTypes();
		assertEquals(1, types.length);
		
		IType type = types[0];
		assertTrue(type.isClass());
		assertFalse(type.isEnum());
		assertFalse(type.isInterface());
		assertFalse(type.isStruct());
		assertFalse(type.isUnion());
		assertFalse(type.isTemplate());
		assertEquals("Clazz1", type.getElementName());
		assertEquals(1, type.getSourceRange().getOffset());
		assertEquals(28, type.getSourceRange().getLength());
		assertEquals(19, type.getNameRange().getOffset());
		assertEquals(6, type.getNameRange().getLength());
		assertEquals("/** hola */ class Clazz1 { }", type.getSource());
		assertEquals(1, type.getJavadocRange().getOffset());
		assertEquals(11, type.getJavadocRange().getLength());
	}
	
	public void testTypeWithTwoComments() throws Exception {
		ICompilationUnit unit = createCompilationUnit("test.d", " /** hola */ /** chau */ class Clazz1 { }");
		
		IType[] types = unit.getTypes();
		assertEquals(1, types.length);
		
		IType type = types[0];
		assertEquals(1, type.getSourceRange().getOffset());
		assertEquals(40, type.getSourceRange().getLength());
		assertEquals(1, type.getJavadocRange().getOffset());
		assertEquals(23, type.getJavadocRange().getLength());
	}
	
	public void testConstructor() throws Exception {
		ICompilationUnit unit = createCompilationUnit("test.d", " class Clazz1 { /** hola */ this(int x) { } }");
		
		IType[] types = unit.getTypes();
		assertEquals(1, types.length);
		
		IType type = types[0];
		IMethod[] methods = type.getMethods();
		assertEquals(1, methods.length);
		
		IMethod method = methods[0];
		assertEquals(16, method.getSourceRange().getOffset());
		assertEquals(27, method.getSourceRange().getLength());
		assertEquals(16, method.getJavadocRange().getOffset());
		assertEquals(11, method.getJavadocRange().getLength());
		assertEquals(1, method.getNumberOfParameters());
		assertEquals(1, method.getParameterNames().length);
		assertEquals(1, method.getParameterTypes().length);
		assertEquals("x", method.getParameterNames()[0]);
		assertEquals("I", method.getParameterTypes()[0]);
		assertEquals("V", method.getReturnType());
		assertEquals(0, method.getTypeParameters().length);
	}
	
	public void testMethod() throws Exception {
		ICompilationUnit unit = createCompilationUnit("test.d", " class Clazz1 { /** hola */ void bla(T)(int x) { } }");
		
		IType[] types = unit.getTypes();
		assertEquals(1, types.length);
		
		IType type = types[0];
		IMethod[] methods = type.getMethods();
		assertEquals(1, methods.length);
		
		IMethod method = methods[0];
		assertEquals(16, method.getSourceRange().getOffset());
		assertEquals(34, method.getSourceRange().getLength());
		assertEquals(16, method.getJavadocRange().getOffset());
		assertEquals(11, method.getJavadocRange().getLength());
		assertEquals("bla", method.getElementName());
		assertEquals(33, method.getNameRange().getOffset());
		assertEquals(3, method.getNameRange().getLength());
		assertEquals(1, method.getNumberOfParameters());
		assertEquals(1, method.getParameterNames().length);
		assertEquals(1, method.getParameterTypes().length);
		assertEquals("x", method.getParameterNames()[0]);
		assertEquals("I", method.getParameterTypes()[0]);
		assertEquals("V", method.getReturnType());
		assertEquals(1, method.getTypeParameters().length);
		assertEquals("T", method.getTypeParameters()[0].getElementName());
		assertEquals(37, method.getTypeParameters()[0].getSourceRange().getOffset());
		assertEquals(1, method.getTypeParameters()[0].getSourceRange().getLength());
		assertEquals(37, method.getTypeParameters()[0].getNameRange().getOffset());
		assertEquals(1, method.getTypeParameters()[0].getNameRange().getLength());
	}
	
	public void testField() throws Exception {
		ICompilationUnit unit = createCompilationUnit("test.d", " class Clazz1 { /** hola */ int x; }");
		
		IType[] types = unit.getTypes();
		assertEquals(1, types.length);
		
		IType type = types[0];
		IField[] fields = type.getFields();
		assertEquals(1, fields.length);
		
		IField field = fields[0];
		assertTrue(field.isVariable());
		assertFalse(field.isEnumConstant());
		assertFalse(field.isAlias());
		assertFalse(field.isTypedef());
		assertFalse(field.isMixin());
		assertEquals(16, field.getSourceRange().getOffset());
		assertEquals(18, field.getSourceRange().getLength());
		assertEquals(16, field.getJavadocRange().getOffset());
		assertEquals(11, field.getJavadocRange().getLength());
		assertEquals("x", field.getElementName());
		assertEquals(32, field.getNameRange().getOffset());
		assertEquals(1, field.getNameRange().getLength());
	}
	
	public void testAlias() throws Exception {
		ICompilationUnit unit = createCompilationUnit("test.d", " class Clazz1 { /** hola */ alias int x; }");
		
		IType[] types = unit.getTypes();
		assertEquals(1, types.length);
		
		IType type = types[0];
		IField[] fields = type.getFields();
		assertEquals(1, fields.length);
		
		IField field = fields[0];
		assertFalse(field.isVariable());
		assertFalse(field.isEnumConstant());
		assertTrue(field.isAlias());
		assertFalse(field.isTypedef());
		assertFalse(field.isMixin());
		assertEquals(16, field.getSourceRange().getOffset());
		assertEquals(24, field.getSourceRange().getLength());
		assertEquals(16, field.getJavadocRange().getOffset());
		assertEquals(11, field.getJavadocRange().getLength());
		assertEquals("x", field.getElementName());
		assertEquals(38, field.getNameRange().getOffset());
		assertEquals(1, field.getNameRange().getLength());
	}
	
	public void testTypdef() throws Exception {
		ICompilationUnit unit = createCompilationUnit("test.d", " class Clazz1 { /** hola */ typedef int x; }");
		
		IType[] types = unit.getTypes();
		assertEquals(1, types.length);
		
		IType type = types[0];
		IField[] fields = type.getFields();
		assertEquals(1, fields.length);
		
		IField field = fields[0];
		assertFalse(field.isVariable());
		assertFalse(field.isEnumConstant());
		assertFalse(field.isAlias());
		assertTrue(field.isTypedef());
		assertFalse(field.isMixin());
		assertEquals(16, field.getSourceRange().getOffset());
		assertEquals(26, field.getSourceRange().getLength());
		assertEquals(16, field.getJavadocRange().getOffset());
		assertEquals(11, field.getJavadocRange().getLength());
		assertEquals("x", field.getElementName());
		assertEquals(40, field.getNameRange().getOffset());
		assertEquals(1, field.getNameRange().getLength());
	}
	
	public void testMixin() throws Exception {
		ICompilationUnit unit = createCompilationUnit("test.d", " class Clazz1 { /** hola */ mixin T!() x; }");
		
		IType[] types = unit.getTypes();
		assertEquals(1, types.length);
		
		IType type = types[0];
		IField[] fields = type.getFields();
		assertEquals(1, fields.length);
		
		IField field = fields[0];
		assertFalse(field.isVariable());
		assertFalse(field.isEnumConstant());
		assertFalse(field.isAlias());
		assertFalse(field.isTypedef());
		assertTrue(field.isMixin());
		assertEquals(16, field.getSourceRange().getOffset());
		assertEquals(25, field.getSourceRange().getLength());
		assertEquals(16, field.getJavadocRange().getOffset());
		assertEquals(11, field.getJavadocRange().getLength());
		assertEquals("x", field.getElementName());
		assertEquals(39, field.getNameRange().getOffset());
		assertEquals(1, field.getNameRange().getLength());
	}
	
	public void testInitializer() throws Exception {
		ICompilationUnit unit = createCompilationUnit("test.d", " class Clazz1 { /** hola */ static this() { } }");
		
		IType[] types = unit.getTypes();
		assertEquals(1, types.length);
		
		IType type = types[0];
		IInitializer[] initializers = type.getInitializers();
		assertEquals(1, initializers.length);
		
		IInitializer init = initializers[0];
		assertEquals(16, init.getSourceRange().getOffset());
		assertEquals(29, init.getSourceRange().getLength());
		assertEquals(16, init.getJavadocRange().getOffset());
		assertEquals(11, init.getJavadocRange().getLength());
		assertEquals("", init.getElementName());
		assertNull(init.getNameRange());
	}
	
	public void testEnum() throws Exception {
		ICompilationUnit unit = createCompilationUnit("test.d", " /** hola */ enum  Clazz1 { x }");
		
		IType[] types = unit.getTypes();
		assertEquals(1, types.length);
		
		IType type = types[0];
		assertFalse(type.isClass());
		assertTrue(type.isEnum());
		assertFalse(type.isInterface());
		assertFalse(type.isStruct());
		assertFalse(type.isUnion());
		assertFalse(type.isTemplate());
		assertEquals("Clazz1", type.getElementName());
		assertEquals(1, type.getSourceRange().getOffset());
		assertEquals(30, type.getSourceRange().getLength());
		assertEquals(19, type.getNameRange().getOffset());
		assertEquals(6, type.getNameRange().getLength());
		assertEquals("/** hola */ enum  Clazz1 { x }", type.getSource());
		assertEquals(1, type.getJavadocRange().getOffset());
		assertEquals(11, type.getJavadocRange().getLength());
		
		IField[] fields = type.getFields();
		assertEquals(1, fields.length);
		
		IField field = fields[0];
		assertFalse(field.isVariable());
		assertTrue(field.isEnumConstant());
		assertFalse(field.isAlias());
		assertFalse(field.isTypedef());
		assertEquals("x", field.getElementName());
		assertEquals("x", field.getSource());
		assertEquals(28, field.getSourceRange().getOffset());
		assertEquals(1, field.getSourceRange().getLength());
	}
	
	public void testInterface() throws Exception {
		ICompilationUnit unit = createCompilationUnit("test.d", " /** hola */ interface Clazz1 { }");
		
		IType[] types = unit.getTypes();
		assertEquals(1, types.length);
		
		IType type = types[0];
		assertFalse(type.isClass());
		assertFalse(type.isEnum());
		assertTrue(type.isInterface());
		assertFalse(type.isStruct());
		assertFalse(type.isUnion());
		assertFalse(type.isTemplate());
		assertEquals("Clazz1", type.getElementName());
		assertEquals(1, type.getSourceRange().getOffset());
		assertEquals(32, type.getSourceRange().getLength());
		assertEquals(23, type.getNameRange().getOffset());
		assertEquals(6, type.getNameRange().getLength());
		assertEquals("/** hola */ interface Clazz1 { }", type.getSource());
		assertEquals(1, type.getJavadocRange().getOffset());
		assertEquals(11, type.getJavadocRange().getLength());
	}
	
	public void testStruct() throws Exception {
		ICompilationUnit unit = createCompilationUnit("test.d", " /** hola */ struct Clazz1 { }");
		
		IType[] types = unit.getTypes();
		assertEquals(1, types.length);
		
		IType type = types[0];
		assertFalse(type.isClass());
		assertFalse(type.isEnum());
		assertFalse(type.isInterface());
		assertTrue(type.isStruct());
		assertFalse(type.isUnion());
		assertFalse(type.isTemplate());
		assertEquals("Clazz1", type.getElementName());
		assertEquals(1, type.getSourceRange().getOffset());
		assertEquals(29, type.getSourceRange().getLength());
		assertEquals(20, type.getNameRange().getOffset());
		assertEquals(6, type.getNameRange().getLength());
		assertEquals("/** hola */ struct Clazz1 { }", type.getSource());
		assertEquals(1, type.getJavadocRange().getOffset());
		assertEquals(11, type.getJavadocRange().getLength());
	}
	
	public void testUnion() throws Exception {
		ICompilationUnit unit = createCompilationUnit("test.d", " /** hola */ union Clazz1 { }");
		
		IType[] types = unit.getTypes();
		assertEquals(1, types.length);
		
		IType type = types[0];
		assertFalse(type.isClass());
		assertFalse(type.isEnum());
		assertFalse(type.isInterface());
		assertFalse(type.isStruct());
		assertTrue(type.isUnion());
		assertFalse(type.isTemplate());
		assertEquals("Clazz1", type.getElementName());
		assertEquals(1, type.getSourceRange().getOffset());
		assertEquals(28, type.getSourceRange().getLength());
		assertEquals(19, type.getNameRange().getOffset());
		assertEquals(6, type.getNameRange().getLength());
		assertEquals("/** hola */ union Clazz1 { }", type.getSource());
		assertEquals(1, type.getJavadocRange().getOffset());
		assertEquals(11, type.getJavadocRange().getLength());
	}
	
	public void testTemplate() throws Exception {
		ICompilationUnit unit = createCompilationUnit("test.d", " /** hola */ template Clazz1() { }");
		
		IType[] types = unit.getTypes();
		assertEquals(1, types.length);
		
		IType type = types[0];
		assertFalse(type.isClass());
		assertFalse(type.isEnum());
		assertFalse(type.isInterface());
		assertFalse(type.isStruct());
		assertFalse(type.isUnion());
		assertTrue(type.isTemplate());
		assertEquals("Clazz1", type.getElementName());
		assertEquals(1, type.getSourceRange().getOffset());
		assertEquals(33, type.getSourceRange().getLength());
		assertEquals(22, type.getNameRange().getOffset());
		assertEquals(6, type.getNameRange().getLength());
		assertEquals("/** hola */ template Clazz1() { }", type.getSource());
		assertEquals(1, type.getJavadocRange().getOffset());
		assertEquals(11, type.getJavadocRange().getLength());
	}	

}
