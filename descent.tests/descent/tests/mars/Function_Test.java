package descent.tests.mars;

import descent.core.dom.IArgument;
import descent.core.dom.ICompilationUnit;
import descent.core.dom.IElement;
import descent.core.dom.IFunctionDeclaration;
import descent.internal.core.dom.Argument;
import descent.internal.core.dom.ParserFacade;

public class Function_Test extends Parser_Test {
	
	public void testConstructor() {
		String s = " this() { }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IFunctionDeclaration f = (IFunctionDeclaration) declDefs[0];
		assertEquals(IElement.FUNCTION_DECLARATION, f.getNodeType0());
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
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IFunctionDeclaration f = (IFunctionDeclaration) declDefs[0];
		assertEquals(IElement.FUNCTION_DECLARATION, f.getNodeType0());
		assertEquals(IFunctionDeclaration.CONSTRUCTOR, f.getFunctionDeclarationType());
		
		assertPosition(f, 1, 7);
	}
	
	public void testDestructor() {
		String s = " ~this() { }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IFunctionDeclaration f = (IFunctionDeclaration) declDefs[0];
		assertEquals(IElement.FUNCTION_DECLARATION, f.getNodeType0());
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
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IFunctionDeclaration f = (IFunctionDeclaration) declDefs[0];
		assertEquals(IElement.FUNCTION_DECLARATION, f.getNodeType0());
		assertEquals(IFunctionDeclaration.DESTRUCTOR, f.getFunctionDeclarationType());
		assertNull(f.getReturnType());
		
		assertPosition(f, 1, 8);
	}
	
	public void testStaticConstructor() {
		String s = " static this() { }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IFunctionDeclaration f = (IFunctionDeclaration) declDefs[0];
		assertEquals(IElement.FUNCTION_DECLARATION, f.getNodeType0());
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
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IFunctionDeclaration f = (IFunctionDeclaration) declDefs[0];
		assertEquals(IElement.FUNCTION_DECLARATION, f.getNodeType0());
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
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IFunctionDeclaration f = (IFunctionDeclaration) declDefs[0];
		assertEquals(IElement.FUNCTION_DECLARATION, f.getNodeType0());
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
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IFunctionDeclaration f = (IFunctionDeclaration) declDefs[0];
		assertEquals(IElement.FUNCTION_DECLARATION, f.getNodeType0());
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
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IFunctionDeclaration func = (IFunctionDeclaration) declDefs[0];
		assertEquals(IElement.FUNCTION_DECLARATION, func.getNodeType0());
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
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IFunctionDeclaration func = (IFunctionDeclaration) declDefs[0];
		assertPosition(func, 1, 12);
	}
	
	public void testFunctionWithArguments() {
		String s = " void func(int a, in char b, out bool c, inout float d, lazy double e) { }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		
		IFunctionDeclaration func = (IFunctionDeclaration) declDefs[0];
		assertPosition(func, 1, 73);
		
		assertFalse(func.isVariadic());
		
		IArgument[] args = func.getArguments();
		assertEquals(5, args.length);
		
		assertPosition(args[0], 11, 5);
		assertEquals(IElement.ARGUMENT, args[0].getNodeType0());
		assertEquals("a", args[0].getName().toString());
		assertEquals("int", args[0].getType().toString());
		assertEquals(Argument.PassageMode.IN, args[0].getPassageMode());
		
		assertPosition(args[1], 18, 9);
		assertEquals("b", args[1].getName().toString());
		assertEquals("char", args[1].getType().toString());
		assertEquals(Argument.PassageMode.IN, args[1].getPassageMode());
		
		assertPosition(args[2], 29, 10);
		assertEquals("c", args[2].getName().toString());
		assertEquals("bool", args[2].getType().toString());
		assertEquals(Argument.PassageMode.OUT, args[2].getPassageMode());
		
		assertPosition(args[3], 41, 13);
		assertEquals("d", args[3].getName().toString());
		assertEquals("float", args[3].getType().toString());
		assertEquals(Argument.PassageMode.INOUT, args[3].getPassageMode());
		
		assertPosition(args[4], 56, 13);
		assertEquals("e", args[4].getName().toString());
		assertEquals("double", args[4].getType().toString());
		assertEquals(Argument.PassageMode.LAZY, args[4].getPassageMode());
	}
	
	public void testFunctionWithOneArgument() {
		String s = " void func(int a) { }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		
		IFunctionDeclaration func = (IFunctionDeclaration) declDefs[0];
		
		IArgument[] args = func.getArguments();
		assertEquals(1, args.length);
	}
	
	public void testConstructorWithArguments() {
		String s = "      this(int a = 2, in char b, out bool c, inout float d, lazy double e) { }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		
		IFunctionDeclaration func = (IFunctionDeclaration) declDefs[0];
		assertPosition(func, 6, 72);
		
		assertFalse(func.isVariadic());
		
		IArgument[] args = func.getArguments();
		assertEquals(5, args.length);
		
		assertPosition(args[0], 11, 9);
		assertEquals("a", args[0].getName().toString());
		assertEquals("int", args[0].getType().toString());
		assertEquals(Argument.PassageMode.IN, args[0].getPassageMode());
		assertEquals("2", args[0].getDefaultValue().toString());
		
		assertPosition(args[1], 22, 9);
		assertEquals("b", args[1].getName().toString());
		assertEquals("char", args[1].getType().toString());
		assertEquals(Argument.PassageMode.IN, args[1].getPassageMode());
		
		assertPosition(args[2], 33, 10);
		assertEquals("c", args[2].getName().toString());
		assertEquals("bool", args[2].getType().toString());
		assertEquals(Argument.PassageMode.OUT, args[2].getPassageMode());
		
		assertPosition(args[3], 45, 13);
		assertEquals("d", args[3].getName().toString());
		assertEquals("float", args[3].getType().toString());
		assertEquals(Argument.PassageMode.INOUT, args[3].getPassageMode());
		
		assertPosition(args[4], 60, 13);
		assertEquals("e", args[4].getName().toString());
		assertEquals("double", args[4].getType().toString());
		assertEquals(Argument.PassageMode.LAZY, args[4].getPassageMode());
	}
	
	public void testFunctionVariadic() {
		String s = " void func(...);";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IFunctionDeclaration func = (IFunctionDeclaration) declDefs[0];
		assertTrue(func.isVariadic());
	}
	
	public void testFunctionVariadic2() {
		String s = " void func(int[] x ...);";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IFunctionDeclaration func = (IFunctionDeclaration) declDefs[0];
		assertEquals(1, func.getArguments().length);
		assertTrue(func.isVariadic());
	}
	
	public void testFunctionBody() {
		String s = " void func() body { }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IFunctionDeclaration func = (IFunctionDeclaration) declDefs[0];
		assertNotNull(func.getBody());
		assertEquals(0, func.getArguments().length);
		assertPosition(func, 1, s.length() - 1);
	}
	
	public void testFunctionIn() {
		String s = " void func() in { } { }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IFunctionDeclaration func = (IFunctionDeclaration) declDefs[0];
		assertNotNull(func.getIn());
		assertPosition(func, 1, s.length() - 1);
	}
	
	public void testFunctionOut() {
		String s = " void func() out { } { }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IFunctionDeclaration func = (IFunctionDeclaration) declDefs[0];
		assertNotNull(func.getOut());
		assertNull(func.getOutName());
		assertPosition(func, 1, s.length() - 1);
	}
	
	public void testFunctionOutName() {
		String s = " void func() out(bla) { } { }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IFunctionDeclaration func = (IFunctionDeclaration) declDefs[0];
		assertNotNull(func.getOut());
		assertEquals("bla", func.getOutName().toString());
		assertPosition(func.getOut(), 22, 3);
		assertPosition(func.getOutName(), 17, 3);
		assertPosition(func, 1, s.length() - 1);
	}

}
