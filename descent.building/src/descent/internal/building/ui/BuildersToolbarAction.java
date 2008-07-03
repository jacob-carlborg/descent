package descent.internal.building.ui;

import org.eclipse.debug.ui.actions.AbstractLaunchToolbarAction;

import descent.internal.building.BuildingPlugin;

public class BuildersToolbarAction extends AbstractLaunchToolbarAction
{
    public BuildersToolbarAction()
    {
        super(BuildingPlugin.ID_BUILD_GROUP);
    }
}
