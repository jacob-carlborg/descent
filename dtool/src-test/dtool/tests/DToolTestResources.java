package dtool.tests;

import static melnorme.miscutil.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import melnorme.miscutil.FileUtil;


public class DToolTestResources {
	
	public static final String TESTDATA = "testdata/";
	public static final String D_TOOL_TEST_RESOURCES_BASE_DIR = "DToolTestResources.baseDir";
	public static final String D_TOOL_TEST_RESOURCES_WORKING_DIR = "DToolTestResources.workingDir";

	protected static DToolTestResources instance;
	
	private String testResourcesDir;
	private String workingDir;
	
	public DToolTestResources() {
		testResourcesDir = System.getProperty(D_TOOL_TEST_RESOURCES_BASE_DIR);
		if(testResourcesDir == null) {
			testResourcesDir = TESTDATA;
		}

		workingDir = System.getProperty(D_TOOL_TEST_RESOURCES_WORKING_DIR);
		
		System.out.println("====>> WORKING DIR: " + workingDir);
	}
	
	public synchronized static DToolTestResources getInstance() {
		if(instance == null) {
			instance = new DToolTestResources();
		}
		return instance;
	}
	
	
	public String readTestDataFile(String pathstr) throws IOException {
		File testDataDir = getResourcesDir();
		File file = new File(testDataDir, pathstr);
		return FileUtil.readStringFromFile(file, "UTF-8");
	}
	
	public File getResourcesDir() {
		File file = new File(testResourcesDir);
		assertTrue(file.exists() && file.isDirectory());
		return file;
	}
	
	public File getWorkingDir() {
		File file = new File(workingDir);
		assertTrue(file.exists() && file.isDirectory());
		return file;
	}

	
	public static File getTestFile(String fileRelPath) {
		return new File(DToolTestResources.getInstance().getResourcesDir(), fileRelPath);
	}
	
}
