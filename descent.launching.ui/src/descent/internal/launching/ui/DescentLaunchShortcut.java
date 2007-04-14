package descent.internal.launching.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

import descent.launching.IDescentLaunchConfigurationConstants;
import descent.launching.ui.DescentLaunchingUI;

public class DescentLaunchShortcut implements ILaunchShortcut {

	public void launch(ISelection selection, String mode) {
		if (selection.isEmpty()) return;
		
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection ss = (IStructuredSelection) selection;
			Object element = ss.getFirstElement();
			if (element instanceof IFile) {
				IFile file = (IFile) element;
				launch(file, mode);
			} else if (element instanceof IAdaptable) {
				IAdaptable adaptable = (IAdaptable) element;
				IResource resource = (IResource) adaptable.getAdapter(IResource.class);
				if (resource != null && resource instanceof IFile) {
					IFile file = (IFile) element;
					launch(file, mode);
				}
			}
		}
	}

	public void launch(IEditorPart editor, String mode) {
		IEditorInput input = editor.getEditorInput();
		if (input instanceof IFileEditorInput) {
			IFile file = ((IFileEditorInput) input).getFile();
			launch(file, mode);
		}
	}

	private void launch(IFile file, String mode) {
		ILaunchConfiguration config = findLaunchConfiguration(file, getConfigurationType());
		if (config != null) {
			DebugUITools.launch(config, mode);
		}	
	}
	
	/**
	 * Locate a configuration to relaunch for the given file.  If one cannot be found, create one.
	 * 
	 * @return a re-useable config or <code>null</code> if none
	 */
	protected ILaunchConfiguration findLaunchConfiguration(IFile file, ILaunchConfigurationType configType) {
		File fileLocation = new File(file.getLocation().toOSString());
		
		List candidateConfigs = Collections.EMPTY_LIST;
		try {
			ILaunchConfiguration[] configs = DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurations(configType);
			candidateConfigs = new ArrayList(configs.length);
			for (int i = 0; i < configs.length; i++) {
				ILaunchConfiguration config = configs[i];
				if (new File(config.getAttribute(IDescentLaunchConfigurationConstants.ATTR_PROGRAM_NAME, "")).equals(fileLocation)) { //$NON-NLS-1$
					if (config.getAttribute(IDescentLaunchConfigurationConstants.ATTR_PROJECT_NAME, "").equals(file.getProject().getName())) { //$NON-NLS-1$
						candidateConfigs.add(config);
					}
				}
			}
		} catch (CoreException e) {
			DescentLaunchingUI.log(e);
		}
		
		// If there are no existing configs associated with the IFile, create one.
		// If there is exactly one config associated with the IFile, return it.
		// Otherwise, if there is more than one config associated with the IType, prompt the
		// user to choose one.
		int candidateCount = candidateConfigs.size();
		if (candidateCount < 1) {
			return createConfiguration(file);
		} else if (candidateCount == 1) {
			return (ILaunchConfiguration) candidateConfigs.get(0);
		} else {
			// Prompt the user to choose a config.  A null result means the user
			// cancelled the dialog, in which case this method returns null,
			// since cancelling the dialog should also cancel launching anything.
			ILaunchConfiguration config = chooseConfiguration(candidateConfigs);
			if (config != null) {
				return config;
			}
		}
		
		return null;
	}
	
	protected ILaunchConfiguration createConfiguration(IFile file) {
		ILaunchConfiguration config = null;
		ILaunchConfigurationWorkingCopy wc = null;
		try {
			ILaunchConfigurationType configType = getConfigurationType();
			wc = configType.newInstance(null, getLaunchManager().generateUniqueLaunchConfigurationNameFrom(file.getName()));
			wc.setAttribute(IDescentLaunchConfigurationConstants.ATTR_PROGRAM_NAME, file.getLocation().toOSString());
			wc.setAttribute(IDescentLaunchConfigurationConstants.ATTR_PROJECT_NAME, file.getProject().getName());
			wc.setMappedResources(new IResource[] {file.getProject()});
			config = wc.doSave();
		} catch (CoreException exception) {
			reportErorr(exception);		
		} 
		return config;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.debug.ui.launcher.JavaLaunchShortcut#getConfigurationType()
	 */
	protected ILaunchConfigurationType getConfigurationType() {
		return getLaunchManager().getLaunchConfigurationType(IDescentLaunchConfigurationConstants.ID_D_APPLICATION);		
	}
	
	protected ILaunchManager getLaunchManager() {
		return DebugPlugin.getDefault().getLaunchManager();
	}
	
	/**
	 * Show a selection dialog that allows the user to choose one of the specified
	 * launch configurations.  Return the chosen config, or <code>null</code> if the
	 * user cancelled the dialog.
	 */
	protected ILaunchConfiguration chooseConfiguration(List configList) {
		IDebugModelPresentation labelProvider = DebugUITools.newDebugModelPresentation();
		ElementListSelectionDialog dialog= new ElementListSelectionDialog(getShell(), labelProvider);
		dialog.setElements(configList.toArray());
		dialog.setTitle("Choose configuration");  
		dialog.setMessage("Choose a configuration from below:");
		dialog.setMultipleSelection(false);
		int result = dialog.open();
		labelProvider.dispose();
		if (result == Window.OK) {
			return (ILaunchConfiguration) dialog.getFirstResult();
		}
		return null;		
	}
	
	/**
	 * Convenience method to get the window that owns this action's Shell.
	 */
	protected Shell getShell() {
		return DescentLaunchingUI.getActiveWorkbenchShell();
	}
	
	/**
	 * Opens an error dialog on the given excpetion.
	 * 
	 * @param exception
	 */
	protected void reportErorr(CoreException exception) {
		MessageDialog.openError(getShell(), "Unexpected error", exception.getStatus().getMessage());  
	}

}
