package descent.tests.mars;

import descent.core.dom.AST;
import descent.internal.compiler.parser.Module;

public class ToBindingString_Test extends Parser_Test {
	
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
				"typedef X x<<class X>>;"
			);
	}
	
	public void testVarDeclaration() {
		assertToBindingString(
				"class X { } X x;",
				
				"class X {\n" +
				"}\n" +
				"X x<<class X>>;"
			);
	}
	
	public void testNewExp() {
		assertToBindingString(
				"class X { } void foo() { X x = new X; }",
				
				"class X {\n" +
				"}\n" +
				"void foo() {\n" +
				"  X x<<class X>> = new X<<class X>>;\n" +
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
				"void bar(int x<<int>>) {\n" +
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
				"void bar(X x<<class X>>) {\n" +
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
				"  int x<<int>> = 2;\n" +
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
				"  int x<<int>> = 2;\n" +
				"  int z<<int>> = x<<function foo().variable x>>;\n" +
				"}"
			);
	}
	
	public void testAddExp() {
		assertToBindingString(
				"void foo() { int x = 2; int z = 2 + x; }",
				
				"void foo() {\n" +
				"  int x<<int>> = 2;\n" +
				"  int z<<int>> = (2 + x<<function foo().variable x>>)<<int>>;\n" +
				"}"
			);
	}
	
	public void testAddrExp() {
		assertToBindingString(
				"void foo() { int x = 2; int* y = &x; }",
				
				"void foo() {\n" +
				"  int x<<int>> = 2;\n" +
				"  int* y<<int*>> = (&x<<function foo().variable x>>)<<int*>>;\n" +
				"}"
			);
	}
	
	public void testArrayLiteralExp() {
		assertToBindingString(
				"void foo() { int x = 2; int[1] y = [ x ]; }",
				
				"void foo() {\n" +
				"  int x<<int>> = 2;\n" +
				"  int[1] y<<int[1]>> = [x<<function foo().variable x>>]<<int[]>>;\n" +
				"}"
			);
	}
	
	public void testMulExp() {
		assertToBindingString(
				"void foo() { int x = 2; int z = 2 * x; }",
				
				"void foo() {\n" +
				"  int x<<int>> = 2;\n" +
				"  int z<<int>> = (2 * x<<function foo().variable x>>)<<int>>;\n" +
				"}"
			);
	}	
	
	protected void assertToBindingString(String actual, String expected) {
		Module mod = getModuleSemanticNoProblems(actual, AST.D1);
		assertEquals(expected, mod.toString());
	}

}
