package descent.tests.mars;

import descent.core.dom.ICompilationUnit;
import descent.core.dom.IDElement;
import descent.core.dom.IMixinDeclaration;
import descent.internal.core.dom.ParserFacade;

public class Mixin_Test extends Parser_Test {
	
	public void testOne() {
		String s = " mixin Foo!(int, real) m;";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IDElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IMixinDeclaration m = (IMixinDeclaration) declDefs[0];
		assertEquals(IDElement.MIXIN_DECLARATION, m.getElementType());
		
		assertEquals("m", m.getName().toString());
		assertPosition(m.getName(), s.length() - 2, 1);
		
		assertPosition(m, 1, s.length() - 1);
		
		IDElement[] args = m.getTemplateArguments();
		assertEquals(2, args.length);
		assertEquals("int", args[0].toString());
		assertEquals("real", args[1].toString());
		
		assertEquals("Foo", m.getType().toString());
		assertPosition(m.getType(), 7, 3);
	}
	
	public void testTwo() {
		String s = " mixin a.b.Foo!(int, real) m;";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IDElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IMixinDeclaration m = (IMixinDeclaration) declDefs[0];
		assertEquals(IDElement.MIXIN_DECLARATION, m.getElementType());
		
		assertEquals("m", m.getName().toString());
		assertPosition(m.getName(), s.length() - 2, 1);
		
		assertPosition(m, 1, s.length() - 1);
		
		assertEquals("a.b.Foo", m.getType().toString());
		assertPosition(m.getType(), 7, 7);
	}

}
