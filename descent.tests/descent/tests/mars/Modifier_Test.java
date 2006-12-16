package descent.tests.mars;

import descent.core.dom.IAggregateDeclaration;
import descent.core.dom.ICompilationUnit;
import descent.core.dom.IElement;
import descent.core.dom.IImportDeclaration;
import descent.core.dom.IModifier;
import descent.core.dom.IModifierDeclaration;
import descent.core.dom.ITypedefDeclaration;
import descent.core.dom.IVariableDeclaration;
import descent.internal.core.dom.Modifier;
import descent.internal.core.dom.ModifierDeclaration;
import descent.internal.core.dom.ParserFacade;

public class Modifier_Test extends Parser_Test {
	
	public void testAsJava() {
		String s = " public class Clazz { }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IAggregateDeclaration agg = (IAggregateDeclaration) declDefs[0];
		assertPosition(agg, 1, 22);
	}
	
	public void testPosition_bug() {
		String s = " this() { } public int bla() { }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(2, declDefs.length);
		
		assertPosition(declDefs[0], 1, 10);
	}
	
	public void testWithoutModifiers() {
		String s = " class Clazz { }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		
		IAggregateDeclaration agg = (IAggregateDeclaration) declDefs[0];
		// TODO assertEquals(IModifier.NONE, agg.getModifier());
	}
	
	public void testSomeModifiersAsJava() {
		Object[][] objs = {
			{ "private", IModifier.PRIVATE },
			{ "package", IModifier.PACKAGE},
			{ "protected", IModifier.PROTECTED},
			{ "public", IModifier.PUBLIC },
			{ "export", IModifier.EXPORT},
		};
		
		for(Object[] pair : objs) {
			String s = pair[0] + " class Clazz { }";
			ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
			IElement[] declDefs = unit.getDeclarationDefinitions();
			
			IAggregateDeclaration agg = (IAggregateDeclaration) declDefs[0];
			// TODO assertEquals(pair[1], agg.getModifier());
		}
	}
	
	public void testSomeModifiersWithCurlies() {
		Object[][] objs = {
			{ "private", Modifier.ModifierKeyword.PRIVATE_KEYWORD },
			{ "package", Modifier.ModifierKeyword.PACKAGE_KEYWORD },
			{ "protected", Modifier.ModifierKeyword.PROTECTED_KEYWORD },
			{ "public", Modifier.ModifierKeyword.PUBLIC_KEYWORD },
			{ "export", Modifier.ModifierKeyword.EXPORT_KEYWORD },
		};
		
		for(Object[] pair : objs) {
			String s = " " + pair[0] + " { class Clazz { } }";
			ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
			IElement[] declDefs = unit.getDeclarationDefinitions();
			
			ModifierDeclaration modifierDeclaration = (ModifierDeclaration) declDefs[0];
			assertEquals(pair[1], modifierDeclaration.getModifier().getModifierKeyword());
			assertPosition(modifierDeclaration.getModifier(), 1, ((String) pair[0]).length());
			assertEquals(1, modifierDeclaration.declarations().size());
			assertEquals(ModifierDeclaration.Syntax.CURLY_BRACES, modifierDeclaration.getSyntax());
		}
	}
	
	public void testSomeModifiersWithDots() {
		Object[][] objs = {
			{ "private", IModifier.PRIVATE },
			{ "package", IModifier.PACKAGE},
			{ "protected", IModifier.PROTECTED},
			{ "public", IModifier.PUBLIC },
			{ "export", IModifier.EXPORT},
		};
		
		for(Object[] pair : objs) {
			String s = pair[0] + ": class Clazz { }";
			ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
			IElement[] declDefs = unit.getDeclarationDefinitions();
			
			IModifierDeclaration prot = (IModifierDeclaration) declDefs[0];
			
			IAggregateDeclaration agg = (IAggregateDeclaration) prot.declarations().get(0);
			// TODO assertEquals(pair[1], agg.getModifier());
		}
	}
	
	public void testSomeModifiersWithDots2() {
		String s = "public: class Pub { } private: class Priv { }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		
		IModifierDeclaration prot;
		IAggregateDeclaration agg;
		
		prot = (IModifierDeclaration) declDefs[0];
		agg = (IAggregateDeclaration) prot.declarations().get(0);
		// TODO assertEquals(IModifier.PUBLIC, agg.getModifier());
		
		prot = (IModifierDeclaration) prot.declarations().get(1);
		agg = (IAggregateDeclaration) prot.declarations().get(0);
		// TODO assertEquals(IModifier.PRIVATE, agg.getModifier());
	}
	
	public void testAbstract() {
		String s = " abstract class Pub { }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		
		IAggregateDeclaration agg = (IAggregateDeclaration) declDefs[0];
		// TODO assertEquals(IModifier.ABSTRACT, agg.getModifier());
	}
	
	public void testAbstract2() {
		String s = " abstract { class Pub { } }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		
		IModifierDeclaration sto = (IModifierDeclaration) declDefs[0];
		assertEquals(1, sto.declarations().size());
		
		IAggregateDeclaration agg = (IAggregateDeclaration) sto.declarations().get(0);
		// TODO assertEquals(IModifier.ABSTRACT, agg.getModifier());
	}
	
	public void testOnImport() {
		String s = " private import x;";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		
		IImportDeclaration decl = (IImportDeclaration) declDefs[0];
		// TODO assertEquals(IModifier.PRIVATE, decl.getModifier());
	}
	
	public void testOnVar1() {
		String s = " private int x;";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		
		IVariableDeclaration var = (IVariableDeclaration) declDefs[0];
		// TODO assertEquals(IModifier.PRIVATE, var.getModifier());
	}
	
	public void testOnVar2() {
		String s = " const int x;";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		
		IVariableDeclaration var = (IVariableDeclaration) declDefs[0];
		// TODO assertEquals(IModifier.CONST, var.getModifier());
	}
	
	public void testOnTypedef() {
		String s = " const typedef int Bla;";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		
		ITypedefDeclaration var = (ITypedefDeclaration) declDefs[0];
		// TODO assertEquals(IModifier.CONST, var.getModifier());
	}

}
