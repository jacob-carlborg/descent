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

}
