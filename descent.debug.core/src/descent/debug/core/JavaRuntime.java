package descent.debug.core;


// TODO JDT UI runtime: stub
public class JavaRuntime {

	public static final String JRELIB_VARIABLE = "JRE_LIB"; //$NON-NLS-1$
	
	/**
	 * Classpath variable name used for the default JRE's library source
	 * (value <code>"JRE_SRC"</code>).
	 */
	public static final String JRESRC_VARIABLE= "JRE_SRC"; //$NON-NLS-1$
	
	/**
	 * Classpath variable name used for the default JRE's library source root
	 * (value <code>"JRE_SRCROOT"</code>).
	 */	
	public static final String JRESRCROOT_VARIABLE= "JRE_SRCROOT"; //$NON-NLS-1$
	
	/**
	 * Attribute key for a classpath attribute referencing a
	 * list of shared libraries that should appear on the
	 * <code>-Djava.library.path</code> system property.
	 * <p>
	 * The factory methods <code>newLibraryPathsAttribute(String[])</code>
	 * and <code>getLibraryPaths(IClasspathAttribute)</code> should be used to
	 * encode and decode the attribute value. 
	 * </p>
	 * <p>
	 * Each string is used to create an <code>IPath</code> using the constructor
	 * <code>Path(String)</code>, and may contain <code>IStringVariable</code>'s.
	 * Variable substitution is performed on the string prior to constructing
	 * a path from the string.
	 * If the resulting <code>IPath</code> is a relative path, it is interpreted
	 * as relative to the workspace location. If the path is absolute, it is 
	 * interpreted as an absolute path in the local file system.
	 * </p>
	 * @since 3.1
	 * @see org.eclipse.jdt.core.IClasspathAttribute
	 */
	public static final String CLASSPATH_ATTR_LIBRARY_PATH_ENTRY =  "descent.ui.CLASSPATH_ATTR_LIBRARY_PATH_ENTRY"; //$NON-NLS-1$
	
	/**
	 * Classpath container used for a project's JRE
	 * (value <code>"org.eclipse.jdt.launching.JRE_CONTAINER"</code>). A
	 * container is resolved in the context of a specific Java project, to one
	 * or more system libraries contained in a JRE. The container can have zero
	 * or two path segments following the container name. When no segments
	 * follow the container name, the workspace default JRE is used to build a
	 * project. Otherwise the segments identify a specific JRE used to build a
	 * project:
	 * <ol>
	 * <li>VM Install Type Identifier - identifies the type of JRE used to build the
	 * 	project. For example, the standard VM.</li>
	 * <li>VM Install Name - a user defined name that identifies that a specific VM
	 * 	of the above kind. For example, <code>IBM 1.3.1</code>. This information is
	 *  shared in a projects classpath file, so teams must agree on JRE naming
	 * 	conventions.</li>
	 * </ol>
	 * <p>
	 * Since 3.2, the path may also identify an execution environment as follows:
	 * <ol>
	 * <li>Execution environment extension point name
	 * (value <code>executionEnvironments</code>)</li>
	 * <li>Identifier of a contributed execution environment</li>
	 * </ol>
	 * </p>
	 * @since 2.0
	 */
	public static final String JRE_CONTAINER = "descent.ui.JRE_CONTAINER"; //$NON-NLS-1$

}
