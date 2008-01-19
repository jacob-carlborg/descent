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

}
