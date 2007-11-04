package descent.internal.launching.dmd;

import java.io.File;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;

import descent.internal.launching.LaunchingMessages;
import descent.internal.launching.LaunchingPlugin;
import descent.launching.AbstractVMInstallType;
import descent.launching.IVMInstall;
import descent.launching.LibraryLocation;

public class DmdCompilerType extends AbstractVMInstallType {
	
	public final static String ID = "descent.internal.debug.ui.launcher.DmdCompilerType"; //$NON-NLS-1$

	@Override
	protected IVMInstall doCreateVMInstall(String id) {
		return new DmdCompiler(this, id);
	}

	public LibraryLocation[] getDefaultLibraryLocations(File installLocation) {
		// TODO phobos hardcoded
		File phobosFile = new File(installLocation, "src/phobos");
		File phobosSrcFile = new File(installLocation, "src/phobos");
		File object = new File(phobosSrcFile, "object.d");
		
		if (phobosSrcFile.exists() && phobosSrcFile.isDirectory() && object.exists()) {
			Path phobosPath = new Path(phobosFile.getAbsolutePath());
			Path phobosSrcPath = new Path(phobosSrcFile.getAbsolutePath());
			
			LibraryLocation phobos = new LibraryLocation(phobosPath, phobosSrcPath, Path.EMPTY);
			return new LibraryLocation[] { phobos };
		} else {
			return new LibraryLocation[0];
		}
	}

	public String getName() {
		return "DMD"; //$NON-NLS-1$
	}

	public IStatus validateInstallLocation(File installLocation) {
		// Check if bin/dmd.exe or bin/dmd are present
		File dmdExe = new File(installLocation, "bin/dmd.exe");
		if (dmdExe.exists()) {
			return Status.OK_STATUS;
		}
		
		File dmd = new File(installLocation, "bin/dmd");
		if (dmd.exists()) {
			return Status.OK_STATUS;
		}
		
		return new Status(IStatus.ERROR, LaunchingPlugin.getUniqueIdentifier(), 0, LaunchingMessages.StandardVMType_Not_a_JDK_Root__Java_executable_was_not_found_1, null); //;
	}

}
