package mmrnmhrm.tests;


import java.io.IOException;
import java.net.URISyntaxException;

import melnorme.miscutil.ExceptionAdapter;
import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.model.CompilationUnit;
import mmrnmhrm.core.model.DeeModelManager;
import mmrnmhrm.core.model.DeeModelRoot;
import mmrnmhrm.core.model.DeeProject;
import mmrnmhrm.core.model.DeeSourceFolder;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;

/**
 * This classes creates a sample project 
 * in which tests can be based upon.
 */
public abstract class SampleProjectBuilder {


	public static final String SAMPLEPROJNAME = "SampleProj";

	public static final String TEST_SRC1 = "src1";
	public static final String TEST_SRC3 = "src3";
	public static final String TEST_SRC_REFS = "OUTrefs";
	public static final String TEST_OUT_SRC = "OUTsrc";
	
	public static DeeProject sampleDeeProj = null;
	public static IProject project;
	
	public static IFile sampleFile1;
	public static IFile sampleOutOfModelFile;
	public static IFile sampleNonExistantFile;


	public static void commonSetUp() throws Exception {
		createAndFillSampleProj();
	}

	public static void commonSetUpUnchecked() {
		try {
			createAndFillSampleProj();
		} catch (Exception e) {
			throw ExceptionAdapter.unchecked(e);
		}
	}


	public static DeeProject createAndFillSampleProj() throws CoreException,
			URISyntaxException, IOException {
		IWorkspaceRoot workspaceRoot = DeeCore.getWorkspaceRoot();
		IProject project = workspaceRoot.getProject(SAMPLEPROJNAME);
		project.create(null);
		project.open(null);
		DeeModelRoot.getInstance().createDeeProject(project);
		sampleDeeProj = DeeModelManager.getLangProject(SAMPLEPROJNAME);
		
		// Now fill some data
		fillSampleProj();
		return sampleDeeProj;
	}
	
	
	public static void fillSampleProj() throws CoreException, URISyntaxException, IOException {
		// Watch out when changing these values, tests may depend on these paths
		
		project = sampleDeeProj.getProject();
		IFolder folder;
		
		sampleNonExistantFile = project.getFile(new Path("nonexistant.d"));

		folder = CoreTestUtils.createWorkspaceFolderFromBundle("sampleSrc1",
				project, TEST_SRC1);
		sampleFile1 = folder.getFile("foo.d");
		

		folder = CoreTestUtils.createWorkspaceFolderFromBundle("sampleSrcOut",
				project, TEST_OUT_SRC);
		sampleOutOfModelFile = folder.getFile("outfile.d");
		
		folder = CoreTestUtils.createWorkspaceFolderFromBundle("refs",
				project, TEST_SRC_REFS);
		sampleDeeProj.addSourceRoot(new DeeSourceFolder(folder, sampleDeeProj));
		
		folder = CoreTestUtils.createWorkspaceFolderFromBundle("sampleSrc3",
				project, TEST_SRC3);
		sampleDeeProj.addSourceRoot(new DeeSourceFolder(folder, sampleDeeProj));

		
		//UITestUtils.runEventLoop(DeePlugin.getActiveWorkbenchShell());
	}

	public static void commonTearDown() throws Exception {
		IWorkspaceRoot workspaceRoot = DeeCore.getWorkspaceRoot();
		IProject project = workspaceRoot.getProject(SAMPLEPROJNAME);
		project.delete(true, null);
	}
	
	/** Gets a IFile from the sample project. */
	public static IFile getFile(String filepath) {
		IFile file = sampleDeeProj.getProject().getFile(filepath);
		BaseTest.assertTrue(file.exists(), "Test file not found.");
		return file;
	}
	
	/** Gets a CompilationUnit from the sample project. 
	 * CompilationUnit must be on the build path. */
	public static CompilationUnit getCompilationUnit(String filepath) {
		return DeeModelManager.getCompilationUnit(getFile(filepath));
	}
	
}