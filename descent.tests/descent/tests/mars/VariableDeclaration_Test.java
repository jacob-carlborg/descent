package descent.tests.mars;

import descent.core.dom.IArrayType;
import descent.core.dom.IAssociativeArrayType;
import descent.core.dom.ICompilationUnit;
import descent.core.dom.IDelegateType;
import descent.core.dom.IElement;
import descent.core.dom.IModifier;
import descent.core.dom.ITemplateInstanceType;
import descent.core.dom.IType;
import descent.core.dom.IVariableDeclaration;
import descent.internal.core.dom.ParserFacade;

public class VariableDeclaration_Test extends Parser_Test {
	
	public void testOne() {
		String s = " int x;";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IVariableDeclaration var = (IVariableDeclaration) declDefs[0];
		assertEquals(IElement.VARIABLE_DECLARATION, var.getElementType());
		assertEquals("x", var.getName().toString());
		assertPosition(var.getName(), 5, 1);
		assertEquals("int", var.getType().toString());
		assertPosition(var, 1, 6);
		
		assertNull(var.getInitializer());
	}
	
	public void testTwo() {
		String s = " int x, y;";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
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
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IVariableDeclaration var = (IVariableDeclaration) declDefs[0];
		assertEquals("hola", var.getComments());
		assertPosition(var, 1, 18);
	}
	
	public void testCStyle() {
		String s = " int x[3];";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IVariableDeclaration var = (IVariableDeclaration) declDefs[0];
		assertEquals(IElement.VARIABLE_DECLARATION, var.getElementType());
		assertEquals("x", var.getName().toString());
		assertPosition(var.getName(), 5, 1);
		assertEquals("int[3]", var.getType().toString());
		assertPosition(var, 1, 9);
	}
	
	public void testCStyle2() {
		String s = " int x[3][5];";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IVariableDeclaration var = (IVariableDeclaration) declDefs[0];
		assertEquals(IElement.VARIABLE_DECLARATION, var.getElementType());
		assertEquals("x", var.getName().toString());
		assertPosition(var.getName(), 5, 1);
		assertEquals("int[3][5]", var.getType().toString());
		assertPosition(var, 1, 12);
	}
	
	public void testCStyle3() {
		String s = " int (*x[5])[3];";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IVariableDeclaration var = (IVariableDeclaration) declDefs[0];
		assertEquals(IElement.VARIABLE_DECLARATION, var.getElementType());
		assertEquals("x", var.getName().toString());
		assertPosition(var.getName(), 7, 1);
		assertEquals("int[3]*[5]", var.getType().toString());
		assertPosition(var, 1, 15);
	}
	
	public void testCStyle5() {
		String s = " int (*x)(char);";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IVariableDeclaration var = (IVariableDeclaration) declDefs[0];
		assertEquals(IElement.VARIABLE_DECLARATION, var.getElementType());
		assertEquals("x", var.getName().toString());
		assertPosition(var.getName(), 7, 1);
		assertEquals(IType.POINTER_TO_FUNCTION_TYPE, var.getType().getElementType());
		assertPosition(var, 1, 15);
		
		IDelegateType del = (IDelegateType) var.getType();
		assertEquals("char", del.getArguments()[0].getType().toString());
		assertEquals("int", del.getReturnType().toString());
	}
	
	public void testCStyle6() {
		String s = " int (*[] x)(char);";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IVariableDeclaration var = (IVariableDeclaration) declDefs[0];
		assertEquals(IElement.VARIABLE_DECLARATION, var.getElementType());
		assertEquals("x", var.getName().toString());
		assertPosition(var.getName(), 10, 1);
		assertEquals(IType.DYNAMIC_ARRAY_TYPE, var.getType().getElementType());
		assertPosition(var, 1, 18);
		
		IArrayType array = (IArrayType) var.getType();
		assertEquals(IType.POINTER_TO_FUNCTION_TYPE, array.getInnerType().getElementType());
		IDelegateType del = (IDelegateType) array.getInnerType();
		assertEquals("char", del.getArguments()[0].getType().toString());
		assertEquals("int", del.getReturnType().toString());
	}
	
	public void testAuto() {
		String s = " auto x = 1;";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IVariableDeclaration var = (IVariableDeclaration) declDefs[0];
		assertEquals(IElement.VARIABLE_DECLARATION, var.getElementType());
		assertNull(var.getType());
		assertTrue((var.getModifiers() & IModifier.AUTO) != 0);
	}
	
	public void testStatic() {
		String s = " static x = 1;";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IVariableDeclaration var = (IVariableDeclaration) declDefs[0];
		assertEquals(IElement.VARIABLE_DECLARATION, var.getElementType());
		assertNull(var.getType());
		assertTrue((var.getModifiers() & IModifier.STATIC) != 0);
	}
	
	public void testExtern() {
		String s = " extern x = 1;";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IVariableDeclaration var = (IVariableDeclaration) declDefs[0];
		assertEquals(IElement.VARIABLE_DECLARATION, var.getElementType());
		assertNull(var.getType());
		assertTrue((var.getModifiers() & IModifier.EXTERN) != 0);
	}
	
	public void testStaticAndOther() {
		Object[][] modifiers = {
				{ "const", IModifier.CONST },
				{ "final", IModifier.FINAL },	
				{ "auto", IModifier.AUTO },
				{ "override", IModifier.OVERRIDE },
				{ "abstract", IModifier.ABSTRACT },
				{ "synchronized", IModifier.SYNCHRONIZED },
				{ "deprecated", IModifier.DEPRECATED },
				{ "scope", IModifier.SCOPE },
		};
		
		for(Object[] modifier : modifiers) {
			String s = " static " + modifier[0] + " x = 1;";
			ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
			IElement[] declDefs = unit.getDeclarationDefinitions();
			assertEquals(1, declDefs.length);
			
			IVariableDeclaration var = (IVariableDeclaration) declDefs[0];
			assertEquals(IElement.VARIABLE_DECLARATION, var.getElementType());
			assertNull(var.getType());
			assertTrue((var.getModifiers() & IModifier.STATIC) != 0);
			assertTrue((var.getModifiers() & ((Integer) modifier[1])) != 0);
		}
	}
	
	public void testOther() {
		Object[][] modifiers = {
				{ "const", IModifier.CONST },
				{ "final", IModifier.FINAL },	
				{ "auto", IModifier.AUTO },
				{ "override", IModifier.OVERRIDE },
				{ "abstract", IModifier.ABSTRACT },
				{ "synchronized", IModifier.SYNCHRONIZED },
				{ "deprecated", IModifier.DEPRECATED },
		};
		
		for(Object[] modifier : modifiers) {
			String s = " alias " + modifier[0] + " x = 1;";
			ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
			IElement[] declDefs = unit.getDeclarationDefinitions();
			assertEquals(1, declDefs.length);
			
			IVariableDeclaration var = (IVariableDeclaration) declDefs[0];
			assertEquals(IElement.VARIABLE_DECLARATION, var.getElementType());
			assertNull(var.getType());
			assertTrue((var.getModifiers() & ((Integer) modifier[1])) != 0);
		}
	}
	
	public void testAssociativeArray() {
		String s = " char x[int] = 1;";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IVariableDeclaration var = (IVariableDeclaration) declDefs[0];
		assertEquals(IElement.VARIABLE_DECLARATION, var.getElementType());
		
		IAssociativeArrayType type = (IAssociativeArrayType) var.getType();
		assertEquals(IArrayType.ASSOCIATIVE_ARRAY_TYPE, type.getElementType());
		assertEquals("char", type.getInnerType().toString());
		assertEquals("int", type.getKeyType().toString());
	}
	
	public void testTemplate() {
		String s = " a.b.Temp!(int) x = 1;";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IVariableDeclaration var = (IVariableDeclaration) declDefs[0];
		assertEquals(IElement.VARIABLE_DECLARATION, var.getElementType());
		
		IType type = var.getType();
		assertEquals(IType.TEMPLATE_INSTANCE_TYPE, type.getElementType());
		ITemplateInstanceType ti = (ITemplateInstanceType) type;
		assertEquals("a.b.Temp", ti.getName().toString());
		assertEquals("Temp", ti.getShortName());
		
		IElement[] args = ti.getTemplateArguments();
		assertEquals(1, args.length);
		assertEquals("int", args[0].toString());
	}
	
	public void testTemplate2() {
		String s = " Temp!(int) x = 1;";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IVariableDeclaration var = (IVariableDeclaration) declDefs[0];
		assertEquals(IElement.VARIABLE_DECLARATION, var.getElementType());
		
		IType type = var.getType();
		assertEquals(IType.TEMPLATE_INSTANCE_TYPE, type.getElementType());
		ITemplateInstanceType ti = (ITemplateInstanceType) type;
		assertEquals("Temp", ti.getName().toString());
		assertEquals("Temp", ti.getShortName());
		
		IElement[] args = ti.getTemplateArguments();
		assertEquals(1, args.length);
		assertEquals("int", args[0].toString());
	}
	
	public void testTemplate3() {
		String s = " .Temp!(int) x = 1;";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IVariableDeclaration var = (IVariableDeclaration) declDefs[0];
		assertEquals(IElement.VARIABLE_DECLARATION, var.getElementType());
		
		IType type = var.getType();
		assertEquals(IType.TEMPLATE_INSTANCE_TYPE, type.getElementType());
		ITemplateInstanceType ti = (ITemplateInstanceType) type;
		assertEquals(".Temp", ti.getName().toString());
		assertEquals("Temp", ti.getShortName());
		
		IElement[] args = ti.getTemplateArguments();
		assertEquals(1, args.length);
		assertEquals("int", args[0].toString());
	}
	
	public void testUnicode() {
		String s = " int épa = 1;";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IVariableDeclaration var = (IVariableDeclaration) declDefs[0];
		assertEquals(IElement.VARIABLE_DECLARATION, var.getElementType());
		
		assertPosition(var.getName(), 5, 3);
		assertEquals("épa", var.getName().toString());
	}
	
}
