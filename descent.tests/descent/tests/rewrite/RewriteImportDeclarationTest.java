package descent.tests.rewrite;

import descent.core.dom.Import;
import descent.core.dom.ImportDeclaration;
import descent.core.dom.Modifier;
import descent.core.dom.SelectiveImport;

public class RewriteImportDeclarationTest extends AbstractRewriteTest {
	
	public void testAddPreDDoc() throws Exception {
		begin(" import one.two.three;");
		
		ImportDeclaration imp = (ImportDeclaration) unit.declarations().get(0);
		imp.preDDocs().add(ast.newDDocComment("/** Some comment */\n"));
		
		assertEqualsTokenByToken("/** Some comment */ import one.two.three;", end());
	}
	
	public void testAddModifiers() throws Exception {
		begin(" import one.two.three;");
		
		ImportDeclaration imp = (ImportDeclaration) unit.declarations().get(0);
		imp.modifiers().add(ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));
		imp.modifiers().add(ast.newModifier(Modifier.ModifierKeyword.ABSTRACT_KEYWORD));
		
		assertEqualsTokenByToken("public abstract import one.two.three;", end());
	}
	
	public void testRemoveModifier() throws Exception {
		begin(" public abstract import one.two.three;");
		
		ImportDeclaration imp = (ImportDeclaration) unit.declarations().get(0);
		imp.modifiers().get(0).delete();
		
		assertEqualsTokenByToken("abstract import one.two.three;", end());
	}
	
	public void testAddStatic() throws Exception {
		begin(" import one.two.three;");
		
		ImportDeclaration imp = (ImportDeclaration) unit.declarations().get(0);
		imp.setStatic(true);
		
		assertEqualsTokenByToken("static import one.two.three;", end());
	}
	
	public void testRemoveStatic() throws Exception {
		begin(" static import one.two.three;");
		
		ImportDeclaration imp = (ImportDeclaration) unit.declarations().get(0);
		imp.setStatic(false);
		
		assertEqualsTokenByToken("import one.two.three;", end());
	}
	
	public void testAddImport() throws Exception {
		begin(" import one.two.three;");
		
		ImportDeclaration imp = (ImportDeclaration) unit.declarations().get(0);
		
		Import i = ast.newImport();
		i.setName(ast.newSimpleName("four"));
		imp.imports().add(i);
		
		assertEqualsTokenByToken("import one.two.three, four;", end());
	}
	
	public void testRemoveImport() throws Exception {
		begin(" import one.two.three, four;");
		
		ImportDeclaration imp = (ImportDeclaration) unit.declarations().get(0);
		imp.imports().get(0).delete();
		
		assertEqualsTokenByToken("import four;", end());
	}
	
	public void testAddAlias() throws Exception {
		begin(" import one.two.three;");
		
		ImportDeclaration imp = (ImportDeclaration) unit.declarations().get(0);
		
		imp.imports().get(0).setAlias(ast.newSimpleName("theAlias"));
		
		assertEqualsTokenByToken("import theAlias = one.two.three;", end());
	}
	
	public void testRemoveAlias() throws Exception {
		begin(" import theAlias = one.two.three;");
		
		ImportDeclaration imp = (ImportDeclaration) unit.declarations().get(0);
		imp.imports().get(0).getAlias().delete();
		
		assertEqualsTokenByToken("import one.two.three;", end());
	}
	
	public void testAddSelectiveImport() throws Exception {
		begin(" import one.two.three;");
		
		ImportDeclaration imp = (ImportDeclaration) unit.declarations().get(0);
		
		SelectiveImport sel = ast.newSelectiveImport();
		sel.setName(ast.newSimpleName("selective"));
		
		imp.imports().get(0).selectiveImports().add(sel);
		
		assertEqualsTokenByToken("import one.two.three : selective;", end());
	}
	
	public void testRemoveSelectiveImport() throws Exception {
		begin(" import one.two.three : selective;");
		
		ImportDeclaration imp = (ImportDeclaration) unit.declarations().get(0);
		imp.imports().get(0).selectiveImports().clear();
		
		assertEqualsTokenByToken("import one.two.three;", end());
	}
	
	public void testAddAliasToSelectiveImport() throws Exception {
		begin(" import one.two.three : selective;");
		
		ImportDeclaration imp = (ImportDeclaration) unit.declarations().get(0);
		imp.imports().get(0).selectiveImports().get(0).setAlias(ast.newSimpleName("theAlias"));
		
		assertEqualsTokenByToken("import one.two.three : theAlias = selective;", end());
	}
	
	public void testRemoveAliasFromSelectiveImport() throws Exception {
		begin(" import one.two.three : theAlias = selective;");
		
		ImportDeclaration imp = (ImportDeclaration) unit.declarations().get(0);
		imp.imports().get(0).selectiveImports().get(0).getAlias().delete();
		
		assertEqualsTokenByToken("import one.two.three : selective;", end());
	}
	
	public void testAddPostDDoc() throws Exception {
		begin(" import one.two.three;");
		
		ImportDeclaration imp = (ImportDeclaration) unit.declarations().get(0);
		imp.setPostDDoc(ast.newDDocComment("/// hello!"));
		
		assertEqualsTokenByToken("import one.two.three; /// hello!", end());
	}
	
	public void testRemovePostDDoc() throws Exception {
		begin(" import one.two.three; /// hello!");
		
		ImportDeclaration imp = (ImportDeclaration) unit.declarations().get(0);
		imp.getPostDDoc().delete();
		
		assertEqualsTokenByToken("import one.two.three;", end());
	}

}
