package dtool.tests.ast.converter;

import java.io.IOException;

import mmrnmhrm.tests.BaseTestClass;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
/**
 * Module convertion tests
 * http://www.digitalmars.com/d/module.html
 */
public class ModuleConvertionTest extends BaseTestClass {

	@Before
	public void setUp() throws Exception {
		System.out.println("====================  ====================");
	}

	@After
	public void tearDown() throws Exception {
	}

	
	@Test
	public void testFoo() {
		testCUparsing(
				"module foo;" +
				"import pack.bar;" +
				"public import std.stdio;");
	}
	
	@Test
	public void testRenamed() {
		// RENAMED IMPORT, static import
		testCUparsing(
				"module pack.modul;" +
				"import dee = std.stdio, lang = lang.string;"
				);
		testCUparsing(
				"module pack.modul;" +
				"private static import dee_io = std.stdio;"
				);
	}

	@Test
	public void testSelective() {
		// SELECTIVE IMPORT, static import
		testCUparsing(
				"module pack.modul;" +
				"import std.stdio : writefln, foo = writef;"
				);
		testCUparsing(	"module pack.modul;" +
				"import langio = std.stdio : writefln, foo = writef; "
				);
		testCUparsing(	"module pack.modul;" +
				"private static import langio = std.stdio : writefln, foo = writef; "
				);
	}
	
	@Test
	public void testAll() throws IOException {
		testCUparsing(readStringFromResource("testall.d"));
	}

}
