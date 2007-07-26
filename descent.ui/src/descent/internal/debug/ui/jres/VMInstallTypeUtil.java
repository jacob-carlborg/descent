package descent.internal.debug.ui.jres;

import java.io.File;

import descent.launching.IVMInstall;
import descent.launching.IVMInstallType;

public class VMInstallTypeUtil {
	
	public static String getVMInstallName(IVMInstallType vm, File installLocation) {
		vm.disposeVMInstall(""); //$NON-NLS-1$
		IVMInstall install = vm.createVMInstall(""); //$NON-NLS-1$
		install.setInstallLocation(installLocation);
		String version = install.getJavaVersion();
		String name;
		if (version != null) {
			name = vm.getName() + " " + install.getJavaVersion(); //$NON-NLS-1$
		} else {
			name = vm.getName();
		}
		vm.disposeVMInstall(""); //$NON-NLS-1$
		return name;
	}

}
