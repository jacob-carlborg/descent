package mmrnmhrm.ui.wizards;

import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.model.ModelUtil;
import mmrnmhrm.tests.BaseUITest;
import mmrnmhrm.tests.SampleMainProject;
import mmrnmhrm.tests.adapters.TestAccessor_WizardDialog;
import mmrnmhrm.ui.DeePlugin;
import mmrnmrhm.org.eclipse.dltk.ui.wizards.TestAccessor;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static melnorme.miscutil.Assert.assertTrue;



public class DeeProjectWizardTest extends BaseUITest {

	private DeeProjectCreationWizard wizard;
	private TestAccessor_WizardDialog wizDialog;
	
	final static String NEWPROJNAME = "WizardCreationProject";
	
	@Before
	public void setUp() throws Exception {
		tearDown();
        //WorkbenchPlugin.getDefault().getNewWizardRegistry().findWizard(id);
		wizard = new DeeProjectCreationWizard();
		IWorkbenchWindow window = DeePlugin.getActiveWorkbenchWindow();
		wizard.init(window.getWorkbench(), null);
		
        Shell parent = DeePlugin.getActiveWorkbenchShell();
        wizDialog = new TestAccessor_WizardDialog(parent, wizard);
        wizDialog.setBlockOnOpen(false);
		wizDialog.open();
	}
	

	@After
	public void tearDown() throws Exception {
		// Should undo all wizard actions
		ResourcesPlugin.getWorkspace().run(new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				/*DeeProject deeproj = ModelUtil.getDeeProject(NEWPROJNAME);
				if(deeproj != null) {
					deeproj.getProject().delete(true, monitor);
				}*/
				IProject project = DeeCore.getWorkspaceRoot().getProject(NEWPROJNAME);
				if(project.exists())
					project.delete(true, monitor);
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
		//wizard.fFirstPage.fNameGroup.setName(SampleMainProject.SAMPLEPROJNAME);
		TestAccessor.TestAccessor_NameGroup._setName(wizard.fFirstPage, SampleMainProject.SAMPLEPROJNAME);
		assertTrue(!wizard.canFinish());

		simulatePressCancel();
		assertTrue(checkNoChanges());
	}

	@Test
	public void test_P1_Finish() throws Throwable {
		wizard.fFirstPage.getProjectName();
		TestAccessor.TestAccessor_NameGroup._setName(wizard.fFirstPage, NEWPROJNAME);
		assertTrue(wizard.canFinish());

		simulatePressFinish();
		assertTrue(checkProjectCreated());
	}

	
	/*@Test
	public void test_P1_P2_Finish() throws Throwable {
		TestAccessor._NameGroup_setName(wizard.fFirstPage, NEWPROJNAME);
		assertTrue(wizard.canFinish());
		
		simulateEnterPage2();
		
		ProjectConfigBlockTest auxtest = new ProjectConfigBlockTest();
		auxtest.init(wizard.fSecondPage.projectConfigBlock);
		auxtest.doChangeSet1();
		
		simulatePressFinish();
		assertTrue(checkProjectCreated());
		//auxtest.assertChangeSet1Applied();
	}*/

	
	
	@Test
	public void test_P1_P2_P1_Finish() throws Throwable {
		TestAccessor.TestAccessor_NameGroup._setName(wizard.fFirstPage, NEWPROJNAME);
		assertTrue(wizard.canFinish());
		simulateEnterPage2();
		
		simulatePage2GoBack();
		
		simulatePressFinish();
		assertTrue(checkProjectCreated());
	}


	/* ---- */
	
	@Test
	public void test_P1_Cancel() throws Throwable {
		TestAccessor.TestAccessor_NameGroup._setName(wizard.fFirstPage, NEWPROJNAME);
		assertTrue(wizard.canFinish());
		
		
		simulatePressCancel();
		assertTrue(checkNoChanges());
	}

	
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
		return ModelUtil.getDeeProject(NEWPROJNAME).dltkProj.exists() == false;
	}

	protected boolean checkProjectCreated() throws Throwable {
		if(exceptionThrown)
			throw exception;
		return ModelUtil.getDeeProject(NEWPROJNAME).dltkProj.exists();
	}
}
