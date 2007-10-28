package descent.internal.ui.text.java;

import org.eclipse.core.runtime.IProgressMonitor;

public class DdocMacroProposalInfo extends ProposalInfo {
	
	private final String ddocReplacement;

	public DdocMacroProposalInfo(String ddocReplacement) {
		this.ddocReplacement = ddocReplacement;
		setNeedsHtmlRendering(false);
	}
	
	@Override
	protected String computeInfo(IProgressMonitor monitor) {
		return ddocReplacement;
	}

}
