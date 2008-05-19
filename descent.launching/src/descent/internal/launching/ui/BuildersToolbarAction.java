package descent.internal.launching.ui;

import org.eclipse.debug.ui.actions.AbstractLaunchToolbarAction;

import descent.internal.launching.LaunchingPlugin;

public class BuildersToolbarAction extends AbstractLaunchToolbarAction
{
    public BuildersToolbarAction()
    {
        super(LaunchingPlugin.ID_BUILD_GROUP);
    }
}
