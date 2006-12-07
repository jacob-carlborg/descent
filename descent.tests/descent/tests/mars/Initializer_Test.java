package descent.tests.mars;

import java.util.List;

import descent.core.dom.IArrayInitializer;
import descent.core.dom.IExpression;
import descent.core.dom.IExpressionInitializer;
import descent.core.dom.IInitializer;
import descent.core.dom.ISimpleName;
import descent.core.dom.IStructInitializer;
import descent.internal.core.dom.ArrayInitializerFragment;
import descent.internal.core.dom.ParserFacade;

public class Initializer_Test extends Parser_Test {
	
	public void testVoid() {
		String s = " void;";
		IInitializer init = new ParserFacade().parseInitializer(s);
		assertEquals(IInitializer.VOID_INITIALIZER, init.getNodeType0());
		assertPosition(init, 1, 4);
		
		assertVisitor(init, 1);
	}
	
	public void testExpression() {
		String s = " 1;";
		IExpressionInitializer init = (IExpressionInitializer) new ParserFacade().parseInitializer(s);
		assertEquals(IInitializer.EXPRESSION_INITIALIZER, init.getNodeType0());
		assertEquals("1", init.getExpression().toString());
		assertPosition(init, 1, 1);
		
		assertVisitor(init, 2);
	}
	
	public void testStructEmpty() {
		String s = " { };";
		IStructInitializer init = (IStructInitializer) new ParserFacade().parseInitializer(s);
		assertEquals(IInitializer.STRUCT_INITIALIZER, init.getNodeType0());
		assertPosition(init, 1, 3);
		
		assertEquals(0, init.getNames().length);
		assertEquals(0, init.getValues().length);
	}
	
	public void testStructInitializers() {
		String s = " { a = 2, b = 3 };";
		IStructInitializer init = (IStructInitializer) new ParserFacade().parseInitializer(s);
		assertEquals(IInitializer.STRUCT_INITIALIZER, init.getNodeType0());
		assertPosition(init, 1, s.length() - 2);
		
		ISimpleName[] names = init.getNames();
		assertEquals(2, names.length);
		
		assertNull(names[0]);
		assertNull(names[1]);
		
		IInitializer[] values = init.getValues();
		assertEquals(2, values.length);
		
		assertPosition(values[0], 3, 5);
		assertPosition(values[1], 10, 5);
		
		assertEquals(IInitializer.EXPRESSION_INITIALIZER, values[0].getNodeType0());
		assertEquals(IInitializer.EXPRESSION_INITIALIZER, values[1].getNodeType0());
	}
	
	public void testStructInitializers2() {
		String s = " { a : b = 2 };";
		IStructInitializer init = (IStructInitializer) new ParserFacade().parseInitializer(s);
		assertEquals(IInitializer.STRUCT_INITIALIZER, init.getNodeType0());
		assertPosition(init, 1, s.length() - 2);
		
		ISimpleName[] names = init.getNames();
		assertEquals(1, names.length);
		
		assertEquals("a", names[0].toString());
		assertPosition(names[0], 3, 1);
	}
	
	public void testStructInitializers3() {
		String s = " { 2 };";
		IStructInitializer init = (IStructInitializer) new ParserFacade().parseInitializer(s);
		assertEquals(IInitializer.STRUCT_INITIALIZER, init.getNodeType0());
		assertPosition(init, 1, s.length() - 2);
		
		ISimpleName[] names = init.getNames();
		assertEquals(1, names.length);
		assertNull(names[0]);
		
		IInitializer[] values = init.getValues();
		assertEquals(IInitializer.EXPRESSION_INITIALIZER, values[0].getNodeType0());
	}
	
	public void testArray() {
		String s = " [ ];";
		IArrayInitializer init = (IArrayInitializer) new ParserFacade().parseInitializer(s);
		assertEquals(IInitializer.ARRAY_INITIALIZER, init.getNodeType0());
		assertPosition(init, 1, s.length() - 2);
	}
	
	public void testArrayInitializers() {
		String s = " [ 1, 2, 3 ];";
		IArrayInitializer init = (IArrayInitializer) new ParserFacade().parseInitializer(s);
		assertEquals(IInitializer.ARRAY_INITIALIZER, init.getNodeType0());
		assertPosition(init, 1, s.length() - 2);
		
		List<ArrayInitializerFragment> fragments = init.fragments();
		assertEquals(3, fragments.size());
		
		assertPosition(fragments.get(0), 3, 1);
		
		assertNull(fragments.get(0).getExpression());
		assertNull(fragments.get(1).getExpression());
		assertNull(fragments.get(2).getExpression());
		
		assertPosition(fragments.get(0).getInitializer(), 3, 1);
		
		IExpressionInitializer expInit = (IExpressionInitializer) fragments.get(0).getInitializer();
		IExpression exp = expInit.getExpression();
		assertEquals("1", exp.toString());
	}
	
	public void testArrayInitializers2() {
		String s = " [ 2 : 1 ];";
		IArrayInitializer init = (IArrayInitializer) new ParserFacade().parseInitializer(s);
		assertEquals(IInitializer.ARRAY_INITIALIZER, init.getNodeType0());
		assertPosition(init, 1, s.length() - 2);
		
		List<ArrayInitializerFragment> fragments = init.fragments();
		assertEquals(1, fragments.size());
		
		assertPosition(fragments.get(0).getExpression(), 3, 1);
		
		assertPosition(fragments.get(0).getInitializer(), 7, 1);
		
		IExpressionInitializer expInit = (IExpressionInitializer) fragments.get(0).getInitializer();
		IExpression exp = expInit.getExpression();
		assertEquals("1", exp.toString());
	}
	
	public void testArrayInitializersNested() {
		String s = " [ [ 1 ] ];";
		IArrayInitializer init = (IArrayInitializer) new ParserFacade().parseInitializer(s);
		assertEquals(IInitializer.ARRAY_INITIALIZER, init.getNodeType0());
		assertPosition(init, 1, s.length() - 2);
		
		assertEquals(1, init.fragments().size());
	}

}
