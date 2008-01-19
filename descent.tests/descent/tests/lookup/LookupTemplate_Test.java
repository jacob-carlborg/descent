package descent.tests.lookup;

/*
 * Tests to check that the implementation of the resolved part of the
 * semantic analysis is working as expected.
 */
public class LookupTemplate_Test extends AbstractLookupTest {
	
	public void testNestedPackagesProblem1() throws Exception {
		one("template Foo() { const char[] Foo = \"int x;\"; }");
		two("mixin(Foo!()); void foo() { x = 3; }");
		assertNoErrors();
	}
	
	public void testTemplateInstance() throws Exception {
		one("class Foo() {\r\n" + 
			"	int x;\r\n" + 
			"}\r\n" + 
			"\r\n" + 
			"Foo!() y;");
		two("void foo() {\r\n" + 
			"	y.x = 3;\r\n" + 
			"}");
		assertNoErrors();
	}
	
	public void testTemplateInstance2() throws Exception {
		one("class Foo(T) {\r\n" + 
			"	T x;\r\n" + 
			"}\r\n" + 
			"\r\n" + 
			"Foo!(int) y;");
		two("void foo() {\r\n" + 
			"	y.x = 3;\r\n" + 
			"}");
		assertNoErrors();
	}
	
	public void testTemplateInstance2A() throws Exception {
		one("class Foo(T : int) {\r\n" + 
			"	T x;\r\n" + 
			"}\r\n" + 
			"\r\n" + 
			"Foo!(int) y;");
		two("void foo() {\r\n" + 
			"	y.x = 3;\r\n" + 
			"}");
		assertNoErrors();
	}
	
	public void testTemplateInstance2B() throws Exception {
		one("class Foo(T : int = int) {\r\n" + 
			"	T x;\r\n" + 
			"}\r\n" + 
			"\r\n" + 
			"Foo!(int) y;");
		two("void foo() {\r\n" + 
			"	y.x = 3;\r\n" + 
			"}");
		assertNoErrors();
	}
	
	public void testTemplateInstance3() throws Exception {
		one("class Foo(int T) {\r\n" + 
			"	int x = T;\r\n" + 
			"}\r\n" + 
			"\r\n" + 
			"Foo!(3) y;");
		two("void foo() {\r\n" + 
			"	y.x = 3;\r\n" + 
			"}");
		assertNoErrors();
	}
	
	public void testNestedTemplateInstance() throws Exception {
		one("template Bar(U) {\r\n" + 
			"	class Foo(T) {\r\n" + 
			"		T prop1;\r\n" + 
			"		U prop2;\r\n" + 
			"	}\r\n" + 
			"}\r\n" + 
			"\r\n" + 
			"Bar!(char[]).Foo!(int) foo;");
		two("void bar() {\r\n" + 
			"	foo.prop1 = 3;\r\n" + 
			"	foo.prop2 = \"hey\";\r\n" + 
			"}");
		assertNoErrors();
	}
	
	public void testMixin() throws Exception {
		one("template Foo(T) {\r\n" + 
			"	int x;\r\n" + 
			"}\r\n" + 
			"\r\n" + 
			"mixin Foo!(int);");
		two("void bar() {\r\n" + 
			"	x = 3;\r\n" + 
			"}");
		assertNoErrors();
	}

}
