package descent.tests.mars;

import descent.core.dom.IArrayType;
import descent.core.dom.IAssociativeArrayType;
import descent.core.dom.IBasicType;
import descent.core.dom.ICompilationUnit;
import descent.core.dom.IDelegateType;
import descent.core.dom.IDynamicArrayType;
import descent.core.dom.IElement;
import descent.core.dom.IIdentifierType;
import descent.core.dom.IPointerType;
import descent.core.dom.IStaticArrayType;
import descent.core.dom.IType;
import descent.core.dom.ITypeofType;
import descent.core.dom.IVariableDeclaration;
import descent.internal.core.dom.ParserFacade;

public class Type_Test extends Parser_Test {
	
	public void testBasicTypes() {
		Object[][] objs = {
			{ "void", IBasicType.VOID, 4 },
			{ "bit", IBasicType.BIT, 3 },
			{ "bool", IBasicType.BOOL, 4 },
			{ "byte", IBasicType.INT8, 4 },
			{ "ubyte", IBasicType.UNS8, 5 },
			{ "short", IBasicType.INT16, 5 },
			{ "ushort", IBasicType.UNS16, 6 },
			{ "int", IBasicType.INT32, 3 },
			{ "uint", IBasicType.UNS32, 4 },
			{ "long", IBasicType.INT64, 4 },
			{ "ulong", IBasicType.UNS64, 5 },
			{ "float", IBasicType.FLOAT32, 5 },
			{ "double", IBasicType.FLOAT64, 6 },
			{ "real", IBasicType.FLOAT80, 4 },
			{ "ifloat", IBasicType.IMAGINARY32, 6 },
			{ "idouble", IBasicType.IMAGINARY64, 7 },
			{ "ireal", IBasicType.IMAGINARY80, 5 },
			{ "char", IBasicType.CHAR, 4 },
			{ "wchar", IBasicType.WCHAR, 5 },
			{ "dchar", IBasicType.DCHAR, 5 },
		};
		
		for(Object[] tri : objs) {
			IBasicType type = (IBasicType) getType(tri[0].toString());
			assertEquals(tri[0].toString(), type.toString());
			assertEquals(IType.BASIC_TYPE, type.getElementType());
			assertEquals(tri[1], type.getBasicTypeKind());
			assertPosition(type, 1, (Integer) tri[2]);
			
			assertVisitor(type, 1);
		}
	}
	
	public void testPointerType() {
		IPointerType type = (IPointerType) getType("int *");
		assertEquals(IType.POINTER_TYPE, type.getElementType());
		assertEquals("int*", type.toString());
		assertPosition(type, 1, 5);
		assertPosition(type.getInnerType(), 1, 3);
		
		assertVisitor(type, 2);
	}
	
	public void testDynamicArrayType() {
		IDynamicArrayType type = (IDynamicArrayType) getType("int []");
		assertEquals(IArrayType.DYNAMIC_ARRAY_TYPE, type.getElementType());
		assertEquals("int[]", type.toString());
		assertPosition(type, 1, 6);
		assertPosition(type.getInnerType(), 1, 3);
		
		assertVisitor(type, 1);
	}
	
	public void testStaticArrayType() {
		IStaticArrayType type = (IStaticArrayType) getType("int [3]");
		assertEquals(IArrayType.STATIC_ARRAY_TYPE, type.getElementType());
		assertEquals("int[3]", type.toString());
		assertEquals("3", type.getDimension().toString());
		assertPosition(type, 1, 7);
		assertPosition(type.getInnerType(), 1, 3);
		
		assertVisitor(type, 2);
	}
	
	public void testAssociativeArrayType() {
		IAssociativeArrayType type = (IAssociativeArrayType) getType("int [char]");
		assertEquals(IArrayType.ASSOCIATIVE_ARRAY_TYPE, type.getElementType());
		assertEquals("int[char]", type.toString());
		assertEquals("char", type.getKeyType().toString());
		assertPosition(type, 1, 10);
		assertPosition(type.getInnerType(), 1, 3);
		
		assertVisitor(type, 2);
	}
	
	public void testIdentifierTypeSingle() {
		IIdentifierType type = (IIdentifierType) getType("Clazz");
		assertEquals(IType.IDENTIFIER_TYPE, type.getElementType());
		assertEquals("Clazz", type.toString());
		assertEquals("Clazz", type.getShortName());
		assertPosition(type, 1, 5);
		
		assertVisitor(type, 1);
	}
	
	public void testIdentifierTypeMany() {
		IIdentifierType type = (IIdentifierType) getType("mod.bla.Clazz");
		assertEquals(IType.IDENTIFIER_TYPE, type.getElementType());
		assertEquals("mod.bla.Clazz", type.toString());
		assertEquals("Clazz", type.getShortName());
		assertPosition(type, 1, 13);
		
		assertVisitor(type, 1);
	}
	
	public void testDelegateType() {
		IDelegateType type = (IDelegateType) getType("int delegate(char, bool)");
		assertEquals(IType.DELEGATE_TYPE, type.getElementType());
		assertEquals("int", type.getReturnType().toString());
		assertPosition(type.getReturnType(), 1, 3);
		assertEquals(2, type.getArguments().length);
		assertEquals("char", type.getArguments()[0].getType().toString());
		assertPosition(type.getArguments()[0], 14, 4);
		assertEquals("bool", type.getArguments()[1].getType().toString());
		assertPosition(type.getArguments()[1], 20, 4);
		assertPosition(type, 1, 24);
		
		assertVisitor(type, 6);
	}
	
	public void testPointerToFunction() {
		IDelegateType type = (IDelegateType) getType("int function(char, bool)");
		assertEquals(IType.POINTER_TO_FUNCTION_TYPE, type.getElementType());
		assertEquals("int", type.getReturnType().toString());
		assertPosition(type.getReturnType(), 1, 3);
		assertEquals(2, type.getArguments().length);
		assertEquals("char", type.getArguments()[0].getType().toString());
		assertPosition(type.getArguments()[0], 14, 4);
		assertEquals("bool", type.getArguments()[1].getType().toString());
		assertPosition(type.getArguments()[1], 20, 4);
		assertPosition(type, 1, 24);
		
		assertVisitor(type, 6);
	}
	
	public void testTypeof() {
		ITypeofType type = (ITypeofType) getType("typeof(1)");
		assertEquals("1", type.getExpression().toString());
		assertPosition(type, 1, 9);
		
		assertVisitor(type, 2);
	}
	
	public void testTypeofPlus() {
		ITypeofType type = (ITypeofType) getType("typeof(1).bla");
		assertEquals("1", type.getExpression().toString());
		assertPosition(type, 1, 13);
		// TODO
	}
	
	private IType getType(String type) {
		String s = " " + type + " x;";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IVariableDeclaration var = (IVariableDeclaration) declDefs[0];
		return var.getType();
	}

}
