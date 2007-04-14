package mmrnmhrm.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;

public class LangModelException extends CoreException {


	private static final long serialVersionUID = 1L;

	public LangModelException(IStatus status) {
		super(status);
	}

}
