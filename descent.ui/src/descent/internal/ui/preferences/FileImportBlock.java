package descent.internal.ui.preferences;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;

import descent.internal.ui.preferences.OptionsConfigurationBlock.Key;
import descent.internal.ui.wizards.IStatusChangeListener;

public class FileImportBlock extends OptionsConfigurationBlock
{
	public static Key[] KEYS = new Key[]
	{
		// TODO ...and then the ogre took the keys...
	};
	
	public FileImportBlock(IStatusChangeListener context,
			IProject project,
			IWorkbenchPreferenceContainer container)
	{
		super(context, project, KEYS, container);
	}

	@Override
	protected Control createContents(Composite parent)
	{
		return new Composite(parent, SWT.NONE);
	}

	@Override
	protected String[] getFullBuildDialogStrings(boolean workspaceSettings)
	{
		String title= PreferencesMessages.FileImportConfigurationBlock_needsbuild_title; 
		String message;
		if (fProject == null) {
			message= PreferencesMessages.FileImportConfigurationBlock_needsfullbuild_message; 
		} else {
			message= PreferencesMessages.FileImportConfigurationBlock_needsprojectbuild_message; 
		}	
		return new String[] { title, message };
	}

	@Override
	protected void validateSettings(Key changedKey, String oldValue,
			String newValue)
	{
		// TODO Auto-generated method stub
	}
	
}
