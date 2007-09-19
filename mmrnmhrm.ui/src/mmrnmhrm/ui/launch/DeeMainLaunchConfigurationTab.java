package mmrnmhrm.ui.launch;

import melnorme.miscutil.ExceptionAdapter;
import mmrnmhrm.core.dltk.DeeLanguageToolkit;
import mmrnmhrm.core.launch.DeeLaunchConfigurationDelegate;
import mmrnmhrm.core.model.DeeModel;
import mmrnmhrm.core.model.DeeNature;
import mmrnmhrm.core.model.DeeProjectOptions;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.dltk.core.DLTKLanguageManager;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.debug.ui.launchConfigurations.MainLaunchConfigurationTab;
import org.eclipse.dltk.ui.DLTKPluginImages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

public class DeeMainLaunchConfigurationTab extends MainLaunchConfigurationTab {

	@Override
	protected boolean validateProject(IScriptProject project) {
		if (project == null)
			return false;
		// check project nature		
		try {
			IDLTKLanguageToolkit ltk = DLTKLanguageManager.getLanguageToolkit(project);
			if (ltk instanceof DeeLanguageToolkit)
				return true;
		} catch (CoreException e) {
		}
		return false;
	}
	@Override
	protected String getLanguageName () {
		return "DEE";
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.AbstractLaunchConfigurationTab#getImage()
	 */
	@Override
	public Image getImage() {
		return DLTKPluginImages.get(DLTKPluginImages.IMG_OBJS_CLASS);
	}
	
	@Override
	protected String getNatureID() {
		return DeeNature.NATURE_ID;
	}
	
	@Override
	public void createControl(Composite parent) {
		// TODO Auto-generated method stub
		super.createControl(parent);
	}
	
	@Override
	protected void createMainModuleEditor(Composite parent, String text) {
		//super.createMainModuleEditor(parent, text);
		Font font = parent.getFont();
		Group mainGroup = new Group(parent, SWT.NONE);
		mainGroup.setText(text);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		mainGroup.setLayoutData(gd);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		mainGroup.setLayout(layout);
		mainGroup.setFont(font);
		fMainText = new Text(mainGroup, SWT.SINGLE | SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		fMainText.setLayoutData(gd);
		fMainText.setFont(font);
		fMainText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				updateLaunchConfigurationDialog();
			}
		});
		/*fSearchButton = createPushButton(mainGroup, DLTKLaunchConfigurationsMessages.mainTab_searchButton, null);
		fSearchButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {}

			public void widgetSelected(SelectionEvent e) {
				handleSearchButtonSelected();
			}
		});*/

		fMainText.setEnabled(false);
	}
	
	@Override
	public void initializeFrom(ILaunchConfiguration config) {
		super.initializeFrom(config);
		IScriptProject deeProj;
		try {
			deeProj = DeeLaunchConfigurationDelegate.getScriptProject(config);
		} catch (CoreException e) {
			// TO DO: check the exception
			throw ExceptionAdapter.unchecked(e);
		}
		DeeProjectOptions deeProjectInfo = DeeModel.getDeeProjectInfo(deeProj);
		fMainText.setText(deeProjectInfo.getArtifactRelPath());

	}

}
