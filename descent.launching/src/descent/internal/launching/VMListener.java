package descent.internal.launching;

import descent.launching.IVMInstall;
import descent.launching.IVMInstallChangedListener;
import descent.launching.PropertyChangeEvent;

/**
 * Simple VM listener that reports whether VM settings have changed.
 * 
 * @since 3.2
 *
 */
public class VMListener implements IVMInstallChangedListener {
	
	private boolean fChanged = false;

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.launching.IVMInstallChangedListener#defaultVMInstallChanged(org.eclipse.jdt.launching.IVMInstall, org.eclipse.jdt.launching.IVMInstall)
	 */
	public void defaultVMInstallChanged(IVMInstall previous, IVMInstall current) {
		fChanged = true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.launching.IVMInstallChangedListener#vmAdded(org.eclipse.jdt.launching.IVMInstall)
	 */
	public void vmAdded(IVMInstall vm) {
		fChanged = true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.launching.IVMInstallChangedListener#vmChanged(org.eclipse.jdt.launching.PropertyChangeEvent)
	 */
	public void vmChanged(PropertyChangeEvent event) {
		fChanged = true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.launching.IVMInstallChangedListener#vmRemoved(org.eclipse.jdt.launching.IVMInstall)
	 */
	public void vmRemoved(IVMInstall vm) {
		fChanged = true;
	}
	
	public boolean isChanged() {
		return fChanged;
	}

}
