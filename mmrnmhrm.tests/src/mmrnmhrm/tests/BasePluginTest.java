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
import org.eclipse.dltk.internal.launching.LazyFileHandle;
import org.eclipse.dltk.launching.IInterpreterInstallType;
import org.eclipse.dltk.launching.InterpreterStandin;
import org.eclipse.dltk.launching.ScriptRuntime;

/**
 * Common Plugin Test class. 
 * Statically loads some read only projects, and prepares the workbench,
 * in case it wasn't cleared.
 */
@SuppressWarnings("restriction")
public class BasePluginTest {
	
	public static final String DEFAULT_DMD2_INSTALL = "defaultTestDMD2";

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
		
		setupTestDMDInstall();
		
		SamplePreExistingProject.checkForExistanceOfPreExistingProject();
		SampleMainProject.createAndSetupSampleProj();
		SampleNonDeeProject.createAndSetupNonDeeProject();
	}

	private static void setupTestDMDInstall() {
		IInterpreterInstallType deeDmdInstallType = ScriptRuntime
				.getInterpreterInstallType(DeeDmdInstallType.INSTALLTYPE_ID);
		InterpreterStandin install = new InterpreterStandin(
				deeDmdInstallType, "defaultTestDMD2-id");

		install.setName(DEFAULT_DMD2_INSTALL);
		//TODO: configure path properly
		install.setInstallLocation(new LazyFileHandle(LocalEnvironment.ENVIRONMENT_ID,
				new Path("D:/devel/D.tools/dmd2.0/dmd/bin/dmd.exe")));
		
		install.setInterpreterArgs(null);
		install.setLibraryLocations(null);
		
		install.convertToRealInterpreter();
	}
	
}