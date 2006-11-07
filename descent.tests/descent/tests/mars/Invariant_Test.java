package descent.tests.mars;

import descent.core.dom.ICompilationUnit;
import descent.core.dom.IDElement;
import descent.core.dom.IInvariantDeclaration;
import descent.internal.core.dom.ParserFacade;

public class Invariant_Test extends Parser_Test {
	
	public void test() {
		String s = " invariant { } ";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IDElement[] declDefs = unit.getDeclarationDefinitions();
		
		IInvariantDeclaration inv = (IInvariantDeclaration) declDefs[0];
		assertEquals(IDElement.INVARIANT_DECLARATION, inv.getElementType());
		assertPosition(inv, 1, 13);
		
		assertVisitor(inv, 2);
	}

}
