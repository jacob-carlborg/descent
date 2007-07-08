package mmrnmhrm.tests;


import java.io.ByteArrayInputStream;
import java.io.InputStream;

import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.model.DeeModelManager;
import mmrnmhrm.core.model.DeeModelRoot;
import mmrnmhrm.core.model.DeeProject;
import mmrnmhrm.core.model.DeeSourceFolder;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * The CommonProjectTestClass creates a sample project (once for all tests)
 * in which tests can be based upon.
 */
public abstract class CommonProjectTestClass extends BaseUITestClass {

	protected static final String SAMPLEPROJNAME = "ExistingProj";
	protected static DeeProject sampleDeeProj = null;
	
	protected static IFile sampleFile1;
	protected static IFile sampleOutFile;

	@BeforeClass
	public static void commonSetUp() throws Exception {
		IWorkspaceRoot workspaceRoot = DeeCore.getWorkspaceRoot();
		IProject project = workspaceRoot.getProject(SAMPLEPROJNAME);
		project.create(null);
		project.open(null);
		DeeModelRoot.getInstance().createDeeProject(project);
		sampleDeeProj = DeeModelManager.getLangProject(SAMPLEPROJNAME);
		
		// Now fill some data
		fillSampleProj();
	}
	
	static String SAMPLEFILE_CONTENTS = "module pack.foo" +
	"int a;" +
	"" +
	"class Foo {}" +
	"void func() {" +
	"  int b = a;" +
	"" +
	"}";
	
	static String OUTFILE_CONTENTS = SAMPLEFILE_CONTENTS; 

	public static void fillSampleProj() throws CoreException {
		InputStream is;
		DeeSourceFolder deeSrcFolder = sampleDeeProj.getSourceFolders()[0];
		sampleFile1 = deeSrcFolder.getUnderlyingResource().getFile("foo.d");
		is = new ByteArrayInputStream(SAMPLEFILE_CONTENTS.getBytes());
		TestUtils.createRecursive(sampleFile1, is, false);

		sampleOutFile = sampleDeeProj.getProject().getFolder("NOTSRC").getFile("out.d");
		is = new ByteArrayInputStream(OUTFILE_CONTENTS.getBytes());
		TestUtils.createRecursive(sampleOutFile, is, false);
	}
	
	@AfterClass
	public static void commonTearDown() throws Exception {
		IWorkspaceRoot workspaceRoot = DeeCore.getWorkspaceRoot();
		IProject project = workspaceRoot.getProject(SAMPLEPROJNAME);
		project.delete(true, null);
	}
	
	protected IWorkspaceRoot getWorkspaceRoot() {
		return ResourcesPlugin.getWorkspace().getRoot();
	}
	

	



}