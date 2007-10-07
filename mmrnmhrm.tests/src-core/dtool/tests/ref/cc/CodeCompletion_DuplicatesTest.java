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
		int offset = getMarkerStartOffset("/+@CC1+/"+1);
		ccTester.testComputeProposals(offset, 1, 
				
				"func(int a, List!(Foo) a)",
				"func(int bbb, List!(Foo) bbb)",
				"func(char a, List!(Foo) a)",
				"func(int a, List!(Bar) a)",
				"func()",
				
		
				"foo_t", "fooalias" 
				);
	}
	
	@Test
	public void test2() throws Exception {
		int offset = getMarkerStartOffset("/+@CC2+/"+1);
		ccTester.testComputeProposals(offset, 1, 
				"oolocalinner", "foolocal1", "fParam", "foobarvar",
				
				"func(int a, List!(Foo) a)",
				"func(int bbb, List!(Foo) bbb)",
				"func(char a, List!(Foo) a)",
				"func(int a, List!(Bar) a)",
				"func()",
				
				"foovar", "foox", 
				
				"foo_t", "fooalias" 
				);
	}



}
