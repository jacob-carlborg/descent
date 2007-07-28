package mmrnmhrm.tests;


import mmrnmhrm.ui.DeePlugin;

import org.eclipse.ui.IWorkbenchPage;

import junit.framework.Assert;

/**
 * Common Plugin Test class. 
 * Statically loads some read only projects, and prepares the workbench,
 * in case it wasn't cleared.
 */
public class BasePluginTest {
	
	static {
		SamplePreExistingProject.checkForExistanteOfPreExistingProject();
		SampleMainProject.createAndSetupSampleProj();
		SampleNonDeeProject.createAndSetupProject();
	}
	

	protected static void assertTrue(boolean b) {
		Assert.assertTrue("Assertion failed.", b);
	}
	
	protected static void assertTrue(boolean b, String msg) {
		Assert.assertTrue(msg, b);
	}
	
	protected static void assertTrueP(boolean b, String msg) {
		if(b == false)
			System.out.println(msg);
		Assert.assertTrue(msg, b);
	}
}