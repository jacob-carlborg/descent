package descent.tests.rewrite;

import descent.core.dom.AlignDeclaration;
import descent.core.dom.Modifier;
import descent.core.dom.PrimitiveType;
import descent.core.dom.VariableDeclaration;
import descent.core.dom.rewrite.ListRewrite;

public class RewriteAlignDeclarationTest extends RewriteTest {
	
	public void testAddPreDDoc() throws Exception {
		begin(" align(4) { }");
		
		AlignDeclaration alias = (AlignDeclaration) unit.declarations().get(0);
		ListRewrite lrw = rewriter.getListRewrite(alias, AlignDeclaration.PRE_D_DOCS_PROPERTY);
		lrw.insertFirst(ast.newDDocComment("/** Some comment */\n"), null);
		
		assertEqualsTokenByToken("/** Some comment */ align(4) { }", end());
	}
	
	public void testAddModifiers() throws Exception {
		begin(" align(4) { }");
		
		AlignDeclaration alias = (AlignDeclaration) unit.declarations().get(0);
		
		ListRewrite lrw = rewriter.getListRewrite(alias, AlignDeclaration.MODIFIERS_PROPERTY);
		Modifier publicModifier = ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD);
		Modifier abstractModifier = ast.newModifier(Modifier.ModifierKeyword.ABSTRACT_KEYWORD);
		
		lrw.insertFirst(publicModifier, null);
		lrw.insertAfter(abstractModifier, publicModifier, null);
		
		assertEqualsTokenByToken("public abstract align(4) { }", end());
	}
	
	public void testRemoveModifier() throws Exception {
		begin(" public abstract align(4) { }");
		
		AlignDeclaration alias = (AlignDeclaration) unit.declarations().get(0);
		
		ListRewrite lrw = rewriter.getListRewrite(alias, AlignDeclaration.MODIFIERS_PROPERTY);
		lrw.remove(alias.modifiers().get(0), null);
		
		assertEqualsTokenByToken("abstract align(4) { }", end());
	}
	
	public void testChangeAlign() throws Exception {
		begin(" align(4) { }");
		
		AlignDeclaration alias = (AlignDeclaration) unit.declarations().get(0);
		rewriter.set(alias, AlignDeclaration.ALIGN_PROPERTY, 8, null);
		
		assertEqualsTokenByToken("align(8) { }", end());
	}
	
	public void testAddDeclarations() throws Exception {
		begin(" align(4) { }");
		
		AlignDeclaration agg = (AlignDeclaration) unit.declarations().get(0);
		
		VariableDeclaration var1 = ast.newVariableDeclaration(ast.newVariableDeclarationFragment(ast.newSimpleName("var1")));
		var1.setType(ast.newPrimitiveType(PrimitiveType.Code.INT));
		
		VariableDeclaration var2 = ast.newVariableDeclaration(ast.newVariableDeclarationFragment(ast.newSimpleName("var2")));
		var2.setType(ast.newPrimitiveType(PrimitiveType.Code.LONG));
		
		ListRewrite lrw = rewriter.getListRewrite(agg, AlignDeclaration.DECLARATIONS_PROPERTY);
		lrw.insertFirst(var1, null);
		lrw.insertAfter(var2, var1, null);
		
		assertEqualsTokenByToken("align(4) { int var1; long var2; }", end());
	}
	
	public void testAddDeclarationsWithComments() throws Exception {
		begin(" align(4) { int var1; // comment for var1 \n }");
		
		AlignDeclaration agg = (AlignDeclaration) unit.declarations().get(0);
	
		VariableDeclaration var2 = ast.newVariableDeclaration(ast.newVariableDeclarationFragment(ast.newSimpleName("var2")));
		var2.setType(ast.newPrimitiveType(PrimitiveType.Code.LONG));
		
		ListRewrite lrw = rewriter.getListRewrite(agg, AlignDeclaration.DECLARATIONS_PROPERTY);
		lrw.insertAfter(var2, agg.declarations().get(0), null);
		
		assertEqualsTokenByToken("align(4) { int var1; // comment for var1 \n long var2; }", end());
	}
	
	public void testAddDeclarationsToColon() throws Exception {
		begin(" align(4):");
		
		AlignDeclaration agg = (AlignDeclaration) unit.declarations().get(0);
		
		VariableDeclaration var1 = ast.newVariableDeclaration(ast.newVariableDeclarationFragment(ast.newSimpleName("var1")));
		var1.setType(ast.newPrimitiveType(PrimitiveType.Code.INT));
		
		VariableDeclaration var2 = ast.newVariableDeclaration(ast.newVariableDeclarationFragment(ast.newSimpleName("var2")));
		var2.setType(ast.newPrimitiveType(PrimitiveType.Code.LONG));
		
		ListRewrite lrw = rewriter.getListRewrite(agg, AlignDeclaration.DECLARATIONS_PROPERTY);
		lrw.insertFirst(var1, null);
		lrw.insertAfter(var2, var1, null);
		
		assertEqualsTokenByToken("align(4): int var1; long var2;", end());
	}
	
	// TODO the comments are now attached to the last declaration, maybe change the colon for curlies :-( 
	public void testAddDeclarationsToColonAndPostDDocs() throws Exception {
		begin(" align(4):");
		
		AlignDeclaration agg = (AlignDeclaration) unit.declarations().get(0);
		
		VariableDeclaration var1 = ast.newVariableDeclaration(ast.newVariableDeclarationFragment(ast.newSimpleName("var1")));
		var1.setType(ast.newPrimitiveType(PrimitiveType.Code.INT));
		
		VariableDeclaration var2 = ast.newVariableDeclaration(ast.newVariableDeclarationFragment(ast.newSimpleName("var2")));
		var2.setType(ast.newPrimitiveType(PrimitiveType.Code.LONG));
		
		ListRewrite lrw = rewriter.getListRewrite(agg, AlignDeclaration.DECLARATIONS_PROPERTY);
		lrw.insertFirst(var1, null);
		lrw.insertAfter(var2, var1, null);
		
		rewriter.set(agg, AlignDeclaration.POST_D_DOC_PROPERTY, ast.newDDocComment("/// hello!"), null);
		
		assertEqualsTokenByToken("align(4): int var1; long var2; /// hello!", end());
	}
	
	public void testRemoveDeclarations() throws Exception {
		begin(" align(4) { int var1; long var2; }");
		
		AlignDeclaration agg = (AlignDeclaration) unit.declarations().get(0);
		
		ListRewrite lrw = rewriter.getListRewrite(agg, AlignDeclaration.DECLARATIONS_PROPERTY);
		lrw.remove(agg.declarations().get(0), null);
		lrw.remove(agg.declarations().get(1), null);
		
		assertEqualsTokenByToken("align(4) { }", end());
	}
	
	public void testAddPostDDoc() throws Exception {
		begin(" align(4) { }");
		
		AlignDeclaration alias = (AlignDeclaration) unit.declarations().get(0);
		rewriter.set(alias, AlignDeclaration.POST_D_DOC_PROPERTY, ast.newDDocComment("/// hello!"), null);
		
		assertEqualsTokenByToken("align(4) { } /// hello!", end());
	}
	
	public void testRemovePostDDoc() throws Exception {
		begin(" align(4) { } /// hello!");
		
		AlignDeclaration alias = (AlignDeclaration) unit.declarations().get(0);
		rewriter.remove(alias.getPostDDoc(), null);
		
		assertEqualsTokenByToken("align(4) { }", end());
	}

}
