package descent.debug.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IPersistableSourceLocator;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;

import descent.core.IJavaProject;
import descent.core.JavaCore;
import descent.debug.core.model.IDebugger;
import descent.debug.core.utils.ArgumentUtils;

public abstract class AbstractDescentLaunchConfigurationDelegate extends LaunchConfigurationDelegate {
	
	/**
	 * The project containing the programs file being launched
	 */
	private IProject project;
	/**
	 * A list of prequisite projects ordered by their build order.
	 */
	private List orderedProjects;

	abstract public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor)
			throws CoreException;

	/**
	 * Returns the working directory specified by the given launch
	 * configuration, or <code>null</code> if none.
	 * 
	 * @deprecated Should use getWorkingDirectory()
	 * @param configuration
	 *            launch configuration
	 * @return the working directory specified by the given launch
	 *         configuration, or <code>null</code> if none
	 * @exception CoreException
	 *                if unable to retrieve the attribute
	 */
	public File getWorkingDir(ILaunchConfiguration configuration) throws CoreException {
		return getWorkingDirectory(configuration);
	}

	/**
	 * Returns the working directory specified by the given launch
	 * configuration, or <code>null</code> if none.
	 * 
	 * @param configuration
	 *            launch configuration
	 * @return the working directory specified by the given launch
	 *         configuration, or <code>null</code> if none
	 * @exception CoreException
	 *                if unable to retrieve the attribute
	 */
	public File getWorkingDirectory(ILaunchConfiguration configuration) throws CoreException {
		return verifyWorkingDirectory(configuration);
	}

	/**
	 * Expands and returns the working directory attribute of the given launch
	 * configuration. Returns <code>null</code> if a working directory is not
	 * specified. If specified, the working is verified to point to an existing
	 * directory in the local file system.
	 * 
	 * @param configuration launch configuration
	 * @return an absolute path to a directory in the local file system, or
	 * <code>null</code> if unspecified
	 * @throws CoreException if unable to retrieve the associated launch
	 * configuration attribute, if unable to resolve any variables, or if the
	 * resolved location does not point to an existing directory in the local
	 * file system
	 */
	protected IPath getWorkingDirectoryPath(ILaunchConfiguration config) throws CoreException {
		String location = config.getAttribute(IDescentLaunchConfigurationConstants.ATTR_WORKING_DIRECTORY, (String)null);
		if (location != null) {
			String expandedLocation = ArgumentUtils.getStringVariableManager().performStringSubstitution(location);
			if (expandedLocation.length() > 0) {
				return new Path(expandedLocation);
			}
		}
		return null;
	}

	/**
	 * Throws a core exception with an error status object built from the given
	 * message, lower level exception, and error code.
	 * 
	 * @param message
	 *            the status message
	 * @param exception
	 *            lower level exception associated with the error, or
	 *            <code>null</code> if none
	 * @param code
	 *            error code
	 */
	protected void abort(String message, Throwable exception, int code) throws CoreException {
		MultiStatus status = new MultiStatus(getPluginID(), code, message, exception);
		status.add(new Status(IStatus.ERROR, getPluginID(), code, exception == null ? "" : exception.getLocalizedMessage(), //$NON-NLS-1$
				exception));
		throw new CoreException(status);
	}

	protected void cancel(String message, int code) throws CoreException {
		throw new CoreException(new Status(IStatus.OK, getPluginID(), code, message, null));
	}

	abstract protected String getPluginID();

	public static IJavaProject getJavaProject(ILaunchConfiguration configuration) throws CoreException {
		String projectName = getProjectName(configuration);
		if (projectName != null) {
			projectName = projectName.trim();
			if (projectName.length() > 0) {
				IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
				IJavaProject javaProject = JavaCore.create(project);
				if (javaProject != null && javaProject.exists()) {
					return javaProject;
				}
			}
		}
		return null;
	}

	public static String getProjectName(ILaunchConfiguration configuration) throws CoreException {
		return configuration.getAttribute(IDescentLaunchConfigurationConstants.ATTR_PROJECT_NAME, (String)null);
	}

	public static String getProgramName(ILaunchConfiguration configuration) throws CoreException {
		return configuration.getAttribute(IDescentLaunchConfigurationConstants.ATTR_PROGRAM_NAME, (String)null);
	}

	public static IPath getProgramPath(ILaunchConfiguration configuration) throws CoreException {
		String path = getProgramName(configuration);
		if (path == null) {
			return null;
		}
		return new Path(path);
	}
	
	/**
	 * @param launch
	 * @param config
	 * @throws CoreException
	 * @deprecated
	 */
	protected void setSourceLocator(ILaunch launch, ILaunchConfiguration config) throws CoreException {
		setDefaultSourceLocator(launch, config);
	}

	/**
	 * Assigns a default source locator to the given launch if a source locator
	 * has not yet been assigned to it, and the associated launch configuration
	 * does not specify a source locator.
	 * 
	 * @param launch
	 *            launch object
	 * @param configuration
	 *            configuration being launched
	 * @exception CoreException
	 *                if unable to set the source locator
	 */
	protected void setDefaultSourceLocator(ILaunch launch, ILaunchConfiguration configuration) throws CoreException {
		//  set default source locator if none specified
		if (launch.getSourceLocator() == null) {
			IPersistableSourceLocator sourceLocator;
			String id = configuration.getAttribute(ILaunchConfiguration.ATTR_SOURCE_LOCATOR_ID, (String)null);
			if (id == null) {
				IJavaProject cProject = getJavaProject(configuration);
				if (cProject == null) {
					abort("Project does not exist", null, //$NON-NLS-1$
							IDescentLaunchConfigurationConstants.ERR_NOT_A_JAVA_PROJECT);
				}
				sourceLocator = null;
				/* TODO launch source locator
				sourceLocator = CDebugUIPlugin.createDefaultSourceLocator();
				sourceLocator.initializeDefaults(configuration);
				*/
			} else {
				sourceLocator = DebugPlugin.getDefault().getLaunchManager().newSourceLocator(id);
				String memento = configuration.getAttribute(ILaunchConfiguration.ATTR_SOURCE_LOCATOR_MEMENTO, (String)null);
				if (memento == null) {
					sourceLocator.initializeDefaults(configuration);
				} else {
					sourceLocator.initializeFromMemento(memento);
				}
			}
			launch.setSourceLocator(sourceLocator);
		}
	}

	/**
	 * Returns the program arguments as a String.
	 * 
	 * @return the program arguments as a String
	 */
	public String getProgramArguments(ILaunchConfiguration config) throws CoreException {
		return ArgumentUtils.getProgramArguments(config);
	}

	/**
	 * Returns the program arguments as an array of individual arguments.
	 * 
	 * @return the program arguments as an array of individual arguments
	 */
	public String[] getProgramArgumentsArray(ILaunchConfiguration config) throws CoreException {
		return ArgumentUtils.getProgramArgumentsArray(config);
	}

	protected String renderProcessLabel(String commandLine) {
		String format = "{0} ({1})"; //$NON-NLS-1$
		String timestamp = DateFormat.getInstance().format(new Date(System.currentTimeMillis()));
		return MessageFormat.format(format, new String[]{commandLine, timestamp});
	}

	protected IJavaProject verifyJavaProject(ILaunchConfiguration config) throws CoreException {
		String name = getProjectName(config);
		if (name == null) {
			abort("Java Project not specified", null, //$NON-NLS-1$
					IDescentLaunchConfigurationConstants.ERR_UNSPECIFIED_PROJECT);
		}
		IJavaProject cproject = getJavaProject(config);
		if (cproject == null) {
			IProject proj = ResourcesPlugin.getWorkspace().getRoot().getProject(name);
			if (!proj.exists()) {
				abort(
						"Project " + name + " does not exist", null, //$NON-NLS-1$
						IDescentLaunchConfigurationConstants.ERR_NOT_A_JAVA_PROJECT);
			} else if (!proj.isOpen()) {
				abort("Project " + name + " is closed", null, //$NON-NLS-1$
						IDescentLaunchConfigurationConstants.ERR_NOT_A_JAVA_PROJECT);
			}
			abort("Project is not a D project", null, //$NON-NLS-1$
					IDescentLaunchConfigurationConstants.ERR_NOT_A_JAVA_PROJECT);
		}
		return cproject;
	}

	protected IPath verifyProgramPath(ILaunchConfiguration config) throws CoreException {
		IJavaProject cproject = verifyJavaProject(config);
		IPath programPath = getProgramPath(config);
		if (programPath == null || programPath.isEmpty()) {
			return null;
		}
		if (!programPath.isAbsolute()) {
			IFile wsProgramPath = cproject.getProject().getFile(programPath);
			programPath = wsProgramPath.getLocation();
		}
		if (!programPath.toFile().exists()) {
			abort(
					"Program file does not exist", //$NON-NLS-1$
					new FileNotFoundException(programPath.toOSString() + " not found"), //$NON-NLS-1$
					IDescentLaunchConfigurationConstants.ERR_PROGRAM_NOT_EXIST);
		}
		return programPath;
	}
	
	protected IDebugger verifyDebugger() throws CoreException {
		IDebuggerDescriptor debugger = DescentDebugPlugin.getCurrentDebugger();
		if (debugger == null) {
			abort("Debugger must be defined in Window -> Preferences -> D -> Debug.", null,
					IDescentLaunchConfigurationConstants.ERR_DEBUGGER_NOT_DEFINED);
		}
		return debugger.createDebugger();
	}
	
	protected String verifyDebuggerPath() throws CoreException {
		String ddbgPath = DescentDebugPlugin.getDefault().getPreferenceStore().getString(IDescentLaunchingPreferenceConstants.DEBUGGER_PATH);
		if (ddbgPath == null || ddbgPath.trim().length() == 0) {
			abort("Debugger executable must be defined in Window -> Preferences -> D -> Debug.", null,
					IDescentLaunchConfigurationConstants.ERR_DEBUGGER_EXECUTABLE_NOT_DEFINED);
		}
		if (!new File(ddbgPath).exists()) {
			abort("Debugger executable file does not exist", 
					new FileNotFoundException(ddbgPath + " not found"),
					IDescentLaunchConfigurationConstants.ERR_DEBUGGER_EXECUTABLE_NOT_EXIST);
		}
		return ddbgPath;
	}
	
	/**
	 * @param config
	 * @return
	 * @throws CoreException
	 * @deprecated Use <code>verifyProgramFile</code> instead.
	 */
	protected IFile getProgramFile(ILaunchConfiguration config) throws CoreException {
		IJavaProject cproject = verifyJavaProject(config);
		String fileName = getProgramName(config);
		if (fileName == null) {
			abort("Program file not specified", null, //$NON-NLS-1$
					IDescentLaunchConfigurationConstants.ERR_UNSPECIFIED_PROGRAM);
		}

		IFile programPath = ((IProject)cproject.getResource()).getFile(fileName);
		if (programPath == null || !programPath.exists() || !programPath.getLocation().toFile().exists()) {
			abort(
					"Program file does not exist", //$NON-NLS-1$
					new FileNotFoundException(programPath.getLocation().toOSString() + " not found"), //$NON-NLS-1$
					IDescentLaunchConfigurationConstants.ERR_PROGRAM_NOT_EXIST);
		}
		return programPath;
	}

	protected IPath verifyProgramFile(ILaunchConfiguration config) throws CoreException {
		return getProgramFile(config).getLocation();
	}

	/**
	 * Verifies the working directory specified by the given launch
	 * configuration exists, and returns the working directory, or
	 * <code>null</code> if none is specified.
	 * 
	 * @param configuration
	 *            launch configuration
	 * @return the working directory specified by the given launch
	 *         configuration, or <code>null</code> if none
	 * @exception CoreException
	 *                if unable to retrieve the attribute
	 */
	public File verifyWorkingDirectory(ILaunchConfiguration configuration) throws CoreException {
		IPath path = getWorkingDirectoryPath(configuration);
		if (path == null) {
			// default working dir is the project if this config has a project
			IJavaProject cp = getJavaProject(configuration);
			if (cp != null) {
				IProject p = cp.getProject();
				return p.getLocation().toFile();
			}
		} else {
			if (path.isAbsolute()) {
				File dir = new File(path.toOSString());
				if (dir.isDirectory()) {
					return dir;
				}
				abort(
						"Working directory does not exist", //$NON-NLS-1$
						new FileNotFoundException("The working directory " + path.toOSString() + " does not exist."), //$NON-NLS-1$
						IDescentLaunchConfigurationConstants.ERR_WORKING_DIRECTORY_DOES_NOT_EXIST);
			} else {
				IResource res = ResourcesPlugin.getWorkspace().getRoot().findMember(path);
				if (res instanceof IContainer && res.exists()) {
					return res.getLocation().toFile();
				}
				abort(
						"Working directory does not exist", //$NON-NLS-1$
						new FileNotFoundException("The working directory " + path.toOSString() + " does not exist."), //$NON-NLS-1$
						IDescentLaunchConfigurationConstants.ERR_WORKING_DIRECTORY_DOES_NOT_EXIST);
			}
		}
		return null;
	}

	/**
	 * Recursively creates a set of projects referenced by the current project
	 * 
	 * @param proj
	 *            The current project
	 * @param referencedProjSet
	 *            A set of referenced projects
	 * @throws CoreException
	 *             if an error occurs while getting referenced projects from the
	 *             current project
	 */
	private void getReferencedProjectSet(IProject proj, HashSet referencedProjSet) throws CoreException {
		IProject[] projects = proj.getReferencedProjects();
		for (int i = 0; i < projects.length; i++) {
			IProject refProject = projects[i];
			if (refProject.exists() && !referencedProjSet.contains(refProject)) {
				referencedProjSet.add(refProject);
				getReferencedProjectSet(refProject, referencedProjSet);
			}
		}

	}

	/**
	 * creates a list of project ordered by their build order from an unordered
	 * list of projects.
	 * 
	 * @param resourceCollection
	 *            The list of projects to sort.
	 * @return A new list of projects, ordered by build order.
	 */
	private List getBuildOrder(List resourceCollection) {
		String[] orderedNames = ResourcesPlugin.getWorkspace().getDescription().getBuildOrder();
		if (orderedNames != null) {
			List orderedProjs = new ArrayList(resourceCollection.size());
			//Projects may not be in the build order but should be built if
			// selected
			List unorderedProjects = new ArrayList(resourceCollection.size());
			unorderedProjects.addAll(resourceCollection);

			for (int i = 0; i < orderedNames.length; i++) {
				String projectName = orderedNames[i];
				for (int j = 0; j < resourceCollection.size(); j++) {
					IProject proj = (IProject)resourceCollection.get(j);
					if (proj.getName().equals(projectName)) {
						orderedProjs.add(proj);
						unorderedProjects.remove(proj);
						break;
					}
				}
			}
			//Add anything not specified before we return
			orderedProjs.addAll(unorderedProjects);
			return orderedProjs;
		}

		// Try the project prerequisite order then
		IProject[] projects = new IProject[resourceCollection.size()];
		projects = (IProject[])resourceCollection.toArray(projects);
		IWorkspace.ProjectOrder po = ResourcesPlugin.getWorkspace().computeProjectOrder(projects);
		ArrayList orderedProjs = new ArrayList();
		orderedProjs.addAll(Arrays.asList(po.projects));
		return orderedProjs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.ILaunchConfigurationDelegate2#preLaunchCheck(org.eclipse.debug.core.ILaunchConfiguration,
	 *      java.lang.String, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public boolean preLaunchCheck(ILaunchConfiguration configuration, String mode, IProgressMonitor monitor) throws CoreException {
		// build project list
		if (monitor != null) {
			monitor.subTask("Building prerequisite project list"); //$NON-NLS-1$
		}
		orderedProjects = null;
		IJavaProject cProject = getJavaProject(configuration);
		if (cProject != null) {
			project = cProject.getProject();
			HashSet projectSet = new HashSet();
			getReferencedProjectSet(project, projectSet);
			orderedProjects = getBuildOrder(new ArrayList(projectSet));
		}
		// do generic launch checks
		return super.preLaunchCheck(configuration, mode, monitor);
	}

	/**
	 * @param config
	 * @return
	 * @throws CoreException
	 */
	protected Properties getEnvironmentAsProperty(ILaunchConfiguration config) throws CoreException {
		String[] envp = getEnvironment(config);
		Properties p = new Properties( );
		for( int i = 0; i < envp.length; i++ ) {
			int idx = envp[i].indexOf('=');
			if (idx != -1) {
				String key = envp[i].substring(0, idx);
				String value = envp[i].substring(idx + 1);
				p.setProperty(key, value);
			} else {
				p.setProperty(envp[i], ""); //$NON-NLS-1$
			}
		}
		return p;
	}

	/**
	 * Return the save environment variables in the configuration. The array
	 * does not include the default environment of the target. array[n] :
	 * name=value
	 * @throws CoreException
	 */
	protected String[] getEnvironment(ILaunchConfiguration config) throws CoreException {
		String[] array = DebugPlugin.getDefault().getLaunchManager().getEnvironment(config);
		if (array == null) {
			return new String[0];
		}
		return array;
	}

}
