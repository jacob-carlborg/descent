package mmrnmhrm.ui.preferences.pages;

import mmrnmhrm.core.model.DeeModel;
import mmrnmhrm.core.model.DeeProjectOptions;
import mmrnmhrm.ui.preferences.DeeProjectCompileOptionsBlock;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.PropertyPage;


public class DeeCompileOptionsPropertyPage extends PropertyPage {
	
	private DeeProjectCompileOptionsBlock fProjCfg;
	private DeeProjectOptions fDeeProject;

	public DeeCompileOptionsPropertyPage() {
		fProjCfg = new DeeProjectCompileOptionsBlock();
	}
	
	/*** {@inheritDoc} */
	protected Control createContents(Composite parent) {
		
		noDefaultAndApplyButton();		
		
		fDeeProject = getDeeProject();
		if (fDeeProject == null) {
			Label label = new Label(parent, SWT.NONE);
			label.setText("Target not a D project.");
			setVisible(false);
			return label;
		} else {
			fProjCfg.init(fDeeProject);
			return fProjCfg.createControl(parent);
		}
	}
	
	private DeeProjectOptions getDeeProject() {
		return DeeModel.getDeeProjectInfo(DLTKCore.create(getProject()));
	}
	
	private IProject getProject() {
		IAdaptable adaptable= getElement();
		if(adaptable instanceof IProject) {
			return (IProject) adaptable;
		}
		return (IProject) adaptable.getAdapter(IProject.class);
	}

	public boolean performOk() {
		if(fDeeProject == null) 
			return true;
		
		return fProjCfg.performOk();
	}
	
}