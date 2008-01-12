package descent.tests.select;

import descent.core.ICompilationUnit;
import descent.core.IJavaElement;
import descent.tests.model.AbstractModelTest;

public class CodeSelecTemplate_Test extends AbstractModelTest {
	
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

}
