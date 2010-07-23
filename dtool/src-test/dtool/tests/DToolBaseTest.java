package dtool.tests;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import melnorme.miscutil.FileUtil;

public class DToolBaseTest extends DToolTestUtils {
	
	private static final String DEFAULT_TESTDATA_ENCODING = "UTF-8";

	public static String readStringFromFile(File file) throws IOException, FileNotFoundException {
		return FileUtil.readStringFromFile(file, DEFAULT_TESTDATA_ENCODING);
	}
	
	public static String readStringFromFileUnchecked(File file) {
		try {
			return FileUtil.readStringFromFile(file, DEFAULT_TESTDATA_ENCODING);
		} catch (IOException e) {
			throw melnorme.miscutil.ExceptionAdapter.unchecked(e);
		}
	}
	
}
