package descent.tests.rewrite;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;

import descent.core.dom.AggregateDeclaration;
import descent.core.dom.BaseClass;
import descent.core.dom.Modifier;
import descent.core.dom.TypeTemplateParameter;
import descent.core.dom.rewrite.ListRewrite;

public class RewriteAggregateDeclarationTest extends RewriteTest {
	
	public void testAddPreDDoc() throws Exception {
		begin(" class X { }");
		
		AggregateDeclaration agg = (AggregateDeclaration) unit.declarations().get(0);
		ListRewrite lrw = rewriter.getListRewrite(agg, AggregateDeclaration.PRE_D_DOCS_PROPERTY);
		lrw.insertFirst(ast.newDDocComment("/** Some comment */\n"), null);
		
		assertEqualsTokenByToken("/** Some comment */ class X { }", end());
	}
	
	public void testAddModifiers() throws Exception {
		begin(" class X { }");
		
		AggregateDeclaration agg = (AggregateDeclaration) unit.declarations().get(0);
		
		ListRewrite lrw = rewriter.getListRewrite(agg, AggregateDeclaration.MODIFIERS_PROPERTY);
		Modifier publicModifier = ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD);
		Modifier abstractModifier = ast.newModifier(Modifier.ModifierKeyword.ABSTRACT_KEYWORD);
		
		lrw.insertFirst(publicModifier, null);
		lrw.insertAfter(abstractModifier, publicModifier, null);
		
		assertEqualsTokenByToken("public abstract class X { }", end());
	}
	
	public void testRemoveModifier() throws Exception {
		begin(" public abstract class X { }");
		
		AggregateDeclaration agg = (AggregateDeclaration) unit.declarations().get(0);
		
		ListRewrite lrw = rewriter.getListRewrite(agg, AggregateDeclaration.MODIFIERS_PROPERTY);
		lrw.remove(agg.modifiers().get(0), null);
		
		assertEqualsTokenByToken("abstract class X { }", end());
	}
	
	public void testChangeKind() throws Exception {
		begin(" class X { }");
		
		AggregateDeclaration agg = (AggregateDeclaration) unit.declarations().get(0);
		
		rewriter.set(agg, AggregateDeclaration.KIND_PROPERTY, AggregateDeclaration.Kind.INTERFACE, null);
		
		assertEqualsTokenByToken("interface X { }", end());
	}
	
	public void testAddName() throws Exception {
		begin(" union { }");
		
		AggregateDeclaration agg = (AggregateDeclaration) unit.declarations().get(0);
		
		rewriter.set(agg, AggregateDeclaration.NAME_PROPERTY, ast.newSimpleName("New"), null);
		
		assertEqualsTokenByToken("union New { }", end());
	}
	
	public void testChangeName() throws Exception {
		begin(" class Old { }");
		
		AggregateDeclaration agg = (AggregateDeclaration) unit.declarations().get(0);
		
		rewriter.set(agg, AggregateDeclaration.NAME_PROPERTY, ast.newSimpleName("New"), null);
		
		assertEqualsTokenByToken("class New { }", end());
	}
	
	public void testRemoveName() throws Exception {
		begin(" union Old { }");
		
		AggregateDeclaration agg = (AggregateDeclaration) unit.declarations().get(0);
		rewriter.remove(agg.getName(), null);
		
		assertEqualsTokenByToken("union { }", end());
	}
	
	public void testAddTemplateParameters() throws Exception {
		begin(" class X { }");
		
		AggregateDeclaration agg = (AggregateDeclaration) unit.declarations().get(0);
		
		TypeTemplateParameter param1 = ast.newTypeTemplateParameter();
		param1.setName(ast.newSimpleName("T"));
		
		TypeTemplateParameter param2 = ast.newTypeTemplateParameter();
		param2.setName(ast.newSimpleName("U"));
		
		ListRewrite lrw = rewriter.getListRewrite(agg, AggregateDeclaration.TEMPLATE_PARAMETERS_PROPERTY);
		lrw.insertFirst(param1, null);
		lrw.insertAfter(param2, param1, null);
		
		assertEqualsTokenByToken("class X(T, U) { }", end());
	}
	
	public void testRemoveTemplateParameters() throws Exception {
		begin(" class X(T, U) { }");
		
		AggregateDeclaration agg = (AggregateDeclaration) unit.declarations().get(0);
		
		ListRewrite lrw = rewriter.getListRewrite(agg, AggregateDeclaration.TEMPLATE_PARAMETERS_PROPERTY);
		lrw.remove(agg.templateParameters().get(0), null);
		lrw.remove(agg.templateParameters().get(1), null);
		
		assertEqualsTokenByToken("class X { }", end());
	}
	
	public void testChangeTemplateParameters() throws Exception {
		begin(" class X(T, U) { }");
		
		AggregateDeclaration agg = (AggregateDeclaration) unit.declarations().get(0);
		
		TypeTemplateParameter param1 = ast.newTypeTemplateParameter();
		param1.setName(ast.newSimpleName("V"));
		
		ListRewrite lrw = rewriter.getListRewrite(agg, AggregateDeclaration.TEMPLATE_PARAMETERS_PROPERTY);
		lrw.replace(agg.templateParameters().get(0),param1,  null);
		
		assertEqualsTokenByToken("class X(V, U) { }", end());
	}
	
	public void testAddBaseClasses() throws Exception {
		begin(" class X { }");
		
		AggregateDeclaration agg = (AggregateDeclaration) unit.declarations().get(0);
		
		BaseClass base1 = ast.newBaseClass();
		base1.setType(ast.newSimpleType(ast.newSimpleName("One")));
		
		BaseClass base2 = ast.newBaseClass();
		base2.setType(ast.newSimpleType(ast.newSimpleName("Two")));
		
		ListRewrite lrw = rewriter.getListRewrite(agg, AggregateDeclaration.BASE_CLASSES_PROPERTY);
		lrw.insertFirst(base1, null);
		lrw.insertAfter(base2, base1, null);
		
		assertEqualsTokenByToken("class X : One, Two { }", end());
	}
	
	public void testRemoveBaseClasses() throws Exception {
		begin(" class X : One, Two { }");
		
		AggregateDeclaration agg = (AggregateDeclaration) unit.declarations().get(0);
		
		ListRewrite lrw = rewriter.getListRewrite(agg, AggregateDeclaration.BASE_CLASSES_PROPERTY);
		lrw.remove(agg.baseClasses().get(0), null);
		lrw.remove(agg.baseClasses().get(1), null);
		
		assertEqualsTokenByToken("class X { }", end());
	}
	
	public void testMultiChange1() throws Exception {
		begin(" class X { }");
		
		AggregateDeclaration agg = (AggregateDeclaration) unit.declarations().get(0);
		ListRewrite lrw;
		
		// Add comments
		lrw = rewriter.getListRewrite(agg, AggregateDeclaration.PRE_D_DOCS_PROPERTY);
		lrw.insertFirst(ast.newDDocComment("/** Some comment */\n"), null);
		
		// Add modifiers
		lrw = rewriter.getListRewrite(agg, AggregateDeclaration.MODIFIERS_PROPERTY);
		Modifier publicModifier = ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD);
		Modifier abstractModifier = ast.newModifier(Modifier.ModifierKeyword.ABSTRACT_KEYWORD);
		lrw.insertFirst(publicModifier, null);
		lrw.insertAfter(abstractModifier, publicModifier, null);
		
		// Change type
		rewriter.set(agg, AggregateDeclaration.KIND_PROPERTY, AggregateDeclaration.Kind.UNION, null);
		
		// Remove name
		rewriter.remove(agg.getName(), null);
		
		assertEqualsTokenByToken("/** Some comment */ public abstract union { }", end());
	}
	
	public void testMultiChange2() throws Exception {
		begin(" union { }");
		
		AggregateDeclaration agg = (AggregateDeclaration) unit.declarations().get(0);
		ListRewrite lrw;
		
		// Add comments
		lrw = rewriter.getListRewrite(agg, AggregateDeclaration.PRE_D_DOCS_PROPERTY);
		lrw.insertFirst(ast.newDDocComment("/** Some comment */\n"), null);
		
		// Add modifiers
		lrw = rewriter.getListRewrite(agg, AggregateDeclaration.MODIFIERS_PROPERTY);
		Modifier publicModifier = ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD);
		Modifier abstractModifier = ast.newModifier(Modifier.ModifierKeyword.ABSTRACT_KEYWORD);
		lrw.insertFirst(publicModifier, null);
		lrw.insertAfter(abstractModifier, publicModifier, null);
		
		// Change type
		rewriter.set(agg, AggregateDeclaration.KIND_PROPERTY, AggregateDeclaration.Kind.CLASS, null);
		
		// Add name
		rewriter.set(agg, AggregateDeclaration.NAME_PROPERTY, ast.newSimpleName("NewClass"), null);
		
		// Add template parameters
		TypeTemplateParameter param1 = ast.newTypeTemplateParameter();
		param1.setName(ast.newSimpleName("T"));
		
		TypeTemplateParameter param2 = ast.newTypeTemplateParameter();
		param2.setName(ast.newSimpleName("U"));
		
		lrw = rewriter.getListRewrite(agg, AggregateDeclaration.TEMPLATE_PARAMETERS_PROPERTY);
		lrw.insertFirst(param1, null);
		lrw.insertAfter(param2, param1, null);		
		
		assertEqualsTokenByToken("/** Some comment */ public abstract class NewClass(T, U) { }", end());
	}

}
