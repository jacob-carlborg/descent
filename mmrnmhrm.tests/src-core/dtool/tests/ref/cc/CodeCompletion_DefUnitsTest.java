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
		ccTester.testComputeProposals(576, 0, 
				"IfTypeDefUnit",
				"parameter",
				"func", 
				"Class",
				
				"sampledefs",
				"Alias",
				"Enum",
				"Interface",
				"StructXXX",
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
		ccTester.testComputeProposals(622, 1, 
				"numMemberA", "numMemberB"
			);
	}
}

