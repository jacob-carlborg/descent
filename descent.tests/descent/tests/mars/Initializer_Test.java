package descent.tests.mars;

import descent.core.dom.ICompilationUnit;
import descent.core.dom.IDElement;
import descent.core.dom.IExpressionInitializer;
import descent.core.dom.IInitializer;
import descent.core.dom.IVariableDeclaration;
import descent.internal.core.dom.ParserFacade;

public class Initializer_Test extends Parser_Test {
	
	public void testVoid() {
		IInitializer init = getInitializer("void");
		assertEquals(IDElement.INITIALIZER, init.getElementType());
		assertEquals(IInitializer.VOID_INITIALIZER, init.getInitializerType());
		assertPosition(init, 8, 4);
		
		assertVisitor(init, 1);
	}
	
	public void testExpression() {
		IExpressionInitializer init = (IExpressionInitializer) getInitializer("1");
		assertEquals(IInitializer.EXPRESSION_INITIALIZER, init.getInitializerType());
		assertEquals("1", init.getExpression().toString());
		assertPosition(init, 8, 1);
		
		assertVisitor(init, 2);
	}
	
	private IInitializer getInitializer(String init) {
		String s = "int x = " + init + ";";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IDElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IVariableDeclaration var = (IVariableDeclaration) declDefs[0];
		return var.getInitializer();
	}

}
