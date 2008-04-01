package descent.tests.binding;

import descent.core.dom.CompilationUnit;
import descent.core.dom.DeclarationStatement;
import descent.core.dom.Expression;
import descent.core.dom.ExpressionInitializer;
import descent.core.dom.FunctionDeclaration;
import descent.core.dom.IBinding;
import descent.core.dom.VariableDeclaration;
import descent.core.dom.VariableDeclarationFragment;


public class BindingExpression_Test extends AbstractBinding_Test {
	
	public void testExpression(String expString, String expectedSignature) throws Exception {
		testExpression("auto", expString, expectedSignature);
	}
	
	public void testExpression(String type, String expString, String expectedSignature) throws Exception {
		CompilationUnit unit = createCU("test.d", "void foo() { " + type + " x = " + expString + "; }");
		
		FunctionDeclaration func = (FunctionDeclaration) unit.declarations().get(0);
		DeclarationStatement statement = (DeclarationStatement) func.getBody().statements().get(0);
		
		VariableDeclaration var = (VariableDeclaration) statement.getDeclaration();
		VariableDeclarationFragment fragment = var.fragments().get(0);
		ExpressionInitializer init = (ExpressionInitializer) fragment.getInitializer();
		Expression exp = init.getExpression();
		
		IBinding typeBinding = exp.resolveTypeBinding();
		assertEquals(expectedSignature, typeBinding.getKey());
		
		assertSame(typeBinding, var.resolveBinding());
	}
	
	public void testInt() throws Exception {
		testExpression("1", "i");
	}
	
	public void testChar() throws Exception {
		testExpression("'x'", "a");
	}
	
	public void testBool() throws Exception {
		testExpression("false", "b");
	}
	
	public void testString() throws Exception {
		testExpression("\"hey\"", "GaG1G3");
	}
	
	public void testStrings() throws Exception {
		testExpression("\"hey\" \"you\"", "GaG1G6");
	}
	
	public void testParenthesis() throws Exception {
		testExpression("(1)", "i");
	}
	
	public void testFloat() throws Exception {
		testExpression("1f", "f");
	}
	
	public void testImaginary() throws Exception {
		testExpression("1i", "p");
	}
	
	public void testAdd() throws Exception {
		testExpression("1 + 1", "i");
	}

}
