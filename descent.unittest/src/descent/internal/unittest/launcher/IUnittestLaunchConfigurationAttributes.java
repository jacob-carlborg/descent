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
	
	/**
     * If this attribute is defined, it sets whether stack tracing should be enabled
     * in the fluted executable. It must be one of:
     * 
     * {@link #STACKTRACING_ENABLED} - Stack tracing is enabled
     * {@link #STACKTRACING_DISABLED_IN_DEBUG_MODE} - Stack tracing is disabled
     *     if this launch configuration is launched in debug mode
     * {@link #STACKTRACING_DISABLED} - Stack tracing is disabled
     */
	public static final String ENABLE_STACKTACING_ATTR= DescentUnittestPlugin.PLUGIN_ID+".ENABLE_STACKTRACING"; //$NON-NLS-1$
	
	/**
	 * Constant used for {@link #ENABLE_STACKTACING_ATTR}. Stack tracing is enabled.
	 * 
	 * @see #ENABLE_STACKTACING_ATTR
	 */
	public static final String STACKTRACING_ENABLED= "enabled"; //$NON-NLS-1$
	
	/**
     * Constant used for {@link #ENABLE_STACKTACING_ATTR}. Stack tracing is enabled
     * except in debug mode.
     * 
     * @see #ENABLE_STACKTACING_ATTR
     */
	public static final String STACKTRACING_DISABLED_IN_DEBUG_MODE= "disabled_in_debug_mode"; //$NON-NLS-1$
	
	/**
     * Constant used for {@link #ENABLE_STACKTACING_ATTR}. Stack tracing is disabled.
     * 
     * @see #ENABLE_STACKTACING_ATTR
     */
	public static final String STACKTRACING_DISABLED= "disabled"; //$NON-NLS-1$
}
