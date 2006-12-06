package descent.tests.mars;

import descent.core.dom.ICompilationUnit;
import descent.core.dom.IElement;
import descent.core.dom.IImport;
import descent.core.dom.IImportDeclaration;
import descent.core.dom.ISimpleName;
import descent.core.dom.IQualifiedName;
import descent.core.dom.ISelectiveImport;
import descent.internal.core.dom.ParserFacade;

public class Import_Test extends Parser_Test {
	
	public void testImportSingle() throws Exception {
		String s = " import a; ";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IImportDeclaration impDecl = (IImportDeclaration) declDefs[0];
		assertEquals(IElement.IMPORT_DECLARATION, impDecl.getNodeType0());
		assertFalse(impDecl.isStatic());
		assertPosition(impDecl, 1, 9);
		
		IImport[] imps = impDecl.imports().toArray(new IImport[impDecl.imports().size()]);
		assertEquals(1, imps.length);
		
		IImport imp = imps[0];
		assertEquals(IElement.IMPORT, imp.getNodeType0());
		assertPosition(imp, 8, 1);
		
		IQualifiedName qName = imp.getQualifiedName();
		assertEquals(IElement.QUALIFIED_NAME, qName.getNodeType0());
		assertEquals("a", qName.toString());
		assertPosition(qName, 8, 1);
		
		assertVisitor(impDecl, 3);
	}
	
	public void testImportMany() throws Exception {
		String s = " import uno.dos.tres; ";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IImportDeclaration impDecl = (IImportDeclaration) declDefs[0];
		assertPosition(impDecl, 1, 20);
		
		IImport[] imps = impDecl.imports().toArray(new IImport[impDecl.imports().size()]);
		assertEquals(1, imps.length);
		
		IImport imp = imps[0];
		assertPosition(imp, 8, 12);
		
		IQualifiedName qName = imp.getQualifiedName();
		assertEquals("uno.dos.tres", qName.toString());
		assertPosition(qName, 8, 12);
		
		assertVisitor(impDecl, 3);
	}
	
	public void testImportAlias() throws Exception {
		String s = " import mAlias = uno.dos.tres; ";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IImportDeclaration impDecl = (IImportDeclaration) declDefs[0];
		assertPosition(impDecl, 1, 29);
		
		IImport[] imps = impDecl.imports().toArray(new IImport[impDecl.imports().size()]);
		assertEquals(1, imps.length);
		
		IImport imp = imps[0];
		assertPosition(imp, 8, 21);
		
		IQualifiedName qName = imp.getQualifiedName();
		assertEquals("uno.dos.tres", qName.toString());
		assertPosition(qName, 17, 12);
		
		ISimpleName alias = imp.getAlias();
		assertEquals("mAlias", alias.toString());
		assertPosition(alias, 8, 6);
		
		assertVisitor(impDecl, 4);
	}
	
	public void testImportSelective() throws Exception {
		String s = " import uno : dos; ";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IImportDeclaration impDecl = (IImportDeclaration) declDefs[0];
		assertPosition(impDecl, 1, 17);
		
		IImport[] imps = impDecl.imports().toArray(new IImport[impDecl.imports().size()]);
		assertEquals(1, imps.length);
		
		IImport imp = imps[0];
		assertPosition(imp, 8, 9);
		
		IQualifiedName qName = imp.getQualifiedName();
		assertEquals("uno", qName.toString());
		assertPosition(qName, 8, 3);
		
		ISelectiveImport[] sels = imp.getSelectiveImports();
		assertEquals(1, sels.length);
		
		ISelectiveImport sel = sels[0];
		assertEquals(IElement.SELECTIVE_IMPORT, sel.getNodeType0());
		assertEquals("dos", sel.getName().toString());
		assertPosition(sel.getName(), 14, 3);
		assertNull(sel.getAlias());
		
		assertVisitor(impDecl, 5);
	}
	
	public void testImportSelectiveWithAlias() throws Exception {
		String s = " import uno : mAlias = dos; ";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IImportDeclaration impDecl = (IImportDeclaration) declDefs[0];
		assertPosition(impDecl, 1, 26);
		
		IImport[] imps = impDecl.imports().toArray(new IImport[impDecl.imports().size()]);
		assertEquals(1, imps.length);
		
		IImport imp = imps[0];
		assertPosition(imp, 8, 18);
		
		IQualifiedName qName = imp.getQualifiedName();
		assertEquals("uno", qName.toString());
		assertPosition(qName, 8, 3);
		
		ISelectiveImport[] sels = imp.getSelectiveImports();
		assertEquals(1, sels.length);
		
		ISelectiveImport sel = sels[0];
		assertEquals("dos", sel.getName().toString());
		assertPosition(sel.getName(), 23, 3);
		
		assertEquals("mAlias", sel.getAlias().toString());
		assertPosition(sel.getAlias(), 14, 6);
		
		assertVisitor(impDecl, 6);
	}
	
	public void testStaticImport() throws Exception {
		String s = " static import a;";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IImportDeclaration impDecl = (IImportDeclaration) declDefs[0];
		assertEquals(IElement.IMPORT_DECLARATION, impDecl.getNodeType0());
		assertTrue(impDecl.isStatic());
		assertPosition(impDecl, 1, s.length() - 1);
		
		IImport[] imps = impDecl.imports().toArray(new IImport[impDecl.imports().size()]);
		assertEquals(1, imps.length);
		
		IImport imp = imps[0];
		assertEquals(IElement.IMPORT, imp.getNodeType0());
		assertPosition(imp, 15, 1);
		
		IQualifiedName qName = imp.getQualifiedName();
		assertEquals(IElement.QUALIFIED_NAME, qName.getNodeType0());
		assertEquals("a", qName.toString());
		assertPosition(qName, 15, 1);
		
		assertVisitor(impDecl, 3);
	}

}
