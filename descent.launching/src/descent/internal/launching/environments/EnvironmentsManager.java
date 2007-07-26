package descent.internal.launching.environments;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Preferences.IPropertyChangeListener;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.ibm.icu.text.MessageFormat;

import descent.internal.launching.LaunchingPlugin;
import descent.launching.IVMInstall;
import descent.launching.IVMInstallChangedListener;
import descent.launching.IVMInstallType;
import descent.launching.JavaRuntime;
import descent.launching.PropertyChangeEvent;
import descent.launching.VMStandin;
import descent.launching.environments.CompatibleEnvironment;
import descent.launching.environments.IExecutionEnvironment;
import descent.launching.environments.IExecutionEnvironmentsManager;

/**
 * Utility class for execution environments.
 * 
 * @since 3.2
 */
public class EnvironmentsManager implements IExecutionEnvironmentsManager, IVMInstallChangedListener, IPropertyChangeListener {

	private static EnvironmentsManager fgManager = null;
	
	/**
	 * Preference store key for XML storing default environments.
	 */
	private static final String PREF_DEFAULT_ENVIRONMENTS_XML = "descent.launching.PREF_DEFAULT_ENVIRONMENTS_XML"; //$NON-NLS-1$
	
	/**
	 * List of environments 
	 */
	private List fEnvironments = null;
	
	/**
	 * Map of environments keyed by id
	 */
	private Map fEnvironmentsMap = null;
	
	/**
	 * Map of analyzers keyed by id
	 */
	private Map fAnalyzers = null;
	
	/**
	 * <code>true</code> while updating the default settings pref
	 */
	private boolean fIsUpdatingDefaults = false;
	
	/**
	 * Whether compatibile environnments have been initialized
	 */
	private boolean fInitializedCompatibilities = false;

	/**
	 * XML attribute
	 */
	private static final String VM_ID = "vmId"; //$NON-NLS-1$

	/**
	 * XML attribute
	 */
	private static final String ENVIRONMENT_ID = "environmentId"; //$NON-NLS-1$

	/**
	 * XML element
	 */
	private static final String DEFAULT_ENVIRONMENT = "defaultEnvironment"; //$NON-NLS-1$

	/**
	 * XML document
	 */
	private static final String DEFAULT_ENVIRONMENTS = "defaultEnvironments"; //$NON-NLS-1$
	
	/**
	 * Returns the singleton environments manager.
	 * 
	 * @return environments manager
	 */
	public static EnvironmentsManager getDefault() {
		if (fgManager == null) {
			fgManager = new EnvironmentsManager();
		}
		return fgManager;
	}
	
	/**
	 * Constructs the new manager.
	 */
	private EnvironmentsManager() {
		JavaRuntime.addVMInstallChangedListener(this);
		LaunchingPlugin.getDefault().getPluginPreferences().addPropertyChangeListener(this);
	}

	/* (non-Javadoc)
	 * @see descent.launching.environments.IExecutionEnvironmentsManager#getExecutionEnvironments()
	 */
	public synchronized IExecutionEnvironment[] getExecutionEnvironments() {
		initializeExtensions();
		return (IExecutionEnvironment[]) fEnvironments.toArray(new IExecutionEnvironment[fEnvironments.size()]);
	}
	
	/* (non-Javadoc)
	 * @see descent.launching.environments.IExecutionEnvironmentsManager#getEnvironment(java.lang.String)
	 */
	public synchronized IExecutionEnvironment getEnvironment(String id) {
		initializeExtensions();
		return (IExecutionEnvironment) fEnvironmentsMap.get(id);
	}
	
	/**
	 * Returns all registered analyzers 
	 * 
	 * @return
	 */
	public synchronized Analyzer[] getAnalyzers() { 
		initializeExtensions();
		Collection collection = fAnalyzers.values();
		return (Analyzer[]) collection.toArray(new Analyzer[collection.size()]);
	}	
	
	private synchronized void initializeExtensions() {
		if (fEnvironments == null) {
			IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(LaunchingPlugin.ID_PLUGIN, JavaRuntime.EXTENSION_POINT_EXECUTION_ENVIRONMENTS);
			IConfigurationElement[] configs= extensionPoint.getConfigurationElements();
			fEnvironments = new ArrayList();
			fEnvironmentsMap = new HashMap(configs.length);
			fAnalyzers = new HashMap(configs.length);
			for (int i = 0; i < configs.length; i++) {
				IConfigurationElement element = configs[i];
				String name = element.getName();
				if (name.equals("environment")) { //$NON-NLS-1$
					String id = element.getAttribute("id"); //$NON-NLS-1$
					if (id == null) {
						LaunchingPlugin.log(MessageFormat.format(EnvironmentMessages.Environments_0, new String[]{element.getContributor().getName()}));
					} else {
						IExecutionEnvironment env = new ExecutionEnvironment(element);
						fEnvironments.add(env);
						fEnvironmentsMap.put(id, env);
					}
				} else if (name.equals("analyzer")) { //$NON-NLS-1$
					String id = element.getAttribute("id"); //$NON-NLS-1$
					if (id == null) {
						LaunchingPlugin.log(MessageFormat.format(EnvironmentMessages.Environments_1, new String[]{element.getContributor().getName()}));
					} else {
						fAnalyzers.put(id, new Analyzer(element));
					}
				}
			}
		}
	}
	
	/**
	 * Initializes compatibility settings.
	 */
	void initializeCompatibilities() {
	    if (!fInitializedCompatibilities) {
	        fInitializedCompatibilities = true;
	        IVMInstallType[] installTypes = JavaRuntime.getVMInstallTypes();
	        synchronized (this) {
	            for (int i = 0; i < installTypes.length; i++) {
	                IVMInstallType type = installTypes[i];
	                IVMInstall[] installs = type.getVMInstalls();
	                for (int j = 0; j < installs.length; j++) {
	                    IVMInstall install = installs[j];
	                    // TODO: progress reporting?
	                    analyze(install, new NullProgressMonitor());
	                }
	            }
	            initializeDefaultVMs();
	        }
	    }
	}
	
	/**
	 * Reads persisted default VMs from pref store
	 */
	private synchronized void initializeDefaultVMs() {
		String xml = LaunchingPlugin.getDefault().getPluginPreferences().getString(PREF_DEFAULT_ENVIRONMENTS_XML);
		try {
			if (xml.length() > 0) {
				DocumentBuilder parser = LaunchingPlugin.getParser();
				
				Document document = parser.parse(new ByteArrayInputStream(xml.getBytes()));
				Element envs = document.getDocumentElement();
				NodeList list = envs.getChildNodes();
				int length = list.getLength();
				for (int i = 0; i < length; ++i) {
					Node node = list.item(i);
					short type = node.getNodeType();
					if (type == Node.ELEMENT_NODE) {
						Element element = (Element) node;
						if (element.getNodeName().equals(DEFAULT_ENVIRONMENT)) {
							String envId = element.getAttribute(ENVIRONMENT_ID);
							String vmId = element.getAttribute(VM_ID);
							ExecutionEnvironment environment = (ExecutionEnvironment) getEnvironment(envId);
							if (environment != null) {
								IVMInstall vm = JavaRuntime.getVMFromCompositeId(vmId);
								if (vm != null) {
									environment.initDefaultVM(vm);
								}
							}
						}
					}
				}			
			}
		} catch (CoreException e) {
			LaunchingPlugin.log(e);
		} catch (SAXException e) {
			LaunchingPlugin.log(e);
		} catch (IOException e) {
			LaunchingPlugin.log(e);
		}
	}

	/**
	 * Returns an XML description of default VMs per environment. Returns
	 * an empty string when there are none. 
	 */
	private String getDefatulVMsAsXML() {
		int count = 0;
		try {
			Document doc = LaunchingPlugin.getDocument();
			Element envs = doc.createElement(DEFAULT_ENVIRONMENTS);
			doc.appendChild(envs);
			IExecutionEnvironment[] environments = getExecutionEnvironments();
			for (int i = 0; i < environments.length; i++) {
				IExecutionEnvironment env = environments[i];
				IVMInstall vm = env.getDefaultVM();
				if (vm != null) {
					count++;
					Element element = doc.createElement(DEFAULT_ENVIRONMENT);
					element.setAttribute(ENVIRONMENT_ID, env.getId());
					element.setAttribute(VM_ID, JavaRuntime.getCompositeIdFromVM(vm));
					envs.appendChild(element);
				}
			}
			if (count > 0) {
				return LaunchingPlugin.serializeDocument(doc);
			}
		} catch (ParserConfigurationException e) {
			LaunchingPlugin.log(e);
		} catch (IOException e) {
			LaunchingPlugin.log(e);
		} catch (TransformerException e) {
			LaunchingPlugin.log(e);
		}
		return ""; //$NON-NLS-1$
	}
	
	/**
	 * Analyzes and compatible execution environments for the given vm install.
	 * 
	 * @param vm
	 * @param monitor
	 */
	private void analyze(IVMInstall vm, IProgressMonitor monitor) {
		Analyzer[] analyzers = getAnalyzers();
		for (int i = 0; i < analyzers.length; i++) {
			Analyzer analyzer = analyzers[i];
			try {
				CompatibleEnvironment[] environments = analyzer.analyze(vm, monitor);
				for (int j = 0; j < environments.length; j++) {
					CompatibleEnvironment compatibleEnvironment = environments[j];
					ExecutionEnvironment environment = (ExecutionEnvironment) compatibleEnvironment.getCompatibleEnvironment();
					environment.add(vm, compatibleEnvironment.isStrictlyCompatbile());					
				}
			} catch (CoreException e) {
				LaunchingPlugin.log(e);
			}
		}	
	}

	/* (non-Javadoc)
	 * @see descent.launching.IVMInstallChangedListener#defaultVMInstallChanged(descent.launching.IVMInstall, descent.launching.IVMInstall)
	 */
	public void defaultVMInstallChanged(IVMInstall previous, IVMInstall current) {
		// nothing
	}

	/* (non-Javadoc)
	 * @see descent.launching.IVMInstallChangedListener#vmChanged(descent.launching.PropertyChangeEvent)
	 */
	public synchronized void vmChanged(PropertyChangeEvent event) {
		IVMInstall vm = (IVMInstall) event.getSource();
		if (vm instanceof VMStandin) {
			return;
		}
		vmRemoved(vm);
		vmAdded(vm);
	}

	/* (non-Javadoc)
	 * @see descent.launching.IVMInstallChangedListener#vmAdded(descent.launching.IVMInstall)
	 */
	public synchronized void vmAdded(IVMInstall vm) {
		// TODO: progress reporting?
		if (vm instanceof VMStandin) {
			return;
		}
		analyze(vm, new NullProgressMonitor());
	}

	/* (non-Javadoc)
	 * @see descent.launching.IVMInstallChangedListener#vmRemoved(descent.launching.IVMInstall)
	 */
	public synchronized void vmRemoved(IVMInstall vm) {
		if (vm instanceof VMStandin) {
			return;
		}
		IExecutionEnvironment[] environments = getExecutionEnvironments();
		for (int i = 0; i < environments.length; i++) {
			ExecutionEnvironment environment = (ExecutionEnvironment) environments[i];
			environment.remove(vm);
		}
	}

	synchronized void updateDefaultVMs() {
		try {
			fIsUpdatingDefaults = true;
			LaunchingPlugin.getDefault().getPluginPreferences().setValue(PREF_DEFAULT_ENVIRONMENTS_XML, getDefatulVMsAsXML());
		} finally {
			fIsUpdatingDefaults = false;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.Preferences.IPropertyChangeListener#propertyChange(org.eclipse.core.runtime.Preferences.PropertyChangeEvent)
	 */
	public synchronized void propertyChange(org.eclipse.core.runtime.Preferences.PropertyChangeEvent event) {
		// don't respond to myself
		if (fIsUpdatingDefaults) {
			return;
		}
		if (event.getProperty().equals(PREF_DEFAULT_ENVIRONMENTS_XML)) {
			initializeDefaultVMs();
		}
	}
		
}
