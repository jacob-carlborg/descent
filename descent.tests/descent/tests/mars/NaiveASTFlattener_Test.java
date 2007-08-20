package descent.tests.mars;

import descent.core.dom.AST;
import descent.internal.compiler.parser.Module;
import descent.internal.compiler.parser.Parser;
import junit.framework.TestCase;

public class NaiveASTFlattener_Test extends TestCase {
	
	public void testModule() throws Exception {
		assertToString("module x.y.z;");
	}
	
	public void testImport() throws Exception {
		assertToString("import x.y.z;");
	}
	
	public void testPrivateImport() throws Exception {
		assertToString("private import x.y.z;");
	}
	
	public void testStaticImport() throws Exception {
		assertToString("static import x.y.z;");
	}
	
	public void testImportWithAlias() throws Exception {
		assertToString("import al = x.y.z;");
	}
	
	public void testImportSelective() throws Exception {
		assertToString("import x.y.z : w;");
	}
	
	public void testImportSelective2() throws Exception {
		assertToString("import x.y.z : w, a;");
	}
	
	public void testImportSelectiveWithAliases() throws Exception {
		assertToString("import x.y.z : b = w, c = a;");
	}
	
	public void testVariableDeclaration() throws Exception {
		assertToString("int x;");
	}
	
	public void testVariableDeclaration2() throws Exception {
		assertToString("int x, \ny;");
	}
	
	public void testVariableDeclaration3() throws Exception {
		assertToString("foo.foo2.Bar x;");
	}
	
	public void testVariableDeclarationIntegerInitializer() throws Exception {
		assertToString("int x = 1;");
	}
	
	public void testVariableDeclarationArrayInitializer() throws Exception {
		assertToString("int x = [1: 2, 3: 4];");
	}
	
	public void testAliasDeclaration() throws Exception {
		assertToString("alias int x;");
	}
	
	public void testAliasDeclaration2() throws Exception {
		assertToString("alias int x, \ny;");
	}
	
	public void testTypedefDeclaration() throws Exception {
		assertToString("typedef int x;");
	}
	
	public void testTypedefDeclaration2() throws Exception {
		assertToString("typedef int x, \ny;");
	}
	
	public void testFunction() throws Exception {
		assertToString(
				"void foo() {\n" +
				"}");
	}
	
	public void testFunctionArguments() throws Exception {
		assertToString(
				"void foo(int x, out float z, in double y = 2, ref bool t...) {\n" +
				"}");
	}
	
	public void testTemplatedFunction() throws Exception {
		assertToString(
				"void foo(T)() {\n" +
				"}");
	}
	
	public void testSwitchCase() throws Exception {
		assertToString(
				"void foo(T)() {\n" +
				"  switch(x) {\n" +
				"    case 1, 2, 3:\n" +
				"      break;\n" +
				"  }\n" +
				"}");
	}
	
	public void testProtDeclaration() throws Exception {
		assertToString(
				"public:\n" +
				"  int x;");
	}
	
	public void testProtDeclaration2() throws Exception {
		assertToString(
				"public:\n" +
				"  int x;\n" +
				"private:\n" +
				"  float z;");
	}
	
	public void testTemplateMixin1() throws Exception {
		assertToString("mixin .Foo!(int) m;");
	}

	public void testTemplateMixin2() throws Exception {
		assertToString("mixin Foo!(int).bar m;");
	}
	
	public void testTemplateMixin3() throws Exception {
		assertToString("mixin Foo!(2) m;");
	}
	
	public void testTemplateMixin4() throws Exception {
		assertToString("mixin a.b.Foo!(int) m;");
	}
	
	public void testTemplateMixin5() throws Exception {
		assertToString("mixin a.Foo!(int) m;");
	}
	
	public void testTemplateMixin6() throws Exception {
		assertToString("mixin typeof(2).Foo!(int) m;");
	}
	
	public void testTemplateMixin7() throws Exception {
		assertToString("mixin typeof(2).Foo m;");
	}
	
	public void testTemplateMixin8() throws Exception {
		assertToString("mixin Foo m;");
	}
	
	private void assertToString(String source) {
		Parser parser = new Parser(AST.D2, source);
		Module module = parser.parseModuleObj();
		assertEquals(source.trim(), module.toString().trim());
	}

}
