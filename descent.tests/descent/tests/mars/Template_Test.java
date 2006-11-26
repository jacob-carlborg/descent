package descent.tests.mars;

import descent.core.dom.IAggregateDeclaration;
import descent.core.dom.ICompilationUnit;
import descent.core.dom.IDElement;
import descent.core.dom.IFunctionDeclaration;
import descent.core.dom.IAliasTemplateParameter;
import descent.core.dom.ITemplateDeclaration;
import descent.core.dom.ITemplateParameter;
import descent.core.dom.ITupleTemplateParameter;
import descent.core.dom.ITypeTemplateParameter;
import descent.core.dom.IValueTemplateParameter;
import descent.core.dom.IType;
import descent.internal.core.dom.ParserFacade;

public class Template_Test extends Parser_Test {
	
	public void testEmpty() {
		String s = " template Temp() { }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IDElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		ITemplateDeclaration t = (ITemplateDeclaration) declDefs[0];
		assertEquals(IDElement.TEMPLATE_DECLARATION, t.getElementType());
		assertPosition(t, 1, 19);
		
		assertEquals("Temp", t.getName().toString());
		assertPosition(t.getName(), 10, 4);
		
		assertEquals(0, t.getTemplateParameters().length);
		
		assertVisitor(t, 2);
	}
	
	public void testDeclDefs() {
		String s = " template Temp() { int x; }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IDElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		ITemplateDeclaration t = (ITemplateDeclaration) declDefs[0];
		
		assertEquals(1, t.getDeclarationDefinitions().length);
		
		assertVisitor(t, 5);
	}
	
	public void testParameters() {
		String s = " template Temp(T, U : U*, V : int, W = int, alias A : B = C) { }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IDElement[] declDefs = unit.getDeclarationDefinitions();
		
		ITemplateDeclaration t = (ITemplateDeclaration) declDefs[0];
		
		ITemplateParameter[] tp = t.getTemplateParameters();
		assertEquals(5, tp.length);
		
		ITypeTemplateParameter ttp;
		
		assertEquals(ITemplateParameter.TYPE_TEMPLATE_PARAMETER, tp[0].getElementType());
		ttp = (ITypeTemplateParameter) tp[0];		
		assertEquals("T", ttp.getName().toString());
		assertPosition(ttp, 15, 1);
		assertNull(ttp.getSpecificType());
		assertNull(ttp.getDefaultType());
		
		assertEquals(ITemplateParameter.TYPE_TEMPLATE_PARAMETER, tp[1].getElementType());
		ttp = (ITypeTemplateParameter) tp[1];		
		assertEquals("U", ttp.getName().toString());
		assertPosition(ttp, 18, 6);
		assertEquals(IType.POINTER_TYPE, ttp.getSpecificType().getElementType());
		assertNull(ttp.getDefaultType());
		
		assertEquals(ITemplateParameter.TYPE_TEMPLATE_PARAMETER, tp[2].getElementType());
		ttp = (ITypeTemplateParameter) tp[2];		
		assertEquals("V", ttp.getName().toString());
		assertPosition(ttp, 26, 7);
		assertEquals(IType.BASIC_TYPE, ttp.getSpecificType().getElementType());
		assertNull(ttp.getDefaultType());
		
		assertEquals(ITemplateParameter.TYPE_TEMPLATE_PARAMETER, tp[3].getElementType());
		ttp = (ITypeTemplateParameter) tp[3];		
		assertEquals("W", ttp.getName().toString());
		assertPosition(ttp, 35, 7);
		assertNull(ttp.getSpecificType());
		assertEquals(IType.BASIC_TYPE, ttp.getDefaultType().getElementType());
		
		assertEquals(ITemplateParameter.ALIAS_TEMPLATE_PARAMETER, tp[4].getElementType());
		IAliasTemplateParameter tap = (IAliasTemplateParameter) tp[4];		
		assertEquals("A", tap.getName().toString());
		assertPosition(tap, 44, 15);
		assertEquals("B", tap.getSpecificType().toString());
		assertEquals("C", tap.getDefaultType().toString());
		
		assertVisitor(t, 18);
	}
	
	public void testParametersTuple() {
		String s = " template Temp(T ...) { }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IDElement[] declDefs = unit.getDeclarationDefinitions();
		
		ITemplateDeclaration t = (ITemplateDeclaration) declDefs[0];
		
		ITemplateParameter[] tp = t.getTemplateParameters();
		assertEquals(1, tp.length);
		
		ITupleTemplateParameter param = (ITupleTemplateParameter) tp[0];
		assertEquals(ITemplateParameter.TUPLE_TEMPLATE_PARAMETER, param.getElementType());
		assertEquals("T", param.getName().toString());
		assertPosition(param, 15, 5);
		assertPosition(param.getName(), 15, 1);
	}
	
	public void testParametersValue() {
		String s = " template Temp(int T : 2 = 3) { }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IDElement[] declDefs = unit.getDeclarationDefinitions();
		
		ITemplateDeclaration t = (ITemplateDeclaration) declDefs[0];
		
		ITemplateParameter[] tp = t.getTemplateParameters();
		assertEquals(1, tp.length);
		
		IValueTemplateParameter param = (IValueTemplateParameter) tp[0];
		assertEquals(ITemplateParameter.VALUE_TEMPLATE_PARAMETER, param.getElementType());
		assertEquals("T", param.getName().toString());
		assertPosition(param, 15, 13);
		assertPosition(param.getName(), 19, 1);
		
		assertEquals("int", param.getType().toString());
		assertEquals("2", param.getSpecificValue().toString());
		assertEquals("3", param.getDefaultValue().toString());
	}
	
	public void testAggregate() {
		String s = " class Bla(T) { }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IDElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IAggregateDeclaration c = (IAggregateDeclaration) declDefs[0];
		assertTrue(c.isTemplate());
		assertEquals(1, c.getTemplateParameters().length);
		
		assertVisitor(c, 4);
	}
	
	public void testFunction() {
		String s = " T Square(T)(T t) { return t * t; }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IDElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IFunctionDeclaration f = (IFunctionDeclaration) declDefs[0];
		assertTrue(f.isTemplate());
		assertEquals(1, f.getTemplateParameters().length);
	}

}
