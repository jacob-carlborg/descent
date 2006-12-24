package descent.tests.mars;

import java.util.List;

import descent.core.dom.ASTNode;
import descent.core.dom.AggregateDeclaration;
import descent.core.dom.AliasTemplateParameter;
import descent.core.dom.FunctionDeclaration;
import descent.core.dom.NumberLiteral;
import descent.core.dom.SimpleType;
import descent.core.dom.TemplateDeclaration;
import descent.core.dom.TemplateParameter;
import descent.core.dom.TupleTemplateParameter;
import descent.core.dom.TypeTemplateParameter;
import descent.core.dom.ValueTemplateParameter;

public class Template_Test extends Parser_Test {
	
	public void testEmpty() {
		String s = " template Temp() { }";
		TemplateDeclaration t = (TemplateDeclaration) getSingleDeclarationNoProblems(s);
		assertEquals(ASTNode.TEMPLATE_DECLARATION, t.getNodeType0());
		assertPosition(t, 1, 19);
		
		assertEquals("Temp", t.getName().getIdentifier());
		assertPosition(t.getName(), 10, 4);
		
		assertEquals(0, t.templateParameters().size());
	}
	
	public void testDeclDefs() {
		String s = " template Temp() { int x; }";
		TemplateDeclaration t = (TemplateDeclaration) getSingleDeclarationNoProblems(s);
		
		assertEquals(1, t.declarations().size());
	}
	
	public void testParameters() {
		String s = " template Temp(T, U : U*, V : int, W = int, alias A : B = C) { }";
		TemplateDeclaration t = (TemplateDeclaration) getSingleDeclarationNoProblems(s);
		
		List<TemplateParameter> tp = t.templateParameters();
		assertEquals(5, tp.size());
		
		TypeTemplateParameter ttp;
		
		assertEquals(ASTNode.TYPE_TEMPLATE_PARAMETER, tp.get(0).getNodeType0());
		ttp = (TypeTemplateParameter) tp.get(0);
		assertEquals("T", ttp.getName().getIdentifier());
		assertPosition(ttp, 15, 1);
		assertNull(ttp.getSpecificType());
		assertNull(ttp.getDefaultType());
		
		assertEquals(ASTNode.TYPE_TEMPLATE_PARAMETER, tp.get(1).getNodeType0());
		ttp = (TypeTemplateParameter) tp.get(1);		
		assertEquals("U", ttp.getName().getIdentifier());
		assertPosition(ttp, 18, 6);
		assertEquals(ASTNode.POINTER_TYPE, ttp.getSpecificType().getNodeType0());
		assertNull(ttp.getDefaultType());
		
		assertEquals(ASTNode.TYPE_TEMPLATE_PARAMETER, tp.get(2).getNodeType0());
		ttp = (TypeTemplateParameter) tp.get(2);		
		assertEquals("V", ttp.getName().getIdentifier());
		assertPosition(ttp, 26, 7);
		assertEquals(ASTNode.PRIMITIVE_TYPE, ttp.getSpecificType().getNodeType0());
		assertNull(ttp.getDefaultType());
		
		assertEquals(ASTNode.TYPE_TEMPLATE_PARAMETER, tp.get(3).getNodeType0());
		ttp = (TypeTemplateParameter) tp.get(3);		
		assertEquals("W", ttp.getName().getIdentifier());
		assertPosition(ttp, 35, 7);
		assertNull(ttp.getSpecificType());
		assertEquals(ASTNode.PRIMITIVE_TYPE, ttp.getDefaultType().getNodeType0());
		
		assertEquals(ASTNode.ALIAS_TEMPLATE_PARAMETER, tp.get(4).getNodeType0());
		AliasTemplateParameter tap = (AliasTemplateParameter) tp.get(4);		
		assertEquals("A", tap.getName().getIdentifier());
		assertPosition(tap, 44, 15);
		assertEquals("B", ((SimpleType) tap.getSpecificType()).getName().getFullyQualifiedName());
		assertEquals("C", ((SimpleType) tap.getDefaultType()).getName().getFullyQualifiedName());
	}
	
	public void testParametersTuple() {
		String s = " template Temp(T ...) { }";
		TemplateDeclaration t = (TemplateDeclaration) getSingleDeclarationNoProblems(s);
		
		List<TemplateParameter> tp = t.templateParameters();
		assertEquals(1, tp.size());
		
		TupleTemplateParameter param = (TupleTemplateParameter) tp.get(0);
		assertEquals(ASTNode.TUPLE_TEMPLATE_PARAMETER, param.getNodeType0());
		assertEquals("T", param.getName().getIdentifier());
		assertPosition(param, 15, 5);
		assertPosition(param.getName(), 15, 1);
	}
	
	public void testParametersValue() {
		String s = " template Temp(int T : 2 = 3) { }";
		TemplateDeclaration t = (TemplateDeclaration) getSingleDeclarationNoProblems(s);
		
		List<TemplateParameter> tp = t.templateParameters();
		assertEquals(1, tp.size());
		
		ValueTemplateParameter param = (ValueTemplateParameter) tp.get(0);
		assertEquals(ASTNode.VALUE_TEMPLATE_PARAMETER, param.getNodeType0());
		assertEquals("T", param.getName().getIdentifier());
		assertPosition(param, 15, 13);
		assertPosition(param.getName(), 19, 1);
		
		assertEquals("int", param.getType().toString());
		assertEquals("2", ((NumberLiteral) param.getSpecificValue()).getToken());
		assertEquals("3", ((NumberLiteral) param.getDefaultValue()).getToken());
	}
	
	public void testAggregate() {
		String s = " class Bla(T) { }";
		AggregateDeclaration c = (AggregateDeclaration) getSingleDeclarationNoProblems(s);
		assertFalse(c.templateParameters().isEmpty());
		assertEquals(1, c.templateParameters().size());
	}
	
	public void testFunction() {
		String s = " T Square(T)(T t) { return t * t; }";
		FunctionDeclaration f = (FunctionDeclaration) getSingleDeclarationNoProblems(s);
		assertTrue(f.templateParameters().size() > 0);
		assertEquals(1, f.templateParameters().size());
	}

}
