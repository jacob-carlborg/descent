package mmrnmhrm.tests.core.ref;

import melnorme.miscutil.Assert;
import mmrnmhrm.tests.SampleMainProject;
import mmrnmhrm.ui.editor.DeeEditorTest;

import org.junit.BeforeClass;
import org.junit.Test;

public class CodeCompletion_2Test extends CodeCompletion__Common {
	
	static final String TEST_SRCFOLDER = SampleMainProject.TEST_SRC3;
	static final String TEST_SRCFILE = TEST_SRCFOLDER + "/testCodeCompletion2.d";

	/* --- */
	
	@BeforeClass
	public static void commonSetUp() throws Exception {
		setupWithFile(SampleMainProject.deeProj, TEST_SRCFILE);
		Assert.isTrue(editor.getScriptSourceViewer() != null);
		//assist = new DeeCodeContentAssistProcessor(null, editor);
		doc = DeeEditorTest.getDocument(editor);
	}

	
	/* ------------- Tests -------------  */
	
	@Test
	public void test1() throws Exception {
		testComputeProposals(274, 1, "unc", "oobarvar",
				"oovar", "oox",  "oo_t", "ooOfModule"
				);
	}
	
	@Test
	public void test2() throws Exception {
		testComputeProposals(335, 1, "oovar", "oox" 
				);
		
		testComputeProposalsWithRepLen2(334, 0, 1, "foovar", "foox", "baz" 
		);
	}

	@Test
	public void test3() throws Exception {
		testComputeProposals(389, 1, 
				"ooOfModule", "oo_t"
				);
		
		testComputeProposalsWithRepLen2(388, 0, 1, 
				"Foo", "fooOfModule", "foo_t", "ix", "FooBar",
				"pack", "nonexistantmodule"
				,"othervar", "Other" 
				);
	}
	
	@Test
	public void test4() throws Exception {
		testComputeProposalsWithRepLen2(575, 0, 0, 
				"foovar", "foox", "baz" 
				);
	}
	
}

