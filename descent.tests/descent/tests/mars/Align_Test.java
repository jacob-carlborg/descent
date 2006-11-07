package descent.tests.mars;

import descent.core.dom.IAlignDeclaration;
import descent.core.dom.ICompilationUnit;
import descent.core.dom.IDElement;
import descent.internal.core.dom.ParserFacade;

public class Align_Test extends Parser_Test {
	
	public void test() {
		String s = " align(4): class Pub { }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IDElement[] declDefs = unit.getDeclarationDefinitions();
		
		IAlignDeclaration align;
		
		align = (IAlignDeclaration) declDefs[0];
		assertEquals(IDElement.ALIGN_DECLARATION, align.getElementType());
		assertEquals(4, align.getAlign());
		
		assertEquals(1, align.getDeclarationDefinitions().length);
		
		assertVisitor(align, 3);
	}

}
