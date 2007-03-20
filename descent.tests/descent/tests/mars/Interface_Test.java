package descent.tests.mars;

import java.util.List;

import descent.core.dom.ASTNode;
import descent.core.dom.AggregateDeclaration;
import descent.core.dom.BaseClass;
import descent.core.dom.SimpleName;
import descent.core.dom.Modifier.ModifierKeyword;

public class Interface_Test extends Parser_Test {
	
	public void testEmpty() {
		String s = " interface Clazz { }";
		AggregateDeclaration c = (AggregateDeclaration) getSingleDeclarationNoProblems(s);
		assertEquals(ASTNode.AGGREGATE_DECLARATION, c.getNodeType());
		assertEquals(AggregateDeclaration.Kind.INTERFACE, c.getKind());
		assertPosition(c, 1, 19);
		
		SimpleName name = c.getName();
		assertEquals(ASTNode.SIMPLE_NAME, name.getNodeType());
		assertEquals("Clazz", name.getIdentifier());
		assertPosition(name, 11, 5);
		
		assertEquals(0, c.baseClasses().size());
	}
	
	public void testSemicolon() {
		String s = " interface Clazz;";
		AggregateDeclaration c = (AggregateDeclaration) getSingleDeclarationNoProblems(s);
		assertPosition(c, 1, 16);
	}
	
	public void testBaseClasses() {
		String s = " interface Clazz : None, private Private, package Package, protected Protected, public Public { }";
		AggregateDeclaration c = (AggregateDeclaration) getSingleDeclarationNoProblems(s);
		List<BaseClass> bs = c.baseClasses();
		assertEquals(5, bs.size());
		
		assertEquals(ASTNode.BASE_CLASS, bs.get(0).getNodeType());
		assertNull(bs.get(0).getModifier());
		assertEquals(ModifierKeyword.PRIVATE_KEYWORD, bs.get(1).getModifier().getModifierKeyword());
		assertEquals(ModifierKeyword.PACKAGE_KEYWORD, bs.get(2).getModifier().getModifierKeyword());
		assertEquals(ModifierKeyword.PROTECTED_KEYWORD, bs.get(3).getModifier().getModifierKeyword());
		assertEquals(ModifierKeyword.PUBLIC_KEYWORD, bs.get(4).getModifier().getModifierKeyword());
	}

}
