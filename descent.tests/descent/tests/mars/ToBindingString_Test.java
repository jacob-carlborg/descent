package descent.tests.mars;

import descent.core.dom.AST;
import descent.internal.compiler.parser.CondExp;
import descent.internal.compiler.parser.ExpInitializer;
import descent.internal.compiler.parser.Module;
import descent.internal.compiler.parser.PostExp;
import descent.internal.compiler.parser.VarDeclaration;

public class ToBindingString_Test extends Parser_Test {
	
	public void testClassBaseClass() {
		assertToBindingString(
				"class X { } class Y : X { }",
				
				"class X {\n" +
				"}\n" +
				"class Y : X<<class X>> {\n" +
				"}"
			);
	}
	
	public void testAlias() {
		assertToBindingString(
				"class X { } alias X x;",
				
				"class X {\n" +
				"}\n" +
				"alias X<<class X>> x;"
			);
	}
	
	public void testAliasWithNesting() {
		assertToBindingString(
				"class X { void foo() { } } alias X.foo x;",
				
				"class X {\n" +
				"  void foo() {\n" +
				"  }\n" +
				"}\n" +
				"alias X<<class X>>.foo<<class X.function foo()>> x;"
			);
	}
	
	public void testAliasWithNesting2() {
		assertToBindingString(
				"class X { class Y { void foo() { } } } alias X.Y.foo x;",
				
				"class X {\n" +
				"  class Y {\n" +
				"    void foo() {\n" +
				"    }\n" +
				"  }\n" +
				"}\n" +
				"alias X<<class X>>.Y<<class X.class Y>>.foo<<class X.class Y.function foo()>> x;"
			);
	}
	
	public void testTypedef() {
		assertToBindingString(
				"class X { } typedef X x;",
				
				"class X {\n" +
				"}\n" +
				"typedef X<<class X>> x;"
			);
	}
	
	public void testVarDeclaration() {
		assertToBindingString(
				"class X { } X x;",
				
				"class X {\n" +
				"}\n" +
				"X<<class X>> x;"
			);
	}
	
	public void testNewExp() {
		assertToBindingString(
				"class X { } void foo() { X x = new X; }",
				
				"class X {\n" +
				"}\n" +
				"void foo() {\n" +
				"  X<<class X>> x = new X<<class X>>;\n" +
				"}"
			);
	}
	
	public void testCallExp() {
		assertToBindingString(
				"class X { } void bar() { } void foo() { bar(); }",
				
				"class X {\n" +
				"}\n" +
				"void bar() {\n" +
				"}\n" +
				"void foo() {\n" +
				"  bar()<<function bar()>>;\n" +
				"}"
			);
	}
	
	public void testCallExpWithScalarArguments() {
		assertToBindingString(
				"class X { } void bar(int x) { } void foo() { bar(1); }",
				
				"class X {\n" +
				"}\n" +
				"void bar(int x) {\n" +
				"}\n" +
				"void foo() {\n" +
				"  bar(1)<<function bar(int)>>;\n" +
				"}"
			);
	}
	
	public void testCallExpWithClassArguments() {
		assertToBindingString(
				"class X { } void bar(X x) { } void foo() { bar(new X); }",
				
				"class X {\n" +
				"}\n" +
				"void bar(X<<class X>> x) {\n" +
				"}\n" +
				"void foo() {\n" +
				"  bar(new X<<class X>>)<<function bar(class X)>>;\n" +
				"}"
			);
	}
	
	public void testFunctionVarBindingInAssignmentLeftHandSide() {
		assertToBindingString(
				"class X { } void foo() { int x = 2; x = 3; }",
				
				"class X {\n" +
				"}\n" +
				"void foo() {\n" +
				"  int x = 2;\n" +
				"  x<<function foo().variable x>> = 3;\n" +
				"}"
			);
	}
	
	public void testFunctionVarBindingInAssignmentRightHandSide() {
		assertToBindingString(
				"class X { } void foo() { int x = 2; int z = x; }",
				
				"class X {\n" +
				"}\n" +
				"void foo() {\n" +
				"  int x = 2;\n" +
				"  int z = x<<function foo().variable x>>;\n" +
				"}"
			);
	}
	
	public void testInitializer() {
		assertToBindingString(
				"int x; int y = x;",
				
				"int x;\n" +
				"int y = x<<variable x>>;"
			);
	}
	
	public void testAddAssignExp() {
		testBinExpScalar("+=");
	}
	
	public void testAddExp() {
		testBinExpScalar("+");
	}
	
	public void testAndAndExp() {
		testBinExpScalar("&&", "bool");
	}
	
	public void testAndAssignExp() {
		testBinExpScalar("&=");
	}
	
	public void testAndExp() {
		testBinExpScalar("&");
	}
	
//	public void testAssignExp() {
//		testBinExpScalar("=");
//	}
	
	public void testCatAssignExp() {
		testBinExpString("~=");
	}
	
	public void testCatExp() {
		testBinExpString("~");
	}
	
	public void testCmpExp() {
		testBinExpScalar("<", "bool");
	}
	
//	public void testCommaExp() {
//		testBinExpScalar(",");
//	}
	
	public void testCondExp() {
		Module m = getModuleSemanticNoProblems("int x; int y; int z; int w = x ? y : z;", AST.D1);
		VarDeclaration x = (VarDeclaration) m.members.get(0);
		VarDeclaration y = (VarDeclaration) m.members.get(1);
		VarDeclaration z = (VarDeclaration) m.members.get(2);
		VarDeclaration w = (VarDeclaration) m.members.get(3);
		CondExp tri = (CondExp) ((ExpInitializer) w.sourceInit).sourceExp;
		assertSame(x, tri.econd.getBinding());
		assertSame(y, tri.sourceE1.getBinding());
		assertSame(z, tri.sourceE2.getBinding());
	}
	
	public void testDivAssignExp() {
		testBinExpScalar("/=");
	}
	
	public void testDivExp() {
		testBinExpScalar("/");
	}
	
	public void testEqualExp() {
		testBinExpScalar("==", "bool");
	}
	
	public void testIdentityExp() {
		testBinExpScalar("is", "bool");
	}
	
	public void testMinAssignExp() {
		testBinExpScalar("-=");
	}
	
	public void testMinExp() {
		testBinExpScalar("-");
	}
	
	public void testModAssignExp() {
		testBinExpScalar("%=");
	}
	
	public void testModExp() {
		testBinExpScalar("%");
	}
	
	public void testMulAssignExp() {
		testBinExpScalar("*=");
	}
	
	public void testMulExp() {
		testBinExpScalar("*");
	}
	
	public void testOrAssignExp() {
		testBinExpScalar("|=");
	}
	
	public void testOrExp() {
		testBinExpScalar("|");
	}
	
	public void testOrOrExp() {
		testBinExpScalar("||", "bool");
	}
	
	public void testPostExp() {
		Module m = getModuleSemanticNoProblems("int x; int y = x++;", AST.D1);
		VarDeclaration x = (VarDeclaration) m.members.get(0);
		VarDeclaration y = (VarDeclaration) m.members.get(1);
		PostExp bin = (PostExp) ((ExpInitializer) y.sourceInit).sourceExp;
		assertSame(x, bin.sourceE1.getBinding());
	}
	
	public void testShlAssignExp() {
		testBinExpScalar("<<=");
	}
	
	public void testShlExp() {
		testBinExpScalar("<<");
	}
	
	public void testShrAssignExp() {
		testBinExpScalar(">>=");
	}
	
	public void testShrExp() {
		testBinExpScalar(">>");
	}
	
	public void testUshrAssignExp() {
		testBinExpScalar(">>>=");
	}
	
	public void testUshrExp() {
		testBinExpScalar(">>>");
	}
	
	public void testXorAssignExp() {
		testBinExpScalar("^=");
	}
	
	public void testXorExp() {
		testBinExpScalar("^");
	}
	
	public void testArrayLiteralExp() {
		assertToBindingString(
				"int x = 2; int[1] y = [ x ];",
				
				"int x = 2;\n" +
				"int[1] y = [x<<variable x>>];"
			);
	}
	
	public void testArrayLiteralExpInFunc() {
		assertToBindingString(
				"void foo() { int x = 2; int[1] y = [ x ]; }",
				
				"void foo() {\n" +
				"  int x = 2;\n" +
				"  int[1] y = [x<<function foo().variable x>>];\n" +
				"}"
			);
	}
	
	protected void assertToBindingString(String actual, String expected) {
		Module mod = getModuleSemanticNoProblems(actual, AST.D1);
		assertEquals(expected, mod.toString());
	}
	
	private void testBinExpScalar(String op) {
		testBinExpScalar(op, "int");
	}
	
	private void testBinExpScalar(String op, String result) {
		assertToBindingString(
				"int x; int y; int z = x " + op + " y;",
				
				"int x;\n" +
				"int y;\n" +
				"int z = (x<<variable x>> " + op + " y<<variable y>>)<<" + result + ">>;"
			);
	}
	
	private void testBinExpString(String op) {
		assertToBindingString(
				"char[] x; char[] y; char[] z = x " + op + " y;",
				
				"char[] x;\n" +
				"char[] y;\n" +
				"char[] z = (x<<variable x>> " + op + " y<<variable y>>)<<char[]>>;"
			);
	}

}
