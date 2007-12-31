package descent.tests.select;

import descent.core.ICompilationUnit;
import descent.core.IJavaElement;
import descent.core.ILocalVariable;
import descent.tests.model.AbstractModelTest;

public class CodeSelectLocalVariable_Test extends AbstractModelTest {
	
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
	
	public void testSelectLocalVariable11() throws Exception {
		testSelectLocalVariable("Interface m(Interface x, Interface o)");
	}
	
	public void testSelectLocalVariable12() throws Exception {
		ICompilationUnit test = createCompilationUnit("test.d", "void m() { int var = 2; var = 3;}");
		
		IJavaElement[] elements = test.codeSelect(25, 0);
		assertEquals(1, elements.length);
		
		ILocalVariable var = (ILocalVariable) elements[0];
		assertEquals("var", var.getElementName());
		assertEquals(IJavaElement.LOCAL_VARIABLE, var.getElementType());
		assertEquals(15, var.getNameRange().getOffset());
		assertEquals(3, var.getNameRange().getLength());
		assertEquals("i", var.getTypeSignature());
		
		assertEquals(getFunction(test, 0), var.getParent());
	}
	
	public void testSelectParameter() throws Exception {
		ICompilationUnit test = createCompilationUnit("test.d", "void m(int var) { var = 3;}");
		
		IJavaElement[] elements = test.codeSelect(19, 0);
		assertEquals(1, elements.length);
		
		ILocalVariable var = (ILocalVariable) elements[0];
		assertEquals("var", var.getElementName());
		assertEquals(IJavaElement.LOCAL_VARIABLE, var.getElementType());
		assertEquals(11, var.getNameRange().getOffset());
		assertEquals(3, var.getNameRange().getLength());
		assertEquals("i", var.getTypeSignature());
		
		assertEquals(getFunction(test, 0), var.getParent());
	}

}
