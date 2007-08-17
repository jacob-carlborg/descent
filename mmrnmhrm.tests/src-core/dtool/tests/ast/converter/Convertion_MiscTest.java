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
		ConvertionCommonTest.testDtoolASTConvertion(
				"module foo;" +
				"import pack.bar;" +
				"public import std.stdio;");
	}
	
	@Test
	public void testRenamed() throws CoreException {
		// RENAMED IMPORT, static import
		ConvertionCommonTest.testDtoolASTConvertion(
				"module pack.modul;" +
				"import dee = std.stdio, lang = lang.string;"
				);
		ConvertionCommonTest.testDtoolASTConvertion(
				"module pack.modul;" +
				"private static import dee_io = std.stdio;"
				);
	}

	@Test
	public void testSelective() throws CoreException {
		// SELECTIVE IMPORT, static import
		ConvertionCommonTest.testDtoolASTConvertion(
				"module pack.modul;" +
				"import std.stdio : writefln, foo = writef;"
				);
		ConvertionCommonTest.testDtoolASTConvertion(	"module pack.modul;" +
				"import langio = std.stdio : writefln, foo = writef; "
				);
		ConvertionCommonTest.testDtoolASTConvertion(	"module pack.modul;" +
				"private static import langio = std.stdio : writefln, foo = writef; "
				);
	}
	
	@Test
	public void testAll() throws IOException, CoreException {
		ConvertionCommonTest.testConversionFromFile("testNodes.d");
	}
	
	@Test
	public void testAll2() throws IOException, CoreException {
		ConvertionCommonTest.testConversionFromFile("conditionals.d");
	}
	
	@Test
	public void testAllMixinContainer() throws IOException, CoreException {
		ConvertionCommonTest.testConversionFromFile("mixincontainer.d");
	}


}
