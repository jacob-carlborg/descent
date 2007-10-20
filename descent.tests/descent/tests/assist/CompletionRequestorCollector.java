package descent.tests.assist;

import java.util.ArrayList;
import java.util.List;

import descent.core.CompletionProposal;
import descent.core.CompletionRequestor;

public class CompletionRequestorCollector extends CompletionRequestor {
	
	List<CompletionProposal> proposals = new ArrayList<CompletionProposal>();

	@Override
	public void accept(CompletionProposal proposal) {
		proposals.add(proposal);
	}

}
