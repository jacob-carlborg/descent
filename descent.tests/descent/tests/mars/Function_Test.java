package descent.tests.mars;

import java.util.List;

import descent.core.dom.ASTNode;
import descent.core.dom.Argument;
import descent.core.dom.FunctionDeclaration;
import descent.core.dom.NumberLiteral;
import descent.core.dom.PrimitiveType;

public class Function_Test extends Parser_Test {
	
	public void testConstructor() {
		String s = " this() { }";
		FunctionDeclaration f = (FunctionDeclaration) getSingleDeclarationNoProblems(s);
		
		assertEquals(ASTNode.FUNCTION_DECLARATION, f.getNodeType());
		assertEquals(FunctionDeclaration.Kind.CONSTRUCTOR, f.getKind());
		assertEquals(PrimitiveType.Code.VOID, ((PrimitiveType) f.getReturnType()).getPrimitiveTypeCode());
		assertEquals(0, f.arguments().size());
		
		assertEquals("this", f.getName().getIdentifier());
		assertPosition(f.getName(), 1, 4);
		
		assertPosition(f, 1, 10);
	}
	
	public void testEmptyConstructor() {
		String s = " this();";
		FunctionDeclaration f = (FunctionDeclaration) getSingleDeclarationNoProblems(s);
		assertEquals(ASTNode.FUNCTION_DECLARATION, f.getNodeType());
		assertEquals(FunctionDeclaration.Kind.CONSTRUCTOR, f.getKind());
	}
	
	public void testDestructor() {
		String s = " ~this() { }";
		FunctionDeclaration f = (FunctionDeclaration) getSingleDeclarationNoProblems(s);
		assertEquals(ASTNode.FUNCTION_DECLARATION, f.getNodeType());
		assertEquals(FunctionDeclaration.Kind.DESTRUCTOR, f.getKind());
		assertEquals(PrimitiveType.Code.VOID, ((PrimitiveType) f.getReturnType()).getPrimitiveTypeCode());
		assertEquals(0, f.arguments().size());
		
		assertEquals("~this", f.getName().getIdentifier());
		assertPosition(f.getName(), 1, 5);
		
		assertPosition(f, 1, 11);
	}
	
	public void testEmptyDestructor() {
		String s = " ~this();";
		FunctionDeclaration f = (FunctionDeclaration) getSingleDeclarationNoProblems(s);
		assertEquals(ASTNode.FUNCTION_DECLARATION, f.getNodeType());
		assertEquals(FunctionDeclaration.Kind.DESTRUCTOR, f.getKind());
		assertEquals(PrimitiveType.Code.VOID, ((PrimitiveType) f.getReturnType()).getPrimitiveTypeCode());
	}
	
	public void testStaticConstructor() {
		String s = " static this() { }";
		FunctionDeclaration f = (FunctionDeclaration) getSingleDeclarationNoProblems(s);
		assertEquals(ASTNode.FUNCTION_DECLARATION, f.getNodeType());
		assertEquals(FunctionDeclaration.Kind.STATIC_CONSTRUCTOR, f.getKind());
		assertEquals(PrimitiveType.Code.VOID, ((PrimitiveType) f.getReturnType()).getPrimitiveTypeCode());
		assertEquals(0, f.arguments().size());
		
		assertEquals("this", f.getName().getIdentifier());
		assertPosition(f.getName(), 8, 4);
		
		assertPosition(f, 1, 17);
	}
	
	public void testStaticDestructor() {
		String s = " static ~this() { }";
		FunctionDeclaration f = (FunctionDeclaration) getSingleDeclarationNoProblems(s);
		assertEquals(ASTNode.FUNCTION_DECLARATION, f.getNodeType());
		assertEquals(FunctionDeclaration.Kind.STATIC_DESTRUCTOR, f.getKind());
		assertEquals(PrimitiveType.Code.VOID, ((PrimitiveType) f.getReturnType()).getPrimitiveTypeCode());
		assertEquals(0, f.arguments().size());
		
		assertEquals("~this", f.getName().getIdentifier());
		assertPosition(f.getName(), 8, 5);
		
		assertPosition(f, 1, 18);
	}
	
	public void testNew() {
		String s = " new() { }";
		FunctionDeclaration f = (FunctionDeclaration) getSingleDeclarationNoProblems(s);
		assertEquals(ASTNode.FUNCTION_DECLARATION, f.getNodeType());
		assertEquals(FunctionDeclaration.Kind.NEW, f.getKind());
		assertEquals(PrimitiveType.Code.VOID, ((PrimitiveType) f.getReturnType()).getPrimitiveTypeCode());
		assertEquals(0, f.arguments().size());
		
		assertEquals("new", f.getName().getIdentifier());
		assertPosition(f.getName(), 1, 3);
		
		assertPosition(f, 1, s.length() - 1);
	}
	
	public void testDelete() {
		String s = " delete() { }";
		FunctionDeclaration f = (FunctionDeclaration) getSingleDeclarationNoProblems(s);
		assertEquals(ASTNode.FUNCTION_DECLARATION, f.getNodeType());
		assertEquals(FunctionDeclaration.Kind.DELETE, f.getKind());
		assertEquals(PrimitiveType.Code.VOID, ((PrimitiveType) f.getReturnType()).getPrimitiveTypeCode());
		assertEquals(0, f.arguments().size());
		
		assertEquals("delete", f.getName().getIdentifier());
		assertPosition(f.getName(), 1, 6);
		
		assertPosition(f, 1, s.length() - 1);
	}
	
	public void testFunctionWithoutArguments() {
		String s = " void func() { }";
		FunctionDeclaration f = (FunctionDeclaration) getSingleDeclarationNoProblems(s);
		assertEquals(ASTNode.FUNCTION_DECLARATION, f.getNodeType());
		assertEquals(FunctionDeclaration.Kind.FUNCTION, f.getKind());

		assertEquals("void", f.getReturnType().toString());
		assertEquals("func", f.getName().getIdentifier());
		assertPosition(f.getName(), 6, 4);
		assertEquals(0, f.arguments().size());
		assertPosition(f, 1, 15);
	}
	
	public void testFunctionSemicolon() {
		String s = " void func();";
		FunctionDeclaration f = (FunctionDeclaration) getSingleDeclarationNoProblems(s);
		assertPosition(f, 1, 12);
	}
	
	public void testFunctionWithArguments() {
		String s = " void func(int a, in char b, out bool c, inout float d, lazy double e) { }";
		FunctionDeclaration f = (FunctionDeclaration) getSingleDeclarationNoProblems(s);
		assertPosition(f, 1, 73);
		
		assertFalse(f.isVariadic());
		
		List<Argument> args = f.arguments();
		assertEquals(5, args.size());
		
		Argument arg = args.get(0);
		assertPosition(arg, 11, 5);
		assertEquals(ASTNode.ARGUMENT, args.get(0).getNodeType());
		assertEquals("a", arg.getName().getIdentifier());
		assertEquals("int", arg.getType().toString());
		assertEquals(Argument.PassageMode.DEFAULT, arg.getPassageMode());
		
		arg = args.get(1);
		assertPosition(arg, 18, 9);
		assertEquals("b", arg.getName().getIdentifier());
		assertEquals("char", arg.getType().toString());
		assertEquals(Argument.PassageMode.IN, arg.getPassageMode());
		
		arg = args.get(2);
		assertPosition(arg, 29, 10);
		assertEquals("c", arg.getName().getIdentifier());
		assertEquals("bool", arg.getType().toString());
		assertEquals(Argument.PassageMode.OUT, arg.getPassageMode());
		
		arg = args.get(3);
		assertPosition(arg, 41, 13);
		assertEquals("d", arg.getName().getIdentifier());
		assertEquals("float", arg.getType().toString());
		assertEquals(Argument.PassageMode.INOUT, arg.getPassageMode());
		
		arg = args.get(4);
		assertPosition(arg, 56, 13);
		assertEquals("e", arg.getName().getIdentifier());
		assertEquals("double", arg.getType().toString());
		assertEquals(Argument.PassageMode.LAZY, arg.getPassageMode());
	}
	
	public void testFunctionWithOneArgument() {
		String s = " void func(int a) { }";
		FunctionDeclaration f = (FunctionDeclaration) getSingleDeclarationNoProblems(s);
		assertEquals(1, f.arguments().size());
	}
	
	public void testConstructorWithArguments() {
		String s = "      this(int a, in char b, out bool c, inout float d, lazy double e = 2) { }";
		FunctionDeclaration f = (FunctionDeclaration) getSingleDeclarationNoProblems(s);
		assertPosition(f, 6, 72);
		
		assertFalse(f.isVariadic());
		
		List<Argument> args = f.arguments();
		assertEquals(5, args.size());
		
		Argument arg = args.get(0);
		assertPosition(arg, 11, 5);
		assertEquals("a", arg.getName().getIdentifier());
		assertEquals("int", arg.getType().toString());
		assertEquals(Argument.PassageMode.DEFAULT, arg.getPassageMode());
		
		arg = args.get(1);
		assertPosition(arg, 18, 9);
		assertEquals("b", arg.getName().getIdentifier());
		assertEquals("char", arg.getType().toString());
		assertEquals(Argument.PassageMode.IN, arg.getPassageMode());
		
		arg = args.get(2);
		assertPosition(arg, 29, 10);
		assertEquals("c", arg.getName().getIdentifier());
		assertEquals("bool", arg.getType().toString());
		assertEquals(Argument.PassageMode.OUT, arg.getPassageMode());
		
		arg = args.get(3);
		assertPosition(arg, 41, 13);
		assertEquals("d", arg.getName().getIdentifier());
		assertEquals("float", arg.getType().toString());
		assertEquals(Argument.PassageMode.INOUT, arg.getPassageMode());
		
		arg = args.get(4);
		assertPosition(arg, 56, 17);
		assertEquals("e", arg.getName().getIdentifier());
		assertEquals("double", arg.getType().toString());
		assertEquals(Argument.PassageMode.LAZY, arg.getPassageMode());
		assertEquals("2", ((NumberLiteral) arg.getDefaultValue()).getToken());
	}
	
	public void testFunctionVariadic() {
		String s = " void func(...);";
		FunctionDeclaration f = (FunctionDeclaration) getSingleDeclarationNoProblems(s);
		assertTrue(f.isVariadic());
	}
	
	public void testFunctionVariadic2() {
		String s = " void func(int[] x ...);";
		FunctionDeclaration f = (FunctionDeclaration) getSingleDeclarationNoProblems(s);
		assertEquals(1, f.arguments().size());
		assertTrue(f.isVariadic());
	}
	
	public void testFunctionBody() {
		String s = " void func() body { }";
		FunctionDeclaration f = (FunctionDeclaration) getSingleDeclarationNoProblems(s);
		assertNotNull(f.getBody());
		assertEquals(0, f.arguments().size());
		assertPosition(f, 1, s.length() - 1);
	}
	
	public void testFunctionIn() {
		String s = " void func() in { } body { }";
		FunctionDeclaration f = (FunctionDeclaration) getSingleDeclarationNoProblems(s);
		assertNotNull(f.getPrecondition());
		assertPosition(f, 1, s.length() - 1);
	}
	
	public void testFunctionOut() {
		String s = " void func() out { } body { }";
		FunctionDeclaration f = (FunctionDeclaration) getSingleDeclarationNoProblems(s);
		assertNotNull(f.getPostcondition());
		assertNull(f.getPostconditionVariableName());
		assertPosition(f, 1, s.length() - 1);
	}
	
	public void testFunctionOutName() {
		String s = " void func() out(bla) { } body { }";
		FunctionDeclaration f = (FunctionDeclaration) getSingleDeclarationNoProblems(s);
		assertNotNull(f.getPostcondition());
		assertEquals("bla", f.getPostconditionVariableName().getIdentifier());
		assertPosition(f.getPostcondition(), 22, 3);
		assertPosition(f.getPostconditionVariableName(), 17, 3);
		assertPosition(f, 1, s.length() - 1);
	}

}
