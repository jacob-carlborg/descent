package descent.tests.dstress2;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.Path;

import descent.core.IClasspathEntry;
import descent.core.ICompilationUnit;
import descent.core.JavaCore;
import descent.tests.model.AbstractModelTest;

public class Dstress2_Test extends AbstractModelTest {
	
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
	
//	public void testNoProblem() throws Exception {
//		ICompilationUnit unit = createCompilationUnit(
//				"test.d", 
//				"import std.stdio;\n" +
//				"\n" +
//				"void main() {\n" +
//				"    writefln(\"Hola!\");\n" +
//				"}");
//		
//		assertEquals(0, unit.getResource().findMarkers(IMarker.PROBLEM, true, 0).length);
//	}
	
	public void testProblem() throws Exception {
		ICompilationUnit one = createCompilationUnit(
				"one.d", 
				"");
		
		ICompilationUnit two = createCompilationUnit(
				"two.d", 
				"import one; Bar b;");
		
		assertEquals(1, two.getResource().findMarkers(IMarker.PROBLEM, true, 0).length);
	}
	
	public void testNoProblem() throws Exception {
		ICompilationUnit one = createCompilationUnit(
				"one.d", 
				"class Bar { }");
		
		ICompilationUnit two = createCompilationUnit(
				"two.d", 
				"import one; Bar b;");
		
		assertEquals(0, two.getResource().findMarkers(IMarker.PROBLEM, true, 0).length);
	}

}
