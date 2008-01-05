package descent.tests.select;

import descent.core.ICompilationUnit;
import descent.core.IJavaElement;
import descent.tests.model.AbstractModelTest;

public class CodeSelectLocalSymbol_Test extends AbstractModelTest {
	
	public void testSelectClassInFunc() throws Exception {
		ICompilationUnit test = createCompilationUnit("test.d", "void m() { class Foo { } }");
		
		IJavaElement[] elements = test.codeSelect(18, 0);
		assertEquals(1, elements.length);
		
		assertEquals(getFunction(test, 0).getChildren()[0], elements[0]);
	}
	
	public void testSelectClassInFuncInFunc() throws Exception {
		ICompilationUnit test = createCompilationUnit("test.d", "void m() { void n() { class Foo { } } }");
		
		IJavaElement[] elements = test.codeSelect(16, 0);
		assertEquals(1, elements.length);
		
		assertEquals(getFunction(getFunction(test, 0), 0), elements[0]);
		
		elements = test.codeSelect(29, 0);
		assertEquals(1, elements.length);
		
		assertEquals(getFunction(getFunction(test, 0), 0).getChildren()[0], elements[0]);
	}

}
