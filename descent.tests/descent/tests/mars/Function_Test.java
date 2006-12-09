package descent.tests.mars;

import descent.core.dom.IArgument;
import descent.core.dom.ICompilationUnit;
import descent.core.dom.IElement;
import descent.core.dom.IFunctionDeclaration;
import descent.internal.core.dom.Argument;
import descent.internal.core.dom.FunctionDeclaration;
import descent.internal.core.dom.ParserFacade;
import descent.internal.core.dom.DmdType;
import descent.internal.core.dom.PrimitiveType;

public class Function_Test extends Parser_Test {
	
	public void testConstructor() {
		String s = " this() { }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IFunctionDeclaration f = (IFunctionDeclaration) declDefs[0];
		assertEquals(IElement.FUNCTION_DECLARATION, f.getNodeType0());
		assertEquals(FunctionDeclaration.Kind.CONSTRUCTOR, f.getKind());
		assertEquals(PrimitiveType.Code.VOID, ((PrimitiveType) f.getReturnType()).getPrimitiveTypeCode());
		assertEquals(0, f.arguments().size());
		
		assertEquals("this", f.getName().getIdentifier());
		assertPosition(f.getName(), 1, 4);
		
		assertPosition(f, 1, 10);
	}
	
	public void testEmptyConstructor() {
		String s = " this();";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IFunctionDeclaration f = (IFunctionDeclaration) declDefs[0];
		assertEquals(IElement.FUNCTION_DECLARATION, f.getNodeType0());
		assertEquals(FunctionDeclaration.Kind.CONSTRUCTOR, f.getKind());
	}
	
	public void testDestructor() {
		String s = " ~this() { }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IFunctionDeclaration f = (IFunctionDeclaration) declDefs[0];
		assertEquals(IElement.FUNCTION_DECLARATION, f.getNodeType0());
		assertEquals(FunctionDeclaration.Kind.DESTRUCTOR, f.getKind());
		assertEquals(PrimitiveType.Code.VOID, ((PrimitiveType) f.getReturnType()).getPrimitiveTypeCode());
		assertEquals(0, f.arguments().size());
		
		assertEquals("~this", f.getName().getIdentifier());
		assertPosition(f.getName(), 1, 5);
		
		assertPosition(f, 1, 11);
	}
	
	public void testEmptyDestructor() {
		String s = " ~this();";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IFunctionDeclaration f = (IFunctionDeclaration) declDefs[0];
		assertEquals(IElement.FUNCTION_DECLARATION, f.getNodeType0());
		assertEquals(FunctionDeclaration.Kind.DESTRUCTOR, f.getKind());
		assertEquals(PrimitiveType.Code.VOID, ((PrimitiveType) f.getReturnType()).getPrimitiveTypeCode());
	}
	
	public void testStaticConstructor() {
		String s = " static this() { }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IFunctionDeclaration f = (IFunctionDeclaration) declDefs[0];
		assertEquals(IElement.FUNCTION_DECLARATION, f.getNodeType0());
		assertEquals(FunctionDeclaration.Kind.STATIC_CONSTRUCTOR, f.getKind());
		assertEquals(PrimitiveType.Code.VOID, ((PrimitiveType) f.getReturnType()).getPrimitiveTypeCode());
		assertEquals(0, f.arguments().size());
		
		assertEquals("this", f.getName().getIdentifier());
		assertPosition(f.getName(), 8, 4);
		
		assertPosition(f, 1, 17);
	}
	
	public void testStaticDestructor() {
		String s = " static ~this() { }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IFunctionDeclaration f = (IFunctionDeclaration) declDefs[0];
		assertEquals(IElement.FUNCTION_DECLARATION, f.getNodeType0());
		assertEquals(FunctionDeclaration.Kind.STATIC_DESTRUCTOR, f.getKind());
		assertEquals(PrimitiveType.Code.VOID, ((PrimitiveType) f.getReturnType()).getPrimitiveTypeCode());
		assertEquals(0, f.arguments().size());
		
		assertEquals("~this", f.getName().getIdentifier());
		assertPosition(f.getName(), 8, 5);
		
		assertPosition(f, 1, 18);
	}
	
	public void testNew() {
		String s = " new() { }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IFunctionDeclaration f = (IFunctionDeclaration) declDefs[0];
		assertEquals(IElement.FUNCTION_DECLARATION, f.getNodeType0());
		assertEquals(FunctionDeclaration.Kind.NEW, f.getKind());
		assertEquals(PrimitiveType.Code.VOID, ((PrimitiveType) f.getReturnType()).getPrimitiveTypeCode());
		assertEquals(0, f.arguments().size());
		
		assertEquals("new", f.getName().getIdentifier());
		assertPosition(f.getName(), 1, 3);
		
		assertPosition(f, 1, s.length() - 1);
	}
	
	public void testDelete() {
		String s = " delete() { }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IFunctionDeclaration f = (IFunctionDeclaration) declDefs[0];
		assertEquals(IElement.FUNCTION_DECLARATION, f.getNodeType0());
		assertEquals(FunctionDeclaration.Kind.DELETE, f.getKind());
		assertEquals(PrimitiveType.Code.VOID, ((PrimitiveType) f.getReturnType()).getPrimitiveTypeCode());
		assertEquals(0, f.arguments().size());
		
		assertEquals("delete", f.getName().getIdentifier());
		assertPosition(f.getName(), 1, 6);
		
		assertPosition(f, 1, s.length() - 1);
	}
	
	public void testFunctionWithoutArguments() {
		String s = " void func() { }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IFunctionDeclaration func = (IFunctionDeclaration) declDefs[0];
		assertEquals(IElement.FUNCTION_DECLARATION, func.getNodeType0());
		assertEquals(FunctionDeclaration.Kind.FUNCTION, func.getKind());

		assertEquals("void", func.getReturnType().toString());
		assertEquals("func", func.getName().getIdentifier());
		assertPosition(func.getName(), 6, 4);
		assertEquals(0, func.arguments().size());
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
		
		IArgument[] args = func.arguments().toArray(new IArgument[func.arguments().size()]);
		assertEquals(5, args.length);
		
		assertPosition(args[0], 11, 5);
		assertEquals(IElement.ARGUMENT, args[0].getNodeType0());
		assertEquals("a", args[0].getName().getIdentifier());
		assertEquals("int", args[0].getType().toString());
		assertEquals(Argument.PassageMode.IN, args[0].getPassageMode());
		
		assertPosition(args[1], 18, 9);
		assertEquals("b", args[1].getName().getIdentifier());
		assertEquals("char", args[1].getType().toString());
		assertEquals(Argument.PassageMode.IN, args[1].getPassageMode());
		
		assertPosition(args[2], 29, 10);
		assertEquals("c", args[2].getName().getIdentifier());
		assertEquals("bool", args[2].getType().toString());
		assertEquals(Argument.PassageMode.OUT, args[2].getPassageMode());
		
		assertPosition(args[3], 41, 13);
		assertEquals("d", args[3].getName().getIdentifier());
		assertEquals("float", args[3].getType().toString());
		assertEquals(Argument.PassageMode.INOUT, args[3].getPassageMode());
		
		assertPosition(args[4], 56, 13);
		assertEquals("e", args[4].getName().getIdentifier());
		assertEquals("double", args[4].getType().toString());
		assertEquals(Argument.PassageMode.LAZY, args[4].getPassageMode());
	}
	
	public void testFunctionWithOneArgument() {
		String s = " void func(int a) { }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		
		IFunctionDeclaration func = (IFunctionDeclaration) declDefs[0];
		
		assertEquals(1, func.arguments().size());
	}
	
	public void testConstructorWithArguments() {
		String s = "      this(int a = 2, in char b, out bool c, inout float d, lazy double e) { }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		
		IFunctionDeclaration func = (IFunctionDeclaration) declDefs[0];
		assertPosition(func, 6, 72);
		
		assertFalse(func.isVariadic());
		
		IArgument[] args = func.arguments().toArray(new IArgument[func.arguments().size()]);
		assertEquals(5, args.length);
		
		assertPosition(args[0], 11, 9);
		assertEquals("a", args[0].getName().getIdentifier());
		assertEquals("int", args[0].getType().toString());
		assertEquals(Argument.PassageMode.IN, args[0].getPassageMode());
		assertEquals("2", args[0].getDefaultValue().toString());
		
		assertPosition(args[1], 22, 9);
		assertEquals("b", args[1].getName().getIdentifier());
		assertEquals("char", args[1].getType().toString());
		assertEquals(Argument.PassageMode.IN, args[1].getPassageMode());
		
		assertPosition(args[2], 33, 10);
		assertEquals("c", args[2].getName().getIdentifier());
		assertEquals("bool", args[2].getType().toString());
		assertEquals(Argument.PassageMode.OUT, args[2].getPassageMode());
		
		assertPosition(args[3], 45, 13);
		assertEquals("d", args[3].getName().getIdentifier());
		assertEquals("float", args[3].getType().toString());
		assertEquals(Argument.PassageMode.INOUT, args[3].getPassageMode());
		
		assertPosition(args[4], 60, 13);
		assertEquals("e", args[4].getName().getIdentifier());
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
		assertEquals(1, func.arguments().size());
		assertTrue(func.isVariadic());
	}
	
	public void testFunctionBody() {
		String s = " void func() body { }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IFunctionDeclaration func = (IFunctionDeclaration) declDefs[0];
		assertNotNull(func.getBody());
		assertEquals(0, func.arguments().size());
		assertPosition(func, 1, s.length() - 1);
	}
	
	public void testFunctionIn() {
		String s = " void func() in { } { }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IFunctionDeclaration func = (IFunctionDeclaration) declDefs[0];
		assertNotNull(func.getPrecondition());
		assertPosition(func, 1, s.length() - 1);
	}
	
	public void testFunctionOut() {
		String s = " void func() out { } { }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IFunctionDeclaration func = (IFunctionDeclaration) declDefs[0];
		assertNotNull(func.getPostcondition());
		assertNull(func.getPostconditionVariableName());
		assertPosition(func, 1, s.length() - 1);
	}
	
	public void testFunctionOutName() {
		String s = " void func() out(bla) { } { }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IFunctionDeclaration func = (IFunctionDeclaration) declDefs[0];
		assertNotNull(func.getPostcondition());
		assertEquals("bla", func.getPostconditionVariableName().getIdentifier());
		assertPosition(func.getPostcondition(), 22, 3);
		assertPosition(func.getPostconditionVariableName(), 17, 3);
		assertPosition(func, 1, s.length() - 1);
	}

}
