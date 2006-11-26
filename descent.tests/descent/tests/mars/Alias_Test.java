package descent.tests.mars;

import descent.core.dom.IAliasDeclaration;
import descent.core.dom.ICompilationUnit;
import descent.core.dom.IElement;
import descent.internal.core.dom.ParserFacade;

public class Alias_Test extends Parser_Test {
	
	public void test() {
		String s = " alias int Ble;";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IAliasDeclaration alias = (IAliasDeclaration) declDefs[0];
		assertEquals(IElement.ALIAS_DECLARATION, alias.getElementType());
		
		assertEquals("Ble", alias.getName().toString());
		assertPosition(alias.getName(), 11, 3);
		assertEquals("int", alias.getType().toString());
		
		assertPosition(alias, 1, 14);
		
		assertVisitor(alias, 3);
	}

}
