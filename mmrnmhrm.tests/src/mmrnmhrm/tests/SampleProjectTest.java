package mmrnmhrm.tests;


import java.io.IOException;
import java.net.URISyntaxException;

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
import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * The CommonProjectTestClass creates a sample project (once for all tests)
 * in which tests can be based upon.
 */
public abstract class SampleProjectTest extends BaseUITest {

	protected static final String SAMPLEPROJNAME = "SampleProj";
	public static final String TEST_SRC_FOLDER = "srctest";
	
	protected static DeeProject sampleDeeProj = null;
	
	protected static IFile sampleFile1;
	protected static IFile sampleOutOfModelFile;
	protected static IFile sampleNonExistantFile;

	@BeforeClass
	public static void commonSetUp() throws Exception {
		createAndFillSampleProj();
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
		
		IProject project = sampleDeeProj.getProject();
		IFolder folder;
		
		sampleNonExistantFile = project.getFile(new Path("nonexistant.d"));

		folder = CoreTestUtils.createWorkspaceFolderFromBundle("sampleSrc1",
				project, "src1");
		sampleFile1 = folder.getFile("foo.d");
		

		folder = CoreTestUtils.createWorkspaceFolderFromBundle("sampleSrcOut",
				project, "OUTSRC");
		sampleOutOfModelFile = folder.getFile("outfile.d");
		
		folder = CoreTestUtils.createWorkspaceFolderFromBundle("sampleSrc3",
				project, TEST_SRC_FOLDER);

		sampleDeeProj.addSourceRoot(new DeeSourceFolder(folder, sampleDeeProj));

		
		//UITestUtils.runEventLoop(DeePlugin.getActiveWorkbenchShell());
	}

	@AfterClass
	public static void commonTearDown() throws Exception {
		IWorkspaceRoot workspaceRoot = DeeCore.getWorkspaceRoot();
		IProject project = workspaceRoot.getProject(SAMPLEPROJNAME);
		project.delete(true, null);
	}


	protected static CompilationUnit getCompilationUnit(String filepath) {
		IFile file = sampleDeeProj.getProject().getFile(TEST_SRC_FOLDER +"/"+ filepath);
		return DeeModelManager.getCompilationUnit(file);
	}
	
}