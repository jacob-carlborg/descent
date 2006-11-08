package descent.tests.mars;

import descent.core.dom.IArgument;
import descent.core.dom.ICompilationUnit;
import descent.core.dom.IDElement;
import descent.core.dom.IFunctionDeclaration;
import descent.internal.core.dom.ParserFacade;

public class Function_Test extends Parser_Test {
	
	public void testConstructor() {
		String s = " this() { }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IDElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IFunctionDeclaration f = (IFunctionDeclaration) declDefs[0];
		assertEquals(IDElement.FUNCTION_DECLARATION, f.getElementType());
		assertEquals(IFunctionDeclaration.CONSTRUCTOR, f.getFunctionDeclarationType());
		assertNull(f.getReturnType());
		assertEquals(0, f.getArguments().length);
		
		assertEquals("this", f.getName().toString());
		assertPosition(f.getName(), 1, 4);
		
		assertPosition(f, 1, 10);
		
		assertVisitor(f, 3);
	}
	
	public void testEmptyConstructor() {
		String s = " this();";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IDElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IFunctionDeclaration f = (IFunctionDeclaration) declDefs[0];
		assertEquals(IDElement.FUNCTION_DECLARATION, f.getElementType());
		assertEquals(IFunctionDeclaration.CONSTRUCTOR, f.getFunctionDeclarationType());
		
		assertPosition(f, 1, 7);
	}
	
	public void testDestructor() {
		String s = " ~this() { }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IDElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IFunctionDeclaration f = (IFunctionDeclaration) declDefs[0];
		assertEquals(IDElement.FUNCTION_DECLARATION, f.getElementType());
		assertEquals(IFunctionDeclaration.DESTRUCTOR, f.getFunctionDeclarationType());
		assertNull(f.getReturnType());
		assertEquals(0, f.getArguments().length);
		
		assertEquals("~this", f.getName().toString());
		assertPosition(f.getName(), 1, 5);
		
		assertPosition(f, 1, 11);
		
		assertVisitor(f, 3);
	}
	
	public void testEmptyDestructor() {
		String s = " ~this();";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IDElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IFunctionDeclaration f = (IFunctionDeclaration) declDefs[0];
		assertEquals(IDElement.FUNCTION_DECLARATION, f.getElementType());
		assertEquals(IFunctionDeclaration.DESTRUCTOR, f.getFunctionDeclarationType());
		assertNull(f.getReturnType());
		
		assertPosition(f, 1, 8);
	}
	
	public void testStaticConstructor() {
		String s = " static this() { }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IDElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IFunctionDeclaration f = (IFunctionDeclaration) declDefs[0];
		assertEquals(IDElement.FUNCTION_DECLARATION, f.getElementType());
		assertEquals(IFunctionDeclaration.STATIC_CONSTRUCTOR, f.getFunctionDeclarationType());
		assertNull(f.getReturnType());
		assertEquals(0, f.getArguments().length);
		
		assertEquals("this", f.getName().toString());
		assertPosition(f.getName(), 8, 4);
		
		assertPosition(f, 1, 17);
		
		assertVisitor(f, 3);
	}
	
	public void testStaticDestructor() {
		String s = " static ~this() { }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IDElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IFunctionDeclaration f = (IFunctionDeclaration) declDefs[0];
		assertEquals(IDElement.FUNCTION_DECLARATION, f.getElementType());
		assertEquals(IFunctionDeclaration.STATIC_DESTRUCTOR, f.getFunctionDeclarationType());
		assertNull(f.getReturnType());
		assertEquals(0, f.getArguments().length);
		
		assertEquals("~this", f.getName().toString());
		assertPosition(f.getName(), 8, 5);
		
		assertPosition(f, 1, 18);
		
		assertVisitor(f, 3);
	}
	
	public void testNew() {
		String s = " new() { }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IDElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IFunctionDeclaration f = (IFunctionDeclaration) declDefs[0];
		assertEquals(IDElement.FUNCTION_DECLARATION, f.getElementType());
		assertEquals(IFunctionDeclaration.NEW, f.getFunctionDeclarationType());
		assertNull(f.getReturnType());
		assertEquals(0, f.getArguments().length);
		
		assertEquals("new", f.getName().toString());
		assertPosition(f.getName(), 1, 3);
		
		assertPosition(f, 1, s.length() - 1);
		
		assertVisitor(f, 3);
	}
	
	public void testDelete() {
		String s = " delete() { }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IDElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IFunctionDeclaration f = (IFunctionDeclaration) declDefs[0];
		assertEquals(IDElement.FUNCTION_DECLARATION, f.getElementType());
		assertEquals(IFunctionDeclaration.DELETE, f.getFunctionDeclarationType());
		assertNull(f.getReturnType());
		assertEquals(0, f.getArguments().length);
		
		assertEquals("delete", f.getName().toString());
		assertPosition(f.getName(), 1, 6);
		
		assertPosition(f, 1, s.length() - 1);
		
		assertVisitor(f, 3);
	}
	
	public void testFunctionWithoutArguments() {
		String s = " void func() { }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IDElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IFunctionDeclaration func = (IFunctionDeclaration) declDefs[0];
		assertEquals(IDElement.FUNCTION_DECLARATION, func.getElementType());
		assertEquals(IFunctionDeclaration.FUNCTION, func.getFunctionDeclarationType());

		assertEquals("void", func.getReturnType().toString());
		assertEquals("func", func.getName().toString());
		assertPosition(func.getName(), 6, 4);
		assertEquals(0, func.getArguments().length);
		assertPosition(func, 1, 15);
	}
	
	public void testFunctionSemicolon() {
		String s = " void func();";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IDElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IFunctionDeclaration func = (IFunctionDeclaration) declDefs[0];
		assertPosition(func, 1, 12);
	}
	
	public void testFunctionWithArguments() {
		String s = " void func(int a, in char b, out bool c, inout float d, lazy double e) { }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IDElement[] declDefs = unit.getDeclarationDefinitions();
		
		IFunctionDeclaration func = (IFunctionDeclaration) declDefs[0];
		assertPosition(func, 1, 73);
		
		assertFalse(func.isVariadic());
		
		IArgument[] args = func.getArguments();
		assertEquals(5, args.length);
		
		assertPosition(args[0], 11, 5);
		assertEquals(IDElement.ARGUMENT, args[0].getElementType());
		assertEquals("a", args[0].getName().toString());
		assertEquals("int", args[0].getType().toString());
		assertEquals(IArgument.IN, args[0].getKind());
		
		assertPosition(args[1], 18, 9);
		assertEquals("b", args[1].getName().toString());
		assertEquals("char", args[1].getType().toString());
		assertEquals(IArgument.IN, args[1].getKind());
		
		assertPosition(args[2], 29, 10);
		assertEquals("c", args[2].getName().toString());
		assertEquals("bool", args[2].getType().toString());
		assertEquals(IArgument.OUT, args[2].getKind());
		
		assertPosition(args[3], 41, 13);
		assertEquals("d", args[3].getName().toString());
		assertEquals("float", args[3].getType().toString());
		assertEquals(IArgument.INOUT, args[3].getKind());
		
		assertPosition(args[4], 56, 13);
		assertEquals("e", args[4].getName().toString());
		assertEquals("double", args[4].getType().toString());
		assertEquals(IArgument.LAZY, args[4].getKind());
		
		assertVisitor(func, 19);
	}
	
	public void testFunctionWithOneArgument() {
		String s = " void func(int a) { }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IDElement[] declDefs = unit.getDeclarationDefinitions();
		
		IFunctionDeclaration func = (IFunctionDeclaration) declDefs[0];
		
		IArgument[] args = func.getArguments();
		assertEquals(1, args.length);
	}
	
	public void testConstructorWithArguments() {
		String s = "      this(int a = 2, in char b, out bool c, inout float d, lazy double e) { }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IDElement[] declDefs = unit.getDeclarationDefinitions();
		
		IFunctionDeclaration func = (IFunctionDeclaration) declDefs[0];
		assertPosition(func, 6, 72);
		
		assertFalse(func.isVariadic());
		
		IArgument[] args = func.getArguments();
		assertEquals(5, args.length);
		
		assertPosition(args[0], 11, 9);
		assertEquals("a", args[0].getName().toString());
		assertEquals("int", args[0].getType().toString());
		assertEquals(IArgument.IN, args[0].getKind());
		assertEquals("2", args[0].getDefaultValue().toString());
		
		assertPosition(args[1], 22, 9);
		assertEquals("b", args[1].getName().toString());
		assertEquals("char", args[1].getType().toString());
		assertEquals(IArgument.IN, args[1].getKind());
		
		assertPosition(args[2], 33, 10);
		assertEquals("c", args[2].getName().toString());
		assertEquals("bool", args[2].getType().toString());
		assertEquals(IArgument.OUT, args[2].getKind());
		
		assertPosition(args[3], 45, 13);
		assertEquals("d", args[3].getName().toString());
		assertEquals("float", args[3].getType().toString());
		assertEquals(IArgument.INOUT, args[3].getKind());
		
		assertPosition(args[4], 60, 13);
		assertEquals("e", args[4].getName().toString());
		assertEquals("double", args[4].getType().toString());
		assertEquals(IArgument.LAZY, args[4].getKind());
	}
	
	public void testFunctionVariadic() {
		String s = " void func(...);";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IDElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IFunctionDeclaration func = (IFunctionDeclaration) declDefs[0];
		assertTrue(func.isVariadic());
	}
	
	public void testFunctionVariadic2() {
		String s = " void func(int[] x ...);";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IDElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IFunctionDeclaration func = (IFunctionDeclaration) declDefs[0];
		assertEquals(1, func.getArguments().length);
		assertTrue(func.isVariadic());
	}

}
