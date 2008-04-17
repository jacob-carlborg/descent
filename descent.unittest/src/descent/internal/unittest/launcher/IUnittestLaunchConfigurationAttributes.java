package descent.internal.unittest.launcher;

import descent.internal.unittest.DescentUnittestPlugin;

public interface IUnittestLaunchConfigurationAttributes
{
	/**
	 * The port to connect to.
	 */
	public static final String PORT_ATTR= DescentUnittestPlugin.PLUGIN_ID+".PORT"; //$NON-NLS-1$s
	
	/**
	 * The launch container, or "" iff running all the tests in the project. The
	 * container should be the handle to a project, package, or source folder.
	 */
	public static final String LAUNCH_CONTAINER_ATTR= DescentUnittestPlugin.PLUGIN_ID+".CONTAINER"; //$NON-NLS-1$
	
	/**
	 * If the launch container defines a package and this value is the string
	 * "true" (without the quotes), then subpackages of the given package should
	 * be included when searching for tests.
	 */
	public static final String INCLUDE_SUBPACKAGES_ATTR= DescentUnittestPlugin.PLUGIN_ID+".INCLUDE_SUBPACKAGES"; //$NON-NLS-1$
}
