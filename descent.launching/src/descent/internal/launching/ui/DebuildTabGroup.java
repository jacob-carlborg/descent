package descent.internal.launching.ui;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;

public class DebuildTabGroup extends AbstractLaunchConfigurationTabGroup
{
    public void createTabs(ILaunchConfigurationDialog dialog, String mode)
    {
        ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[]
        {
             new BuildParametersTab(),
        };
        setTabs(tabs);
    }
}
