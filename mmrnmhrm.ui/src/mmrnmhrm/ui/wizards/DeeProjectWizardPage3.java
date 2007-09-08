/**
 * 
 */
package mmrnmhrm.ui.wizards;

import mmrnmhrm.core.model.DeeProjectOptions;
import mmrnmhrm.ui.preferences.DeeProjectCompileOptionsBlock;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.ui.wizards.ProjectWizardSecondPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;

import static melnorme.miscutil.Assert.assertFail;

public class DeeProjectWizardPage3 extends WizardPage {

	private static final String PAGE_NAME = "DeeProjectWizardPage3";
	protected ProjectWizardSecondPage fSecondPage;
	protected DeeProjectCompileOptionsBlock fProjCfg;

	public DeeProjectWizardPage3(ProjectWizardSecondPage secondPage) {
		super(PAGE_NAME);
		setPageComplete(false);
		setTitle("Setup");
		setDescription("");

		fSecondPage = secondPage;
		fProjCfg = new DeeProjectCompileOptionsBlock();
	}
	

	@Override
	public void createControl(Composite parent) {
		setControl(fProjCfg.createControl(parent));
	}
	
	public void setVisible(boolean visible) {
		if (visible) {
			fProjCfg.init2(DLTKCore.create(getProject()));
		} 
		super.setVisible(visible);
	}
	
	
	private IProject getProject() {
		return fSecondPage.getScriptProject().getProject();
	}
	
	
	public boolean performOk() {
		return fProjCfg.performOk();
	}

	public void performCancel() {
		IFile file = getProject().getFile(DeeProjectOptions.CFG_FILE_NAME);
		if(file.exists())
			assertFail();
	}


}