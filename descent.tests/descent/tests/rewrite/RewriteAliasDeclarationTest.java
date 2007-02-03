package descent.tests.rewrite;

import descent.core.dom.AliasDeclaration;
import descent.core.dom.AliasDeclarationFragment;
import descent.core.dom.Modifier;
import descent.core.dom.PrimitiveType;
import descent.core.dom.rewrite.ListRewrite;

public class RewriteAliasDeclarationTest extends RewriteTest {
	
	public void testAddPreDDoc() throws Exception {
		begin(" alias int bla;");
		
		AliasDeclaration alias = (AliasDeclaration) unit.declarations().get(0);
		ListRewrite lrw = rewriter.getListRewrite(alias, AliasDeclaration.PRE_D_DOCS_PROPERTY);
		lrw.insertFirst(ast.newDDocComment("/** Some comment */\n"), null);
		
		assertEqualsTokenByToken("/** Some comment */ alias int bla;", end());
	}
	
	public void testAddModifiers() throws Exception {
		begin(" alias int bla;");
		
		AliasDeclaration alias = (AliasDeclaration) unit.declarations().get(0);
		
		ListRewrite lrw = rewriter.getListRewrite(alias, AliasDeclaration.MODIFIERS_PROPERTY);
		Modifier publicModifier = ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD);
		Modifier abstractModifier = ast.newModifier(Modifier.ModifierKeyword.ABSTRACT_KEYWORD);
		
		lrw.insertFirst(publicModifier, null);
		lrw.insertAfter(abstractModifier, publicModifier, null);
		
		assertEqualsTokenByToken("public abstract alias int bla;", end());
	}
	
	public void testRemoveModifier() throws Exception {
		begin(" public abstract alias int x;");
		
		AliasDeclaration alias = (AliasDeclaration) unit.declarations().get(0);
		
		ListRewrite lrw = rewriter.getListRewrite(alias, AliasDeclaration.MODIFIERS_PROPERTY);
		lrw.remove(alias.modifiers().get(0), null);
		
		assertEqualsTokenByToken("abstract alias int x;", end());
	}
	
	public void testChangeType() throws Exception {
		begin("alias int x;");
		AliasDeclaration alias = (AliasDeclaration) unit.declarations().get(0);
		rewriter.set(alias, AliasDeclaration.TYPE_PROPERTY, ast.newPrimitiveType(PrimitiveType.Code.LONG), null);
		assertEqualsTokenByToken("alias long x;", end());
	}
	
	public void testAddFragment() throws Exception {
		begin("alias int x;");
		AliasDeclaration alias = (AliasDeclaration) unit.declarations().get(0);
		
		ListRewrite lrw = rewriter.getListRewrite(alias, AliasDeclaration.FRAGMENTS_PROPERTY);
		lrw.insertAfter(ast.newAliasDeclarationFragment(ast.newSimpleName("y")), alias.fragments().get(0), null);
		
		assertEqualsTokenByToken("alias int x, y;", end());
	}
	
	public void testRemoveFragment() throws Exception {
		begin("alias int x, y;");
		AliasDeclaration alias = (AliasDeclaration) unit.declarations().get(0);
		
		ListRewrite lrw = rewriter.getListRewrite(alias, AliasDeclaration.FRAGMENTS_PROPERTY);
		lrw.remove(alias.fragments().get(0), null);
		
		assertEqualsTokenByToken("alias int y;", end());
	}
	
	public void testChangeFragmentName() throws Exception {
		begin("alias int x;");
		AliasDeclaration alias = (AliasDeclaration) unit.declarations().get(0);
		
		rewriter.set(alias.fragments().get(0), AliasDeclarationFragment.NAME_PROPERTY, ast.newSimpleName("y"), null);
		
		assertEqualsTokenByToken("alias int y;", end());
	}
	
	public void testAddPostDDoc() throws Exception {
		begin(" alias int x;");
		
		AliasDeclaration alias = (AliasDeclaration) unit.declarations().get(0);
		rewriter.set(alias, AliasDeclaration.POST_D_DOC_PROPERTY, ast.newDDocComment("/// hello!"), null);
		
		assertEqualsTokenByToken("alias int x; /// hello!", end());
	}
	
	public void testRemovePostDDoc() throws Exception {
		begin(" alias int x; /// hello!");
		
		AliasDeclaration alias = (AliasDeclaration) unit.declarations().get(0);
		rewriter.remove(alias.getPostDDoc(), null);
		
		assertEqualsTokenByToken("alias int x;", end());
	}
	
	public void testMultiChange1() throws Exception {
		begin(" alias int x;");
		
		AliasDeclaration alias = (AliasDeclaration) unit.declarations().get(0);
		ListRewrite lrw;
		
		// Add comments
		lrw = rewriter.getListRewrite(alias, AliasDeclaration.PRE_D_DOCS_PROPERTY);
		lrw.insertFirst(ast.newDDocComment("/** Some comment */\n"), null);
		
		// Add modifiers
		lrw = rewriter.getListRewrite(alias, AliasDeclaration.MODIFIERS_PROPERTY);
		Modifier publicModifier = ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD);
		Modifier abstractModifier = ast.newModifier(Modifier.ModifierKeyword.ABSTRACT_KEYWORD);
		lrw.insertFirst(publicModifier, null);
		lrw.insertAfter(abstractModifier, publicModifier, null);
		
		// Change type
		rewriter.set(alias, AliasDeclaration.TYPE_PROPERTY, ast.newPrimitiveType(PrimitiveType.Code.LONG), null);
		
		// Add fragment
		lrw = rewriter.getListRewrite(alias, AliasDeclaration.FRAGMENTS_PROPERTY);
		lrw.insertAfter(ast.newAliasDeclarationFragment(ast.newSimpleName("y")), alias.fragments().get(0), null);
		
		assertEqualsTokenByToken("/** Some comment */ public abstract alias long x, y;", end());
	}

}
