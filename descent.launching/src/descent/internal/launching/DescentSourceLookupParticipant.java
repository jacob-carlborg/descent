package descent.internal.launching;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.sourcelookup.AbstractSourceLookupParticipant;

import descent.internal.launching.model.DescentStackFrame;

public class DescentSourceLookupParticipant extends AbstractSourceLookupParticipant {

	public String getSourceName(Object object) throws CoreException {
		if (object instanceof DescentStackFrame) {
			return ((DescentStackFrame) object).getSourceName();
		}
		return null;
	}

}
