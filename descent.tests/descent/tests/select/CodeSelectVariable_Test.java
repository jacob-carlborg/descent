package descent.tests.select;

import descent.core.ICompilationUnit;
import descent.core.IJavaElement;
import descent.tests.model.AbstractModelTest;

public class CodeSelectVariable_Test extends AbstractModelTest {
	
	public void testSelectVariable() throws Exception {
		ICompilationUnit unit = createCompilationUnit("test.d", "class Foo { } Foo foo;");
		
		IJavaElement[] elements = unit.codeSelect(19, 0);
		assertEquals(1, elements.length);
		
		assertEquals(getVariable(unit, 0), elements[0]);
	}
	
	public void testSelectReferenceToVariable() throws Exception {
		ICompilationUnit unit = createCompilationUnit("test.d", "class Foo { } Foo foo; void func() { foo = null; }");
		
		IJavaElement[] elements = unit.codeSelect(38, 0);
		assertEquals(1, elements.length);
		
		assertEquals(getVariable(unit, 0), elements[0]);
	}
	
	public void testSelectReferenceToExternalVariable() throws Exception {
		ICompilationUnit other = createCompilationUnit("other.d", "class Foo { } Foo foo;");
		ICompilationUnit test = createCompilationUnit("test.d", "import other; void func() { foo = null; }");
		
		IJavaElement[] elements = test.codeSelect(29, 0);
		assertEquals(1, elements.length);
		
		assertEquals(getVariable(other, 0), elements[0]);
	}
	
	public void testSelectReferenceToExternalAlias() throws Exception {
		ICompilationUnit other = createCompilationUnit("other.d", "alias int myInt;");
		ICompilationUnit test = createCompilationUnit("test.d", "import other; myInt i;");
		
		IJavaElement[] elements = test.codeSelect(15, 0);
		assertEquals(1, elements.length);
		
		assertEquals(getVariable(other, 0), elements[0]);
	}
	
	public void testSelectReferenceToStaticClassVariable() throws Exception {
		ICompilationUnit test = createCompilationUnit("test.d", "class Foo { static int x; } void m() { Foo.x = 2; }");
		
		IJavaElement[] elements = test.codeSelect(43, 0);
		assertEquals(1, elements.length);
		
		assertEquals(test.getAllTypes()[0].getChildren()[0], elements[0]);
	}
	
	public void testSelectReferenceToClassInStaticClassVariable() throws Exception {
		ICompilationUnit test = createCompilationUnit("test.d", "class Foo { static int x; } void m() { Foo.x = 2; }");
		
		IJavaElement[] elements = test.codeSelect(40, 0);
		assertEquals(1, elements.length);
		
		assertEquals(test.getAllTypes()[0], elements[0]);
	}
	
	public void testSelectReferenceToVariableClass() throws Exception {
		ICompilationUnit test = createCompilationUnit("test.d", "class Foo { int x; } Foo f; void m() { f.x = 2; }");
		
		IJavaElement[] elements = test.codeSelect(39, 0);
		assertEquals(1, elements.length);
		
		assertEquals(getVariable(test, 0), elements[0]);
	}
	
	public void testSelectReferenceToClassVariableClass() throws Exception {
		ICompilationUnit test = createCompilationUnit("test.d", "class Foo { int x; } Foo f; void m() { f.x = 2; }");
		
		IJavaElement[] elements = test.codeSelect(41, 0);
		assertEquals(1, elements.length);
		
		assertEquals(test.getAllTypes()[0].getChildren()[0], elements[0]);
	}
	
	public void testSelectExternalReferenceToVariableClass() throws Exception {
		ICompilationUnit other = createCompilationUnit("other.d", "class Foo { int x; } Foo f;");
		ICompilationUnit test = createCompilationUnit("test.d", "import other; void m() { f.x = 2; }");
		
		IJavaElement[] elements = test.codeSelect(27, 0);
		assertEquals(1, elements.length);
		
		assertEquals(other.getAllTypes()[0].getChildren()[0], elements[0]);
	}
	
	public void testSelectExternalReferenceToClassVariableClass() throws Exception {
		ICompilationUnit other = createCompilationUnit("other.d", "class Foo { int x; } Foo f;");
		ICompilationUnit test = createCompilationUnit("test.d", "import other; void m() { f.x = 2; }");
		
		IJavaElement[] elements = test.codeSelect(25, 0);
		assertEquals(1, elements.length);
		
		assertEquals(getVariable(other, 0), elements[0]);
	}
	
	public void testSelectExternalReferenceToStructVariableThroughPointer() throws Exception {
		ICompilationUnit other = createCompilationUnit("other.d", "struct Foo { int x; }");
		ICompilationUnit test = createCompilationUnit("test.d", "import other; void foo(Foo* f) { f.x = 2; }");
		
		IJavaElement[] elements = test.codeSelect(35, 0);
		assertEquals(1, elements.length);
		
		assertEquals(other.getAllTypes()[0].getChildren()[0], elements[0]);
	}
	
	public void testSelectAlias() throws Exception {
		ICompilationUnit test = createCompilationUnit("test.d", "alias int FOO;");
		
		IJavaElement[] elements = test.codeSelect(11, 0);
		assertEquals(1, elements.length);
		
		assertEquals(getVariable(test, 0), elements[0]);
	}
	
	public void testSelectAliasType() throws Exception {
		ICompilationUnit test = createCompilationUnit("test.d", "class Foo { } alias Foo FOO;");
		
		IJavaElement[] elements = test.codeSelect(21, 0);
		assertEquals(1, elements.length);
		
		assertEquals(test.getAllTypes()[0], elements[0]);
	}
	
	public void testSelectTypedef() throws Exception {
		ICompilationUnit test = createCompilationUnit("test.d", "typedef int FOO;");
		
		IJavaElement[] elements = test.codeSelect(13, 0);
		assertEquals(1, elements.length);
		
		assertEquals(getVariable(test, 0), elements[0]);
	}
	
	public void testSelectTypedefType() throws Exception {
		ICompilationUnit test = createCompilationUnit("test.d", "class Foo { } typedef Foo FOO;");
		
		IJavaElement[] elements = test.codeSelect(23, 0);
		assertEquals(1, elements.length);
		
		assertEquals(test.getAllTypes()[0], elements[0]);
	}
	
	public void testSelectAliasTypeAsPointer() throws Exception {
		ICompilationUnit test = createCompilationUnit("test.d", "class Foo { } alias Foo* FOO;");
		
		IJavaElement[] elements = test.codeSelect(21, 0);
		assertEquals(1, elements.length);
		
		assertEquals(test.getAllTypes()[0], elements[0]);
	}

}
