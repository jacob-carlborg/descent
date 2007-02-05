package descent.tests.rewrite;

import descent.core.dom.Modifier;
import descent.core.dom.PrimitiveType;
import descent.core.dom.StaticIfDeclaration;
import descent.core.dom.VariableDeclaration;

public class RewriteStaticIfDeclarationTest extends AbstractRewriteTest {
	
	public void testAddPreDDoc() throws Exception {
		begin(" static if(true) { }");
		
		StaticIfDeclaration decl = (StaticIfDeclaration) unit.declarations().get(0);
		decl.preDDocs().add(ast.newDDocComment("/** Some comment */\n"));
		
		assertEqualsTokenByToken("/** Some comment */ static if(true) { }", end());
	}
	
	public void testAddModifiers() throws Exception {
		begin(" static if(true) { }");
		
		StaticIfDeclaration decl = (StaticIfDeclaration) unit.declarations().get(0);
		decl.modifiers().add(ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));
		decl.modifiers().add(ast.newModifier(Modifier.ModifierKeyword.ABSTRACT_KEYWORD));
		
		assertEqualsTokenByToken("public abstract static if(true) { }", end());
	}
	
	public void testRemoveModifier() throws Exception {
		begin(" public abstract static if(true) { }");
		
		StaticIfDeclaration decl = (StaticIfDeclaration) unit.declarations().get(0);
		decl.modifiers().get(0).delete();
		
		assertEqualsTokenByToken("abstract static if(true) { }", end());
	}
	
	public void testChangeExpression() throws Exception {
		begin(" static if(true) { }");
		
		StaticIfDeclaration decl = (StaticIfDeclaration) unit.declarations().get(0);
		decl.setExpression(ast.newSimpleName("a"));
		
		assertEqualsTokenByToken("static if(a) { }", end());
	}
	
	public void testAddThenDeclaration() throws Exception {
		begin(" static if(true) { }");
		
		StaticIfDeclaration decl = (StaticIfDeclaration) unit.declarations().get(0);
		
		VariableDeclaration var = ast.newVariableDeclaration(ast.newVariableDeclarationFragment(ast.newSimpleName("x")));
		var.setType(ast.newPrimitiveType(PrimitiveType.Code.INT));
		
		decl.thenDeclarations().add(var);
		
		assertEqualsTokenByToken("static if(true) { int x; }", end());
	}
	
	public void testAddThenDeclaration2() throws Exception {
		begin(" static if(true) int x;");
		
		StaticIfDeclaration decl = (StaticIfDeclaration) unit.declarations().get(0);
		
		VariableDeclaration var = ast.newVariableDeclaration(ast.newVariableDeclarationFragment(ast.newSimpleName("y")));
		var.setType(ast.newPrimitiveType(PrimitiveType.Code.LONG));
		
		decl.thenDeclarations().add(var);
		
		assertEqualsTokenByToken("static if(true) { int x; long y; }", end());
	}
	
	public void testAddThenDeclaration3() throws Exception {
		begin(" static if(true): ");
		
		StaticIfDeclaration decl = (StaticIfDeclaration) unit.declarations().get(0);
		
		VariableDeclaration var = ast.newVariableDeclaration(ast.newVariableDeclarationFragment(ast.newSimpleName("x")));
		var.setType(ast.newPrimitiveType(PrimitiveType.Code.INT));
		
		decl.thenDeclarations().add(var);
		
		assertEqualsTokenByToken("static if(true): int x;", end());
	}
	
	public void testRemoveThenDeclaration() throws Exception {
		begin(" static if(true) int x;");
		
		StaticIfDeclaration decl = (StaticIfDeclaration) unit.declarations().get(0);
		decl.thenDeclarations().clear();
		
		assertEqualsTokenByToken("static if(true) { }", end());
	}
	
	public void testRemoveThenDeclaration2() throws Exception {
		begin(" static if(true): int x;");
		
		StaticIfDeclaration decl = (StaticIfDeclaration) unit.declarations().get(0);
		decl.thenDeclarations().clear();
		
		assertEqualsTokenByToken("static if(true):", end());
	}
	
	public void testRemoveThenDeclarationWithElse() throws Exception {
		begin(" static if(true) int x; else { long x; }");
		
		StaticIfDeclaration decl = (StaticIfDeclaration) unit.declarations().get(0);
		decl.thenDeclarations().clear();
		
		assertEqualsTokenByToken("static if(true) { } else { long x; }", end());
	}
	
	public void testAddElseDeclaration() throws Exception {
		begin(" static if(true) { }");
		
		StaticIfDeclaration decl = (StaticIfDeclaration) unit.declarations().get(0);
		
		VariableDeclaration var = ast.newVariableDeclaration(ast.newVariableDeclarationFragment(ast.newSimpleName("x")));
		var.setType(ast.newPrimitiveType(PrimitiveType.Code.INT));
		
		decl.elseDeclarations().add(var);
		
		assertEqualsTokenByToken("static if(true) { } else { int x; }", end());
	}
	
	public void testAddElseDeclaration2() throws Exception {
		begin(" static if(true) int x;");
		
		StaticIfDeclaration decl = (StaticIfDeclaration) unit.declarations().get(0);
		
		VariableDeclaration var = ast.newVariableDeclaration(ast.newVariableDeclarationFragment(ast.newSimpleName("y")));
		var.setType(ast.newPrimitiveType(PrimitiveType.Code.LONG));
		
		decl.elseDeclarations().add(var);
		
		assertEqualsTokenByToken("static if(true) int x; else { long y; }", end());
	}
	
	public void testAddElseDeclaration3() throws Exception {
		begin(" static if(true) int x; else { }");
		
		StaticIfDeclaration decl = (StaticIfDeclaration) unit.declarations().get(0);
		
		VariableDeclaration var = ast.newVariableDeclaration(ast.newVariableDeclarationFragment(ast.newSimpleName("y")));
		var.setType(ast.newPrimitiveType(PrimitiveType.Code.LONG));
		
		decl.elseDeclarations().add(var);
		
		assertEqualsTokenByToken("static if(true) int x; else { long y; }", end());
	}
	
	public void testRemoveElseDeclaration() throws Exception {
		begin(" static if(true) int x; else { long x; }");
		
		StaticIfDeclaration decl = (StaticIfDeclaration) unit.declarations().get(0);
		decl.elseDeclarations().clear();
		
		assertEqualsTokenByToken("static if(true) int x;", end());
	}
	
	public void testRemoveElseDeclaration2() throws Exception {
		begin(" static if(true) int x; else: long x;");
		
		StaticIfDeclaration decl = (StaticIfDeclaration) unit.declarations().get(0);
		decl.elseDeclarations().clear();
		
		assertEqualsTokenByToken("static if(true) int x;", end());
	}
	
	public void testAddPostDDoc() throws Exception {
		begin(" static if(true) { }");
		
		StaticIfDeclaration decl = (StaticIfDeclaration) unit.declarations().get(0);
		decl.setPostDDoc(ast.newDDocComment("/// hello!"));
		
		assertEqualsTokenByToken("static if(true) { } /// hello!", end());
	}
	
	public void testRemovePostDDoc() throws Exception {
		begin(" static if(true) { } /// hello!");
		
		StaticIfDeclaration decl = (StaticIfDeclaration) unit.declarations().get(0);
		decl.getPostDDoc().delete();
		
		assertEqualsTokenByToken("static if(true) { }", end());
	}

}
