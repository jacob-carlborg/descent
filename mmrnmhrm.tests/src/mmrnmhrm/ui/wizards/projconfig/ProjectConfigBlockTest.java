package mmrnmhrm.ui.wizards.projconfig;


import static org.junit.Assert.assertFalse;
import melnorme.miscutil.ArrayUtil;
import mmrnmhrm.core.model.DeeProject;
import mmrnmhrm.core.model.DeeSourceFolder;
import mmrnmhrm.tests.BaseUITest;
import mmrnmhrm.tests.TestUtils;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.junit.After;
import org.junit.Before;


public class ProjectConfigBlockTest extends BaseUITest {
	
	protected ProjectConfigBlock fProjectConfigBlock;
	protected DeeProject deeProject;
	protected IProject project;

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	public void init(ProjectConfigBlock fProjectConfigBlock) throws CoreException {
		this.fProjectConfigBlock = fProjectConfigBlock;
		this.deeProject = fProjectConfigBlock.fDeeProject;
		this.project = deeProject.getProject();
		
		initChangeSet1();
		assertChangeSet1NotApplied();
	}
	
	IFolder containerOutput; 
	IFolder containerSrc1; 


	public void initChangeSet1() throws CoreException {
		containerOutput = project.getFolder(new Path("bin2/out"));
		containerSrc1 = project.getFolder(new Path("mysrc"));
		containerSrc1.create(false, true, null);
	}


	public void doChangeSet1() throws CoreException {
		TestUtils.createRecursive(containerOutput, false);
		fProjectConfigBlock.fSourceFoldersPage.fOutputLocationField.setText(containerOutput.getProjectRelativePath().toOSString());

		fProjectConfigBlock.fSourceFoldersPage.addEntry(containerSrc1);
	}

	public void assertChangeSet1Applied() throws CoreException {
		assertTrue(deeProject.getOutputDir().getProjectRelativePath().equals(containerOutput.getProjectRelativePath()));
		assertTrue(deeProject.getOutputDir().equals(containerOutput));
		
		DeeSourceFolder obj = new DeeSourceFolder(containerSrc1, deeProject);
		assertTrue(ArrayUtil.contains(deeProject.getSourceFolders(), obj));
		assertTrue(deeProject.getSourceRoot(obj.getUnderlyingResource()) != null);
	}
	
	public void assertChangeSet1NotApplied() throws CoreException {
		assertFalse(deeProject.getOutputDir().getProjectRelativePath().equals(containerOutput.getProjectRelativePath()));
		assertFalse(deeProject.getOutputDir().equals(containerOutput));

		DeeSourceFolder obj = new DeeSourceFolder(containerSrc1, deeProject);
		assertFalse(ArrayUtil.contains(deeProject.getSourceFolders(), obj));
		assertFalse(deeProject.getSourceRoot(obj.getUnderlyingResource()) != null);
	}

}
