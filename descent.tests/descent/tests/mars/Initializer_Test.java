package descent.tests.mars;

import java.util.List;

import descent.core.dom.ASTNode;
import descent.core.dom.ArrayInitializer;
import descent.core.dom.ArrayInitializerFragment;
import descent.core.dom.Expression;
import descent.core.dom.ExpressionInitializer;
import descent.core.dom.Initializer;
import descent.core.dom.NumberLiteral;
import descent.core.dom.StructInitializer;

public class Initializer_Test extends Parser_Test {
	
	public void testVoid() {
		String s = " void;";
		Initializer init = parseInitializer(s);
		assertEquals(ASTNode.VOID_INITIALIZER, init.getNodeType());
		assertPosition(init, 1, 4);
	}
	
	public void testExpression() {
		String s = " 1;";
		ExpressionInitializer init = (ExpressionInitializer) parseInitializer(s);
		assertEquals(ASTNode.EXPRESSION_INITIALIZER, init.getNodeType());
		assertEquals("1", ((NumberLiteral) init.getExpression()).getToken());
		assertPosition(init, 1, 1);
	}
	
	public void testStructEmpty() {
		String s = " { };";
		StructInitializer init = (StructInitializer) parseInitializer(s);
		assertEquals(ASTNode.STRUCT_INITIALIZER, init.getNodeType());
		assertPosition(init, 1, 3);
		
		assertEquals(0, init.fragments().size());
	}
	
	public void testStructInitializers() {
		String s = " { a = 2, b = 3 };";
		StructInitializer init = (StructInitializer) parseInitializer(s);
		assertEquals(ASTNode.STRUCT_INITIALIZER, init.getNodeType());
		assertPosition(init, 1, s.length() - 2);
		
		assertEquals(2, init.fragments().size());
		
		assertNull(init.fragments().get(0).getName());
		assertNull(init.fragments().get(1).getName());
		
		assertPosition(init.fragments().get(0).getInitializer(), 3, 5);
		assertPosition(init.fragments().get(1).getInitializer(), 10, 5);
		
		assertEquals(ASTNode.EXPRESSION_INITIALIZER, init.fragments().get(0).getInitializer().getNodeType());
		assertEquals(ASTNode.EXPRESSION_INITIALIZER, init.fragments().get(0).getInitializer().getNodeType());
	}
	
	public void testStructInitializers2() {
		String s = " { a : b = 2 };";
		StructInitializer init = (StructInitializer) parseInitializer(s);
		assertEquals(ASTNode.STRUCT_INITIALIZER, init.getNodeType());
		assertPosition(init, 1, s.length() - 2);
		
		assertEquals(1, init.fragments().size());
		
		assertEquals("a", init.fragments().get(0).getName().getIdentifier());
		assertPosition(init.fragments().get(0).getName(), 3, 1);
	}
	
	public void testStructInitializers3() {
		String s = " { 2 };";
		StructInitializer init = (StructInitializer) parseInitializer(s);
		assertEquals(ASTNode.STRUCT_INITIALIZER, init.getNodeType());
		assertPosition(init, 1, s.length() - 2);
		
		assertEquals(1, init.fragments().size());
		assertNull(init.fragments().get(0).getName());
		
		assertEquals(ASTNode.EXPRESSION_INITIALIZER, init.fragments().get(0).getInitializer().getNodeType());
	}
	
	public void testArray() {
		String s = " [ ];";
		ArrayInitializer init = (ArrayInitializer) parseInitializer(s);
		assertEquals(ASTNode.ARRAY_INITIALIZER, init.getNodeType());
		assertPosition(init, 1, s.length() - 2);
	}
	
	public void testArrayInitializers() {
		String s = " [ 1, 2, 3 ];";
		ArrayInitializer init = (ArrayInitializer) parseInitializer(s);
		assertEquals(ASTNode.ARRAY_INITIALIZER, init.getNodeType());
		assertPosition(init, 1, s.length() - 2);
		
		List<ArrayInitializerFragment> fragments = init.fragments();
		assertEquals(3, fragments.size());
		
		assertPosition(fragments.get(0), 3, 1);
		
		assertNull(fragments.get(0).getExpression());
		assertNull(fragments.get(1).getExpression());
		assertNull(fragments.get(2).getExpression());
		
		assertPosition(fragments.get(0).getInitializer(), 3, 1);
		
		ExpressionInitializer expInit = (ExpressionInitializer) fragments.get(0).getInitializer();
		Expression exp = expInit.getExpression();
		assertEquals("1", ((NumberLiteral) exp).getToken());
	}
	
	public void testArrayInitializers2() {
		String s = " [ 2 : 1 ];";
		ArrayInitializer init = (ArrayInitializer) parseInitializer(s);
		assertEquals(Initializer.ARRAY_INITIALIZER, init.getNodeType());
		assertPosition(init, 1, s.length() - 2);
		
		List<ArrayInitializerFragment> fragments = init.fragments();
		assertEquals(1, fragments.size());
		
		assertPosition(fragments.get(0).getExpression(), 3, 1);
		
		assertPosition(fragments.get(0).getInitializer(), 7, 1);
		
		ExpressionInitializer expInit = (ExpressionInitializer) fragments.get(0).getInitializer();
		Expression exp = expInit.getExpression();
		assertEquals("1", ((NumberLiteral) exp).getToken());
	}
	
	public void testArrayInitializersNested() {
		String s = " [ [ 1 ] ];";
		ArrayInitializer init = (ArrayInitializer) parseInitializer(s);
		assertEquals(Initializer.ARRAY_INITIALIZER, init.getNodeType());
		assertPosition(init, 1, s.length() - 2);
		
		assertEquals(1, init.fragments().size());
	}

}
