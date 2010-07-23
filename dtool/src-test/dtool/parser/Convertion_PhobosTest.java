package dtool.parser;

import static melnorme.miscutil.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.junit.Test;

import dtool.tests.DToolTestResources;
import dtool.tests.DToolTestUtils;

/**
 * Test conversion of common sources (Phobos, Tango)
 */
public class Convertion_PhobosTest extends Parser__CommonTest {
	
	public static final String COMMON = "common/";
	private static final String COMMON_UNPACK = "_common-unpack/";

	public static final String TESTSRC_DRUNTIME_PHOBOS2 = "druntime_phobos-2.047-src";
	public static final String TESTSRC_PHOBOS1_OLD = "phobos1-old";
	public static final String TESTSRC_PHOBOS1_OLD__HEADER = TESTSRC_PHOBOS1_OLD + "phobos-header";
	public static final String TESTSRC_PHOBOS1_OLD__INTERNAL = TESTSRC_PHOBOS1_OLD + "phobos-internal";
	public static final String TESTSRC_TANGO = "tango-0.99";
	
	static {
		unzipSource(TESTSRC_DRUNTIME_PHOBOS2);
		unzipSource(TESTSRC_PHOBOS1_OLD);
		unzipSource(TESTSRC_TANGO);
	}

	private static void unzipSource(String zipName) {
		File zipFile = DToolTestResources.getTestFile(COMMON + zipName + ".zip");
		File outDir = new File(DToolTestResources.getInstance().getWorkingDir(), "_common-unpack/" + zipName);
		deleteDir(outDir);
		try {
			DToolTestUtils.unzipFile(zipFile, outDir);
		} catch (IOException e) {
			throw melnorme.miscutil.ExceptionAdapter.unchecked(e);
		}
	}
	
	public static File getCommonResource(String subPath) {
		return new File(DToolTestResources.getInstance().getWorkingDir(), COMMON_UNPACK + subPath);
	}
	
	private void parseFolder(String testDataPath) {
		File folder = getCommonResource(testDataPath);
		assertTrue(folder.exists() && folder.isDirectory());
		parseFolder(folder, true);
	}
	
	@Test
	public void testPhobosDRuntime() throws CoreException {
		parseFolder(TESTSRC_DRUNTIME_PHOBOS2);
	}
	
	@Test
	public void testPhobosHeaders() throws CoreException {
		parseFolder(TESTSRC_PHOBOS1_OLD);
	}
	
	@Test
	public void testTango() throws CoreException {
		parseFolder(TESTSRC_TANGO);
	}
	
}
