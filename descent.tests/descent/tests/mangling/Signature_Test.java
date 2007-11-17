package descent.tests.mangling;

import descent.core.dom.AST;
import descent.internal.compiler.parser.AliasDeclaration;
import descent.internal.compiler.parser.ClassDeclaration;
import descent.internal.compiler.parser.EnumDeclaration;
import descent.internal.compiler.parser.FuncDeclaration;
import descent.internal.compiler.parser.Module;
import descent.internal.compiler.parser.StructDeclaration;
import descent.internal.compiler.parser.TY;
import descent.internal.compiler.parser.TemplateDeclaration;
import descent.internal.compiler.parser.TypeBasic;
import descent.internal.compiler.parser.TypedefDeclaration;
import descent.internal.compiler.parser.VarDeclaration;
import descent.tests.mars.Parser_Test;

public class Signature_Test extends Parser_Test {
	
	public void testTypeBasic() {
		assertEquals("v", new TypeBasic(TY.Tvoid).getSignature());
		assertEquals("g", new TypeBasic(TY.Tint8).getSignature());
		assertEquals("h", new TypeBasic(TY.Tuns8).getSignature());
		assertEquals("s", new TypeBasic(TY.Tint16).getSignature());
		assertEquals("t", new TypeBasic(TY.Tuns16).getSignature());
		assertEquals("i", new TypeBasic(TY.Tint32).getSignature());
		assertEquals("k", new TypeBasic(TY.Tuns32).getSignature());
		assertEquals("l", new TypeBasic(TY.Tint64).getSignature());
		assertEquals("m", new TypeBasic(TY.Tuns64).getSignature());
		assertEquals("f", new TypeBasic(TY.Tfloat32).getSignature());
		assertEquals("d", new TypeBasic(TY.Tfloat64).getSignature());
		assertEquals("e", new TypeBasic(TY.Tfloat80).getSignature());
		assertEquals("o", new TypeBasic(TY.Timaginary32).getSignature());
		assertEquals("p", new TypeBasic(TY.Timaginary64).getSignature());
		assertEquals("j", new TypeBasic(TY.Timaginary80).getSignature());
		assertEquals("q", new TypeBasic(TY.Tcomplex32).getSignature());
		assertEquals("r", new TypeBasic(TY.Tcomplex64).getSignature());
		assertEquals("c", new TypeBasic(TY.Tcomplex80).getSignature());
		assertEquals("b", new TypeBasic(TY.Tbool).getSignature());
		assertEquals("a", new TypeBasic(TY.Tchar).getSignature());
		assertEquals("u", new TypeBasic(TY.Twchar).getSignature());
		assertEquals("w", new TypeBasic(TY.Tdchar).getSignature());
	}
	
	public void testTypePointer() {
		assertVarType("Pi", "int*");
	}
	
	public void testTypeDArray() {
		assertVarType("Ai", "int[]");
	}
	
	public void testTypeSArray() {
		assertVarType("G3i", "int[3]");
	}
	
	public void testTypeAArray() {
		assertVarType("Hui", "int[wchar]");
	}
	
	public void testClass() {
		String s = "module main; class Foo { }";
		Module m = getModuleSemantic(s, AST.D1).module;
		
		ClassDeclaration c = (ClassDeclaration) m.members.get(1);
		assertEquals("C4main3Foo", c.getSignature());
	}
	
	public void testStruct() {
		String s = "module main; struct Foo { }";
		Module m = getModuleSemantic(s, AST.D1).module;
		
		StructDeclaration c = (StructDeclaration) m.members.get(1);
		assertEquals("S4main3Foo", c.getSignature());
	}
	
	public void testEnum() {
		String s = "module main; enum Foo { x }";
		Module m = getModuleSemantic(s, AST.D1).module;
		
		EnumDeclaration c = (EnumDeclaration) m.members.get(1);
		assertEquals("E4main3Foo", c.getSignature());
	}
	
	public void testTypedef() {
		String s = "module main; typedef int Foo;";
		Module m = getModuleSemantic(s, AST.D1).module;
		
		TypedefDeclaration c = (TypedefDeclaration) m.members.get(1);
		assertEquals("T4main3Foo", c.getSignature());
	}
	
	public void testVar() {
		String s = "module main; int Foo;";
		Module m = getModuleSemantic(s, AST.D1).module;
		
		VarDeclaration c = (VarDeclaration) m.members.get(1);
		assertEquals("Q4main3Foo", c.getSignature());
	}
	
	public void testFunction() {
		String s = "module main; void Foo() { }";
		Module m = getModuleSemantic(s, AST.D1).module;
		
		FuncDeclaration c = (FuncDeclaration) m.members.get(1);
		assertEquals("O4main3FooFZv", c.getSignature());
	}
	
	// TODO missing parameter information
	public void testTemplate() {
		String s = "module main; template Foo() { }";
		Module m = getModuleSemantic(s, AST.D1).module;
		
		TemplateDeclaration c = (TemplateDeclaration) m.members.get(1);
		assertEquals("N4main3Foo", c.getSignature());
	}
	
	public void testAlias() {
		String s = "module main; alias int Foo;";
		Module m = getModuleSemantic(s, AST.D1).module;
		
		AliasDeclaration c = (AliasDeclaration) m.members.get(1);
		assertEquals("M4main3Foo", c.getSignature());
	}
	
	private void assertVarType(String expected, String source) {
		String s = source + " x;";
		Module m = getModuleSemantic(s, AST.D1).module;
		VarDeclaration v = (VarDeclaration) m.members.get(1);
		assertEquals(expected, v.type.getSignature());
	}
	
}
