package descent.tests.mars;

import descent.core.dom.ICompilationUnit;
import descent.core.dom.IElement;
import descent.core.dom.IMixinDeclaration;
import descent.internal.core.dom.NumberLiteral;
import descent.internal.core.dom.ParserFacade;

public class Mixin_Test extends Parser_Test {
	
	public void testOne() {
		String s = " mixin Foo!(int, real) m;";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		assertEquals(0, unit.getProblems().length);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IMixinDeclaration m = (IMixinDeclaration) declDefs[0];
		assertEquals(IElement.MIXIN_DECLARATION, m.getNodeType0());
		
		assertEquals("m", m.getName().toString());
		assertPosition(m.getName(), s.length() - 2, 1);
		
		assertPosition(m, 1, s.length() - 1);
		
		IElement[] args = m.getTemplateArguments();
		assertEquals(2, args.length);
		assertEquals("int", args[0].toString());
		assertEquals("real", args[1].toString());
		
		assertEquals("Foo", m.getType().toString());
		assertPosition(m.getType(), 7, 3);
	}
	
	public void testTwo() {
		String s = " mixin a.b.Foo!(int, real) m;";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		assertEquals(0, unit.getProblems().length);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IMixinDeclaration m = (IMixinDeclaration) declDefs[0];
		assertEquals(IElement.MIXIN_DECLARATION, m.getNodeType0());
		
		assertEquals("m", m.getName().toString());
		assertPosition(m.getName(), s.length() - 2, 1);
		
		assertPosition(m, 1, s.length() - 1);
		
		assertEquals("a.b.Foo", m.getType().toString());
		assertPosition(m.getType(), 7, 7);
	}
	
	// TODO fix
	/*
	public void testDot() {
		String s = " mixin .Foo!(int, real) m;";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IMixinDeclaration m = (IMixinDeclaration) declDefs[0];
		assertEquals(IElement.MIXIN_DECLARATION, m.getNodeType0());
		
		assertEquals("m", m.getName().toString());
		assertPosition(m.getName(), s.length() - 2, 1);
		
		assertPosition(m, 1, s.length() - 1);
		
		assertEquals(".Foo", m.getType().toString());
		assertPosition(m.getType(), 7, 4);
	}
	*/
	
	public void testTypeof() {
		String s = " mixin typeof(2).Foo!(int, real) m;";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		assertEquals(0, unit.getProblems().length);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IMixinDeclaration m = (IMixinDeclaration) declDefs[0];
		assertEquals(IElement.MIXIN_DECLARATION, m.getNodeType0());
		
		assertEquals("m", m.getName().toString());
		assertPosition(m.getName(), s.length() - 2, 1);
		
		assertPosition(m, 1, s.length() - 1);
		
		assertEquals("Foo", m.getType().toString());
		assertPosition(m.getType(), 17, 3);
		
		assertEquals("2", ((NumberLiteral) m.getTypeofType().getExpression()).getToken());
	}
	
	public void testExpressionParameter() {
		String s = " mixin Foo!(2) m;";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		assertEquals(0, unit.getProblems().length);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IMixinDeclaration m = (IMixinDeclaration) declDefs[0];
		assertEquals(IElement.MIXIN_DECLARATION, m.getNodeType0());
		
		IElement[] params = m.getTemplateArguments();
		assertEquals(1, params.length);
		assertEquals("2", ((NumberLiteral) params[0]).getToken());
	}
	
	public void testWithoutNot() {
		String s = " mixin Foo m;";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		assertEquals(0, unit.getProblems().length);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IMixinDeclaration m = (IMixinDeclaration) declDefs[0];
		assertEquals(IElement.MIXIN_DECLARATION, m.getNodeType0());
		
		assertEquals("m", m.getName().toString());
		assertPosition(m.getName(), s.length() - 2, 1);
		
		assertPosition(m, 1, s.length() - 1);
		
		assertEquals("Foo", m.getType().toString());
		assertPosition(m.getType(), 7, 3);
		
		assertEquals(0, m.getTemplateArguments().length);
	}
	
	// TODO: what to do with this?
	public void testDotAfterTemplate() {
		String s = " mixin Foo!().bar m;";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		assertEquals(0, unit.getProblems().length);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IMixinDeclaration m = (IMixinDeclaration) declDefs[0];
		assertEquals(IElement.MIXIN_DECLARATION, m.getNodeType0());
		
		assertEquals("m", m.getName().toString());
		assertPosition(m.getName(), s.length() - 2, 1);
		
		assertPosition(m, 1, s.length() - 1);
		
		assertEquals("Foo.bar", m.getType().toString());
		assertPosition(m.getType(), 7, 10);
		
		assertEquals(0, m.getTemplateArguments().length);
	}

}
