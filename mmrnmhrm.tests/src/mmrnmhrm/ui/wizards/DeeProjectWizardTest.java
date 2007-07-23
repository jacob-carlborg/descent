package mmrnmhrm.ui.wizards;

import static org.junit.Assert.assertFalse;
import mmrnmhrm.core.model.DeeModelManager;
import mmrnmhrm.core.model.DeeModelRoot;
import mmrnmhrm.core.model.DeeProject;
import mmrnmhrm.tests.BaseUITest;
import mmrnmhrm.tests.SampleProjectBuilder;
import mmrnmhrm.tests.adapters.Test_WizardDialog;
import mmrnmhrm.ui.DeePlugin;
import mmrnmhrm.ui.wizards.projconfig.ProjectConfigBlockTest;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;



public class DeeProjectWizardTest extends BaseUITest {

	private DeeProjectWizard wizard;
	private Test_WizardDialog wizDialog;
	
	final static String NEWPROJNAME = "TestProject";
	
	@Before
	public void setUp() throws Exception {
		
        //WorkbenchPlugin.getDefault().getNewWizardRegistry().findWizard(id);
		wizard = new DeeProjectWizard();
		IWorkbenchWindow window = DeePlugin.getActiveWorkbenchWindow();
		wizard.init(window.getWorkbench(), null);
		
        Shell parent = DeePlugin.getActiveWorkbenchShell();
        wizDialog = new Test_WizardDialog(parent, wizard);
        wizDialog.setBlockOnOpen(false);
		wizDialog.open();
	}
	

	@After
	public void tearDown() throws Exception {
		// Should undo all wizard actions
		ResourcesPlugin.getWorkspace().run(new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				DeeProject deeproj = DeeModelManager.getLangProject(NEWPROJNAME);
				if(deeproj != null) {
					DeeModelRoot.getInstance().removeDeeProject(deeproj);
					deeproj.getProject().delete(true, monitor);
				}
			}
		}, null);
	}


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

	
	@Test
	public void test_P1Validation() throws Throwable {
		wizard.fFirstPage.fNameGroup.setName(SampleProjectBuilder.SAMPLEPROJNAME);
		assertFalse(wizard.canFinish());

		simulatePressCancel();
		assertTrue(checkNoChanges());
	}
	
	@Test
	public void test_P1_Finish() throws Throwable {
		wizard.fFirstPage.fNameGroup.setName(NEWPROJNAME);
		assertTrue(wizard.canFinish());

		simulatePressFinish();
		assertTrue(checkProjectCreated());
	}

	
	@Test
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
	}


	
	
	@Test
	public void test_P1_P2_P1_Creation() throws Throwable {
		wizard.fFirstPage.fNameGroup.setName(NEWPROJNAME);
		assertTrue(wizard.canFinish());
		simulateEnterPage2();
		
		simulatePage2GoBack();
		
		simulatePressFinish();
		assertTrue(checkProjectCreated());
	}


	/* ---- */
	
	@Test
	public void test_P1_Cancel() throws Throwable {
		wizard.fFirstPage.fNameGroup.setName(NEWPROJNAME);
		assertTrue(wizard.canFinish());
		
		
		simulatePressCancel();
		assertTrue(checkNoChanges());
	}

	
	@Test
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
	}
	
	
	@Test
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
	}
	
	protected boolean checkNoChanges() throws Throwable {
		if(exceptionThrown)
			throw exception;
		return DeeModelManager.getLangProject(NEWPROJNAME) == null;
	}

	protected boolean checkProjectCreated() throws Throwable {
		if(exceptionThrown)
			throw exception;
		return DeeModelManager.getLangProject(NEWPROJNAME) != null;
	}
}
