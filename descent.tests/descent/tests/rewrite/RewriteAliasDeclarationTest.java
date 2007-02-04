package descent.tests.rewrite;

import descent.core.dom.AliasDeclaration;
import descent.core.dom.Modifier;
import descent.core.dom.PrimitiveType;

public class RewriteAliasDeclarationTest extends AbstractRewriteTest {
	
	public void testAddPreDDoc() throws Exception {
		begin(" alias int bla;");
		
		AliasDeclaration alias = (AliasDeclaration) unit.declarations().get(0);
		alias.preDDocs().add(ast.newDDocComment("/** Some comment */\n"));
		
		assertEqualsTokenByToken("/** Some comment */ alias int bla;", end());
	}
	
	public void testAddModifiers() throws Exception {
		begin(" alias int bla;");
		
		AliasDeclaration alias = (AliasDeclaration) unit.declarations().get(0);
		alias.modifiers().add(ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));
		alias.modifiers().add(ast.newModifier(Modifier.ModifierKeyword.ABSTRACT_KEYWORD));
		
		assertEqualsTokenByToken("public abstract alias int bla;", end());
	}
	
	public void testRemoveModifier() throws Exception {
		begin(" public abstract alias int x;");
		
		AliasDeclaration alias = (AliasDeclaration) unit.declarations().get(0);
		alias.modifiers().get(0).delete();
		
		assertEqualsTokenByToken("abstract alias int x;", end());
	}
	
	public void testChangeType() throws Exception {
		begin("alias int x;");
		
		AliasDeclaration alias = (AliasDeclaration) unit.declarations().get(0);
		alias.setType(ast.newPrimitiveType(PrimitiveType.Code.LONG));
		
		assertEqualsTokenByToken("alias long x;", end());
	}
	
	public void testAddFragment() throws Exception {
		begin("alias int x;");
		
		AliasDeclaration alias = (AliasDeclaration) unit.declarations().get(0);
		alias.fragments().add(ast.newAliasDeclarationFragment(ast.newSimpleName("y")));
		
		assertEqualsTokenByToken("alias int x, y;", end());
	}
	
	public void testRemoveFragment() throws Exception {
		begin("alias int x, y;");
		
		AliasDeclaration alias = (AliasDeclaration) unit.declarations().get(0);
		alias.fragments().get(0).delete();
		
		assertEqualsTokenByToken("alias int y;", end());
	}
	
	public void testChangeFragmentName() throws Exception {
		begin("alias int x;");
		
		AliasDeclaration alias = (AliasDeclaration) unit.declarations().get(0);
		alias.fragments().get(0).setName(ast.newSimpleName("y"));
		
		assertEqualsTokenByToken("alias int y;", end());
	}
	
	public void testAddPostDDoc() throws Exception {
		begin(" alias int x;");
		
		AliasDeclaration alias = (AliasDeclaration) unit.declarations().get(0);
		alias.setPostDDoc(ast.newDDocComment("/// hello!"));
		
		assertEqualsTokenByToken("alias int x; /// hello!", end());
	}
	
	public void testRemovePostDDoc() throws Exception {
		begin(" alias int x; /// hello!");
		
		AliasDeclaration alias = (AliasDeclaration) unit.declarations().get(0);
		alias.getPostDDoc().delete();
		
		assertEqualsTokenByToken("alias int x;", end());
	}
	
	public void testMultiChange1() throws Exception {
		begin(" alias int x;");
		
		AliasDeclaration alias = (AliasDeclaration) unit.declarations().get(0);
		
		// Add comments
		alias.preDDocs().add(ast.newDDocComment("/** Some comment */\n"));
		
		// Add modifiers
		alias.modifiers().add(ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));
		alias.modifiers().add(ast.newModifier(Modifier.ModifierKeyword.ABSTRACT_KEYWORD));
		
		// Change type
		alias.setType(ast.newPrimitiveType(PrimitiveType.Code.LONG));
		
		// Add fragment
		alias.fragments().add(ast.newAliasDeclarationFragment(ast.newSimpleName("y")));
		
		assertEqualsTokenByToken("/** Some comment */ public abstract alias long x, y;", end());
	}

}
