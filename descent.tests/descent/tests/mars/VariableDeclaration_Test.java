package descent.tests.mars;

import descent.core.dom.IArrayType;
import descent.core.dom.ICompilationUnit;
import descent.core.dom.IDelegateType;
import descent.core.dom.IDElement;
import descent.core.dom.ITemplateInstanceType;
import descent.core.dom.IType;
import descent.core.dom.IVariableDeclaration;
import descent.internal.core.dom.ParserFacade;

public class VariableDeclaration_Test extends Parser_Test {
	
	public void testOne() {
		String s = " int x;";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IDElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IVariableDeclaration var = (IVariableDeclaration) declDefs[0];
		assertEquals(IDElement.VARIABLE_DECLARATION, var.getElementType());
		assertEquals("x", var.getName().toString());
		assertPosition(var.getName(), 5, 1);
		assertEquals("int", var.getType().toString());
		assertPosition(var, 1, 6);
		
		assertNull(var.getInitializer());
	}
	
	public void testTwo() {
		String s = " int x, y;";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IDElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(2, declDefs.length);
		
		IVariableDeclaration var;
		
		var = (IVariableDeclaration) declDefs[0];
		assertEquals("x", var.getName().toString());
		assertPosition(var.getName(), 5, 1);
		assertEquals("int", var.getType().toString());
		assertPosition(var, 1, 5);
		
		var = (IVariableDeclaration) declDefs[1];
		assertEquals("y", var.getName().toString());
		assertPosition(var.getName(), 8, 1);
		assertEquals("int", var.getType().toString());
		assertPosition(var, 8, 2);
	}
	
	public void testComments() {
		String s = " /** hola */ int x;";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IDElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IVariableDeclaration var = (IVariableDeclaration) declDefs[0];
		assertEquals("hola", var.getComments());
		assertPosition(var, 1, 18);
	}
	
	public void testCStyle() {
		String s = " int x[3];";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IDElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IVariableDeclaration var = (IVariableDeclaration) declDefs[0];
		assertEquals(IDElement.VARIABLE_DECLARATION, var.getElementType());
		assertEquals("x", var.getName().toString());
		assertPosition(var.getName(), 5, 1);
		assertEquals("int[3]", var.getType().toString());
		assertPosition(var, 1, 9);
	}
	
	public void testCStyle2() {
		String s = " int x[3][5];";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IDElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IVariableDeclaration var = (IVariableDeclaration) declDefs[0];
		assertEquals(IDElement.VARIABLE_DECLARATION, var.getElementType());
		assertEquals("x", var.getName().toString());
		assertPosition(var.getName(), 5, 1);
		assertEquals("int[3][5]", var.getType().toString());
		assertPosition(var, 1, 12);
	}
	
	public void testCStyle3() {
		String s = " int (*x[5])[3];";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IDElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IVariableDeclaration var = (IVariableDeclaration) declDefs[0];
		assertEquals(IDElement.VARIABLE_DECLARATION, var.getElementType());
		assertEquals("x", var.getName().toString());
		assertPosition(var.getName(), 7, 1);
		assertEquals("int[3]*[5]", var.getType().toString());
		assertPosition(var, 1, 15);
	}
	
	public void testCStyle5() {
		String s = " int (*x)(char);";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IDElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IVariableDeclaration var = (IVariableDeclaration) declDefs[0];
		assertEquals(IDElement.VARIABLE_DECLARATION, var.getElementType());
		assertEquals("x", var.getName().toString());
		assertPosition(var.getName(), 7, 1);
		assertEquals(IType.TYPE_POINTER_TO_FUNCTION, var.getType().getTypeType());
		assertPosition(var, 1, 15);
		
		IDelegateType del = (IDelegateType) var.getType();
		assertEquals("char", del.getArguments()[0].getType().toString());
		assertEquals("int", del.getReturnType().toString());
	}
	
	public void testCStyle6() {
		String s = " int (*[] x)(char);";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IDElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IVariableDeclaration var = (IVariableDeclaration) declDefs[0];
		assertEquals(IDElement.VARIABLE_DECLARATION, var.getElementType());
		assertEquals("x", var.getName().toString());
		assertPosition(var.getName(), 10, 1);
		assertEquals(IType.TYPE_ARRAY, var.getType().getTypeType());
		assertPosition(var, 1, 18);
		
		IArrayType array = (IArrayType) var.getType();
		assertEquals(IType.TYPE_POINTER_TO_FUNCTION, array.getInnerType().getTypeType());
		IDelegateType del = (IDelegateType) array.getInnerType();
		assertEquals("char", del.getArguments()[0].getType().toString());
		assertEquals("int", del.getReturnType().toString());
	}
	
	public void testAuto() {
		String s = " auto x = 1;";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IDElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IVariableDeclaration var = (IVariableDeclaration) declDefs[0];
		assertEquals(IDElement.VARIABLE_DECLARATION, var.getElementType());
		assertNull(var.getType());
	}
	
	public void testTemplate() {
		String s = " a.b.Temp!(int) x = 1;";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IDElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IVariableDeclaration var = (IVariableDeclaration) declDefs[0];
		assertEquals(IDElement.VARIABLE_DECLARATION, var.getElementType());
		
		IType type = var.getType();
		assertEquals(IType.TYPE_TEMPLATE_INSTANCE, type.getTypeType());
		ITemplateInstanceType ti = (ITemplateInstanceType) type;
		assertEquals("a.b.Temp", ti.getName().toString());
		assertEquals("Temp", ti.getShortName());
		
		IDElement[] args = ti.getTemplateArguments();
		assertEquals(1, args.length);
		assertEquals("int", args[0].toString());
	}
	
	public void testTemplate2() {
		String s = " Temp!(int) x = 1;";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IDElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IVariableDeclaration var = (IVariableDeclaration) declDefs[0];
		assertEquals(IDElement.VARIABLE_DECLARATION, var.getElementType());
		
		IType type = var.getType();
		assertEquals(IType.TYPE_TEMPLATE_INSTANCE, type.getTypeType());
		ITemplateInstanceType ti = (ITemplateInstanceType) type;
		assertEquals("Temp", ti.getName().toString());
		assertEquals("Temp", ti.getShortName());
		
		IDElement[] args = ti.getTemplateArguments();
		assertEquals(1, args.length);
		assertEquals("int", args[0].toString());
	}
	
}
