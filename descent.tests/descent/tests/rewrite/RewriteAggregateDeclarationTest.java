package descent.tests.rewrite;

import descent.core.dom.AggregateDeclaration;
import descent.core.dom.BaseClass;
import descent.core.dom.Modifier;
import descent.core.dom.PrimitiveType;
import descent.core.dom.TypeTemplateParameter;
import descent.core.dom.VariableDeclaration;

public class RewriteAggregateDeclarationTest extends AbstractRewriteTest {
	
	public void testAddPreDDoc() throws Exception {
		begin(" class X { }");
		
		AggregateDeclaration agg = (AggregateDeclaration) unit.declarations().get(0);
		agg.preDDocs().add(ast.newDDocComment("/** Some comment */\n"));
		
		assertEqualsTokenByToken("/** Some comment */ class X { }", end());
	}
	
	public void testAddModifiers() throws Exception {
		begin(" class X { }");
		
		AggregateDeclaration agg = (AggregateDeclaration) unit.declarations().get(0);
		agg.modifiers().add(ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));
		agg.modifiers().add(ast.newModifier(Modifier.ModifierKeyword.ABSTRACT_KEYWORD));
		
		assertEqualsTokenByToken("public abstract class X { }", end());
	}
	
	public void testRemoveModifier() throws Exception {
		begin(" public abstract class X { }");
		
		AggregateDeclaration agg = (AggregateDeclaration) unit.declarations().get(0);
		agg.modifiers().get(0).delete();
		
		assertEqualsTokenByToken("abstract class X { }", end());
	}
	
	public void testChangeKind() throws Exception {
		begin(" class X { }");
		
		AggregateDeclaration agg = (AggregateDeclaration) unit.declarations().get(0);
		agg.setKind(AggregateDeclaration.Kind.INTERFACE);
		
		assertEqualsTokenByToken("interface X { }", end());
	}
	
	public void testAddName() throws Exception {
		begin(" union { }");
		
		AggregateDeclaration agg = (AggregateDeclaration) unit.declarations().get(0);
		agg.setName(ast.newSimpleName("New"));
		
		assertEqualsTokenByToken("union New { }", end());
	}
	
	public void testChangeName() throws Exception {
		begin(" class Old { }");
		
		AggregateDeclaration agg = (AggregateDeclaration) unit.declarations().get(0);
		agg.setName(ast.newSimpleName("New"));
		
		assertEqualsTokenByToken("class New { }", end());
	}
	
	public void testRemoveName() throws Exception {
		begin(" union Old { }");
		
		AggregateDeclaration agg = (AggregateDeclaration) unit.declarations().get(0);
		agg.getName().delete();
		
		assertEqualsTokenByToken("union { }", end());
	}
	
	public void testAddTemplateParameters() throws Exception {
		begin(" class X { }");
		
		AggregateDeclaration agg = (AggregateDeclaration) unit.declarations().get(0);
		
		TypeTemplateParameter param1 = ast.newTypeTemplateParameter();
		param1.setName(ast.newSimpleName("T"));
		
		TypeTemplateParameter param2 = ast.newTypeTemplateParameter();
		param2.setName(ast.newSimpleName("U"));
		
		agg.templateParameters().add(param1);
		agg.templateParameters().add(param2);
		
		assertEqualsTokenByToken("class X(T, U) { }", end());
	}
	
	public void testRemoveTemplateParameters() throws Exception {
		begin(" class X(T, U) { }");
		
		AggregateDeclaration agg = (AggregateDeclaration) unit.declarations().get(0);
		agg.templateParameters().clear();
		
		assertEqualsTokenByToken("class X { }", end());
	}
	
	public void testChangeTemplateParameters() throws Exception {
		begin(" class X(T, U) { }");
		
		AggregateDeclaration agg = (AggregateDeclaration) unit.declarations().get(0);
		
		TypeTemplateParameter param1 = ast.newTypeTemplateParameter();
		param1.setName(ast.newSimpleName("V"));
		
		agg.templateParameters().set(0, param1);
		
		assertEqualsTokenByToken("class X(V, U) { }", end());
	}
	
	public void testAddBaseClasses() throws Exception {
		begin(" class X { }");
		
		AggregateDeclaration agg = (AggregateDeclaration) unit.declarations().get(0);
		
		BaseClass base1 = ast.newBaseClass();
		base1.setType(ast.newSimpleType(ast.newSimpleName("One")));
		
		BaseClass base2 = ast.newBaseClass();
		base2.setType(ast.newSimpleType(ast.newSimpleName("Two")));
		
		agg.baseClasses().add(base1);
		agg.baseClasses().add(base2);
		
		assertEqualsTokenByToken("class X : One, Two { }", end());
	}
	
	public void testRemoveBaseClasses() throws Exception {
		begin(" class X : One, Two { }");
		
		AggregateDeclaration agg = (AggregateDeclaration) unit.declarations().get(0);
		agg.baseClasses().clear();
		
		assertEqualsTokenByToken("class X { }", end());
	}
	
	public void testAddDeclarations() throws Exception {
		begin(" class X { }");
		
		AggregateDeclaration agg = (AggregateDeclaration) unit.declarations().get(0);
		
		VariableDeclaration var1 = ast.newVariableDeclaration(ast.newVariableDeclarationFragment(ast.newSimpleName("var1")));
		var1.setType(ast.newPrimitiveType(PrimitiveType.Code.INT));
		
		VariableDeclaration var2 = ast.newVariableDeclaration(ast.newVariableDeclarationFragment(ast.newSimpleName("var2")));
		var2.setType(ast.newPrimitiveType(PrimitiveType.Code.LONG));
		
		agg.declarations().add(var1);
		agg.declarations().add(var2);
		
		assertEqualsTokenByToken("class X { int var1; long var2; }", end());
	}
	
	public void testAddDeclarationsWithComments() throws Exception {
		begin(" class X { int var1; // comment for var1 \n }");
		
		AggregateDeclaration agg = (AggregateDeclaration) unit.declarations().get(0);
	
		VariableDeclaration var2 = ast.newVariableDeclaration(ast.newVariableDeclarationFragment(ast.newSimpleName("var2")));
		var2.setType(ast.newPrimitiveType(PrimitiveType.Code.LONG));
		
		agg.declarations().add(var2);
		
		assertEqualsTokenByToken("class X { int var1; // comment for var1 \n long var2; }", end());
	}
	
	public void testAddDeclarationsToClosed() throws Exception {
		begin(" class X;");
		
		AggregateDeclaration agg = (AggregateDeclaration) unit.declarations().get(0);
		
		VariableDeclaration var1 = ast.newVariableDeclaration(ast.newVariableDeclarationFragment(ast.newSimpleName("var1")));
		var1.setType(ast.newPrimitiveType(PrimitiveType.Code.INT));
		
		VariableDeclaration var2 = ast.newVariableDeclaration(ast.newVariableDeclarationFragment(ast.newSimpleName("var2")));
		var2.setType(ast.newPrimitiveType(PrimitiveType.Code.LONG));
		
		agg.declarations().add(var1);
		agg.declarations().add(var2);
		
		assertEqualsTokenByToken("class X { int var1; long var2; }", end());
	}
	
	public void testAddDeclarationsToClosedAndPostDDocs() throws Exception {
		begin(" class X;");
		
		AggregateDeclaration agg = (AggregateDeclaration) unit.declarations().get(0);
		
		VariableDeclaration var1 = ast.newVariableDeclaration(ast.newVariableDeclarationFragment(ast.newSimpleName("var1")));
		var1.setType(ast.newPrimitiveType(PrimitiveType.Code.INT));
		
		VariableDeclaration var2 = ast.newVariableDeclaration(ast.newVariableDeclarationFragment(ast.newSimpleName("var2")));
		var2.setType(ast.newPrimitiveType(PrimitiveType.Code.LONG));
		
		agg.declarations().add(var1);
		agg.declarations().add(var2);
		
		agg.setPostDDoc(ast.newDDocComment("/// hello!"));
		
		assertEqualsTokenByToken("class X { int var1; long var2; } /// hello!", end());
	}
	
	public void testPostDDocsToClosed() throws Exception {
		begin(" class X;");
		
		AggregateDeclaration agg = (AggregateDeclaration) unit.declarations().get(0);
		agg.setPostDDoc(ast.newDDocComment("/// hello!"));
		
		assertEqualsTokenByToken("class X; /// hello!", end());
	}
	
	public void testRemoveDeclarations() throws Exception {
		begin(" class X { int var1; long var2; }");
		
		AggregateDeclaration agg = (AggregateDeclaration) unit.declarations().get(0);
		agg.declarations().clear();
		
		assertEqualsTokenByToken("class X { }", end());
	}
	
	public void testAddPostDDoc() throws Exception {
		begin(" class X { }");
		
		AggregateDeclaration agg = (AggregateDeclaration) unit.declarations().get(0);
		agg.setPostDDoc(ast.newDDocComment("/// hello!"));
		
		assertEqualsTokenByToken("class X { } /// hello!", end());
	}
	
	public void testRemovePostDDoc() throws Exception {
		begin(" class X { } /// hello!");
		
		AggregateDeclaration agg = (AggregateDeclaration) unit.declarations().get(0);
		agg.getPostDDoc().delete();
		
		assertEqualsTokenByToken("class X { }", end());
	}
	
	public void testMultiChange1() throws Exception {
		begin(" union { }");
		
		AggregateDeclaration agg = (AggregateDeclaration) unit.declarations().get(0);
		
		// Add comments
		agg.preDDocs().add(ast.newDDocComment("/** Some comment */\n"));
		
		// Add modifiers
		agg.modifiers().add(ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));
		agg.modifiers().add(ast.newModifier(Modifier.ModifierKeyword.ABSTRACT_KEYWORD));
		
		// Change type
		agg.setKind(AggregateDeclaration.Kind.CLASS);
		
		// Add name
		agg.setName(ast.newSimpleName("NewClass"));
		
		// Add template parameters
		TypeTemplateParameter param1 = ast.newTypeTemplateParameter();
		param1.setName(ast.newSimpleName("T"));
		
		TypeTemplateParameter param2 = ast.newTypeTemplateParameter();
		param2.setName(ast.newSimpleName("U"));
		
		agg.templateParameters().add(param1);
		agg.templateParameters().add(param2);
		
		// Add base classes
		BaseClass base1 = ast.newBaseClass();
		base1.setType(ast.newSimpleType(ast.newSimpleName("One")));
		
		BaseClass base2 = ast.newBaseClass();
		base2.setType(ast.newSimpleType(ast.newSimpleName("Two")));
		
		agg.baseClasses().add(base1);
		agg.baseClasses().add(base2);
		
		// Add declarations
		VariableDeclaration var1 = ast.newVariableDeclaration(ast.newVariableDeclarationFragment(ast.newSimpleName("var1")));
		var1.setType(ast.newPrimitiveType(PrimitiveType.Code.INT));
		
		VariableDeclaration var2 = ast.newVariableDeclaration(ast.newVariableDeclarationFragment(ast.newSimpleName("var2")));
		var2.setType(ast.newPrimitiveType(PrimitiveType.Code.LONG));
		
		agg.declarations().add(var1);
		agg.declarations().add(var2);
		
		assertEqualsTokenByToken("/** Some comment */ public abstract class NewClass(T, U) : One, Two { int var1; long var2; }", end());
	}

}

