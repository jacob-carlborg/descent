package mmrnmhrm.tests;


import static melnorme.miscutil.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import melnorme.miscutil.ExceptionAdapter;
import melnorme.miscutil.MiscUtil;
import mmrnmhrm.core.dltk.DeeParserUtil;
import mmrnmhrm.core.model.CompilationUnit;
import mmrnmhrm.core.model.ModelUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.core.IScriptProject;

import dtool.ast.definitions.Module;
import dtool.parser.Convertion_PhobosTest;

/**
 * This classes creates a sample project 
 * in which tests can be based upon.
 */
public abstract class SampleMainProject extends CoreTestUtils {
	

	public static final String SAMPLEPROJNAME = "SampleProj";

	public static final String TEST_SRC1 = "src1";
	public static final String TEST_SRC3 = "src3";
	public static final String TEST_SRC_REFS = "OUTrefs";
	public static final String TEST_OUTSRC = "OUTsrc";
	
	static {
		MiscUtil.loadClass(BaseDeePluginTest.class);
		SampleMainProject.createAndSetupSampleProj();
	}

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
		
		folder = project.getFolder(TEST_SRC1);
		copyDeeCoreDirToWorkspace(ITestDataConstants.SAMPLE_SRC1, folder);
		sampleFile1 = folder.getFile("bigfile.d");
		
		folder = project.getFolder(TEST_OUTSRC);
		copyDeeCoreDirToWorkspace("sampleSrcOut", folder);
		sampleOutOfModelFile = folder.getFile("outfile.d");
		
		createSrcFolderInProject("refs", project.getFolder(TEST_SRC_REFS));
		
		createSrcFolderInProject(ITestDataConstants.SAMPLE_SRC3, project.getFolder(TEST_SRC3));
		
		
		copyDToolCommonResource(Convertion_PhobosTest.TESTSRC_PHOBOS1_OLD);
		ModelUtil.addSourceFolder(project.getFolder(Convertion_PhobosTest.TESTSRC_PHOBOS1_OLD__HEADER), null);
		ModelUtil.addSourceFolder(project.getFolder(Convertion_PhobosTest.TESTSRC_PHOBOS1_OLD__INTERNAL), null);
		
		copyDToolCommonResource(Convertion_PhobosTest.TESTSRC_TANGO);
		ModelUtil.addSourceFolder(project.getFolder(Convertion_PhobosTest.TESTSRC_TANGO), null);
	}
	
	private static void copyDToolCommonResource(String resourcePath) throws CoreException {
		File testFile = Convertion_PhobosTest.getCommonResource(resourcePath);
		copyURLResourceToWorkspace(testFile.toURI(), project.getFolder(resourcePath));
	}
	
	
	/** Gets a IFile from the sample project. */
	public static IFile getFile(String filepath) {
		IFile file = deeProj.getProject().getFile(filepath);
		assertTrue(file.exists(), "Test file not found.");
		return file;
	}
	
	public static Module getModule(String filepath) throws CoreException {
		CompilationUnit cunit = new CompilationUnit(getFile(filepath));
		return DeeParserUtil.getNeoASTModule(cunit.modUnit);
	}
	
}