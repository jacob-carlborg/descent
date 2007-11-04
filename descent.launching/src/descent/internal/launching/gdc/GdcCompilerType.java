package descent.internal.launching.gdc;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;

import descent.internal.launching.LaunchingMessages;
import descent.internal.launching.LaunchingPlugin;
import descent.launching.AbstractVMInstallType;
import descent.launching.IVMInstall;
import descent.launching.LibraryLocation;

public class GdcCompilerType extends AbstractVMInstallType {
	
	public final static String ID = "descent.internal.debug.ui.launcher.GdcCompilerType"; //$NON-NLS-1$

	@Override
	protected IVMInstall doCreateVMInstall(String id) {
		return new GdcCompiler(this, id);
	}

	public LibraryLocation[] getDefaultLibraryLocations(File installLocation) {
		// TODO phobos hardcoded
		File exe = getExecutable(installLocation);
		String gdcVersion = getGdcVersion(exe);
		if (gdcVersion == null) {
			return new LibraryLocation[0];
		}
		
		File phobosFile = new File(installLocation, "include/d/" + gdcVersion);
		File phobosSrcFile = new File(installLocation, "include/d/" + gdcVersion);
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
		return "GDC"; //$NON-NLS-1$
	}

	public IStatus validateInstallLocation(File installLocation) {
		// Check if bin/gdc.exe or bin/gdc are present
		File exe = getExecutable(installLocation);
		if (exe == null) {
			return new Status(IStatus.ERROR, LaunchingPlugin.getUniqueIdentifier(), 0, LaunchingMessages.StandardVMType_Not_a_JDK_Root__Java_executable_was_not_found_1, null); //;
		}
		
		return Status.OK_STATUS;
	}
	
	private File getExecutable(File installLocation) {
		File gdcExe = new File(installLocation, "bin/gdc.exe");
		if (gdcExe.exists()) {
			return gdcExe;
		}
		
		File gdc = new File(installLocation, "bin/gdc");
		if (gdc.exists()) {
			return gdc;
		}
		
		return null;
	}
	
	private String getGdcVersion(File exe) {
		try {
			Process process = Runtime.getRuntime().exec(exe.getAbsolutePath() + " -dumpversion"); //$NON-NLS-1$
			InputStream is = process.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			
			String gdcVersion = br.readLine();
			
			br.close();
			isr.close();
			is.close();
			
			return gdcVersion;
		} catch (IOException e) {
			return null;
		}
	}

}
