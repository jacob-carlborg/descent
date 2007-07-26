package descent.internal.launching;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import descent.core.IClasspathEntry;
import descent.core.IJavaProject;
import descent.launching.IRuntimeClasspathEntry;
import descent.launching.IRuntimeClasspathEntryResolver;
import descent.launching.IVMInstall;
import descent.launching.JavaRuntime;


public class VariableClasspathResolver implements IRuntimeClasspathEntryResolver {

	/* (non-Javadoc)
	 * @see descent.launching.IRuntimeClasspathEntryResolver#resolveRuntimeClasspathEntry(descent.launching.IRuntimeClasspathEntry, org.eclipse.debug.core.ILaunchConfiguration)
	 */
	public IRuntimeClasspathEntry[] resolveRuntimeClasspathEntry(IRuntimeClasspathEntry entry, ILaunchConfiguration configuration) throws CoreException {
		return resolveRuntimeClasspathEntry(entry);
	}

	/* (non-Javadoc)
	 * @see descent.launching.IRuntimeClasspathEntryResolver#resolveRuntimeClasspathEntry(descent.launching.IRuntimeClasspathEntry, descent.core.IJavaProject)
	 */
	public IRuntimeClasspathEntry[] resolveRuntimeClasspathEntry(IRuntimeClasspathEntry entry, IJavaProject project) throws CoreException {
		return resolveRuntimeClasspathEntry(entry);
	}

	private IRuntimeClasspathEntry[] resolveRuntimeClasspathEntry(IRuntimeClasspathEntry entry) throws CoreException{
		String variableString = ((VariableClasspathEntry)entry).getVariableString();
		String strpath = VariablesPlugin.getDefault().getStringVariableManager().performStringSubstitution(variableString);
		IPath path = new Path(strpath).makeAbsolute();
		IRuntimeClasspathEntry archiveEntry = JavaRuntime.newArchiveRuntimeClasspathEntry(path);
		return new IRuntimeClasspathEntry[] { archiveEntry };	
	}
	
	/* (non-Javadoc)
	 * @see descent.launching.IRuntimeClasspathEntryResolver#resolveVMInstall(descent.core.IClasspathEntry)
	 */
	public IVMInstall resolveVMInstall(IClasspathEntry entry) throws CoreException {
		return null;
	}
}
