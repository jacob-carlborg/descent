package descent.tests.select;

import descent.core.ICompilationUnit;
import descent.core.IJavaElement;
import descent.tests.model.AbstractModelTest;

public class CodeSelecType_Test extends AbstractModelTest {
	
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
	
	public void testSelectNestedClassFromParameterInFuncDeclaration1() throws Exception {
		ICompilationUnit unit = createCompilationUnit("test.d", "class Foo { class Bar { } } void foo(Foo.Bar f) { }");
		
		IJavaElement[] elements = unit.codeSelect(38, 0);
		assertEquals(1, elements.length);
		assertEquals(unit.getAllTypes()[0], elements[0]);
	}
	
	public void testSelectNestedClassFromParameterInFuncDeclaration2() throws Exception {
		ICompilationUnit unit = createCompilationUnit("test.d", "class Foo { class Bar { } } void foo(Foo.Bar f) { }");
		
		IJavaElement[] elements = unit.codeSelect(42, 0);
		assertEquals(1, elements.length);
		assertEquals(unit.getAllTypes()[0].getChildren()[0], elements[0]);
	}
	
	public void testTypeInTypeof() throws Exception {
		ICompilationUnit unit = createCompilationUnit("test.d", "class Foo { } void foo() { typeof(Foo) f; }");
		
		IJavaElement[] elements = unit.codeSelect(35, 0);
		assertEquals(1, elements.length);
		assertEquals(unit.getAllTypes()[0], elements[0]);
	}
	
	public void testTypeInTypeid() throws Exception {
		ICompilationUnit unit = createCompilationUnit("test.d", "class Foo { } void foo() { auto o = typeid(Foo); }");
		
		IJavaElement[] elements = unit.codeSelect(44, 0);
		assertEquals(1, elements.length);
		assertEquals(unit.getAllTypes()[0], elements[0]);
	}
	
	public void testAliasOfSymbolDotSymbol() throws Exception {
		ICompilationUnit unit = createCompilationUnit("test.d", "class Foo { void bar(); } alias Foo.bar myAlias;");
		
		IJavaElement[] elements = unit.codeSelect(33, 0);
		assertEquals(1, elements.length);
		assertEquals(unit.getAllTypes()[0], elements[0]);
		
		elements = unit.codeSelect(37, 0);
		assertEquals(1, elements.length);
		assertEquals(unit.getAllTypes()[0].getChildren()[0], elements[0]);
	}
	
	public void testSelectClassCreatedFromMixin() throws Exception {
		ICompilationUnit unit = createCompilationUnit("test.d", "mixin(\"class Foo { }\"); Foo foo;");
		
		IJavaElement[] elements = unit.codeSelect(27, 0);
		assertEquals(1, elements.length);
		assertEquals(unit.getChildren()[0], elements[0]);
	}

}
