package descent.internal.debug.ui.jres;


import descent.launching.IVMInstall;

/**
 * This interface is implemented by clients of the <code>AddVMDialog</code>.
 */
public interface IAddVMDialogRequestor {

	/**
	 * Reply whether or not a new VM of the specified name would
	 * constitute a duplicate.
	 * 
	 * @param name the name of a potential new VM
	 * @return whether a new VM with the specified name would be a duplicate VM
	 */
	public boolean isDuplicateName(String name);
	
	/**
	 * Notification that a VM has been added from the <code>AddVMDialog</code>.
	 * 
	 * @param vm the added vm
	 */
	public void vmAdded(IVMInstall vm);
	
}
