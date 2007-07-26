package descent.internal.launching;

 
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.debug.core.ILaunchConfiguration;

import descent.core.IClasspathEntry;
import descent.core.IJavaProject;
import descent.launching.IRuntimeClasspathEntry;
import descent.launching.IRuntimeClasspathEntryResolver;
import descent.launching.IRuntimeClasspathEntryResolver2;
import descent.launching.IVMInstall;

/**
 * Proxy to a runtime classpath entry resolver extension.
 */
public class RuntimeClasspathEntryResolver implements IRuntimeClasspathEntryResolver2 {

	private IConfigurationElement fConfigurationElement;
	
	private IRuntimeClasspathEntryResolver fDelegate;
	
	/**
	 * Constructs a new resolver on the given configuration element
	 */
	public RuntimeClasspathEntryResolver(IConfigurationElement element) {
		fConfigurationElement = element;
	}
	
	/**
	 * @see IRuntimeClasspathEntryResolver#resolveRuntimeClasspathEntry(IRuntimeClasspathEntry, ILaunchConfiguration)
	 */
	public IRuntimeClasspathEntry[] resolveRuntimeClasspathEntry(IRuntimeClasspathEntry entry, ILaunchConfiguration configuration) throws CoreException {
		return getResolver().resolveRuntimeClasspathEntry(entry, configuration);
	}
	
	/**
	 * Returns the resolver delegate (and creates if required) 
	 */
	protected IRuntimeClasspathEntryResolver getResolver() throws CoreException {
		if (fDelegate == null) {
			fDelegate = (IRuntimeClasspathEntryResolver)fConfigurationElement.createExecutableExtension("class"); //$NON-NLS-1$
		}
		return fDelegate;
	}
	
	/**
	 * Returns the variable name this resolver is registered for, or <code>null</code>
	 */
	public String getVariableName() {
		return fConfigurationElement.getAttribute("variable"); //$NON-NLS-1$
	}
	
	/**
	 * Returns the container id this resolver is registered for, or <code>null</code>
	 */
	public String getContainerId() {
		return fConfigurationElement.getAttribute("container"); //$NON-NLS-1$
	}	
	
	/**
	 * Returns the runtime classpath entry id this resolver is registered
	 * for,or <code>null</code> if none.
	 */
	public String getRuntimeClasspathEntryId() {
		return fConfigurationElement.getAttribute("runtimeClasspathEntryId"); //$NON-NLS-1$
	}

	/**
	 * @see IRuntimeClasspathEntryResolver#resolveVMInstall(IClasspathEntry)
	 */
	public IVMInstall resolveVMInstall(IClasspathEntry entry) throws CoreException {
		return getResolver().resolveVMInstall(entry);
	}

	/**
	 * @see IRuntimeClasspathEntryResolver#resolveRuntimeClasspathEntry(IRuntimeClasspathEntry, IJavaProject)
	 */
	public IRuntimeClasspathEntry[] resolveRuntimeClasspathEntry(IRuntimeClasspathEntry entry, IJavaProject project) throws CoreException {
		return getResolver().resolveRuntimeClasspathEntry(entry, project);
	}

	/* (non-Javadoc)
	 * @see descent.launching.IRuntimeClasspathEntryResolver2#isVMInstallReference(descent.core.IClasspathEntry)
	 */
	public boolean isVMInstallReference(IClasspathEntry entry) {
		try {
			IRuntimeClasspathEntryResolver resolver = getResolver();
			if (resolver instanceof IRuntimeClasspathEntryResolver2) {
				IRuntimeClasspathEntryResolver2 resolver2 = (IRuntimeClasspathEntryResolver2) resolver;
				return resolver2.isVMInstallReference(entry);
			} else {
				return resolver.resolveVMInstall(entry) != null;
			}
		} catch (CoreException e) {
			return false;
		}
	}

}
