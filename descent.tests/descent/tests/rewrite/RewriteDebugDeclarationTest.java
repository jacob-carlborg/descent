package descent.tests.rewrite;

import descent.core.dom.DebugDeclaration;
import descent.core.dom.Modifier;
import descent.core.dom.PrimitiveType;
import descent.core.dom.VariableDeclaration;

public class RewriteDebugDeclarationTest extends AbstractRewriteTest {
	
	public void testAddPreDDoc() throws Exception {
		begin(" debug { }");
		
		DebugDeclaration decl = (DebugDeclaration) unit.declarations().get(0);
		decl.preDDocs().add(ast.newDDocComment("/** Some comment */\n"));
		
		assertEqualsTokenByToken("/** Some comment */ debug { }", end());
	}
	
	public void testAddModifiers() throws Exception {
		begin(" debug { }");
		
		DebugDeclaration decl = (DebugDeclaration) unit.declarations().get(0);
		decl.modifiers().add(ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));
		decl.modifiers().add(ast.newModifier(Modifier.ModifierKeyword.ABSTRACT_KEYWORD));
		
		assertEqualsTokenByToken("public abstract debug { }", end());
	}
	
	public void testRemoveModifier() throws Exception {
		begin(" public abstract debug { }");
		
		DebugDeclaration decl = (DebugDeclaration) unit.declarations().get(0);
		decl.modifiers().get(0).delete();
		
		assertEqualsTokenByToken("abstract debug { }", end());
	}
	
	public void testChangeVersion() throws Exception {
		begin(" debug { }");
		
		DebugDeclaration decl = (DebugDeclaration) unit.declarations().get(0);
		decl.setVersion(ast.newVersion("8"));
		
		assertEqualsTokenByToken("debug (8) { }", end());
	}
	
	public void testAddThenDeclaration() throws Exception {
		begin(" debug { }");
		
		DebugDeclaration decl = (DebugDeclaration) unit.declarations().get(0);
		
		VariableDeclaration var = ast.newVariableDeclaration(ast.newVariableDeclarationFragment(ast.newSimpleName("x")));
		var.setType(ast.newPrimitiveType(PrimitiveType.Code.INT));
		
		decl.thenDeclarations().add(var);
		
		assertEqualsTokenByToken("debug { int x; }", end());
	}
	
	public void testAddThenDeclaration2() throws Exception {
		begin(" debug int x;");
		
		DebugDeclaration decl = (DebugDeclaration) unit.declarations().get(0);
		
		VariableDeclaration var = ast.newVariableDeclaration(ast.newVariableDeclarationFragment(ast.newSimpleName("y")));
		var.setType(ast.newPrimitiveType(PrimitiveType.Code.LONG));
		
		decl.thenDeclarations().add(var);
		
		assertEqualsTokenByToken("debug { int x; long y; }", end());
	}
	
	public void testAddThenDeclaration3() throws Exception {
		begin(" debug: ");
		
		DebugDeclaration decl = (DebugDeclaration) unit.declarations().get(0);
		
		VariableDeclaration var = ast.newVariableDeclaration(ast.newVariableDeclarationFragment(ast.newSimpleName("x")));
		var.setType(ast.newPrimitiveType(PrimitiveType.Code.INT));
		
		decl.thenDeclarations().add(var);
		
		assertEqualsTokenByToken("debug: int x;", end());
	}
	
	public void testRemoveThenDeclaration() throws Exception {
		begin(" debug int x;");
		
		DebugDeclaration decl = (DebugDeclaration) unit.declarations().get(0);
		decl.thenDeclarations().clear();
		
		assertEqualsTokenByToken("debug { }", end());
	}
	
	public void testRemoveThenDeclaration2() throws Exception {
		begin(" debug: int x;");
		
		DebugDeclaration decl = (DebugDeclaration) unit.declarations().get(0);
		decl.thenDeclarations().clear();
		
		assertEqualsTokenByToken("debug:", end());
	}
	
	public void testRemoveThenDeclarationWithElse() throws Exception {
		begin(" debug int x; else { long x; }");
		
		DebugDeclaration decl = (DebugDeclaration) unit.declarations().get(0);
		decl.thenDeclarations().clear();
		
		assertEqualsTokenByToken("debug { } else { long x; }", end());
	}
	
	public void testAddElseDeclaration() throws Exception {
		begin(" debug { }");
		
		DebugDeclaration decl = (DebugDeclaration) unit.declarations().get(0);
		
		VariableDeclaration var = ast.newVariableDeclaration(ast.newVariableDeclarationFragment(ast.newSimpleName("x")));
		var.setType(ast.newPrimitiveType(PrimitiveType.Code.INT));
		
		decl.elseDeclarations().add(var);
		
		assertEqualsTokenByToken("debug { } else { int x; }", end());
	}
	
	public void testAddElseDeclaration2() throws Exception {
		begin(" debug int x;");
		
		DebugDeclaration decl = (DebugDeclaration) unit.declarations().get(0);
		
		VariableDeclaration var = ast.newVariableDeclaration(ast.newVariableDeclarationFragment(ast.newSimpleName("y")));
		var.setType(ast.newPrimitiveType(PrimitiveType.Code.LONG));
		
		decl.elseDeclarations().add(var);
		
		assertEqualsTokenByToken("debug int x; else { long y; }", end());
	}
	
	public void testAddElseDeclaration3() throws Exception {
		begin(" debug int x; else { }");
		
		DebugDeclaration decl = (DebugDeclaration) unit.declarations().get(0);
		
		VariableDeclaration var = ast.newVariableDeclaration(ast.newVariableDeclarationFragment(ast.newSimpleName("y")));
		var.setType(ast.newPrimitiveType(PrimitiveType.Code.LONG));
		
		decl.elseDeclarations().add(var);
		
		assertEqualsTokenByToken("debug int x; else { long y; }", end());
	}
	
	public void testRemoveElseDeclaration() throws Exception {
		begin(" debug int x; else { long x; }");
		
		DebugDeclaration decl = (DebugDeclaration) unit.declarations().get(0);
		decl.elseDeclarations().clear();
		
		assertEqualsTokenByToken("debug int x;", end());
	}
	
	public void testRemoveElseDeclaration2() throws Exception {
		begin(" debug int x; else: long x;");
		
		DebugDeclaration decl = (DebugDeclaration) unit.declarations().get(0);
		decl.elseDeclarations().clear();
		
		assertEqualsTokenByToken("debug int x;", end());
	}
	
	public void testAddPostDDoc() throws Exception {
		begin(" debug { }");
		
		DebugDeclaration decl = (DebugDeclaration) unit.declarations().get(0);
		decl.setPostDDoc(ast.newDDocComment("/// hello!"));
		
		assertEqualsTokenByToken("debug { } /// hello!", end());
	}
	
	public void testRemovePostDDoc() throws Exception {
		begin(" debug { } /// hello!");
		
		DebugDeclaration decl = (DebugDeclaration) unit.declarations().get(0);
		decl.getPostDDoc().delete();
		
		assertEqualsTokenByToken("debug { }", end());
	}

}
