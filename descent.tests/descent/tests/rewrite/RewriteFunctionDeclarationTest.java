package descent.tests.rewrite;

import descent.core.dom.Argument;
import descent.core.dom.FunctionDeclaration;
import descent.core.dom.Modifier;
import descent.core.dom.PrimitiveType;
import descent.core.dom.ValueTemplateParameter;

public class RewriteFunctionDeclarationTest extends AbstractRewriteTest {
	
	public void testAddPreDDoc() throws Exception {
		begin(" void bla() { }");
		
		FunctionDeclaration func = (FunctionDeclaration) unit.declarations().get(0);
		func.preDDocs().add(ast.newDDocComment("/** Some comment */\n"));
		
		assertEqualsTokenByToken("/** Some comment */ void bla() { }", end());
	}
	
	public void testAddModifiers() throws Exception {
		begin(" void bla() { }");
		
		FunctionDeclaration func = (FunctionDeclaration) unit.declarations().get(0);
		func.modifiers().add(ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));
		func.modifiers().add(ast.newModifier(Modifier.ModifierKeyword.ABSTRACT_KEYWORD));
		
		assertEqualsTokenByToken("public abstract void bla() { }", end());
	}
	
	public void testAddTemplateParameter() throws Exception {
		begin(" void bla() { }");
		
		FunctionDeclaration func = (FunctionDeclaration) unit.declarations().get(0);
		
		ValueTemplateParameter param = ast.newValueTemplateParameter();
		param.setName(ast.newSimpleName("T"));
		func.templateParameters().add(param);
		
		assertEqualsTokenByToken("void bla(T)() { }", end());
	}
	
	public void testRemoveTemplateParameter() throws Exception {
		begin(" void bla(T)() { }");
		
		FunctionDeclaration func = (FunctionDeclaration) unit.declarations().get(0);
		func.templateParameters().clear();
		
		assertEqualsTokenByToken("void bla() { }", end());
	}
	
	public void testAddArgument() throws Exception {
		begin(" void bla() { }");
		
		FunctionDeclaration func = (FunctionDeclaration) unit.declarations().get(0);
		
		Argument arg = ast.newArgument();
		arg.setName(ast.newSimpleName("x"));
		arg.setType(ast.newPrimitiveType(PrimitiveType.Code.INT));
		func.arguments().add(arg);
		
		assertEqualsTokenByToken("void bla(int x) { }", end());
	}
	
	public void testRemoveArgument() throws Exception {
		begin(" void bla(int x) { }");
		
		FunctionDeclaration func = (FunctionDeclaration) unit.declarations().get(0);
		func.arguments().clear();
		
		assertEqualsTokenByToken("void bla() { }", end());
	}
	
	public void testRemoveModifier() throws Exception {
		begin(" public abstract void bla() { }");
		
		FunctionDeclaration func = (FunctionDeclaration) unit.declarations().get(0);
		func.modifiers().get(0).delete();
		
		assertEqualsTokenByToken("abstract void bla() { }", end());
	}
	
	public void testAddPostDDoc() throws Exception {
		begin(" void bla() { }");
		
		FunctionDeclaration func = (FunctionDeclaration) unit.declarations().get(0);
		func.setPostDDoc(ast.newDDocComment("/// hello!"));
		
		assertEqualsTokenByToken("void bla() { } /// hello!", end());
	}
	
	public void testRemovePostDDoc() throws Exception {
		begin(" void bla() { } /// hello!");
		
		FunctionDeclaration func = (FunctionDeclaration) unit.declarations().get(0);
		func.getPostDDoc().delete();
		
		assertEqualsTokenByToken("void bla() { }", end());
	}

}
