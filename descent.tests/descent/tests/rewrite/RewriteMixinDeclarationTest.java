package descent.tests.rewrite;

import descent.core.dom.TemplateMixinDeclaration;
import descent.core.dom.Modifier;
import descent.core.dom.PrimitiveType;

public class RewriteMixinDeclarationTest extends AbstractRewriteTest {
	
	public void testAddPreDDoc() throws Exception {
		begin(" mixin Foo!(bar) bla;");
		
		TemplateMixinDeclaration mixin = (TemplateMixinDeclaration) unit.declarations().get(0);
		mixin.preDDocs().add(ast.newDDocComment("/** Some comment */\n"));
		
		assertEqualsTokenByToken("/** Some comment */ mixin Foo!(bar) bla;", end());
	}
	
	public void testAddModifiers() throws Exception {
		begin(" mixin Foo!(bar) bla;");
		
		TemplateMixinDeclaration mixin = (TemplateMixinDeclaration) unit.declarations().get(0);
		mixin.modifiers().add(ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));
		mixin.modifiers().add(ast.newModifier(Modifier.ModifierKeyword.ABSTRACT_KEYWORD));
		
		assertEqualsTokenByToken("public abstract mixin Foo!(bar) bla;", end());
	}
	
	public void testRemoveModifier() throws Exception {
		begin(" public abstract mixin Foo!(bar) x;");
		
		TemplateMixinDeclaration mixin = (TemplateMixinDeclaration) unit.declarations().get(0);
		mixin.modifiers().get(0).delete();
		
		assertEqualsTokenByToken("abstract mixin Foo!(bar) x;", end());
	}
	
	public void testChangeType() throws Exception {
		begin("mixin Foo!(bar) x;");
		
		TemplateMixinDeclaration mixin = (TemplateMixinDeclaration) unit.declarations().get(0);
		mixin.setType(ast.newPrimitiveType(PrimitiveType.Code.LONG));
		
		assertEqualsTokenByToken("mixin long x;", end());
	}
	
	public void testAddName() throws Exception {
		begin("mixin Foo!(bar);");
		
		TemplateMixinDeclaration mixin = (TemplateMixinDeclaration) unit.declarations().get(0);
		mixin.setName(ast.newSimpleName("theNew"));
		
		assertEqualsTokenByToken("mixin Foo!(bar) theNew;", end());
	}
	
	public void testRemoveName() throws Exception {
		begin("mixin Foo!(bar) aName;");
		
		TemplateMixinDeclaration mixin = (TemplateMixinDeclaration) unit.declarations().get(0);
		mixin.getName().delete();
		
		assertEqualsTokenByToken("mixin Foo!(bar);", end());
	}
	
	public void testAddPostDDoc() throws Exception {
		begin(" mixin Foo!(bar) x;");
		
		TemplateMixinDeclaration mixin = (TemplateMixinDeclaration) unit.declarations().get(0);
		mixin.setPostDDoc(ast.newDDocComment("/// hello!"));
		
		assertEqualsTokenByToken("mixin Foo!(bar) x; /// hello!", end());
	}
	
	public void testRemovePostDDoc() throws Exception {
		begin(" mixin Foo!(bar) x; /// hello!");
		
		TemplateMixinDeclaration mixin = (TemplateMixinDeclaration) unit.declarations().get(0);
		mixin.getPostDDoc().delete();
		
		assertEqualsTokenByToken("mixin Foo!(bar) x;", end());
	}

}
