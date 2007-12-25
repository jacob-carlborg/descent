package descent.tests.dstress2;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;

import descent.core.IClasspathEntry;
import descent.core.ICompilationUnit;
import descent.core.JavaCore;
import descent.tests.model.AbstractModelTest;

public class Dstress2_Test extends AbstractModelTest {
	
	public void testDefinedNotOk() throws Exception {
		one("");
		two("import one; Bar b;");		
		build();
		assertTwoErrors(3);
//		two.d(1): Error: identifier 'Bar' is not defined
//		two.d(1): Error: Bar is used as a type
//		two.d(1): variable two.b voids have no value
	}
	
	public void testClassDefinedOk() throws Exception {
		one("class Bar { }");
		two("import one; Bar b;");
		build();
		assertTwoErrors(0);
	}
	
	public void testInterfaceDefinedOk() throws Exception {
		one("interface Bar { a }");
		two("import one; Bar b;");
		build();
		assertTwoErrors(0);
	}
	
	public void testStructDefinedOk() throws Exception {
		one("struct Bar { }");
		two("import one; Bar b;");
		build();
		assertTwoErrors(0);
	}
	
	public void testUnionDefinedOk() throws Exception {
		one("union Bar { a }");
		two("import one; Bar b;");
		build();
		assertTwoErrors(0);
	}
	
	public void testEnumDefinedOk() throws Exception {
		one("enum Bar { a }");
		two("import one; Bar b;");
		build();
		assertTwoErrors(0);
	}
	
	public void testClassInheritanceNotOk() throws Exception {
		one("class Bar { } class Foo { }");
		two("import one; void x(Foo f) { Bar b = f; }");
		build();
		assertTwoErrors(1);
//		two.d(1): Error: cannot implicitly convert expression (f) of type one.Foo to one .Bar
	}
	
	public void testClassInheritanceOk() throws Exception {
		one("class Bar { } class Foo : Bar { }");
		two("import one; void x(Foo f) { Bar b = f; }");
		build();
		assertTwoErrors(0);
	}
	
	public void testInterfaceInheritanceOk() throws Exception {
		one("interface Bar { } class Foo : Bar { }");
		two("import one; void x(Foo f) { Bar b = f; }");
		build();
		assertTwoErrors(0);
	}
	
	public void testInterfaceInheritance2Ok() throws Exception {
		one("interface Bar { } interface Foo : Bar { }");
		two("import one; void x(Foo f) { Bar b = f; }");
		build();
		assertTwoErrors(0);
	}
	
	public void testEnumValueNotOk() throws Exception {
		one("enum Bar { a, b, c }");
		two("import one; void x(Bar f) { f = Bar.d; }");
		build();
		assertTwoErrors(2);
//		two.d(1): Error: no property 'd' for type 'int'
//		two.d(1): Error: cannot implicitly convert expression (1) of type int to Bar
	}
	
	public void testEnumValueOk() throws Exception {
		one("enum Bar { a, b, c }");
		two("import one; void x(Bar f) { f = Bar.a; }");
		build();
		assertTwoErrors(0);
	}
	
	protected ICompilationUnit one;
	protected ICompilationUnit two;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		javaProject.open(null);
		
		IClasspathEntry[] oldEntries = javaProject.getRawClasspath();		
		IClasspathEntry entry = JavaCore.newLibraryEntry(
				new Path("c:\\ary\\programacion\\d\\1.020\\dmd\\src\\phobos"), 
				null, null);
		
		IClasspathEntry[] newEntries = new IClasspathEntry[oldEntries.length + 1];
		System.arraycopy(oldEntries, 0, newEntries, 0, oldEntries.length);
		newEntries[oldEntries.length] = entry;
		
		javaProject.setRawClasspath(newEntries, null);
	}
	
	protected ICompilationUnit one(String contents) throws Exception {
		return one = createCompilationUnit(
				"one.d", 
				contents);
	}
	
	protected ICompilationUnit two(String contents) throws Exception {
		return two = createCompilationUnit(
				"two.d", 
				contents);
	}
	
	protected void assertTwoErrors(int num) throws CoreException {
		assertEquals(num, two.getResource().findMarkers(IMarker.PROBLEM, true, 0).length);
	}

}
