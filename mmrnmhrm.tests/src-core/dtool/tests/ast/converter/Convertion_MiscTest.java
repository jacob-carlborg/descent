package dtool.tests.ast.converter;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.junit.Test;
/**
 * Misc convertion tests
 */
public class Convertion_MiscTest {


	
	@Test
	public void testFoo() throws CoreException {
		Convertion__CommonTest.testDtoolASTConvertion(
				"module foo;" +
				"import pack.bar;" +
				"public import std.stdio;");
	}
	
	@Test
	public void testRenamed() throws CoreException {
		// RENAMED IMPORT, static import
		Convertion__CommonTest.testDtoolASTConvertion(
				"module pack.modul;" +
				"import dee = std.stdio, lang = lang.string;"
				);
		Convertion__CommonTest.testDtoolASTConvertion(
				"module pack.modul;" +
				"private static import dee_io = std.stdio;"
				);
	}

	@Test
	public void testSelective() throws CoreException {
		// SELECTIVE IMPORT, static import
		Convertion__CommonTest.testDtoolASTConvertion(
				"module pack.modul;" +
				"import std.stdio : writefln, foo = writef;"
				);
		Convertion__CommonTest.testDtoolASTConvertion(	"module pack.modul;" +
				"import langio = std.stdio : writefln, foo = writef; "
				);
		Convertion__CommonTest.testDtoolASTConvertion(	"module pack.modul;" +
				"private static import langio = std.stdio : writefln, foo = writef; "
				);
	}
	
	@Test
	public void testAll() throws IOException, CoreException {
		Convertion__CommonTest.testConversionFromFile("testNodes.d");
	}
	
	@Test
	public void testAll2() throws IOException, CoreException {
		Convertion__CommonTest.testConversionFromFile("conditionals.d");
	}
	
	@Test
	public void testAllMixinContainer() throws IOException, CoreException {
		Convertion__CommonTest.testConversionFromFile("mixincontainer.d");
	}
	
	@Test
	public void testRefNodes() throws IOException, CoreException {
		Convertion__CommonTest.testConversionFromFile("refs.d");
	}

	@Test
	public void testDeclAttrib() throws IOException, CoreException {
		Convertion__CommonTest.testConversionFromFile("declAttrib.d");
	}
	
	@Test
	public void testForeach() throws IOException, CoreException {
		Convertion__CommonTest.testConversionFromFile("for_each.d");
	}
	
	@Test
	public void testNewExp() throws IOException, CoreException {
		Convertion__CommonTest.testConversionFromFile("newExp.d");
	}

}
