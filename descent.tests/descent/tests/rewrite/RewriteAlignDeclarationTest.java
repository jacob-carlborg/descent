package descent.tests.rewrite;

import descent.core.dom.AlignDeclaration;
import descent.core.dom.Modifier;
import descent.core.dom.PrimitiveType;
import descent.core.dom.VariableDeclaration;

public class RewriteAlignDeclarationTest extends AbstractRewriteTest {
	
	public void testAddPreDDoc() throws Exception {
		begin(" align(4) { }");
		
		AlignDeclaration align = (AlignDeclaration) unit.declarations().get(0);
		align.preDDocs().add(ast.newDDocComment("/** Some comment */\n"));
		
		assertEqualsTokenByToken("/** Some comment */ align(4) { }", end());
	}
	
	public void testAddModifiers() throws Exception {
		begin(" align(4) { }");
		
		AlignDeclaration align = (AlignDeclaration) unit.declarations().get(0);
		align.modifiers().add(ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));
		align.modifiers().add(ast.newModifier(Modifier.ModifierKeyword.ABSTRACT_KEYWORD));
		
		assertEqualsTokenByToken("public abstract align(4) { }", end());
	}
	
	public void testRemoveModifier() throws Exception {
		begin(" public abstract align(4) { }");
		
		AlignDeclaration align = (AlignDeclaration) unit.declarations().get(0);
		align.modifiers().get(0).delete();
		
		assertEqualsTokenByToken("abstract align(4) { }", end());
	}
	
	public void testChangeAlign() throws Exception {
		begin(" align(4) { }");
		
		AlignDeclaration align = (AlignDeclaration) unit.declarations().get(0);
		align.setAlign(8);
		
		assertEqualsTokenByToken("align(8) { }", end());
	}
	
	public void testAddDeclarations() throws Exception {
		begin(" align(4) { }");
		
		AlignDeclaration align = (AlignDeclaration) unit.declarations().get(0);
		
		VariableDeclaration var1 = ast.newVariableDeclaration(ast.newVariableDeclarationFragment(ast.newSimpleName("var1")));
		var1.setType(ast.newPrimitiveType(PrimitiveType.Code.INT));
		
		VariableDeclaration var2 = ast.newVariableDeclaration(ast.newVariableDeclarationFragment(ast.newSimpleName("var2")));
		var2.setType(ast.newPrimitiveType(PrimitiveType.Code.LONG));
		
		align.declarations().add(var1);
		align.declarations().add(var2);
		
		assertEqualsTokenByToken("align(4) { int var1; long var2; }", end());
	}
	
	public void testAddDeclarationsWithComments() throws Exception {
		begin(" align(4) { int var1; // comment for var1 \n }");
		
		AlignDeclaration align = (AlignDeclaration) unit.declarations().get(0);
	
		VariableDeclaration var2 = ast.newVariableDeclaration(ast.newVariableDeclarationFragment(ast.newSimpleName("var2")));
		var2.setType(ast.newPrimitiveType(PrimitiveType.Code.LONG));
		
		align.declarations().add(var2);
		
		assertEqualsTokenByToken("align(4) { int var1; // comment for var1 \n long var2; }", end());
	}
	
	public void testAddDeclarationsToColon() throws Exception {
		begin(" align(4):");
		
		AlignDeclaration align = (AlignDeclaration) unit.declarations().get(0);
		
		VariableDeclaration var1 = ast.newVariableDeclaration(ast.newVariableDeclarationFragment(ast.newSimpleName("var1")));
		var1.setType(ast.newPrimitiveType(PrimitiveType.Code.INT));
		
		VariableDeclaration var2 = ast.newVariableDeclaration(ast.newVariableDeclarationFragment(ast.newSimpleName("var2")));
		var2.setType(ast.newPrimitiveType(PrimitiveType.Code.LONG));
		
		align.declarations().add(var1);
		align.declarations().add(var2);
		
		assertEqualsTokenByToken("align(4): int var1; long var2;", end());
	}
	
	// TODO the comments are now attached to the last declaration, maybe change the colon for curlies :-( 
	public void testAddDeclarationsToColonAndPostDDocs() throws Exception {
		begin(" align(4):");
		
		AlignDeclaration align = (AlignDeclaration) unit.declarations().get(0);
		
		VariableDeclaration var1 = ast.newVariableDeclaration(ast.newVariableDeclarationFragment(ast.newSimpleName("var1")));
		var1.setType(ast.newPrimitiveType(PrimitiveType.Code.INT));
		
		VariableDeclaration var2 = ast.newVariableDeclaration(ast.newVariableDeclarationFragment(ast.newSimpleName("var2")));
		var2.setType(ast.newPrimitiveType(PrimitiveType.Code.LONG));
		
		align.declarations().add(var1);
		align.declarations().add(var2);
		
		align.setPostDDoc(ast.newDDocComment("/// hello!"));
		
		assertEqualsTokenByToken("align(4): int var1; long var2; /// hello!", end());
	}
	
	public void testRemoveDeclarations() throws Exception {
		begin(" align(4) { int var1; long var2; }");
		
		AlignDeclaration align = (AlignDeclaration) unit.declarations().get(0);
		align.declarations().clear();
		
		assertEqualsTokenByToken("align(4) { }", end());
	}
	
	public void testAddPostDDoc() throws Exception {
		begin(" align(4) { }");
		
		AlignDeclaration align = (AlignDeclaration) unit.declarations().get(0);
		align.setPostDDoc(ast.newDDocComment("/// hello!"));
		
		assertEqualsTokenByToken("align(4) { } /// hello!", end());
	}
	
	public void testRemovePostDDoc() throws Exception {
		begin(" align(4) { } /// hello!");
		
		AlignDeclaration align = (AlignDeclaration) unit.declarations().get(0);
		align.getPostDDoc().delete();
		
		assertEqualsTokenByToken("align(4) { }", end());
	}

}
