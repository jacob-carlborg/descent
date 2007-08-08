package descent.tests.rewrite;

import descent.core.dom.Argument;
import descent.core.dom.DelegateType;
import descent.core.dom.FunctionDeclaration;
import descent.core.dom.PrimitiveType;
import descent.core.dom.VariableDeclaration;


public class RewriteArgumentTest extends AbstractRewriteTest {
	
	/* TODO
	public void testAddPassageMode() throws Exception {
		begin("void bla(int x) { }");
		
		FunctionDeclaration func = (FunctionDeclaration) unit.declarations().get(0);
		Argument arg = func.arguments().get(0);
		arg.setPassageMode(PassageMode.OUT);
		
		assertEqualsTokenByToken("void bla(out int x) { }", end());
	}
	
	public void testChangePassageMode() throws Exception {
		begin("void bla(out int x) { }");
		
		FunctionDeclaration func = (FunctionDeclaration) unit.declarations().get(0);
		Argument arg = func.arguments().get(0);
		arg.setPassageMode(PassageMode.INOUT);
		
		assertEqualsTokenByToken("void bla(inout int x) { }", end());
	}
	
	public void testRemovePassageMode() throws Exception {
		begin("void bla(out int x) { }");
		
		FunctionDeclaration func = (FunctionDeclaration) unit.declarations().get(0);
		Argument arg = func.arguments().get(0);
		arg.setPassageMode(PassageMode.DEFAULT);
		
		assertEqualsTokenByToken("void bla(int x) { }", end());
	}
	
	public void testAddType() throws Exception {
		
	}
	*/
	
	public void testChangeTypeWithoutPassageMode() throws Exception {
		begin("void bla(int x) { }");
		
		FunctionDeclaration func = (FunctionDeclaration) unit.declarations().get(0);
		Argument arg = func.arguments().get(0);
		arg.setType(ast.newPrimitiveType(PrimitiveType.Code.LONG));
		
		assertEqualsTokenByToken("void bla(long x) { }", end());
	}
	
	public void testChangeTypeWithPassageMode() throws Exception {
		begin("void bla(in int x) { }");
		
		FunctionDeclaration func = (FunctionDeclaration) unit.declarations().get(0);
		Argument arg = func.arguments().get(0);
		arg.setType(ast.newPrimitiveType(PrimitiveType.Code.LONG));
		
		assertEqualsTokenByToken("void bla(in long x) { }", end());
	}
	
	public void testRemoveType() throws Exception {
		begin("void bla(in int x) { }");
		
		FunctionDeclaration func = (FunctionDeclaration) unit.declarations().get(0);
		Argument arg = func.arguments().get(0);
		arg.getType().delete();
		
		assertEqualsTokenByToken("void bla(in x) { }", end());
	}
	
	public void testAddName() throws Exception {
		begin("int function(char) bla;");
		
		VariableDeclaration var = (VariableDeclaration) unit.declarations().get(0);
		DelegateType delegateType = (DelegateType) var.getType();
		Argument argument = delegateType.arguments().get(0);
		argument.setName(ast.newSimpleName("illegal"));
		
		assertEqualsTokenByToken("int function(char illegal) bla;", end());
	}
	
	public void testChangeName() throws Exception {
		begin("void bla(int x) { }");
		
		FunctionDeclaration func = (FunctionDeclaration) unit.declarations().get(0);
		Argument arg = func.arguments().get(0);
		arg.setName(ast.newSimpleName("y"));
		
		assertEqualsTokenByToken("void bla(int y) { }", end());
	}
	
	public void testRemoveName() throws Exception {
		begin("void bla(int x) { }");
		
		FunctionDeclaration func = (FunctionDeclaration) unit.declarations().get(0);
		Argument arg = func.arguments().get(0);
		arg.getName().delete();
		
		assertEqualsTokenByToken("void bla(int) { }", end());
	}
	
	public void testAddDefaultValue() throws Exception {
		begin("void bla(int x) { }");
		
		FunctionDeclaration func = (FunctionDeclaration) unit.declarations().get(0);
		Argument arg = func.arguments().get(0);
		arg.setDefaultValue(ast.newNumberLiteral("2"));
		
		assertEqualsTokenByToken("void bla(int x = 2) { }", end());
	}
	
	public void testChangeDefaultValue() throws Exception {
		begin("void bla(int x = 2) { }");
		
		FunctionDeclaration func = (FunctionDeclaration) unit.declarations().get(0);
		Argument arg = func.arguments().get(0);
		arg.setDefaultValue(ast.newNumberLiteral("4"));
		
		assertEqualsTokenByToken("void bla(int x = 4) { }", end());
	}
	
	public void testRemoveDefaultValue() throws Exception {
		begin("void bla(int x = 2) { }");
		
		FunctionDeclaration func = (FunctionDeclaration) unit.declarations().get(0);
		Argument arg = func.arguments().get(0);
		arg.getDefaultValue().delete();
		
		assertEqualsTokenByToken("void bla(int x) { }", end());
	}

}
