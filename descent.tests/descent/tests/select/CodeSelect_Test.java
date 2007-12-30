package descent.tests.select;

import descent.core.ICompilationUnit;
import descent.core.IJavaElement;
import descent.core.ILocalVariable;
import descent.tests.model.AbstractModelTest;

public class CodeSelect_Test extends AbstractModelTest {
	
	public void testSelectClassFromName() throws Exception {
		ICompilationUnit unit = createCompilationUnit("test.d", "class Foo { }");
		
		IJavaElement[] elements = unit.codeSelect(7, 0);
		assertEquals(1, elements.length);
		assertEquals(unit.getAllTypes()[0], elements[0]);
	}
	
	public void testSelectInterfaceFromName() throws Exception {
		ICompilationUnit unit = createCompilationUnit("test.d", "interface Foo { }");
		
		IJavaElement[] elements = unit.codeSelect(11, 0);
		assertEquals(1, elements.length);
		assertEquals(unit.getAllTypes()[0], elements[0]);
	}
	
	public void testSelectStructFromName() throws Exception {
		ICompilationUnit unit = createCompilationUnit("test.d", "struct Foo { }");
		
		IJavaElement[] elements = unit.codeSelect(8, 0);
		assertEquals(1, elements.length);
		assertEquals(unit.getAllTypes()[0], elements[0]);
	}
	
	public void testSelectUnionFromName() throws Exception {
		ICompilationUnit unit = createCompilationUnit("test.d", "union Foo { }");
		
		IJavaElement[] elements = unit.codeSelect(7, 0);
		assertEquals(1, elements.length);
		assertEquals(unit.getAllTypes()[0], elements[0]);
	}
	
	public void testSelectEnumFromName() throws Exception {
		ICompilationUnit unit = createCompilationUnit("test.d", "enum Foo { x }");
		
		IJavaElement[] elements = unit.codeSelect(6, 0);
		assertEquals(1, elements.length);
		assertEquals(unit.getAllTypes()[0], elements[0]);
	}
	
	public void testSelectClassFromTypeInVarDeclaration() throws Exception {
		ICompilationUnit unit = createCompilationUnit("test.d", "class Foo { } Foo foo;");
		
		IJavaElement[] elements = unit.codeSelect(15, 0);
		assertEquals(1, elements.length);
		assertEquals(unit.getAllTypes()[0], elements[0]);
	}
	
	public void testSelectClassFromReturnTypeInFuncDeclaration() throws Exception {
		ICompilationUnit unit = createCompilationUnit("test.d", "class Foo { } Foo foo() { return null; }");
		
		IJavaElement[] elements = unit.codeSelect(15, 0);
		assertEquals(1, elements.length);
		assertEquals(unit.getAllTypes()[0], elements[0]);
	}
	
	public void testSelectClassFromParameterInFuncDeclaration() throws Exception {
		ICompilationUnit unit = createCompilationUnit("test.d", "class Foo { } void foo(Foo f) { }");
		
		IJavaElement[] elements = unit.codeSelect(24, 0);
		assertEquals(1, elements.length);
		assertEquals(unit.getAllTypes()[0], elements[0]);
	}
	
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
	
	public void testSelectLocalVariable() throws Exception {
		ICompilationUnit test = createCompilationUnit("test.d", "void m() { int var = 2; }");
		
		IJavaElement[] elements = test.codeSelect(16, 0);
		assertEquals(1, elements.length);
		
		ILocalVariable var = (ILocalVariable) elements[0];
		assertEquals("var", var.getElementName());
		assertEquals(IJavaElement.LOCAL_VARIABLE, var.getElementType());
		assertEquals(15, var.getNameRange().getOffset());
		assertEquals(3, var.getNameRange().getLength());
		assertEquals("i", var.getTypeSignature());
		
		assertEquals(getFunction(test, 0), var.getParent());
	}
	
	public void testSelectLocalVariable2() throws Exception {
		ICompilationUnit test = createCompilationUnit("test.d", "int m(int x) { } void m() { int var = 2; }");
		
		IJavaElement[] elements = test.codeSelect(33, 0);
		assertEquals(1, elements.length);
		
		ILocalVariable var = (ILocalVariable) elements[0];
		assertEquals("var", var.getElementName());
		assertEquals(IJavaElement.LOCAL_VARIABLE, var.getElementType());
		assertEquals(32, var.getNameRange().getOffset());
		assertEquals(3, var.getNameRange().getLength());
		assertEquals("i", var.getTypeSignature());
		
		assertEquals(getFunction(test, 1), var.getParent());
	}
	
	private void testSelectLocalVariable(String signature) throws Exception {
		ICompilationUnit test = createCompilationUnit("test.d", "void m() { } " + signature + " { int var = 2; }");
		
		IJavaElement[] elements = test.codeSelect(21 + signature.length(), 0);
		assertEquals(1, elements.length);
		
		ILocalVariable var = (ILocalVariable) elements[0];
		assertEquals("var", var.getElementName());
		assertEquals(IJavaElement.LOCAL_VARIABLE, var.getElementType());
		assertEquals(20 + signature.length(), var.getNameRange().getOffset());
		assertEquals(3, var.getNameRange().getLength());
		assertEquals("i", var.getTypeSignature());
		
		assertEquals(getFunction(test, 1), var.getParent());
	}
	
	public void testSelectLocalVariable3() throws Exception {
		testSelectLocalVariable("int m(int x)");
	}
	
	public void testSelectLocalVariable4() throws Exception {
		testSelectLocalVariable("int m(int x, bool b)");
	}
	
	public void testSelectLocalVariable5() throws Exception {
		testSelectLocalVariable("int m(int x, Object o)");
	}
	
	public void testSelectLocalVariable6() throws Exception {
		testSelectLocalVariable("int* m(int* x, Object o)");
	}
	
	public void testSelectLocalVariable7() throws Exception {
		testSelectLocalVariable("char[] m(char[] x, Object o)");
	}
	
	public void testSelectLocalVariable8() throws Exception {
		testSelectLocalVariable("char[float] m(char[float] x, Object o)");
	}
	
	public void testSelectLocalVariable9() throws Exception {
		testSelectLocalVariable("ifloat m(char[2] x, Object o)");
	}
	
	public void testSelectLocalVariable10() throws Exception {
		testSelectLocalVariable("Object m(Object x, Object o)");
	}
	
	public void testSelectLocalVariable11(String signature) throws Exception {
		ICompilationUnit test = createCompilationUnit("test.d", "void m() { int var = 2; var = 3;}");
		
		IJavaElement[] elements = test.codeSelect(25, 0);
		assertEquals(1, elements.length);
		
		ILocalVariable var = (ILocalVariable) elements[0];
		assertEquals("var", var.getElementName());
		assertEquals(IJavaElement.LOCAL_VARIABLE, var.getElementType());
		assertEquals(24, var.getNameRange().getOffset());
		assertEquals(3, var.getNameRange().getLength());
		assertEquals("i", var.getTypeSignature());
		
		assertEquals(getFunction(test, 0), var.getParent());
	}
	
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

}
