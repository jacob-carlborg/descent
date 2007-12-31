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
		testSelectLocalVariable("int opApply(int delegate(inout bool) dg)");
	}
	
	public void testSelectLocalVariableReferenceAssign(String op) throws Exception {
		ICompilationUnit test = createCompilationUnit("test.d", "void m() { int var = 2; var = var " + op + " var;}");
		
		for(int i : new int[] { 33, 38 + op.length() }) {
			IJavaElement[] elements = test.codeSelect(i, 0);
			assertEquals(1, elements.length);
			
			ILocalVariable var = (ILocalVariable) elements[0];
			assertEquals("var", var.getElementName());
			assertEquals(IJavaElement.LOCAL_VARIABLE, var.getElementType());
			assertEquals(15, var.getNameRange().getOffset());
			assertEquals(3, var.getNameRange().getLength());
			assertEquals("i", var.getTypeSignature());
			
			assertEquals(getFunction(test, 0), var.getParent());
		}
	}
	
	public void testSelectLocalVariableReferenceAssign() throws Exception {
		testSelectLocalVariableReferenceAssign("=");
	}
	
	public void testSelectLocalVariableReferenceAddAssign() throws Exception {
		testSelectLocalVariableReferenceAssign("+=");
	}
	
	public void testSelectLocalVariableReferenceMinAssign() throws Exception {
		testSelectLocalVariableReferenceAssign("-=");
	}
	
	public void testSelectLocalVariableReferenceMulAssign() throws Exception {
		testSelectLocalVariableReferenceAssign("*=");
	}
	
	public void testSelectLocalVariableReferenceDivAssign() throws Exception {
		testSelectLocalVariableReferenceAssign("/=");
	}
	
	public void testSelectLocalVariableReferenceAndAssign() throws Exception {
		testSelectLocalVariableReferenceAssign("&=");
	}
	
	public void testSelectLocalVariableReferenceOrAssign() throws Exception {
		testSelectLocalVariableReferenceAssign("|=");
	}
	
	public void testSelectLocalVariableReferenceXorAssign() throws Exception {
		testSelectLocalVariableReferenceAssign("^=");
	}
	
	public void testSelectLocalVariableReferenceModAssign() throws Exception {
		testSelectLocalVariableReferenceAssign("%=");
	}
	
	public void testSelectLocalVariableReferenceCatAssign() throws Exception {
		testSelectLocalVariableReferenceAssign("~=");
	}
	
	public void testSelectLocalVariableReferenceShiftRightAssign() throws Exception {
		testSelectLocalVariableReferenceAssign(">>=");
	}
	
	public void testSelectLocalVariableReferenceShiftLeftAssign() throws Exception {
		testSelectLocalVariableReferenceAssign("<<=");
	}
	
	public void testSelectLocalVariableReferenceUnsignedShiftRightAssign() throws Exception {
		testSelectLocalVariableReferenceAssign(">>>=");
	}
	
	public void testSelectLocalVariableReferenceOp(String op) throws Exception {
		ICompilationUnit test = createCompilationUnit("test.d", "void m() { int var = 2; int x = var " + op + " var;}");
		
		for(int i : new int[] { 33, 38 + op.length() }) {
			IJavaElement[] elements = test.codeSelect(i, 0);
			assertEquals(1, elements.length);
			
			ILocalVariable var = (ILocalVariable) elements[0];
			assertEquals("var", var.getElementName());
			assertEquals(IJavaElement.LOCAL_VARIABLE, var.getElementType());
			assertEquals(15, var.getNameRange().getOffset());
			assertEquals(3, var.getNameRange().getLength());
			assertEquals("i", var.getTypeSignature());
			
			assertEquals(getFunction(test, 0), var.getParent());
		}
	}
	
	public void testSelectLocalVariableReferenceAdd() throws Exception {
		testSelectLocalVariableReferenceAssign("+");
	}
	
	public void testSelectLocalVariableReferenceAndAnd() throws Exception {
		testSelectLocalVariableReferenceAssign("&&");
	}
	
	public void testSelectLocalVariableReferenceAnd() throws Exception {
		testSelectLocalVariableReferenceAssign("&");
	}
	
	public void testSelectLocalVariableReferenceCat() throws Exception {
		testSelectLocalVariableReferenceAssign("~");
	}
	
	public void testSelectLocalVariableReferenceCmp() throws Exception {
		testSelectLocalVariableReferenceAssign("<");
	}
	
	public void testSelectLocalVariableReferenceDiv() throws Exception {
		testSelectLocalVariableReferenceAssign("/");
	}
	
	public void testSelectLocalVariableReferenceIdentity() throws Exception {
		testSelectLocalVariableReferenceAssign("is");
	}
	
	public void testSelectLocalVariableReferenceIn() throws Exception {
		testSelectLocalVariableReferenceAssign("in");
	}
	
	public void testSelectLocalVariableReferenceMin() throws Exception {
		testSelectLocalVariableReferenceAssign("-");
	}
	
	public void testSelectLocalVariableReferenceMod() throws Exception {
		testSelectLocalVariableReferenceAssign("%");
	}
	
	public void testSelectLocalVariableReferenceMul() throws Exception {
		testSelectLocalVariableReferenceAssign("*");
	}
	
	public void testSelectLocalVariableReferenceOr() throws Exception {
		testSelectLocalVariableReferenceAssign("|");
	}
	
	public void testSelectLocalVariableReferenceShiftRight() throws Exception {
		testSelectLocalVariableReferenceAssign(">>");
	}
	
	public void testSelectLocalVariableReferenceShiftLeft() throws Exception {
		testSelectLocalVariableReferenceAssign("<<");
	}
	
	public void testSelectLocalVariableReferenceUnsignedShiftRight() throws Exception {
		testSelectLocalVariableReferenceAssign(">>");
	}
	
	public void testSelectLocalVariableReferenceXor() throws Exception {
		testSelectLocalVariableReferenceAssign("^");
	}
	
	public void testSelectLocalVariableReferencePost(String op) throws Exception {
		ICompilationUnit test = createCompilationUnit("test.d", "void m() { int var = 2; int x = var " + op + ";}");
		
		IJavaElement[] elements = test.codeSelect(33, 0);
		assertEquals(1, elements.length);
		
		ILocalVariable var = (ILocalVariable) elements[0];
		assertEquals("var", var.getElementName());
		assertEquals(IJavaElement.LOCAL_VARIABLE, var.getElementType());
		assertEquals(15, var.getNameRange().getOffset());
		assertEquals(3, var.getNameRange().getLength());
		assertEquals("i", var.getTypeSignature());
		
		assertEquals(getFunction(test, 0), var.getParent());
	}
	
	public void testSelectLocalVariableReferenceInc() throws Exception {
		testSelectLocalVariableReferenceAssign("++");
	}
	
	public void testSelectLocalVariableReferenceDec() throws Exception {
		testSelectLocalVariableReferenceAssign("--");
	}
	
	public void testForVariable() throws Exception {
		ICompilationUnit test = createCompilationUnit("test.d", "void m() { for(int i = 0; i < 10; i++) { i = i; }");
		
		for(int i : new int[] { 19, 26, 34, 41, 45 }) {
			IJavaElement[] elements = test.codeSelect(i, 0);
			assertEquals(1, elements.length);
			
			ILocalVariable var = (ILocalVariable) elements[0];
			assertEquals("i", var.getElementName());
			assertEquals(IJavaElement.LOCAL_VARIABLE, var.getElementType());
			assertEquals(19, var.getNameRange().getOffset());
			assertEquals(1, var.getNameRange().getLength());
			assertEquals("i", var.getTypeSignature());
			
			assertEquals(getFunction(test, 0), var.getParent());
		}
	}
	
	public void testForeachVariable() throws Exception {
		ICompilationUnit test = createCompilationUnit("test.d", "void m() { foreach(char c; \"hey\") { char d = c; } }");
		
		for(int i : new int[] { 24, 45 }) {
			IJavaElement[] elements = test.codeSelect(i, 0);
			assertEquals(1, elements.length);
			
			ILocalVariable var = (ILocalVariable) elements[0];
			assertEquals("c", var.getElementName());
			assertEquals(IJavaElement.LOCAL_VARIABLE, var.getElementType());
			assertEquals(24, var.getNameRange().getOffset());
			assertEquals(1, var.getNameRange().getLength());
			assertEquals("a", var.getTypeSignature());
			
			assertEquals(getFunction(test, 0), var.getParent());
		}
	}
	
	public void testForeachVariable2() throws Exception {
		ICompilationUnit test = createCompilationUnit("test.d", "void m() { foreach(c; \"hey\") { char d = c; } }");
		
		for(int i : new int[] { 19, 40 }) {
			IJavaElement[] elements = test.codeSelect(i, 0);
			assertEquals(1, elements.length);
			
			ILocalVariable var = (ILocalVariable) elements[0];
			assertEquals("c", var.getElementName());
			assertEquals(IJavaElement.LOCAL_VARIABLE, var.getElementType());
			assertEquals(19, var.getNameRange().getOffset());
			assertEquals(1, var.getNameRange().getLength());
			assertEquals("a", var.getTypeSignature());
			
			assertEquals(getFunction(test, 0), var.getParent());
		}
	}
	
	public void testSelectParameter() throws Exception {
		ICompilationUnit test = createCompilationUnit("test.d", "void m(int var) { var = 3;}");
		
		for(int i : new int[] { 12, 19 }) {
			IJavaElement[] elements = test.codeSelect(i, 0);
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

}
