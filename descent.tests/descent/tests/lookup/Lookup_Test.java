package descent.tests.lookup;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;

import descent.core.IClasspathEntry;
import descent.core.ICompilationUnit;
import descent.core.JavaCore;
import descent.tests.model.AbstractModelTest;

/*
 * Tests to check that the implementation of the resolved part of the
 * semantic analysis is working as expected.
 */
public class Lookup_Test extends AbstractModelTest {
	
	public void testDefinedNotOk() throws Exception {
		one("");
		two("Bar b;");
		assertErrors();
	}
	
	public void testClassDefinedOk() throws Exception {
		one("class Bar { }");
		two("Bar b;");
		assertNoErrors();
	}
	
	public void testClassMangle() throws Exception {
		one("class Bar { }");
		two("static assert(Bar.mangleof == \"C3one3Bar\");");
		assertNoErrors();
	}
	
	public void testInterfaceDefinedOk() throws Exception {
		one("interface Bar { a }");
		two("Bar b;");
		assertNoErrors();
	}
	
	public void testInterfaceMangle() throws Exception {
		one("interface Bar { }");
		two("static assert(Bar.mangleof == \"C3one3Bar\");");
		assertNoErrors();
	}
	
	public void testStructDefinedOk() throws Exception {
		one("struct Bar { }");
		two("Bar b;");
		assertNoErrors();
	}
	
	public void testStructMangle() throws Exception {
		one("struct Bar { }");
		two("static assert(Bar.mangleof == \"S3one3Bar\");");
		assertNoErrors();
	}
	
	public void testUnionDefinedOk() throws Exception {
		one("union Bar { a }");
		two("Bar b;");
		assertNoErrors();
	}
	
	public void testUnionMangle() throws Exception {
		one("union Bar { }");
		two("static assert(Bar.mangleof == \"S3one3Bar\");");
		assertNoErrors();
	}
	
	public void testEnumDefinedOk() throws Exception {
		one("enum Bar { a }");
		two("Bar b;");
		assertNoErrors();
	}
	
	public void testEnumMangle() throws Exception {
		one("enum Bar { a }");
		two("static assert(Bar.mangleof == \"i\");");
		assertNoErrors();
	}
	
	public void testEnumMangle2() throws Exception {
		one("enum Bar : char { a }");
		two("static assert(Bar.mangleof == \"a\");");
		assertNoErrors();
	}
	
	public void testClassInheritanceNotOk() throws Exception {
		one("class Bar { } class Foo { }");
		two("void x(Foo f) { Bar b = f; }");
		assertErrors();
	}
	
	public void testClassInheritanceOk() throws Exception {
		one("class Bar { } class Foo : Bar { }");
		two("void x(Foo f) { Bar b = f; }");
		assertNoErrors();
	}
	
	public void testInterfaceInheritanceOk() throws Exception {
		one("interface Bar { } class Foo : Bar { }");
		two("void x(Foo f) { Bar b = f; }");
		assertNoErrors();
	}
	
	public void testInterfaceInheritance2Ok() throws Exception {
		one("interface Bar { } interface Foo : Bar { }");
		two("void x(Foo f) { Bar b = f; }");
		assertNoErrors();
	}
	
	public void testEnumValueNotOk() throws Exception {
		one("enum Bar { a, b, c }");
		two("void x(Bar f) { f = Bar.d; }");
		assertErrors();
	}
	
	public void testEnumValueOk() throws Exception {
		one("enum Bar { a, b, c }");
		two("void x(Bar f) { f = Bar.a; }");
		assertNoErrors();
	}
	
	public void testEnumValueCT() throws Exception {
		one("enum Bar { a, b = 3, c }");
		two("static assert(Bar.b == 3);");
		assertNoErrors();
	}
	
	public void testEnumValue2() throws Exception {
		one("enum Bar { a, b, c }");
		two("static assert(Bar.b == 1);");
		assertNoErrors();
	}
	
	public void testFuncNotFound() throws Exception {
		one("");
		two("void foo() { bar(); }");
		assertErrors();
	}
	
	public void testFuncFound() throws Exception {
		one("void bar() { }");
		two("void foo() { bar(); }");
		assertNoErrors();
	}
	
	public void testFuncFound2() throws Exception {
		one("void bar(int x) { }");
		two("void foo() { bar(1); }");
		assertNoErrors();
	}
	
	public void testFuncOverloadFound2() throws Exception {
		one("void bar(int x) { } void bar(char[] x) { }");
		two("void foo() { bar(\"hey\"); }");
		assertNoErrors();
	}
	
	public void testClassMemberNotAccessible() throws Exception {
		one("class Bar { private int x; }");
		two("void foo(Bar b) { b.x = 2; }");
		assertErrors();
	}
	
	public void testStructMemberNotAccessible() throws Exception {
		one("struct Bar { private int x; }");
		two("void foo(Bar b) { b.x = 2; }");
		assertErrors();
	}
	
	public void testDeprecatedClass() throws Exception {
		one("deprecated class Bar { }");
		two("Bar b;");
		assertErrors();
	}
	
	public void testConstructor() throws Exception {
		one("class Bar { }");
		two("void foo() { Bar b = new Bar(); }");
		assertNoErrors();
	}
	
	public void testConstructor2() throws Exception {
		one("class Bar { this() { } }");
		two("void foo() { Bar b = new Bar(); }");
		assertNoErrors();
	}
	
	public void testConstructor3() throws Exception {
		one("class Bar { this(int x) { } }");
		two("void foo() { Bar b = new Bar(1); }");
		assertNoErrors();
	}
	
	public void testConstructorOverload() throws Exception {
		one("class Bar { this() { } this(int x) { } }");
		two("void foo() { Bar b = new Bar(1); }");
		assertNoErrors();
	}
	
	public void testAlias() throws Exception {
		one("alias int myInt;");
		two("myInt i;");
		assertNoErrors();
	}
	
	public void testAlias2() throws Exception {
		one("alias int myInt; void foo(myInt m) { }");
		two("void bar() { myInt i; foo(i); }");
		assertNoErrors();
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
		two = createCompilationUnit(
				"two.d", 
				"import one; " + contents);
		build();
		return two;
	}
	
	// Assertions on the precense of errors in "two.d"
	
	protected void assertNoErrors() throws CoreException {
		assertEquals(0, two.getResource().findMarkers(IMarker.PROBLEM, true, 0).length);
	}
	
	protected void assertErrors() throws CoreException {
		assertTrue(two.getResource().findMarkers(IMarker.PROBLEM, true, 0).length > 0);
	}

}
