package dtool.tests.ref.cc;

import mmrnmhrm.tests.SampleMainProject;

import org.junit.BeforeClass;
import org.junit.Test;

public class CodeCompletion_n3Test extends CodeCompletion__Common {
	
	public static final String TEST_SRCFILE = 
		SampleMainProject.TEST_SRC3 + "/testCodeCompletion3.d";

	/* --- */
	
	@BeforeClass
	public static void commonSetUp() throws Exception {
		setupWithFile(SampleMainProject.deeProj, TEST_SRCFILE);
	}

	
	/* ------------- Tests -------------  */
	
	@Test
	public void test_ImpSelection() throws Exception {
		ccTester.testComputeProposalsWithRepLen(
				getMarkerEndOffset("/+CC1@+/")+1, 1, 10, false,
				"ampleClass", "ampleClassB"
				);
		
		ccTester.testComputeProposalsWithRepLen(
				getMarkerEndOffset("/+CC2@+/")+1, 1, 11, false,
				"ampleClass", "ampleClassB"
				);
	}
	
	@Test
	public void test_impModuleRef() throws Exception {
		int offset = getMarkerEndOffset("/+CC3@+/");
		ccTester.testComputeProposalsWithRepLen(offset+1, 1, 8, false,
				"ack.mod1", "ack.mod2", "ack.mod3", "ack.modSyntaxErrors", 
				"ack.sample", "ack.sample2", "ack.sample3", 
				"ack.testSelfImport3",
				"ack.subpack.mod3", "ack.subpack.mod4",
				"ack2.fooprivate", "ack2.foopublic", "ack2.foopublic2",
				"hobos" // this one comes from phobos
				);

		ccTester.testComputeProposalsWithRepLen(offset+5, 5, 4, false,
				"mod1", "mod2", "mod3", "modSyntaxErrors", 
				"sample", "sample2", "sample3", 
				"testSelfImport3",
				"subpack.mod3", "subpack.mod4"
		);
		
		ccTester.testComputeProposalsWithRepLen(offset+6, 6, 3, false,
				"od1", "od2", "od3", "odSyntaxErrors"
		);
	}

}

