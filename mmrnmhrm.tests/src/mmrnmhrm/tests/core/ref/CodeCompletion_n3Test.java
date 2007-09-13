package mmrnmhrm.tests.core.ref;

import mmrnmhrm.tests.SampleMainProject;

import org.junit.BeforeClass;
import org.junit.Test;

public class CodeCompletion_n3Test extends CodeCompletion__Common {
	
	static final String TEST_SRCFOLDER = SampleMainProject.TEST_SRC3;
	static final String TEST_SRCFILE = TEST_SRCFOLDER + "/testCodeCompletion3.d";

	/* --- */
	
	@BeforeClass
	public static void commonSetUp() throws Exception {
		setupWithFile(SampleMainProject.deeProj, TEST_SRCFILE);
	}

	
	/* ------------- Tests -------------  */
	
	@Test
	public void test_ImpSelection() throws Exception {
		testComputeProposalsWithRepLen(56, 1, 10, 
				"ampleClass", "ampleClassB"
				);
		
		testComputeProposalsWithRepLen(88, 1, 11, 
				"ampleClass", "ampleClassB"
				);
	}
	
	@Test
	public void test_impModuleRef() throws Exception {
		testComputeProposalsWithRepLen(249, 1, 8,
				"ack.mod1", "ack.mod2", "ack.mod3", "ack.modSyntaxErrors", 
				"ack.sample", "ack.sample2", "ack.sample3", 
				"ack.testSelfImport3",
				"ack.subpack.mod3", "ack.subpack.mod4",
				"ack2.fooprivate", "ack2.foopublic", "ack2.foopublic2",
				"hobos" // this one comes from phobos
				);

		testComputeProposalsWithRepLen(253, 5, 4,
				"mod1", "mod2", "mod3", "modSyntaxErrors", 
				"sample", "sample2", "sample3", 
				"testSelfImport3",
				"subpack.mod3", "subpack.mod4"
		);
		
		testComputeProposalsWithRepLen(254, 6, 3,
				"od1", "od2", "od3", "odSyntaxErrors"
		);
	}

}

