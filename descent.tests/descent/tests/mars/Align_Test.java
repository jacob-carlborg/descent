package descent.tests.mars;

import descent.core.dom.IAlignDeclaration;
import descent.core.dom.ICompilationUnit;
import descent.core.dom.IElement;
import descent.internal.core.dom.ParserFacade;
import descent.internal.core.dom.global;

public class Align_Test extends Parser_Test {
	
	public void testWithNumber() {
		String s = " align(4): class Pub { }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		
		IAlignDeclaration align;
		
		align = (IAlignDeclaration) declDefs[0];
		assertEquals(IElement.ALIGN_DECLARATION, align.getNodeType0());
		assertEquals(4, align.getAlign());
		
		assertEquals(1, align.declarations().size());
		
		assertVisitor(align, 3);
	}
	
	public void testWithoutNumber() {
		String s = " align: class Pub { }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		
		IAlignDeclaration align;
		
		align = (IAlignDeclaration) declDefs[0];
		assertEquals(IElement.ALIGN_DECLARATION, align.getNodeType0());
		assertEquals(global.structalign, align.getAlign());
		
		assertEquals(1, align.declarations().size());
		
		assertVisitor(align, 3);
	}

}
