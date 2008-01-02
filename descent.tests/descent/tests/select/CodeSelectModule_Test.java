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
	
	public void testSelectReferenceToModule4() throws Exception {
		ICompilationUnit other = createCompilationUnit("one.two", "three.d", "class Foo { }");
		ICompilationUnit test = createCompilationUnit("test.d", "import one.two.three; one.two.three.Foo f;");
		
		IJavaElement[] elements;
		
		elements = test.codeSelect(31, 0);
		assertEquals(1, elements.length);
		
		assertEquals(other, elements[0]);
		
		elements = test.codeSelect(37, 0);
		assertEquals(1, elements.length);
		
		assertEquals(other.getAllTypes()[0], elements[0]);
		
		assertEquals(0, test.codeSelect(27, 0).length);
		assertEquals(0, test.codeSelect(23, 0).length);
	}
	
	public void testSelectReferenceToModule5() throws Exception {
		ICompilationUnit oneTwo = createCompilationUnit("one", "foo.d", "class Foo { }");
		createCompilationUnit("one.two", "three.d", "class Bar { }");
		ICompilationUnit test = createCompilationUnit("test.d", "import one.two.three; import one.foo; one.foo.Foo f;");
		
		IJavaElement[] elements;
		
		elements = test.codeSelect(47, 0);
		assertEquals(1, elements.length);
		assertEquals(oneTwo.getAllTypes()[0], elements[0]);
		
		assertEquals(0, test.codeSelect(39, 0).length);
		
		elements = test.codeSelect(43, 0);
		assertEquals(1, elements.length);
		assertEquals(oneTwo, elements[0]);
	}

}
