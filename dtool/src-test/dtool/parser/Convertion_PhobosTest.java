package dtool.parser;

import static melnorme.miscutil.Assert.assertTrue;

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.junit.Test;

import dtool.tests.TestResources;

/**
 * Test conversion of common sources (Phobos, Tango)
 */
public class Convertion_PhobosTest extends Parser__CommonTest {
	
	public static final String TEST_SRC_PHOBOSHD = "phobos-header";
	public static final String TEST_SRC_PHOBOSIMPL = "phobos-internal";
	public static final String TEST_SRC_TANGO = "tango";
	
	
	private void parseFolder(String testDataPath) {
		File folder = new File(TestResources.getInstance().getBaseResourcesDir(), testDataPath);
		assertTrue(folder.exists() && folder.isDirectory());
		parseFolder(folder, true);
	}
	
	@Test
	public void testPhobosHeaders() throws CoreException {
		parseFolder(TEST_SRC_PHOBOSHD);
	}
	
	@Test
	public void testPhobosInternal() throws CoreException {
		parseFolder(TEST_SRC_PHOBOSIMPL);
	}
	
	@Test
	public void testTango() throws CoreException {
		parseFolder(TEST_SRC_TANGO);
	}
	
}
