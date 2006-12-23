package descent.tests.mars;

import java.util.List;

import descent.core.dom.IAggregateDeclaration;
import descent.core.dom.IAliasTemplateParameter;
import descent.core.dom.ICompilationUnit;
import descent.core.dom.IElement;
import descent.core.dom.IFunctionDeclaration;
import descent.core.dom.ITemplateDeclaration;
import descent.core.dom.ITemplateParameter;
import descent.core.dom.ITupleTemplateParameter;
import descent.core.dom.IType;
import descent.core.dom.ITypeTemplateParameter;
import descent.core.dom.IValueTemplateParameter;
import descent.internal.core.dom.NumberLiteral;
import descent.internal.core.dom.ParserFacade;
import descent.internal.core.dom.SimpleType;
import descent.internal.core.dom.TemplateParameter;

public class Template_Test extends Parser_Test {
	
	public void testEmpty() {
		String s = " template Temp() { }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		assertEquals(0, unit.getProblems().length);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		ITemplateDeclaration t = (ITemplateDeclaration) declDefs[0];
		assertEquals(IElement.TEMPLATE_DECLARATION, t.getNodeType0());
		assertPosition(t, 1, 19);
		
		assertEquals("Temp", t.getName().getIdentifier());
		assertPosition(t.getName(), 10, 4);
		
		assertEquals(0, t.templateParameters().size());
		
		assertVisitor(t, 2);
	}
	
	public void testDeclDefs() {
		String s = " template Temp() { int x; }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		assertEquals(0, unit.getProblems().length);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		ITemplateDeclaration t = (ITemplateDeclaration) declDefs[0];
		
		assertEquals(1, t.declarations().size());
	}
	
	public void testParameters() {
		String s = " template Temp(T, U : U*, V : int, W = int, alias A : B = C) { }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		assertEquals(0, unit.getProblems().length);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		
		ITemplateDeclaration t = (ITemplateDeclaration) declDefs[0];
		
		List<TemplateParameter> tp = t.templateParameters();
		assertEquals(5, tp.size());
		
		ITypeTemplateParameter ttp;
		
		assertEquals(ITemplateParameter.TYPE_TEMPLATE_PARAMETER, tp.get(0).getNodeType0());
		ttp = (ITypeTemplateParameter) tp.get(0);
		assertEquals("T", ttp.getName().getIdentifier());
		assertPosition(ttp, 15, 1);
		assertNull(ttp.getSpecificType());
		assertNull(ttp.getDefaultType());
		
		assertEquals(ITemplateParameter.TYPE_TEMPLATE_PARAMETER, tp.get(1).getNodeType0());
		ttp = (ITypeTemplateParameter) tp.get(1);		
		assertEquals("U", ttp.getName().getIdentifier());
		assertPosition(ttp, 18, 6);
		assertEquals(IType.POINTER_TYPE, ttp.getSpecificType().getNodeType0());
		assertNull(ttp.getDefaultType());
		
		assertEquals(ITemplateParameter.TYPE_TEMPLATE_PARAMETER, tp.get(2).getNodeType0());
		ttp = (ITypeTemplateParameter) tp.get(2);		
		assertEquals("V", ttp.getName().getIdentifier());
		assertPosition(ttp, 26, 7);
		assertEquals(IType.PRIMITIVE_TYPE, ttp.getSpecificType().getNodeType0());
		assertNull(ttp.getDefaultType());
		
		assertEquals(ITemplateParameter.TYPE_TEMPLATE_PARAMETER, tp.get(3).getNodeType0());
		ttp = (ITypeTemplateParameter) tp.get(3);		
		assertEquals("W", ttp.getName().getIdentifier());
		assertPosition(ttp, 35, 7);
		assertNull(ttp.getSpecificType());
		assertEquals(IType.PRIMITIVE_TYPE, ttp.getDefaultType().getNodeType0());
		
		assertEquals(ITemplateParameter.ALIAS_TEMPLATE_PARAMETER, tp.get(4).getNodeType0());
		IAliasTemplateParameter tap = (IAliasTemplateParameter) tp.get(4);		
		assertEquals("A", tap.getName().getIdentifier());
		assertPosition(tap, 44, 15);
		assertEquals("B", ((SimpleType) tap.getSpecificType()).getName().getFullyQualifiedName());
		assertEquals("C", ((SimpleType) tap.getDefaultType()).getName().getFullyQualifiedName());
	}
	
	public void testParametersTuple() {
		String s = " template Temp(T ...) { }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		assertEquals(0, unit.getProblems().length);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		
		ITemplateDeclaration t = (ITemplateDeclaration) declDefs[0];
		
		List<TemplateParameter> tp = t.templateParameters();
		assertEquals(1, tp.size());
		
		ITupleTemplateParameter param = (ITupleTemplateParameter) tp.get(0);
		assertEquals(ITemplateParameter.TUPLE_TEMPLATE_PARAMETER, param.getNodeType0());
		assertEquals("T", param.getName().getIdentifier());
		assertPosition(param, 15, 5);
		assertPosition(param.getName(), 15, 1);
	}
	
	public void testParametersValue() {
		String s = " template Temp(int T : 2 = 3) { }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		assertEquals(0, unit.getProblems().length);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		
		ITemplateDeclaration t = (ITemplateDeclaration) declDefs[0];
		
		List<TemplateParameter> tp = t.templateParameters();
		assertEquals(1, tp.size());
		
		IValueTemplateParameter param = (IValueTemplateParameter) tp.get(0);
		assertEquals(ITemplateParameter.VALUE_TEMPLATE_PARAMETER, param.getNodeType0());
		assertEquals("T", param.getName().getIdentifier());
		assertPosition(param, 15, 13);
		assertPosition(param.getName(), 19, 1);
		
		assertEquals("int", param.getType().toString());
		assertEquals("2", ((NumberLiteral) param.getSpecificValue()).getToken());
		assertEquals("3", ((NumberLiteral) param.getDefaultValue()).getToken());
	}
	
	public void testAggregate() {
		String s = " class Bla(T) { }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		assertEquals(0, unit.getProblems().length);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IAggregateDeclaration c = (IAggregateDeclaration) declDefs[0];
		assertFalse(c.templateParameters().isEmpty());
		assertEquals(1, c.templateParameters().size());
		
		assertVisitor(c, 4);
	}
	
	public void testFunction() {
		String s = " T Square(T)(T t) { return t * t; }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		assertEquals(0, unit.getProblems().length);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IFunctionDeclaration f = (IFunctionDeclaration) declDefs[0];
		assertTrue(f.templateParameters().size() > 0);
		assertEquals(1, f.templateParameters().size());
	}

}
