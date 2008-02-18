package descent.tests.select;

import descent.core.ICompilationUnit;
import descent.core.IJavaElement;
import descent.tests.model.AbstractModelTest;

public class CodeSelectTemplate_Test extends AbstractModelTest {
	
	public void testSelectTemplate() throws Exception {
		ICompilationUnit unit = createCompilationUnit("test.d", 
				"template Foo() { const char[] Foo = \"int x;\"; } mixin(Foo!());");
		
		IJavaElement[] elements = unit.codeSelect(10, 0);
		assertEquals(1, elements.length);
		assertEquals(unit.getAllTypes()[0], elements[0]);
	}
	
	public void testSelectTemplateFromName() throws Exception {
		ICompilationUnit unit = createCompilationUnit("test.d", 
				"template Foo() { const char[] Foo = \"int x;\"; } mixin(Foo!());");
		
		IJavaElement[] elements = unit.codeSelect(57, 0);
		assertEquals(1, elements.length);
		assertEquals(unit.getAllTypes()[0], elements[0]);
	}
	
	public void testSelectTemplateFromName2() throws Exception {
		ICompilationUnit unit = createCompilationUnit("test.d", 
				"template Foo(int T) { const char[] Foo = \"int x;\"; } mixin(Foo!(1));");
		
		IJavaElement[] elements = unit.codeSelect(62, 0);
		assertEquals(1, elements.length);
		assertEquals(unit.getAllTypes()[0], elements[0]);
	}
	
	public void testSelectNestedTemplateFromName() throws Exception {
		ICompilationUnit unit = createCompilationUnit("test.d", 
				"class Bar { template Foo(int T) { const char[] Foo = \"int x;\"; } } mixin(Bar.Foo!(1));");
		
		IJavaElement[] elements = unit.codeSelect(76, 0);
		assertEquals(1, elements.length);
		assertEquals(unit.getAllTypes()[0], elements[0]);
		
		elements = unit.codeSelect(80, 0);
		assertEquals(1, elements.length);
		assertEquals(unit.getAllTypes()[0].getChildren()[0], elements[0]);
	}
	
	public void testSelectNestedTemplateFromName2() throws Exception {
		ICompilationUnit unit = createCompilationUnit("test.d", 
				"template Bar() { template Foo(int T) { const char[] Foo = \"int x;\"; } } mixin(Bar!().Foo!(1));");
		
		IJavaElement[] elements = unit.codeSelect(81, 0);
		assertEquals(1, elements.length);
		assertEquals(unit.getAllTypes()[0], elements[0]);
		
		elements = unit.codeSelect(85, 0);
		assertEquals(1, elements.length);
		assertEquals(unit.getAllTypes()[0].getChildren()[0], elements[0]);
	}
	
	public void testSelectNestedTemplateFromName3() throws Exception {
		ICompilationUnit unit = createCompilationUnit("test.d", 
				"template Bar() { template Foo(alias T) { const char[] Foo = \"int x;\"; } } mixin(Bar!(Bar).Foo!(1));");
		
		IJavaElement[] elements = unit.codeSelect(88, 0);
		assertEquals(1, elements.length);
		assertEquals(unit.getAllTypes()[0], elements[0]);
	}
	
	public void testSelectTemplatedClass() throws Exception {
		ICompilationUnit unit = createCompilationUnit("test.d", 
				"class Bar() { }");
		
		IJavaElement[] elements = unit.codeSelect(7, 0);
		assertEquals(1, elements.length);
		assertEquals(unit.getAllTypes()[0], elements[0]);
	}
	
	public void testSelectReferenceToTemplatedClass() throws Exception {
		ICompilationUnit unit = createCompilationUnit("test.d", 
				"class Bar() { } Bar!() x;");
		
		IJavaElement[] elements = unit.codeSelect(17, 0);
		assertEquals(1, elements.length);
		assertEquals(unit.getAllTypes()[0], elements[0]);
	}
	
	public void testSelectReferenceToTemplatedClass2() throws Exception {
		ICompilationUnit unit = createCompilationUnit("test.d", 
				"class Bar(int T) { } class Bar(float T) { } Bar!(1) x; Bar!(1.3) y;");
		
		IJavaElement[] elements;
		
		elements = unit.codeSelect(7, 0);
		assertEquals(1, elements.length);
		assertEquals(unit.getAllTypes()[0], elements[0]);
		
		elements = unit.codeSelect(28, 0);
		assertEquals(1, elements.length);
		assertEquals(unit.getAllTypes()[1], elements[0]);

		elements = unit.codeSelect(45, 0);
		assertEquals(1, elements.length);
		assertEquals(unit.getAllTypes()[0], elements[0]);
		
		elements = unit.codeSelect(56, 0);
		assertEquals(1, elements.length);
		assertEquals(unit.getAllTypes()[1], elements[0]);
	}
	
	public void testSelectTemplatedFunction() throws Exception {
		ICompilationUnit unit = createCompilationUnit("test.d", 
				"void foo()() { }");
		
		IJavaElement[] elements = unit.codeSelect(6, 0);
		assertEquals(1, elements.length);
		assertEquals(getFunction(unit, 0), elements[0]);
	}
	
	public void testSelectReferenceToTemplatedFunction() throws Exception {
		ICompilationUnit unit = createCompilationUnit("test.d", 
				"void foo()() { } void main() { foo!()(); }");
		
		IJavaElement[] elements = unit.codeSelect(32, 0);
		assertEquals(1, elements.length);
		assertEquals(getFunction(unit, 0), elements[0]);
	}
	
	public void testSelectReferenceToTemplatedFunction2() throws Exception {
		ICompilationUnit unit = createCompilationUnit("test.d", 
				"void foo(int T)() { } void foo(float T)() { } void main() { foo!(1)(); foo!(1.3)(); }");
		
		IJavaElement[] elements;
		
		elements = unit.codeSelect(6, 0);
		assertEquals(1, elements.length);
		assertEquals(getFunction(unit, 0), elements[0]);
		
		elements = unit.codeSelect(28, 0);
		assertEquals(1, elements.length);
		assertEquals(getFunction(unit, 1), elements[0]);
		
		elements = unit.codeSelect(61, 0);
		assertEquals(1, elements.length);
		assertEquals(getFunction(unit, 0), elements[0]);
		
		elements = unit.codeSelect(72, 0);
		assertEquals(1, elements.length);
		assertEquals(getFunction(unit, 1), elements[0]);
	}

}
