package descent.tests.select;

import descent.core.ICompilationUnit;
import descent.core.IJavaElement;
import descent.tests.model.AbstractModelTest;

public class CodeSelectFunction_Test extends AbstractModelTest {
	
	public void testSelectFunction() throws Exception {
		ICompilationUnit test = createCompilationUnit("test.d", "void func() { }");
		
		IJavaElement[] elements = test.codeSelect(6, 0);
		assertEquals(1, elements.length);
		
		assertEquals(getFunction(test, 0), elements[0]);
	}
	
	public void testSelectFunctionReference() throws Exception {
		ICompilationUnit test = createCompilationUnit("test.d", "void func() { func(); }");
		
		IJavaElement[] elements = test.codeSelect(15, 0);
		assertEquals(1, elements.length);
		
		assertEquals(getFunction(test, 0), elements[0]);
	}
	
	public void testSelectExternalFunctionReference() throws Exception {
		ICompilationUnit other = createCompilationUnit("other.d", "module other; void func() { }");
		ICompilationUnit test = createCompilationUnit("test.d", "import other; void foo() { func(); }");
		
		IJavaElement[] elements = test.codeSelect(28, 0);
		assertEquals(1, elements.length);
		
		assertEquals(getFunction(other, 0), elements[0]);
	}
	
	public void testSelectStaticMethodReference() throws Exception {
		ICompilationUnit test = createCompilationUnit("test.d", "class Foo { static void foo() { } } void bla() { Foo.foo(); }");
		
		IJavaElement[] elements = test.codeSelect(54, 0);
		assertEquals(1, elements.length);
		
		assertEquals(test.getAllTypes()[0].getChildren()[0], elements[0]);
	}
	
	public void testSelectClassStaticMethodReference() throws Exception {
		ICompilationUnit test = createCompilationUnit("test.d", "class Foo { static void foo() { } } void bla() { Foo.foo(); }");
		
		IJavaElement[] elements = test.codeSelect(50, 0);
		assertEquals(1, elements.length);
		
		assertEquals(test.getAllTypes()[0], elements[0]);
	}
	
	public void testSelectExternalStaticMethodReference() throws Exception {
		ICompilationUnit other = createCompilationUnit("other.d", "class Foo { static void foo() { } }");
		ICompilationUnit test = createCompilationUnit("test.d", "import other; void bla() { Foo.foo(); }");
		
		IJavaElement[] elements = test.codeSelect(32, 0);
		assertEquals(1, elements.length);
		
		assertEquals(other.getAllTypes()[0].getChildren()[0], elements[0]);
	}
	
	public void testSelectClassExternalStaticMethodReference() throws Exception {
		ICompilationUnit other = createCompilationUnit("other.d", "class Foo { static void foo() { } }");
		ICompilationUnit test = createCompilationUnit("test.d", "import other; void bla() { Foo.foo(); }");
		
		IJavaElement[] elements = test.codeSelect(28, 0);
		assertEquals(1, elements.length);
		
		assertEquals(other.getAllTypes()[0], elements[0]);
	}
	
	public void testSelectSetter() throws Exception {
		ICompilationUnit test = createCompilationUnit("test.d", "class Foo { void length(int x) { } } void bla() { Foo f = new Foo(); f.length = 2; }");
		
		IJavaElement[] elements = test.codeSelect(71, 0);
		assertEquals(1, elements.length);
		
		assertEquals(test.getAllTypes()[0].getChildren()[0], elements[0]);
	}
	
	public void testSelectGetter() throws Exception {
		ICompilationUnit test = createCompilationUnit("test.d", "class Foo { int length() { return 1; } } void bla() { Foo f = new Foo(); int x = f.length; }");
		
		IJavaElement[] elements = test.codeSelect(84, 0);
		assertEquals(1, elements.length);
		
		assertEquals(test.getAllTypes()[0].getChildren()[0], elements[0]);
	}
	
	public void testSelectExternalSetter() throws Exception {
		ICompilationUnit other = createCompilationUnit("other.d", "class Foo { void length(int x) { } }");
		ICompilationUnit test = createCompilationUnit("test.d", "import other; void bla() { Foo f = new Foo(); f.length = 2; }");
		
		IJavaElement[] elements = test.codeSelect(49, 0);
		assertEquals(1, elements.length);
		
		assertEquals(other.getAllTypes()[0].getChildren()[0], elements[0]);
	}
	
	public void testSelectExternalGetter() throws Exception {
		ICompilationUnit other = createCompilationUnit("other.d", "class Foo { int length() { return 1; } }");
		ICompilationUnit test = createCompilationUnit("test.d", "import other; void bla() { Foo f = new Foo(); int x = f.length; }");
		
		IJavaElement[] elements = test.codeSelect(57, 0);
		assertEquals(1, elements.length);
		
		assertEquals(other.getAllTypes()[0].getChildren()[0], elements[0]);
	}

}
