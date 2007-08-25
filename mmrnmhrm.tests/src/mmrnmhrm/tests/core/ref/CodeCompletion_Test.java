package mmrnmhrm.tests.core.ref;

import melnorme.miscutil.Assert;
import mmrnmhrm.tests.SampleMainProject;
import mmrnmhrm.tests.adapters.Mock_Document;
import mmrnmhrm.ui.editor.DeeEditorTest;
import mmrnmhrm.ui.editor.text.DeeCodeContentAssistProcessor;
import mmrnmhrm.ui.editor.text.DeeCompletionProposal;

import org.eclipse.dltk.core.ModelException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.junit.BeforeClass;
import org.junit.Test;

import dtool.refmodel.PrefixDefUnitSearch.CompletionSession;

public class CodeCompletion_Test extends UITestWithEditor {
	
	static final String TEST_SRCFOLDER = SampleMainProject.TEST_SRC3;
	static final String TEST_SRCFILE = TEST_SRCFOLDER + "/testCodeCompletion.d";

	/* --- */
	
	//static DeeCodeContentAssistProcessor assist;
	static IDocument doc;
	
	protected int offset;
	String[] proposalNames;
	int repOffset;
	int repLen; 
	String repStr;
	int prefixLen;
	
	@BeforeClass
	public static void commonSetUp() throws Exception {
		setupWithFile(SampleMainProject.deeProj, TEST_SRCFILE);
		Assert.isTrue(editor.getScriptSourceViewer() != null);
		//assist = new DeeCodeContentAssistProcessor(null, editor);
		doc = DeeEditorTest.getDocument(editor);
	}
	
	private static final class TestCompletion_Document extends Mock_Document {
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
					&& text.equals(repStr),
					"Proposal Mismatch");
		}
	}
	
	private static void checkProposal(ICompletionProposal proposal,
			final int repOffset, final String repStr, final int repLen) {
		proposal.apply(new TestCompletion_Document(repStr, repLen, repOffset));
	}

	private static void testComputeProposals(int repOffset,
			int prefixLen, String... expectedProposals) throws ModelException {
		testComputeProposalsWithRepLen(repOffset, 0, prefixLen, expectedProposals);
	}
	
	private static void testComputeProposalsWithRepLen(int repOffset, int repLen,
			int prefixLen, String... expectedProposals) throws ModelException {
		ICompletionProposal[] proposals = DeeCodeContentAssistProcessor
				.computeProposals(repOffset, srcModule, srcModule.getSource(), new CompletionSession());
		checkProposals(repOffset, repLen, prefixLen, proposals, expectedProposals);
	}

	private static void checkProposals(int repOffset, int repLen, int prefixLen,
			ICompletionProposal[] proposals, String... expectedProposals) {
		boolean[] proposalsMatched = new boolean[expectedProposals.length];

		if(proposals == null) {
			assertTrue(expectedProposals.length == 0);
			return;
		} else {
			assertTrue(proposals.length == expectedProposals.length, 
					"Size mismatch, expected: "+expectedProposals.length
					+" got: "+ proposals.length);
		}
		
		for (int i = 0; i < proposals.length; i++) {
			DeeCompletionProposal proposal = (DeeCompletionProposal) proposals[i];
			String defName = proposal.defUnit.defname.toString();

			String repStr;
			// Find this proposal in the expecteds
			int j = 0;
			for (; true; j++) {
				repStr = expectedProposals[j];
				if(defName.toString().substring(prefixLen).equals(repStr))
					break;
				
				if(j == expectedProposals.length)
					assertTrue(false);
			}
			
			checkProposal(proposal, repOffset, repStr, repLen);
			// Mark that expected as obtained
			proposalsMatched[j] = true;
		}
		
		for (int i = 0; i < proposalsMatched.length; i++) {
			// assert all expecteds were matched
			assertTrue(proposalsMatched[i] == true);
		}
	}
	
	/* ------------- Tests -------------  */
	
	@Test
	public void test0() throws Exception {
		//testComputeProposals(0, 0);
	}
	@Test
	public void test1() throws Exception {
		testComputeProposals(364, 0, "fParam", "test", "func", "foobarvar",
				"foovar", "foox", "baz",
				"FooBar", "ix",  "foo_t", "fooalias", "fooOfModule", "frak", "Foo", "Xpto",
				"testCodeCompletion"
				);
	}
	@Test
	public void test2() throws Exception {
		testComputeProposals(386, 1, "Param", "unc", "oobarvar",
				"oovar", "oox", 
				/*"FooBar",*/  "oo_t", "ooalias", "ooOfModule", "rak" /*,"Foo",*/
				);
		
		// same test, but characters ahead of offset
		testComputeProposalsWithRepLen(410, 2, 1, "Param", "unc", "oobarvar",
				"oovar", "oox", 
				/*"FooBar",*/  "oo_t", "ooalias", "ooOfModule", "rak" /*,"Foo",*/
				);
	}

	@Test
	public void test3() throws Exception {
		testComputeProposals(412, 3, "barvar",
				"var", "x", "_t", "alias", "OfModule"
				);
	}
	
	@Test
	public void test4UI() throws Exception {
	}
	@Test
	public void test5UI() throws Exception {
	}
	@Test
	public void test6() throws Exception {
		// FIXUP because of syntax errors;
		DeeEditorTest.getDocument(editor).replace(541, 1, ".");
		try {
			testComputeProposals(542, 0, "foovar", "foox", "baz");
		} finally {
			DeeEditorTest.getDocument(editor).replace(541, 1, " ");
		}
	}
	@Test
	public void test6b() throws Exception {
		// Test in middle of the first name of the qualified ref
		testComputeProposalsWithRepLen(575, 2, 1, "oo", "ooBar");
		// Test at end of qualified ref
		testComputeProposals(579, 1, "oovar", "oox");
	}
	
	@Test
	public void test7() throws Exception {
		DeeEditorTest.getDocument(editor).replace(615, 1, ".");
		try {
			testComputeProposals(616, 0, "Foo", "fooOfModule", "frak",
					"fooalias", "foo_t", "ix", "FooBar", "Xpto");
		} finally {
			DeeEditorTest.getDocument(editor).replace(615, 1, " ");
		}
	}
	@Test
	public void test7b() throws Exception {
		testComputeProposals(657, 1, "ooOfModule", "rak",
				"ooalias", "oo_t");
	}


	@Test
	public void test8() throws Exception {
		testComputeProposals(714, 1, "oovar", "oox");
	}
}

