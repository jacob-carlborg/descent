package descent.tests.assist;

import java.util.ArrayList;
import java.util.List;

import descent.core.CompletionProposal;
import descent.core.CompletionRequestor;

public class CompletionRequestorCollector extends CompletionRequestor {
	
	List<CompletionProposal> proposals = new ArrayList<CompletionProposal>();
	
	private final int kind;
	
	public CompletionRequestorCollector(int kind) {
		this.kind = kind;		
	}

	@Override
	public void accept(CompletionProposal proposal) {
		if (proposal.getKind() == kind) {
			proposals.add(proposal);
		}
	}

}
