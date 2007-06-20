package mmrnmhrm.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;

public class LangModelException extends CoreException {


	private static final long serialVersionUID = 1L;
	
	protected CoreException nestedCoreException;
	
	public LangModelException(IStatus status) {
		super(status);
	}
	
	/** Creates a Lang model exception for the given CoreException.
	 */
	public LangModelException(CoreException exception) {
		super(exception.getStatus());
		this.nestedCoreException = exception;
	}
	
	/**
	 * Returns the underlying <code>Throwable</code> that caused the failure, or 
	 * <code>null</code> if the direct case of the failure was at the Lang 
	 * model layer.
	 */
	public Throwable getException() {
		if (this.nestedCoreException == null) {
			return getStatus().getException();
		} else {
			return this.nestedCoreException;
		}
	}

}
