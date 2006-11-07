package descent.tests.mars;

import descent.core.dom.ICompilationUnit;
import descent.core.dom.IDElement;
import descent.core.dom.IExpression;
import descent.core.dom.IPragmaDeclaration;
import descent.internal.core.dom.ParserFacade;

public class Pragma_Test extends Parser_Test {
	
	public void testOne() {
		String s = " pragma(lib, 1, 2, 3)";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IDElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IPragmaDeclaration p = (IPragmaDeclaration) declDefs[0];
		assertEquals(IDElement.PRAGMA_DECLARATION, p.getElementType());
		
		assertPosition(p, 1, s.length() - 1);
		
		assertEquals("lib", p.getIdentifier().toString());
		assertPosition(p.getIdentifier(), 8, 3);
		
		IExpression[] args = p.getArguments();
		assertEquals(3, args.length);
	}

}
