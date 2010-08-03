package dtool.parser;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import dtool.tests.DToolTestResources;
import dtool.tests.DToolTestUtils;

/**
 * Test conversion of common sources (Phobos, Tango)
 */
public abstract class MassParse__CommonTest extends Parser__CommonTest {
	
	private static final String COMMON_UNPACK = "_common-unpack/";

	public static final String TESTSRC_DRUNTIME_PHOBOS2 = "druntime_phobos-2.047-src";
	public static final String TESTSRC_PHOBOS1_OLD = "phobos1-old";
	public static final String TESTSRC_PHOBOS1_OLD__HEADER = TESTSRC_PHOBOS1_OLD + "phobos-header";
	public static final String TESTSRC_PHOBOS1_OLD__INTERNAL = TESTSRC_PHOBOS1_OLD + "phobos-internal";
	public static final String TESTSRC_TANGO_0_99 = "tango-0.99";
	
	static {
		unzipSource(TESTSRC_DRUNTIME_PHOBOS2);
		unzipSource(TESTSRC_PHOBOS1_OLD);
		unzipSource(TESTSRC_TANGO_0_99);
	}
	
	private static void unzipSource(String zipName) {
		File zipFile = DToolTestResources.getTestFile(COMMON + zipName + ".zip");
		File outDir = new File(DToolTestResources.getInstance().getWorkingDir(), COMMON_UNPACK + zipName);
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
	
	/* ------------------------------------ */
	
	protected final File file;
	
	public MassParse__CommonTest(File file) {
		this.file = file;
	}
	
	@Test
	public void testParseFile() throws IOException {
		parseFile(file, failOnSyntaxErrors());
	}

	protected boolean failOnSyntaxErrors() {
		return true;
	}
	
}
