package descent.internal.debug.ui;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

import descent.core.IJavaModel;
import descent.core.IJavaProject;
import descent.core.JavaCore;
import descent.core.JavaModelException;
import descent.debug.core.DescentDebugPlugin;
import descent.debug.core.IDebuggerDescriptor;
import descent.debug.core.IDescentLaunchConfigurationConstants;
import descent.debug.core.IDescentLaunchingPreferenceConstants;
import descent.debug.ui.DescentDebugUI;
import descent.ui.JavaElementLabelProvider;

public class MainTab extends AbstractLaunchConfigurationTab {
	
	// Project UI widgets
	protected Label fProjLabel;
	protected Text fProjText;
	protected Button fProjButton;

	// Main class UI widgets
	protected Label fProgLabel;
	protected Text fProgText;
	private Button fLineTrackCheckbox;
	private Label fLineTrackHelp;
	private Label fLineTrackLabel;
	private Text fLineTrackText;
	private Button fLineTrackDefaultButton;
	
	protected static final String EMPTY_STRING = ""; //$NON-NLS-1$
	private final String fMode;
	
	public MainTab(String mode) {
		this.fMode = mode;
	}
	
	public void createControl(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		setControl(comp);

		//LaunchUIPlugin.getDefault().getWorkbench().getHelpSystem().setHelp(getControl(), ICDTLaunchHelpContextIds.LAUNCH_CONFIGURATION_DIALOG_MAIN_TAB);

		GridLayout topLayout = new GridLayout();
		comp.setLayout(topLayout);

		createVerticalSpacer(comp, 1);
		createProjectGroup(comp, 1);
		createExeFileGroup(comp, 1);
		createLineTrackGroup(comp, 1);
		createVerticalSpacer(comp, 1);
		//LaunchUIPlugin.setDialogShell(parent.getShell());
	}
	
	protected void createProjectGroup(Composite parent, int colSpan) {
		Composite projComp = new Composite(parent, SWT.NONE);
		GridLayout projLayout = new GridLayout();
		projLayout.numColumns = 2;
		projLayout.marginHeight = 0;
		projLayout.marginWidth = 0;
		projComp.setLayout(projLayout);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = colSpan;
		projComp.setLayoutData(gd);

		fProjLabel = new Label(projComp, SWT.NONE);
		fProjLabel.setText("&Project:"); //$NON-NLS-1$
		gd = new GridData();
		gd.horizontalSpan = 2;
		fProjLabel.setLayoutData(gd);

		fProjText = new Text(projComp, SWT.SINGLE | SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		fProjText.setLayoutData(gd);
		fProjText.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent evt) {
				updateLaunchConfigurationDialog();
			}
		});

		fProjButton = createPushButton(projComp, "&Browse...", null); //$NON-NLS-1$
		fProjButton.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent evt) {
				handleProjectButtonSelected();
				updateLaunchConfigurationDialog();
			}
		});
	}
	
	protected void createExeFileGroup(Composite parent, int colSpan) {
		Composite mainComp = new Composite(parent, SWT.NONE);
		GridLayout mainLayout = new GridLayout();
		mainLayout.numColumns = 2;
		mainLayout.marginHeight = 0;
		mainLayout.marginWidth = 0;
		mainComp.setLayout(mainLayout);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = colSpan;
		mainComp.setLayoutData(gd);
		fProgLabel = new Label(mainComp, SWT.NONE);
		fProgLabel.setText("D Application:"); //$NON-NLS-1$
		gd = new GridData();
		gd.horizontalSpan = 3;
		fProgLabel.setLayoutData(gd);
		fProgText = new Text(mainComp, SWT.SINGLE | SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		fProgText.setLayoutData(gd);
		fProgText.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent evt) {
				updateLaunchConfigurationDialog();
			}
		});

		Button fBrowseForBinaryButton;
		fBrowseForBinaryButton = createPushButton(mainComp, "B&rowse...", null); //$NON-NLS-1$
		fBrowseForBinaryButton.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent evt) {
				handleBinaryBrowseButtonSelected();
				updateLaunchConfigurationDialog();
			}
		});
	}
	
	protected void createLineTrackGroup(Composite parent, int colSpan)
	{
		// Create a composite to arrange the options
		Group comp = new Group(parent, SWT.NONE);
		comp.setText("Stack trace linking");
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		comp.setLayout(layout);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = colSpan;
		comp.setLayoutData(gd);
		
		// Create the help text
		fLineTrackHelp = new Label(comp, SWT.LEFT | SWT.WRAP);
		fLineTrackHelp.setText("Descent can be configured to create links to files/lines from stack " +
				"traces in the console output. To enable this, specify a regular expression to match lines containing a trace " +
				"element. If there is a match, the first group must match the filename, while the second matches " +
				"the line. The regular expression syntax is that of java.util.Pattern. The default regular " +
				"expression matches lines from Tango's stack trace.");
	    gd = new GridData(GridData.FILL_BOTH);
	    gd.horizontalSpan = 3;
	    fLineTrackHelp.setLayoutData(gd);
		
		// Create the checkbox
		fLineTrackCheckbox = new Button(comp, SWT.CHECK);
		fLineTrackCheckbox.setText("Enable stack trace linking");
		fLineTrackCheckbox.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                updateLaunchConfigurationDialog();
                updateLineTrackEnablement();
            }
        });
        gd = new GridData();
        gd.horizontalSpan = 3;
        fLineTrackCheckbox.setLayoutData(gd);
        
		// Create the label
		fLineTrackLabel = new Label(comp, SWT.NONE);
		fLineTrackLabel.setText("Stack trace regex:"); //$NON-NLS-1$
		gd = new GridData();
		gd.horizontalSpan = 1;
		fLineTrackLabel.setLayoutData(gd);
		
		// Create the text box
		fLineTrackText = new Text(comp, SWT.SINGLE | SWT.BORDER);
		fLineTrackText.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent evt)
			{
				updateLaunchConfigurationDialog();
			}
		});
		gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = 1;
		fLineTrackText.setLayoutData(gd);
		
		// Create the default button
		fLineTrackDefaultButton = new Button(comp, SWT.PUSH);
		fLineTrackDefaultButton.setText("Default");
		fLineTrackDefaultButton.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
            	fLineTrackText.setText(IDescentLaunchConfigurationConstants.DEFAULT_STACKTRACE_REGEX);
                updateLaunchConfigurationDialog();
            }
        });
        gd = new GridData();
        gd.horizontalSpan = 1;
        fLineTrackDefaultButton.setLayoutData(gd);
	}
	
	private void updateLineTrackEnablement()
	{
		boolean enabled = fLineTrackCheckbox.getSelection();
		fLineTrackLabel.setEnabled(enabled);
		fLineTrackText.setEnabled(enabled);
		fLineTrackDefaultButton.setEnabled(enabled);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#initializeFrom(org.eclipse.debug.core.ILaunchConfiguration)
	 */
	public void initializeFrom(ILaunchConfiguration config) {
		updateProjectFromConfig(config);
		updateProgramFromConfig(config);
		updateLineTrackFromConfig(config);
	}
	
	protected void updateProjectFromConfig(ILaunchConfiguration config) {
		String projectName = EMPTY_STRING;
		try {
			projectName = config.getAttribute(IDescentLaunchConfigurationConstants.ATTR_PROJECT_NAME, EMPTY_STRING);
		} catch (CoreException ce) {
			DescentDebugUI.log(ce);
		}
		fProjText.setText(projectName);
	}
	
	protected void updateProgramFromConfig(ILaunchConfiguration config) {
		String programName = EMPTY_STRING;
		try {
			programName = config.getAttribute(IDescentLaunchConfigurationConstants.ATTR_PROGRAM_NAME, EMPTY_STRING);
		} catch (CoreException ce) {
			DescentDebugUI.log(ce);
		}
		fProgText.setText(programName);
	}
	
	protected void updateLineTrackFromConfig(ILaunchConfiguration config)
	{
		boolean enabled = false;
		try {
			enabled = config.getAttribute(IDescentLaunchConfigurationConstants.ATTR_LINK_STACKTRACE, false);
		} catch (CoreException ce) {
			DescentDebugUI.log(ce);
		}
		fLineTrackCheckbox.setSelection(enabled);
		
		final String dflt = IDescentLaunchConfigurationConstants.DEFAULT_STACKTRACE_REGEX;
		String regex = dflt;
		try {
			regex = config.getAttribute(IDescentLaunchConfigurationConstants.ATTR_STACKTRACE_REGEX, dflt);
		} catch (CoreException ce) {
			DescentDebugUI.log(ce);
		}
		fLineTrackText.setText(regex);
		
		updateLineTrackEnablement();
	}
	
	public void performApply(ILaunchConfigurationWorkingCopy config) {
		config.setAttribute(IDescentLaunchConfigurationConstants.ATTR_PROJECT_NAME, fProjText.getText());
		config.setAttribute(IDescentLaunchConfigurationConstants.ATTR_PROGRAM_NAME, fProgText.getText());
		config.setAttribute(IDescentLaunchConfigurationConstants.ATTR_LINK_STACKTRACE, fLineTrackCheckbox.getSelection());
		config.setAttribute(IDescentLaunchConfigurationConstants.ATTR_STACKTRACE_REGEX, fLineTrackText.getText());
	}

	/**
	 * Show a dialog that lets the user select a project. This in turn provides context for the main
	 * type, allowing the user to key a main type name, or constraining the search for main types to
	 * the specified project.
	 */
	protected void handleBinaryBrowseButtonSelected() {
		final IJavaProject jproject = getJavaProject();
		if (jproject == null) {
			MessageDialog.openInformation(getShell(), "Project required", //$NON-NLS-1$
					"Project must first be entered before browsing for a program"); //$NON-NLS-1$
			return;
		}
		FileDialog fileDialog = new FileDialog(getShell(), SWT.NONE);
		fileDialog.setFilterPath(jproject.getProject().getLocation().toOSString());
		fileDialog.setFileName(fProgText.getText());
		String text= fileDialog.open();
		if (text != null) {
			fProgText.setText(text);
		}
	}
	
	/**
	 * Show a dialog that lets the user select a project. This in turn provides context for the main
	 * type, allowing the user to key a main type name, or constraining the search for main types to
	 * the specified project.
	 */
	protected void handleProjectButtonSelected() {
		IJavaProject project = chooseCProject();
		if (project == null) {
			return;
		}

		String projectName = project.getElementName();
		fProjText.setText(projectName);
	}
	
	/**
	 * Realize a C Project selection dialog and return the first selected project, or null if there
	 * was none.
	 */
	protected IJavaProject chooseCProject() {
		try {
			IJavaProject[] projects = getJavaProjects();

			ILabelProvider labelProvider = new JavaElementLabelProvider();
			ElementListSelectionDialog dialog = new ElementListSelectionDialog(getShell(), labelProvider);
			dialog.setTitle("Project Selection"); //$NON-NLS-1$
			dialog.setMessage("Choose a &project to constrain the search for a program"); //$NON-NLS-1$
			dialog.setElements(projects);

			IJavaProject cProject = getJavaProject();
			if (cProject != null) {
				dialog.setInitialSelections(new Object[]{cProject});
			}
			if (dialog.open() == Window.OK) {
				return (IJavaProject)dialog.getFirstResult();
			}
		} catch (JavaModelException e) {
			DescentDebugUI.errorDialog("Launch UI internal error", e); //$NON-NLS-1$			
		}
		return null;
	}

	/**
	 * Return an array a IJavaProject whose platform match that of the runtime env.
	 */
	protected IJavaProject[] getJavaProjects() throws JavaModelException {
		return getJavaModel().getJavaProjects();
	}

	/**
	 * Return the IJavaProject corresponding to the project name in the project name text field, or
	 * null if the text does not match a project name.
	 */
	protected IJavaProject getJavaProject() {
		String projectName = fProjText.getText().trim();
		if (projectName.length() < 1) {
			return null;
		}
		return getJavaModel().getJavaProject(projectName);
	}
	
	/**
	 * Utility method to get the Java Model.
	 */
	protected IJavaModel getJavaModel() {
		return JavaCore.create(ResourcesPlugin.getWorkspace().getRoot());
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#isValid(org.eclipse.debug.core.ILaunchConfiguration)
	 */
	public boolean isValid(ILaunchConfiguration config) {

		setErrorMessage(null);
		setMessage(null);
		
		if (fMode.equals(ILaunchManager.DEBUG_MODE)) {
			// Check the debugger
			IDebuggerDescriptor debugger = DescentDebugPlugin.getCurrentDebugger();
			if (debugger == null) {
				setErrorMessage("Debugger must be defined in Window -> Preferences -> D -> Debug.");
				return false;
			}
			
			String ddbgPath = DescentDebugPlugin.getDefault().getPreferenceStore().getString(IDescentLaunchingPreferenceConstants.DEBUGGER_PATH);
			if (ddbgPath == null || ddbgPath.trim().length() == 0) {
				setErrorMessage("Debugger executable must be defined in Window -> Preferences -> D -> Debug."); //$NON-NLS-1$
				return false;
			}
			if (!new File(ddbgPath).exists()) {
				setErrorMessage("Debugger executable file (" + ddbgPath + ") does not exist");
				return false;
			}
		}

		String name = fProjText.getText().trim();
		if (name.length() == 0) {
			setErrorMessage("Java Project not specified"); //$NON-NLS-1$
			return false;
		}
		if (!ResourcesPlugin.getWorkspace().getRoot().getProject(name).exists()) {
			setErrorMessage("Project does not exist"); //$NON-NLS-1$
			return false;
		}
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(name);
		if (!project.isOpen()) {
			setErrorMessage("Project must be opened"); //$NON-NLS-1$
			return false;
		}

		name = fProgText.getText().trim();
		if (name.length() == 0) {
			setErrorMessage("Program not specified"); //$NON-NLS-1$
			return false;
		}
		if (name.equals(".") || name.equals("..")) { //$NON-NLS-1$ //$NON-NLS-2$
			setErrorMessage("Program does not exist"); //$NON-NLS-1$
			return false;
		}
		IPath exePath = new Path(name);
		if (!exePath.isAbsolute()) {
			if (!project.getFile(name).exists()) {
				setErrorMessage("Program does not exist"); //$NON-NLS-1$
				return false;
			}
			exePath = project.getFile(name).getLocation();
		} else {
			if (!exePath.toFile().exists()) {
				setErrorMessage("Program does not exist"); //$NON-NLS-1$
				return false;
			}
		}
		
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#setDefaults(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
	 */
	public void setDefaults(ILaunchConfigurationWorkingCopy config) {
		/*
		 * Use the first project available as default.
		 */
		try {
			IJavaProject[] projects = getJavaModel().getJavaProjects();
			if (projects.length > 0) {
				config.setAttribute(IDescentLaunchConfigurationConstants.ATTR_PROJECT_NAME, projects[0].getElementName());
			}
		} catch (JavaModelException e) {
			config.setAttribute(IDescentLaunchConfigurationConstants.ATTR_PROJECT_NAME, EMPTY_STRING);
		}
		
		config.setAttribute(IDescentLaunchConfigurationConstants.ATTR_PROGRAM_NAME, EMPTY_STRING);
		
		config.setAttribute(IDescentLaunchConfigurationConstants.ATTR_LINK_STACKTRACE, false);
		config.setAttribute(IDescentLaunchConfigurationConstants.DEFAULT_STACKTRACE_REGEX,
				IDescentLaunchConfigurationConstants.DEFAULT_STACKTRACE_REGEX);
	}

	public String getName() {
		return "Main";
	}
	
	@Override
	public Image getImage() {
		return DescentDebugUI.getDefault().getImageRegistry().get(Images.MAIN_TAB);
	}

}
