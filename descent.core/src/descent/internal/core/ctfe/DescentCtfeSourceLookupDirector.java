package descent.internal.core.ctfe;

import org.eclipse.debug.core.sourcelookup.AbstractSourceLookupDirector;
import org.eclipse.debug.core.sourcelookup.ISourceLookupParticipant;

public class DescentCtfeSourceLookupDirector extends AbstractSourceLookupDirector {
	
	public void initializeParticipants() {
		addParticipants(new ISourceLookupParticipant[]{ new DescentCtfeSourceLookupParticipant() });
	}

}
