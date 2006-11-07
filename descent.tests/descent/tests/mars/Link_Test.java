package descent.tests.mars;

import descent.core.dom.ICompilationUnit;
import descent.core.dom.IDElement;
import descent.core.dom.ILinkDeclaration;
import descent.internal.core.dom.ParserFacade;

public class Link_Test extends Parser_Test {
	
	public void test() {
		String s = " extern(C) { }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IDElement[] declDefs = unit.getDeclarationDefinitions();
		
		ILinkDeclaration link = (ILinkDeclaration) declDefs[0];
		assertEquals(IDElement.LINK_DECLARATION, link.getElementType());
		
		assertPosition(link, 1, s.length() - 1);
	}

}
