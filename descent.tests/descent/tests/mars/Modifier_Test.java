package descent.tests.mars;

import java.util.Arrays;
import java.util.List;

import descent.core.dom.AliasDeclaration;
import descent.core.dom.Declaration;
import descent.core.dom.Modifier;
import descent.core.dom.ModifierDeclaration;

public class Modifier_Test extends Parser_Test {
	
	public void testWithoutModifiers() {
		String s = " alias int Bla;";
		
		AliasDeclaration aliasDeclaration = (AliasDeclaration) getSingleDeclarationNoProblems(s);
		assertPosition(aliasDeclaration, 1, s.length() - 1);
		
		assertEquals(0, aliasDeclaration.modifiers().size());
	}
	
	public void testAllModifiersAsJava() {
		Object[][] objs = {
				{ "private", Modifier.ModifierKeyword.PRIVATE_KEYWORD },
				{ "package", Modifier.ModifierKeyword.PACKAGE_KEYWORD },
				{ "protected", Modifier.ModifierKeyword.PROTECTED_KEYWORD },
				{ "public", Modifier.ModifierKeyword.PUBLIC_KEYWORD },
				{ "export", Modifier.ModifierKeyword.EXPORT_KEYWORD },
				{ "const", Modifier.ModifierKeyword.CONST_KEYWORD },
				{ "final", Modifier.ModifierKeyword.FINAL_KEYWORD },
				{ "auto", Modifier.ModifierKeyword.AUTO_KEYWORD },
				{ "scope", Modifier.ModifierKeyword.SCOPE_KEYWORD },
				{ "override", Modifier.ModifierKeyword.OVERRIDE_KEYWORD },
				{ "abstract", Modifier.ModifierKeyword.ABSTRACT_KEYWORD },
				{ "synchronized", Modifier.ModifierKeyword.SYNCHRONIZED_KEYWORD },
				{ "deprecated", Modifier.ModifierKeyword.DEPRECATED_KEYWORD },
			};
		
		for(Object[] pair : objs) {
			String s = " " + pair[0] + " alias int Bla;";
			AliasDeclaration aliasDeclaration = (AliasDeclaration) getSingleDeclarationNoProblems(s);
			assertPosition(aliasDeclaration, 1, s.length() - 1);
			
			try {
				assertEquals(1, aliasDeclaration.modifiers().size());
			} catch (Throwable t) {
				fail(Arrays.toString(pair));
			}
			
			Modifier modifier = aliasDeclaration.modifiers().get(0);
			assertEquals(pair[1], modifier.getModifierKeyword());
			assertPosition(modifier, 1, ((String) pair[0]).length());
		}
	}
	
	public void testManyProtectionModifiersAsJava() {
		String s = " private public alias int Bla;";
		AliasDeclaration aliasDeclaration = (AliasDeclaration) getSingleDeclarationNoProblems(s);
		assertPosition(aliasDeclaration, 1, s.length() - 1);
		
		assertEquals(2, aliasDeclaration.modifiers().size());
		
		Modifier modifier;
		
		modifier = aliasDeclaration.modifiers().get(0);
		assertEquals(Modifier.ModifierKeyword.PRIVATE_KEYWORD, modifier.getModifierKeyword());
		assertPosition(modifier, 1, "private".length());
		
		modifier = aliasDeclaration.modifiers().get(1);
		assertEquals(Modifier.ModifierKeyword.PUBLIC_KEYWORD, modifier.getModifierKeyword());
		assertPosition(modifier, 9, "public".length());
	}
	
	public void testProtectionAndStaticModifiersAsJava() {
		String s = " private static alias int Bla;";
		AliasDeclaration aliasDeclaration = (AliasDeclaration) getSingleDeclarationNoProblems(s);
		assertPosition(aliasDeclaration, 1, s.length() - 1);
		
		assertEquals(2, aliasDeclaration.modifiers().size());
		
		Modifier modifier;
		
		modifier = aliasDeclaration.modifiers().get(0);
		assertEquals(Modifier.ModifierKeyword.PRIVATE_KEYWORD, modifier.getModifierKeyword());
		assertPosition(modifier, 1, "private".length());
		
		modifier = aliasDeclaration.modifiers().get(1);
		assertEquals(Modifier.ModifierKeyword.STATIC_KEYWORD, modifier.getModifierKeyword());
		assertPosition(modifier, 9, "static".length());
	}
	
	public void testProtectionAndOtherModifiersAsJava() {
		String s = " private const alias int Bla;";
		AliasDeclaration aliasDeclaration = (AliasDeclaration) getSingleDeclarationNoProblems(s);
		assertPosition(aliasDeclaration, 1, s.length() - 1);
		
		assertEquals(2, aliasDeclaration.modifiers().size());
		
		Modifier modifier;
		
		modifier = aliasDeclaration.modifiers().get(0);
		assertEquals(Modifier.ModifierKeyword.PRIVATE_KEYWORD, modifier.getModifierKeyword());
		assertPosition(modifier, 1, "private".length());
		
		modifier = aliasDeclaration.modifiers().get(1);
		assertEquals(Modifier.ModifierKeyword.CONST_KEYWORD, modifier.getModifierKeyword());
		assertPosition(modifier, 9, "const".length());
	}
	
	public void testStaticAndProtectionModifiersAsJava() {
		String s = " static private alias int Bla;";
		AliasDeclaration aliasDeclaration = (AliasDeclaration) getSingleDeclarationNoProblems(s);
		assertPosition(aliasDeclaration, 1, s.length() - 1);
		
		assertEquals(2, aliasDeclaration.modifiers().size());
		
		Modifier modifier;
		
		modifier = aliasDeclaration.modifiers().get(0);
		assertEquals(Modifier.ModifierKeyword.STATIC_KEYWORD, modifier.getModifierKeyword());
		assertPosition(modifier, 1, "static".length());
		
		modifier = aliasDeclaration.modifiers().get(1);
		assertEquals(Modifier.ModifierKeyword.PRIVATE_KEYWORD, modifier.getModifierKeyword());
		assertPosition(modifier, 8, "private".length());
	}
	
	public void testOtherAndProtectionModifiersAsJava() {
		String s = " const private alias int Bla;";
		AliasDeclaration aliasDeclaration = (AliasDeclaration) getSingleDeclarationNoProblems(s);
		assertPosition(aliasDeclaration, 1, s.length() - 1);
		
		assertEquals(2, aliasDeclaration.modifiers().size());
		
		Modifier modifier;
		
		modifier = aliasDeclaration.modifiers().get(0);
		assertEquals(Modifier.ModifierKeyword.CONST_KEYWORD, modifier.getModifierKeyword());
		assertPosition(modifier, 1, "const".length());
		
		modifier = aliasDeclaration.modifiers().get(1);
		assertEquals(Modifier.ModifierKeyword.PRIVATE_KEYWORD, modifier.getModifierKeyword());
		assertPosition(modifier, 7, "private".length());
	}
	
	public void testOtherAndStaticModifiersAsJava() {
		String s = " const static alias int Bla;";
		AliasDeclaration aliasDeclaration = (AliasDeclaration) getSingleDeclarationNoProblems(s);
		assertPosition(aliasDeclaration, 1, s.length() - 1);
		
		assertEquals(2, aliasDeclaration.modifiers().size());
		
		Modifier modifier;
		
		modifier = aliasDeclaration.modifiers().get(0);
		assertEquals(Modifier.ModifierKeyword.CONST_KEYWORD, modifier.getModifierKeyword());
		assertPosition(modifier, 1, "const".length());
		
		modifier = aliasDeclaration.modifiers().get(1);
		assertEquals(Modifier.ModifierKeyword.STATIC_KEYWORD, modifier.getModifierKeyword());
		assertPosition(modifier, 7, "static".length());
	}
	
	public void testStaticAndOtherModifiersAsJava() {
		String s = " static const alias int Bla;";
		AliasDeclaration aliasDeclaration = (AliasDeclaration) getSingleDeclarationNoProblems(s);
		assertPosition(aliasDeclaration, 1, s.length() - 1);
		
		assertEquals(2, aliasDeclaration.modifiers().size());
		
		Modifier modifier;
		
		modifier = aliasDeclaration.modifiers().get(0);
		assertEquals(Modifier.ModifierKeyword.STATIC_KEYWORD, modifier.getModifierKeyword());
		assertPosition(modifier, 1, "static".length());
		
		modifier = aliasDeclaration.modifiers().get(1);
		assertEquals(Modifier.ModifierKeyword.CONST_KEYWORD, modifier.getModifierKeyword());
		assertPosition(modifier, 8, "const".length());
	}
	
	public void testModifiersWithCurlyBraces() {
		Object[][] objs = {
			{ "private", Modifier.ModifierKeyword.PRIVATE_KEYWORD },
			{ "package", Modifier.ModifierKeyword.PACKAGE_KEYWORD },
			{ "protected", Modifier.ModifierKeyword.PROTECTED_KEYWORD },
			{ "public", Modifier.ModifierKeyword.PUBLIC_KEYWORD },
			{ "export", Modifier.ModifierKeyword.EXPORT_KEYWORD },
			{ "const", Modifier.ModifierKeyword.CONST_KEYWORD },
			{ "final", Modifier.ModifierKeyword.FINAL_KEYWORD },
			{ "auto", Modifier.ModifierKeyword.AUTO_KEYWORD },
			{ "scope", Modifier.ModifierKeyword.SCOPE_KEYWORD },
			{ "override", Modifier.ModifierKeyword.OVERRIDE_KEYWORD },
			{ "abstract", Modifier.ModifierKeyword.ABSTRACT_KEYWORD },
			{ "synchronized", Modifier.ModifierKeyword.SYNCHRONIZED_KEYWORD },
			{ "deprecated", Modifier.ModifierKeyword.DEPRECATED_KEYWORD },
		};
		
		for(Object[] pair : objs) {
			String s = " " + pair[0] + " { class Clazz { } }";
			ModifierDeclaration modifierDeclaration = (ModifierDeclaration) getSingleDeclarationNoProblems(s);
			assertEquals(pair[1], modifierDeclaration.getModifier().getModifierKeyword());
			assertPosition(modifierDeclaration.getModifier(), 1, ((String) pair[0]).length());
			assertEquals(1, modifierDeclaration.declarations().size());
			assertEquals(ModifierDeclaration.Syntax.CURLY_BRACES, modifierDeclaration.getSyntax());
			assertEquals(0, modifierDeclaration.modifiers().size());
		}
	}
	
	public void testModifiersWithColon() {
		Object[][] objs = {
			{ "private", Modifier.ModifierKeyword.PRIVATE_KEYWORD },
			{ "package", Modifier.ModifierKeyword.PACKAGE_KEYWORD },
			{ "protected", Modifier.ModifierKeyword.PROTECTED_KEYWORD },
			{ "public", Modifier.ModifierKeyword.PUBLIC_KEYWORD },
			{ "export", Modifier.ModifierKeyword.EXPORT_KEYWORD },
			{ "const", Modifier.ModifierKeyword.CONST_KEYWORD },
			{ "final", Modifier.ModifierKeyword.FINAL_KEYWORD },
			{ "auto", Modifier.ModifierKeyword.AUTO_KEYWORD },
			{ "scope", Modifier.ModifierKeyword.SCOPE_KEYWORD },
			{ "override", Modifier.ModifierKeyword.OVERRIDE_KEYWORD },
			{ "abstract", Modifier.ModifierKeyword.ABSTRACT_KEYWORD },
			{ "synchronized", Modifier.ModifierKeyword.SYNCHRONIZED_KEYWORD },
			{ "deprecated", Modifier.ModifierKeyword.DEPRECATED_KEYWORD },
		};
		
		for(Object[] pair : objs) {
			String s = " " + pair[0] + ": class Clazz { }";
			ModifierDeclaration modifierDeclaration = (ModifierDeclaration) getSingleDeclarationNoProblems(s);
			assertEquals(pair[1], modifierDeclaration.getModifier().getModifierKeyword());
			assertPosition(modifierDeclaration.getModifier(), 1, ((String) pair[0]).length());
			assertEquals(1, modifierDeclaration.declarations().size());
			assertEquals(ModifierDeclaration.Syntax.COLON, modifierDeclaration.getSyntax());
			assertEquals(0, modifierDeclaration.modifiers().size());
		}
	}
	
	public void testPosition_bug() {
		String s = " this() { } public int bla() { }";
		List<Declaration> declDefs = getDeclarationsNoProblems(s);
		assertEquals(2, declDefs.size());
		
		assertPosition(declDefs.get(0), 1, 10);
	}

}
