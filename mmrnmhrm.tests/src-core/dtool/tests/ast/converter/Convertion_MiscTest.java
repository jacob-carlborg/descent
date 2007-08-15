package dtool.tests.ast.converter;

import java.io.IOException;

import mmrnmhrm.tests.BasePluginTest;
import mmrnmhrm.tests.TestUtils;

import org.eclipse.core.runtime.CoreException;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import descent.core.domX.ASTNode;
import dtool.descentadapter.DescentASTConverter;
import dtool.dom.definitions.Module;
import dtool.refmodel.ParserAdapter;
/**
 * Module convertion tests
 * http://www.digitalmars.com/d/module.html
 */
public class Convertion_MiscTest {

	private static final String TESTFILESDIR = "astparser/";

	public static ASTNode testASTConvertion(final String source) throws CoreException {
		
		descent.internal.compiler.parser.Module mod = ParserAdapter.parseSource(source).mod;
		BasePluginTest.assertTrue(mod.problems.size() == 0,
				"Found syntax errors while parsing.");
	
		Module neoModule = DescentASTConverter.convertModule(mod);
		return neoModule;
	}
	
	@BeforeClass
	public static void setUp() throws Exception {
		System.out.println("====================  ====================");
	}

	@After
	public void tearDown() throws Exception {
	}

	
	@Test
	public void testFoo() throws CoreException {
		Convertion_MiscTest.testASTConvertion(
				"module foo;" +
				"import pack.bar;" +
				"public import std.stdio;");
	}
	
	@Test
	public void testRenamed() throws CoreException {
		// RENAMED IMPORT, static import
		Convertion_MiscTest.testASTConvertion(
				"module pack.modul;" +
				"import dee = std.stdio, lang = lang.string;"
				);
		Convertion_MiscTest.testASTConvertion(
				"module pack.modul;" +
				"private static import dee_io = std.stdio;"
				);
	}

	@Test
	public void testSelective() throws CoreException {
		// SELECTIVE IMPORT, static import
		Convertion_MiscTest.testASTConvertion(
				"module pack.modul;" +
				"import std.stdio : writefln, foo = writef;"
				);
		Convertion_MiscTest.testASTConvertion(	"module pack.modul;" +
				"import langio = std.stdio : writefln, foo = writef; "
				);
		Convertion_MiscTest.testASTConvertion(	"module pack.modul;" +
				"private static import langio = std.stdio : writefln, foo = writef; "
				);
	}
	
	@Test
	public void testAll() throws IOException, CoreException {
		Convertion_MiscTest.testASTConvertion(TestUtils.readTestDataFile(TESTFILESDIR+"testNodes.d"));
	}
	
	@Test
	public void testAll2() throws IOException, CoreException {
		Convertion_MiscTest.testASTConvertion(TestUtils.readTestDataFile(TESTFILESDIR+"conditionals.d"));
	}



}
