package mmrnmhrm.tests;


import static melnorme.miscutil.Assert.assertTrue;
import melnorme.miscutil.ExceptionAdapter;
import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.launch.DeeDmdInstallType;

import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.core.internal.environment.LocalEnvironment;
import org.eclipse.dltk.core.search.indexing.IndexManager;
import org.eclipse.dltk.internal.core.ModelManager;
import org.eclipse.dltk.core.internal.environment.LazyFileHandle;
import org.eclipse.dltk.launching.IInterpreterInstallType;
import org.eclipse.dltk.launching.InterpreterStandin;
import org.eclipse.dltk.launching.ScriptRuntime;

/**
 * Common Plugin Test class. 
 * Statically loads some read only projects, and prepares the workbench,
 * in case it wasn't cleared.
 */
@SuppressWarnings("restriction")
public class BaseDeePluginTest extends BasePluginTest {
	
	//TODO: !! configure path properly
	private static final String DMD2_PATH = "D:/devel/D.tools/dmd-archive/dmd-2.019/dmd/bin/dmd.exe";
	public static final String DEFAULT_DMD2_INSTALL = "defaultTestDMD2";

	static {
		DToolResourcesPluginAdapter.initialize();
		
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
		
		setupTestDMDInstall();
		
		SamplePreExistingProject.checkForExistanceOfPreExistingProject();
		SampleNonDeeProject.createAndSetupNonDeeProject();
	}

	private static void setupTestDMDInstall() {
		IInterpreterInstallType deeDmdInstallType = ScriptRuntime.getInterpreterInstallType(DeeDmdInstallType.INSTALLTYPE_ID);
		InterpreterStandin install = new InterpreterStandin(deeDmdInstallType, "defaultTestDMD2-id");

		install.setName(DEFAULT_DMD2_INSTALL);
		install.setInstallLocation(new LazyFileHandle(LocalEnvironment.ENVIRONMENT_ID, new Path(DMD2_PATH)));
		
		
		install.setInterpreterArgs(null);
		install.setLibraryLocations(null);
		
		install.convertToRealInterpreter();
	}
	
}