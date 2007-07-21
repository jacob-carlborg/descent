package descent.debug.core;


/**
 * Constant definitions for Java launch configurations.
 * <p>
 * Constant definitions only; not to be implemented.
 * </p>
 * @since 2.0
 */
public interface IDescentLaunchConfigurationConstants {
	
	/**
	 * Identifier for the Local Java Application launch configuration type
	 * (value <code>"descent.debug.core.localJavaApplication"</code>).
	 */
	public static final String ID_D_APPLICATION = DescentDebugPlugin.PLUGIN_ID + ".localDApplication"; //$NON-NLS-1$
	
	/**
	 * Unique identifier for the ddbg debug model (value 
	 * <code>descent.debug.core.model</code>).
	 */
	public static final String ID_D_DEBUG_MODEL = DescentDebugPlugin.PLUGIN_ID + ".model"; //$NON-NLS-1$
	
	/**
	 * Launch configuration attribute key. The value is a string specifying a
	 * path to the working directory to use when launching a local VM.
	 * When specified as an absolute path, the path represents a path in the local
	 * file system. When specified as a full path, the path represents a workspace
	 * relative path. When unspecified, the working directory defaults to the project
	 * associated with a launch configuration. When no project is associated with a
	 * launch configuration, the working directory is inherited from the current
	 * process.
	 */
	public static final String ATTR_WORKING_DIRECTORY = DescentDebugPlugin.PLUGIN_ID + ".WORKING_DIRECTORY";	 //$NON-NLS-1$
	
	/**
	 * Launch configuration attribute key. The value is a string specifying
	 * program arguments for a Java launch configuration, as they should appear
	 * on the command line.
	 */
	public static final String ATTR_PROGRAM_ARGUMENTS = DescentDebugPlugin.PLUGIN_ID + ".PROGRAM_ARGUMENTS"; //$NON-NLS-1$
	
	/**
	 * Launch configuration attribute key. The value is a name of
	 * a Java project associated with a Java launch configuration.
	 */
	public static final String ATTR_PROJECT_NAME = DescentDebugPlugin.PLUGIN_ID + ".PROJECT_ATTR"; //$NON-NLS-1$
	
	/**
	 * Launch configuration attribute key. The value is a path to the executable to launch.
	 */
	public static final String ATTR_PROGRAM_NAME = DescentDebugPlugin.PLUGIN_ID + ".PROGRAM_NAME";	 //$NON-NLS-1$
	
	/**
	 * Status code indicating the project associated with
	 * a launch configuration is not a Java project.
	 */
	public static final int ERR_NOT_A_JAVA_PROJECT = 100;
	
	/**
	 * Status code indicating a launch configuration does not
	 * specify a project when a project is required.
	 */
	public static final int ERR_UNSPECIFIED_PROJECT = 101;	
	
	/**
	 * Status code indicating that the project referenced by a launch configuration
	 * is closed.
	 * 
	 * @since 3.0
	 */
	public static final int ERR_PROJECT_CLOSED = 102;	
	
	/**
	 * Status code indicating that the program referenced by a launch configuration
	 * does not exist.
	 * 
	 * @since 3.0
	 */
	public static final int ERR_PROGRAM_NOT_EXIST = 103;
	
	/**
	 * Status code indicating that the program referenced by a launch configuration
	 * was unspecified.
	 * 
	 * @since 3.0
	 */
	public static final int ERR_UNSPECIFIED_PROGRAM = 104;
	
	/**
	 * Status code indicating that the working directory referenced by a launch configuration
	 * does not exist.
	 * 
	 * @since 3.0
	 */
	public static final int ERR_WORKING_DIRECTORY_DOES_NOT_EXIST = 105;
	
	/**
	 * Status code indicating that the Eclipse runtime does not support
	 * launching a program with a working directory. This feature is only
	 * available if Eclipse is run on a 1.3 runtime or higher.
	 * <p>
	 * A status handler may be registered for this error condition,
	 * and should return a Boolean indicating whether the program
	 * should be relaunched with the default working directory.
	 * </p>
	 */
	public static final int ERR_WORKING_DIRECTORY_NOT_SUPPORTED = 106;
	
	/**
	 * Status code indicating that the debugger
	 * is not defined.
	 */
	public static final int ERR_DEBUGGER_NOT_DEFINED = 107;
	
	/**
	 * Status code indicating that the path for the debugger executable
	 * is not defined.
	 */
	public static final int ERR_DEBUGGER_EXECUTABLE_NOT_DEFINED = 108;
	
	/**
	 * Status code indicating that the path for the ddbg executable
	 * is invalid.
	 */
	public static final int ERR_DEBUGGER_EXECUTABLE_NOT_EXIST = 109;
	
	/**
	 * Status code indicating an unexpected internal error.
	 */
	public static final int ERR_INTERNAL_ERROR = 150;

}
