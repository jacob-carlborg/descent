package mmrnmhrm.tests.core.ref;

import mmrnmhrm.tests.SampleMainProject;

import org.junit.BeforeClass;
import org.junit.Test;

public class CodeCompletion_LookupTest extends CodeCompletion__Common {
	
	static final String TEST_SRCFOLDER = SampleMainProject.TEST_SRC3;
	static final String TEST_SRCFILE = TEST_SRCFOLDER + "/testCodeCompletion.d";

	/* --- */
	
	@BeforeClass
	public static void commonSetUp() throws Exception {
		setupWithFile(SampleMainProject.deeProj, TEST_SRCFILE);
	}

	
	/* ------------- Tests -------------  */
	
	//@Test
	public void test0() throws Exception {
		//testComputeProposals(0, 0);
	}
	@Test
	public void test1() throws Exception {
		testComputeProposals(getTestOffset("/+@CC1+/"), 0,
				"fParam", "test", "func", "foobarvar",
				"foovar", "foox", "baz",
				"FooBar", "ix",  "foo_t", "fooalias", "fooOfModule", "frak", "Foo", "Xpto",
				"testCodeCompletion",
				"pack", "nonexistantmodule",
				"othervar", "Other"
				);
	}
	@Test
	public void test2() throws Exception {
		testComputeProposals(getTestOffset("/+@CC2+/")+1, 1,
				"Param", "unc", "oobarvar",
				"oovar", "oox", 
				/*"FooBar",*/  "oo_t", "ooalias", "ooOfModule", "rak" /*,"Foo",*/
				);
		
		// same test, but characters ahead of offset
		testComputeProposalsWithRepLen(getTestOffset("/+@CC3+/")+1, 1, 2,
				"Param", "unc", "oobarvar",
				"oovar", "oox", 
				/*"FooBar",*/  "oo_t", "ooalias", "ooOfModule", "rak" /*,"Foo",*/
				);
	}

	@Test
	public void test3() throws Exception {
		testComputeProposals(getTestOffset("/+@CC4+/")+3, 3, "barvar",
				"var", "x", "_t", "alias", "OfModule"
				);
	}
	
	@Test
	public void test6() throws Exception {
		// FIXUP because of syntax errors;
		getTestDocument().replace(getTestOffset("/+@CC6+/"), 1, ".");
		try {
			testComputeProposals(getTestOffset("/+@CC6+/")+4, 0, 
					"foovar", "foox", "baz");
		} finally {
			getTestDocument().replace(541, 1, " ");
		}
	}
	@Test
	public void test6b() throws Exception {
		// Test in middle of the first name of the qualified ref
		testComputeProposalsWithRepLen(getTestOffset("/+@CC6b+/")+1, 1, 2,
				"oo", "ooBar");
		// Test at end of qualified ref
		testComputeProposals(getTestOffset("/+@CC6b+/")+4, 1,
				"oovar", "oox");
	}
	
	@Test
	public void test7() throws Exception {
		getTestDocument().replace(getTestOffset("/+@CC7+/"), 1, ".");
		try {
			testComputeProposals(getTestOffset("/+@CC7+/")+1, 0,
					"Foo", "fooOfModule", "frak",
					"fooalias", "foo_t", "ix", "FooBar", "Xpto",
					"pack", "nonexistantmodule",
					"othervar", "Other");
		} finally {
			getTestDocument().replace(615, 1, " ");
		}
	}


	@Test
	public void test7b() throws Exception {
		testComputeProposals(getTestOffset("/+@CC7b+/")+2, 1, 
				"ooOfModule", "rak",
				"ooalias", "oo_t");
	}


	@Test
	public void test8() throws Exception {
		testComputeProposals(getTestOffset("/+@CC8+/")+14, 1, "oovar", "oox");
	}
}

