package descent.internal.core.ctfe;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.sourcelookup.AbstractSourceLookupParticipant;

public class DescentCtfeSourceLookupParticipant extends AbstractSourceLookupParticipant {
	
	public String getSourceName(Object object) throws CoreException {
		if (object instanceof DescentCtfeStackFrame) {
			return ((DescentCtfeStackFrame) object).getCompilationUnit().getFullyQualifiedName();
		}
		return null;
	}
	
	@Override
	public Object[] findSourceElements(Object object) throws CoreException {
		if (object instanceof DescentCtfeStackFrame) {
			DescentCtfeStackFrame frame = (DescentCtfeStackFrame) object;
			return new Object[] { frame.getCompilationUnit().getResource() };
		}
		return null;
	}

}
