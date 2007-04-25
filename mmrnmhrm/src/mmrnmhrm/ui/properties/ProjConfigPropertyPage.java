package mmrnmhrm.ui.properties;

import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.model.DeeModelManager;
import mmrnmhrm.core.model.DeeProject;
import mmrnmhrm.ui.ExceptionHandler;
import mmrnmhrm.ui.wizards.projconfig.ProjectConfigBlock;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.PropertyPage;

public class ProjConfigPropertyPage extends PropertyPage {

	private ProjectConfigBlock fProjCfg;
	private DeeProject fDeeProject;

	public ProjConfigPropertyPage() {
		fProjCfg = new ProjectConfigBlock();
	}
	
	/*** {@inheritDoc} */
	protected Control createContents(Composite parent) {
		
		noDefaultAndApplyButton();		
		
		IProject project = getProject();
		if (project == null) {
			Label label = new Label(parent, SWT.NONE);
			label.setText("Target not an IProject. WTH");
			//setVisible(false);
			return label;
		} 
		
		fDeeProject = getDeeProject();
		if (fDeeProject == null) {
			Label label = new Label(parent, SWT.NONE);
			label.setText("Target not an D project.");
			//setVisible(false);
			return label;
		} else {
			fProjCfg.init(fDeeProject);
			return fProjCfg.createControl(parent);
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


	public boolean performOk() {
		if(fDeeProject == null) 
			return true;
		
		try {
			DeeCore.run(new IWorkspaceRunnable() {
				public void run(IProgressMonitor monitor) throws CoreException {
					// TODO: Status, err
					/*throw new LangModelException(new Status(IStatus.ERROR, JavaPlugin.getPluginId(), 
							IJavaStatusConstants.INTERNAL_ERROR, "Status Message", new NullPointerException()));
							*/
					fProjCfg.applyConfig();
					fDeeProject.saveProjectConfigFile();
				}
			}, null);
		} catch (CoreException e) {
			ExceptionHandler.handle(e, "D Project Config Error", "Error saving project settings.");
			return false;
		}
		return true;
	}

}