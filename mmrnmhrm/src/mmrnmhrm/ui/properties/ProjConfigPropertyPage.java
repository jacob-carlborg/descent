package mmrnmhrm.ui.properties;

import mmrnmhrm.core.model.DeeModelManager;
import mmrnmhrm.core.model.DeeProject;
import mmrnmhrm.ui.wizards.projconfig.ProjectConfigBlock;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.PropertyPage;

public class ProjConfigPropertyPage extends PropertyPage {

	private ProjectConfigBlock fProjCfg;

	
	public ProjConfigPropertyPage() {
		fProjCfg = new ProjectConfigBlock();
	}
	
	/**
	 * @see PreferencePage#createContents(Composite)
	 */
	protected Control createContents(Composite parent) {
		
		noDefaultAndApplyButton();		
		
		IProject project = getProject();
		//project = DeeModel.getDeeProject("DeeProj").getProject();
		
		//Composite content = new RowComposite(parent);
		
		if (project == null) {
			Label label = new Label(parent, SWT.NONE);
			label.setText("Not an IProject");
			setVisible(false);
			return label;
		} 
		
		DeeProject deeProj = getDeeProject();

		if (deeProj == null) {
			Label label = new Label(parent, SWT.NONE);
			label.setText("Not an IProject");
			setVisible(false);
			return label;
		} else {
			fProjCfg.init(DeeModelManager.getLangProject("DeeProj"));
			return fProjCfg.createControl(parent);
			//return content;
		}
	}
	
	private DeeProject getDeeProject() {
		return DeeModelManager.getLangProject(getProject());
	}
	
	private IProject getProject() {
		IAdaptable adaptable= getElement();
		if(adaptable instanceof IProject) {
			return (IProject) adaptable;
		}
		return null;
	}


	protected void performDefaults() {
		// Populate the owner text field with the default value
	}
	
	public boolean performOk() {
		// store the value in the owner text field
		return true;
	}

}