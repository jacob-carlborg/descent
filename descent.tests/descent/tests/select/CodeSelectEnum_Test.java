package descent.tests.select;

import descent.core.ICompilationUnit;
import descent.core.IJavaElement;
import descent.tests.model.AbstractModelTest;

public class CodeSelectEnum_Test extends AbstractModelTest {
	
	public void testMemberOfEnum() throws Exception {
		ICompilationUnit test = createCompilationUnit("test.d", "enum foo { one, two }");
		
		IJavaElement[] elements = test.codeSelect(12, 0);
		assertEquals(1, elements.length);
		
		assertEquals(test.getAllTypes()[0].getChildren()[0], elements[0]);
	}
	
	public void testReferenceToMemberOfEnum() throws Exception {
		ICompilationUnit test = createCompilationUnit("test.d", "enum foo { one, two } void bla() { auto x = foo.one; }");
		
		IJavaElement[] elements = test.codeSelect(49, 0);
		assertEquals(1, elements.length);
		
		assertEquals(test.getAllTypes()[0].getChildren()[0], elements[0]);
	}
	
	public void testReferenceToMemberOfEnum2() throws Exception {
		ICompilationUnit test = createCompilationUnit("test.d", "enum foo { one, two } void bla() { auto x = foo.one; }");
		
		IJavaElement[] elements = test.codeSelect(45, 0);
		assertEquals(1, elements.length);
		
		assertEquals(test.getAllTypes()[0], elements[0]);
	}
	
	public void testMemberOfAnonymousEnum() throws Exception {
		ICompilationUnit test = createCompilationUnit("test.d", "enum { one, two }");
		
		IJavaElement[] elements = test.codeSelect(8, 0);
		assertEquals(1, elements.length);
		
		assertEquals(test.getAllTypes()[0].getChildren()[0], elements[0]);
	}
	
	public void testReferenceToMemberOfAnonymousEnum() throws Exception {
		ICompilationUnit test = createCompilationUnit("test.d", "enum { one, two }  void bla() { auto x = one; }");
		
		IJavaElement[] elements = test.codeSelect(42, 0);
		assertEquals(1, elements.length);
		
		assertEquals(test.getAllTypes()[0].getChildren()[0], elements[0]);
	}

}
