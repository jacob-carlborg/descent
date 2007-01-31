package descent.tests.mars;

import java.util.List;

import descent.core.dom.ASTNode;
import descent.core.dom.AggregateDeclaration;
import descent.core.dom.BaseClass;
import descent.core.dom.SimpleName;
import descent.core.dom.SimpleType;
import descent.core.dom.Modifier.ModifierKeyword;

public class Class_Test extends Parser_Test {
	
	public void testEmpty() {
		String s = " class Clazz { }";
		AggregateDeclaration c = (AggregateDeclaration) getSingleDeclarationNoProblems(s);
		assertEquals(ASTNode.AGGREGATE_DECLARATION, c.getNodeType());
		assertEquals(AggregateDeclaration.Kind.CLASS, c.getKind());
		assertPosition(c, 1, 15);
		
		SimpleName name = c.getName();
		assertEquals(ASTNode.SIMPLE_NAME, name.getNodeType());
		assertEquals("Clazz", name.getIdentifier());
		assertPosition(name, 7, 5);
		
		assertEquals(0, c.baseClasses().size());
		
		assertTrue(c.templateParameters().size() == 0);
	}
	
	public void testSemicolon() {
		String s = " class Clazz;";
		AggregateDeclaration c = (AggregateDeclaration) getSingleDeclarationNoProblems(s);
		assertPosition(c, 1, 12);
	}
	
	public void testBaseClasses() {
		String s = " class Clazz : None, private Private, package Package, protected Protected, public Public { }";
		AggregateDeclaration c = (AggregateDeclaration) getSingleDeclarationNoProblems(s);
		List<BaseClass> bs = c.baseClasses();
		assertEquals(5, bs.size());
		
		assertEquals(ASTNode.BASE_CLASS, bs.get(0).getNodeType());
		assertNull(bs.get(0).getModifier());
		assertEquals(ModifierKeyword.PRIVATE_KEYWORD, bs.get(1).getModifier().getModifierKeyword());
		assertEquals(ModifierKeyword.PACKAGE_KEYWORD, bs.get(2).getModifier().getModifierKeyword());
		assertEquals(ModifierKeyword.PROTECTED_KEYWORD, bs.get(3).getModifier().getModifierKeyword());
		assertEquals(ModifierKeyword.PUBLIC_KEYWORD, bs.get(4).getModifier().getModifierKeyword());
		
		assertEquals("None", ((SimpleType) bs.get(0).getType()).getName().getFullyQualifiedName());
		assertPosition(bs.get(0).getType(), 15, 4);
		assertPosition(bs.get(0), 15, 4);
	}
	
	public void testWithMembers() {
		String s = " class Clazz { int x; }";
		AggregateDeclaration c = (AggregateDeclaration) getSingleDeclarationNoProblems(s);
		assertEquals(1, c.declarations().size());
	}
	
	public void testClassAlias() {
		String s = " alias class Clazz { int x; }";
		AggregateDeclaration c = (AggregateDeclaration) getSingleDeclarationNoProblems(s);
		assertEquals(1, c.declarations().size());
	}

}
