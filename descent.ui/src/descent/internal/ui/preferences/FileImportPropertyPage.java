package descent.internal.ui.preferences;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;

import descent.core.IJavaElement;
import descent.core.IJavaProject;
import descent.core.JavaCore;
import descent.internal.ui.JavaPlugin;
import descent.internal.ui.dialogs.StatusUtil;
import descent.internal.ui.wizards.IStatusChangeListener;

public class FileImportPropertyPage extends PropertyPage implements IStatusChangeListener
{
	public static final String PROP_ID = "descent.ui.propertyPages.FileImportPropertyPage"; //$NON-NLS-1$
	private static final String PAGE_SETTINGS = "FileImportPropertyPage"; //$NON-NLS-1$
	
	private FileImportBlock fFileImportBlock;
	
	protected Control createContents(Composite parent)
	{
		IProject project= getProject();
		
		Control result;
		if (project == null || !isDProject(project))
			result = createForNonDProject(parent);
		else if (!project.isOpen())
			result = createForClosedProject(parent);
		else
			result = createConfigBlock(parent, project);
		
		Dialog.applyDialogFont(result);
		return result;
	}
	
	private IDialogSettings getSettings()
	{
		IDialogSettings javaSettings = JavaPlugin.getDefault().getDialogSettings();
		IDialogSettings pageSettings = javaSettings.getSection(PAGE_SETTINGS);
		if (pageSettings == null) {
			pageSettings = javaSettings.addNewSection(PAGE_SETTINGS);
		}
		return pageSettings;
	}
	
	private Control createConfigBlock(Composite parent, IProject project)
	{
		fFileImportBlock = new FileImportBlock(this, project, 
				(IWorkbenchPreferenceContainer) getContainer());
		return fFileImportBlock.createContents(parent);
	}

	private Control createForNonDProject(Composite parent)
	{
		Label label= new Label(parent, SWT.LEFT);
		label.setText(PreferencesMessages.FileImportPreferencePage_no_java_project_message); 
		setValid(true);
		return label;
	}

	private Control createForClosedProject(Composite parent)
	{
		Label label= new Label(parent, SWT.LEFT);
		label.setText(PreferencesMessages.FileImportPreferencePage_closed_project_message); 
		setValid(true);
		return label;
	}
	
	private IProject getProject() {
		IAdaptable adaptable= getElement();
		if (adaptable != null) {
			IJavaElement elem= (IJavaElement) adaptable.getAdapter(IJavaElement.class);
			if (elem instanceof IJavaProject) {
				return ((IJavaProject) elem).getProject();
			}
		}
		return null;
	}
	
	private boolean isDProject(IProject proj)
	{
		try {
			return proj.hasNature(JavaCore.NATURE_ID);
		} catch (CoreException e) {
			JavaPlugin.log(e);
		}
		return false;
	}
	
	public void statusChanged(IStatus status)
	{
		setValid(!status.matches(IStatus.ERROR));
		StatusUtil.applyToStatusLine(this, status);
	}
}
