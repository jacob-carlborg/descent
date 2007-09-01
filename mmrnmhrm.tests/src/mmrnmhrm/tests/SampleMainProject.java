package mmrnmhrm.tests;


import java.io.IOException;
import java.net.URISyntaxException;

import melnorme.miscutil.ExceptionAdapter;
import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.model.CompilationUnit;
import mmrnmhrm.core.model.DeeProject;
import mmrnmhrm.core.model.ModelUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.core.DLTKCore;

/**
 * This classes creates a sample project 
 * in which tests can be based upon.
 */
public abstract class SampleMainProject {


	public static final String SAMPLEPROJNAME = "SampleProj";

	public static final String TEST_SRC1 = "src1";
	public static final String TEST_SRC3 = "src3";
	public static final String TEST_SRC_REFS = "OUTrefs";
	public static final String TEST_OUTSRC = "OUTsrc";
	public static final String TEST_SRC_PHOBOSHD = "phobos-header";
	public static final String TEST_SRC_PHOBOSIMPL = "phobos-internal";
	public static final String TEST_SRC_TANGO = "tango";
	
	public static IProject project;
	public static DeeProject deeProj = null;
	
	public static IFile sampleFile1;
	public static IFile sampleOutOfModelFile;
	public static IFile sampleNonExistantFile;


	public static void createAndSetupSampleProj() {
		try {
			deeProj = createAndOpenDeeProject(SAMPLEPROJNAME);
			fillSampleProj();
		} catch (Exception e) {
			throw ExceptionAdapter.unchecked(e);
		}
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
		ModelUtil.createDeeProject(project);
		return new DeeProject(DLTKCore.create(project));
	}
	
	private static IFolder createFolderInProject(String bundleDir, String destDir, boolean addSrcFolder) throws CoreException,
			URISyntaxException, IOException {
		IFolder folder;
		folder = CoreTestUtils.createWorkspaceFolderFromBundle(bundleDir,
				project, destDir);
		if(addSrcFolder) {
			ModelUtil.createAddSourceFolder(deeProj.dltkProj, folder);
			deeProj.dltkProj.save(null, false);
		}
		return folder;
	}

	public static void fillSampleProj() throws CoreException, URISyntaxException, IOException {
		// Watch out when changing these values, tests may depend on these paths
		
		project = deeProj.getProject();
		IFolder folder;
		
		sampleNonExistantFile = project.getFile(new Path("nonexistant.d"));

		folder = createFolderInProject("sampleSrc1", TEST_SRC1, false);
		sampleFile1 = folder.getFile("foo.d");

		folder = createFolderInProject("sampleSrcOut", TEST_OUTSRC, false);
		sampleOutOfModelFile = folder.getFile("outfile.d");
		
		folder = createFolderInProject("refs", TEST_SRC_REFS, true);

		folder = createFolderInProject("sampleSrc3", TEST_SRC3, true);

		folder = createFolderInProject(TEST_SRC_PHOBOSHD, TEST_SRC_PHOBOSHD, true);
		folder = createFolderInProject(TEST_SRC_PHOBOSIMPL, TEST_SRC_PHOBOSIMPL, true);
		folder = createFolderInProject(TEST_SRC_TANGO, TEST_SRC_TANGO, true);

		//UITestUtils.runEventLoop(DeePlugin.getActiveWorkbenchShell());
	}

	
	/** Gets a IFile from the sample project. */
	public static IFile getFile(String filepath) {
		IFile file = deeProj.getProject().getFile(filepath);
		BasePluginTest.assertTrue(file.exists(), "Test file not found.");
		return file;
	}
	
	/** Gets a CompilationUnit from the sample project. 
	 * CompilationUnit must be on the build path. */
	public static CompilationUnit getCompilationUnit(String filepath) throws CoreException {
		return new CompilationUnit(getFile(filepath));
	}
	
}