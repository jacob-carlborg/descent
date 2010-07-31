package dtool.parser;

import static melnorme.miscutil.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import melnorme.miscutil.ArrayUtil;
import melnorme.miscutil.Function;

import org.junit.Test;

import dtool.tests.DToolTestResources;
import dtool.tests.DToolTestUtils;

/**
 * Test conversion of common sources (Phobos, Tango)
 */
public abstract class Convertion_PhobosTest extends Parser__CommonTest {
	
	public static final String COMMON = "common/";
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
	
	/* ------------------------------------ */
	
	private final File file;
	
	public Convertion_PhobosTest(File file) {
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
