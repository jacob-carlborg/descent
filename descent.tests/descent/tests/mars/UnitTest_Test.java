package descent.tests.mars;

import descent.core.dom.ICompilationUnit;
import descent.core.dom.IElement;
import descent.core.dom.IUnitTestDeclaration;
import descent.internal.core.dom.ParserFacade;

public class UnitTest_Test extends Parser_Test {
	
	public void test() {
		String s = " unittest { } ";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		
		IUnitTestDeclaration inv = (IUnitTestDeclaration) declDefs[0];
		assertEquals(IElement.UNIT_TEST_DECLARATION, inv.getNodeType0());
		assertPosition(inv, 1, 12);
	}

}
