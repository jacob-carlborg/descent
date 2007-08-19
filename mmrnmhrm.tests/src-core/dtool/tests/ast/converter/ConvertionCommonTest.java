package dtool.tests.ast.converter;

import java.io.IOException;

import mmrnmhrm.tests.BasePluginTest;
import mmrnmhrm.tests.TestUtils;

import org.eclipse.core.runtime.CoreException;
import org.junit.After;
import org.junit.BeforeClass;

import descent.internal.compiler.parser.ast.ASTNode;
import dtool.descentadapter.DescentASTConverter;
import dtool.dom.ast.ASTChecker;
import dtool.dom.definitions.Module;
import dtool.refmodel.ParserAdapter;

public abstract class ConvertionCommonTest extends BasePluginTest {

	protected static final String TESTFILESDIR = "astparser/";

	public static ASTNode testDtoolASTConvertion(final String source) throws CoreException {
		
		descent.internal.compiler.parser.Module mod = ParserAdapter.parseSource(source).mod;
		BasePluginTest.assertTrue(mod.problems.size() == 0,
				"Found syntax errors while parsing.");
	
		Module neoModule = DescentASTConverter.convertModule(mod);
		ASTChecker.checkConsistency(neoModule);
		return neoModule;
	}
	
	public static void testConversionFromFile(String filename) throws CoreException, IOException {
		testDtoolASTConvertion(TestUtils.readTestDataFile(TESTFILESDIR+filename));
	}
	
	@BeforeClass
	public static void setUp() throws Exception {
		System.out.println("====================  ====================");
	}

	@After
	public void tearDown() throws Exception {
	}



}
