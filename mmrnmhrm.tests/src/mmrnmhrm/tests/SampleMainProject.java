package mmrnmhrm.tests;


import static melnorme.miscutil.Assert.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;

import melnorme.miscutil.ExceptionAdapter;
import mmrnmhrm.core.dltk.ParsingUtil;
import mmrnmhrm.core.model.CompilationUnit;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.core.IScriptProject;

import dtool.ast.definitions.Module;
import dtool.tests.DToolTests;

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
	
	static { DToolTests.loadTestProjects(); }

	public static IProject project;
	public static IScriptProject deeProj;

	public static IFile sampleFile1;
	public static IFile sampleOutOfModelFile;
	public static IFile sampleNonExistantFile;


	public static void createAndSetupSampleProj() {
		try {
			deeProj = CoreTestUtils.createAndOpenProject(SAMPLEPROJNAME);
			fillSampleProj();
		} catch (Exception e) {
			throw ExceptionAdapter.unchecked(e);
		}
	}

	public static void fillSampleProj() throws CoreException, URISyntaxException, IOException {
		// Watch out when changing these values, tests may depend on these paths
		
		project = deeProj.getProject();
		IFolder folder;
		
		sampleNonExistantFile = project.getFile(new Path("nonexistant.d"));

		folder = CoreTestUtils.createFolderInProject(project, 
				ITestDataConstants.SAMPLE_SRC1, TEST_SRC1, false);
		sampleFile1 = folder.getFile("bigfile.d");

		folder = CoreTestUtils.createFolderInProject(project, 
				"sampleSrcOut", TEST_OUTSRC, false);
		sampleOutOfModelFile = folder.getFile("outfile.d");
		
		folder = CoreTestUtils.createFolderInProject(project, 
				"refs", TEST_SRC_REFS, true);

		folder = CoreTestUtils.createFolderInProject(project, 
				ITestDataConstants.SAMPLE_SRC3, TEST_SRC3, true);

		folder = CoreTestUtils.createFolderInProject(project, 
				TEST_SRC_PHOBOSHD, TEST_SRC_PHOBOSHD, true);
		folder = CoreTestUtils.createFolderInProject(project, 
				TEST_SRC_PHOBOSIMPL, TEST_SRC_PHOBOSIMPL, true);
		folder = CoreTestUtils.createFolderInProject(project, 
				TEST_SRC_TANGO, TEST_SRC_TANGO, true);

		//UITestUtils.runEventLoop(DeePlugin.getActiveWorkbenchShell());
	}

	
	/** Gets a IFile from the sample project. */
	public static IFile getFile(String filepath) {
		IFile file = deeProj.getProject().getFile(filepath);
		assertTrue(file.exists(), "Test file not found.");
		return file;
	}
	
	public static Module getModule(String filepath) throws CoreException {
		CompilationUnit cunit = new CompilationUnit(getFile(filepath));
		return ParsingUtil.getNeoASTModule(cunit.modUnit);
	}
	
}