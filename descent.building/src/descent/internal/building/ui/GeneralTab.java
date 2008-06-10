package descent.internal.building.ui;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.dialogs.FilteredItemsSelectionDialog;

import descent.core.ICompilationUnit;
import descent.core.IJavaElement;
import descent.core.IJavaModel;
import descent.core.IJavaProject;
import descent.core.IMethod;
import descent.core.IPackageFragment;
import descent.core.IPackageFragmentRoot;
import descent.core.IParent;
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
        public IJavaProject getJavaProject()
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
        private Button fExecutableRadio;
        private Button fStaticLibRadio;
        
        // Note: Okay to use == for comparing this field
        private String fCurrentSetting;
        
        public void addToControl(Composite comp)
        {   
            Label label = new Label(comp, SWT.LEFT);
            label.setText("Target type:");
            GridData gd = new GridData();
            gd.horizontalSpan = 3;
            label.setLayoutData(gd);
            
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
        }

        public void initializeFrom(ILaunchConfiguration config)
        {
            String outputType = getAttribute(config, ATTR_OUTPUT_TYPE,
                    OUTPUT_TYPE_EXECUTABLE);
            
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

        public boolean isExecutable()
        {
            return fExecutableRadio.getSelection();
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
        private final class ModuleSearchDialog extends FilteredItemsSelectionDialog
        {
            // Note -- these classes are a workaround for the superclass not being
            // able to refresh and generlaly being a bitch of an API to work with.
            private abstract class AbstractModuleFilter extends ItemsFilter
            {   
                public boolean isConsistentItem(Object item)
                {
                    return true;
                }
                
                public boolean matchItem(Object item)
                {
                    return matches(getName(item));
                }
            }
            
            private final class AllModulesFilter extends AbstractModuleFilter
            {
                @Override
                public boolean equalsFilter(ItemsFilter filter)
                {
                    return filter instanceof AllModulesFilter && 
                            super.equalsFilter(filter);
                }
            }
            
            private final class OnlyMainFilter extends AbstractModuleFilter
            {
                @Override
                public boolean matchItem(Object item)
                {
                    return isMainModule(item) && super.matchItem(item);
                }
                
                @Override
                public boolean equalsFilter(ItemsFilter filter)
                {
                    return filter instanceof OnlyMainFilter &&
                            super.equalsFilter(filter);
                }
            }
            
            private IJavaElement[] fElements;
            private boolean fOnlyMain;
            private Button fOnlyMainCheckbox;
            
            public ModuleSearchDialog(Shell shell, boolean onlyMain)
            {
                super(shell);
                fOnlyMain = onlyMain;
                
                final ILabelProvider labelProvider = new JavaElementLabelProvider();
                setTitle("Select included module/package");
                setListLabelProvider(labelProvider);
                setSelectionHistory(new SelectionHistory()
                {
                    @Override
                    protected Object restoreItemFromMemento(IMemento memento)
                    {
                        IJavaElement element = JavaCore.create(memento.getTextData());
                        if(null == element || !element.exists())
                            return null;
                        if(!(element instanceof ICompilationUnit) &&
                           !(element instanceof IPackageFragment))
                            return null;
                        return element;
                    }

                    @Override
                    protected void storeItemToMemento(Object item,
                            IMemento memento)
                    {
                        memento.putTextData(((IJavaElement) item).
                                getHandleIdentifier());
                    }
                });
                setDetailsLabelProvider(new LabelProvider()
                {
                    @Override
                    public Image getImage(Object element)
                    {
                        IPackageFragment pkg = getPackage(element);
                        return null == pkg ? null : labelProvider.getImage(pkg);
                    }

                    @Override
                    public String getText(Object element)
                    {
                        IPackageFragment pkg = getPackage(element);
                        return null == pkg ? "" : labelProvider.getText(pkg);
                    }
                    
                    private IPackageFragment getPackage(Object element)
                    {
                        if(element instanceof ICompilationUnit)
                            return (IPackageFragment) ((ICompilationUnit) element).
                                    getAncestor(IJavaElement.PACKAGE_FRAGMENT);
                        else
                            return null;
                    }
                });
                
                initializeElements();
            }

            @Override
            protected Control createExtendedContentArea(Composite parent)
            {
                Composite comp = new Composite(parent, SWT.NONE);
                GridLayout layout = new GridLayout();
                layout.numColumns = 1;
                comp.setLayout(layout);
                
                fOnlyMainCheckbox = new Button(comp, SWT.CHECK);
                fOnlyMainCheckbox.setText("Show only modules with main function");
                GridData gd = new GridData();
                gd.horizontalSpan = 1;
                fOnlyMainCheckbox.setLayoutData(gd);
                fOnlyMainCheckbox.setSelection(fOnlyMain);
                
                fOnlyMainCheckbox.addSelectionListener(new SelectionAdapter()
                {
                    @Override
                    public void widgetSelected(SelectionEvent e)
                    {
                        fOnlyMain = fOnlyMainCheckbox.getSelection();
                        applyFilter();
                    }
                });
                
                return comp;
            }

            @Override
            protected ItemsFilter createFilter()
            {
                return fOnlyMain ? new OnlyMainFilter() : new AllModulesFilter();
            }

            @Override
            protected void fillContentProvider(
                    AbstractContentProvider contentProvider,
                    ItemsFilter itemsFilter, IProgressMonitor pm)
                    throws CoreException
            {
                for(IJavaElement element : fElements)
                    contentProvider.add(element, itemsFilter);
            }

            @Override
            protected IDialogSettings getDialogSettings()
            {
                return BuildingPlugin.getDefault().getDialogSettingsSection(
                        MODULES_SEARCH_DIALOG_SETTINGS_ID);
            }

            @Override
            public String getElementName(Object element)
            {
                return getName(element);
            }

            @Override
            protected Comparator getItemsComparator()
            {
                return new Comparator()
                {
                    public int compare(Object o1, Object o2)
                    {
                        if(o1.getClass().equals(o2.getClass()))
                        {
                            String name1 = getName(o1);
                            String name2 = getName(o2);
                            return name1.compareTo(name2);
                        }
                        else
                        {
                            // Always show modules before packages
                            return o1 instanceof ICompilationUnit ? -1 : 1;
                        }
                    }
                };
            }

            @Override
            protected IStatus validateItem(Object item)
            {
                // Anything that'd made it this far is valid
                return Status.OK_STATUS;
            }
            
            private String getName(Object element)
            {
                // PERHAPS why do modules have a ".d" there?
                return ((IJavaElement) element).getElementName();
            }
            
            private void initializeElements()
            {
                try
                {
                    // Get the seed projects
                    IJavaModel model = JavaCore.create(ResourcesPlugin.getWorkspace().getRoot());
                    IJavaProject project = projectSetting.getJavaProject();
                    IJavaProject[] projects;
                    if((null == project) || !project.exists())
                    {
                        projects = model.getJavaProjects();
                    }
                    else
                    {
                        String[] requiredProjects = project.getRequiredProjectNames();
                        ArrayList<IJavaProject> projectsList = new ArrayList<IJavaProject>
                                (requiredProjects.length + 1);
                        projectsList.add(project);
                        for(String requiredProjectName : requiredProjects)
                        {
                            IJavaProject requiredProject = model.getJavaProject(requiredProjectName);
                            if(requiredProject.exists() && requiredProject.isOpen())
                                projectsList.add(requiredProject);
                        }
                        projects = projectsList.toArray(new IJavaProject[projectsList.size()]);
                    }
                    
                    Set<IJavaElement> elements = new HashSet<IJavaElement>();
                    for(IJavaProject current : projects)
                        for(IPackageFragmentRoot root : current.getAllPackageFragmentRoots())
                            if(!root.isArchive())
                                collectRecursive(elements, root);
                    fElements = elements.toArray(new IJavaElement[elements.size()]);
                }
                catch(JavaModelException e)
                {
                    BuildingPlugin.log(e);
                    fElements = new IJavaElement[] { };
                }
            }
            
            private void collectRecursive(Set<IJavaElement> elements, IParent parent)
            {
                try
                {
                    for(IJavaElement child : parent.getChildren())
                    {
                        if(child instanceof IPackageFragmentRoot)
                        {
                            collectRecursive(elements, (IPackageFragmentRoot) child);
                        }
                        else if(child instanceof IPackageFragment)
                        {
                            IPackageFragment pkg = (IPackageFragment) child;
                            boolean hasChildren = pkg.containsJavaResources();
                            if(!pkg.isDefaultPackage() && hasChildren)
                                elements.add(child);
                            if(hasChildren)
                                collectRecursive(elements, pkg);
                        }
                        else if(child instanceof ICompilationUnit)
                        {
                            elements.add(child);
                        }
                    }
                }
                catch(JavaModelException e)
                {
                    // Guess we can't collect from this one... :-(
                }
            }
        }
        
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
                    updateLaunchConfigurationDialog();
                }
            }
            
            // Create the group
            comp = createGroup(comp, "Included modules", 3, 3, GridData.FILL_HORIZONTAL);
            
            // Add the help labels
            // PERHAPS is this the best way to convey this information? This has
            // fairly complicated semantics for a new user
            newHelpLabel(comp, "- For an executable, choose the module containing main()");
            newHelpLabel(comp, "- For a library, choose all exported modules");
            newHelpLabel(comp, "- Dependancies will be built automatically");
            createSpacer(comp, 3);
            
            // Create the list
            ModulesListAdapter adapter = new ModulesListAdapter();
            fList = new ListDialogField(adapter, buttonLabels, 
                    new JavaElementLabelProvider());
            fList.setDialogFieldListener(adapter);
            fList.setViewerSorter(new JavaElementSorter());
            fList.setRemoveButtonIndex(IDX_REMOVE);
            
            fList.doFillIntoGrid(comp, 3);
        }
        
        private void newHelpLabel(Composite comp, String text)
        {
            Label label = new Label(comp, SWT.LEFT);
            label.setText(text);
            GridData gd = new GridData();
            gd.horizontalSpan = 3;
            label.setLayoutData(gd);
        }
        
        private void performAdd()
        {
            IJavaElement entry = showElementSearchDialog(null);
            if(null != entry)
                fList.addElement(entry);
        }
        
        private void performEdit(ListDialogField field)
        {
            IJavaElement seed = (IJavaElement) field.getElement(0);
            if(null != seed)
            {
                IJavaElement entry = showElementSearchDialog(seed);
                if(null != entry)
                    fList.replaceElement(seed, entry);
            }
        }
        
        private IJavaElement showElementSearchDialog(IJavaElement seed)
        {
            ModuleSearchDialog dialog = new ModuleSearchDialog(getShell(),
                    outputTypeSetting.isExecutable() && 
                    fList.getElementsNoCopy().isEmpty());
            dialog.setInitialPattern(seed == null ? "**" : seed.getElementName());
            int status = dialog.open();
            if(status != IDialogConstants.OK_ID)
                return null;
            
            Object[] result = dialog.getResult();
            if(null == result || 0 == result.length)
                return null;
            
            return (IJavaElement) result[0];
        }

        public void initializeFrom(ILaunchConfiguration config)
        {
            List<String> handles = getAttribute(config, ATTR_MODULES_LIST, EMPTY_LIST);
            List<IJavaElement> elements = 
                new ArrayList<IJavaElement>(handles.size());
            for(String handle : handles)
            {
                IJavaElement element = JavaCore.create(handle);
                if(null != element)
                    elements.add(element);
            }
            fList.setElements(elements);
        }

        public void performApply(ILaunchConfigurationWorkingCopy config)
        {
            List<IJavaElement> elements = fList.getElements();
            List<String> handles = new ArrayList<String>(elements.size());
            for(IJavaElement element : elements)
                handles.add(element.getHandleIdentifier());
            config.setAttribute(ATTR_MODULES_LIST, handles);
        }

        public void setDefaults(ILaunchConfigurationWorkingCopy config)
        {
            ICompilationUnit module = getActiveModule();
            if(isMainModule(module))
            {
                String handle = module.getHandleIdentifier();
                List<String> list = new ArrayList<String>(1);
                list.add(handle);
                config.setAttribute(ATTR_MODULES_LIST, list);
            }
            else
            {
                config.setAttribute(ATTR_MODULES_LIST, EMPTY_LIST);
            }
        }
        
        private boolean isMainModule(Object element)
        {
            try
            {
                if(!(element instanceof ICompilationUnit))
                    return false;
                ICompilationUnit module = (ICompilationUnit) element;
                
                if(null == module || !module.exists())
                    return false;
                
                for(IJavaElement child : module.getChildren())
                    if(child instanceof IMethod)
                        if(((IMethod) child).isMainMethod())
                            return true;
            }
            catch(JavaModelException e) { }
            return false;
        }

        public String validate()
        {
            List<IJavaElement> elements = fList.getElementsNoCopy();
            
            if(elements.isEmpty())
                return "Must specify at least one included module";
            
            for(IJavaElement element : elements)
            {
                if(!(element instanceof ICompilationUnit) &&
                   !(element instanceof IPackageFragment)) 
                    return String.format("Element %1s is not a module or package",
                            element.getElementName());
                
                if(!element.exists())
                    return String.format("Element %1$s does not exist",
                            element.getElementName());
            }
            
            return null;
        }
    }
    
    //--------------------------------------------------------------------------
    // Constants
    
    private static final String MODULES_SEARCH_DIALOG_SETTINGS_ID =
        BuildingPlugin.PLUGIN_ID + "MODULES_SEARCH_DIALOG_SETTINGS";
    
    //--------------------------------------------------------------------------
    // Tab
    
    private OutputTypeSetting outputTypeSetting;
    private OutputFileSetting outputFileSetting;
    private ProjectSetting projectSetting;
    
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
        projectSetting = new ProjectSetting();
        return new ISetting[]
        {
            projectSetting,
            new GroupSetting("Output target", 3, 3, GridData.FILL_HORIZONTAL,
                new ISetting[]
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
