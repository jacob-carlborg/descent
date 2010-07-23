package dtool.parser;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.junit.Test;
/**
 * Misc convertion tests
 */
public class Convertion_MiscTest extends Parser__CommonTest {

	
	@Test
	public void testFoo() throws CoreException {
		testDtoolASTConvertion(
				"module foo;" +
				"import pack.bar;" +
				"public import std.stdio;"
		);
	}
	
	@Test
	public void testRenamed() throws CoreException {
		// RENAMED IMPORT, static import
		testDtoolASTConvertion(
				"module pack.modul;" +
				"import dee = std.stdio, lang = lang.string;"
		);
		testDtoolASTConvertion(
				"module pack.modul;" +
				"private static import dee_io = std.stdio;"
		);
	}

	@Test
	public void testSelective() throws CoreException {
		// SELECTIVE IMPORT, static import
		testDtoolASTConvertion(
				"module pack.modul;" +
				"import std.stdio : writefln, foo = writef;"
		);
		testDtoolASTConvertion(	"module pack.modul;" +
				"import langio = std.stdio : writefln, foo = writef; "
		);
		testDtoolASTConvertion(	"module pack.modul;" +
				"private static import langio = std.stdio : writefln, foo = writef; "
		);
	}
	
	@Test
	public void testAll() throws IOException, CoreException {
		testConversionFromFile("testNodes.d");
	}
	
	@Test
	public void testAll2() throws IOException, CoreException {
		testConversionFromFile("conditionals.d");
	}
	
	@Test
	public void testAllMixinContainer() throws IOException, CoreException {
		testConversionFromFile("mixincontainer.d");
	}
	
	@Test
	public void testRefNodes() throws IOException, CoreException {
		testConversionFromFile("refs.d");
	}

	@Test
	public void testDeclAttrib() throws IOException, CoreException {
		testConversionFromFile("declAttrib.d");
	}
	
	@Test
	public void testForeach() throws IOException, CoreException {
		testConversionFromFile("for_each.d");
	}
	
	@Test
	public void testNewExp() throws IOException, CoreException {
		testConversionFromFile("newExp.d");
	}

}
