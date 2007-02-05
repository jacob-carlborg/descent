package descent.tests.rewrite;

import descent.core.dom.ExternDeclaration;
import descent.core.dom.Modifier;
import descent.core.dom.PrimitiveType;
import descent.core.dom.VariableDeclaration;
import descent.core.dom.ExternDeclaration.Linkage;

public class RewriteExternDeclarationTest extends AbstractRewriteTest {
	
	public void testAddPreDDoc() throws Exception {
		begin(" extern() { }");
		
		ExternDeclaration extern = (ExternDeclaration) unit.declarations().get(0);
		extern.preDDocs().add(ast.newDDocComment("/** Some comment */\n"));
		
		assertEqualsTokenByToken("/** Some comment */ extern() { }", end());
	}
	
	public void testAddModifiers() throws Exception {
		begin(" extern() { }");
		
		ExternDeclaration extern = (ExternDeclaration) unit.declarations().get(0);
		extern.modifiers().add(ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));
		extern.modifiers().add(ast.newModifier(Modifier.ModifierKeyword.ABSTRACT_KEYWORD));
		
		assertEqualsTokenByToken("public abstract extern() { }", end());
	}
	
	public void testRemoveModifier() throws Exception {
		begin(" public abstract extern() { }");
		
		ExternDeclaration extern = (ExternDeclaration) unit.declarations().get(0);
		extern.modifiers().get(0).delete();
		
		assertEqualsTokenByToken("abstract extern() { }", end());
	}
	
	public void testAddLinkage() throws Exception {
		begin(" extern() { }");
		
		ExternDeclaration extern = (ExternDeclaration) unit.declarations().get(0);
		extern.setLinkage(Linkage.CPP);
		
		assertEqualsTokenByToken("extern(C++) { }", end());
	}
	
	public void testChangeLinkage() throws Exception {
		begin(" extern(C++) { }");
		
		ExternDeclaration extern = (ExternDeclaration) unit.declarations().get(0);
		extern.setLinkage(Linkage.WINDOWS);
		
		assertEqualsTokenByToken("extern(Windows) { }", end());
	}
	
	public void testRemoveLinkage() throws Exception {
		begin(" extern(C++) { }");
		
		ExternDeclaration extern = (ExternDeclaration) unit.declarations().get(0);
		extern.setLinkage(Linkage.DEFAULT);
		
		assertEqualsTokenByToken("extern() { }", end());
	}
	
	public void testAddDeclarations() throws Exception {
		begin(" extern() { }");
		
		ExternDeclaration extern = (ExternDeclaration) unit.declarations().get(0);
		
		VariableDeclaration var1 = ast.newVariableDeclaration(ast.newVariableDeclarationFragment(ast.newSimpleName("var1")));
		var1.setType(ast.newPrimitiveType(PrimitiveType.Code.INT));
		
		VariableDeclaration var2 = ast.newVariableDeclaration(ast.newVariableDeclarationFragment(ast.newSimpleName("var2")));
		var2.setType(ast.newPrimitiveType(PrimitiveType.Code.LONG));
		
		extern.declarations().add(var1);
		extern.declarations().add(var2);
		
		assertEqualsTokenByToken("extern() { int var1; long var2; }", end());
	}
	
	public void testAddDeclarationsWithComments() throws Exception {
		begin(" extern() { int var1; // comment for var1 \n }");
		
		ExternDeclaration extern = (ExternDeclaration) unit.declarations().get(0);
	
		VariableDeclaration var2 = ast.newVariableDeclaration(ast.newVariableDeclarationFragment(ast.newSimpleName("var2")));
		var2.setType(ast.newPrimitiveType(PrimitiveType.Code.LONG));
		
		extern.declarations().add(var2);
		
		assertEqualsTokenByToken("extern() { int var1; // comment for var1 \n long var2; }", end());
	}
	
	public void testAddDeclarationsToColon() throws Exception {
		begin(" extern(): ");
		
		ExternDeclaration extern = (ExternDeclaration) unit.declarations().get(0);
		
		VariableDeclaration var1 = ast.newVariableDeclaration(ast.newVariableDeclarationFragment(ast.newSimpleName("var1")));
		var1.setType(ast.newPrimitiveType(PrimitiveType.Code.INT));
		
		VariableDeclaration var2 = ast.newVariableDeclaration(ast.newVariableDeclarationFragment(ast.newSimpleName("var2")));
		var2.setType(ast.newPrimitiveType(PrimitiveType.Code.LONG));
		
		extern.declarations().add(var1);
		extern.declarations().add(var2);
		
		assertEqualsTokenByToken("extern(): int var1; long var2;", end());
	}
	
	public void testPostDDocsToColon() throws Exception {
		begin(" extern(): ");
		
		ExternDeclaration extern = (ExternDeclaration) unit.declarations().get(0);
		extern.setPostDDoc(ast.newDDocComment("/// hello!"));
		
		assertEqualsTokenByToken("extern(): /// hello!", end());
	}
	
	public void testRemoveDeclarations() throws Exception {
		begin(" extern() { int var1; long var2; }");
		
		ExternDeclaration extern = (ExternDeclaration) unit.declarations().get(0);
		extern.declarations().clear();
		
		assertEqualsTokenByToken("extern() { }", end());
	}
	
	public void testAddPostDDoc() throws Exception {
		begin(" extern() { }");
		
		ExternDeclaration extern = (ExternDeclaration) unit.declarations().get(0);
		extern.setPostDDoc(ast.newDDocComment("/// hello!"));
		
		assertEqualsTokenByToken("extern() { } /// hello!", end());
	}
	
	public void testRemovePostDDoc() throws Exception {
		begin(" extern() { } /// hello!");
		
		ExternDeclaration extern = (ExternDeclaration) unit.declarations().get(0);
		extern.getPostDDoc().delete();
		
		assertEqualsTokenByToken("extern() { }", end());
	}

}

