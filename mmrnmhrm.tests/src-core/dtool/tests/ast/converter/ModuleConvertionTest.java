package dtool.tests.ast.converter;

import java.io.IOException;

import mmrnmhrm.tests.BaseTestClass;
import mmrnmhrm.tests.CoreTestUtils;
import mmrnmhrm.tests.TestUtils;

import org.eclipse.core.runtime.CoreException;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
/**
 * Module convertion tests
 * http://www.digitalmars.com/d/module.html
 */
public class ModuleConvertionTest extends BaseTestClass {

	@BeforeClass
	public static void setUp() throws Exception {
		System.out.println("====================  ====================");
	}

	@After
	public void tearDown() throws Exception {
	}

	
	@Test
	public void testFoo() throws CoreException {
		CoreTestUtils.testParseCUnit(
				"module foo;" +
				"import pack.bar;" +
				"public import std.stdio;");
	}
	
	@Test
	public void testRenamed() throws CoreException {
		// RENAMED IMPORT, static import
		CoreTestUtils.testParseCUnit(
				"module pack.modul;" +
				"import dee = std.stdio, lang = lang.string;"
				);
		CoreTestUtils.testParseCUnit(
				"module pack.modul;" +
				"private static import dee_io = std.stdio;"
				);
	}

	@Test
	public void testSelective() throws CoreException {
		// SELECTIVE IMPORT, static import
		CoreTestUtils.testParseCUnit(
				"module pack.modul;" +
				"import std.stdio : writefln, foo = writef;"
				);
		CoreTestUtils.testParseCUnit(	"module pack.modul;" +
				"import langio = std.stdio : writefln, foo = writef; "
				);
		CoreTestUtils.testParseCUnit(	"module pack.modul;" +
				"private static import langio = std.stdio : writefln, foo = writef; "
				);
	}
	
	@Test
	public void testAll() throws IOException, CoreException {
		CoreTestUtils.testParseCUnit(TestUtils.readTestDataFile("nodes/testNodes.d"));
	}
	
	@Test
	public void testAll2() throws IOException, CoreException {
		CoreTestUtils.testParseCUnit(TestUtils.readTestDataFile("nodes/conditionals.d"));
	}

}
