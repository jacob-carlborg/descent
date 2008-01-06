package descent.tests.evaluate;

import descent.core.ICompilationUnit;
import descent.tests.model.AbstractModelTest;

public class Evaluate_Test extends AbstractModelTest {
	
	public void testConstVoid() throws Exception {
		ICompilationUnit unit = createCompilationUnit("test.d", "const int x = void;");
		assertNull(unit.codeEvaluate(10).getValue());
	}
	
	public void testConstBool() throws Exception {
		ICompilationUnit unit = createCompilationUnit("test.d", "const bool x = true;");
		assertEquals(true, unit.codeEvaluate(11).getValue());
	}
	
	public void testConstInt() throws Exception {
		ICompilationUnit unit = createCompilationUnit("test.d", "const int x = 2;");
		assertEquals(2, unit.codeEvaluate(10).getValue());
	}
	
	public void testVariableReference() throws Exception {
		ICompilationUnit unit = createCompilationUnit("test.d", "const int x = 2; const int y = x + 2;");
		assertEquals(2, unit.codeEvaluate(31).getValue());
	}
	
	public void testFunctionCall() throws Exception {
		ICompilationUnit unit = createCompilationUnit("test.d", "int foo() { return 2; } const int x = foo();");
		assertEquals(2, unit.codeEvaluate(39).getValue());
	}

}
