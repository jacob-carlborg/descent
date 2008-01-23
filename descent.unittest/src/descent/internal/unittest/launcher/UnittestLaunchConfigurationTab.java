package descent.internal.unittest.launcher;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
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
	private TestContainerSelector fSelector;

	private void createTestContainerSelector(Composite comp)
	{
		fTestContainerRadioButton = new Button(comp, SWT.RADIO);
		fTestContainerRadioButton
				.setText(JUnitMessages.UnittestLaunchConfigurationTab_selected_container);
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
	}

	private static class TestContainerSelector
	{

	}

	//--------------------------------------------------------------------------
	// State Management

	private void testModeChanged()
	{
		// TODO
	}

	//--------------------------------------------------------------------------
	// Initialization

	public void initializeFrom(ILaunchConfiguration configuration)
	{
		// TODO
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

		validateProject();
	}

	private void validateProject()
	{
		String projectName = fProjText.getText().trim();
		if (projectName.length() == 0)
		{
			setErrorMessage("Project not defined");
			return;
		}

		IStatus status = ResourcesPlugin.getWorkspace().validatePath(
				IPath.SEPARATOR + projectName, IResource.PROJECT);
		if (!status.isOK())
		{
			setErrorMessage(String.format("Invalid project name: %$1s",
					projectName));
			return;
		}

		IProject project = getWorkspaceRoot().getProject(projectName);
		if (!project.exists())
		{
			setErrorMessage("Project does not exist");
			return;
		}

		try
		{
			if (!project.hasNature(JavaCore.NATURE_ID))
			{
				setErrorMessage("Not a D project");
				return;
			}
		}
		catch (Exception e)
		{
			// Ignore
		}
	}

	//--------------------------------------------------------------------------
	// Application

	public void performApply(ILaunchConfigurationWorkingCopy config)
	{
		config.setAttribute(
				IDescentLaunchConfigurationConstants.ATTR_PROJECT_NAME,
				fProjText.getText());
		config.setAttribute(
				IDescentLaunchConfigurationConstants.ATTR_PROGRAM_NAME, ""); // TODO
		config.setAttribute(
				IUnittestLaunchConfigurationAttributes.LAUNCH_CONTAINER_ATTR,
				"");
	}

	//--------------------------------------------------------------------------
	// Defaults
	public void setDefaults(ILaunchConfigurationWorkingCopy config)
	{
		// TODO
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
