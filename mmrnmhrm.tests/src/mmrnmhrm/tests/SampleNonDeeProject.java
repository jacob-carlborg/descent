package mmrnmhrm.tests;


import java.io.IOException;
import java.net.URISyntaxException;

import melnorme.miscutil.ExceptionAdapter;
import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.model.CompilationUnit;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;

/**
 * Builds a simple project with d sources, but that is not a Dee Project
 */
public abstract class SampleNonDeeProject {


	public static final String SAMPLEPROJNAME = "SampleNonDeeProj";

	public static final String TEST_OUT_SRC = "OUTsrc";
	public static final String TEST_SRC3 = "src3";

	
	public static IProject project = null;
	
	public static IFile sampleOutOfModelFile;
	public static IFile sampleNonExistantFile;

	public static void createAndSetupProject() {
		try {
			createAndFillSampleProj();
		} catch (Exception e) {
			throw ExceptionAdapter.unchecked(e);
		}
	}


	public static IProject createAndFillSampleProj() throws CoreException,
			URISyntaxException, IOException {
		IWorkspaceRoot workspaceRoot = DeeCore.getWorkspaceRoot();
		project = workspaceRoot.getProject(SAMPLEPROJNAME);
		if(project.exists())
			project.delete(true, null);
		project.create(null);
		project.open(null);
		fillSampleProj();
		return project;
	}
	
	
	public static void fillSampleProj() throws CoreException, URISyntaxException, IOException {
		// Watch out when changing these values, tests may depend on these paths
		
		//IFolder folder;
		
		CoreTestUtils.createWorkspaceFolderFromBundle("sampleSrc1",
				project, TEST_OUT_SRC);
		
		
		//UITestUtils.runEventLoop(DeePlugin.getActiveWorkbenchShell());
	}

	public static void commonTearDown() throws Exception {
		IWorkspaceRoot workspaceRoot = DeeCore.getWorkspaceRoot();
		IProject project = workspaceRoot.getProject(SAMPLEPROJNAME);
		project.delete(true, null);
	}
	
	/** Gets a IFile from the sample project. */
	public static IFile getFile(String filepath) {
		IFile file = project.getProject().getFile(filepath);
		BasePluginTest.assertTrue(file.exists(), "Test file not found.");
		return file;
	}
	
	/** Gets a CompilationUnit from the sample project. 
	 * CompilationUnit must be on the build path. */
	public static CompilationUnit getCompilationUnit(String filepath) throws CoreException {
		return new CompilationUnit(getFile(filepath));
	}
	
}