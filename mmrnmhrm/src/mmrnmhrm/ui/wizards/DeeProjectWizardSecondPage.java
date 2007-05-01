package mmrnmhrm.ui.wizards;

import mmrnmhrm.core.model.DeeProject;
import mmrnmhrm.ui.wizards.projconfig.ProjectConfigBlock;
import mmrnmhrm.util.ui.RowComposite;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * DeeProjectWizardFirstPage
 */
public class DeeProjectWizardSecondPage extends WizardPage {

	public static final String PAGE_NAME = "DeeProjectWizardPage2";

	private ProjectConfigBlock projectConfigBlock;


	protected DeeProjectWizardSecondPage() {
		super(PAGE_NAME);
		setTitle("Configure a D Project");
		setDescription("Configure a D project build path and compiler options.");
	}
	
	public void createControl(Composite parent) {
		
		final Composite content= new RowComposite(parent);
		
// FIXME: create a mock Project? 
// Or allow null project?
		init(null);
		Control control = projectConfigBlock.createControl(content);
		control.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		setControl(content);		
	}
	
	public void init(DeeProject proj) {
		projectConfigBlock.init(proj); 
	}

	@Override
	public IWizardPage getPreviousPage() {
		init(null);
		//deleteProjecT(getWizard().get)
		// TODO Auto-generated method stub
		// remove proj
		return super.getPreviousPage();
	}

}
