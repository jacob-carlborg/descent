package mmrnmhrm.tests.core.ref;

import mmrnmhrm.tests.SampleMainProject;

import org.junit.BeforeClass;
import org.junit.Test;

public class CodeCompletion_DefUnitsTest extends CodeCompletion__Common {
	
	static final String TEST_SRCFOLDER = SampleMainProject.TEST_SRC3;
	static final String TEST_SRCFILE = TEST_SRCFOLDER + "/sampledefs.d";

	/* --- */
	
	@BeforeClass
	public static void commonSetUp() throws Exception {
		setupWithFile(SampleMainProject.deeProj, TEST_SRCFILE);
	}

	
	/* ------------- Tests -------------  */
	
	@Test
	public void test1() throws Exception {
		testComputeProposals(576, 0, 
				"IfTypeDefUnit",
				"parameter",
				"func", 
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
		testComputeProposals(622, 1, 
				"numMemberA", "numMemberB"
			);
	}
}

