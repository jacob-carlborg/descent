package dtool.parser;

import static melnorme.miscutil.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import descent.internal.compiler.parser.Module;
import dtool.ast.ASTChecker;
import dtool.descentadapter.DescentASTConverter;
import dtool.refmodel.ParserAdapter;
import dtool.tests.DToolTestResources;

@RunWith(Parameterized.class)
public class MassParse_MiscBadSyntaxCasesTest extends Parser__CommonTest {
	
	@Parameters
	public static Collection<Object[]> filesToParse() throws IOException {
		File scanDir = new File(DToolTestResources.getInstance().getResourcesDir(), COMMON + "miscCasesInvalidSyntax");
		return getParseFileParameterList(scanDir);
	}
	
	protected final File file;
	
	public MassParse_MiscBadSyntaxCasesTest(File file) {
		this.file = file;
	}
	
	@Test
	public void testParseFile() throws IOException {
		parseFileWithSyntaxErrors();
	}

	private void parseFileWithSyntaxErrors() {
		String source = readStringFromFileUnchecked(file);
		Module mod = ParserAdapter.parseSource(source).mod;
		assertTrue(mod.problems.size() > 0, "Expected syntax errors while parsing.");
		
		// Convert AST with syntax errors
		dtool.ast.definitions.Module neoModule = DescentASTConverter.convertModule(mod);
		ASTChecker.checkConsistency(neoModule);
	}
	
}
