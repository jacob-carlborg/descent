package mmrnmhrm.tests;


import junit.framework.Assert;

/**
 * Common Plugin Test class. 
 * Statically loads some read only projects, and prepares the workbench,
 * in case it wasn't cleared.
 */
public class BasePluginTest {
	
	static {
		SamplePreExistingProject.checkForExistanceOfPreExistingProject();
		SampleMainProject.createAndSetupSampleProj();
		SampleNonDeeProject.createAndSetupProject();
	}
	

	public static void assertTrue(boolean b) {
		assertTrue(b, "Assertion failed.");
	}
	
	public static void assertTrue(boolean b, String msg) {
		if(b == false) {
			b = false; // dummy op for breakpoint
		}
		Assert.assertTrue(msg, b);
	}
	
}