package mmrnmhrm.tests;


import melnorme.miscutil.ExceptionAdapter;
import mmrnmhrm.core.DeeCore;

import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.search.indexing.IndexManager;
import org.eclipse.dltk.internal.core.ModelManager;

import static melnorme.miscutil.Assert.assertTrue;

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
		
		IWorkspaceDescription desc = DeeCore.getWorkspace().getDescription();
		desc.setAutoBuilding(false);
		try {
			DeeCore.getWorkspace().setDescription(desc);
		} catch (CoreException e) {
			throw ExceptionAdapter.unchecked(e);
		}
		assertTrue(DeeCore.getWorkspace().isAutoBuilding() == false);
		
		SamplePreExistingProject.checkForExistanceOfPreExistingProject();
		SampleMainProject.createAndSetupSampleProj();
		SampleNonDeeProject.createAndSetupProject();
	}
	
}