package descent.internal.debug.core;

import descent.debug.core.AbstractDescentLaunchConfigurationDelegate;
import descent.debug.core.DescentDebugPlugin;

public class DescentLaunchConfigurationDelegate 
        extends AbstractDescentLaunchConfigurationDelegate
{
	protected String getPluginID()
	{
		return DescentDebugPlugin.PLUGIN_ID;
	}
}
