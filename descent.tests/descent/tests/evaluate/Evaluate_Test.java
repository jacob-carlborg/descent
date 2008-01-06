package descent.tests.evaluate;

import descent.core.ICompilationUnit;
import descent.core.dom.Void;
import descent.tests.model.AbstractModelTest;

public class Evaluate_Test extends AbstractModelTest {
	
	public void testConstVoid() throws Exception {
		ICompilationUnit unit = createCompilationUnit("test.d", "const int x = void;");
		assertEquals(Void.getInstance(), unit.codeEvaluate(10));
	}
	
	public void testConstBool() throws Exception {
		ICompilationUnit unit = createCompilationUnit("test.d", "const bool x = true;");
		assertEquals(true, unit.codeEvaluate(11));
	}
	
	public void testConstInt() throws Exception {
		ICompilationUnit unit = createCompilationUnit("test.d", "const int x = 2;");
		assertEquals(2, unit.codeEvaluate(10));
	}
	
	public void testVariableReference() throws Exception {
		ICompilationUnit unit = createCompilationUnit("test.d", "const int x = 2; const int y = x + 2;");
		assertEquals(2, unit.codeEvaluate(31));
	}
	
	public void testFunctionCall() throws Exception {
		ICompilationUnit unit = createCompilationUnit("test.d", "int foo() { return 2; } const int x = foo();");
		assertEquals(2, unit.codeEvaluate(39));
	}

}
