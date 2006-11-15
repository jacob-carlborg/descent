package descent.tests.mars;

import descent.core.dom.IArrayInitializer;
import descent.core.dom.IDElement;
import descent.core.dom.IExpression;
import descent.core.dom.IExpressionInitializer;
import descent.core.dom.IInitializer;
import descent.core.dom.IName;
import descent.core.dom.IStructInitializer;
import descent.internal.core.dom.ParserFacade;

public class Initializer_Test extends Parser_Test {
	
	public void testVoid() {
		String s = " void;";
		IInitializer init = new ParserFacade().parseInitializer(s);
		assertEquals(IDElement.INITIALIZER, init.getElementType());
		assertEquals(IInitializer.VOID_INITIALIZER, init.getInitializerType());
		assertPosition(init, 1, 4);
		
		assertVisitor(init, 1);
	}
	
	public void testExpression() {
		String s = " 1;";
		IExpressionInitializer init = (IExpressionInitializer) new ParserFacade().parseInitializer(s);
		assertEquals(IInitializer.EXPRESSION_INITIALIZER, init.getInitializerType());
		assertEquals("1", init.getExpression().toString());
		assertPosition(init, 1, 1);
		
		assertVisitor(init, 2);
	}
	
	public void testStructEmpty() {
		String s = " { };";
		IStructInitializer init = (IStructInitializer) new ParserFacade().parseInitializer(s);
		assertEquals(IInitializer.STRUCT_INITIALIZER, init.getInitializerType());
		assertPosition(init, 1, 3);
		
		assertEquals(0, init.getNames().length);
		assertEquals(0, init.getValues().length);
	}
	
	public void testStructInitializers() {
		String s = " { a = 2, b = 3 };";
		IStructInitializer init = (IStructInitializer) new ParserFacade().parseInitializer(s);
		assertEquals(IInitializer.STRUCT_INITIALIZER, init.getInitializerType());
		assertPosition(init, 1, s.length() - 2);
		
		IName[] names = init.getNames();
		assertEquals(2, names.length);
		
		assertNull(names[0]);
		assertNull(names[1]);
		
		IInitializer[] values = init.getValues();
		assertEquals(2, values.length);
		
		assertPosition(values[0], 3, 5);
		assertPosition(values[1], 10, 5);
		
		assertEquals(IInitializer.EXPRESSION_INITIALIZER, values[0].getInitializerType());
		assertEquals(IInitializer.EXPRESSION_INITIALIZER, values[1].getInitializerType());
	}
	
	public void testStructInitializers2() {
		String s = " { a : b = 2 };";
		IStructInitializer init = (IStructInitializer) new ParserFacade().parseInitializer(s);
		assertEquals(IInitializer.STRUCT_INITIALIZER, init.getInitializerType());
		assertPosition(init, 1, s.length() - 2);
		
		IName[] names = init.getNames();
		assertEquals(1, names.length);
		
		assertEquals("a", names[0].toString());
		assertPosition(names[0], 3, 1);
	}
	
	public void testStructInitializers3() {
		String s = " { 2 };";
		IStructInitializer init = (IStructInitializer) new ParserFacade().parseInitializer(s);
		assertEquals(IInitializer.STRUCT_INITIALIZER, init.getInitializerType());
		assertPosition(init, 1, s.length() - 2);
		
		IName[] names = init.getNames();
		assertEquals(1, names.length);
		assertNull(names[0]);
		
		IInitializer[] values = init.getValues();
		assertEquals(IInitializer.EXPRESSION_INITIALIZER, values[0].getInitializerType());
	}
	
	public void testArray() {
		String s = " [ ];";
		IArrayInitializer init = (IArrayInitializer) new ParserFacade().parseInitializer(s);
		assertEquals(IInitializer.ARRAY_INITIALIZER, init.getInitializerType());
		assertPosition(init, 1, s.length() - 2);
	}
	
	public void testArrayInitializers() {
		String s = " [ 1, 2, 3 ];";
		IArrayInitializer init = (IArrayInitializer) new ParserFacade().parseInitializer(s);
		assertEquals(IInitializer.ARRAY_INITIALIZER, init.getInitializerType());
		assertPosition(init, 1, s.length() - 2);
		
		IExpression[] exps = init.getLengths();
		assertEquals(3, exps.length);
		
		assertNull(exps[0]);
		assertNull(exps[1]);
		assertNull(exps[2]);
		
		IInitializer[] values = init.getValues();
		assertEquals(3, values.length);
		
		assertPosition(values[0], 3, 1);
		
		IExpressionInitializer expInit = (IExpressionInitializer) values[0];
		IExpression exp = expInit.getExpression();
		assertEquals("1", exp.toString());
	}
	
	public void testArrayInitializers2() {
		String s = " [ 2 : 1 ];";
		IArrayInitializer init = (IArrayInitializer) new ParserFacade().parseInitializer(s);
		assertEquals(IInitializer.ARRAY_INITIALIZER, init.getInitializerType());
		assertPosition(init, 1, s.length() - 2);
		
		IExpression[] exps = init.getLengths();
		assertEquals(1, exps.length);
		
		assertEquals("2", exps[0].toString());
		assertPosition(exps[0], 3, 1);
		
		IInitializer[] values = init.getValues();
		assertEquals(1, values.length);
		
		assertPosition(values[0], 7, 1);
		
		IExpressionInitializer expInit = (IExpressionInitializer) values[0];
		IExpression exp = expInit.getExpression();
		assertEquals("1", exp.toString());
	}
	
	public void testArrayInitializersNested() {
		String s = " [ [ 1 ] ];";
		IArrayInitializer init = (IArrayInitializer) new ParserFacade().parseInitializer(s);
		assertEquals(IInitializer.ARRAY_INITIALIZER, init.getInitializerType());
		assertPosition(init, 1, s.length() - 2);
		
		IExpression[] exps = init.getLengths();
		assertEquals(1, exps.length);
	}

}
