package descent.internal.launching;


import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.ILaunchConfiguration;
import descent.core.IAccessRule;
import descent.core.IClasspathAttribute;
import descent.core.IClasspathEntry;
import descent.core.IJavaProject;
import descent.core.JavaCore;
import descent.launching.IRuntimeClasspathEntry;
import descent.launching.IRuntimeClasspathEntryResolver2;
import descent.launching.IVMInstall;
import descent.launching.JavaRuntime;
import descent.launching.LibraryLocation;

/**
 * Resolves for JRELIB_VARIABLE and JRE_CONTAINER
 */
public class JRERuntimeClasspathEntryResolver implements IRuntimeClasspathEntryResolver2 {
	
	private static IAccessRule[] EMPTY_RULES = new IAccessRule[0];

	/**
	 * @see IRuntimeClasspathEntryResolver#resolveRuntimeClasspathEntry(IRuntimeClasspathEntry, ILaunchConfiguration)
	 */
	public IRuntimeClasspathEntry[] resolveRuntimeClasspathEntry(IRuntimeClasspathEntry entry, ILaunchConfiguration configuration) throws CoreException {
		IVMInstall jre = null;
		if (entry.getType() == IRuntimeClasspathEntry.CONTAINER && entry.getPath().segmentCount() > 1) {
			// a specific VM
			jre = JREContainerInitializer.resolveVM(entry.getPath()); 
		} else {
			// default VM for config
			// jre = JavaRuntime.computeVMInstall(configuration);
		}
		if (jre == null) {
			// cannot resolve JRE
			return new IRuntimeClasspathEntry[0];
		}
		return resolveLibraryLocations(jre, entry.getClasspathProperty());
	}
	
	/**
	 * @see IRuntimeClasspathEntryResolver#resolveRuntimeClasspathEntry(IRuntimeClasspathEntry, IJavaProject)
	 */
	public IRuntimeClasspathEntry[] resolveRuntimeClasspathEntry(IRuntimeClasspathEntry entry, IJavaProject project) throws CoreException {
		IVMInstall jre = null;
		if (entry.getType() == IRuntimeClasspathEntry.CONTAINER && entry.getPath().segmentCount() > 1) {
			// a specific VM
			jre = JREContainerInitializer.resolveVM(entry.getPath()); 
		} else {
			// default VM for project
			jre = JavaRuntime.getVMInstall(project);
		}
		if (jre == null) {
			// cannot resolve JRE
			return new IRuntimeClasspathEntry[0];
		}		
		return resolveLibraryLocations(jre, entry.getClasspathProperty());
	}

	/**
	 * Resolves libray locations for the given VM install
	 */
	protected IRuntimeClasspathEntry[] resolveLibraryLocations(IVMInstall vm, int kind) {
		LibraryLocation[] libs = vm.getLibraryLocations();
		LibraryLocation[] defaultLibs = vm.getVMInstallType().getDefaultLibraryLocations(vm.getInstallLocation());
		boolean overrideJavadoc = false;
		if (libs == null) {
			// default system libs
			libs = defaultLibs;
			overrideJavadoc = true;
		} else if (!isSameArchives(libs, defaultLibs)) {
			// determine if bootpath should be explicit
			kind = IRuntimeClasspathEntry.BOOTSTRAP_CLASSES;
		}		
		if (kind == IRuntimeClasspathEntry.BOOTSTRAP_CLASSES) {
			File vmInstallLocation= vm.getInstallLocation();
			if (vmInstallLocation != null) {
				LibraryInfo libraryInfo= LaunchingPlugin.getLibraryInfo(vmInstallLocation.getAbsolutePath());
				if (libraryInfo != null) {
					// only return endorsed and bootstrap classpath entries if we have the info
					// libs in the ext dirs are not loaded by the boot class loader
					String[] extensionDirsArray = libraryInfo.getExtensionDirs();
					Set extensionDirsSet = new HashSet();
					for (int i = 0; i < extensionDirsArray.length; i++) {
						extensionDirsSet.add(extensionDirsArray[i]);
					}
					List resolvedEntries = new ArrayList(libs.length);
					for (int i = 0; i < libs.length; i++) {
						LibraryLocation location = libs[i];
						IPath libraryPath = location.getSystemLibraryPath();
						String dir = libraryPath.toFile().getParent();
						// exclude extension directory entries
						if (!extensionDirsSet.contains(dir)) {
							resolvedEntries.add(resolveLibraryLocation(vm, location, kind, overrideJavadoc));
						}
					}
					return (IRuntimeClasspathEntry[]) resolvedEntries.toArray(new IRuntimeClasspathEntry[resolvedEntries.size()]);
				}
			}
		}
		List resolvedEntries = new ArrayList(libs.length);
		for (int i = 0; i < libs.length; i++) {
			IPath systemLibraryPath = libs[i].getSystemLibraryPath();
			if (systemLibraryPath.toFile().exists()) {
				resolvedEntries.add(resolveLibraryLocation(vm, libs[i], kind, overrideJavadoc));
			}
		}
		return (IRuntimeClasspathEntry[]) resolvedEntries.toArray(new IRuntimeClasspathEntry[resolvedEntries.size()]);
	}
		
	/**
	 * Return whether the given list of libraries refer to the same archives in the same
	 * order. Only considers the binary archive (not source or javadoc locations). 
	 *  
	 * @param libs
	 * @param defaultLibs
	 * @return whether the given list of libraries refer to the same archives in the same
	 * order
	 */
	public static boolean isSameArchives(LibraryLocation[] libs, LibraryLocation[] defaultLibs) {
		if (libs.length != defaultLibs.length) {
			return false;
		}
		for (int i = 0; i < defaultLibs.length; i++) {
			LibraryLocation def = defaultLibs[i];
			LibraryLocation lib = libs[i];
			if (!def.getSystemLibraryPath().equals(lib.getSystemLibraryPath())) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @see IRuntimeClasspathEntryResolver#resolveVMInstall(IClasspathEntry)
	 */
	public IVMInstall resolveVMInstall(IClasspathEntry entry) {
		switch (entry.getEntryKind()) {
			case IClasspathEntry.CPE_VARIABLE:
				if (entry.getPath().segment(0).equals(JavaRuntime.JRELIB_VARIABLE)) {
					return JavaRuntime.getDefaultVMInstall();
				}
				break;
			case IClasspathEntry.CPE_CONTAINER:
				if (entry.getPath().segment(0).equals(JavaRuntime.JRE_CONTAINER)) {
					return JREContainerInitializer.resolveVM(entry.getPath());
				}
				break;
			default:
				break;
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see descent.launching.IRuntimeClasspathEntryResolver2#isVMInstallReference(descent.core.IClasspathEntry)
	 */
	public boolean isVMInstallReference(IClasspathEntry entry) {
		switch (entry.getEntryKind()) {
			case IClasspathEntry.CPE_VARIABLE:
				if (entry.getPath().segment(0).equals(JavaRuntime.JRELIB_VARIABLE)) {
					return true;
				}
				break;
			case IClasspathEntry.CPE_CONTAINER:
				if (entry.getPath().segment(0).equals(JavaRuntime.JRE_CONTAINER)) {
					return true;
				}
				break;
			default:
				break;
		}
		return false;
	}
	
	/**
	 * Returns a runtime classpath entry for the given library in the specified VM.
	 * 
	 * @param vm
	 * @param location
	 * @param kind
	 * @return runtime classpath entry
	 * @since 3.2
	 */
	private IRuntimeClasspathEntry resolveLibraryLocation(IVMInstall vm, LibraryLocation location, int kind, boolean overrideJavaDoc) {
		IPath libraryPath = location.getSystemLibraryPath();
		URL javadocLocation = location.getJavadocLocation();
		if (overrideJavaDoc && javadocLocation == null) {
			javadocLocation = vm.getJavadocLocation();
		}							
		IClasspathAttribute[] attributes = null;
		if (javadocLocation == null) {
			attributes = new IClasspathAttribute[0];
		} else {
			attributes = new IClasspathAttribute[]{JavaCore.newClasspathAttribute(IClasspathAttribute.JAVADOC_LOCATION_ATTRIBUTE_NAME, javadocLocation.toExternalForm())};
		}
		IClasspathEntry cpe = JavaCore.newLibraryEntry(libraryPath, location.getSystemLibraryPath(), location.getPackageRootPath(), EMPTY_RULES, attributes, false);
		IRuntimeClasspathEntry resolved = new RuntimeClasspathEntry(cpe);
		resolved.setClasspathProperty(kind);
		IPath sourcePath = location.getSystemLibrarySourcePath();
		if (sourcePath != null && !sourcePath.isEmpty()) {
			resolved.setSourceAttachmentPath(sourcePath);
			resolved.setSourceAttachmentRootPath(location.getPackageRootPath());
		}
		return resolved;
	}

}
