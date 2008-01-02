package descent.tests.select;

import descent.core.ICompilationUnit;
import descent.core.IJavaElement;
import descent.tests.model.AbstractModelTest;

public class CodeSelectModule_Test extends AbstractModelTest {
	
	public void testSelectReferenceToModule() throws Exception {
		ICompilationUnit other = createCompilationUnit("other.d", "");
		ICompilationUnit test = createCompilationUnit("test.d", "import other;");
		
		IJavaElement[] elements = test.codeSelect(8, 0);
		assertEquals(1, elements.length);
		
		assertEquals(other, elements[0]);
	}
	
	public void testSelectReferenceToModule2() throws Exception {
		ICompilationUnit other = createCompilationUnit("pack", "other.d", "");
		ICompilationUnit test = createCompilationUnit("test.d", "import pack.other;");
		
		IJavaElement[] elements = test.codeSelect(13, 0); // other
		assertEquals(1, elements.length);
		
		assertEquals(other, elements[0]);
		
		elements = test.codeSelect(8, 0); // pack
		assertEquals(0, elements.length);
	}
	
	public void testSelectReferenceToModule3() throws Exception {
		ICompilationUnit other = createCompilationUnit("", "other.d", "class Foo { }");
		ICompilationUnit test = createCompilationUnit("test.d", "import other; other.Foo f;");
		
		IJavaElement[] elements = test.codeSelect(15, 0); // other
		assertEquals(1, elements.length);
		
		assertEquals(other, elements[0]);
	}

}
