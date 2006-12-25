package descent.tests.mars;

import descent.core.dom.ASTNode;
import descent.core.dom.Import;
import descent.core.dom.ImportDeclaration;
import descent.core.dom.QualifiedName;
import descent.core.dom.SelectiveImport;
import descent.core.dom.SimpleName;

public class Import_Test extends Parser_Test {
	
	public void testImportSingle() throws Exception {
		String s = " import a; ";
		ImportDeclaration importDeclaration = (ImportDeclaration) getSingleDeclarationNoProblems(s);
		assertEquals(ASTNode.IMPORT_DECLARATION, importDeclaration.getNodeType());
		assertFalse(importDeclaration.isStatic());
		assertPosition(importDeclaration, 1, 9);
		
		assertEquals(1, importDeclaration.imports().size());
		
		Import anImport = importDeclaration.imports().get(0);
		assertEquals(ASTNode.IMPORT, anImport.getNodeType());
		assertPosition(anImport, 8, 1);
		
		SimpleName name = (SimpleName) anImport.getName();
		assertEquals("a", name.getIdentifier());
		assertPosition(name, 8, 1);
	}
	
	public void testImportMultiple() throws Exception {
		String s = " import a, b, c; ";
		ImportDeclaration importDeclaration = (ImportDeclaration) getSingleDeclarationNoProblems(s);
		assertEquals(ASTNode.IMPORT_DECLARATION, importDeclaration.getNodeType());
		assertFalse(importDeclaration.isStatic());
		assertPosition(importDeclaration, 1, 15);
		
		assertEquals(3, importDeclaration.imports().size());
	}
	
	public void testImportQualified() throws Exception {
		String s = " import uno.dos.tres; ";
		ImportDeclaration importDeclaration = (ImportDeclaration) getSingleDeclarationNoProblems(s);
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
		ImportDeclaration importDeclaration = (ImportDeclaration) getSingleDeclarationNoProblems(s);
		assertPosition(importDeclaration, 1, 29);
		
		assertEquals(1, importDeclaration.imports().size());
		
		Import anImport = importDeclaration.imports().get(0);
		assertPosition(anImport, 8, 21);
		
		QualifiedName qualifiedName = (QualifiedName) anImport.getName();
		assertEquals("uno.dos.tres", qualifiedName.getFullyQualifiedName());
		assertPosition(qualifiedName, 17, 12);
		
		SimpleName alias = anImport.getAlias();
		assertEquals("mAlias", alias.getIdentifier());
		assertPosition(alias, 8, 6);
	}
	
	public void testImportSelective() throws Exception {
		String s = " import uno : dos; ";
		ImportDeclaration importDeclaration = (ImportDeclaration) getSingleDeclarationNoProblems(s);
		assertPosition(importDeclaration, 1, 17);
		
		assertEquals(1, importDeclaration.imports().size());
		
		Import imp = importDeclaration.imports().get(0);
		assertPosition(imp, 8, 9);
		
		SimpleName name = (SimpleName) imp.getName();
		assertEquals("uno", name.getIdentifier());
		assertPosition(name, 8, 3);
		
		assertEquals(1, imp.selectiveImports().size());
		
		SelectiveImport sel = imp.selectiveImports().get(0);
		assertEquals("dos", sel.getName().getIdentifier());
		assertPosition(sel.getName(), 14, 3);
		assertNull(sel.getAlias());
	}
	
	public void testImportSelectiveWithAlias() throws Exception {
		String s = " import uno : mAlias = dos; ";
		ImportDeclaration importDeclaration = (ImportDeclaration) getSingleDeclarationNoProblems(s);
		assertPosition(importDeclaration, 1, 26);
		
		assertEquals(1, importDeclaration.imports().size());
		
		Import imp = importDeclaration.imports().get(0);
		assertPosition(imp, 8, 18);
		
		SimpleName qName = (SimpleName) imp.getName();
		assertEquals("uno", qName.getIdentifier());
		assertPosition(qName, 8, 3);
		
		assertEquals(1, imp.selectiveImports().size());
		
		SelectiveImport sel = imp.selectiveImports().get(0);
		assertEquals("dos", sel.getName().getIdentifier());
		assertPosition(sel.getName(), 23, 3);
		
		assertEquals("mAlias", sel.getAlias().getIdentifier());
		assertPosition(sel.getAlias(), 14, 6);
	}
	
	public void testStaticImport() throws Exception {
		String s = " static import a;";
		ImportDeclaration importDeclaration = (ImportDeclaration) getSingleDeclarationNoProblems(s);
		assertEquals(ASTNode.IMPORT_DECLARATION, importDeclaration.getNodeType());
		assertTrue(importDeclaration.isStatic());
		assertPosition(importDeclaration, 1, s.length() - 1);
		
		assertEquals(1, importDeclaration.imports().size());
		
		Import imp = importDeclaration.imports().get(0);
		assertEquals(ASTNode.IMPORT, imp.getNodeType());
		assertPosition(imp, 15, 1);
		
		SimpleName qName = (SimpleName) imp.getName();
		assertEquals("a", qName.getIdentifier());
		assertPosition(qName, 15, 1);
	}

}
