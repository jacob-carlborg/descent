package descent.internal.unittest.launcher;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

import descent.core.IJavaElement;
import descent.core.IJavaModel;
import descent.core.IJavaProject;
import descent.core.JavaCore;
import descent.core.JavaModelException;

import descent.debug.core.IDescentLaunchConfigurationConstants;

import descent.ui.JavaElementLabelProvider;
import descent.ui.JavaElementSorter;
import descent.ui.StandardJavaElementContentProvider;

import descent.internal.unittest.DescentUnittestPlugin;
import descent.internal.unittest.ui.JUnitMessages;
import descent.internal.unittest.ui.LayoutUtil;

public class UnittestLaunchConfigurationTab extends
		AbstractLaunchConfigurationTab
{

	//--------------------------------------------------------------------------
	// General

	public void createControl(Composite parent)
	{
		Composite comp = new Composite(parent, SWT.NONE);
		setControl(comp);

		GridLayout topLayout = new GridLayout();
		topLayout.numColumns = 3;
		comp.setLayout(topLayout);

		createProjectSelection(comp);
		createRunAllTests(comp);
		createTestContainerSelector(comp);

		Dialog.applyDialogFont(comp);
		validatePage();
	}

	public String getName()
	{
		return JUnitMessages.JUnitMainTab_tab_label;
	}

	@Override
	public void dispose()
	{
		super.dispose();
		fTestIcon.dispose();
	}

	//--------------------------------------------------------------------------
	// Project selection

	private Label fProjLabel;
	private Text fProjText;
	private Button fProjButton;

	private void createProjectSelection(Composite comp)
	{
		fProjLabel = new Label(comp, SWT.NONE);
		fProjLabel.setText(JUnitMessages.JUnitMainTab_label_project);
		GridData gd = new GridData();
		fProjLabel.setLayoutData(gd);

		fProjText = new Text(comp, SWT.SINGLE | SWT.BORDER);
		fProjText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		fProjText.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent evt)
			{
				resetTestMode();
				validatePage();
				updateLaunchConfigurationDialog();
			}
		});

		fProjButton = new Button(comp, SWT.PUSH);
		fProjButton.setText(JUnitMessages.JUnitMainTab_label_browse);
		gd = new GridData();
		fProjButton.setLayoutData(gd);
		LayoutUtil.setButtonDimensionHint(fProjButton);
		fProjButton.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent evt)
			{
				handleProjectButtonSelected();
			}
		});
	}

	/**
	 * Show a dialog that lets the user select a project.  This in turn provides
	 * context for choosing the container that should be run.
	 */
	private void handleProjectButtonSelected()
	{
		IJavaProject project = chooseJavaProject();
		if (project == null)
		{
			return;
		}

		String projectName = project.getElementName();
		fProjText.setText(projectName);
	}

	/**
	 * Realize a Java Project selection dialog and return the first selected project,
	 * or null if there was none.
	 */
	private IJavaProject chooseJavaProject()
	{
		IJavaProject[] projects;
		try
		{
			projects = JavaCore.create(getWorkspaceRoot()).getJavaProjects();
		}
		catch (JavaModelException e)
		{
			DescentUnittestPlugin.log(e.getStatus());
			projects = new IJavaProject[0];
		}

		ILabelProvider labelProvider = new JavaElementLabelProvider(
				JavaElementLabelProvider.SHOW_DEFAULT);
		ElementListSelectionDialog dialog = new ElementListSelectionDialog(
				getShell(), labelProvider);
		dialog.setTitle(JUnitMessages.JUnitMainTab_projectdialog_title);
		dialog.setMessage(JUnitMessages.JUnitMainTab_projectdialog_message);
		dialog.setElements(projects);

		IJavaProject javaProject = getJavaProject();
		if (javaProject != null)
		{
			dialog.setInitialSelections(new Object[] { javaProject });
		}
		if (dialog.open() == Window.OK)
		{
			return (IJavaProject) dialog.getFirstResult();
		}
		return null;
	}

	/**
	 * Return the IJavaProject corresponding to the project name in the project name
	 * text field, or null if the text does not match a project name.
	 */
	private IJavaProject getJavaProject()
	{
		String projectName = fProjText.getText().trim();
		if (projectName.length() < 1)
		{
			return null;
		}
		return getJavaModel().getJavaProject(projectName);
	}

	//--------------------------------------------------------------------------
	// Run all tests in project

	private Button fAllTestsRadioButton;

	private void createRunAllTests(Composite comp)
	{
		fAllTestsRadioButton = new Button(comp, SWT.RADIO);
		fAllTestsRadioButton
				.setText(JUnitMessages.UnittestLaunchConfigurationTab_all_tests_in_project);
		GridData gd = new GridData();
		gd.horizontalSpan = 3;
		fAllTestsRadioButton.setLayoutData(gd);
		fAllTestsRadioButton.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				if (fAllTestsRadioButton.getSelection())
					testModeChanged();
			}
		});
	}

	//--------------------------------------------------------------------------
	// Test container selection
	
	private Button fTestContainerRadioButton;
	private Button fIncludeSubpackagesCheckbox;
	private TreeViewer fContainerSelectionViewer;

	private void createTestContainerSelector(Composite comp)
	{
		// TODO this is way too slow; make it a dialog with a progress monitor
		
		fTestContainerRadioButton = new Button(comp, SWT.RADIO);
		fTestContainerRadioButton.setText(JUnitMessages.UnittestLaunchConfigurationTab_selected_container);
		GridData gd = new GridData();
		gd.horizontalSpan = 3;
		fTestContainerRadioButton.setLayoutData(gd);
		fTestContainerRadioButton.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				if (fTestContainerRadioButton.getSelection())
					testModeChanged();
			}
		});
		
		fIncludeSubpackagesCheckbox = new Button(comp, SWT.CHECK);
		fIncludeSubpackagesCheckbox.setText("Include sub-packages if a package is selected");
		gd = new GridData();
		gd.horizontalSpan = 3;
		gd.horizontalIndent = 25;
		fIncludeSubpackagesCheckbox.setLayoutData(gd);
		
		fContainerSelectionViewer = new TreeViewer(comp);
		gd = new GridData(GridData.FILL_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL);
		gd.horizontalSpan = 3;
		gd.horizontalIndent = 25;
		fContainerSelectionViewer.getControl().setLayoutData(gd);
		fContainerSelectionViewer.setLabelProvider(new JavaElementLabelProvider(JavaElementLabelProvider.SHOW_DEFAULT));
		fContainerSelectionViewer.setSorter(new JavaElementSorter());
	}
	
	private void setEnableContainerSelection(boolean enabled)
	{
		fIncludeSubpackagesCheckbox.setEnabled(enabled);
		fContainerSelectionViewer.getControl().setEnabled(enabled);
		
		if(enabled)
			fContainerSelectionViewer.setInput(getJavaProject());
	}

	//--------------------------------------------------------------------------
	// State Management

	private void testModeChanged()
	{
		boolean isAllTestsMode = fAllTestsRadioButton.getSelection();
		setEnableContainerSelection(!isAllTestsMode);
		validatePage();
		updateLaunchConfigurationDialog();
	}
	
	/**
	 * This is called whenever the project changes. It switchs to the "run
	 * all tests in project", since test lookup takes time.
	 */
	private void resetTestMode()
	{
		fTestContainerRadioButton.setSelection(false);
		fAllTestsRadioButton.setSelection(true);
		testModeChanged();
	}

	//--------------------------------------------------------------------------
	// Initialization

	public void initializeFrom(ILaunchConfiguration config)
	{
		updateProjectFromConfig(config);
		updateTestContainerFromConfig(config);
	}
	
	private void updateProjectFromConfig(ILaunchConfiguration config)
	{
		String projectName= ""; //$NON-NLS-1$
		try {
			projectName = config.getAttribute(IDescentLaunchConfigurationConstants.ATTR_PROJECT_NAME, ""); //$NON-NLS-1$
		} catch (CoreException ce) {
		}
		fProjText.setText(projectName);
	}

	public void updateTestContainerFromConfig(ILaunchConfiguration config)
	{
		String containerHandle= ""; //$NON-NLS-1$
		try {
			containerHandle = config.getAttribute(IUnittestLaunchConfigurationAttributes.LAUNCH_CONTAINER_ATTR, ""); //$NON-NLS-1$
		} catch (CoreException ce) {			
		}
		
		if(containerHandle.equals(""))
		{
			fAllTestsRadioButton.setSelection(true);
		}
		else
		{
			fTestContainerRadioButton.setSelection(true);
		}
	}

	//--------------------------------------------------------------------------
	// Validation

	@Override
	public boolean isValid(ILaunchConfiguration config)
	{
		return getErrorMessage() == null;
	}

	protected void validatePage()
	{
		setErrorMessage(null);
		setMessage(null);

		String errorMsg = validateProject();
		if(null != errorMsg)
			setErrorMessage(errorMsg);
	}

	private String validateProject()
	{
		String projectName = fProjText.getText().trim();
		if (projectName.length() == 0)
			return "Project not defined";

		IStatus status = ResourcesPlugin.getWorkspace().validatePath(
				IPath.SEPARATOR + projectName, IResource.PROJECT);
		if (!status.isOK())
			return String.format("Invalid project name: %$1s", projectName);

		IProject project = getWorkspaceRoot().getProject(projectName);
		if (!project.exists())
			return "Project does not exist";

		try
		{
			if (!project.hasNature(JavaCore.NATURE_ID))
				return "Not a D project";
		}
		catch (Exception e)
		{
			// Ignore
		}
		
		return null;
	}

	//--------------------------------------------------------------------------
	// Application

	public void performApply(ILaunchConfigurationWorkingCopy config)
	{	
		config.setAttribute(IDescentLaunchConfigurationConstants.ATTR_PROJECT_NAME, fProjText.getText());
		config.setAttribute(IUnittestLaunchConfigurationAttributes.LAUNCH_CONTAINER_ATTR, "");
		
		// WAITING_ON_CORE get the fluted program executable
		config.setAttribute(IDescentLaunchConfigurationConstants.ATTR_PROGRAM_NAME,
				"C:/Users/xycos/workspace/descent.unittest/testdata/src/test.exe");
	}

	//--------------------------------------------------------------------------
	// Defaults
	public void setDefaults(ILaunchConfigurationWorkingCopy config)
	{
		IJavaElement javaElement = getContext();
		String name = "";
		if (javaElement != null)
		{
			IJavaProject javaProject = javaElement.getJavaProject();
			name = (javaProject != null && javaProject.exists()) ?
					javaProject.getElementName() : "";
			config.setAttribute(IDescentLaunchConfigurationConstants.ATTR_PROJECT_NAME, name);
			
			name = getLaunchConfigurationDialog().generateName(name);
			config.rename(name);
		}
		else
		{
			config.setAttribute(IDescentLaunchConfigurationConstants.ATTR_PROJECT_NAME, ""); //$NON-NLS-1$
		}
		
		config.setAttribute(IUnittestLaunchConfigurationAttributes.LAUNCH_CONTAINER_ATTR, "");
	}

	/**
	 * Returns the current Java element context from which to initialize
	 * default settings, or <code>null</code> if none.
	 * 
	 * @return Java element context.
	 */
	protected IJavaElement getContext()
	{
		IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		if (activeWorkbenchWindow == null)
		{
			return null;
		}
		IWorkbenchPage page = activeWorkbenchWindow.getActivePage();
		if (page != null)
		{
			ISelection selection = page.getSelection();
			if (selection instanceof IStructuredSelection)
			{
				IStructuredSelection ss = (IStructuredSelection) selection;
				if (!ss.isEmpty())
				{
					Object obj = ss.getFirstElement();
					if (obj instanceof IJavaElement)
					{
						return (IJavaElement) obj;
					}
					if (obj instanceof IResource)
					{
						IJavaElement je = JavaCore.create((IResource) obj);
						if (je == null)
						{
							IProject pro = ((IResource) obj).getProject();
							je = JavaCore.create(pro);
						}
						if (je != null)
						{
							return je;
						}
					}
				}
			}
			IEditorPart part = page.getActiveEditor();
			if (part != null)
			{
				IEditorInput input = part.getEditorInput();
				return (IJavaElement) input.getAdapter(IJavaElement.class);
			}
		}
		return null;
	}

	//--------------------------------------------------------------------------
	// Convenience (non-layout) methods

	/**
	 * Convenience method to get the workspace root.
	 */
	private IWorkspaceRoot getWorkspaceRoot()
	{
		return ResourcesPlugin.getWorkspace().getRoot();
	}

	/**
	 * Convenience method to get access to the java model.
	 */
	private IJavaModel getJavaModel()
	{
		return JavaCore.create(getWorkspaceRoot());
	}

	//--------------------------------------------------------------------------
	// Icon
	private final Image fTestIcon = createImage("obj16/test.gif"); //$NON-NLS-1$

	private static Image createImage(String path)
	{
		return DescentUnittestPlugin.getImageDescriptor(path).createImage();
	}

	@Override
	public Image getImage()
	{
		return fTestIcon;
	}
}
