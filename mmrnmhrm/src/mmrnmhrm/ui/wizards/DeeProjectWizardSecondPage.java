package mmrnmhrm.ui.wizards;

import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.model.DeeModelManager;
import mmrnmhrm.ui.util.RowComposite;
import mmrnmhrm.ui.wizards.projconfig.ProjectConfigBlock;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * DeeProjectWizardFirstPage
 */
public class DeeProjectWizardSecondPage extends WizardPage {


	
	private ProjectConfigBlock projectConfigBlock;


	protected DeeProjectWizardSecondPage(String pageName) {
		super(pageName);
		setTitle("Configure a D Project");
		setDescription("Configure a D project build path and compiler options.");

	}
	
	public void createControl(Composite parent) {
		
		final Composite content= new RowComposite(parent);
		
		projectConfigBlock = new ProjectConfigBlock();
		IProject proj = DeeCore.getWorkspaceRoot().getProject("DeeProj");
		projectConfigBlock.init(DeeModelManager.getLangProject(proj));
		Control control = projectConfigBlock.createControl(content);
		control.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		
		Button button = new Button(content, SWT.RADIO);
		button.setText("BUTTON");

		Button button2 = new Button(content, SWT.RADIO);
		button2.setText("BUTTON2");

		setControl(content);		
	}


}
