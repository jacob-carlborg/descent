package descent.launching;

import descent.internal.launching.LaunchingPlugin;

/**
 * A VM install changed listener is notified when
 * the workspace default VM install changes, or when an attribute of
 * a specific VM install changes.
 * Listeners register with <code>JavaRuntime</code>.
 * <p>
 * Clients may implement this interface.
 * </p>
 * @since 2.0
 */
public interface IVMInstallChangedListener {
	
	/**
	 * Property constant indicating the library locations associated
	 * with a VM install have changed.
	 */
	public static final String PROPERTY_LIBRARY_LOCATIONS = LaunchingPlugin.getUniqueIdentifier() + ".PROPERTY_LIBRARY_LOCATIONS"; //$NON-NLS-1$

	/**
	 * Property constant indicating the name associated
	 * with a VM install has changed.
	 */
	public static final String PROPERTY_NAME = LaunchingPlugin.getUniqueIdentifier() + ".PROPERTY_NAME"; //$NON-NLS-1$
	
	/**
	 * Property constant indicating the install location of
	 * a VM install has changed.
	 */
	public static final String PROPERTY_INSTALL_LOCATION = LaunchingPlugin.getUniqueIdentifier() + ".PROPERTY_INSTALL_LOCATION";	 //$NON-NLS-1$
			
	/**
	 * Property constant indicating the Javadoc location associated
	 * with a VM install has changed.
	 */
	public static final String PROPERTY_JAVADOC_LOCATION = LaunchingPlugin.getUniqueIdentifier() + ".PROPERTY_JAVADOC_LOCATION"; //$NON-NLS-1$

	/**
	 * Property constant indicating the VM arguments associated
	 * with a VM install has changed.
     * 
     * @since 3.2
	 */
	public static final String PROPERTY_VM_ARGUMENTS = LaunchingPlugin.getUniqueIdentifier() + ".PROPERTY_VM_ARGUMENTS"; //$NON-NLS-1$

	/**
	 * Notification that the workspace default VM install
	 * has changed.
	 * 
	 * @param previous the VM install that was previously assigned
	 * 	to the workspace, possibly <code>null</code>
	 * @param current the VM install that is currently assigned to the
	 * 	workspace, possibly <code>null</code>
	 */
	public void defaultVMInstallChanged(IVMInstall previous, IVMInstall current);
	
	/**
	 * Notification that a property of a VM install has changed.
	 * 
	 * @param event event describing the change. The VM that has changed
	 * 	is the source object associated with the event.
	 */
	public void vmChanged(PropertyChangeEvent event);	
	
	/**
	 * Notification that a VM has been created.
	 * 
	 * @param vm the vm that has been created
	 */
	public void vmAdded(IVMInstall vm);		
	
	/**
	 * Notification that a VM has been disposed.
	 * 
	 * @param vm the vm that has been disposed
	 */
	public void vmRemoved(IVMInstall vm);			
	
}
