package descent.internal.building.ui;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

import descent.building.IDescentBuilderConstants;
import descent.core.IJavaProject;
import descent.core.JavaCore;
import descent.core.JavaModelException;
import descent.internal.building.BuildingPlugin;
import descent.internal.ui.wizards.dialogfields.DialogField;
import descent.internal.ui.wizards.dialogfields.IDialogFieldListener;
import descent.internal.ui.wizards.dialogfields.IListAdapter;
import descent.internal.ui.wizards.dialogfields.ListDialogField;
import descent.ui.JavaElementLabelProvider;
import descent.ui.JavaElementSorter;

@SuppressWarnings("unchecked")
public class BuildTab extends AbstractBuilderTab
{    
    //--------------------------------------------------------------------------
    // Project selection
    
    private final class ProjectSetting implements ISetting
    { 
        private Label fLabel;
        private Text fText;
        private Button fButton;
        
        public void addToControl(Composite comp)
        {
            fLabel = new Label(comp, SWT.NONE);
            fLabel.setText("Project:");
            GridData gd = new GridData();
            fLabel.setLayoutData(gd);
    
            fText = new Text(comp, SWT.SINGLE | SWT.BORDER);
            fText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            fText.addModifyListener(new ModifyListener()
            {
                public void modifyText(ModifyEvent evt)
                {
                    validatePage();
                    updateLaunchConfigurationDialog();
                }
            });
    
            fButton = new Button(comp, SWT.PUSH);
            fButton.setText("Browse...");
            gd = new GridData();
            fButton.setLayoutData(gd);
            setButtonDimensionHint(fButton);
            fButton.addSelectionListener(new SelectionAdapter()
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
            fText.setText(projectName);
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
                BuildingPlugin.log(e.getStatus());
                projects = new IJavaProject[0];
            }
            
            ILabelProvider labelProvider = new JavaElementLabelProvider(JavaElementLabelProvider.SHOW_DEFAULT);
            ElementListSelectionDialog dialog = new ElementListSelectionDialog(
                    getShell(), labelProvider);
            dialog.setTitle("Select Project");
            dialog.setMessage("Select the project in which to place output files");
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
            String projectName = fText.getText().trim();
            if (projectName.length() < 1)
            {
                return null;
            }
            return getJavaModel().getJavaProject(projectName);
        }

        public void initializeFrom(ILaunchConfiguration config)
        {
            String projectName = "";
            try
            {
                projectName = config.getAttribute(IDescentBuilderConstants.ATTR_PROJECT_NAME, "");
            }
            catch(CoreException e) { }
            
            fText.setText(projectName);
        }

        public void performApply(ILaunchConfigurationWorkingCopy config)
        {
            config.setAttribute(IDescentBuilderConstants.ATTR_PROJECT_NAME, fText.getText().trim());
        }

        public void setDefaults(ILaunchConfigurationWorkingCopy config)
        {
            String projectName = ""; //$NON-NLS-1$
            
            IJavaProject javaProject = getActiveProject();
            if(null != javaProject && javaProject.exists())
                projectName = javaProject.getElementName();
            
            config.setAttribute(IDescentBuilderConstants.ATTR_PROJECT_NAME, projectName);
            
            // Also, rename the config here (don't do it anywhere else in the
            // builder UI!!!)
            config.rename(getLaunchConfigurationDialog().generateName(projectName));
        }

        public void validate()
        {
            String projectName = fText.getText().trim();
            if (projectName.length() == 0)
            {
                setErrorMessage("Project not defined");
                return;
            }

            IStatus status = ResourcesPlugin.getWorkspace().validatePath(
                    IPath.SEPARATOR + projectName, IResource.PROJECT);
            if (!status.isOK())
            {
                setErrorMessage(String.format("Invalid project name: %$1s", projectName));
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
    }
    
    //--------------------------------------------------------------------------
    // Module selection
    
    private final class ModulesSetting implements ISetting
    { 
        private Label fLabel;
        private ListDialogField fList;
        
        public void addToControl(Composite comp)
        {
            // Define the listener constants
            final int IDX_ADD = 0;
            final int IDX_REMOVE = 1;
            final String[] buttonLabels = new String[]
            {
                "Add...",
                "Remove",
            };
            
            // Define the listener class
            class ModulesListAdapter implements IListAdapter, IDialogFieldListener
            {   
                public void customButtonPressed(ListDialogField field, int index)
                {
                    switch(index)
                    {
                    case IDX_ADD:
                        performAdd();
                        break;
                    default:
                        break;
                    }
                }
    
                public void doubleClicked(ListDialogField field)
                {
                    performEdit(field);
                }
    
                public void selectionChanged(ListDialogField field)
                {
                    // Ignore
                }
    
                public void dialogFieldChanged(DialogField field)
                {
                    validatePage();
                }
            }
            
            // Add the label
            fLabel = new Label(comp, SWT.LEFT | SWT.WRAP);
            fLabel.setText("Main modules to build (dependancies will be included)");
            GridData gd = new GridData(GridData.FILL_BOTH);
            gd.horizontalSpan = 3;
            fLabel.setLayoutData(gd);
            
            // Create the list
            ModulesListAdapter adapter = new ModulesListAdapter();
            fList = new ListDialogField(adapter, buttonLabels, 
                    new JavaElementLabelProvider());
            fList.setDialogFieldListener(adapter);
            fList.setRemoveButtonIndex(IDX_REMOVE);
            fList.setViewerSorter(new JavaElementSorter());
            
            // TODO ack, so ugly!!!! when we know the final layout of the
            // page, change it so that it doesn't make users want to gouge
            // their eyes out.
            fList.doFillIntoGrid(comp, 3);
        }
        
        private void performAdd()
        {
            // TODO
        }
        
        private void performEdit(ListDialogField field)
        {
            // TODO
        }

        public void initializeFrom(ILaunchConfiguration config)
        {
            List modules = EMPTY_LIST;
            try
            {
                modules = config.getAttribute(IDescentBuilderConstants.ATTR_MODULES_LIST, EMPTY_LIST);
            }
            catch(CoreException e) { }
            
            fList.setElements(modules);
        }

        public void performApply(ILaunchConfigurationWorkingCopy config)
        {
            // TODO
            config.setAttribute(IDescentBuilderConstants.ATTR_MODULES_LIST, EMPTY_LIST);
        }

        public void setDefaults(ILaunchConfigurationWorkingCopy config)
        {
            config.setAttribute(IDescentBuilderConstants.ATTR_MODULES_LIST, EMPTY_LIST);
        }

        public void validate()
        {
            List<String> modules = fList.getElementsNoCopy();
            
            if(modules.isEmpty())
            {
                setErrorMessage("Module list is empty");
                return;
            }
            
            // TODO validate the individual modules... maybe (maybe instead
            // have a check button, since this can be a lengthy operation?)
            
        }
    }
    
    //--------------------------------------------------------------------------
    // Tab

    public String getName()
    {
        return "Build";
    }
    
    @Override
    protected String getIconPath()
    {
        return "obj16/builders.gif";
    }
    
    @Override
    protected ISetting[] getSettings()
    {
        return new ISetting[]
        {
            new ProjectSetting(),
            new ModulesSetting(),
        };
    }

    @Override
    protected Layout getTopLayout()
    {
        GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        return layout;
    }
}
