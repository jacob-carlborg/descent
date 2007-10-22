package dtool.tests.ref.cc;

import mmrnmhrm.tests.SampleMainProject;

import org.junit.BeforeClass;
import org.junit.Test;

public class CodeCompletion_DefUnitsTest extends CodeCompletion__Common {
	
	public static final String TEST_SRCFILE = 
		SampleMainProject.TEST_SRC3 + "/sampledefs.d";

	/* --- */
	
	@BeforeClass
	public static void commonSetUp() throws Exception {
		setupWithFile(SampleMainProject.deeProj, TEST_SRCFILE);
	}

	
	/* ------------- Tests -------------  */
	
	@Test
	public void test1() throws Exception {
		ccTester.testComputeProposals(getMarkerStartOffset("/+@CC1+/"), 0, 
				"IfTypeDefUnit",
				"parameter",
				"func(asdf.qwer parameter)", 
				"Class",
				
				"sampledefs",
				"Alias",
				"Enum",
				"Interface",
				"Struct",
				"Typedef",
				"Union",
				"variable",
				"ImportSelectiveAlias",
				"ImportAliasingDefUnit",
				"pack",

				"Template",
				"TypeParam",
				"ValueParam",
				"AliasParam",
				"TupleParam"
		);

	}

	@Test
	public void test2() throws Exception {
		ccTester.testComputeProposals(getMarkerStartOffset("/+@CC2+/"), 1, 
				"numMemberA", "numMemberB"
			);
	}
}

