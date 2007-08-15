package dtool.tests.ast.converter;

import mmrnmhrm.tests.BasePluginTest;
import mmrnmhrm.tests.SampleMainProject;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.junit.After;
import org.junit.Test;

/**
 */
public class Convertion_PhobosTest extends BasePluginTest {

	public static void setUp() throws Exception {
		System.out.println("====================  ====================");
	}

	@After
	public void tearDown() throws Exception {
	}

	
	@Test
	public void testPhobosHeaders() throws CoreException {
		IFolder folder = 
			SampleMainProject.project.getFolder(SampleMainProject.TEST_SRC_PHOBOSHD);
		SampleMainProject.deeProj.getSourceRoot(folder).updateElementRecursive();
	}
	
	@Test
	public void testPhobosInternal() throws CoreException {
		IFolder folder = 
			SampleMainProject.project.getFolder(SampleMainProject.TEST_SRC_PHOBOSIMPL);
		SampleMainProject.deeProj.getSourceRoot(folder).updateElementRecursive();
	}
	

}
