package dtool.parser;


import static melnorme.miscutil.Assert.assertTrue;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import melnorme.miscutil.ArrayUtil;
import melnorme.miscutil.Function;
import melnorme.miscutil.VoidFunction;

import org.junit.After;
import org.junit.BeforeClass;

import descent.internal.compiler.parser.ast.NaiveASTFlattener;
import dtool.DeeNamingRules_Test;
import dtool.ast.ASTChecker;
import dtool.ast.ASTNeoNode;
import dtool.ast.definitions.Module;
import dtool.descentadapter.DescentASTConverter;
import dtool.refmodel.ParserAdapter;
import dtool.tests.DToolBaseTest;
import dtool.tests.DToolTestUtils;

public abstract class Parser__CommonTest extends DToolBaseTest {

	public static final String COMMON = "common/";
	

	public static ASTNeoNode testDtoolParse(final String source) {
		return testDtoolParse(source, true);
	}
	
	private static ASTNeoNode testDtoolParse(final String source, boolean failOnSyntaxErrors) {
		descent.internal.compiler.parser.Module mod = ParserAdapter.parseSource(source).mod;
		if(failOnSyntaxErrors) {
			assertTrue(mod.problems.size() == 0, "Found syntax errors while parsing.");
		}
		NaiveASTFlattener naiveASTFlattener = new NaiveASTFlattener();
		mod.accept(naiveASTFlattener); // Test NaiveASTFlattener
		
		Module neoModule = DescentASTConverter.convertModule(mod);
		ASTChecker.checkConsistency(neoModule);
		return neoModule;
	}
	
	protected static ArrayList<File> getDeeModuleList(File folder, boolean recurseDirs) throws IOException {
		return getDeeModuleList(folder, recurseDirs, false);
	}
	protected static ArrayList<File> getDeeModuleList(File folder, boolean recurseDirs, final boolean validCUsOnly)
			throws IOException {
		
		final boolean addInAnyFileName = !validCUsOnly;
		final ArrayList<File> fileList = new ArrayList<File>();
		
		VoidFunction<File> fileVisitor = new VoidFunction<File>() {
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
			public boolean accept(File parent, String childName) {
				System.out.println("dir:" + parent +" " + childName);
				File childFile = new File(parent, childName);
				if(childFile.isDirectory()) {
					// exclude team private folder, like .svn, and other crap
					return !childName.startsWith(".");
				} else {
					return addInAnyFileName || DeeNamingRules_Test.isValidCompilationUnitName(childName);
				}
			}
		};
		DToolTestUtils.traverseFiles(folder, recurseDirs, fileVisitor, filter);
		return fileList;
	}
	
	public static Collection<Object[]> getParseFileParameterList(File folder) throws IOException {
		assertTrue(folder.exists() && folder.isDirectory());
		ArrayList<File> deeModuleList = getDeeModuleList(folder, true);
		
		Function<Object, Object[]> arrayWrap = new Function<Object, Object[]>() {
			@Override
			public Object[] evaluate(Object obj) {
				return new Object[] { obj };
			};
		};
		
		return Arrays.asList(ArrayUtil.map(deeModuleList, arrayWrap, Object[].class));
	}
	
	protected static void parseFile(File file, boolean failOnSyntaxErrors) {
		assertTrue(file.isFile());
		String source = readStringFromFileUnchecked(file);
		System.out.println("parsing: " + file);
		testDtoolParse(source, failOnSyntaxErrors);
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
