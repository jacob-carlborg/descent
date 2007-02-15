package descent.tests.model;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

import descent.core.ICompilationUnit;
import descent.core.IField;
import descent.core.IImportContainer;
import descent.core.IImportDeclaration;
import descent.core.IInitializer;
import descent.core.IJavaElement;
import descent.core.IJavaProject;
import descent.core.IMethod;
import descent.core.IPackageDeclaration;
import descent.core.IPackageFragment;
import descent.core.IPackageFragmentRoot;
import descent.core.IType;
import descent.core.JavaCore;

public class ModelTest extends TestCase {
	
	private IProject project;
	
	@Override
	protected void setUp() throws Exception {
		project = createProject("D");
	}
	
	@Override
	protected void tearDown() throws Exception {
		project.close(null);
		project.delete(true, null);
	}
	
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
		assertEquals(16, field.getSourceRange().getOffset());
		assertEquals(18, field.getSourceRange().getLength());
		assertEquals(16, field.getJavadocRange().getOffset());
		assertEquals(11, field.getJavadocRange().getLength());
		assertEquals("x", field.getElementName());
		assertEquals(32, field.getNameRange().getOffset());
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
	
	private IProject createProject(String name) throws CoreException {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();		
		IProject project = root.getProject(name);		
		IProjectDescription description = workspace.newProjectDescription(name);		
		project.create(description, null);
		
		if (!project.isOpen()) {
			project.open(null);
		}
		
		description = project.getDescription();
		
		String[] natures = description.getNatureIds();
		String[] newNatures = new String[natures.length + 1];
		System.arraycopy(natures, 0, newNatures, 0, natures.length);
		newNatures[natures.length] = JavaCore.NATURE_ID;
		
		description.setNatureIds(newNatures);
		project.setDescription(description, null);
		
		return project;
	}
	
	private ICompilationUnit createCompilationUnit(String filename, String contents) throws Exception {
		IJavaProject javaProject = JavaCore.create(project);
		assertNotNull(javaProject);
		
		assertFalse(javaProject.isOpen());
		
		javaProject.open(null);
		assertTrue(javaProject.isOpen());
		
		IPackageFragmentRoot[] roots = javaProject.getPackageFragmentRoots();
		assertEquals(1, roots.length);
		
		IPackageFragmentRoot root = roots[0];
		assertFalse(root.isOpen());
		
		root.open(null);
		assertTrue(root.isOpen());
		
		IJavaElement[] children = root.getChildren();
		assertEquals(1, children.length);
		
		IPackageFragment pack = (IPackageFragment) children[0];
		assertFalse(pack.isOpen());
		
		pack.open(null);
		assertTrue(pack.isOpen());
		
		ICompilationUnit unit = pack.createCompilationUnit(filename, contents, true, null);
		assertTrue(unit.exists());
		
		assertFalse(unit.isOpen());
		
		unit.open(null);
		assertTrue(unit.isOpen());
		
		return unit;
	}

}
