package mmrnmhrm.ui.wizards;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import mmrnmhrm.core.model.DeeModelManager;
import mmrnmhrm.tests.CommonProjectTestClass;
import mmrnmhrm.ui.DeePlugin;
import mmrnmhrm.ui.wizards.projconfig.ProjectConfigBlockTest;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;



public class DeeProjectWizardTest extends CommonProjectTestClass {

	private DeeProjectWizard wizard;
	private WizardDialog wizDialog;
	
	final static String NEWPROJNAME = "TestProject";
	
	@Before
	public void setUp() throws Exception {
		
        //WorkbenchPlugin.getDefault().getNewWizardRegistry().findWizard(id);
		wizard = new DeeProjectWizard();
		IWorkbenchWindow window = DeePlugin.getActiveWorkbenchWindow();
		wizard.init(window.getWorkbench(), null);
		
        Shell parent = DeePlugin.getActiveWorkbenchShell();
        wizDialog = new WizardDialog(parent, wizard);
        wizDialog.setBlockOnOpen(false);
		wizDialog.open();

		exceptionThrown = false;
		exception = null;
	}
	

	@After
	public void tearDown() throws Exception {
		wizard.performCancel();
		// Should undo all wizard actions
		ResourcesPlugin.getWorkspace().run(new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				if(getWorkspaceRoot().getProject(NEWPROJNAME).exists());
					getWorkspaceRoot().getProject(NEWPROJNAME).delete(true, monitor);
			}
		}, null);
	}


	private void simulateEnterPage2() {
		wizard.performPage2Entry();
		wizDialog.showPage(wizard.fSecondPage);
		//flushUI();
	}

	private void simulatePage2GoBack() {
		wizard.performPage2GoBack();
		wizDialog.showPage(wizard.fSecondPage);
	}
	
	private void simulatePressCancel() {
		wizard.performCancel();
	}

	private void simulatePressFinish() {
		wizard.performFinish();
	}

	
	@Test
	public void test_P1Validation() throws Throwable {
		wizard.fFirstPage.fNameGroup.setName(EXISTINGPROJNAME);

		assertFalse(wizard.canFinish());
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
