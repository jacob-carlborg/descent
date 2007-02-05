package descent.tests.rewrite;

import descent.core.dom.EnumDeclaration;
import descent.core.dom.Modifier;
import descent.core.dom.PrimitiveType;

public class RewriteEnumDeclarationTest extends AbstractRewriteTest {
	
	public void testAddPreDDoc() throws Exception {
		begin(" enum X { }");
		
		EnumDeclaration e = (EnumDeclaration) unit.declarations().get(0);
		e.preDDocs().add(ast.newDDocComment("/** Some comment */\n"));
		
		assertEqualsTokenByToken("/** Some comment */ enum X { }", end());
	}
	
	public void testAddModifiers() throws Exception {
		begin(" enum X { }");
		
		EnumDeclaration e = (EnumDeclaration) unit.declarations().get(0);
		e.modifiers().add(ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));
		e.modifiers().add(ast.newModifier(Modifier.ModifierKeyword.ABSTRACT_KEYWORD));
		
		assertEqualsTokenByToken("public abstract enum X { }", end());
	}
	
	public void testRemoveModifier() throws Exception {
		begin(" public abstract enum X { }");
		
		EnumDeclaration e = (EnumDeclaration) unit.declarations().get(0);
		e.modifiers().get(0).delete();
		
		assertEqualsTokenByToken("abstract enum X { }", end());
	}
	
	public void testAddName() throws Exception {
		begin(" enum { }");
		
		EnumDeclaration e = (EnumDeclaration) unit.declarations().get(0);
		e.setName(ast.newSimpleName("New"));
		
		assertEqualsTokenByToken("enum New { }", end());
	}
	
	public void testChangeName() throws Exception {
		begin(" enum Old { }");
		
		EnumDeclaration e = (EnumDeclaration) unit.declarations().get(0);
		e.setName(ast.newSimpleName("New"));
		
		assertEqualsTokenByToken("enum New { }", end());
	}
	
	public void testRemoveName() throws Exception {
		begin(" enum Old { }");
		
		EnumDeclaration e = (EnumDeclaration) unit.declarations().get(0);
		e.getName().delete();
		
		assertEqualsTokenByToken("enum { }", end());
	}
	
	public void testAddBaseType() throws Exception {
		begin(" enum { }");
		
		EnumDeclaration e = (EnumDeclaration) unit.declarations().get(0);
		e.setBaseType(ast.newPrimitiveType(PrimitiveType.Code.LONG));
		
		assertEqualsTokenByToken("enum : long { }", end());
	}
	
	public void testChangeBaseType() throws Exception {
		begin(" enum : int { }");
		
		EnumDeclaration e = (EnumDeclaration) unit.declarations().get(0);
		e.setBaseType(ast.newPrimitiveType(PrimitiveType.Code.LONG));
		
		assertEqualsTokenByToken("enum : long { }", end());
	}
	
	public void testRemoveBaseType() throws Exception {
		begin(" enum : int { }");
		
		EnumDeclaration e = (EnumDeclaration) unit.declarations().get(0);
		e.getBaseType().delete();
		
		assertEqualsTokenByToken("enum { }", end());
	}
	
	public void testAddBaseType2() throws Exception {
		begin(" enum X { }");
		
		EnumDeclaration e = (EnumDeclaration) unit.declarations().get(0);
		e.setBaseType(ast.newPrimitiveType(PrimitiveType.Code.LONG));
		
		assertEqualsTokenByToken("enum X : long { }", end());
	}
	
	public void testChangeBaseType2() throws Exception {
		begin(" enum X : int { }");
		
		EnumDeclaration e = (EnumDeclaration) unit.declarations().get(0);
		e.setBaseType(ast.newPrimitiveType(PrimitiveType.Code.LONG));
		
		assertEqualsTokenByToken("enum X : long { }", end());
	}
	
	public void testRemoveBaseType2() throws Exception {
		begin(" enum X : int { }");
		
		EnumDeclaration e = (EnumDeclaration) unit.declarations().get(0);
		e.getBaseType().delete();
		
		assertEqualsTokenByToken("enum X { }", end());
	}
	
	public void testAddPostDDoc() throws Exception {
		begin(" enum X { }");
		
		EnumDeclaration e = (EnumDeclaration) unit.declarations().get(0);
		e.setPostDDoc(ast.newDDocComment("/// hello!"));
		
		assertEqualsTokenByToken("enum X { } /// hello!", end());
	}
	
	public void testRemovePostDDoc() throws Exception {
		begin(" enum X { } /// hello!");
		
		EnumDeclaration e = (EnumDeclaration) unit.declarations().get(0);
		e.getPostDDoc().delete();
		
		assertEqualsTokenByToken("enum X { }", end());
	}
	
	public void testChangeEnumMemberName() throws Exception {
		begin(" enum X { a, b, c }");
		
		EnumDeclaration e = (EnumDeclaration) unit.declarations().get(0);
		e.enumMembers().get(0).setName(ast.newSimpleName("theNew"));
		
		assertEqualsTokenByToken("enum X { theNew, b, c }", end());
	}
	
	public void testSetEnumMemberValue() throws Exception {
		begin(" enum X { a, b, c }");
		
		EnumDeclaration e = (EnumDeclaration) unit.declarations().get(0);
		e.enumMembers().get(0).setValue(ast.newSimpleName("theNew"));
		
		assertEqualsTokenByToken("enum X { a = theNew, b, c }", end());
	}
	
	public void testRemoveEnumMemberValue() throws Exception {
		begin(" enum X { a = theNew, b, c }");
		
		EnumDeclaration e = (EnumDeclaration) unit.declarations().get(0);
		e.enumMembers().get(0).getValue().delete();
		
		assertEqualsTokenByToken("enum X { a, b, c }", end());
	}

}

