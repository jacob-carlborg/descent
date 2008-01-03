package descent.tests.lookup;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;

import descent.core.ICompilationUnit;
import descent.tests.model.AbstractModelTest;

public abstract class AbstractLookupTest extends AbstractModelTest {
	
	protected ICompilationUnit one;
	protected ICompilationUnit two;
	protected ICompilationUnit three;
	
	protected ICompilationUnit one(String contents) throws Exception {
		return one = createCompilationUnit(
				"one.d", 
				contents);
	}
	
	protected ICompilationUnit three(String contents) throws Exception {
		return three = createCompilationUnit(
				"three.d", 
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
