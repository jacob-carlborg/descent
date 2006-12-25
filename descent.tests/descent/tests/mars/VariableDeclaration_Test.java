package descent.tests.mars;

import java.util.List;

import descent.core.dom.ASTNode;
import descent.core.dom.ArrayType;
import descent.core.dom.AssociativeArrayType;
import descent.core.dom.Comment;
import descent.core.dom.DelegateType;
import descent.core.dom.VariableDeclaration;
import descent.core.dom.VariableDeclarationFragment;

public class VariableDeclaration_Test extends Parser_Test {
	
	public void testOne() {
		String s = " int x;";
		
		VariableDeclaration var = (VariableDeclaration) getSingleDeclarationNoProblems(s);
		assertEquals(ASTNode.VARIABLE_DECLARATION, var.getNodeType());
		
		assertPosition(var, 1, s.length() - 1);
		assertEquals("int", var.getType().toString());
		assertPosition(var.getType(), 1, 3);
		assertEquals(1, var.fragments().size());
		
		VariableDeclarationFragment fragment = var.fragments().get(0);
		
		assertEquals("x", fragment.getName().getFullyQualifiedName());
		assertPosition(fragment.getName(), 5, 1);
		
		assertNull(fragment.getInitializer());
	}
	
	public void testTwo() {
		String s = " int x, y;";
		VariableDeclaration var = (VariableDeclaration) getSingleDeclarationNoProblems(s);
		
		assertPosition(var, 1, s.length() - 1);
		assertEquals("int", var.getType().toString());
		assertPosition(var.getType(), 1, 3);
		assertEquals(2, var.fragments().size());
		
		VariableDeclarationFragment fragment;
		
		fragment = var.fragments().get(0);
		assertEquals("x", fragment.getName().getFullyQualifiedName());
		assertPosition(fragment.getName(), 5, 1);
		assertNull(fragment.getInitializer());
		
		fragment = var.fragments().get(1);
		assertEquals("y", fragment.getName().getFullyQualifiedName());
		assertPosition(fragment.getName(), 8, 1);
		assertNull(fragment.getInitializer());
	}
	
	public void testComments() {
		String s = " /** hola */ int x;";
		VariableDeclaration var = (VariableDeclaration) getSingleDeclarationNoProblems(s);
		assertPosition(var, 13, 6);
		
		List<Comment> comments = var.getComments();
		assertEquals(1, comments.size());
		assertEquals("/** hola */", comments.get(0).getComment());
	}
	
	public void testCStyle() {
		String s = " int x[3];";
		VariableDeclaration var = (VariableDeclaration) getSingleDeclarationNoProblems(s);
		assertEquals(ASTNode.VARIABLE_DECLARATION, var.getNodeType());
		assertEquals("x", var.fragments().get(0).getName().getFullyQualifiedName());
		assertPosition(var.fragments().get(0).getName(), 5, 1);
		assertPosition(var, 1, 9);
	}
	
	public void testCStyle2() {
		String s = " int x[3][5];";
		VariableDeclaration var = (VariableDeclaration) getSingleDeclarationNoProblems(s);
		assertEquals(ASTNode.VARIABLE_DECLARATION, var.getNodeType());
		assertEquals("x", var.fragments().get(0).getName().getFullyQualifiedName());
		assertPosition(var.fragments().get(0).getName(), 5, 1);
		assertPosition(var, 1, 12);
	}
	
	public void testCStyle3() {
		String s = " int (*x[5])[3];";
		VariableDeclaration var = (VariableDeclaration) getSingleDeclarationNoProblems(s);
		assertEquals(ASTNode.VARIABLE_DECLARATION, var.getNodeType());
		assertEquals("x", var.fragments().get(0).getName().getFullyQualifiedName());
		assertPosition(var.fragments().get(0).getName(), 7, 1);
		assertPosition(var, 1, 15);
	}
	
	public void testCStyle5() {
		String s = " int (*x)(char);";
		VariableDeclaration var = (VariableDeclaration) getSingleDeclarationNoProblems(s);
		assertEquals(ASTNode.VARIABLE_DECLARATION, var.getNodeType());
		assertEquals("x", var.fragments().get(0).getName().getFullyQualifiedName());
		assertPosition(var.fragments().get(0).getName(), 7, 1);
		assertEquals(ASTNode.DELEGATE_TYPE, var.getType().getNodeType());
		assertPosition(var, 1, 15);
		
		DelegateType del = (DelegateType) var.getType();
		assertEquals("char", del.arguments().get(0).getType().toString());
		assertEquals("int", del.getReturnType().toString());
	}
	
	public void testCStyle6() {
		String s = " int (*[] x)(char);";
		VariableDeclaration var = (VariableDeclaration) getSingleDeclarationNoProblems(s);
		assertEquals(ASTNode.VARIABLE_DECLARATION, var.getNodeType());
		assertEquals("x", var.fragments().get(0).getName().getFullyQualifiedName());
		assertPosition(var.fragments().get(0).getName(), 10, 1);
		assertEquals(ASTNode.DYNAMIC_ARRAY_TYPE, var.getType().getNodeType());
		assertPosition(var, 1, 18);
		
		ArrayType array = (ArrayType) var.getType();
		assertEquals(ASTNode.DELEGATE_TYPE, array.getComponentType().getNodeType());
		DelegateType del = (DelegateType) array.getComponentType();
		assertEquals("char", del.arguments().get(0).getType().toString());
		assertEquals("int", del.getReturnType().toString());
	}
	
	public void testAuto() {
		String s = " auto x = 1;";
		VariableDeclaration var = (VariableDeclaration) getSingleDeclarationNoProblems(s);
		assertEquals(ASTNode.VARIABLE_DECLARATION, var.getNodeType());
		assertNull(var.getType());
		// TODO assertTrue((var.getModifier() & IModifier.AUTO) != 0);
	}
	
	public void testStatic() {
		String s = " static x = 1;";
		VariableDeclaration var = (VariableDeclaration) getSingleDeclarationNoProblems(s);
		assertEquals(ASTNode.VARIABLE_DECLARATION, var.getNodeType());
		assertNull(var.getType());
		// TODO assertTrue((var.getModifier() & IModifier.STATIC) != 0);
	}
	
	public void testExtern() {
		String s = " extern x = 1;";
		VariableDeclaration var = (VariableDeclaration) getSingleDeclarationNoProblems(s);
		assertEquals(ASTNode.VARIABLE_DECLARATION, var.getNodeType());
		assertNull(var.getType());
		// TODO assertTrue((var.getModifier() & IModifier.EXTERN) != 0);
	}
	
	/* TODO see if this is tested in Modifier_Test
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
			VariableDeclaration var = (VariableDeclaration) getSingleDeclarationNoProblems(s);
			assertEquals(ASTNode.VARIABLE_DECLARATION, var.getNodeType0());
			assertNull(var.getType());
			// TODO assertTrue((var.getModifier() & IModifier.STATIC) != 0);
			// TODO assertTrue((var.getModifier() & ((Integer) modifier[1])) != 0);
		}
	}
	*/
	
	/* TODO see if this is tested in Modifier_Test
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
			VariableDeclaration var = (VariableDeclaration) getSingleDeclarationNoProblems(s);
			assertEquals(ASTNode.VARIABLE_DECLARATION, var.getNodeType0());
			assertNull(var.getType());
			// TODO assertTrue((var.getModifier() & ((Integer) modifier[1])) != 0);
		}
	}
	*/
	
	public void testAssociativeArray() {
		String s = " char x[int] = 1;";
		VariableDeclaration var = (VariableDeclaration) getSingleDeclarationNoProblems(s);
		assertEquals(ASTNode.VARIABLE_DECLARATION, var.getNodeType());
		
		AssociativeArrayType type = (AssociativeArrayType) var.getType();
		assertEquals(ArrayType.ASSOCIATIVE_ARRAY_TYPE, type.getNodeType());
		assertEquals("char", type.getComponentType().toString());
		assertEquals("int", type.getKeyType().toString());
	}
	
	/* TODO Solve UTF SVN problems
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
	*/
	
}
