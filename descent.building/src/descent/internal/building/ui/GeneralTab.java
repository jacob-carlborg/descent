package descent.internal.building.ui;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

import descent.core.IJavaProject;
import descent.core.JavaCore;
import descent.core.JavaModelException;
import descent.internal.building.BuilderUtil;
import descent.internal.building.BuildingPlugin;
import descent.internal.ui.wizards.dialogfields.DialogField;
import descent.internal.ui.wizards.dialogfields.IDialogFieldListener;
import descent.internal.ui.wizards.dialogfields.IListAdapter;
import descent.internal.ui.wizards.dialogfields.ListDialogField;
import descent.ui.JavaElementLabelProvider;
import descent.ui.JavaElementSorter;

import static descent.building.IDescentBuilderConstants.*;

@SuppressWarnings("unchecked")
/* package */ final class GeneralTab extends AbstractBuilderTab
{   
    //--------------------------------------------------------------------------
    // Project selection
    
    private final class ProjectSetting extends TextSetting
    {
        ProjectSetting()
        {
            super(ATTR_PROJECT_NAME, "Project:", 3, true, false);
        }
        
        /**
         * Show a dialog that lets the user select a project.  This in turn provides
         * context for choosing the container that should be run.
         */
        @Override
        protected String browse()
        {
            IJavaProject project = chooseJavaProject();
            if(null != project)
            {
                String name = project.getElementName();
                if(!name.equals(fText.getText()))
                    outputFileSetting.projectChanged(project);
                return name;
            }
            else
            {
                return null;
            }
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

        public void setDefaults(ILaunchConfigurationWorkingCopy config)
        {
            String projectName = ""; //$NON-NLS-1$
            
            IJavaProject javaProject = getActiveProject();
            if(null != javaProject && javaProject.exists())
                projectName = javaProject.getElementName();
            
            config.setAttribute(ATTR_PROJECT_NAME, projectName);
            
            // Also, rename the config here (don't do it anywhere else in the
            // builder UI!!!)
            config.rename(getLaunchConfigurationDialog().generateName(projectName));
        }

        public String validate()
        {
            String projectName = fText.getText().trim();
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
    }
    
    //--------------------------------------------------------------------------
    // Output type
    
    private final class OutputTypeSetting implements ISetting
    {
        private Label fLabel;
        private Button fExecutableRadio;
        private Button fStaticLibRadio;
        
        // Note: Okay to use == for comparing this field
        private String fCurrentSetting;
        
        public void addToControl(Composite comp)
        {   
            fLabel = new Label(comp, SWT.LEFT);
            fLabel.setText("Target type:");
            GridData gd = new GridData();
            gd.horizontalSpan = 3;
            fLabel.setLayoutData(gd);
            
            fExecutableRadio = createRadioButton(comp, 3, "Executable", 25, 
                    new SelectionAdapter()
                    {
                        public void widgetSelected(SelectionEvent e)
                        {
                            if(fExecutableRadio.getSelection())
                            {
                                if(fCurrentSetting != OUTPUT_TYPE_EXECUTABLE)
                                {
                                    fCurrentSetting = OUTPUT_TYPE_EXECUTABLE;
                                    outputFileSetting.outputTypeChanged();
                                    updateLaunchConfigurationDialog();
                                }
                            }
                        }
                    });
            fStaticLibRadio = createRadioButton(comp, 3, "Static library", 25, 
                    new SelectionAdapter()
                    {
                        public void widgetSelected(SelectionEvent e)
                        {
                            if(fStaticLibRadio.getSelection())
                            {
                                if(fCurrentSetting != OUTPUT_TYPE_STATIC_LIBRARY)
                                {
                                    fCurrentSetting = OUTPUT_TYPE_STATIC_LIBRARY;
                                    outputFileSetting.outputTypeChanged();
                                    updateLaunchConfigurationDialog();
                                }
                            }
                        }
                    });
            createSpacer(comp, 3);
        }

        public void initializeFrom(ILaunchConfiguration config)
        {
            String outputType = OUTPUT_TYPE_EXECUTABLE;
            try
            {
                outputType = config.getAttribute(ATTR_OUTPUT_TYPE, OUTPUT_TYPE_EXECUTABLE);
            }
            catch(CoreException e) { }
            
            if(outputType.equals(OUTPUT_TYPE_STATIC_LIBRARY))
            {
                fExecutableRadio.setSelection(false);
                fStaticLibRadio.setSelection(true);
                fCurrentSetting = OUTPUT_TYPE_STATIC_LIBRARY;
            }
            else
            {
                fExecutableRadio.setSelection(true);
                fStaticLibRadio.setSelection(false);
                fCurrentSetting = OUTPUT_TYPE_EXECUTABLE;
            }
        }

        public void performApply(ILaunchConfigurationWorkingCopy config)
        {
            config.setAttribute(ATTR_OUTPUT_TYPE, fCurrentSetting);
        }

        public void setDefaults(ILaunchConfigurationWorkingCopy config)
        {
            config.setAttribute(ATTR_OUTPUT_TYPE, OUTPUT_TYPE_EXECUTABLE);
        }

        public String validate()
        {
            // Nothing to do -- any choice is valid
            return null;
        }
        
        public String getExtension()
        {
            if(fExecutableRadio == null)
                return BuilderUtil.EXTENSION_EXECUTABLE;
            
            if(fExecutableRadio.getSelection())
                return BuilderUtil.EXTENSION_EXECUTABLE;
            else
                return BuilderUtil.EXTENSION_STATIC_LIBRARY;
        }
    }
    
    //--------------------------------------------------------------------------
    // Output file
    
    private final class OutputFileSetting extends TextSetting
    { 
        OutputFileSetting()
        {
            super(ATTR_OUTPUT_FILE, "Output file:", 3, true, true);
        }
        
        @Override
        protected String browse()
        {
            String defaultExtension = getExtension();
            String defaultLocation = fText.getText();
            if(0 == defaultLocation.length())
                defaultLocation = null;
            
            FileDialog dialog = new FileDialog(getShell(), SWT.SAVE);
            dialog.setText("Output file location");
            dialog.setFilterExtensions((defaultExtension.length()) > 0 ?
                    new String[] { "*" + getExtension(), "*.*" } :
                    new String[] { "*.*" });
            dialog.setFilterPath(toFilterPath(defaultLocation));
            
            return normalizeFilename(dialog.open());
        }
        
        private String toFilterPath(String filename)
        {
            IPath path = new Path(filename);
            if(path.segmentCount() <= 1)
                return null;
            return path.removeLastSegments(1).removeTrailingSeparator().toOSString();
        }
        
        private String normalizeFilename(String filename)
        {
            return new Path(filename).toString();
        }
        
        public void setDefaults(ILaunchConfigurationWorkingCopy config)
        {
            config.setAttribute(ATTR_OUTPUT_FILE, 
                    getDefaultLocationForProject(getActiveProject()));
        }

        public String validate()
        {
            String pathText = fText.getText().trim();
            if (pathText.length() == 0)
                return "Output path not defined";
            
            // No way to check if a file is a valid filename since that's
            // file-system (NTFS/FAT/whatever) dependent :-(. I guess some small
            // sanity checks could be added... but whatever, I'll just handle
            // the error in the builder itself
            
            return null;
        }
        
        public void projectChanged(IJavaProject project)
        {
            if(null != fText)
                fText.setText(getDefaultLocationForProject(project));
        }
        
        public void outputTypeChanged()
        {
            if(null != fText)
                fText.setText(updateExtension(fText.getText()));
        }
        
        /**
         * Gets the path for the default output file in the given project.
         * 
         * @param project the project whose output folder the result should be in
         * @return        the string representation of the output path
         */
        private String getDefaultLocationForProject(IJavaProject project)
        {
            if(null == project || !project.exists())
                return "";
            
            try
            {
                StringBuilder outputFile = new StringBuilder();
                outputFile.append(BuilderUtil.getAbsolutePath(project.
                        getOutputLocation().makeAbsolute()));
                outputFile.append(IPath.SEPARATOR);
                outputFile.append(project.getElementName());
                return updateExtension(outputFile.toString());
            }
            catch(JavaModelException e)
            {
                return "";
            }
        }
        
        /**
         * Updates the extension in the given path to reflect the extension that
         * should be there depending on the file type. For example if the given
         * path is "C:\d\project\out.exe" and the type is changed to
         * dynamic library, this method will return "C:\d\project\out.dll".
         * 
         * @param filename
         * @return
         */
        private String updateExtension(String filename)
        {
            // PERHAPS on unix systems should this actually return libXYZ.a for an
            // input of XYZ if the type is static library...?
            
            if(null == filename || 0 == filename.length())
                return "";
            return filenameWithoutExtension(filename) + getExtension();
        }
        
        /**
         * Gets the default extension for the current output file type on this OS.
         * 
         * @return the default extension for the current output file type on this OS
         */
        private String getExtension()
        {
            return outputTypeSetting.getExtension();
        }
        
        /**
         * Returns the filename without the attached extension. If it has no
         * extension, just returns the filename
         * 
         * @param filename the original filename
         * @return         the filename without the extension (if there is one)
         */
        private String filenameWithoutExtension(String filename)
        {
            // String.lastIndexOf('.') won't work since the output file may have
            // no extension but a containing folder may
            for(int i = filename.length() - 1; i >= 0; i--)
            {
                char c = filename.charAt(i);
                if(c == '.')
                    return filename.substring(0, i);
                else if(c == '/' || (BuilderUtil.IS_WINDOWS && c == '\\'))
                    return filename;
            }
            return filename;
        }
    }
    
    //--------------------------------------------------------------------------
    // Module selection
    
    private final class ModulesSetting implements ISetting
    {
        private Group fGroup;
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
            
            // Create the group
            fGroup = createGroup(comp, "Included modules", 3, 3);
            
            // Add the label
            fLabel = new Label(fGroup, SWT.LEFT | SWT.WRAP);
            fLabel.setText("Select the &modules or packages to build (dependancies will " +
            		"be built automatically). For an application, it is sufficent " +
            		"to specify only the module containing the main() or winMain() " +
            		"function, for libraries there may be multiple modules or " +
            		"packages to include.");
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
            fList.doFillIntoGrid(fGroup, 3);
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
                modules = config.getAttribute(ATTR_MODULES_LIST, EMPTY_LIST);
            }
            catch(CoreException e) { }
            
            fList.setElements(modules);
        }

        public void performApply(ILaunchConfigurationWorkingCopy config)
        {
            // TODO
            config.setAttribute(ATTR_MODULES_LIST, EMPTY_LIST);
        }

        public void setDefaults(ILaunchConfigurationWorkingCopy config)
        {
            config.setAttribute(ATTR_MODULES_LIST, EMPTY_LIST);
        }

        public String validate()
        {
            List<String> modules = fList.getElementsNoCopy();
            
            if(modules.isEmpty())
                return "Module list is empty";
            
            // TODO validate the individual modules... maybe (maybe instead
            // have a check button, since this can be a lengthy operation?)
            
            return null;
        }
    }
    
    //--------------------------------------------------------------------------
    // Tab
    
    private OutputTypeSetting outputTypeSetting;
    private OutputFileSetting outputFileSetting;
    
    public String getName()
    {
        return "General";
    }
    
    @Override
    protected String getIconPath()
    {
        return "obj16/builders.gif";
    }
    
    @Override
    protected ISetting[] getSettings()
    {
        outputTypeSetting = new OutputTypeSetting();
        outputFileSetting = new OutputFileSetting();
        return new ISetting[]
        {
            new ProjectSetting(),
            new GroupSetting("Output target", 3, 3, new ISetting[]
            {
                outputTypeSetting,
                outputFileSetting,
            }),
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
