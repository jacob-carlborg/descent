package mmrnmhrm.ui.wizards.projconfig;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import mmrnmhrm.core.model.DeeProject;
import mmrnmhrm.core.model.DeeSourceFolder;
import mmrnmhrm.tests.CommonProjectTestClass;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.junit.After;
import org.junit.Before;

public class ProjectConfigBlockTest extends CommonProjectTestClass {
	
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
	}


	public void doChangeSet1() throws CoreException {
		createRecursive(containerOutput, false);
		fProjectConfigBlock.fSourceFoldersPage.fOutputLocationField.setText(containerOutput.getProjectRelativePath().toOSString());

		fProjectConfigBlock.fSourceFoldersPage.addEntry(containerSrc1);
	}

	public void assertChangeSet1Applied() {
		assertTrue(deeProject.getOutputDir().getProjectRelativePath().equals(containerOutput.getProjectRelativePath()));
		assertTrue(deeProject.getOutputDir().equals(containerOutput));
		assertTrue(deeProject.getSourceFolders().contains(new DeeSourceFolder(containerSrc1, deeProject)));
	}
	
	public void assertChangeSet1NotApplied() {
		assertFalse(deeProject.getOutputDir().getProjectRelativePath().equals(containerOutput.getProjectRelativePath()));
		assertFalse(deeProject.getOutputDir().equals(containerOutput));
		assertFalse(deeProject.getSourceFolders().contains(new DeeSourceFolder(containerSrc1, deeProject)));
	}

}