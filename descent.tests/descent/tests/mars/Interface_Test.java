package descent.tests.mars;

import java.util.List;

import descent.core.dom.IAggregateDeclaration;
import descent.core.dom.IComment;
import descent.core.dom.ICompilationUnit;
import descent.core.dom.IElement;
import descent.core.dom.ISimpleName;
import descent.internal.core.dom.AggregateDeclaration;
import descent.internal.core.dom.BaseClass;
import descent.internal.core.dom.ParserFacade;
import descent.internal.core.dom.Modifier.ModifierKeyword;

public class Interface_Test extends Parser_Test {
	
	public void testEmpty() {
		String s = " interface Clazz { }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		assertEquals(0, unit.getProblems().length);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IAggregateDeclaration c = (IAggregateDeclaration) declDefs[0];
		assertEquals(IElement.AGGREGATE_DECLARATION, c.getNodeType0());
		assertEquals(AggregateDeclaration.Kind.INTERFACE, c.getKind());
		assertPosition(c, 1, 19);
		
		ISimpleName name = c.getName();
		assertEquals(IElement.SIMPLE_NAME, name.getNodeType0());
		assertEquals("Clazz", name.getIdentifier());
		assertPosition(name, 11, 5);
		
		assertEquals(0, c.baseClasses().size());
	}
	
	public void testSemicolon() {
		String s = " interface Clazz;";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		assertEquals(0, unit.getProblems().length);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IAggregateDeclaration c = (IAggregateDeclaration) declDefs[0];
		assertPosition(c, 1, 16);
	}
	
	public void testBaseClasses() {
		String s = " interface Clazz : None, private Private, package Package, protected Protected, public Public { }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		assertEquals(0, unit.getProblems().length);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		IAggregateDeclaration c = (IAggregateDeclaration) declDefs[0];
		List<BaseClass> bs = c.baseClasses();
		assertEquals(5, bs.size());
		
		assertEquals(IElement.BASE_CLASS, bs.get(0).getNodeType0());
		assertNull(bs.get(0).getModifier());
		assertEquals(ModifierKeyword.PRIVATE_KEYWORD, bs.get(1).getModifier().getModifierKeyword());
		assertEquals(ModifierKeyword.PACKAGE_KEYWORD, bs.get(2).getModifier().getModifierKeyword());
		assertEquals(ModifierKeyword.PROTECTED_KEYWORD, bs.get(3).getModifier().getModifierKeyword());
		assertEquals(ModifierKeyword.PUBLIC_KEYWORD, bs.get(4).getModifier().getModifierKeyword());
	}
	
	public void testWithComments() {
		String s = " /** hola */ interface Clazz;";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		assertEquals(0, unit.getProblems().length);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IAggregateDeclaration c = (IAggregateDeclaration) declDefs[0];
		assertPosition(c, 13, 16);
		
		IComment[] comments = c.getComments();
		assertEquals(1, comments.length);
		assertEquals("/** hola */", comments[0].getComment());
	}

}
