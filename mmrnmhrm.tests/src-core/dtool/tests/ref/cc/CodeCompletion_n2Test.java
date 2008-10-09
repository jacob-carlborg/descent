package dtool.tests.ref.cc;

import mmrnmhrm.tests.SampleMainProject;

import org.junit.BeforeClass;
import org.junit.Test;

public class CodeCompletion_n2Test extends CodeCompletion__Common {
	
	public static final String TEST_SRCFILE = 
		SampleMainProject.TEST_SRC3 + "/testCodeCompletion2.d";

	/* --- */
	
	@BeforeClass
	public static void commonSetUp() throws Exception {
		setupWithFile(SampleMainProject.deeProj, TEST_SRCFILE);
	}

	
	/* ------------- Tests -------------  */
	
	@Test
	public void test1() throws Exception {
		ccTester.testComputeProposals(getMarkerStartOffset("/+@CC1+/"), 1, false,
				"unc()", "oobarvar",
				"oovar", "oox",  "oo_t", "ooOfModule"
				);
	}
	
	@Test
	public void test2() throws Exception {
		int offset = getMarkerStartOffset("/+@CC2+/");
		ccTester.testComputeProposals(offset, 1, false,
				"oovar", "oox" 
				);
		
		ccTester.testComputeProposalsWithRepLen(offset-1, 0, 1, false,
				"foovar", "foox", "baz" 
		);
	}

	@Test
	public void test3() throws Exception {
		int offset = getMarkerStartOffset("/+@CC3+/");
		ccTester.testComputeProposals(offset, 1, false,
				"ooOfModule", "oo_t"
				);
		
		ccTester.testComputeProposalsWithRepLen(offset-1, 0, 1, false,
				"Foo", "fooOfModule", "foo_t", "ix", "FooBar",
				"pack", "nonexistantmodule"
				,"othervar", "Other" 
				);
	}
	
	@Test
	public void test4() throws Exception {
		ccTester.testComputeProposals(getMarkerStartOffset("/+@CC4+/"), 0, false,
				"foovar", "foox", "baz" 
				);
	}
	
}

