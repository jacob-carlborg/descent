package descent.internal.launching.environments;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import descent.launching.IVMInstall;
import descent.launching.environments.CompatibleEnvironment;
import descent.launching.environments.IExecutionEnvironmentAnalyzerDelegate;

/**
 * Contributed analyzer.
 * 
 * @since 3.2
 *
 */
class Analyzer implements IExecutionEnvironmentAnalyzerDelegate {
	
	private IConfigurationElement fElement;
	
	private IExecutionEnvironmentAnalyzerDelegate fDelegate;
	
	Analyzer(IConfigurationElement element) {
		fElement = element;
	}

	/* (non-Javadoc)
	 * @see descent.launching.environments.IExecutionEnvironmentAnalyzer#analyze(descent.launching.IVMInstall, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public CompatibleEnvironment[] analyze(IVMInstall vm, IProgressMonitor monitor) throws CoreException {
		try {
			return getDelegate().analyze(vm, monitor);
		} catch (AbstractMethodError e) {
			// TODO: remove once PDE catches up with API changes
			return new CompatibleEnvironment[0];
		}
	}

	/**
	 * Instantiates and returns the contributed analyzer.
	 * 
	 * @return analyzer
	 * @throws CoreException
	 */
	private IExecutionEnvironmentAnalyzerDelegate getDelegate() throws CoreException {
		if (fDelegate == null) {
			fDelegate = (IExecutionEnvironmentAnalyzerDelegate) fElement.createExecutableExtension("class");  //$NON-NLS-1$
		}
		return fDelegate;
	}

	/**
	 * Returns the id of this delegate
	 * @return id
	 */
	public String getId() {
		return fElement.getAttribute("id"); //$NON-NLS-1$
	}

}
