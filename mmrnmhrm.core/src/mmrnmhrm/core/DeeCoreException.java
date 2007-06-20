package mmrnmhrm.core;


import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;


public class DeeCoreException extends CoreException {

	private static final long serialVersionUID = 1974118931623091239L;


	public DeeCoreException(String message, Throwable e) {
		super(new Status(IStatus.ERROR, DeeCore.PLUGIN_ID, IStatus.ERROR,
				message, e));
	}


}
