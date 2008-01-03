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

}
