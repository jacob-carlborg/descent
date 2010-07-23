package dtool.tests;

import static melnorme.miscutil.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import melnorme.miscutil.FileUtil;


public class TestResources {
	
	protected static TestResources instance = new TestResources();
	
	public static TestResources getInstance() {
		return instance;
	}
	
	
	public String readTestDataFile(String pathstr) throws IOException {
		File testDataDir = getBaseResourcesDir();
		File file = new File(testDataDir, pathstr);
		return FileUtil.readStringFromFile(file, "UTF-8");
	}
	
	public File getBaseResourcesDir() {
		File file = new File("" + "testdata/");
		assertTrue(file.exists() && file.isDirectory());
		return file;
	}
	
}
