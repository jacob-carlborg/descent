package descent.internal.launching;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.core.runtime.Status;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import descent.core.IClasspathEntry;
import descent.core.IJavaProject;
import descent.launching.IJavaLaunchConfigurationConstants;
import descent.launching.IRuntimeClasspathEntry;
import descent.launching.IRuntimeClasspathEntry2;

/**
 * Common function for runtime classpath entries.
 * <p>
 * Clients implementing runtime classpath entries must subclass this
 * class.
 * </p>
 * @since 3.0
 */
public abstract class AbstractRuntimeClasspathEntry extends PlatformObject implements IRuntimeClasspathEntry2 {
	
	private IPath sourceAttachmentPath = null;
	private IPath rootSourcePath = null;
	private int classpathProperty = IRuntimeClasspathEntry.USER_CLASSES;
	/**
	 * Associated Java project, or <code>null</code>
	 */
	private IJavaProject fJavaProject;
	
	/* (non-Javadoc)
	 * 
	 * Default implementation returns <code>false</code>.
	 * Subclasses should override if required.
	 * 
	 * @see descent.internal.launching.IRuntimeClasspathEntry2#isComposite()
	 */
	public boolean isComposite() {
		return false;
	}
	
	/* (non-Javadoc)
	 * 
	 * Default implementation returns an empty collection.
	 * Subclasses should override if required.
	 * 
	 * @see descent.internal.launching.IRuntimeClasspathEntry2#getRuntimeClasspathEntries()
	 */
	public IRuntimeClasspathEntry[] getRuntimeClasspathEntries() throws CoreException {
		return new IRuntimeClasspathEntry[0];
	}
	
	/**
	 * Throws an exception with the given message and underlying exception.
	 * 
	 * @param message error message
	 * @param exception underlying exception or <code>null</code> if none
	 * @throws CoreException
	 */
	protected void abort(String message, Throwable exception) throws CoreException {
		IStatus status = new Status(IStatus.ERROR, LaunchingPlugin.getUniqueIdentifier(), IJavaLaunchConfigurationConstants.ERR_INTERNAL_ERROR, message, exception);
		throw new CoreException(status);
	}

	/* (non-Javadoc)
	 * 
	 * Default implementation generates a string containing an XML
	 * document. Subclasses should override <code>buildMemento</code>
	 * to specify the contents of the required <code>memento</code>
	 * node.
	 * 
	 * @see descent.launching.IRuntimeClasspathEntry#getMemento()
	 */
	public String getMemento() throws CoreException {
		try {
			Document doc= LaunchingPlugin.getDocument();
			Element root = doc.createElement("runtimeClasspathEntry"); //$NON-NLS-1$
			doc.appendChild(root);
			root.setAttribute("id", getTypeId()); //$NON-NLS-1$
			Element memento = doc.createElement("memento"); //$NON-NLS-1$
			root.appendChild(memento);
			buildMemento(doc, memento);
			return LaunchingPlugin.serializeDocument(doc);
		} catch (Exception e) {
			LaunchingPlugin.log(e);
			return null;
		}
	}
	
	/**
	 * Constructs a memento for this classpath entry in the given 
	 * document and element. The memento element has already been
	 * appended to the document.
	 * 
	 * @param document XML document
	 * @param memento element node for client specific attributes
	 * @throws CoreException if unable to create a memento 
	 */
	protected abstract void buildMemento(Document document, Element memento) throws CoreException;
	
	/* (non-Javadoc)
	 * 
	 * Default implementation returns <code>null</code>.
	 * Subclasses should override if required.
	 * 
	 * @see descent.launching.IRuntimeClasspathEntry#getPath()
	 */
	public IPath getPath() {
		return null;
	}
	
	/* (non-Javadoc)
	 * 
	 * Default implementation returns <code>null</code>.
	 * Subclasses should override if required.
	 * 
	 * @see descent.launching.IRuntimeClasspathEntry#getResource()
	 */
	public IResource getResource() {
		return null;
	}
	
	/* (non-Javadoc)
	 * @see descent.launching.IRuntimeClasspathEntry#getSourceAttachmentPath()
	 */
	public IPath getSourceAttachmentPath() {
		return sourceAttachmentPath;
	}
	/* (non-Javadoc)
	 * @see descent.launching.IRuntimeClasspathEntry#setSourceAttachmentPath(org.eclipse.core.runtime.IPath)
	 */
	public void setSourceAttachmentPath(IPath path) {
		sourceAttachmentPath = path;
	}
	/* (non-Javadoc)
	 * @see descent.launching.IRuntimeClasspathEntry#getSourceAttachmentRootPath()
	 */
	public IPath getSourceAttachmentRootPath() {
		return rootSourcePath;
	}
	/* (non-Javadoc)
	 * @see descent.launching.IRuntimeClasspathEntry#setSourceAttachmentRootPath(org.eclipse.core.runtime.IPath)
	 */
	public void setSourceAttachmentRootPath(IPath path) {
		rootSourcePath = path;
	}
	/* (non-Javadoc)
	 * @see descent.launching.IRuntimeClasspathEntry#getClasspathProperty()
	 */
	public int getClasspathProperty() {
		return classpathProperty;
	}
	/* (non-Javadoc)
	 * @see descent.launching.IRuntimeClasspathEntry#setClasspathProperty(int)
	 */
	public void setClasspathProperty(int property) {
		classpathProperty = property;
	}
	/* (non-Javadoc)
	 * 
	 * Default implementation returns <code>null</code>.
	 * Subclasses should override if required.
	 * 
	 * @see descent.launching.IRuntimeClasspathEntry#getLocation()
	 */
	public String getLocation() {
		return null;
	}
	
	/* (non-Javadoc)
	 * 
	 * Default implementation returns <code>null</code>.
	 * Subclasses should override if required.
	 * @see descent.launching.IRuntimeClasspathEntry#getSourceAttachmentLocation()
	 */
	public String getSourceAttachmentLocation() {
		return null;
	}
	/* (non-Javadoc)
	 * 
	 * Default implementation returns <code>null</code>.
	 * Subclasses should override if required.
	 * @see descent.launching.IRuntimeClasspathEntry#getSourceAttachmentRootLocation()
	 */
	public String getSourceAttachmentRootLocation() {
		return null;
	}
	/* (non-Javadoc)
	 * 
	 * Default implementation returns <code>null</code>.
	 * Subclasses should override if required.
	 * 
	 * @see descent.launching.IRuntimeClasspathEntry#getVariableName()
	 */
	public String getVariableName() {
		return null;
	}
	/* (non-Javadoc)
	 * 
	 * Default implementation returns <code>null</code>.
	 * Subclasses should override if required.
	 * 
	 * @see descent.launching.IRuntimeClasspathEntry#getClasspathEntry()
	 */
	public IClasspathEntry getClasspathEntry() {
		return null;
	}
	/* (non-Javadoc)
	 * @see descent.launching.IRuntimeClasspathEntry#getJavaProject()
	 */
	public IJavaProject getJavaProject() {
		return fJavaProject;
	}
	
	/**
	 * Sets the Java project associated with this entry.
	 * 
	 * @param javaProject
	 */
	protected void setJavaProject(IJavaProject javaProject) {
		fJavaProject = javaProject;
	}
}
