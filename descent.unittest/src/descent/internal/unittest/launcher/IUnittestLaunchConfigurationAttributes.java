package descent.internal.unittest.launcher;

import descent.internal.unittest.DescentUnittestPlugin;

public interface IUnittestLaunchConfigurationAttributes
{
	/**
	 * The port to connect to.
	 */
	public static final String PORT_ATTR= DescentUnittestPlugin.PLUGIN_ID+".PORT"; //$NON-NLS-1$s
	
	/**
	 * The launch container, or "" iff running all the tests in the project
	 */
	public static final String LAUNCH_CONTAINER_ATTR= DescentUnittestPlugin.PLUGIN_ID+".CONTAINER"; //$NON-NLS-1$
}
