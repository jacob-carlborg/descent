package descent.tests.mars;

import descent.core.dom.ICompilationUnit;
import descent.core.dom.IElement;
import descent.core.dom.IInvariantDeclaration;
import descent.internal.core.dom.ParserFacade;

public class Invariant_Test extends Parser_Test {
	
	public void test() {
		String s = " invariant { } ";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		assertEquals(0, unit.getProblems().length);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		
		IInvariantDeclaration inv = (IInvariantDeclaration) declDefs[0];
		assertEquals(IElement.INVARIANT_DECLARATION, inv.getNodeType0());
		assertPosition(inv, 1, 13);
		
		assertVisitor(inv, 2);
	}

}
