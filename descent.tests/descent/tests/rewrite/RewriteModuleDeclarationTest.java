package descent.tests.rewrite;

import descent.core.dom.ModuleDeclaration;

public class RewriteModuleDeclarationTest extends AbstractRewriteTest {
	
	public void testAddPreDDoc() throws Exception {
		begin(" module bla.ble.bli;");
		
		ModuleDeclaration module = (ModuleDeclaration) unit.getModuleDeclaration();
		module.preDDocs().add(ast.newDDocComment("/** Some comment */\n"));
		
		assertEqualsTokenByToken("/** Some comment */ module bla.ble.bli;", end());
	}
	
	public void testChangeName() throws Exception {
		begin("module bla.ble.bli;");
		
		ModuleDeclaration module = (ModuleDeclaration) unit.getModuleDeclaration();
		module.setName(ast.newSimpleName("hola"));
		
		assertEqualsTokenByToken("module hola;", end());
	}
	
	public void testAddPostDDoc() throws Exception {
		begin(" module bla.ble.bli;");
		
		ModuleDeclaration module = (ModuleDeclaration) unit.getModuleDeclaration();
		module.setPostDDoc(ast.newDDocComment("/// hello!"));
		
		assertEqualsTokenByToken("module bla.ble.bli; /// hello!", end());
	}
	
	public void testRemovePostDDoc() throws Exception {
		begin(" module bla.ble.bli; /// hello!");
		
		ModuleDeclaration module = (ModuleDeclaration) unit.getModuleDeclaration();
		module.getPostDDoc().delete();
		
		assertEqualsTokenByToken("module bla.ble.bli;", end());
	}

}
