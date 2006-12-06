package descent.tests.mars;

import descent.core.dom.IAggregateDeclaration;
import descent.core.dom.ICompilationUnit;
import descent.core.dom.IElement;
import descent.core.dom.IImport;
import descent.core.dom.IImportDeclaration;
import descent.core.dom.IModifier;
import descent.core.dom.IProtectionDeclaration;
import descent.core.dom.IStorageClassDeclaration;
import descent.core.dom.ITypedefDeclaration;
import descent.core.dom.IVariableDeclaration;
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
		assertEquals(IModifier.NONE, agg.getModifierFlags());
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
			assertEquals(pair[1], agg.getModifierFlags());
		}
	}
	
	/** TODO
	public void testSomeModifiersAsJava2() {
		String s = "public private class Priv { }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		
		IAggregateDeclaration agg = (IAggregateDeclaration) declDefs[0];
		assertEquals(IModifier.PRIVATE, agg.getModifiers());
	}
	*/
	
	public void testSomeModifiersWithCurlies() {
		Object[][] objs = {
			{ "private", IModifier.PRIVATE },
			{ "package", IModifier.PACKAGE},
			{ "protected", IModifier.PROTECTED},
			{ "public", IModifier.PUBLIC },
			{ "export", IModifier.EXPORT},
		};
		
		for(Object[] pair : objs) {
			String s = pair[0] + " { class Clazz { } }";
			ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
			IElement[] declDefs = unit.getDeclarationDefinitions();
			
			IProtectionDeclaration prot = (IProtectionDeclaration) declDefs[0];
			
			IAggregateDeclaration agg = (IAggregateDeclaration) prot.getDeclarationDefinitions()[0];
			assertEquals(pair[1], agg.getModifierFlags());
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
			
			IProtectionDeclaration prot = (IProtectionDeclaration) declDefs[0];
			
			IAggregateDeclaration agg = (IAggregateDeclaration) prot.getDeclarationDefinitions()[0];
			assertEquals(pair[1], agg.getModifierFlags());
		}
	}
	
	public void testSomeModifiersWithDots2() {
		String s = "public: class Pub { } private: class Priv { }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		
		IProtectionDeclaration prot;
		IAggregateDeclaration agg;
		
		prot = (IProtectionDeclaration) declDefs[0];
		agg = (IAggregateDeclaration) prot.getDeclarationDefinitions()[0];
		assertEquals(IModifier.PUBLIC, agg.getModifierFlags());
		
		prot = (IProtectionDeclaration) prot.getDeclarationDefinitions()[1];
		agg = (IAggregateDeclaration) prot.getDeclarationDefinitions()[0];
		assertEquals(IModifier.PRIVATE, agg.getModifierFlags());
	}
	
	public void testAbstract() {
		String s = " abstract class Pub { }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		
		IAggregateDeclaration agg = (IAggregateDeclaration) declDefs[0];
		assertEquals(IModifier.ABSTRACT, agg.getModifierFlags());
	}
	
	public void testAbstract2() {
		String s = " abstract { class Pub { } }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		
		IStorageClassDeclaration sto = (IStorageClassDeclaration) declDefs[0];
		assertEquals(1, sto.getDeclarationDefinitions().length);
		
		IAggregateDeclaration agg = (IAggregateDeclaration) sto.getDeclarationDefinitions()[0];
		assertEquals(IModifier.ABSTRACT, agg.getModifierFlags());
	}
	
	public void testOnImport() {
		String s = " private import x;";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		
		IImportDeclaration decl = (IImportDeclaration) declDefs[0];
		assertEquals(IModifier.PRIVATE, decl.getModifierFlags());
	}
	
	public void testOnVar1() {
		String s = " private int x;";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		
		IVariableDeclaration var = (IVariableDeclaration) declDefs[0];
		assertEquals(IModifier.PRIVATE, var.getModifierFlags());
	}
	
	public void testOnVar2() {
		String s = " const int x;";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		
		IVariableDeclaration var = (IVariableDeclaration) declDefs[0];
		assertEquals(IModifier.CONST, var.getModifierFlags());
	}
	
	public void testOnTypedef() {
		String s = " const typedef Bla int;";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		
		ITypedefDeclaration var = (ITypedefDeclaration) declDefs[0];
		assertEquals(IModifier.CONST, var.getModifierFlags());
	}

}
