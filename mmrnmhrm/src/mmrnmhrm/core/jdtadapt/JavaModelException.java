package mmrnmhrm.core.jdtadapt;


import org.eclipse.core.runtime.CoreException;


/**
 * JAVA stub. @see org.eclipse.jdt.core.JavaModelException
 */
public class JavaModelException extends CoreException {

	private static final long serialVersionUID = 1974118931623091239L;

	CoreException nestedCoreException;

	/**
	 * Creates a Java model exception for the given <code>CoreException</code>.
	 */
	public JavaModelException(CoreException exception) {
		super(exception.getStatus());
		this.nestedCoreException = exception;
	}

}
