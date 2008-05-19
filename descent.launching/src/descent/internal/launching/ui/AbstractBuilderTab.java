package descent.internal.launching.ui;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import descent.core.ICompilationUnit;
import descent.core.IJavaElement;
import descent.core.IJavaProject;
import descent.internal.launching.LaunchingPlugin;

/**
 * This class mostly exists to add some utility methods that are shared between
 * the builder tabs and to let tabs basically ignore all the annoying icon
 * stuff.
 * 
 * Overrides isValid and assumes that validation is marked by setting the page
 * error message to a non-null value. So do all your validation at your leisure
 * setting the error message to null when the page is valid and to non-null
 * when it is not.
 * 
 * @author Robert Fraser
 */
public abstract class AbstractBuilderTab extends AbstractLaunchConfigurationTab
{
    //--------------------------------------------------------------------------
    // Abstract methods
    /**
     * Gets the path to the icon for the tab. This is called during construction
     * so should not rely on any instance variables.
     */
    protected abstract String getIconPath();
    
    //--------------------------------------------------------------------------
    // Convenience/utility methods
    
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
     * Returns the current compilation unit context from which to initialize
     * default settings, or <code>null</code> if none. This is generally the
     * module currently open in the editor.
     * 
     * @return compilation unit context.
     */
    protected ICompilationUnit getActiveModule()
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
    protected IJavaProject getActiveProject()
    {
        ICompilationUnit activeModule = getActiveModule();
        return activeModule == null ? null : activeModule.getJavaProject();
    }
    
    //--------------------------------------------------------------------------
    // Defaults for superclass stuff
    @Override
    public boolean isValid(ILaunchConfiguration config)
    {
        return getErrorMessage() == null;
    }
    
    //-------------------------------------------------------------------------0
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
        return LaunchingPlugin.getImageDescriptor(path).createImage();
    }

    @Override
    public Image getImage()
    {
        return fTabIcon;
    }
}
