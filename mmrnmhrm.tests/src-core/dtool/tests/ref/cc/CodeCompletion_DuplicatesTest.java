package dtool.tests.ref.cc;

import mmrnmhrm.tests.SampleMainProject;

import org.junit.BeforeClass;
import org.junit.Test;

public class CodeCompletion_DuplicatesTest extends CodeCompletion__Common {
	
	public static final String TEST_SRCFILE = 
		SampleMainProject.TEST_SRC3 + "/testCodeCompletion_dups.d";

	/* --- */
	
	@BeforeClass
	public static void commonSetUp() throws Exception {
		setupWithFile(SampleMainProject.deeProj, TEST_SRCFILE);
	}

	
	/* ------------- Tests -------------  */
	
	
	@Test
	public void test1() throws Exception {
		int offset = getMarkerStartOffset("/+@CC1+/");
		ccTester.testComputeProposals(offset, 1, 
				
				"unc(int a, List!(Foo) a)",
				"unc(int bbb, List!(Foo) bbb)",
				"unc(char a, List!(Foo) a)",
				"unc(int a, List!(Bar) a)",
				"unc()",
				
		
				"oo_t", "ooalias" 
				);
	}
	
	@Test
	public void test2() throws Exception {
		int offset = getMarkerStartOffset("/+@CC2+/");
		ccTester.testComputeProposals(offset, 1, 
				"oolocalinner", "oolocal1", "Param", "oobarvar",
				
				"unc(int a, List!(Foo) a)",
				"unc(int bbb, List!(Foo) bbb)",
				"unc(char a, List!(Foo) a)",
				"unc(int a, List!(Bar) a)",
				"unc()",
				
				"oovar", "oox", 
				
				"oo_t", "ooalias" 
				);
	}


}
