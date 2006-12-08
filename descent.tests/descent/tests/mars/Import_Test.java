package descent.tests.mars;

import descent.core.dom.ICompilationUnit;
import descent.core.dom.IElement;
import descent.internal.core.dom.Import;
import descent.internal.core.dom.ImportDeclaration;
import descent.internal.core.dom.ParserFacade;
import descent.internal.core.dom.QualifiedName;
import descent.internal.core.dom.SelectiveImport;
import descent.internal.core.dom.SimpleName;

public class Import_Test extends Parser_Test {
	
	public void testImportSingle() throws Exception {
		String s = " import a; ";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		
		IElement[] declarations = unit.getDeclarationDefinitions();
		assertEquals(1, declarations.length);
		
		ImportDeclaration importDeclaration = (ImportDeclaration) declarations[0];
		assertEquals(IElement.IMPORT_DECLARATION, importDeclaration.getNodeType0());
		assertFalse(importDeclaration.isStatic());
		assertPosition(importDeclaration, 1, 9);
		
		assertEquals(1, importDeclaration.imports().size());
		
		Import anImport = importDeclaration.imports().get(0);
		assertEquals(IElement.IMPORT, anImport.getNodeType0());
		assertPosition(anImport, 8, 1);
		
		SimpleName name = (SimpleName) anImport.getName();
		assertEquals("a", name.getIdentifier());
		assertEquals("a", name.toString());
		assertPosition(name, 8, 1);
	}
	
	public void testImportMultiple() throws Exception {
		String s = " import a, b, c; ";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		
		IElement[] declarations = unit.getDeclarationDefinitions();
		assertEquals(1, declarations.length);
		
		ImportDeclaration importDeclaration = (ImportDeclaration) declarations[0];
		assertEquals(IElement.IMPORT_DECLARATION, importDeclaration.getNodeType0());
		assertFalse(importDeclaration.isStatic());
		assertPosition(importDeclaration, 1, 15);
		
		assertEquals(3, importDeclaration.imports().size());
	}
	
	public void testImportQualified() throws Exception {
		String s = " import uno.dos.tres; ";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		ImportDeclaration importDeclaration = (ImportDeclaration) declDefs[0];
		assertPosition(importDeclaration, 1, 20);
		
		assertEquals(1, importDeclaration.imports().size());
		
		Import anImport = importDeclaration.imports().get(0);
		assertPosition(anImport, 8, 12);
		
		QualifiedName qualifiedName = (QualifiedName) anImport.getName();
		assertEquals("uno.dos.tres", qualifiedName.getFullyQualifiedName());
		assertPosition(qualifiedName, 8, 12);
	}
	
	public void testImportAlias() throws Exception {
		String s = " import mAlias = uno.dos.tres; ";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		ImportDeclaration importDeclaration = (ImportDeclaration) declDefs[0];
		assertPosition(importDeclaration, 1, 29);
		
		assertEquals(1, importDeclaration.imports().size());
		
		Import anImport = importDeclaration.imports().get(0);
		assertPosition(anImport, 8, 21);
		
		QualifiedName qualifiedName = (QualifiedName) anImport.getName();
		assertEquals("uno.dos.tres", qualifiedName.getFullyQualifiedName());
		assertPosition(qualifiedName, 17, 12);
		
		SimpleName alias = anImport.getAlias();
		assertEquals("mAlias", alias.toString());
		assertPosition(alias, 8, 6);
	}
	
	public void testImportSelective() throws Exception {
		String s = " import uno : dos; ";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		ImportDeclaration impDecl = (ImportDeclaration) declDefs[0];
		assertPosition(impDecl, 1, 17);
		
		assertEquals(1, impDecl.imports().size());
		
		Import imp = impDecl.imports().get(0);
		assertPosition(imp, 8, 9);
		
		SimpleName name = (SimpleName) imp.getName();
		assertEquals("uno", name.toString());
		assertPosition(name, 8, 3);
		
		assertEquals(1, imp.selectiveImports().size());
		
		SelectiveImport sel = imp.selectiveImports().get(0);
		assertEquals("dos", sel.getName().toString());
		assertPosition(sel.getName(), 14, 3);
		assertNull(sel.getAlias());
	}
	
	public void testImportSelectiveWithAlias() throws Exception {
		String s = " import uno : mAlias = dos; ";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		ImportDeclaration impDecl = (ImportDeclaration) declDefs[0];
		assertPosition(impDecl, 1, 26);
		
		assertEquals(1, impDecl.imports().size());
		
		Import imp = impDecl.imports().get(0);
		assertPosition(imp, 8, 18);
		
		SimpleName qName = (SimpleName) imp.getName();
		assertEquals("uno", qName.toString());
		assertPosition(qName, 8, 3);
		
		assertEquals(1, imp.selectiveImports().size());
		
		SelectiveImport sel = imp.selectiveImports().get(0);
		assertEquals("dos", sel.getName().toString());
		assertPosition(sel.getName(), 23, 3);
		
		assertEquals("mAlias", sel.getAlias().toString());
		assertPosition(sel.getAlias(), 14, 6);
	}
	
	public void testStaticImport() throws Exception {
		String s = " static import a;";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		ImportDeclaration impDecl = (ImportDeclaration) declDefs[0];
		assertEquals(IElement.IMPORT_DECLARATION, impDecl.getNodeType0());
		assertTrue(impDecl.isStatic());
		assertPosition(impDecl, 1, s.length() - 1);
		
		assertEquals(1, impDecl.imports().size());
		
		Import imp = impDecl.imports().get(0);
		assertEquals(IElement.IMPORT, imp.getNodeType0());
		assertPosition(imp, 15, 1);
		
		SimpleName qName = (SimpleName) imp.getName();
		assertEquals("a", qName.toString());
		assertPosition(qName, 15, 1);
	}

}
