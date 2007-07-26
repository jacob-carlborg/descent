package descent.launching.environments;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import descent.launching.IVMInstall;

/**
 * Analyzes vm installs for compatibility with execution environments.
 * <p>
 * An execution environment analyzer delegate is contributed in plug-in XML via
 * the <code>descent.launching.executionEnvironments</code> 
 * extension point.
 * </p>
 * <p>
 * Clients may implement this interface.
 * </p>
 * @since 3.2
 */
public interface IExecutionEnvironmentAnalyzerDelegate {
	
	/**
	 * Analyzes the given vm install and returns a collection of compatible
	 * execution environments, possibly empty.
	 * 
	 * @param vm vm install to analyze
	 * @param monitor progress monitor
	 * @return compatible execution environments, possibly empty
	 * @throws CoreException if an exception occurs analyzing the vm install
	 */
	public CompatibleEnvironment[] analyze(IVMInstall vm, IProgressMonitor monitor) throws CoreException;

}
