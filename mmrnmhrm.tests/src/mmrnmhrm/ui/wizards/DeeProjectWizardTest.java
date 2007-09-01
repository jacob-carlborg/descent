package mmrnmhrm.ui.wizards;

import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.model.DeeProject;
import mmrnmhrm.core.model.ModelUtil;
import mmrnmhrm.tests.BaseUITest;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.junit.After;
import org.junit.Before;



public class DeeProjectWizardTest extends BaseUITest {

	//private DeeProjectCreationWizard wizard;
	//private TestAdapter_WizardDialog wizDialog;
	
	final static String NEWPROJNAME = "TestProject";
	
	@Before
	public void setUp() throws Exception {
		tearDown();
        //WorkbenchPlugin.getDefault().getNewWizardRegistry().findWizard(id);
		/*wizard = new DeeProjectCreationWizard();
		IWorkbenchWindow window = DeePlugin.getActiveWorkbenchWindow();
		wizard.init(window.getWorkbench(), null);
		
        Shell parent = DeePlugin.getActiveWorkbenchShell();
        wizDialog = new TestAdapter_WizardDialog(parent, wizard);
        wizDialog.setBlockOnOpen(false);
		wizDialog.open();*/
	}
	

	@After
	public void tearDown() throws Exception {
		// Should undo all wizard actions
		ResourcesPlugin.getWorkspace().run(new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				DeeProject deeproj = ModelUtil.getDeeProject(NEWPROJNAME);
				if(deeproj != null) {
//					DeeModelRoot.getInstance().removeDeeProject(deeproj);
//					deeproj.getProject().delete(true, monitor);
				}
				IProject project = DeeCore.getWorkspaceRoot().getProject(NEWPROJNAME);
				if(project.exists())
					project.delete(true, monitor);
			}
		}, null);
	}

/*
	private void simulateEnterPage2() {
		wizDialog.nextPressed();
	}

	private void simulatePage2GoBack() {
		wizDialog.backPressed();
	}
	
	private void simulatePressCancel() {
		wizDialog.cancelPressed();
	}

	private void simulatePressFinish() {
		wizDialog.finishPressed();
	}
*/
	
	/*@Test
	public void test_P1Validation() throws Throwable {
		wizard.fFirstPage.fNameGroup.setName(SampleMainProject.SAMPLEPROJNAME);
		assertFalse(wizard.canFinish());

		simulatePressCancel();
		assertTrue(checkNoChanges());
	}*/
	
	/*@Test
	public void test_P1_Finish() throws Throwable {
		wizard.fFirstPage.fNameGroup.setName(NEWPROJNAME);
		assertTrue(wizard.canFinish());

		simulatePressFinish();
		assertTrue(checkProjectCreated());
	}*/

	
	/*@Test
	public void test_P1_P2_Finish() throws Throwable {
		wizard.fFirstPage.fNameGroup.setName(NEWPROJNAME);
		assertTrue(wizard.canFinish());
		
		simulateEnterPage2();
		
		ProjectConfigBlockTest auxtest = new ProjectConfigBlockTest();
		auxtest.init(wizard.fSecondPage.projectConfigBlock);
		auxtest.doChangeSet1();
		
		simulatePressFinish();
		assertTrue(checkProjectCreated());
		auxtest.assertChangeSet1Applied();
	}*/


	
	
	/*@Test
	public void test_P1_P2_P1_Creation() throws Throwable {
		wizard.fFirstPage.fNameGroup.setName(NEWPROJNAME);
		assertTrue(wizard.canFinish());
		simulateEnterPage2();
		
		simulatePage2GoBack();
		
		simulatePressFinish();
		assertTrue(checkProjectCreated());
	}*/


	/* ---- */
	
	/*@Test
	public void test_P1_Cancel() throws Throwable {
		wizard.fFirstPage.fNameGroup.setName(NEWPROJNAME);
		assertTrue(wizard.canFinish());
		
		
		simulatePressCancel();
		assertTrue(checkNoChanges());
	}*/

	
	/*@Test
	public void test_P1_P2_Cancel() throws Throwable {
		wizard.fFirstPage.fNameGroup.setName(NEWPROJNAME);
		assertTrue(wizard.canFinish());
		
		simulateEnterPage2();
		ProjectConfigBlockTest auxtest = new ProjectConfigBlockTest();
		auxtest.init(wizard.fSecondPage.projectConfigBlock);
		auxtest.doChangeSet1();

		simulatePressCancel();
		assertTrue(checkNoChanges());
		auxtest.assertChangeSet1NotApplied();
	}*/
	
	
	/*@Test
	public void test_P1_P2_P1_Cancel() throws Throwable {
		wizard.fFirstPage.fNameGroup.setName(NEWPROJNAME);
		assertTrue(wizard.canFinish());
		
		simulateEnterPage2();
		ProjectConfigBlockTest auxtest = new ProjectConfigBlockTest();
		auxtest.init(wizard.fSecondPage.projectConfigBlock);
		auxtest.doChangeSet1();
		
		simulatePage2GoBack();
		
		simulatePressCancel();
		assertTrue(checkNoChanges());
		auxtest.assertChangeSet1NotApplied();
	}*/
	
	protected boolean checkNoChanges() throws Throwable {
		if(exceptionThrown)
			throw exception;
		return ModelUtil.getDeeProject(NEWPROJNAME) == null;
	}

	protected boolean checkProjectCreated() throws Throwable {
		if(exceptionThrown)
			throw exception;
		return ModelUtil.getDeeProject(NEWPROJNAME) != null;
	}
}
