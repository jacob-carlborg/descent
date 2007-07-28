package mmrnmhrm.tests;


import java.io.IOException;
import java.net.URISyntaxException;

import melnorme.miscutil.ExceptionAdapter;
import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.model.CompilationUnit;
import mmrnmhrm.core.model.DeeModel;
import mmrnmhrm.core.model.DeeProject;
import mmrnmhrm.core.model.DeeSourceFolder;
import mmrnmhrm.core.model.lang.LangElement;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;

/**
 * This classes creates a sample project that should exist *before*
 * DeeCore is loaded, with the intent of detecting some startup bugs
 * (this requires that the plugin unit test workspace is not cleared 
 *  on startup) 
 */
public abstract class SamplePreExistingProject {


	public static final String PREEXISTINGPROJNAME = "ExistingProj";

	public static final String TEST_SRC1 = "src1";

	private static final boolean REQUIRE_PREEXISTING_PROJ = false;
	
	public static DeeProject sampleDeeProj = null;
	public static IProject project;
	

	public static LangElement checkForExistanteOfPreExistingProject() {
		IWorkspaceRoot workspaceRoot = DeeCore.getWorkspaceRoot();
		IProject project = workspaceRoot.getProject(PREEXISTINGPROJNAME);

		if(!project.exists()) {
			// If the preexisting project doesn't exist, create it
			try {
				sampleDeeProj = SampleMainProject.createAndOpenDeeProject(PREEXISTINGPROJNAME);
				fillPreExistingSampleProj();
			} catch (Exception e) {
				ExceptionAdapter.unchecked(e);
			}
			// And throw up, to force restarting the unit tests
			if(REQUIRE_PREEXISTING_PROJ)
			throw new RuntimeException("The pre-existing project was not found,"
					+ "and was now created. Please restart the plugin unit tests"
					+ "and make the workspace is not cleared.");
		}
				
		return null;
	}

	private static void fillPreExistingSampleProj() throws CoreException, URISyntaxException, IOException {
		project = sampleDeeProj.getProject();
		//IFolder folder;
		
		createFolderInProject("sampleSrc1", TEST_SRC1, true);
	}

	private static IFolder createFolderInProject(String bundleDir, String destDir, boolean addSrcFolder) throws CoreException,
			URISyntaxException, IOException {
		IFolder folder;
		folder = CoreTestUtils.createWorkspaceFolderFromBundle(bundleDir,
				project, destDir);
		if(addSrcFolder)
			sampleDeeProj.addSourceRoot(new DeeSourceFolder(folder, sampleDeeProj));
		return folder;
	}
	
}