package descent.launching;

/**
 * An implementation of IVMInstall that is used for manipulating VMs without necessarily 
 * committing changes.
 * <p>
 * Instances of this class act like wrappers.  All other instances of IVMInstall represent 
 * 'real live' VMs that may be used for building or launching.  Instances of this class
 * behave like 'temporary' VMs that are not visible and not available for building or launching.
 * </p>
 * <p>
 * Instances of this class may be constructed as a preliminary step to creating a 'live' VM
 * or as a preliminary step to making changes to a 'real' VM.
 * </p>
 * When <code>convertToRealVM</code> is called, a corresponding 'real' VM is created
 * if one did not previously exist, or the corresponding 'real' VM is updated.
 * </p>
 * <p>
 * Clients may instantiate this class; it is not intended to be subclassed.
 * </p>
 * 
 * @since 2.1
 */
public class VMStandin extends AbstractVMInstall {
    
    /**
     * <code>java.version</code> system property, or <code>null</code>
     * @since 3.1
     */
    private String fJavaVersion = null;

	/*
	 * @see org.eclipse.jdt.launching.AbstractVMInstall#AbstractVMInstall(org.eclipse.jdt.launching.IVMInstallType, java.lang.String)
	 */
	public VMStandin(IVMInstallType type, String id) {
		super(type, id);
		setNotify(false);
	}
	
	/**
	 * Constructs a copy of the specified VM with the given identifier.
	 * 
	 * @param sourceVM
	 * @param id
	 * @since 3.2
	 */
	public VMStandin(IVMInstall sourceVM, String id) {
		super(sourceVM.getVMInstallType(), id);
		setNotify(false);
		init(sourceVM);
	}
	
	/**
	 * Construct a <code>VMStandin</code> instance based on the specified <code>IVMInstall</code>.
	 * Changes to this standin will not be reflected in the 'real' VM until <code>convertToRealVM</code>
	 * is called.
	 * 
	 * @param realVM the 'real' VM from which to construct this standin VM
	 */
	public VMStandin(IVMInstall realVM) {
		this (realVM.getVMInstallType(), realVM.getId());
		init(realVM);
	}

	/**
	 * Initializes the settings of this standin based on the settings in the given
	 * VM install.
	 * 
	 * @param realVM VM to copy settings from
	 */
	private void init(IVMInstall realVM) {
		setName(realVM.getName());
		setInstallLocation(realVM.getInstallLocation());
		setLibraryLocations(realVM.getLibraryLocations());
		setJavadocLocation(realVM.getJavadocLocation());
        fJavaVersion = realVM.getJavaVersion();			
	}
	
	/**
	 * If no corresponding 'real' VM exists, create one and populate it from this standin instance. 
	 * If a corresponding VM exists, update its attributes from this standin instance.
	 * 
	 * @return IVMInstall the 'real' corresponding to this standin VM
	 */
	public IVMInstall convertToRealVM() {
		IVMInstallType vmType= getVMInstallType();
		IVMInstall realVM= vmType.findVMInstall(getId());
		boolean notify = true;
		
		if (realVM == null) {
			realVM= vmType.createVMInstall(getId());
			notify = false;
		}
		// do not notify of property changes on new VMs
		if (realVM instanceof AbstractVMInstall) {
			 ((AbstractVMInstall)realVM).setNotify(notify);
		}
		realVM.setName(getName());
		realVM.setInstallLocation(getInstallLocation());
		realVM.setLibraryLocations(getLibraryLocations());
		realVM.setJavadocLocation(getJavadocLocation());
		
		if (realVM instanceof AbstractVMInstall) {
			 ((AbstractVMInstall)realVM).setNotify(true);
		}		
		if (!notify) {
			JavaRuntime.fireVMAdded(realVM);
		}
		return realVM;
	}
		
    /* (non-Javadoc)
     * @see org.eclipse.jdt.launching.IVMInstall#getJavaVersion()
     */
    public String getJavaVersion() {
        return fJavaVersion;
    }
}
