package dtool.parser;


import static melnorme.miscutil.Assert.assertTrue;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;

import melnorme.miscutil.VoidFunction;

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
import dtool.tests.DToolTestUtils;

public abstract class Parser__CommonTest extends DToolBaseTest {

	protected static final String TESTFILESDIR = "parser/";

	public static ASTNeoNode testDtoolParse(final String source) {
		return testDtoolParse(source, true);
	}
	
	private static ASTNeoNode testDtoolParse(final String source, boolean failOnSyntaxErrors) {
		descent.internal.compiler.parser.Module mod = ParserAdapter.parseSource(source).mod;
		if(failOnSyntaxErrors) {
			assertTrue(mod.problems.size() == 0, "Found syntax errors while parsing.");
		}
		
		Module neoModule = DescentASTConverter.convertModule(mod);
		ASTChecker.checkConsistency(neoModule);
		return neoModule;
	}
	
	public static ASTNeoNode testConversionFromFile(String filename) throws CoreException, IOException {
		return testDtoolParse(DToolTestResources.getInstance().readTestDataFile(TESTFILESDIR+filename));
	}
	
	protected static ArrayList<File> getDeeModuleList(File folder, boolean recurseDirs) throws IOException {
		final ArrayList<File> fileList = new ArrayList<File>();
		
		VoidFunction<File> fileParser = new VoidFunction<File>() {
			@Override
			public Void evaluate(File file) {
				if(file.isFile()) {
					fileList.add(file);
				}
				return null;
			}
		};
		
		FilenameFilter filter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return dir.isDirectory() || DeeNamingRules_Test.isValidCompilationUnitName(name);
			}
		};
		DToolTestUtils.traverseFiles(folder, recurseDirs, fileParser, filter);
		return fileList;
	}
	
	protected static void parseFile(File file, boolean failOnSyntaxErrors) {
		assertTrue(file.isFile());
		String source = readStringFromFileUnchecked(file);
		testDtoolParse(source, failOnSyntaxErrors);
		System.out.println("parsed: " + file);
	}
	
	public static void parseFolder(File folder, boolean recurseDirs) throws IOException {
		final ArrayList<File> fileList = getDeeModuleList(folder, recurseDirs);
		for(File file : fileList) {
			parseFile(file, true);
		}
	}
	
	
	@BeforeClass
	public static void setUp() throws Exception {
		System.out.println("====================  ====================");
	}
	
	@After
	public void tearDown() throws Exception {
	}
	
}
