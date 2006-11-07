package descent.tests.mars;

import descent.core.dom.ICompilationUnit;
import descent.core.dom.IDElement;
import descent.core.dom.IUnitTestDeclaration;
import descent.internal.core.dom.ParserFacade;

public class UnitTest_Test extends Parser_Test {
	
	public void test() {
		String s = " unittest { } ";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IDElement[] declDefs = unit.getDeclarationDefinitions();
		
		IUnitTestDeclaration inv = (IUnitTestDeclaration) declDefs[0];
		assertEquals(IDElement.UNITTEST_DECLARATION, inv.getElementType());
		assertPosition(inv, 1, 12);
	}

}
