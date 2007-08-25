package mmrnmhrm.tests;


import junit.framework.Assert;

import org.eclipse.dltk.core.search.indexing.IndexManager;
import org.eclipse.dltk.internal.core.ModelManager;

/**
 * Common Plugin Test class. 
 * Statically loads some read only projects, and prepares the workbench,
 * in case it wasn't cleared.
 */
@SuppressWarnings("restriction")
public class BasePluginTest {
	
	static {
		IndexManager indexManager = ModelManager.getModelManager().getIndexManager();
		indexManager.disable();
		//indexManager.shutdown();
		//melnorme.miscutil.Assert.isTrue(indexManager.activated == false);
		SamplePreExistingProject.checkForExistanceOfPreExistingProject();
		SampleMainProject.createAndSetupSampleProj();
		SampleNonDeeProject.createAndSetupProject();
	}
	

	public static void assertTrue(boolean b) {
		assertTrue(b, "Assertion failed.");
	}
	
	public static void assertTrue(boolean b, String msg) {
		if(b == false) {
			b = false; // dummy statement to allow breakpoint placement
		}
		Assert.assertTrue(msg, b);
	}
	
}