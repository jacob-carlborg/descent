package descent.internal.launching.ui;

import java.io.File;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.debug.ui.StringVariableSelectionDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;

import descent.core.IJavaProject;
import descent.launching.DescentLaunching;
import descent.launching.IDescentLaunchConfigurationConstants;
import descent.launching.ui.DescentLaunchingUI;

/**
 * A control for setting the working directory associated with a launch
 * configuration.
 */
public class WorkingDirectoryBlock extends AbstractLaunchConfigurationTab {
			
	// Local directory
	private Button fWorkspaceButton;
	private Button fFileSystemButton;
	private Button fVariablesButton;
	
	//bug 29565 fix
	private Button fUseDefaultDirButton = null;
	private Button fUseOtherDirButton = null;
	private Text fOtherWorkingText = null;
	private Text fWorkingDirText;
	
	/**
	 * The last launch config this tab was initialized from
	 */
	private ILaunchConfiguration fLaunchConfiguration;
	
	/**
	 * A listener to update for text changes and widget selection
	 */
	private class WidgetListener extends SelectionAdapter implements ModifyListener {
		public void modifyText(ModifyEvent e) {
			updateLaunchConfigurationDialog();
		}//end modifyText
		public void widgetSelected(SelectionEvent e) {
			Object source= e.getSource();
			if (source == fWorkspaceButton) {
				handleWorkspaceDirBrowseButtonSelected();
			}//end if
			else if (source == fFileSystemButton) {
				handleWorkingDirBrowseButtonSelected();
			}//end if 
			else if (source == fVariablesButton) {
				handleWorkingDirVariablesButtonSelected();
			}//end if 
			else if(source == fUseDefaultDirButton) {
				//only perform the action if this is the button that was selected
				if(fUseDefaultDirButton.getSelection()) {
					setDefaultWorkingDir();
				}//end if
			}//end if 
			else if(source == fUseOtherDirButton) {
				//only perform the action if this is the button that was selected
				if(fUseOtherDirButton.getSelection()) {
					handleUseOtherWorkingDirButtonSelected();
				}//end if
			}//end if
		}//end widgetSelected
	}//end WidgetListener class
	
	private WidgetListener fListener = new WidgetListener();
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		Font font = parent.getFont();
				
		Group group = new Group(parent, SWT.NONE);
		GridLayout workingDirLayout = new GridLayout();
		workingDirLayout.numColumns = 2;
		workingDirLayout.makeColumnsEqualWidth = false;
		group.setLayout(workingDirLayout);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		group.setLayoutData(gd);
		group.setFont(font);
		setControl(group);
		
		group.setText("Working directory:"); 
		
		fUseDefaultDirButton = new Button(group, SWT.RADIO);
		fUseDefaultDirButton.setText("Defa&ult:");
		fUseDefaultDirButton.setFont(font);
		fUseDefaultDirButton.addSelectionListener(fListener);
		fWorkingDirText = new Text(group, SWT.SINGLE | SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		fWorkingDirText.setLayoutData(gd);
		fWorkingDirText.setFont(font);
		fWorkingDirText.addModifyListener(fListener);
		fWorkingDirText.setEnabled(false);
		
		fUseOtherDirButton = new Button(group, SWT.RADIO);
		fUseOtherDirButton.setText("Ot&her:");
		fUseOtherDirButton.setFont(font);
		fUseOtherDirButton.addSelectionListener(fListener);
		fOtherWorkingText = new Text(group, SWT.SINGLE | SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		fOtherWorkingText.setLayoutData(gd);
		fOtherWorkingText.setFont(font);
		fOtherWorkingText.addModifyListener(fListener);
		
		Composite buttonComp = new Composite(group, SWT.NONE);
		GridLayout layout = new GridLayout(3, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		buttonComp.setLayout(layout);
		gd = new GridData(GridData.HORIZONTAL_ALIGN_END);
		gd.horizontalSpan = 2;
		buttonComp.setLayoutData(gd);
		buttonComp.setFont(font);		
		fWorkspaceButton = createPushButton(buttonComp, "W&orkspace...", null); 
		fWorkspaceButton.addSelectionListener(fListener);
		
		fFileSystemButton = createPushButton(buttonComp, "File S&ystem...", null); 
		fFileSystemButton.addSelectionListener(fListener);
		
		fVariablesButton = createPushButton(buttonComp, "Variabl&es...", null); 
		fVariablesButton.addSelectionListener(fListener);
	}
					
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#dispose()
	 */
	public void dispose() {}
		
	/**
	 * Show a dialog that lets the user select a working directory
	 */
	private void handleWorkingDirBrowseButtonSelected() {
		DirectoryDialog dialog = new DirectoryDialog(getShell());
		dialog.setMessage("Select a working directory for the launch configuration:"); 
		String currentWorkingDir = getWorkingDirectoryText();
		if (!currentWorkingDir.trim().equals("")) { //$NON-NLS-1$
			File path = new File(currentWorkingDir);
			if (path.exists()) {
				dialog.setFilterPath(currentWorkingDir);
			}//end if		
		}//end if
		String selectedDirectory = dialog.open();
		if (selectedDirectory != null) {
			fOtherWorkingText.setText(selectedDirectory);
		}//end if		
	}//end handleQWrokingDirBrowseBUttonSelected

	/**
	 * Show a dialog that lets the user select a working directory from 
	 * the workspace
	 */
	private void handleWorkspaceDirBrowseButtonSelected() {
	    IContainer currentContainer= getContainer();
		if (currentContainer == null) {
		    currentContainer = ResourcesPlugin.getWorkspace().getRoot();
		}//end if 
		ContainerSelectionDialog dialog = new ContainerSelectionDialog(getShell(), currentContainer, false,	"Select a &workspace relative working directory:"); 
		dialog.showClosedProjects(false);
		dialog.open();
		Object[] results = dialog.getResult();		
		if ((results != null) && (results.length > 0) && (results[0] instanceof IPath)) {
			IPath path = (IPath)results[0];
			String containerName = path.makeRelative().toString();
			setOtherWorkingDirectoryText("${workspace_loc:" + containerName + "}"); //$NON-NLS-1$ //$NON-NLS-2$
		}//end if			
	}//end handleWorkspaceDirBrowseButtonSelected
	
	/**
	 * Returns the selected workspace container,or <code>null</code>
	 */
	protected IContainer getContainer() {
		String path = getWorkingDirectoryText();
		if (path.length() > 0) {
		    IResource res = null;
		    IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		    if (path.startsWith("${workspace_loc:")) { //$NON-NLS-1$
		        IStringVariableManager manager = VariablesPlugin.getDefault().getStringVariableManager();
			    try {
                    path = manager.performStringSubstitution(path, false);
                    IContainer[] containers = root.findContainersForLocation(new Path(path));
                    if (containers.length > 0) {
                        res = containers[0];
                    }//end if
                }//end try 
			    catch (CoreException e) {}
			}//end if 
		    else {	    
				res = root.findMember(path);
			}//end else
			if (res instanceof IContainer) {
				return (IContainer)res;
			}//end if
		}//end if
		return null;
	}//end getContainer
		
	/**
	 * The default working dir radio button has been selected.
	 */
	private void handleUseDefaultWorkingDirButtonSelected() {
		fWorkspaceButton.setEnabled(false);
		fOtherWorkingText.setEnabled(false);
		fVariablesButton.setEnabled(false);
		fFileSystemButton.setEnabled(false);
		fUseOtherDirButton.setSelection(false);
	}

	/**
	 * The other working dir radio button has been selected
	 * 
	 * @since 3.2
	 */
	private void handleUseOtherWorkingDirButtonSelected() {
		fOtherWorkingText.setEnabled(true);
		fWorkspaceButton.setEnabled(true);
		fVariablesButton.setEnabled(true);
		fFileSystemButton.setEnabled(true);
		updateLaunchConfigurationDialog();
	}

	/**
	 * The working dir variables button has been selected
	 */
	private void handleWorkingDirVariablesButtonSelected() {
		StringVariableSelectionDialog dialog = new StringVariableSelectionDialog(getShell());
		dialog.open();
		String variableText = dialog.getVariableExpression();
		if (variableText != null) {
			fOtherWorkingText.insert(variableText);
		}//end if
	}//end handleWorkingDirVariablesButtonSelected
	
	/**
	 * Sets the default working directory
	 */
	protected void setDefaultWorkingDir() {
		try {
			ILaunchConfiguration config = getLaunchConfiguration();
			if (config != null) {
				IJavaProject javaProject = DescentLaunching.getJavaProject(config);
				if (javaProject != null) {
					setDefaultWorkingDirectoryText("${workspace_loc:" + javaProject.getPath().makeRelative().toOSString() + "}");  //$NON-NLS-1$//$NON-NLS-2$
					return;
				}//end if
			}//end if
		}//end try 
		catch (CoreException ce) {}
		setDefaultWorkingDirectoryText(System.getProperty("user.dir")); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#isValid(org.eclipse.debug.core.ILaunchConfiguration)
	 */
	public boolean isValid(ILaunchConfiguration config) {
		setErrorMessage(null);
		setMessage(null);
		// if variables are present, we cannot resolve the directory
		String workingDirPath = getWorkingDirectoryText();
		if (workingDirPath.indexOf("${") >= 0) { //$NON-NLS-1$
			IStringVariableManager manager = VariablesPlugin.getDefault().getStringVariableManager();
			try {
				manager.validateStringVariables(workingDirPath);
			}//end try
			catch (CoreException e) {
				setErrorMessage(e.getMessage());
				return false;
			}//end catch
		}//end if 
		else if (workingDirPath.length() > 0) {
			IContainer container = getContainer();
			if (container == null) {
				File dir = new File(workingDirPath);
				if (dir.isDirectory()) {
					return true;
				}//end if
				setErrorMessage("Working directory does not exist"); 
				return false;
			}//end if
		} else if (workingDirPath.length() == 0) {
			setErrorMessage("Working directory not specified");
		}
		return true;
	}//end isValid

	/**
	 * Defaults are empty.
	 * 
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#setDefaults(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
	 */
	public void setDefaults(ILaunchConfigurationWorkingCopy config) {
		config.setAttribute(IDescentLaunchConfigurationConstants.ATTR_WORKING_DIRECTORY, (String)null);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#initializeFrom(org.eclipse.debug.core.ILaunchConfiguration)
	 */
	public void initializeFrom(ILaunchConfiguration configuration) {
		setLaunchConfiguration(configuration);
		try {			
			String wd = configuration.getAttribute(IDescentLaunchConfigurationConstants.ATTR_WORKING_DIRECTORY, (String)null);
			setDefaultWorkingDir();
			if (wd != null) {
				setOtherWorkingDirectoryText(wd);
			}//end else
		}//end try 
		catch (CoreException e) {
			setErrorMessage("Exception occurred reading configuration:" + e.getStatus().getMessage());
			DescentLaunchingUI.log(e);
		}//end catch
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#performApply(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
	 */
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		if(fUseDefaultDirButton.getSelection()) {
			configuration.setAttribute(IDescentLaunchConfigurationConstants.ATTR_WORKING_DIRECTORY, (String)null);
		}//end if
		else {
			configuration.setAttribute(IDescentLaunchConfigurationConstants.ATTR_WORKING_DIRECTORY, getWorkingDirectoryText());
		}//end else
	}//end performApply
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#getName()
	 */
	public String getName() {
		return "Working Directory"; 
	}//end getName
	
	/**
	 * gets the path from the text box that is selected
	 * @return the working directory the user wishes to use
	 * @since 3.2
	 */
	protected String getWorkingDirectoryText() {
		if(fUseDefaultDirButton.getSelection()) {
			return fWorkingDirText.getText().trim();
		}//end if
		return fOtherWorkingText.getText().trim();
	}//end getWorkingDirectoryPath
	
	/**
	 * sets the default working directory text
	 * @param dir the dir to set the widget to
	 * @since 3.2
	 */
	protected void setDefaultWorkingDirectoryText(String dir) {
		if(dir != null) {
			fWorkingDirText.setText(dir);
			fUseDefaultDirButton.setSelection(true);
			handleUseDefaultWorkingDirButtonSelected();
		}//end if
	}//setDefaultWorkingDirectoryText
	
	/**
	 * sets the other dir text
	 * @param dir the new text
	 * @since 3.2
	 */
	protected void setOtherWorkingDirectoryText(String dir) {
		if(dir != null) {
			fOtherWorkingText.setText(dir);
			fUseDefaultDirButton.setSelection(false);
			fUseOtherDirButton.setSelection(true);
			handleUseOtherWorkingDirButtonSelected();
		}//end if
	}//end setOtherWorkingDirectoryText
	
	/**
	 * Sets the java project currently specified by the
	 * given launch config, if any.
	 */
	protected void setLaunchConfiguration(ILaunchConfiguration config) {
		fLaunchConfiguration = config;
	}	
	
	/**
	 * Returns the current java project context
	 */
	protected ILaunchConfiguration getLaunchConfiguration() {
		return fLaunchConfiguration;
	}
	
	/**
	 * Allows this entire block to be enabled/disabled
	 * @param enabled whether to enable it or not
	 */
	protected void setEnabled(boolean enabled) {
		fUseDefaultDirButton.setEnabled(enabled);
		fUseOtherDirButton.setEnabled(enabled);
		if(fOtherWorkingText.isEnabled()) {
			fOtherWorkingText.setEnabled(enabled);
			fWorkspaceButton.setEnabled(enabled);
			fVariablesButton.setEnabled(enabled);
			fFileSystemButton.setEnabled(enabled);
		}//end if
		// in the case where the 'other' text is selected and we want to enable
		if(fUseOtherDirButton.getSelection() && enabled == true) {
			fOtherWorkingText.setEnabled(enabled);
		}//end if
	}//end setEnabled
	
}//end class


