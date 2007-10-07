package dtool.tests.ref.cc;

import mmrnmhrm.tests.SampleMainProject;

import org.junit.BeforeClass;
import org.junit.Test;

public class CodeCompletion_LookupTest extends CodeCompletion__Common {
	
	public static final String TEST_SRCFILE = 
		SampleMainProject.TEST_SRC3 + "/testCodeCompletion.d";

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
		ccTester.testComputeProposals(getMarkerEndOffset("/+CC1@+/"), 0,
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
		ccTester.testComputeProposals(getMarkerEndOffset("/+CC2@+/")+1, 1,
				"Param", "unc", "oobarvar",
				"oovar", "oox", 
				/*"FooBar",*/  "oo_t", "ooalias", "ooOfModule", "rak" /*,"Foo",*/
				);
		
		// same test, but characters ahead of offset
		ccTester.testComputeProposalsWithRepLen(getMarkerEndOffset("/+CC3@+/")+1, 1, 2,
				"Param", "unc", "oobarvar",
				"oovar", "oox", 
				/*"FooBar",*/  "oo_t", "ooalias", "ooOfModule", "rak" /*,"Foo",*/
				);
	}

	@Test
	public void test3() throws Exception {
		ccTester.testComputeProposals(getMarkerEndOffset("/+CC4@+/")+3, 3, "barvar",
				"var", "x", "_t", "alias", "OfModule"
				);
	}
	
	@Test
	public void test6() throws Exception {
		// FIXUP because of syntax errors;
		srcModule.getBuffer().replace(getMarkerEndOffset("/+CC6@+/"), 1, ".");
		try {
			ccTester.testComputeProposals(getMarkerEndOffset("/+CC6@+/")+4, 0, 
					"foovar", "foox", "baz");
		} finally {
			srcModule.getBuffer().replace(541, 1, " ");
		}
	}
	@Test
	public void test6b() throws Exception {
		// Test in middle of the first name of the qualified ref
		int offset = getMarkerEndOffset("/+CC6b@+/");
		ccTester.testComputeProposalsWithRepLen(offset+1, 1, 2,
				"oo", "ooBar");
		// Test at end of qualified ref
		ccTester.testComputeProposals(offset+4, 1,
				"oovar", "oox");
	}
	
	@Test
	public void test7() throws Exception {
		srcModule.getBuffer().replace(getMarkerEndOffset("/+CC7@+/"), 1, ".");
		try {
			ccTester.testComputeProposals(getMarkerEndOffset("/+CC7@+/")+1, 0,
					"Foo", "fooOfModule", "frak",
					"fooalias", "foo_t", "ix", "FooBar", "Xpto",
					"pack", "nonexistantmodule",
					"othervar", "Other");
		} finally {
			srcModule.getBuffer().replace(615, 1, " ");
		}
	}


	@Test
	public void test7b() throws Exception {
		ccTester.testComputeProposals(getMarkerEndOffset("/+CC7b@+/")+2, 1, 
				"ooOfModule", "rak",
				"ooalias", "oo_t");
	}


	@Test
	public void test8() throws Exception {
		ccTester.testComputeProposals(getMarkerEndOffset("/+CC8@+/")+14, 1, 
				"oovar", "oox");
	}
}

