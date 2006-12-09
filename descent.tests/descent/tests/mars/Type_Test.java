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
import descent.core.dom.ISliceType;
import descent.core.dom.IStaticArrayType;
import descent.core.dom.IType;
import descent.core.dom.ITypeofType;
import descent.core.dom.IVariableDeclaration;
import descent.internal.core.dom.ParserFacade;
import descent.internal.core.dom.PrimitiveType;

public class Type_Test extends Parser_Test {
	
	public void testBasicTypes() {
		Object[][] objs = {
			{ "void", PrimitiveType.Code.VOID, 4 },
			{ "bit", PrimitiveType.Code.BIT, 3 },
			{ "bool", PrimitiveType.Code.BOOL, 4 },
			{ "byte", PrimitiveType.Code.BYTE, 4 },
			{ "ubyte", PrimitiveType.Code.UBYTE, 5 },
			{ "short", PrimitiveType.Code.SHORT, 5 },
			{ "ushort", PrimitiveType.Code.USHORT, 6 },
			{ "int", PrimitiveType.Code.INT, 3 },
			{ "uint", PrimitiveType.Code.UINT, 4 },
			{ "long", PrimitiveType.Code.LONG, 4 },
			{ "ulong", PrimitiveType.Code.ULONG, 5 },
			{ "float", PrimitiveType.Code.FLOAT, 5 },
			{ "double", PrimitiveType.Code.DOUBLE, 6 },
			{ "real", PrimitiveType.Code.REAL, 4 },
			{ "ifloat", PrimitiveType.Code.IFLOAT, 6 },
			{ "idouble", PrimitiveType.Code.IDOUBLE, 7 },
			{ "ireal", PrimitiveType.Code.IREAL, 5 },
			{ "char", PrimitiveType.Code.CHAR, 4 },
			{ "wchar", PrimitiveType.Code.WCHAR, 5 },
			{ "dchar", PrimitiveType.Code.DCHAR, 5 },
		};
		
		for(Object[] tri : objs) {
			IBasicType type = (IBasicType) getType(tri[0].toString());
			assertEquals(tri[0].toString(), type.toString());
			assertEquals(IType.PRIMITIVE_TYPE, type.getNodeType0());
			assertEquals(tri[1], type.getPrimitiveTypeCode());
			assertPosition(type, 1, (Integer) tri[2]);
			
			assertVisitor(type, 1);
		}
	}
	
	public void testPointerType() {
		IPointerType type = (IPointerType) getType("int *");
		assertEquals(IType.POINTER_TYPE, type.getNodeType0());
		assertEquals("int*", type.toString());
		assertPosition(type, 1, 5);
		assertPosition(type.getInnerType(), 1, 3);
		
		assertVisitor(type, 2);
	}
	
	public void testDynamicArrayType() {
		IDynamicArrayType type = (IDynamicArrayType) getType("int []");
		assertEquals(IArrayType.DYNAMIC_ARRAY_TYPE, type.getNodeType0());
		// TODO test to string somehow assertEquals("int[]", type.toString());
		assertPosition(type, 1, 6);
		assertPosition(type.getComponentType(), 1, 3);
	}
	
	public void testStaticArrayType() {
		IStaticArrayType type = (IStaticArrayType) getType("int [3]");
		assertEquals(IArrayType.STATIC_ARRAY_TYPE, type.getNodeType0());
		// TODO test to string somehow assertEquals("int[3]", type.toString()); // not 
		assertEquals("3", type.getSize().toString());
		assertPosition(type, 1, 7);
		assertPosition(type.getComponentType(), 1, 3);
	}
	
	public void testAssociativeArrayType() {
		IAssociativeArrayType type = (IAssociativeArrayType) getType("int [char]");
		assertEquals(IArrayType.ASSOCIATIVE_ARRAY_TYPE, type.getNodeType0());
		// TODO assertEquals("int[char]", type.toString());
		assertEquals("char", type.getKeyType().toString());
		assertPosition(type, 1, 10);
		assertPosition(type.getComponentType(), 1, 3);
	}
	
	public void testIdentifierTypeSingle() {
		IIdentifierType type = (IIdentifierType) getType("Clazz");
		assertEquals(IType.IDENTIFIER_TYPE, type.getNodeType0());
		assertEquals("Clazz", type.toString());
		assertEquals("Clazz", type.getShortName());
		assertPosition(type, 1, 5);
		
		assertVisitor(type, 1);
	}
	
	public void testIdentifierTypeMany() {
		IIdentifierType type = (IIdentifierType) getType("mod.bla.Clazz");
		assertEquals(IType.IDENTIFIER_TYPE, type.getNodeType0());
		assertEquals("mod.bla.Clazz", type.toString());
		assertEquals("Clazz", type.getShortName());
		assertPosition(type, 1, 13);
		
		assertVisitor(type, 1);
	}
	
	public void testDelegateType() {
		IDelegateType type = (IDelegateType) getType("int delegate(char, bool)");
		assertEquals(IType.DELEGATE_TYPE, type.getNodeType0());
		assertEquals("int", type.getReturnType().toString());
		assertPosition(type.getReturnType(), 1, 3);
		assertEquals(2, type.getArguments().length);
		assertEquals("char", type.getArguments()[0].getType().toString());
		assertPosition(type.getArguments()[0], 14, 4);
		assertEquals("bool", type.getArguments()[1].getType().toString());
		assertPosition(type.getArguments()[1], 20, 4);
		assertPosition(type, 1, 24);
	}
	
	public void testPointerToFunction() {
		IDelegateType type = (IDelegateType) getType("int function(char, bool)");
		assertEquals(IType.POINTER_TO_FUNCTION_TYPE, type.getNodeType0());
		assertEquals("int", type.getReturnType().toString());
		assertPosition(type.getReturnType(), 1, 3);
		assertEquals(2, type.getArguments().length);
		assertEquals("char", type.getArguments()[0].getType().toString());
		assertPosition(type.getArguments()[0], 14, 4);
		assertEquals("bool", type.getArguments()[1].getType().toString());
		assertPosition(type.getArguments()[1], 20, 4);
		assertPosition(type, 1, 24);
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
	
	public void testTypeSlice() {
		ISliceType type = (ISliceType) getType("int[1 .. 2]");
		assertPosition(type, 1, 11);
		assertEquals("int", type.getInnerType().toString());
		assertEquals("1", type.getFrom().toString());
		assertEquals("2", type.getTo().toString());
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
