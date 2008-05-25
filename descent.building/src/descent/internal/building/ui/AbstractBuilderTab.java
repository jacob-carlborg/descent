package descent.internal.building.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import descent.core.ICompilationUnit;
import descent.core.IJavaElement;
import descent.core.IJavaModel;
import descent.core.IJavaProject;
import descent.core.JavaCore;
import descent.internal.building.BuildingPlugin;
import descent.internal.ui.util.PixelConverter;

// TODO comments
public abstract class AbstractBuilderTab extends AbstractLaunchConfigurationTab
{
    //--------------------------------------------------------------------------
    // Setting interface
    
    protected interface ISetting
    {
        public void addToControl(Composite comp);
        public void initializeFrom(ILaunchConfiguration config);
        public void performApply(ILaunchConfigurationWorkingCopy config);
        public void setDefaults(ILaunchConfigurationWorkingCopy config);
        public void validate();
    }
    
    private final ISetting[] settings = getSettings();
    
    //--------------------------------------------------------------------------
    // Group setting
    
    /**
     * A group that contains one or more sub-settings
     */
    protected final class GroupSetting implements ISetting
    {
        private final String fLabel;
        private final int fWidth;
        private final int fNumColumns;
        private final ISetting[] fChildren;
        
        private Group fGroup;
        
        public GroupSetting(String label, int width, int numColumns,
                ISetting[] subSettings)
        {
            
            fLabel = label;
            fWidth = width;
            fNumColumns = numColumns;
            fChildren = subSettings;
        }

        public void addToControl(Composite comp)
        {
            fGroup = createGroup(comp, fLabel, fWidth, fNumColumns);
            for(ISetting setting : fChildren)
                setting.addToControl(fGroup);
        }

        public void initializeFrom(ILaunchConfiguration config)
        {
            for(ISetting setting : fChildren)
                setting.initializeFrom(config);
        }

        public void performApply(ILaunchConfigurationWorkingCopy config)
        {
            for(ISetting setting : fChildren)
                setting.performApply(config);
        }

        public void setDefaults(ILaunchConfigurationWorkingCopy config)
        {
            for(ISetting setting : fChildren)
                setting.setDefaults(config);
        }

        public void validate()
        {
            for(ISetting setting : fChildren)
            {
                setting.validate();
                if(null != getErrorMessage())
                    return;
            }
        }
    }
    
    //--------------------------------------------------------------------------
    // Superclass implementations
    
    /* (non-Javadoc)
     * @see org.eclipse.debug.ui.ILaunchConfigurationTab#createControl(org.eclipse.swt.widgets.Composite)
     */
    public final void createControl(Composite parent)
    {
        Composite comp = new Composite(parent, SWT.NONE);
        setControl(comp);

        Layout topLayout = getTopLayout();
        comp.setLayout(topLayout);
        for(ISetting setting : settings)
            setting.addToControl(comp);

        Dialog.applyDialogFont(comp);
        validatePage();
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.debug.ui.AbstractLaunchConfigurationTab#isValid(org.eclipse.debug.core.ILaunchConfiguration)
     */
    @Override
    public final boolean isValid(ILaunchConfiguration config)
    {
        return getErrorMessage() == null;
    }
    
    /**
     * Validates the page by setting error messages if necessary.
     */
    protected final void validatePage()
    {
        setErrorMessage(null);
        setMessage(null);
        
        for(ISetting setting : settings)
        {
            setting.validate();
            if(null != getErrorMessage())
                return;
        }
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.debug.ui.ILaunchConfigurationTab#performApply(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
     */
    public final void performApply(ILaunchConfigurationWorkingCopy config)
    {
        for(ISetting setting : settings)
            setting.performApply(config);
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.debug.ui.ILaunchConfigurationTab#initializeFrom(org.eclipse.debug.core.ILaunchConfiguration)
     */
    public final void initializeFrom(ILaunchConfiguration config)
    {
        for(ISetting setting : settings)
            setting.initializeFrom(config);
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.debug.ui.ILaunchConfigurationTab#setDefaults(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
     */
    public void setDefaults(ILaunchConfigurationWorkingCopy config)
    {
        for(ISetting setting : settings)
            setting.setDefaults(config);
    }
    
    //--------------------------------------------------------------------------
    // Abstract methods
    /**
     * Gets the path to the icon for the tab. This is called during construction
     * so should not rely on any instance variables.
     */
    protected abstract String getIconPath();
    
    /**
     * Gets the settings in the order they should be added to the control
     * (which is also the order they will be validated in, etc.)
     */
    protected abstract ISetting[] getSettings();
    
    /**
     * Gets the top layout to be used in the control
     */
    protected abstract Layout getTopLayout();
    
    //--------------------------------------------------------------------------
    // Icon management
    
    private final Image fTabIcon = createImage(getIconPath());
    
    @Override
    public void dispose()
    {
        super.dispose();
        fTabIcon.dispose();
    }

    private static Image createImage(String path)
    {
        return BuildingPlugin.getImageDescriptor(path).createImage();
    }

    @Override
    public Image getImage()
    {
        return fTabIcon;
    }
    
    //--------------------------------------------------------------------------
    // Convenience/utility methods and fields
    
    /**
     * An empty list to use as a default for list-typed constants (there's no
     * similar constant here for the empty string since the empty string is
     * internalized by the JVM).
     */
    protected final List EMPTY_LIST = new ArrayList(0);
    
    /**
     * Creates a group in a grid layout, using columnsUsed columns and having
     * column columns.
     * 
     * @param comp        the composite to create the group under
     * @param text        the group label
     * @param columnsUsed the number of columns the group should take up
     * @param columns     the number of columns the new group will have
     * @return            the new group
     */
    protected Group createGroup(Composite comp, String text, int columnsUsed, 
            int columns)
    {
        Group group = new Group(comp, SWT.NONE);
        group.setText(text);
        
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = columnsUsed;
        group.setLayoutData(gd);
        
        GridLayout layout = new GridLayout();
        layout.numColumns = columns;
        group.setLayout(layout);
        
        return group;
    }
    
    /**
     * Sets width and height hint for the button control.
     * <b>Note:</b> This is a NOP if the button's layout data is not
     * an instance of <code>GridData</code>.
     * 
     * @param button    the button for which to set the dimension hint
     */     
    protected static void setButtonDimensionHint(Button button)
    {
        assert(null != button);
        Object gd= button.getLayoutData();
        if (gd instanceof GridData) {
            ((GridData)gd).widthHint = getButtonWidthHint(button);   
            ((GridData)gd).horizontalAlignment = GridData.FILL;  
        }
    }
    
    /**
     * Returns a width hint for a button control.
     * @param button    the button for which to set the dimension hint
     * @return the width hint
     */
    private static int getButtonWidthHint(Button button)
    {
        button.setFont(JFaceResources.getDialogFont());
        PixelConverter converter= new PixelConverter(button);
        int widthHint= converter.convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH);
        return Math.max(widthHint, button.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
    }
    
    /**
     * Returns the current compilation unit context from which to initialize
     * default settings, or <code>null</code> if none. This is generally the
     * module currently open in the editor.
     * 
     * @return compilation unit context.
     */
    protected static ICompilationUnit getActiveModule()
    {
        IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (null == activeWorkbenchWindow)
            return null;
        
        IWorkbenchPage page = activeWorkbenchWindow.getActivePage();
        if(null == page)
            return null;
        
        IEditorPart part = page.getActiveEditor();
        if(null == part)
            return null;
        
        IEditorInput input = part.getEditorInput();
        IJavaElement element = (IJavaElement) input.getAdapter(IJavaElement.class);
        if(element instanceof ICompilationUnit)
            return (ICompilationUnit) element;
        else
            return null;
    }
    
    /**
     * Returns the project context from which to initialize settings, or
     * <code>null</code> if not one. If there's a module open in the editor,
     * the project it belongs to will be returned, otherwise null will be
     * returned.
     * 
     * @return project context
     */
    protected static IJavaProject getActiveProject()
    {
        ICompilationUnit activeModule = getActiveModule();
        return activeModule == null ? null : activeModule.getJavaProject();
    }
    
    /**
     * Creates a spacing label for a grid data.
     * 
     * @param parent     the composite to place it on
     * @param numColumns the number of columns it should take up
     */
    protected Label createSpacer(Composite parent, int numColumns)
    {
        Label spacer = new Label(parent, SWT.NONE);
        GridData gd = new GridData();
        gd.horizontalAlignment= GridData.BEGINNING;
        gd.grabExcessHorizontalSpace = false;
        gd.horizontalSpan = numColumns;
        gd.horizontalIndent = 0;
        gd.widthHint = 0;
        gd.heightHint = 0;
        spacer.setLayoutData(gd);
        
        return spacer;
    }
    
    /**
     * Convenience method to get access to the java model.
     */
    protected static IJavaModel getJavaModel()
    {
        return JavaCore.create(getWorkspaceRoot());
    }
    
    /**
     * Convenience method to get the workspace root.
     */
    protected static IWorkspaceRoot getWorkspaceRoot()
    {
        return ResourcesPlugin.getWorkspace().getRoot();
    }
}