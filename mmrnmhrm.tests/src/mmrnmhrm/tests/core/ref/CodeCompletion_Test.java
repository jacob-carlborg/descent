package mmrnmhrm.tests.core.ref;

import melnorme.miscutil.Assert;
import mmrnmhrm.tests.SampleMainProject;
import mmrnmhrm.ui.editor.DeeEditorTest;

import org.junit.BeforeClass;
import org.junit.Test;

public class CodeCompletion_Test extends CodeCompletion__Common {
	
	static final String TEST_SRCFOLDER = SampleMainProject.TEST_SRC3;
	static final String TEST_SRCFILE = TEST_SRCFOLDER + "/testCodeCompletion.d";

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
	public void test0() throws Exception {
		//testComputeProposals(0, 0);
	}
	@Test
	public void test1() throws Exception {
		testComputeProposals(364, 0, "fParam", "test", "func", "foobarvar",
				"foovar", "foox", "baz",
				"FooBar", "ix",  "foo_t", "fooalias", "fooOfModule", "frak", "Foo", "Xpto",
				"testCodeCompletion",
				"pack", "nonexistantmodule",
				"othervar", "Other"
				);
	}
	@Test
	public void test2() throws Exception {
		testComputeProposals(386, 1, "Param", "unc", "oobarvar",
				"oovar", "oox", 
				/*"FooBar",*/  "oo_t", "ooalias", "ooOfModule", "rak" /*,"Foo",*/
				);
		
		// same test, but characters ahead of offset
		testComputeProposalsWithRepLen2(410, 1, 2,"Param", "unc", "oobarvar",
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
		testComputeProposalsWithRepLen2(575, 1, 2, "oo", "ooBar");
		// Test at end of qualified ref
		testComputeProposals(579, 1, "oovar", "oox");
	}
	
	@Test
	public void test7() throws Exception {
		DeeEditorTest.getDocument(editor).replace(615, 1, ".");
		try {
			testComputeProposals(616, 0, "Foo", "fooOfModule", "frak",
					"fooalias", "foo_t", "ix", "FooBar", "Xpto",
					"pack", "nonexistantmodule",
					"othervar", "Other");
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

