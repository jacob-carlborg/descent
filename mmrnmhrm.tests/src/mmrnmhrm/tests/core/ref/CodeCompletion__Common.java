package mmrnmhrm.tests.core.ref;

import static melnorme.miscutil.Assert.assertFail;
import static melnorme.miscutil.Assert.assertNotNull;
import static melnorme.miscutil.Assert.assertTrue;
import melnorme.miscutil.Assert;
import mmrnmhrm.core.model.DeeProject;
import mmrnmhrm.tests.adapters.Mock_Document;
import mmrnmhrm.ui.editor.DeeEditorTest;
import mmrnmhrm.ui.editor.text.DeeCodeContentAssistProcessor;
import mmrnmhrm.ui.editor.text.DeeCompletionProposal;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.PartInitException;

import dtool.refmodel.PrefixDefUnitSearch.CompletionSession;

public class CodeCompletion__Common extends UITestWithEditor {

	protected static IDocument doc;
	protected int offset;
	String[] proposalNames;
	int repOffset;
	int repLen;
	String repStr;
	int prefixLen;
	
	protected static void setupWithFile(DeeProject deeProject, String path) throws PartInitException, CoreException {
		UITestWithEditor.setupWithFile(deeProject, path);
		Assert.isTrue(editor.getScriptSourceViewer() != null);
		//assist = new DeeCodeContentAssistProcessor(null, editor);
		doc = DeeEditorTest.getDocument(editor);
	}


	protected static final class TestCompletion_Document extends Mock_Document {
			private final String repStr;
			private final int repLen;
			private final int repOffset;
	
			private TestCompletion_Document(String repStr, int repLen, int repOffset) {
				this.repStr = repStr;
				this.repLen = repLen;
				this.repOffset = repOffset;
			}
	
			@Override
			public void replace(int offset, int length, String text)
					throws BadLocationException {
				assertTrue(offset == repOffset 
				&& length == repLen
				&& text.equals(repStr), "Proposal Mismatch");
			}
		}



	protected static void testComputeProposals(int repOffset,
			int prefixLen, String... expectedProposals) throws ModelException {
		testComputeProposalsWithRepLen(repOffset, prefixLen, 0, expectedProposals);
	}
	
	protected static void testComputeProposalsWithRepLen(int repOffset, int prefixLen, 
			int repLen, String... expectedProposals) throws ModelException {
		ICompletionProposal[] proposals = DeeCodeContentAssistProcessor
				.computeProposals(repOffset, srcModule, srcModule.getSource(), new CompletionSession());
		assertNotNull(proposals, "Code Completion Unavailable");
		checkProposals(repOffset, repLen, prefixLen, proposals, expectedProposals);
		invokeContentAssist();
	}

	private static void invokeContentAssist() {
		ITextOperationTarget target= 
			(ITextOperationTarget) editor.getAdapter(ITextOperationTarget.class);
		if (target != null && target.canDoOperation(ISourceViewer.CONTENTASSIST_PROPOSALS))
			target.doOperation(ISourceViewer.CONTENTASSIST_PROPOSALS);
	}

	protected static void checkProposal(ICompletionProposal proposal,
			final int repOffset, final String repStr, final int repLen) {
		proposal.apply(new TestCompletion_Document(repStr, repLen, repOffset));
	}
	
	protected static void checkProposals(int repOffset, int repLen, int prefixLen,
			ICompletionProposal[] proposals, String... expectedProposals) {
		boolean[] proposalsMatched = new boolean[expectedProposals.length];

		assertTrue(proposals.length == expectedProposals.length, 
				"Size mismatch, expected: "+expectedProposals.length
				+" got: "+ proposals.length);
		
		for (int i = 0; i < proposals.length; i++) {
			DeeCompletionProposal proposal = (DeeCompletionProposal) proposals[i];
			String defName = proposal.defUnit.getName();

			String repStr;
			// Find this proposal in the expecteds
			int j = 0;
			for (; true; j++) {

				repStr = expectedProposals[j];
				if(defName.substring(prefixLen).equals(repStr))
					break;

				if(j == expectedProposals.length-1)
					assertFail("Got Unmatched proposal:"+defName);
				
			}
			
			checkProposal(proposal, repOffset, repStr, repLen);
			// Mark that expected as obtained
			proposalsMatched[j] = true;
		}
		
		for (int i = 0; i < proposalsMatched.length; i++) {
			// assert all expecteds were matched
			assertTrue(proposalsMatched[i] == true, "Assertion failed.");
		}
	}

}