package descent.tests.mars;

import descent.core.dom.ICompilationUnit;
import descent.core.dom.IElement;
import descent.core.dom.IExpression;
import descent.core.dom.IPragmaDeclaration;
import descent.internal.core.dom.ParserFacade;

public class Pragma_Test extends Parser_Test {
	
	public void testOne() {
		String s = " pragma(lib, 1, 2, 3)";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IPragmaDeclaration p = (IPragmaDeclaration) declDefs[0];
		assertEquals(IElement.PRAGMA_DECLARATION, p.getNodeType0());
		
		assertPosition(p, 1, s.length() - 1);
		
		assertEquals("lib", p.getName().toString());
		assertPosition(p.getName(), 8, 3);
		
		assertEquals(3, p.arguments().size());
	}

}
