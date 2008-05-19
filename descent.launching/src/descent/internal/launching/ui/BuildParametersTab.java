package descent.internal.launching.ui;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class BuildParametersTab extends AbstractBuilderTab
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

        Dialog.applyDialogFont(comp);
        validatePage();
    }

    public String getName()
    {
        return "Build Parameters";
    }
    
    @Override
    protected String getIconPath()
    {
        return "obj16/builders.gif";
    }
    
    //--------------------------------------------------------------------------
    // Initialization

    public void initializeFrom(ILaunchConfiguration config)
    {
        // TODO
    }

    //--------------------------------------------------------------------------
    // Validation
    
    private void validatePage()
    {
        setErrorMessage(null);
        setMessage(null);
        
        // TODO
    }

    //--------------------------------------------------------------------------
    // Application

    public void performApply(ILaunchConfigurationWorkingCopy config)
    {
        // TODO
    }

    //--------------------------------------------------------------------------
    // Defaults
    public void setDefaults(ILaunchConfigurationWorkingCopy config)
    {
        // TODO
    }
}
