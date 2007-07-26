package descent.launching;


import descent.core.IClasspathEntry;

/**
 * Optional enhancements to {@link IRuntimeClasspathEntryResolver}.
 * <p>
 * Clients may implement this interface.
 * </p>
 * @since 3.2
 */
public interface IRuntimeClasspathEntryResolver2 extends IRuntimeClasspathEntryResolver {
	
	/**
	 * Returns whether the given classpath entry references a VM install.
	 * 
	 * @param entry classpath entry
	 * @return whether the given classpath entry references a VM install
	 */
	public boolean isVMInstallReference(IClasspathEntry entry);
}
