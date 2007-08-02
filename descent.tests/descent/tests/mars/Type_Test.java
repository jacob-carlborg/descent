package descent.tests.mars;

import descent.core.dom.AST;
import descent.core.dom.ASTNode;
import descent.core.dom.AssociativeArrayType;
import descent.core.dom.DelegateType;
import descent.core.dom.DynamicArrayType;
import descent.core.dom.ModifiedType;
import descent.core.dom.NumberLiteral;
import descent.core.dom.PointerType;
import descent.core.dom.PrimitiveType;
import descent.core.dom.QualifiedType;
import descent.core.dom.SimpleType;
import descent.core.dom.SliceType;
import descent.core.dom.StaticArrayType;
import descent.core.dom.TemplateType;
import descent.core.dom.TypeofType;
import descent.core.dom.VariableDeclaration;

public class Type_Test extends Parser_Test {
	
	public void testBasicTypes() {
		Object[][] objs = {
			{ "void", PrimitiveType.Code.VOID, 4 },
			//{ "bit", PrimitiveType.Code.BIT, 3 },
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
			try {
				PrimitiveType type = (PrimitiveType) getType(tri[0].toString());
				assertEquals(tri[0].toString(), type.toString());
				assertEquals(ASTNode.PRIMITIVE_TYPE, type.getNodeType());
				assertEquals(tri[1], type.getPrimitiveTypeCode());
				assertPosition(type, 1, (Integer) tri[2]);
			} catch (Exception e) {
				fail(tri[0].toString());
			}
		}
	}
	
	public void testPointerType() {
		PointerType type = (PointerType) getType("int *");
		assertEquals(ASTNode.POINTER_TYPE, type.getNodeType());
		assertPosition(type, 1, 5);
		assertPosition(type.getComponentType(), 1, 3);
	}
	
	public void testModifiedTypeInvariant() {
		ModifiedType type = (ModifiedType) getType("invariant(int)", AST.D2);
		assertEquals(ASTNode.MODIFIED_TYPE, type.getNodeType());
		assertEquals("invariant", type.getModifier().toString());
		assertPosition(type.getModifier(), 1, 9);
		assertPosition(type, 1, 14);
		assertPosition(type.getComponentType(), 11, 3);
	}
	
	public void testModifiedTypeConst() {
		ModifiedType type = (ModifiedType) getType("const(int)", AST.D2);
		assertEquals(ASTNode.MODIFIED_TYPE, type.getNodeType());
		assertEquals("const", type.getModifier().toString());
		assertPosition(type.getModifier(), 1, 5);
		assertPosition(type, 1, 10);
		assertPosition(type.getComponentType(), 7, 3);
	}
	
	public void testModifiedTypeRecursive() {
		ModifiedType type = (ModifiedType) getType("invariant(const(int))", AST.D2);
		assertEquals(ASTNode.MODIFIED_TYPE, type.getNodeType());
		assertEquals("invariant", type.getModifier().toString());
		type = (ModifiedType) type.getComponentType();
		assertEquals(ASTNode.MODIFIED_TYPE, type.getNodeType());
		assertEquals("const", type.getModifier().toString());
	}
	
	public void testDynamicArrayType() {
		DynamicArrayType type = (DynamicArrayType) getType("int []");
		assertEquals(ASTNode.DYNAMIC_ARRAY_TYPE, type.getNodeType());
		assertPosition(type, 1, 6);
		assertPosition(type.getComponentType(), 1, 3);
	}
	
	public void testStaticArrayType() {
		StaticArrayType type = (StaticArrayType) getType("int [3]");
		assertEquals(ASTNode.STATIC_ARRAY_TYPE, type.getNodeType());
		assertEquals("3", ((NumberLiteral) type.getSize()).getToken());
		assertPosition(type, 1, 7);
		assertPosition(type.getComponentType(), 1, 3);
	}
	
	public void testAssociativeArrayType() {
		AssociativeArrayType type = (AssociativeArrayType) getType("int [char]");
		assertEquals(ASTNode.ASSOCIATIVE_ARRAY_TYPE, type.getNodeType());
		assertEquals("char", type.getKeyType().toString());
		assertPosition(type, 1, 10);
		assertPosition(type.getComponentType(), 1, 3);
	}
	
	public void testIdentifierTypeSingle() {
		SimpleType type = (SimpleType) getType("Clazz");
		assertEquals(ASTNode.SIMPLE_TYPE, type.getNodeType());
		assertEquals("Clazz", type.getName().getFullyQualifiedName());
		assertPosition(type, 1, 5);
	}
	
	public void testIdentifierTypeMany() {
		QualifiedType type = (QualifiedType) getType("mod.bla.Clazz");
		assertEquals(ASTNode.QUALIFIED_TYPE, type.getNodeType());
		
		assertEquals("Clazz", ((SimpleType) type.getType()).getName().getFullyQualifiedName());
		assertPosition(((SimpleType) type.getType()).getName(), 9, 5);
		
		QualifiedType type2 = (QualifiedType) type.getQualifier();
		assertEquals("bla", ((SimpleType) type2.getType()).getName().getFullyQualifiedName());
		assertPosition(((SimpleType) type2.getType()).getName(), 5, 3);
		
		SimpleType type3 = (SimpleType) type2.getQualifier();
		assertEquals("mod", type3.getName().getFullyQualifiedName());
		assertPosition(type3.getName(), 1, 3);
		
		assertPosition(type, 1, 13);
	}
	
	public void testDelegateType() {
		DelegateType type = (DelegateType) getType("int delegate(char, bool)");
		assertEquals(ASTNode.DELEGATE_TYPE, type.getNodeType());
		assertFalse(type.isFunctionPointer());
		assertEquals("int", type.getReturnType().toString());
		assertPosition(type.getReturnType(), 1, 3);
		assertEquals(2, type.arguments().size());
		assertEquals("char", type.arguments().get(0).getType().toString());
		assertPosition(type.arguments().get(0), 14, 4);
		assertEquals("bool", type.arguments().get(1).getType().toString());
		assertPosition(type.arguments().get(1), 20, 4);
		assertPosition(type, 1, 24);
	}
	
	public void testPointerToFunction() {
		DelegateType type = (DelegateType) getType("int function(char, bool)");
		assertEquals(ASTNode.DELEGATE_TYPE, type.getNodeType());
		assertTrue(type.isFunctionPointer());
		assertEquals("int", type.getReturnType().toString());
		assertPosition(type.getReturnType(), 1, 3);
		assertEquals(2, type.arguments().size());
		assertEquals("char", type.arguments().get(0).getType().toString());
		assertPosition(type.arguments().get(0), 14, 4);
		assertEquals("bool", type.arguments().get(1).getType().toString());
		assertPosition(type.arguments().get(1), 20, 4);
		assertPosition(type, 1, 24);
	}
	
	public void testTypeof() {
		TypeofType type = (TypeofType) getType("typeof(1)");
		assertEquals("1", ((NumberLiteral) type.getExpression()).getToken());
		assertPosition(type, 1, 9);
	}
	
	public void testQualifiedTypeofWithTypeof() {
		QualifiedType type = (QualifiedType) getType("typeof(1).bla.ble");
		
		assertEquals("ble", ((SimpleType) type.getType()).getName().getFullyQualifiedName());
		assertPosition(((SimpleType) type.getType()).getName(), 15, 3);
		
		QualifiedType type2 = (QualifiedType) type.getQualifier();
		assertEquals("bla", ((SimpleType) type2.getType()).getName().getFullyQualifiedName());
		assertPosition(((SimpleType) type2.getType()).getName(), 11, 3);
		
		TypeofType type3 = (TypeofType) type2.getQualifier();
		assertEquals("1", ((NumberLiteral) type3.getExpression()).getToken());
		assertPosition(type3, 1, 9);
		
		assertPosition(type, 1, 17);
	}
	
	public void testTypeSlice() {
		SliceType type = (SliceType) getType("int[1 .. 2]");
		assertPosition(type, 1, 11);
		assertEquals("int", type.getComponentType().toString());
		assertEquals("1", ((NumberLiteral) type.getFromExpression()).getToken());
		assertEquals("2", ((NumberLiteral) type.getToExpression()).getToken());
	}
	
	public void testTemplateType() {
		QualifiedType type = (QualifiedType) getType("a.b.Temp!(int)");
		assertPosition(type, 1, 14);
		
		TemplateType templateType = (TemplateType) type.getType();
		
		assertEquals("Temp", templateType.getName().getFullyQualifiedName());
		assertEquals(1, templateType.arguments().size());
		
		QualifiedType type2 = (QualifiedType) type.getQualifier();
		assertEquals("b", ((SimpleType) type2.getType()).getName().getFullyQualifiedName());
		assertPosition(type2, 1, 3);
		assertPosition(((SimpleType) type2.getType()).getName(), 3, 1);
		
		SimpleType type3 = (SimpleType) type2.getQualifier();
		assertEquals("a", type3.getName().getFullyQualifiedName());
		assertPosition(type3, 1, 1);
		assertPosition(type3.getName(), 1, 1);
	}
	
	public void testTemplateType2() {
		TemplateType type = (TemplateType) getType("Temp!(int)");
		
		assertEquals("Temp", type.getName().getFullyQualifiedName());
		assertEquals(1, type.arguments().size());
		assertPosition(type, 1, 10);
	}
	
	public void testTemplateType3() {
		QualifiedType type = (QualifiedType) getType(".Temp!(int)");
		
		assertNull(type.getQualifier());
		assertPosition(type, 1, 11);
		
		TemplateType type2 = (TemplateType) type.getType();
		
		assertEquals("Temp", type2.getName().getFullyQualifiedName());
		assertEquals(1, type2.arguments().size());
	}
	
	public void testTemplateType4() {
		QualifiedType qType = (QualifiedType) getType("Temp!(int).b");
		assertPosition(qType, 1, 12);
		
		SimpleType sType = (SimpleType) qType.getType();
		assertEquals("b", sType.getName().getFullyQualifiedName());
		assertPosition(sType, 12, 1);
		
		TemplateType type = (TemplateType) qType.getQualifier();
		
		assertEquals("Temp", type.getName().getFullyQualifiedName());
		assertEquals(1, type.arguments().size());
		assertPosition(type, 1, 10);
	}
	
	public void testTemplateType5() {
		QualifiedType type = (QualifiedType) getType("Temp!(int).Temp2!(long)");
		assertPosition(type, 1, 23);
		
		TemplateType qType = (TemplateType) type.getQualifier();
		assertPosition(qType, 1, 10);
		
		TemplateType sType = (TemplateType) type.getType();
		assertPosition(sType, 12, 12);
	}
	
	public void testTemplateType6() {
		QualifiedType type = (QualifiedType) getType("typeof(33).Temp2!(long)");
		assertPosition(type, 1, 23);
		
		TypeofType qType = (TypeofType) type.getQualifier();
		assertPosition(qType, 1, 10);
		
		TemplateType sType = (TemplateType) type.getType();
		assertPosition(sType, 12, 12);
	}
	
	private ASTNode getType(String type) {
		return getType(type, "", 0, AST.D1);
	}
	
	private ASTNode getType(String type, int apiLevel) {
		return getType(type, "", 0, apiLevel);
	}
	
	private ASTNode getType(String type, String extra, int problems, int apiLevel) {
		String s = " " + type + " x; " + extra;
		
		VariableDeclaration var = (VariableDeclaration) getSingleDeclarationWithProblems(s, problems, apiLevel);
		return var.getType();
	}

}
