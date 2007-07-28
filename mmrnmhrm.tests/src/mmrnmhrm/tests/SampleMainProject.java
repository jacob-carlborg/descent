package mmrnmhrm.tests;


import java.io.IOException;
import java.net.URISyntaxException;

import melnorme.miscutil.ExceptionAdapter;
import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.model.CompilationUnit;
import mmrnmhrm.core.model.DeeModel;
import mmrnmhrm.core.model.DeeModelRoot;
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
 * This classes creates a sample project 
 * in which tests can be based upon.
 */
public abstract class SampleMainProject {


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


	public static void createAndSetupSampleProj() {
		try {
			createAndFillSampleProj();
		} catch (Exception e) {
			throw ExceptionAdapter.unchecked(e);
		}
	}

	public static LangElement createAndFillSampleProj() throws CoreException,
			URISyntaxException, IOException {
				
		sampleDeeProj = createAndOpenDeeProject(SAMPLEPROJNAME);
		fillSampleProj();
		return sampleDeeProj;
	}

	public static DeeProject createAndOpenDeeProject(String name)
			throws CoreException {
		IWorkspaceRoot workspaceRoot = DeeCore.getWorkspaceRoot();

		IProject project;
		project = workspaceRoot.getProject(name);
		if(project.exists())
			project.delete(true, null);
		project.create(null);
		project.open(null);
		DeeModelRoot.getInstance().createDeeProject(project);
		return DeeModel.getLangProject(name);
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

	public static void fillSampleProj() throws CoreException, URISyntaxException, IOException {
		// Watch out when changing these values, tests may depend on these paths
		
		project = sampleDeeProj.getProject();
		IFolder folder;
		
		sampleNonExistantFile = project.getFile(new Path("nonexistant.d"));

		folder = createFolderInProject("sampleSrc1", TEST_SRC1, false);
		sampleFile1 = folder.getFile("foo.d");
		

		folder = createFolderInProject("sampleSrcOut", TEST_OUT_SRC, false);
		sampleOutOfModelFile = folder.getFile("outfile.d");
		
		folder = createFolderInProject("refs", TEST_SRC_REFS, true);

		folder = createFolderInProject("sampleSrc3", TEST_SRC3, true);
		
		
		//UITestUtils.runEventLoop(DeePlugin.getActiveWorkbenchShell());
	}

	
	/** Gets a IFile from the sample project. */
	public static IFile getFile(String filepath) {
		IFile file = sampleDeeProj.getProject().getFile(filepath);
		BasePluginTest.assertTrue(file.exists(), "Test file not found.");
		return file;
	}
	
	/** Gets a CompilationUnit from the sample project. 
	 * CompilationUnit must be on the build path. */
	public static CompilationUnit getCompilationUnit(String filepath) throws CoreException {
		return DeeModel.findCompilationUnit(getFile(filepath));
	}
	
}