package mmrnmhrm.ui.wizards;

import mmrnmhrm.core.model.DeeProject;
import mmrnmhrm.ui.wizards.projconfig.ProjectConfigBlock;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import util.Assert;

/**
 * DeeProjectWizardFirstPage
 */
public class DeeProjectWizardSecondPage extends WizardPage {

	public static final String PAGE_NAME = "DeeProjectWizardPage2";

	protected ProjectConfigBlock projectConfigBlock;


	protected DeeProjectWizardSecondPage() {
		super(PAGE_NAME);
		setTitle("Configure a D Project");
		setDescription("Configure a D project build path and compiler options.");

		projectConfigBlock = new ProjectConfigBlock();
	}
	
	public void createControl(Composite parent) {
		Control content = projectConfigBlock.createControl(parent);
		setControl(content);
	}
	
	public void init(DeeProject proj) {
		projectConfigBlock.init(proj); 
	}
	
	public DeeProjectWizard getWizard() {
		return (DeeProjectWizard) super.getWizard();
	}
	
}
