package descent.internal.ui.text.java;

import descent.core.CompletionProposal;
import descent.core.IJavaProject;

public class TemplateProposalInfo extends MemberProposalInfo {

	/**
	 * Creates a new proposal info.
	 *
	 * @param project the java project to reference when resolving types
	 * @param proposal the proposal to generate information for
	 */
	public TemplateProposalInfo(IJavaProject project, CompletionProposal proposal) {
		super(project, proposal);
	}

}
