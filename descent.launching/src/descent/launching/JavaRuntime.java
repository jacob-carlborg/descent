package descent.launching;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.variables.VariablesPlugin;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.ibm.icu.text.MessageFormat;

import descent.core.IClasspathEntry;
import descent.core.IJavaProject;
import descent.core.JavaCore;
import descent.internal.launching.CompositeId;
import descent.internal.launching.JREContainerInitializer;
import descent.internal.launching.LaunchingMessages;
import descent.internal.launching.LaunchingPlugin;
import descent.internal.launching.ListenerList;
import descent.internal.launching.RuntimeClasspathEntry;
import descent.internal.launching.RuntimeClasspathEntryResolver;
import descent.internal.launching.VMDefinitionsContainer;
import descent.internal.launching.dmd.DmdCompilerType;
import descent.internal.launching.environments.EnvironmentsManager;
import descent.launching.environments.IExecutionEnvironment;
import descent.launching.environments.IExecutionEnvironmentsManager;

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
	 * Simple identifier constant (value <code>"runtimeClasspathEntryResolvers"</code>) for the
	 * runtime classpath entry resolvers extension point.
	 * 
	 * @since 2.0
	 */
	public static final String EXTENSION_POINT_RUNTIME_CLASSPATH_ENTRY_RESOLVERS= "runtimeClasspathEntryResolvers";	 //$NON-NLS-1$	
	
	/**
	 * Simple identifier constant (value <code>"classpathProviders"</code>) for the
	 * runtime classpath providers extension point.
	 * 
	 * @since 2.0
	 */
	public static final String EXTENSION_POINT_RUNTIME_CLASSPATH_PROVIDERS= "classpathProviders";	 //$NON-NLS-1$		
	
	/**
	 * Simple identifier constant (value <code>"executionEnvironments"</code>) for the
	 * execution environments extension point.
	 * 
	 * @since 3.2
	 */
	public static final String EXTENSION_POINT_EXECUTION_ENVIRONMENTS= "executionEnvironments";	 //$NON-NLS-1$
	
	/**
	 * Simple identifier constant (value <code>"vmInstalls"</code>) for the
	 * VM installs extension point.
	 * 
	 * @since 3.2
	 */
	public static final String EXTENSION_POINT_VM_INSTALLS = "vmInstalls";	 //$NON-NLS-1$
	
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
	 * @see descent.core.IClasspathAttribute
	 */
	public static final String CLASSPATH_ATTR_LIBRARY_PATH_ENTRY =  "descent.ui.CLASSPATH_ATTR_LIBRARY_PATH_ENTRY"; //$NON-NLS-1$
	
	/**
	 * Classpath container used for a project's JRE
	 * (value <code>"descent.launching.JRE_CONTAINER"</code>). A
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
	public static final String JRE_CONTAINER = LaunchingPlugin.getUniqueIdentifier() + ".JRE_CONTAINER"; //$NON-NLS-1$
	
	/**
	 * Preference key for the String of XML that defines all installed VMs.
	 * 
	 * @since 2.1
	 */
	public static final String PREF_VM_XML = LaunchingPlugin.getUniqueIdentifier() + ".PREF_VM_XML"; //$NON-NLS-1$
	
	/**
	 * Preference key for launch/connect timeout. VM Runners should honor this timeout
	 * value when attempting to launch and connect to a debuggable VM. The value is
	 * an int, indicating a number of milliseconds.
	 * 
	 * @since 2.0
	 */
	public static final String PREF_CONNECT_TIMEOUT = LaunchingPlugin.getUniqueIdentifier() + ".PREF_CONNECT_TIMEOUT"; //$NON-NLS-1$
	
	// lock for vm initialization
	private static Object fgVMLock = new Object();
	private static boolean fgInitializingVMs = false;
	
	private static IVMInstallType[] fgVMTypes= null;
	private static String fgDefaultVMId= null;
	private static String fgDefaultVMConnectorId = null;
	
	/**
	 * Resolvers keyed by variable name, container id,
	 * and runtime classpath entry id.
	 */
	private static Map fgVariableResolvers = null;
	private static Map fgContainerResolvers = null;
	private static Map fgRuntimeClasspathEntryResolvers = null;
	
	/**
	 * Path providers keyed by id
	 */
	private static Map fgPathProviders = null;
	
	/**
	 * VM change listeners
	 */
	private static ListenerList fgVMListeners = new ListenerList(5);
	
	/**
	 * Cache of already resolved projects in container entries. Used to avoid
	 * cycles in project dependencies when resolving classpath container entries.
	 * Counters used to know when entering/exiting to clear cache
	 */
	private static ThreadLocal fgProjects = new ThreadLocal(); // Lists
	private static ThreadLocal fgEntryCount = new ThreadLocal(); // Integers
	
    /**
     *  Set of IDs of VMs contributed via vmInstalls extension point.
     */
    private static Set fgContributedVMs = new HashSet();
	
	/**
	 * Adds the given listener to the list of registered VM install changed
	 * listeners. Has no effect if an identical listener is already registered.
	 * 
	 * @param listener the listener to add
	 * @since 2.0
	 */
	public static void addVMInstallChangedListener(IVMInstallChangedListener listener) {
		fgVMListeners.add(listener);
	}
	
	/**
	 * Removes the given listener from the list of registered VM install changed
	 * listeners. Has no effect if an identical listener is not already registered.
	 * 
	 * @param listener the listener to remove
	 * @since 2.0
	 */
	public static void removeVMInstallChangedListener(IVMInstallChangedListener listener) {
		fgVMListeners.remove(listener);
	}	
	
	private static void notifyDefaultVMChanged(IVMInstall previous, IVMInstall current) {
		Object[] listeners = fgVMListeners.getListeners();
		for (int i = 0; i < listeners.length; i++) {
			IVMInstallChangedListener listener = (IVMInstallChangedListener)listeners[i];
			listener.defaultVMInstallChanged(previous, current);
		}
	}
	
	/**
	 * Notifies all VM install changed listeners of the given property change.
	 * 
	 * @param event event describing the change.
	 * @since 2.0
	 */
	public static void fireVMChanged(PropertyChangeEvent event) {
		Object[] listeners = fgVMListeners.getListeners();
		for (int i = 0; i < listeners.length; i++) {
			IVMInstallChangedListener listener = (IVMInstallChangedListener)listeners[i];
			listener.vmChanged(event);
		}		
	}
	
	/**
	 * Notifies all VM install changed listeners of the VM addition
	 * 
	 * @param vm the VM that has been added
	 * @since 2.0
	 */
	public static void fireVMAdded(IVMInstall vm) {
		if (!fgInitializingVMs) {
			Object[] listeners = fgVMListeners.getListeners();
			for (int i = 0; i < listeners.length; i++) {
				IVMInstallChangedListener listener = (IVMInstallChangedListener)listeners[i];
				listener.vmAdded(vm);
			}
		}
	}	
	
	/**
	 * Notifies all VM install changed listeners of the VM removal
	 * 
	 * @param vm the VM that has been removed
	 * @since 2.0
	 */
	public static void fireVMRemoved(IVMInstall vm) {
		Object[] listeners = fgVMListeners.getListeners();
		for (int i = 0; i < listeners.length; i++) {
			IVMInstallChangedListener listener = (IVMInstallChangedListener)listeners[i];
			listener.vmRemoved(vm);
		}		
	}
	
	/**
	 * Returns the list of registered VM types. VM types are registered via
	 * <code>"descent.launching.vmTypes"</code> extension point.
	 * Returns an empty list if there are no registered VM types.
	 * 
	 * @return the list of registered VM types
	 */
	public static IVMInstallType[] getVMInstallTypes() {
		initializeVMs();
		return fgVMTypes; 
	}
	
	private static String getDefaultVMId() {
		initializeVMs();
		return fgDefaultVMId;
	}
	
	private static String getDefaultVMConnectorId() {
		initializeVMs();
		return fgDefaultVMConnectorId;
	}
	
	/**
	 * Perform VM type and VM install initialization. Does not hold locks
	 * while performing change notification.
	 * 
	 * @since 3.2
	 */
	private static void initializeVMs() {
		VMDefinitionsContainer vmDefs = null;
		boolean setPref = false;
		boolean updateCompliance = false;
		synchronized (fgVMLock) {
			if (fgVMTypes == null) {
				try {
					fgInitializingVMs = true;
					// 1. load VM type extensions
					initializeVMTypeExtensions();
					try {
						vmDefs = new VMDefinitionsContainer();
						// 2. add persisted VMs
						setPref = addPersistedVMs(vmDefs);
						
						// 3. if there are none, detect the eclipse runtime
						/*
						if (vmDefs.getValidVMList().isEmpty()) {
							// calling out to detectEclipseRuntime() could allow clients to change
							// VM settings (i.e. call back into change VM settings).
							VMListener listener = new VMListener();
							addVMInstallChangedListener(listener);
							setPref = true;
							VMStandin runtime = detectEclipseRuntime();
							removeVMInstallChangedListener(listener);
							if (!listener.isChanged()) {
								if (runtime != null) {
									updateCompliance = true;
									vmDefs.addVM(runtime);
									vmDefs.setDefaultVMInstallCompositeID(getCompositeIdFromVM(runtime));
								}
							} else {
								// VMs were changed - reflect current settings
								addPersistedVMs(vmDefs);
								vmDefs.setDefaultVMInstallCompositeID(fgDefaultVMId);
								updateCompliance = fgDefaultVMId != null;
							}
						}
						*/
						// 4. load contributed VM installs
						addVMExtensions(vmDefs);
						// 5. verify default VM is valid
						String defId = vmDefs.getDefaultVMInstallCompositeID();
						boolean validDef = false;
						if (defId != null) {
							Iterator iterator = vmDefs.getValidVMList().iterator();
							while (iterator.hasNext()) {
								IVMInstall vm = (IVMInstall) iterator.next();
								if (getCompositeIdFromVM(vm).equals(defId)) {
									validDef = true;
									break;
								}
							}
						}
						if (!validDef) {
							// use the first as the default
							setPref = true;
							List list = vmDefs.getValidVMList();
							if (!list.isEmpty()) {
								IVMInstall vm = (IVMInstall) list.get(0);
								vmDefs.setDefaultVMInstallCompositeID(getCompositeIdFromVM(vm));
							}
						}
						fgDefaultVMId = vmDefs.getDefaultVMInstallCompositeID();
						fgDefaultVMConnectorId = vmDefs.getDefaultVMInstallConnectorTypeID();
						
						// Create the underlying VMs for each valid VM
						List vmList = vmDefs.getValidVMList();
						Iterator vmListIterator = vmList.iterator();
						while (vmListIterator.hasNext()) {
							VMStandin vmStandin = (VMStandin) vmListIterator.next();
							vmStandin.convertToRealVM();
						}						
						

					} catch (IOException e) {
						LaunchingPlugin.log(e);
					}
				} finally {
					fgInitializingVMs = false;
				}
			}
		}
		if (vmDefs != null) {
			// notify of initial VMs for backwards compatibility
			IVMInstallType[] installTypes = getVMInstallTypes();
			for (int i = 0; i < installTypes.length; i++) {
				IVMInstallType type = installTypes[i];
				IVMInstall[] installs = type.getVMInstalls();
				for (int j = 0; j < installs.length; j++) {
					fireVMAdded(installs[j]);
				}
			}
			
			// save settings if required
			if (setPref) {
				try {
					String xml = vmDefs.getAsXML();
					LaunchingPlugin.getDefault().getPluginPreferences().setValue(PREF_VM_XML, xml);
				} catch (ParserConfigurationException e) {
					LaunchingPlugin.log(e);
				} catch (IOException e) {
					LaunchingPlugin.log(e);
				} catch (TransformerException e) {
					LaunchingPlugin.log(e);
				}
				
			}
			
			// update compliance if required
			if (updateCompliance) {
				updateCompliance(getDefaultVMInstall());
			}
		}
	}
	
	/**
	 * Initializes vm type extensions.
	 */
	private static void initializeVMTypeExtensions() {
		IExtensionPoint extensionPoint= Platform.getExtensionRegistry().getExtensionPoint(LaunchingPlugin.ID_PLUGIN, "vmInstallTypes"); //$NON-NLS-1$
		IConfigurationElement[] configs= extensionPoint.getConfigurationElements(); 
		MultiStatus status= new MultiStatus(LaunchingPlugin.getUniqueIdentifier(), IStatus.OK, LaunchingMessages.JavaRuntime_exceptionOccurred, null); 
		fgVMTypes= new IVMInstallType[configs.length];

		for (int i= 0; i < configs.length; i++) {
			try {
				IVMInstallType vmType= (IVMInstallType)configs[i].createExecutableExtension("class"); //$NON-NLS-1$
				fgVMTypes[i]= vmType;
			} catch (CoreException e) {
				status.add(e.getStatus());
			}
		}
		if (!status.isOK()) {
			//only happens on a CoreException
			LaunchingPlugin.log(status);
			//cleanup null entries in fgVMTypes
			List temp= new ArrayList(fgVMTypes.length);
			for (int i = 0; i < fgVMTypes.length; i++) {
				if(fgVMTypes[i] != null) {
					temp.add(fgVMTypes[i]);
				}
				fgVMTypes= new IVMInstallType[temp.size()];
				fgVMTypes= (IVMInstallType[])temp.toArray(fgVMTypes);
			}
		}
	}
	
	/**
	 * Returns the VM install type with the given unique id. 
	 * @param id the VM install type unique id
	 * @return	The VM install type for the given id, or <code>null</code> if no
	 * 			VM install type with the given id is registered.
	 */
	public static IVMInstallType getVMInstallType(String id) {
		IVMInstallType[] vmTypes= getVMInstallTypes();
		for (int i= 0; i < vmTypes.length; i++) {
			if (vmTypes[i].getId().equals(id)) {
				return vmTypes[i];
			}
		}
		return null;
	}
	
	/**
	 * Returns the VM assigned to build the given Java project.
	 * The project must exist. The VM assigned to a project is
	 * determined from its build path.
	 * 
	 * @param project the project to retrieve the VM from
	 * @return the VM instance that is assigned to build the given Java project
	 * 		   Returns <code>null</code> if no VM is referenced on the project's build path.
	 * @throws CoreException if unable to determine the project's VM install
	 */
	public static IVMInstall getVMInstall(IJavaProject project) throws CoreException {
		// check the classpath
		IVMInstall vm = null;
		IClasspathEntry[] classpath = project.getRawClasspath();
		IRuntimeClasspathEntryResolver resolver = null;
		for (int i = 0; i < classpath.length; i++) {
			IClasspathEntry entry = classpath[i];
			switch (entry.getEntryKind()) {
				case IClasspathEntry.CPE_VARIABLE:
					resolver = getVariableResolver(entry.getPath().segment(0));
					if (resolver != null) {
						vm = resolver.resolveVMInstall(entry);
					}
					break;
				case IClasspathEntry.CPE_CONTAINER:
					resolver = getContainerResolver(entry.getPath().segment(0));
					if (resolver != null) {
						vm = resolver.resolveVMInstall(entry);
					}
					break;
			}
			if (vm != null) {
				return vm;
			}
		}
		return null;
	}
	
	/**
	 * This method loads installed JREs based an existing user preference
	 * or old vm configurations file. The VMs found in the preference
	 * or vm configurations file are added to the given VM definitions container.
	 * 
	 * Returns whether the user preferences should be set - i.e. if it was
	 * not already set when initialized.
	 */
	private static boolean addPersistedVMs(VMDefinitionsContainer vmDefs) throws IOException {
		// Try retrieving the VM preferences from the preference store
		String vmXMLString = getPreferences().getString(PREF_VM_XML);
		
		// If the preference was found, load VMs from it into memory
		if (vmXMLString.length() > 0) {
			try {
				ByteArrayInputStream inputStream = new ByteArrayInputStream(vmXMLString.getBytes("UTF8")); //$NON-NLS-1$
				VMDefinitionsContainer.parseXMLIntoContainer(inputStream, vmDefs);
				return false;
			} catch (IOException ioe) {
				LaunchingPlugin.log(ioe);
			}			
		} else {			
			// Otherwise, look for the old file that previously held the VM definitions
			IPath stateLocation= LaunchingPlugin.getDefault().getStateLocation();
			IPath stateFile= stateLocation.append("vmConfiguration.xml"); //$NON-NLS-1$
			File file = new File(stateFile.toOSString());
			
			if (file.exists()) {        
				// If file exists, load VM definitions from it into memory and write the definitions to
				// the preference store WITHOUT triggering any processing of the new value
				FileInputStream fileInputStream = new FileInputStream(file);
				VMDefinitionsContainer.parseXMLIntoContainer(fileInputStream, vmDefs);			
			}		
		}
		return true;
	}
	
	/**
	 * Returns the preference store for the launching plug-in.
	 * 
	 * @return the preference store for the launching plug-in
	 * @since 2.0
	 */
	public static Preferences getPreferences() {
		return LaunchingPlugin.getDefault().getPluginPreferences();
	}
	
	/**
	 * Loads contributed VM installs
	 * @since 3.2
	 */
	private static void addVMExtensions(VMDefinitionsContainer vmDefs) {
		IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(LaunchingPlugin.ID_PLUGIN, JavaRuntime.EXTENSION_POINT_VM_INSTALLS);
		IConfigurationElement[] configs= extensionPoint.getConfigurationElements();
		for (int i = 0; i < configs.length; i++) {
			IConfigurationElement element = configs[i];
			try {
				if ("vmInstall".equals(element.getName())) { //$NON-NLS-1$
					String vmType = element.getAttribute("vmInstallType"); //$NON-NLS-1$
					if (vmType == null) {
						abort(MessageFormat.format("Missing required vmInstallType attribute for vmInstall contributed by {0}", //$NON-NLS-1$
								new String[]{element.getContributor().getName()}), null);
					}
					String id = element.getAttribute("id"); //$NON-NLS-1$
					if (id == null) {
						abort(MessageFormat.format("Missing required id attribute for vmInstall contributed by {0}", //$NON-NLS-1$
								new String[]{element.getContributor().getName()}), null);
					}
					IVMInstallType installType = getVMInstallType(vmType);
					if (installType == null) {
						abort(MessageFormat.format("vmInstall {0} contributed by {1} references undefined VM install type {2}", //$NON-NLS-1$
								new String[]{id, element.getContributor().getName(), vmType}), null);
					}
					IVMInstall install = installType.findVMInstall(id);
					if (install == null) {
						// only load/create if first time we've seen this VM install
						String name = element.getAttribute("name"); //$NON-NLS-1$
						if (name == null) {
							abort(MessageFormat.format("vmInstall {0} contributed by {1} missing required attribute name", //$NON-NLS-1$
									new String[]{id, element.getContributor().getName()}), null);
						}
						String home = element.getAttribute("home"); //$NON-NLS-1$
						if (home == null) {
							abort(MessageFormat.format("vmInstall {0} contributed by {1} missing required attribute home", //$NON-NLS-1$
									new String[]{id, element.getContributor().getName()}), null);
						}		
						String javadoc = element.getAttribute("javadocURL"); //$NON-NLS-1$
						String vmArgs = element.getAttribute("vmArgs"); //$NON-NLS-1$
						VMStandin standin = new VMStandin(installType, id);
						standin.setName(name);
						home = substitute(home);
						File homeDir = new File(home);
                        if (homeDir.exists()) {
                            try {
                            	// adjust for relative path names
                                home = homeDir.getCanonicalPath();
                                homeDir = new File(home);
                            } catch (IOException e) {
                            }
                        }
                        IStatus status = installType.validateInstallLocation(homeDir);
                        if (!status.isOK()) {
                        	abort(MessageFormat.format("Illegal install location {0} for vmInstall {1} contributed by {2}: {3}", //$NON-NLS-1$
                        			new String[]{home, id, element.getContributor().getName(), status.getMessage()}), null);
                        }
                        standin.setInstallLocation(homeDir);
						if (javadoc != null) {
							try {
								standin.setJavadocLocation(new URL(javadoc));
							} catch (MalformedURLException e) {
								abort(MessageFormat.format("Illegal javadocURL attribute for vmInstall {0} contributed by {1}", //$NON-NLS-1$
										new String[]{id, element.getContributor().getName()}), e);
							}
						}
                        IConfigurationElement[] libraries = element.getChildren("library"); //$NON-NLS-1$
                        LibraryLocation[] locations = null;
                        if (libraries.length > 0) {
                            locations = new LibraryLocation[libraries.length];
                            for (int j = 0; j < libraries.length; j++) {
                                IConfigurationElement library = libraries[j];
                                String libPathStr = library.getAttribute("path"); //$NON-NLS-1$
                                if (libPathStr == null) {
                                    abort(MessageFormat.format("library for vmInstall {0} contributed by {1} missing required attribute libPath", //$NON-NLS-1$
                                            new String[]{id, element.getContributor().getName()}), null);
                                }
                                String sourcePathStr = library.getAttribute("sourcePath"); //$NON-NLS-1$
                                String packageRootStr = library.getAttribute("packageRootPath"); //$NON-NLS-1$
                                String javadocOverride = library.getAttribute("javadocURL"); //$NON-NLS-1$
                                URL url = null;
                                if (javadocOverride != null) {
                                    try {
                                        url = new URL(javadocOverride);
                                    } catch (MalformedURLException e) {
                                        abort(MessageFormat.format("Illegal javadocURL attribute specified for library {0} for vmInstall {1} contributed by {2}" //$NON-NLS-1$
                                                ,new String[]{libPathStr, id, element.getContributor().getName()}), e);
                                    }
                                }
                                IPath homePath = new Path(home);
                                IPath libPath = homePath.append(substitute(libPathStr));
                                IPath sourcePath = Path.EMPTY;
                                if (sourcePathStr != null) {
                                    sourcePath = homePath.append(substitute(sourcePathStr));
                                }
                                IPath packageRootPath = Path.EMPTY;
                                if (packageRootStr != null) {
                                    packageRootPath = new Path(substitute(packageRootStr));
                                }
                                locations[j] = new LibraryLocation(libPath, sourcePath, packageRootPath, url);
                            }
                        }
                        standin.setLibraryLocations(locations);
                        vmDefs.addVM(standin);
					}
                    fgContributedVMs.add(id);
				} else {
					abort(MessageFormat.format("Illegal element {0} in vmInstalls extension contributed by {1}", //$NON-NLS-1$
							new String[]{element.getName(), element.getContributor().getName()}), null);
				}
			} catch (CoreException e) {
				LaunchingPlugin.log(e);
			}
		}
	}
	
	/**
	 * Throws a core exception with an internal error status.
	 * 
	 * @param message the status message
	 * @param exception lower level exception associated with the
	 *  error, or <code>null</code> if none
	 */
	private static void abort(String message, Throwable exception) throws CoreException {
		abort(message, IJavaLaunchConfigurationConstants.ERR_INTERNAL_ERROR, exception);
	}	
		
		
	/**
	 * Throws a core exception with an internal error status.
	 * 
	 * @param message the status message
	 * @param code status code
	 * @param exception lower level exception associated with the
	 * 
	 *  error, or <code>null</code> if none
	 */
	private static void abort(String message, int code, Throwable exception) throws CoreException {
		throw new CoreException(new Status(IStatus.ERROR, LaunchingPlugin.getUniqueIdentifier(), code, message, exception));
	}
	
	/**
     * Performs string substitution on the given expression.
     * 
     * @param expression
     * @return expression after string substitution 
     * @throws CoreException
     * @since 3.2
     */
    private static String substitute(String expression) throws CoreException {
        return VariablesPlugin.getDefault().getStringVariableManager().performStringSubstitution(expression);
    }
    
    /**
	 * Return the default VM set with <code>setDefaultVM()</code>.
	 * @return	Returns the default VM. May return <code>null</code> when no default
	 * 			VM was set or when the default VM has been disposed.
	 */
	public static IVMInstall getDefaultVMInstall() {
		IVMInstall install= getVMFromCompositeId(getDefaultVMId());
		if (install != null && install.getInstallLocation().exists()) {
			return install;
		}
		// if the default JRE goes missing, re-detect
		if (install != null) {
			install.getVMInstallType().disposeVMInstall(install.getId());
		}
		synchronized (fgVMLock) {
			fgDefaultVMId = null;
			fgVMTypes = null;
			initializeVMs();
		}
		return getVMFromCompositeId(getDefaultVMId());
	}
	
	/**
	 * Return the VM corresponding to the specified composite Id.  The id uniquely
	 * identifies a VM across all vm types.  
	 * 
	 * @param idString the composite id that specifies an instance of IVMInstall
	 * 
	 * @since 2.1
	 */
	public static IVMInstall getVMFromCompositeId(String idString) {
		if (idString == null || idString.length() == 0) {
			return null;
		}
		CompositeId id= CompositeId.fromString(idString);
		if (id.getPartCount() == 2) {
			IVMInstallType vmType= getVMInstallType(id.get(0));
			if (vmType != null) {
				return vmType.findVMInstall(id.get(1));
			}
		}
		return null;
	}
	
	/**
	 * Update compiler compliance settings based on the given vm.
	 * 
	 * @param vm
	 */
	private static void updateCompliance(IVMInstall vm) {
        String javaVersion = vm.getJavaVersion();
        if (javaVersion != null && javaVersion.startsWith(JavaCore.VERSION_1_x)) {
            Hashtable defaultOptions = JavaCore.getDefaultOptions();
            Hashtable options = JavaCore.getOptions();
            boolean isDefault =
            	equals(JavaCore.COMPILER_COMPLIANCE, defaultOptions, options) &&
            	equals(JavaCore.COMPILER_SOURCE, defaultOptions, options) &&
            	equals(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, defaultOptions, options) &&
            	equals(JavaCore.COMPILER_PB_ASSERT_IDENTIFIER, defaultOptions, options) &&
            	equals(JavaCore.COMPILER_PB_ENUM_IDENTIFIER, defaultOptions, options);
            // only update the compliance settings if they are default settings, otherwise the
            // settings have already been modified by a tool or user
            if (isDefault) {
                options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_x);
                options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_x);
                options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_x);
                options.put(JavaCore.COMPILER_PB_ASSERT_IDENTIFIER, JavaCore.ERROR);
                options.put(JavaCore.COMPILER_PB_ENUM_IDENTIFIER, JavaCore.ERROR);
                JavaCore.setOptions(options);
            }
        }
	}
	
	private static boolean equals(String optionName, Map defaultOptions, Map options) {
		return defaultOptions.get(optionName).equals(options.get(optionName));
	}
	
	/** 
	 * Returns a String that uniquely identifies the specified VM across all VM types.
	 * 
	 * @param vm the instance of IVMInstallType to be identified
	 * 
	 * @since 2.1
	 */
	public static String getCompositeIdFromVM(IVMInstall vm) {
		if (vm == null) {
			return null;
		}
		IVMInstallType vmType= vm.getVMInstallType();
		String typeID= vmType.getId();
		CompositeId id= new CompositeId(new String[] { typeID, vm.getId() });
		return id.toString();
	}
	
	/**
	 * Evaluates library locations for a IVMInstall. If no library locations are set on the install, a default
	 * location is evaluated and checked if it exists.
	 * @return library locations with paths that exist or are empty
	 * @since 2.0
	 */
	public static LibraryLocation[] getLibraryLocations(IVMInstall vm)  {
		IPath[] libraryPaths;
		IPath[] sourcePaths;
		IPath[] sourceRootPaths;
		URL[] javadocLocations;
		LibraryLocation[] locations= vm.getLibraryLocations();
		if (locations == null) {
            URL defJavaDocLocation = vm.getJavadocLocation(); 
			LibraryLocation[] dflts= vm.getVMInstallType().getDefaultLibraryLocations(vm.getInstallLocation());
			libraryPaths = new IPath[dflts.length];
			sourcePaths = new IPath[dflts.length];
			sourceRootPaths = new IPath[dflts.length];
			javadocLocations= new URL[dflts.length];
			for (int i = 0; i < dflts.length; i++) {
				libraryPaths[i]= dflts[i].getSystemLibraryPath();
                if (defJavaDocLocation == null) {
                    javadocLocations[i]= dflts[i].getJavadocLocation();
                } else {
                    javadocLocations[i]= defJavaDocLocation;
                }
                // TODO changed from isFile to isDirectory, double check this
				if (!libraryPaths[i].toFile().isDirectory()) {
					libraryPaths[i]= Path.EMPTY;
				}
				
				sourcePaths[i]= dflts[i].getSystemLibrarySourcePath();
				// TODO changed from isFile to isDirectory, double check this
				if (sourcePaths[i].toFile().isDirectory()) {
					sourceRootPaths[i]= dflts[i].getPackageRootPath();
				} else {
					sourcePaths[i]= Path.EMPTY;
					sourceRootPaths[i]= Path.EMPTY;
				}
			}
		} else {
			libraryPaths = new IPath[locations.length];
			sourcePaths = new IPath[locations.length];
			sourceRootPaths = new IPath[locations.length];
			javadocLocations= new URL[locations.length];
			for (int i = 0; i < locations.length; i++) {			
				libraryPaths[i]= locations[i].getSystemLibraryPath();
				sourcePaths[i]= locations[i].getSystemLibrarySourcePath();
				sourceRootPaths[i]= locations[i].getPackageRootPath();
				javadocLocations[i]= locations[i].getJavadocLocation();
			}
		}
		locations = new LibraryLocation[sourcePaths.length];
		for (int i = 0; i < sourcePaths.length; i++) {
			locations[i] = new LibraryLocation(libraryPaths[i], sourcePaths[i], sourceRootPaths[i], javadocLocations[i]);
		}
		return locations;
	}
	
	/**
	 * Returns a new runtime classpath entry for the given archive.
	 * 
	 * @param resource archive resource
	 * @return runtime classpath entry
	 * @since 2.0
	 */
	public static IRuntimeClasspathEntry newArchiveRuntimeClasspathEntry(IResource resource) {
		IClasspathEntry cpe = JavaCore.newLibraryEntry(resource.getFullPath(), null, null);
		return newRuntimeClasspathEntry(cpe);
	}
	
	/**
	 * Returns a new runtime classpath entry for the given archive (possibly
	 * external).
	 * 
	 * @param path absolute path to an archive
	 * @return runtime classpath entry
	 * @since 2.0
	 */
	public static IRuntimeClasspathEntry newArchiveRuntimeClasspathEntry(IPath path) {
		IClasspathEntry cpe = JavaCore.newLibraryEntry(path, null, null);
		return newRuntimeClasspathEntry(cpe);
	}
	
	/**
	 * Returns a runtime classpath entry constructed from the given memento.
	 * 
	 * @param memento a memento for a runtime classpath entry
	 * @return runtime classpath entry
	 * @exception CoreException if unable to construct a runtime classpath entry
	 * @since 2.0
	 */
	public static IRuntimeClasspathEntry newRuntimeClasspathEntry(String memento) throws CoreException {
		try {
			Element root = null;
			DocumentBuilder parser = LaunchingPlugin.getParser();
			StringReader reader = new StringReader(memento);
			InputSource source = new InputSource(reader);
			root = parser.parse(source).getDocumentElement();
												
			String id = root.getAttribute("id"); //$NON-NLS-1$
			if (id == null || id.length() == 0) {
				// assume an old format
				return new RuntimeClasspathEntry(root);
			}
			// get the extension & create a new one
			IRuntimeClasspathEntry2 entry = LaunchingPlugin.getDefault().newRuntimeClasspathEntry(id);
			NodeList list = root.getChildNodes();
			for (int i = 0; i < list.getLength(); i++) {
				Node node = list.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element)node;
					if ("memento".equals(element.getNodeName())) { //$NON-NLS-1$
						entry.initializeFrom(element);
					}
				}
			}
			return entry;
		} catch (SAXException e) {
			abort(LaunchingMessages.JavaRuntime_31, e); 
		} catch (IOException e) {
			abort(LaunchingMessages.JavaRuntime_32, e); 
		}
		return null;
	}
	
	/**
	 * Returns a runtime classpath entry that corresponds to the given
	 * classpath entry. The classpath entry may not be of type <code>CPE_SOURCE</code>
	 * or <code>CPE_CONTAINER</code>.
	 * 
	 * @param entry a classpath entry
	 * @return runtime classpath entry
	 * @since 2.0
	 */
	private static IRuntimeClasspathEntry newRuntimeClasspathEntry(IClasspathEntry entry) {
		return new RuntimeClasspathEntry(entry);
	}
	
	/**
     * Returns whether the VM install with the specified id was contributed via
     * the vmInstalls extension point.
     * 
     * @param id vm id
     * @return whether the vm install was contributed via extension point
     * @since 3.2
     */
    public static boolean isContributedVMInstall(String id) {
        getVMInstallTypes(); // ensure VMs are initialized
        return fgContributedVMs.contains(id);
    }
    
    /**
	 * Saves the preferences for the launching plug-in.
	 * 
	 * @since 2.0
	 */
	public static void savePreferences() {
		LaunchingPlugin.getDefault().savePluginPreferences();
	}
	
	/**
	 * Returns the execution environments manager.
	 * 
	 * @return execution environments manager
	 * @since 3.2
	 */
	public static IExecutionEnvironmentsManager getExecutionEnvironmentsManager() {
		return EnvironmentsManager.getDefault();
	}
	
	/**
	 * Returns a path for the JRE classpath container identifying the 
	 * specified VM install by type and name.
	 * 
	 * @param vm vm install
	 * @return classpath container path
	 * @since 3.2
	 */
	public static IPath newJREContainerPath(IVMInstall vm) {
		return newJREContainerPath(vm.getVMInstallType().getId(), vm.getName());
	}
	
	/**
	 * Returns a path for the JRE classpath container identifying the 
	 * specified VM install by type and name.
	 * 
	 * @param typeId vm install type identifier
	 * @param name vm install name
	 * @return classpath container path
	 * @since 3.2
	 */	
	public static IPath newJREContainerPath(String typeId, String name) {
		IPath path = newDefaultJREContainerPath();
		path = path.append(typeId);
		path = path.append(name);
		return path;		
	}
	
	/**
	 * Returns a path for the JRE classpath container identifying the 
	 * specified execution environment.
	 * 
	 * @param environment execution environment
	 * @return classpath container path
	 * @since 3.2
	 */
	public static IPath newJREContainerPath(IExecutionEnvironment environment) {
		IPath path = newDefaultJREContainerPath(); 
		path = path.append(DmdCompilerType.ID);
		path = path.append(JREContainerInitializer.encodeEnvironmentId(environment.getId()));
		return path;
	}
	
	/**
	 * Returns a path for the JRE classpath container identifying the 
	 * default VM install.
	 * 
	 * @return classpath container path
	 * @since 3.2
	 */	
	public static IPath newDefaultJREContainerPath() {
		return new Path(JRE_CONTAINER);
	}
	
	/**
	 * Saves the VM configuration information to the preferences. This includes
	 * the following information:
	 * <ul>
	 * <li>The list of all defined IVMInstall instances.</li>
	 * <li>The default VM</li>
	 * <ul>
	 * This state will be read again upon first access to VM
	 * configuration information.
	 */
	public static void saveVMConfiguration() throws CoreException {
		if (fgVMTypes == null) {
			// if the VM types have not been instantiated, there can be no changes.
			return;
		}
		try {
			String xml = getVMsAsXML();
			getPreferences().setValue(PREF_VM_XML, xml);
			savePreferences();
		} catch (IOException e) {
			throw new CoreException(new Status(IStatus.ERROR, LaunchingPlugin.getUniqueIdentifier(), IStatus.ERROR, LaunchingMessages.JavaRuntime_exceptionsOccurred, e)); 
		} catch (ParserConfigurationException e) {
			throw new CoreException(new Status(IStatus.ERROR, LaunchingPlugin.getUniqueIdentifier(), IStatus.ERROR, LaunchingMessages.JavaRuntime_exceptionsOccurred, e)); 
		} catch (TransformerException e) {
			throw new CoreException(new Status(IStatus.ERROR, LaunchingPlugin.getUniqueIdentifier(), IStatus.ERROR, LaunchingMessages.JavaRuntime_exceptionsOccurred, e)); 
		}
	}
	
	private static String getVMsAsXML() throws IOException, ParserConfigurationException, TransformerException {
		VMDefinitionsContainer container = new VMDefinitionsContainer();	
		container.setDefaultVMInstallCompositeID(getDefaultVMId());
		container.setDefaultVMInstallConnectorTypeID(getDefaultVMConnectorId());	
		IVMInstallType[] vmTypes= getVMInstallTypes();
		for (int i = 0; i < vmTypes.length; ++i) {
			IVMInstall[] vms = vmTypes[i].getVMInstalls();
			for (int j = 0; j < vms.length; j++) {
				IVMInstall install = vms[j];
				container.addVM(install);
			}
		}
		return container.getAsXML();
	}
	
	/**
	 * Sets a VM as the system-wide default VM, and notifies registered VM install
	 * change listeners of the change.
	 * 
	 * @param vm	The vm to make the default. May be <code>null</code> to clear 
	 * 				the default.
	 * @param monitor progress monitor or <code>null</code>
	 */
	public static void setDefaultVMInstall(IVMInstall vm, IProgressMonitor monitor) throws CoreException {
		setDefaultVMInstall(vm, monitor, true);
	}	
	
	/**
	 * Sets a VM as the system-wide default VM, and notifies registered VM install
	 * change listeners of the change.
	 * 
	 * @param vm	The vm to make the default. May be <code>null</code> to clear 
	 * 				the default.
	 * @param monitor progress monitor or <code>null</code>
	 * @param savePreference If <code>true</code>, update workbench preferences to reflect
	 * 		   				  the new default VM.
	 * @since 2.1
	 */
	public static void setDefaultVMInstall(IVMInstall vm, IProgressMonitor monitor, boolean savePreference) throws CoreException {
		IVMInstall previous = null;
		if (fgDefaultVMId != null) {
			previous = getVMFromCompositeId(fgDefaultVMId);
		}
		fgDefaultVMId= getCompositeIdFromVM(vm);
		if (savePreference) {
			saveVMConfiguration();
		}
		IVMInstall current = null;
		if (fgDefaultVMId != null) {
			current = getVMFromCompositeId(fgDefaultVMId);
		}
		if (previous != current) {
			notifyDefaultVMChanged(previous, current);
		}
	}
	
	/**
	 * Creates and returns a classpath entry describing
	 * the default JRE container entry.
	 * 
	 * @return a new IClasspathEntry that describes the default JRE container entry
	 * @since 2.0
	 */
	public static IClasspathEntry getDefaultJREContainerEntry() {
		return JavaCore.newContainerEntry(newDefaultJREContainerPath());
	}	
	
	/**
	 * Returns all registered variable resolvers.
	 */
	private static Map getVariableResolvers() {
		if (fgVariableResolvers == null) {
			initializeResolvers();
		}
		return fgVariableResolvers;
	}
	
	private static void initializeResolvers() {
		IExtensionPoint point = Platform.getExtensionRegistry().getExtensionPoint(LaunchingPlugin.ID_PLUGIN, EXTENSION_POINT_RUNTIME_CLASSPATH_ENTRY_RESOLVERS);
		IConfigurationElement[] extensions = point.getConfigurationElements();
		fgVariableResolvers = new HashMap(extensions.length);
		fgContainerResolvers = new HashMap(extensions.length);
		fgRuntimeClasspathEntryResolvers = new HashMap(extensions.length);
		for (int i = 0; i < extensions.length; i++) {
			RuntimeClasspathEntryResolver res = new RuntimeClasspathEntryResolver(extensions[i]);
			String variable = res.getVariableName();
			String container = res.getContainerId();
			String entryId = res.getRuntimeClasspathEntryId();
			if (variable != null) {
				fgVariableResolvers.put(variable, res);
			}
			if (container != null) {
				fgContainerResolvers.put(container, res);
			}
			if (entryId != null) {
				fgRuntimeClasspathEntryResolvers.put(entryId, res);
			}
		}		
	}
	
	/**
	 * Returns the resolver registered for the given variable, or
	 * <code>null</code> if none.
	 * 
	 * @param variableName the variable to determine the resolver for
	 * @return the resolver registered for the given variable, or
	 * <code>null</code> if none
	 */
	private static IRuntimeClasspathEntryResolver2 getVariableResolver(String variableName) {
		return (IRuntimeClasspathEntryResolver2)getVariableResolvers().get(variableName);
	}
	
	/**
	 * Returns all registered container resolvers.
	 */
	private static Map getContainerResolvers() {
		if (fgContainerResolvers == null) {
			initializeResolvers();
		}
		return fgContainerResolvers;
	}
	
	/**
	 * Returns the resolver registered for the given container id, or
	 * <code>null</code> if none.
	 * 
	 * @param containerId the container to determine the resolver for
	 * @return the resolver registered for the given container id, or
	 * <code>null</code> if none
	 */	
	private static IRuntimeClasspathEntryResolver2 getContainerResolver(String containerId) {
		return (IRuntimeClasspathEntryResolver2)getContainerResolvers().get(containerId);
	}
	
	/**
	 * Returns a runtime classpath entry for the given container path with the given
	 * classpath property.
	 * 
	 * @param path container path
	 * @param classpathProperty the type of entry - one of <code>USER_CLASSES</code>,
	 * 	<code>BOOTSTRAP_CLASSES</code>, or <code>STANDARD_CLASSES</code>
	 * @return runtime classpath entry
	 * @exception CoreException if unable to construct a runtime classpath entry
	 * @since 2.0
	 */
	public static IRuntimeClasspathEntry newRuntimeContainerClasspathEntry(IPath path, int classpathProperty) throws CoreException {
		return newRuntimeContainerClasspathEntry(path, classpathProperty, null);
	}
	
	/**
	 * Returns a runtime classpath entry for the given container path with the given
	 * classpath property to be resolved in the context of the given Java project.
	 * 
	 * @param path container path
	 * @param classpathProperty the type of entry - one of <code>USER_CLASSES</code>,
	 * 	<code>BOOTSTRAP_CLASSES</code>, or <code>STANDARD_CLASSES</code>
	 * @param project Java project context used for resolution, or <code>null</code>
	 *  if to be resolved in the context of the launch configuration this entry
	 *  is referenced in
	 * @return runtime classpath entry
	 * @exception CoreException if unable to construct a runtime classpath entry
	 * @since 3.0
	 */
	public static IRuntimeClasspathEntry newRuntimeContainerClasspathEntry(IPath path, int classpathProperty, IJavaProject project) throws CoreException {
		IClasspathEntry cpe = JavaCore.newContainerEntry(path);
		RuntimeClasspathEntry entry = new RuntimeClasspathEntry(cpe, classpathProperty);
		entry.setJavaProject(project);
		return entry;
	}	
	
	/**
	 * Returns a new runtime classpath entry for the classpath
	 * variable with the given path.
	 * 
	 * @param path variable path; first segment is the name of the variable; 
	 * 	trailing segments are appended to the resolved variable value
	 * @return runtime classpath entry
	 * @since 2.0
	 */
	public static IRuntimeClasspathEntry newVariableRuntimeClasspathEntry(IPath path) {
		IClasspathEntry cpe = JavaCore.newVariableEntry(path, null, null);
		return newRuntimeClasspathEntry(cpe);
	}

}
