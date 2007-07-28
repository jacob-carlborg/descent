package mmrnmhrm.tests.core.ref;

import java.io.IOException;

import melnorme.miscutil.ExceptionAdapter;
import mmrnmhrm.core.model.CompilationUnit;
import mmrnmhrm.tests.BasePluginTest;
import mmrnmhrm.tests.SampleMainProject;

import org.eclipse.core.runtime.CoreException;
import org.junit.AfterClass;
import org.junit.Test;

import dtool.tests.ref.FindDef_CommonTest;

public abstract class FindDef_CommonImportTest extends BasePluginTest {

	public static final String TESTALT_KEY = "/++/";
	public static final String TEST_SRCFOLDER = SampleMainProject.TEST_SRC3;

	protected static CompilationUnit defaultCUnit;

	protected static void staticTestInit(String testSrcFile) {
		FindDef_CommonTest.counter = -1;
		System.out.println("======== " + testSrcFile + " ========");
		try {
			defaultCUnit = getTestCompilationUnit(testSrcFile);
		} catch (CoreException ce) {
			ExceptionAdapter.unchecked(ce);
		}
	}
	
	protected static CompilationUnit getTestCompilationUnit(String path) throws CoreException {
		return SampleMainProject.getCompilationUnit(TEST_SRCFOLDER +"/"+ path);
	}

	protected int offset;
	protected int targetOffset;
	protected CompilationUnit targetCUnit;
	protected CompilationUnit cunit;

	public FindDef_CommonImportTest(int offset, int targetOffset, String targetFile)
			throws IOException, CoreException {
		this(null, offset, targetOffset, targetFile);
	}
	
	public FindDef_CommonImportTest(CompilationUnit newCUnit, int defOffset, int refOffset, String targetFile)
	throws IOException, CoreException {
		this.offset = defOffset;
		this.targetOffset = refOffset;
		if(newCUnit == null)
			cunit = defaultCUnit;
		else
			cunit = newCUnit;
		if(targetFile == null)
			targetCUnit = null;
		else
			targetCUnit = getTestCompilationUnit(targetFile);
	}

	@Test
	public void test() throws Exception {
		FindDef_CommonTest.assertFindReF(cunit, offset, targetCUnit,
				targetOffset);
	}

}