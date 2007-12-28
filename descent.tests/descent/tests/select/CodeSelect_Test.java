package descent.tests.select;

import descent.core.ICompilationUnit;
import descent.core.IJavaElement;
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
		ICompilationUnit unit = createCompilationUnit("test.d", "class Foo { } Foo f;");
		
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

}
