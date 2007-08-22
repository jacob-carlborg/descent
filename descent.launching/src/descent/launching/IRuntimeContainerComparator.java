package descent.launching;


import org.eclipse.core.runtime.IPath;

/**
 * Determines if container entries are duplicates/redundant on a runtime
 * classpath. If an <code>IClasspathContianer</code> implements this interface,
 * the <code>isDuplicate</code> method is used to determine if containers are
 * duplicates/redundant. Otherwise, containers with the same identifier are
 * considered duplicates. 
 * 
 * @since 2.0.1
 * @deprecated support has been added to <code>ClasspathContainerInitializer</code>
 *  to handle comparison of classpath containers. Use
 *  <code>ClasspathContainerInitializer.getComparisonID(IPath,IJavaProject)</code>.
 *  When a classpath container implements this interface, this interface is
 *  used to determine equality before using the support defined in
 *  <code>ClasspathContainerInitializer</code>. 
 */
public interface IRuntimeContainerComparator {
	
	/**
	 * Returns whether this container is a duplicate of the container
	 * identified by the given path.
	 * 
	 * @param containerPath the container to compare against
	 * @return whether this container is a duplicate of the container
	 * identified by the given path
	 */
	public boolean isDuplicate(IPath containerPath);

}
