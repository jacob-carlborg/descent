package dtool.parser;


import static melnorme.miscutil.Assert.assertTrue;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.junit.After;
import org.junit.BeforeClass;

import dtool.DeeNamingRules_Test;
import dtool.ast.ASTChecker;
import dtool.ast.ASTNeoNode;
import dtool.ast.definitions.Module;
import dtool.descentadapter.DescentASTConverter;
import dtool.refmodel.ParserAdapter;
import dtool.tests.DToolBaseTest;
import dtool.tests.DToolTestResources;

public abstract class Parser__CommonTest extends DToolBaseTest {

	protected static final String TESTFILESDIR = "parser/";

	public static ASTNeoNode testDtoolASTConvertion(final String source) {
		
		descent.internal.compiler.parser.Module mod = ParserAdapter.parseSource(source).mod;
		assertTrue(mod.problems.size() == 0, "Found syntax errors while parsing.");
	
		Module neoModule = DescentASTConverter.convertModule(mod);
		ASTChecker.checkConsistency(neoModule);
		return neoModule;
	}
	
	public static ASTNeoNode testConversionFromFile(String filename) throws CoreException, IOException {
		return testDtoolASTConvertion(DToolTestResources.getInstance().readTestDataFile(TESTFILESDIR+filename));
	}
	
	public static void parseFolder(File folder, boolean recurseDirs) {
		FilenameFilter filter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return dir.isDirectory() || DeeNamingRules_Test.isValidCompilationUnitName(name);
			}
		};
		File[] children = folder.listFiles(filter);
		for (File file : children) {
			if(file.isDirectory() && recurseDirs) {
				parseFolder(file, recurseDirs);
			} else {
				parseFile(file);
			}
		}
	}
	
	public static void parseFile(File file) {
		assertTrue(file.isFile());
		String source = readStringFromFileUnchecked(file);
		testDtoolASTConvertion(source);
		System.out.println("parsed: " + file);
	}
	
	
	@BeforeClass
	public static void setUp() throws Exception {
		System.out.println("====================  ====================");
	}
	
	@After
	public void tearDown() throws Exception {
	}
	
}
